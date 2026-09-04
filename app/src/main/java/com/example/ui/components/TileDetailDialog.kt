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
import com.example.model.TileType
import com.example.ui.theme.*

@Composable
fun TileDetailDialog(
    tile: Tile,
    owner: Player?,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(2.dp, GoldPrimary, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = NavyDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // هدر سند مالکیت با رنگ گروه
                val bannerColor = Color(tile.group.colorHex)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(bannerColor)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "سند مالکیت",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = tile.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // اطلاعات عمومی و قیمت خرید
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "قیمت خرید ملک:", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        text = if (tile.cost > 0) PersianUtils.formatCurrency(tile.cost) else "غیرقابل خرید",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 10.dp), color = NavyBorder)

                // وضعیت مالکیت
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "مالک فعلی:", color = TextSecondary, fontSize = 14.sp)
                    if (owner != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = owner.avatarEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = owner.name,
                                color = Color(owner.colorHex),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        Text(text = "در اختیار بانک شهر ثروت", color = TextMuted, fontSize = 13.sp)
                    }
                }

                if (tile.type == TileType.PROPERTY) {
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = NavyBorder)

                    // نرخ‌های اجاره
                    RentRow(title = "اجاره پایه:", amount = tile.baseRent)
                    RentRow(title = "اجاره با کل گروه (مونوپولی):", amount = tile.baseRent * 2)
                    tile.houseRents.forEachIndexed { index, rent ->
                        RentRow(title = "با ${PersianUtils.formatNumber(index + 1)} خانه:", amount = rent)
                    }
                    RentRow(title = "با هتل مجلل:", amount = tile.hotelRent, isHighlight = true)
                    RentRow(title = "با آسمان‌خراش ویژه:", amount = tile.skyscraperRent, isHighlight = true)

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = NavyBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "هزینه هر خانه / هتل:", color = TextSecondary, fontSize = 13.sp)
                        Text(
                            text = PersianUtils.formatCurrency(tile.houseCost),
                            color = EmeraldProfit,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                } else if (tile.type == TileType.RAILROAD) {
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = NavyBorder)
                    Text(
                        text = "اجاره ایستگاه‌ها:\n• ۱ ایستگاه: ۲۵٬۰۰۰ تومان\n• ۲ ایستگاه: ۵۰٬۰۰۰ تومان\n• ۳ ایستگاه: ۱۰۰٬۰۰۰ تومان\n• ۴ ایستگاه: ۲۰۰٬۰۰۰ تومان",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )
                } else if (tile.type == TileType.UTILITY) {
                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = NavyBorder)
                    Text(
                        text = "اجاره خدمات عمومی:\nاجاره بر اساس مجموع عدد تاس متغیر است (۴٬۰۰۰ یا ۱۰٬۰۰۰ برابر عدد تاس).",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 22.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "بستن سند", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun RentRow(title: String, amount: Int, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = if (isHighlight) GoldLight else TextSecondary
        )
        Text(
            text = PersianUtils.formatCurrency(amount),
            fontSize = 13.sp,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal,
            color = if (isHighlight) GoldPrimary else TextPrimary
        )
    }
}
