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
import com.example.model.AuctionState
import com.example.model.Player
import com.example.model.PlayerType
import com.example.model.Tile
import com.example.ui.theme.*

@Composable
fun AuctionDialog(
    auction: AuctionState,
    tile: Tile,
    players: List<Player>,
    onPlaceBid: (playerId: Int, amount: Int) -> Unit,
    onPass: (playerId: Int) -> Unit
) {
    val currentBidder = players.find { it.id == auction.currentTurnPlayerId }
    val highestBidder = players.find { it.id == auction.highestBidderId }
    val isHumanTurn = currentBidder?.type == PlayerType.HUMAN

    Dialog(onDismissRequest = { /* Modal during active auction */ }) {
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
                // هدر حراج
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "🔨", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "حراج عمومی ملک",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // برچسب ملک
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(tile.group.colorHex))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tile.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // بالاترین پیشنهاد فعلی
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = NavyCard),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "بالاترین پیشنهاد قیمت ثبت‌شده:",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = PersianUtils.formatCurrency(auction.currentBid),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (highestBidder != null) "توسط: ${highestBidder.avatarEmoji} ${highestBidder.name}" else "هنوز پیشنهادی ثبت نشده است",
                            fontSize = 13.sp,
                            color = if (highestBidder != null) Color(highestBidder.colorHex) else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // نوبت پیشنهاد
                if (currentBidder != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(NavyBorder.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "نوبت پیشنهاد قیمت:", color = TextSecondary, fontSize = 13.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentBidder.avatarEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentBidder.name,
                                color = Color(currentBidder.colorHex),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // اقدامات بازیکن انسانی
                if (isHumanTurn && currentBidder != null) {
                    Text(
                        text = "پیشنهاد قیمت خود را انتخاب کنید:",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val step1 = auction.currentBid + 10000
                    val step2 = auction.currentBid + 25000
                    val step3 = auction.currentBid + 50000

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { onPlaceBid(currentBidder.id, step1) },
                            enabled = currentBidder.cash >= step1,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldProfit)
                        ) {
                            Text(
                                text = "+۱۰ هزار",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { onPlaceBid(currentBidder.id, step2) },
                            enabled = currentBidder.cash >= step2,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldProfit)
                        ) {
                            Text(
                                text = "+۲۵ هزار",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { onPlaceBid(currentBidder.id, step3) },
                            enabled = currentBidder.cash >= step3,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldProfit)
                        ) {
                            Text(
                                text = "+۵۰ هزار",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { onPass(currentBidder.id) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonLoss),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CrimsonLoss))
                    ) {
                        Text(text = "انصراف از این نوبت", fontWeight = FontWeight.Bold)
                    }
                } else {
                    // در حال تفکر هوش مصنوعی
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = GoldPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "هوش مصنوعی در حال تحلیل قیمت و استراتژی...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
