package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

object SoundboardAudioEngine {
    private const val SAMPLE_RATE = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var masterVolume: Float = 0.85f

    fun playSound(soundKey: String, customFreq: Float = 440f, customDurationMs: Int = 1200) {
        scope.launch {
            try {
                val samples = when (soundKey) {
                    "AIRHORN" -> generateAirhorn()
                    "LAUGHTER" -> generateLaughter()
                    "APPLAUSE" -> generateApplause()
                    "VICTORY" -> generateVictoryFanfare()
                    "SAD_TROMBONE" -> generateSadTrombone()
                    "HEADSHOT" -> generateHeadshot()
                    "BRUH" -> generateBruh()
                    "CRICKET" -> generateCrickets()
                    "BASS_DROP" -> generateBassDrop()
                    "GAME_OVER" -> generateGameOver()
                    "WHISTLE" -> generateWhistle()
                    "GIGACHAD" -> generatePhonkBrass()
                    "POP" -> generateReactionPop()
                    "SYNTH_AIRHORN" -> generateAirhorn()
                    "SYNTH_LAUGH" -> generateLaughter()
                    "SYNTH_LASER" -> generateHeadshot()
                    "SYNTH_CHIME" -> generateVictoryFanfare()
                    "SYNTH_BASS" -> generateBassDrop()
                    else -> generateCustomSynth(customFreq, customDurationMs)
                }

                playPcmBuffer(samples)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun playPcmBuffer(samples: ShortArray) {
        if (samples.isEmpty()) return
        val bufferSize = samples.size * 2
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(samples, 0, samples.size)
        audioTrack.setVolume(masterVolume.coerceIn(0.0f, 1.0f))
        audioTrack.play()

        // Release track after playing
        val playDurationMs = (samples.size * 1000L) / SAMPLE_RATE + 200L
        scope.launch {
            kotlinx.coroutines.delay(playDurationMs)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // Ignore cleanup errors
            }
        }
    }

    // --- Sound Synthesis Algorithms ---

    // 1. Airhorn Fanfare (3 rapid stabs + 1 long sustained blast)
    private fun generateAirhorn(): ShortArray {
        val totalDurationMs = 1100
        val totalSamples = (SAMPLE_RATE * totalDurationMs) / 1000
        val buffer = ShortArray(totalSamples)

        val hornNotes = doubleArrayOf(466.16, 587.33, 698.46) // Bb4, D5, F5
        val bursts = listOf(
            0 to 120,      // Blast 1
            160 to 280,    // Blast 2
            320 to 440,    // Blast 3
            480 to 1050    // Big Hold
        )

        for ((startMs, endMs) in bursts) {
            val startSample = (SAMPLE_RATE * startMs) / 1000
            val endSample = (SAMPLE_RATE * endMs) / 1000
            val durationSamples = endSample - startSample

            for (i in 0 until durationSamples) {
                val index = startSample + i
                if (index >= totalSamples) break
                val t = i.toDouble() / SAMPLE_RATE

                // Slight brass overtones and pulse wave
                var wave = 0.0
                for (note in hornNotes) {
                    wave += sin(2.0 * PI * note * t) * 0.4
                    wave += sin(2.0 * PI * (note * 2.01) * t) * 0.2
                    wave += sin(2.0 * PI * (note * 3.0) * t) * 0.1
                }

                // Envelope
                val progress = i.toDouble() / durationSamples
                val env = when {
                    progress < 0.08 -> progress / 0.08
                    progress > 0.85 -> (1.0 - progress) / 0.15
                    else -> 1.0
                }

                // Hard clipper for that buzzy MLG airhorn grit
                var rawVal = wave * env * 0.6
                if (rawVal > 0.85) rawVal = 0.85
                if (rawVal < -0.85) rawVal = -0.85

                buffer[index] = (rawVal * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    // 2. Crowd Laughter Track (rhythmic harmonic giggles)
    private fun generateLaughter(): ShortArray {
        val durationMs = 1800
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        // Generate 7 laughter chuckle pulses: "Ha-Ha-Ha-Ha-Ha-He-He"
        val chuckleStarts = listOf(50, 240, 440, 660, 900, 1150, 1420)
        val chuckles = chuckleStarts.mapIndexed { idx, start ->
            val pitch = 380.0 + (idx % 3) * 45.0 - (idx * 15.0)
            Triple(start, 160, pitch)
        }

        for ((startMs, durMs, pitch) in chuckles) {
            val startSample = (SAMPLE_RATE * startMs) / 1000
            val durSamples = (SAMPLE_RATE * durMs) / 1000

            for (i in 0 until durSamples) {
                val idx = startSample + i
                if (idx >= totalSamples) break
                val t = i.toDouble() / SAMPLE_RATE

                // Vocal vowel approximation with formant harmonics
                val f0 = pitch * (1.0 - (i.toDouble() / durSamples) * 0.15)
                var vocal = sin(2.0 * PI * f0 * t) * 0.5 +
                        sin(2.0 * PI * (f0 * 2.0) * t) * 0.35 +
                        sin(2.0 * PI * (f0 * 3.2) * t) * 0.2

                val env = sin(PI * (i.toDouble() / durSamples))
                val sampleVal = vocal * env * 0.7
                val existing = buffer[idx].toDouble() / Short.MAX_VALUE
                val combined = (sampleVal + existing).coerceIn(-1.0, 1.0)
                buffer[idx] = (combined * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    // 3. Stadium Applause (stochastic noise claps + cheering surge)
    private fun generateApplause(): ShortArray {
        val durationMs = 2000
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        // Multiple discrete claps overlaid
        val clapTimes = (0..50).map { (Math.random() * 1900).toInt() }
        for (clapMs in clapTimes) {
            val startSample = (SAMPLE_RATE * clapMs) / 1000
            val clapLength = (SAMPLE_RATE * 35) / 1000 // 35ms sharp clap

            for (i in 0 until clapLength) {
                val idx = startSample + i
                if (idx >= totalSamples) break
                val noise = (Math.random() * 2.0 - 1.0)
                val decay = exp(-i.toDouble() / (clapLength * 0.25))
                val clapSample = noise * decay * 0.35

                val existing = buffer[idx].toDouble() / Short.MAX_VALUE
                buffer[idx] = ((existing + clapSample).coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
            }
        }

        // Add roaring crowd cheering swell
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val cheerEnv = sin(PI * progress) * 0.25
            val cheerNoise = (Math.random() * 2.0 - 1.0) * cheerEnv
            val existing = buffer[i].toDouble() / Short.MAX_VALUE
            buffer[i] = ((existing + cheerNoise).coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // 4. Victory / Level Up Chiptune Fanfare
    private fun generateVictoryFanfare(): ShortArray {
        val notes = doubleArrayOf(
            523.25, // C5
            659.25, // E5
            783.99, // G5
            1046.50 // C6
        )
        val noteDurMs = 120
        val totalSamples = (SAMPLE_RATE * (notes.size * noteDurMs + 400)) / 1000
        val buffer = ShortArray(totalSamples)

        var curSample = 0
        for (i in notes.indices) {
            val dur = if (i == notes.size - 1) noteDurMs + 400 else noteDurMs
            val samplesForNote = (SAMPLE_RATE * dur) / 1000
            val freq = notes[i]

            for (s in 0 until samplesForNote) {
                if (curSample >= totalSamples) break
                val t = s.toDouble() / SAMPLE_RATE

                // 8-bit square wave with duty cycle 0.5
                val phase = (t * freq) % 1.0
                val square = if (phase < 0.5) 0.5 else -0.5

                val env = 1.0 - (s.toDouble() / samplesForNote) * 0.4
                buffer[curSample++] = (square * env * 0.7 * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    // 5. Sad Trombone (Wah-Wah-Wah-Waaaaah)
    private fun generateSadTrombone(): ShortArray {
        val notes = floatArrayOf(277.18f, 261.63f, 246.94f, 233.08f) // C#4, C4, B3, Bb3
        val durations = intArrayOf(260, 260, 260, 700)
        val totalMs = durations.sum()
        val totalSamples = (SAMPLE_RATE * totalMs) / 1000
        val buffer = ShortArray(totalSamples)

        var curSample = 0
        for (n in notes.indices) {
            val noteFreq = notes[n]
            val dur = durations[n]
            val samplesForNote = (SAMPLE_RATE * dur) / 1000

            for (s in 0 until samplesForNote) {
                if (curSample >= totalSamples) break
                val t = s.toDouble() / SAMPLE_RATE

                // Trombone brass with wah modulation
                val wah = 1.0 + 0.3 * sin(2.0 * PI * 6.0 * t) // 6Hz tremolo wah
                val pitchSlide = if (n == 3) noteFreq.toDouble() * (1.0 - (s.toDouble() / samplesForNote) * 0.1) else noteFreq.toDouble()

                var brass = 0.0
                for (h in 1..4) {
                    val hDouble = h.toDouble()
                    brass += (sin(2.0 * PI * (pitchSlide * hDouble) * t) / hDouble)
                }

                val env = sin(PI * (s.toDouble() / samplesForNote))
                buffer[curSample++] = (brass * wah * env * 0.45 * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    // 6. Headshot / Laser Pew
    private fun generateHeadshot(): ShortArray {
        val durationMs = 380
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            // Rapid pitch drop from 2200Hz down to 180Hz
            val freq = 2200.0 * exp(-progress * 6.0) + 120.0
            val wave = sin(2.0 * PI * freq * t)
            val sub = sin(2.0 * PI * 80.0 * t) * exp(-progress * 8.0) * 0.5
            val env = exp(-progress * 5.0)

            val mixed = (wave * 0.7 + sub) * env
            buffer[i] = (mixed.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // 7. Bruh Vocoder Grunt
    private fun generateBruh(): ShortArray {
        val durationMs = 450
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples

            // Deep vocal formant glide 130Hz -> 90Hz
            val f0 = 130.0 - (progress * 40.0)
            val vocal = sin(2.0 * PI * f0 * t) * 0.6 +
                    sin(2.0 * PI * (f0 * 2.2) * t) * 0.35 +
                    sin(2.0 * PI * (f0 * 3.4) * t) * 0.2

            val env = sin(PI * progress)
            buffer[i] = (vocal * env * 0.75 * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // 8. Awkward Crickets
    private fun generateCrickets(): ShortArray {
        val durationMs = 1200
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        val chirpTimes = listOf(100, 200, 600, 700)
        for (cTime in chirpTimes) {
            val startSample = (SAMPLE_RATE * cTime) / 1000
            val chirpLen = (SAMPLE_RATE * 50) / 1000

            for (i in 0 until chirpLen) {
                val idx = startSample + i
                if (idx >= totalSamples) break
                val t = i.toDouble() / SAMPLE_RATE
                val chirp = sin(2.0 * PI * 4400.0 * t) * sin(2.0 * PI * 60.0 * t)
                val env = sin(PI * (i.toDouble() / chirpLen))
                buffer[idx] = (chirp * env * 0.45 * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    // 9. 808 Bass Drop
    private fun generateBassDrop(): ShortArray {
        val durationMs = 1300
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            // Smooth pitch drop 160Hz -> 38Hz
            val freq = 160.0 * exp(-progress * 3.5) + 38.0
            var sine = sin(2.0 * PI * freq * t)
            // Soft saturation
            sine = (1.5 * sine) / (1.0 + Math.abs(sine))
            val env = exp(-progress * 2.0)
            buffer[i] = (sine * env * 0.8 * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // 10. Game Over Descending Crunch
    private fun generateGameOver(): ShortArray {
        val notes = doubleArrayOf(440.0, 415.0, 392.0, 311.0)
        val totalSamples = (SAMPLE_RATE * 1100) / 1000
        val buffer = ShortArray(totalSamples)

        var curSample = 0
        for (note in notes) {
            val noteSamples = (SAMPLE_RATE * 240) / 1000
            for (s in 0 until noteSamples) {
                if (curSample >= totalSamples) break
                val t = s.toDouble() / SAMPLE_RATE
                val tri = 2.0 * Math.abs(2.0 * ((t * note) % 1.0) - 1.0) - 1.0
                val env = 1.0 - (s.toDouble() / noteSamples) * 0.6
                buffer[curSample++] = (tri * env * 0.65 * Short.MAX_VALUE).toInt().toShort()
            }
        }
        return buffer
    }

    // 11. Alert Whistle
    private fun generateWhistle(): ShortArray {
        val durationMs = 500
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            val freq = 1100.0 + sin(progress * PI) * 1200.0
            val whistle = sin(2.0 * PI * freq * t) + 0.15 * sin(2.0 * PI * (freq * 2.0) * t)
            val env = sin(PI * progress)
            buffer[i] = (whistle * env * 0.6 * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // 12. Phonk GigaChad Brass Stab
    private fun generatePhonkBrass(): ShortArray {
        val durationMs = 600
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)

        val root = 130.81 // C3
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples

            // Sawtooth harmonics stack
            var saw = 0.0
            for (h in 1..6) {
                val hDouble = h.toDouble()
                saw += sin(2.0 * PI * (root * hDouble) * t) / hDouble
            }
            val filterMod = exp(-progress * 4.0)
            saw *= (0.5 + 0.5 * filterMod)

            val env = exp(-progress * 3.0)
            buffer[i] = (saw * env * 0.7 * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // Reaction Pop sound for emoji bursts
    private fun generateReactionPop(): ShortArray {
        val totalSamples = (SAMPLE_RATE * 120) / 1000
        val buffer = ShortArray(totalSamples)
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            val freq = 400.0 + progress * 600.0
            val pop = sin(2.0 * PI * freq * t) * exp(-progress * 15.0)
            buffer[i] = (pop * 0.75 * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }

    // Custom user configurable synthesizer
    private fun generateCustomSynth(freq: Float, durationMs: Int): ShortArray {
        val totalSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(totalSamples)
        val f = freq.toDouble()
        for (i in 0 until totalSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val progress = i.toDouble() / totalSamples
            val wave = sin(2.0 * PI * f * t) + 0.3 * sin(2.0 * PI * (f * 2.0) * t)
            val env = exp(-progress * 3.0)
            buffer[i] = (wave * env * 0.65 * Short.MAX_VALUE).toInt().toShort()
        }
        return buffer
    }
}
