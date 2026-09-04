package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.model.Player
import com.example.model.Tile
import com.example.ui.theme.*

@Composable
fun BuyPropertyDialog(
    tile: Tile,
    player: Player,
    discountMultiplier: Float,
    onBuy: () -> Unit,
    onDecline: () -> Unit
) {
    val effectiveCost = (tile.cost * discountMultiplier).toInt()
    val canAfford = player.cash >= effectiveCost

    Dialog(onDismissRequest = { /* Modal: require choice */ }) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, GoldPrimary, RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = NavyDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // هدر
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(tile.group.colorHex))
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "فرصت خرید ملک جدید",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = tile.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "آیا تمایل دارید سند این ملک را خریداری کنید؟ در صورت انصراف، ملک مستقیماً وارد حراج عمومی خواهد شد.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // جدول قیمت و موجودی
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "قیمت رسمی سند:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = PersianUtils.formatCurrency(effectiveCost),
                                color = GoldPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        if (discountMultiplier < 1.0f) {
                            Text(
                                text = "🏷️ اعمال تخفیف جشنواره خرید ۲۰٪",
                                color = EmeraldProfit,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 8.dp), color = NavyBorder)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "موجودی فعلی شما:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = PersianUtils.formatCurrency(player.cash),
                                color = if (canAfford) EmeraldProfit else CrimsonLoss,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "اجاره اولیه دریافتی:", color = TextSecondary, fontSize = 13.sp)
                            Text(
                                text = PersianUtils.formatCurrency(tile.baseRent),
                                color = TextPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // دکمه‌های اقدام
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onBuy,
                        enabled = canAfford,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldProfit,
                            disabledContainerColor = EmeraldProfit.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "خرید ملک",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary))
                    ) {
                        Text(
                            text = "انصراف و حراج",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
