package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NetworkStats
import com.example.model.ServerRegion
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.GamerEmerald
import com.example.ui.theme.PingGreen
import com.example.ui.theme.PingRed
import com.example.ui.theme.PingYellow
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun NetworkSettingsScreen(
    networkStats: NetworkStats,
    availableRegions: List<ServerRegion>,
    isPttModeEnabled: Boolean,
    onToggleLowLatency: (Boolean) -> Unit,
    onSelectRegion: (ServerRegion) -> Unit,
    onSelectBitrate: (Int) -> Unit,
    onTogglePttMode: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val pingColor = when {
        networkStats.pingMs < 35 -> PingGreen
        networkStats.pingMs < 75 -> PingYellow
        else -> PingRed
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("network_settings_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "LOW-LATENCY NETWORK",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Ultra-fast voice packet routing & competitive audio telemetry",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Live Gaming Telemetry Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("telemetry_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = pingColor, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "REAL-TIME TELEMETRY",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(pingColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, pingColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (networkStats.pingMs < 35) "EXCELLENT" else "STABLE",
                                color = pingColor,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Stat Metric Tiles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricTile(
                            label = "PING (RTT)",
                            value = "${networkStats.pingMs}ms",
                            color = pingColor,
                            modifier = Modifier.weight(1f)
                        )
                        MetricTile(
                            label = "JITTER",
                            value = "${networkStats.jitterMs}ms",
                            color = CyberCyan,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricTile(
                            label = "PACKET LOSS",
                            value = "${networkStats.packetLossPercent}%",
                            color = if (networkStats.packetLossPercent == 0f) GamerEmerald else PingYellow,
                            modifier = Modifier.weight(1f)
                        )
                        MetricTile(
                            label = "BUFFER LATENCY",
                            value = "${networkStats.bufferLatencyMs}ms",
                            color = GamerEmerald,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Low-Latency Mode Switch
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = GamerEmerald, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ultra-Low Latency Audio Pipeline",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Prioritizes 14ms buffer & high UDP priority",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = networkStats.lowLatencyModeEnabled,
                        onCheckedChange = onToggleLowLatency,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GamerEmerald,
                            checkedTrackColor = GamerEmerald.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceVariantDark
                        ),
                        modifier = Modifier.testTag("toggle_low_latency_switch")
                    )
                }
            }
        }

        // Input Mode: PTT vs Voice Activity
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isPttModeEnabled) "Input: Push-to-Talk (PTT)" else "Input: Voice Activity (VAD)",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (isPttModeEnabled) "Hold PTT bar to transmit comms" else "Mic opens automatically on voice detection",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = isPttModeEnabled,
                        onCheckedChange = onTogglePttMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberCyan,
                            checkedTrackColor = CyberCyan.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = SurfaceVariantDark
                        ),
                        modifier = Modifier.testTag("toggle_ptt_mode_switch")
                    )
                }
            }
        }

        // Server Regions Section
        item {
            Text(
                text = "SERVER REGION ROUTING",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                availableRegions.forEach { region ->
                    val isSelected = region.id == networkStats.selectedRegion.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) CyberCyan else SurfaceBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectRegion(region) }
                            .testTag("region_card_${region.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) CyberCyan.copy(alpha = 0.1f) else SurfaceDark
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = region.flag, fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = region.name,
                                        color = if (isSelected) CyberCyan else TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = region.location,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "~${region.basePingMs}ms",
                                    color = if (region.basePingMs < 35) PingGreen else PingYellow,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Codec Bitrates
        item {
            Text(
                text = "OPUS CODEC BITRATE",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val bitrates = listOf(
                    16 to "16 kbps\nUltra-Low Latency",
                    32 to "32 kbps\nBalanced Gaming",
                    64 to "64 kbps\nHQ Studio Voice"
                )

                bitrates.forEach { (rate, desc) ->
                    val isSelected = networkStats.currentBitrateKbps == rate
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (isSelected) CyberCyan.copy(alpha = 0.15f) else SurfaceDark,
                                RoundedCornerShape(12.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) CyberCyan else SurfaceBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectBitrate(rate) }
                            .padding(vertical = 10.dp, horizontal = 6.dp)
                            .testTag("bitrate_option_$rate"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = desc,
                            color = if (isSelected) CyberCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun MetricTile(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceVariantDark, RoundedCornerShape(12.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}
