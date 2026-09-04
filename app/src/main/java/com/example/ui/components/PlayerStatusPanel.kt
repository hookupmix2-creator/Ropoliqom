package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Player
import com.example.model.PlayerType
import com.example.model.Tile
import com.example.ui.theme.*

@Composable
fun PlayerStatusPanel(
    players: List<Player>,
    currentTurnPlayerId: Int,
    allTiles: List<Tile>,
    onPlayerClick: (Player) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(players) { player ->
            val isCurrentTurn = player.id == currentTurnPlayerId
            PlayerCard(
                player = player,
                isCurrentTurn = isCurrentTurn,
                netWorth = player.calculateNetWorth(allTiles),
                onClick = { onPlayerClick(player) }
            )
        }
    }
}

@Composable
private fun PlayerCard(
    player: Player,
    isCurrentTurn: Boolean,
    netWorth: Int,
    onClick: () -> Unit
) {
    val cardBackground = if (isCurrentTurn && !player.isBankrupt) {
        Brush.linearGradient(
            colors = listOf(ActivePlayerPurpleStart, ActivePlayerIndigoEnd)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0x14FFFFFF), Color(0x0AFFFFFF))
        )
    }

    val borderColor = when {
        player.isBankrupt -> CrimsonLoss.copy(alpha = 0.4f)
        isCurrentTurn -> ActivePlayerBorder
        else -> ElegantCardBorder
    }

    Box(
        modifier = Modifier
            .width(155.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(cardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // سطر عنوان: نام و نشانگر فعال
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(player.colorHex))
                            .border(1.dp, if (isCurrentTurn) AmberLight else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = player.avatarEmoji, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = player.name,
                        fontSize = 12.sp,
                        fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Medium,
                        color = if (player.isBankrupt) TextMuted else TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (player.isBankrupt) TextDecoration.LineThrough else null
                    )
                }

                if (isCurrentTurn && !player.isBankrupt) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4ADE80))
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // نقش / نوع
            val subtitle = if (player.isBankrupt) {
                "ورشکسته 💥"
            } else if (player.inJail) {
                "در بازداشتگاه 🔒"
            } else if (player.type == PlayerType.HUMAN) {
                "بازیکن ۱ (شما)"
            } else {
                player.aiPersonality.titleFa
            }

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = if (isCurrentTurn) Color(0xFFDDD6FE) else TextSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            // مبلغ نقدینگی
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = PersianUtils.formatNumber(player.cash),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (player.isBankrupt) TextMuted else TextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "تومان",
                    fontSize = 9.sp,
                    color = if (isCurrentTurn) Color(0xFFC4B5FD) else TextMuted
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // نوار کوچک پیشرفت مالی
            val cashRatio = (player.cash.toFloat() / 2500000f).coerceIn(0.05f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0x26FFFFFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(cashRatio)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (isCurrentTurn) PurpleAccent else Color(0xFF64748B))
                )
            }
        }
    }
}
