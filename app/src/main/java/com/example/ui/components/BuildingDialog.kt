package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ai.AiDecisionEngine
import com.example.model.Player
import com.example.model.Tile
import com.example.ui.theme.*

@Composable
fun BuildingDialog(
    player: Player,
    allTiles: List<Tile>,
    onBuildHouse: (tileId: Int) -> Unit,
    onBuildHotel: (tileId: Int) -> Unit,
    onBuildSkyscraper: (tileId: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val monopolyGroups = AiDecisionEngine.getPlayerMonopolyGroups(player, allTiles)
    val eligibleTiles = allTiles.filter { it.ownerId == player.id && it.group in monopolyGroups }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, GoldPrimary, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = NavyDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "سازمان عمران و ساخت‌وساز شهر 🏗️",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(NavyCard)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "موجودی در دسترس شما:", fontSize = 12.sp, color = TextSecondary)
                    Text(
                        text = PersianUtils.formatCurrency(player.cash),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldProfit
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (eligibleTiles.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = "🏢", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "هنوز مونوپولی کامل ندارید!",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "طبق قوانین شهر ثروت، برای ساخت خانه و هتل ابتدا باید تمام املاک یک گروه رنگی خاص را به طور کامل تصاحب نمایید.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 22.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(eligibleTiles) { tile ->
                            BuildingTileCard(
                                tile = tile,
                                playerCash = player.cash,
                                onBuildHouse = { onBuildHouse(tile.id) },
                                onBuildHotel = { onBuildHotel(tile.id) },
                                onBuildSkyscraper = { onBuildSkyscraper(tile.id) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                ) {
                    Text(text = "بازگشت به صفحه بازی", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun BuildingTileCard(
    tile: Tile,
    playerCash: Int,
    onBuildHouse: () -> Unit,
    onBuildHotel: () -> Unit,
    onBuildSkyscraper: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(tile.group.colorHex))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = tile.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }

                // وضعیت سازه‌ها
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when {
                        tile.hasSkyscraper -> Text(text = "🌆 آسمان‌خراش", fontSize = 12.sp, color = GoldPrimary)
                        tile.hasHotel -> Text(text = "🏨 هتل لوکس", fontSize = 12.sp, color = GoldLight)
                        tile.houses > 0 -> Text(
                            text = "🏡 ${PersianUtils.formatNumber(tile.houses)} خانه",
                            fontSize = 12.sp,
                            color = EmeraldProfit
                        )
                        else -> Text(text = "زمین خام", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // دکمه‌های ارتقا
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (tile.canBuildHouse) {
                    val canAfford = playerCash >= tile.houseCost
                    Button(
                        onClick = onBuildHouse,
                        enabled = canAfford,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldProfit)
                    ) {
                        Text(
                            text = "+ ساخت خانه (${PersianUtils.formatCurrency(tile.houseCost)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                if (tile.canBuildHotel) {
                    val canAfford = playerCash >= tile.houseCost
                    Button(
                        onClick = onBuildHotel,
                        enabled = canAfford,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                    ) {
                        Text(
                            text = "+ تبدیل به هتل (${PersianUtils.formatCurrency(tile.houseCost)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyDarkest
                        )
                    }
                }

                if (tile.canBuildSkyscraper) {
                    val cost = tile.houseCost * 2
                    val canAfford = playerCash >= cost
                    Button(
                        onClick = onBuildSkyscraper,
                        enabled = canAfford,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                    ) {
                        Text(
                            text = "+ آسمان‌خراش (${PersianUtils.formatCurrency(cost)})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
