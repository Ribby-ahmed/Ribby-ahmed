package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
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
import com.example.model.SquadChannel
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.PingGreen
import com.example.ui.theme.PingRed
import com.example.ui.theme.PingYellow
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChannelBanner(
    channel: SquadChannel,
    networkStats: NetworkStats,
    onSwitchChannelClick: () -> Unit,
    onNetworkClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .border(width = 1.dp, color = SurfaceBorder, shape = RoundedCornerShape(16.dp))
            .clickable(onClick = onSwitchChannelClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .testTag("channel_banner"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = channel.iconEmoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${channel.name}",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = "Switch Channel",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${channel.gameCategory} · ${channel.bitrateKbps}kbps OPUS",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Low-latency network pill (clickable to view telemetry)
        PingBadge(
            networkStats = networkStats,
            onClick = onNetworkClick
        )
    }
}

@Composable
fun PingBadge(
    networkStats: NetworkStats,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val ping = networkStats.pingMs
    val pingColor = when {
        ping < 35 -> PingGreen
        ping < 75 -> PingYellow
        else -> PingRed
    }

    Box(
        modifier = modifier
            .background(SurfaceBorder.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .border(1.dp, pingColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("ping_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(pingColor, CircleShape)
            )
            Text(
                text = "${ping}ms",
                color = pingColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = networkStats.selectedRegion.flag,
                fontSize = 11.sp
            )
        }
    }
}
