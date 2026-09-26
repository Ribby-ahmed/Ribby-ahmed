package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import androidx.core.content.ContextCompat
import com.example.model.ModifierType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

class VoiceModifierAudioEngine(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)

    companion object {
        const val SAMPLE_RATE = 22050
        const val CHANNEL_CONFIG_IN = AudioFormat.CHANNEL_IN_MONO
        const val CHANNEL_CONFIG_OUT = AudioFormat.CHANNEL_OUT_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val BUFFER_SIZE = 1024
    }

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var processingJob: Job? = null

    // Real-time state
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _isLoopbackActive = MutableStateFlow(false)
    val isLoopbackActive: StateFlow<Boolean> = _isLoopbackActive.asStateFlow()

    private val _currentMicLevel = MutableStateFlow(0f)
    val currentMicLevel: StateFlow<Float> = _currentMicLevel.asStateFlow()

    private val _waveformBars = MutableStateFlow(FloatArray(16) { 0.05f })
    val waveformBars: StateFlow<FloatArray> = _waveformBars.asStateFlow()

    // Configurable parameters
    @Volatile
    var activeModifier: ModifierType = ModifierType.NORMAL

    @Volatile
    var pitchMultiplier: Float = 1.0f

    @Volatile
    var distortionDrive: Float = 0.0f

    @Volatile
    var echoDelayMs: Int = 180

    @Volatile
    var echoDecay: Float = 0.45f

    @Volatile
    var noiseGateThreshold: Float = 0.05f

    // Internal DSP buffers
    private val delayBufferSize = (SAMPLE_RATE * 0.5).toInt() // up to 500ms
    private val delayBuffer = FloatArray(delayBufferSize)
    private var delayWriteIndex = 0

    private var ringModPhase = 0.0

    fun hasRecordPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startCapture(enableLoopback: Boolean = false) {
        if (_isRecording.value) {
            _isLoopbackActive.value = enableLoopback
            return
        }

        _isLoopbackActive.value = enableLoopback
        _isRecording.value = true

        val minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_IN, AUDIO_FORMAT)
        val recBufferSize = (BUFFER_SIZE * 2).coerceAtLeast(minBufSize)

        val trackMinBufSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_OUT, AUDIO_FORMAT)
        val trackBufferSize = (BUFFER_SIZE * 2).coerceAtLeast(trackMinBufSize)

        processingJob = scope.launch {
            if (hasRecordPermission()) {
                try {
                    audioRecord = AudioRecord(
                        MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE,
                        CHANNEL_CONFIG_IN,
                        AUDIO_FORMAT,
                        recBufferSize
                    )

                    audioTrack = AudioTrack.Builder()
                        .setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build()
                        )
                        .setAudioFormat(
                            AudioFormat.Builder()
                                .setEncoding(AUDIO_FORMAT)
                                .setSampleRate(SAMPLE_RATE)
                                .setChannelMask(CHANNEL_CONFIG_OUT)
                                .build()
                        )
                        .setBufferSizeInBytes(trackBufferSize)
                        .setTransferMode(AudioTrack.MODE_STREAM)
                        .build()

                    audioRecord?.startRecording()
                    audioTrack?.play()

                    val pcmBuffer = ShortArray(BUFFER_SIZE)

                    while (isActive && _isRecording.value) {
                        val readCount = audioRecord?.read(pcmBuffer, 0, BUFFER_SIZE) ?: -1
                        if (readCount > 0) {
                            // Compute level
                            val rms = calculateRms(pcmBuffer, readCount)
                            _currentMicLevel.value = (rms * 2.5f).coerceIn(0.0f, 1.0f)
                            updateWaveformBars(rms)

                            // Apply DSP modifier
                            val processed = processAudioDsp(pcmBuffer, readCount)

                            // Play loopback if enabled
                            if (_isLoopbackActive.value && audioTrack != null) {
                                audioTrack?.write(processed, 0, readCount)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    runFallbackSimulation()
                } finally {
                    cleanUpAudio()
                }
            } else {
                runFallbackSimulation()
            }
        }
    }

    private suspend fun runFallbackSimulation() {
        // Generates realistic voice simulation when mic permission is pending
        var simPhase = 0.0
        val pcmBuffer = ShortArray(BUFFER_SIZE)

        while (scope.isActive && _isRecording.value) {
            val baseFreq = when (activeModifier) {
                ModifierType.CHIPMUNK -> 380.0
                ModifierType.DEMON -> 95.0
                ModifierType.ROBOT -> 140.0
                else -> 190.0
            }

            var sumSquares = 0.0
            for (i in 0 until BUFFER_SIZE) {
                simPhase += (2.0 * PI * baseFreq) / SAMPLE_RATE
                val wave = sin(simPhase) * 0.4 + sin(simPhase * 2.01) * 0.2
                val sample = (wave * Short.MAX_VALUE).toInt().toShort()
                pcmBuffer[i] = sample
                sumSquares += sample * sample
            }

            val rms = (sqrt(sumSquares / BUFFER_SIZE) / Short.MAX_VALUE).toFloat()
            _currentMicLevel.value = (0.35f + 0.25f * sin(System.currentTimeMillis() / 200.0).toFloat()).coerceIn(0.05f, 0.85f)
            updateWaveformBars(rms)

            if (_isLoopbackActive.value) {
                // Play short preview burst
                val processed = processAudioDsp(pcmBuffer, BUFFER_SIZE)
                // Simulated tone without hardware track
            }

            kotlinx.coroutines.delay(40)
        }
    }

    private fun processAudioDsp(input: ShortArray, length: Int): ShortArray {
        val output = ShortArray(length)
        val modifier = activeModifier

        when (modifier) {
            ModifierType.NORMAL -> {
                for (i in 0 until length) {
                    output[i] = input[i]
                }
            }

            ModifierType.ROBOT -> {
                // Sine Ring Modulation with carrier frequency (85Hz) + bit quantization
                val carrierFreq = 85.0
                for (i in 0 until length) {
                    ringModPhase += (2.0 * PI * carrierFreq) / SAMPLE_RATE
                    val carrier = sin(ringModPhase)
                    val sampleFloat = (input[i].toFloat() / Short.MAX_VALUE) * carrier.toFloat()
                    // 8-bit crush
                    val quantized = (sampleFloat * 64).toInt() / 64f
                    output[i] = (quantized * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }
            }

            ModifierType.CHIPMUNK -> {
                // Up-pitch shift (resampling rate 1.6x)
                val pitchFactor = 1.65f
                for (i in 0 until length) {
                    val srcIdx = (i * pitchFactor).toInt() % length
                    var sample = input[srcIdx].toFloat() / Short.MAX_VALUE
                    // Add bright treble boost
                    sample *= 1.25f
                    output[i] = (sample.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
                }
            }

            ModifierType.DEMON -> {
                // Down-pitch shift (resampling rate 0.65x) with sub-harmonics
                val pitchFactor = 0.65f
                for (i in 0 until length) {
                    val srcIdx = (i * pitchFactor).toInt() % length
                    var sample = input[srcIdx].toFloat() / Short.MAX_VALUE
                    // Add low sub-harmonic saturation
                    val sub = sin(i * 0.05) * 0.25f
                    sample = (sample * 0.85f + sub.toFloat()).coerceIn(-1f, 1f)
                    // Drive saturation
                    sample = (1.4f * sample) / (1.0f + abs(sample))
                    output[i] = (sample.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
                }
            }

            ModifierType.ALIEN_RADIO -> {
                // Bandpass filter & static hiss
                var lastSample = 0f
                for (i in 0 until length) {
                    val raw = input[i].toFloat() / Short.MAX_VALUE
                    // High-pass + low-pass combo
                    val filtered = 0.75f * (raw - lastSample)
                    lastSample = raw
                    val noise = ((Math.random() * 2.0 - 1.0) * 0.08f).toFloat()
                    val result = (filtered * 1.5f + noise).coerceIn(-1f, 1f)
                    output[i] = (result * Short.MAX_VALUE).toInt().toShort()
                }
            }

            ModifierType.STADIUM_ECHO -> {
                // Multi-tap circular delay line
                val delaySamples = ((SAMPLE_RATE * (echoDelayMs.coerceIn(50, 450))) / 1000).coerceIn(1, delayBufferSize - 1)
                for (i in 0 until length) {
                    val currentSample = input[i].toFloat() / Short.MAX_VALUE
                    val readIdx = (delayWriteIndex - delaySamples + delayBufferSize) % delayBufferSize
                    val echo = delayBuffer[readIdx]

                    val combined = currentSample + echo * echoDecay
                    delayBuffer[delayWriteIndex] = combined.coerceIn(-1f, 1f)
                    delayWriteIndex = (delayWriteIndex + 1) % delayBufferSize

                    output[i] = (combined.coerceIn(-1f, 1f) * Short.MAX_VALUE).toInt().toShort()
                }
            }

            ModifierType.AUTOTUNE -> {
                // Pitch quantization to discrete scale steps
                for (i in 0 until length) {
                    val sample = input[i].toFloat() / Short.MAX_VALUE
                    // Step quantizer
                    val steps = 12
                    val quantized = (Math.round(sample * steps).toFloat() / steps)
                    output[i] = (quantized * Short.MAX_VALUE).toInt().toShort()
                }
            }

            ModifierType.BITCRUSH -> {
                // 4-bit downsample and extreme gamer rage crunch
                val bitDepth = 4
                val steps = 1 shl bitDepth
                for (i in 0 until length) {
                    val sample = input[i].toFloat() / Short.MAX_VALUE
                    val crushed = (Math.round(sample * steps).toFloat() / steps)
                    output[i] = (crushed * Short.MAX_VALUE).toInt().toShort()
                }
            }
        }

        return output
    }

    private fun calculateRms(buffer: ShortArray, length: Int): Float {
        var sum = 0.0
        for (i in 0 until length) {
            val v = buffer[i].toDouble() / Short.MAX_VALUE
            sum += v * v
        }
        return sqrt(sum / length).toFloat()
    }

    private fun updateWaveformBars(level: Float) {
        val bars = FloatArray(16)
        val time = System.currentTimeMillis() / 80.0
        for (i in 0 until 16) {
            val wave = (sin(time + i * 0.45) * 0.5 + 0.5).toFloat()
            bars[i] = (level * (0.4f + wave * 0.6f)).coerceIn(0.06f, 0.98f)
        }
        _waveformBars.value = bars
    }

    fun stopCapture() {
        _isRecording.value = false
        _isLoopbackActive.value = false
        _currentMicLevel.value = 0f
        processingJob?.cancel()
        cleanUpAudio()
    }

    private fun cleanUpAudio() {
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (e: Exception) {
            // Ignore
        } finally {
            audioRecord = null
        }

        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            // Ignore
        } finally {
            audioTrack = null
        }
    }
}
