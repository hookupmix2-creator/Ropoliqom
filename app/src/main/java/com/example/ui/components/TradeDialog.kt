package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Player
import com.example.model.Tile
import com.example.model.TradeOffer
import com.example.ui.theme.*

@Composable
fun TradeDialog(
    currentPlayer: Player,
    otherPlayers: List<Player>,
    allTiles: List<Tile>,
    onProposeTrade: (TradeOffer) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTargetPlayerId by remember {
        mutableStateOf(otherPlayers.firstOrNull()?.id ?: -1)
    }

    val targetPlayer = otherPlayers.find { it.id == selectedTargetPlayerId }

    var offeredCash by remember { mutableStateOf(0) }
    var requestedCash by remember { mutableStateOf(0) }

    val selectedOfferedProps = remember { mutableStateListOf<Int>() }
    val selectedRequestedProps = remember { mutableStateListOf<Int>() }

    val myProperties = allTiles.filter { it.ownerId == currentPlayer.id }
    val targetProperties = allTiles.filter { it.ownerId == selectedTargetPlayerId }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.98f)
                .fillMaxHeight(0.88f)
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
                    text = "میز معامله و تبادل املاک 🤝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // انتخاب طرف معامله
                Text(text = "انتخاب شریک معامله:", fontSize = 13.sp, color = TextSecondary)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    otherPlayers.forEach { p ->
                        val isSelected = p.id == selectedTargetPlayerId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PurpleAccent else NavyCard)
                                .border(1.dp, if (isSelected) GoldPrimary else NavyBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedTargetPlayerId = p.id
                                    selectedRequestedProps.clear()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = p.avatarEmoji, fontSize = 16.sp)
                                Text(
                                    text = p.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextPrimary
                                )
                            }
                        }
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp), color = NavyBorder)

                // دو بخش: اموالی که شما می‌دهید / اموالی که دریافت می‌کنید
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // بخش ۱: دارایی‌های شما
                    item {
                        Text(
                            text = "دارایی‌هایی که پیشنهاد می‌دهید:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldProfit
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        // مبلغ نقد
                        CashAdjuster(
                            title = "پول نقد پیشنهادی:",
                            amount = offeredCash,
                            maxAmount = currentPlayer.cash,
                            onAmountChange = { offeredCash = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (myProperties.isEmpty()) {
                        item {
                            Text(text = "شما ملکی برای واگذاری ندارید.", color = TextMuted, fontSize = 12.sp)
                        }
                    } else {
                        items(myProperties) { tile ->
                            val isChecked = tile.id in selectedOfferedProps
                            PropertyCheckboxItem(
                                tile = tile,
                                isChecked = isChecked,
                                onToggle = {
                                    if (isChecked) selectedOfferedProps.remove(tile.id)
                                    else selectedOfferedProps.add(tile.id)
                                }
                            )
                        }
                    }

                    item {
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = NavyBorder)
                        // بخش ۲: دارایی‌های طرف مقابل
                        Text(
                            text = "دارایی‌هایی که درخواست می‌کنید:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        CashAdjuster(
                            title = "پول نقد درخواستی:",
                            amount = requestedCash,
                            maxAmount = targetPlayer?.cash ?: 0,
                            onAmountChange = { requestedCash = it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (targetProperties.isEmpty()) {
                        item {
                            Text(
                                text = "بازیکن مقابل در حال حاضر ملکی ندارد.",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        items(targetProperties) { tile ->
                            val isChecked = tile.id in selectedRequestedProps
                            PropertyCheckboxItem(
                                tile = tile,
                                isChecked = isChecked,
                                onToggle = {
                                    if (isChecked) selectedRequestedProps.remove(tile.id)
                                    else selectedRequestedProps.add(tile.id)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // دکمه‌های ثبت و انصراف
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (targetPlayer != null) {
                                val offer = TradeOffer(
                                    fromPlayerId = currentPlayer.id,
                                    toPlayerId = targetPlayer.id,
                                    offeredCash = offeredCash,
                                    offeredProperties = selectedOfferedProps.toList(),
                                    requestedCash = requestedCash,
                                    requestedProperties = selectedRequestedProps.toList()
                                )
                                onProposeTrade(offer)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldProfit)
                    ) {
                        Text(text = "ارسال پیشنهاد معامله", fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(0.7f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text(text = "انصراف")
                    }
                }
            }
        }
    }
}

@Composable
private fun CashAdjuster(
    title: String,
    amount: Int,
    maxAmount: Int,
    onAmountChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(NavyCard)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontSize = 12.sp, color = TextSecondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { onAmountChange((amount - 50000).coerceAtLeast(0)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "کاهش", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }
            Text(
                text = PersianUtils.formatCurrency(amount),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            IconButton(
                onClick = { onAmountChange((amount + 50000).coerceAtMost(maxAmount)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "افزایش", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun PropertyCheckboxItem(
    tile: Tile,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isChecked) PurpleAccent.copy(alpha = 0.25f) else NavyCard)
            .border(1.dp, if (isChecked) GoldPrimary else NavyBorder, RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(tile.group.colorHex))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = tile.name, fontSize = 13.sp, color = TextPrimary)
        }
        Text(
            text = PersianUtils.formatCurrency(tile.cost),
            fontSize = 12.sp,
            color = GoldPrimary
        )
    }
}
