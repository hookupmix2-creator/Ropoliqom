package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GameViewModel
) {
    val settings by viewModel.settings.collectAsState()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "تنظیمات بازی",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenRoute.MAIN_MENU) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
            )
        },
        containerColor = NavyDarkest
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // تنظیمات صوتی و دیداری
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "تنظیمات صوتی و گرافیکی:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // افکت‌های صوتی
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "افکت‌های صوتی بازی", fontSize = 14.sp, color = TextPrimary)
                            Text(text = "صدای تاس، خرید، پرداخت و پیروزی", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = settings.soundEnabled,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(soundEnabled = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldPrimary,
                                checkedTrackColor = NavyDark
                            )
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = NavyBorder)

                    // انیمیشن‌های سریع
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "انیمیشن‌های سریع", fontSize = 14.sp, color = TextPrimary)
                            Text(text = "حرکت پرسرعت مهره‌ها روی تخته", fontSize = 11.sp, color = TextSecondary)
                        }
                        Switch(
                            checked = settings.fastAnimations,
                            onCheckedChange = { viewModel.updateSettings(settings.copy(fastAnimations = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldPrimary,
                                checkedTrackColor = NavyDark
                            )
                        )
                    }
                }
            }

            // سرعت تفکر هوش مصنوعی
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "سرعت تفکر هوش مصنوعی:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "مدت زمانی که ربات برای تحلیل معامله و تاس صرف می‌کند",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val speeds = listOf(
                            "سریع (۰.۴ ثانیه)" to 400L,
                            "متعادل (۰.۸ ثانیه)" to 800L,
                            "تحلیلی (۱.۵ ثانیه)" to 1500L
                        )
                        speeds.forEach { (label, delayMs) ->
                            val isSelected = settings.aiThinkingDelayMs == delayMs
                            Button(
                                onClick = { viewModel.updateSettings(settings.copy(aiThinkingDelayMs = delayMs)) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) GoldPrimary else NavyDark,
                                    contentColor = if (isSelected) NavyDarkest else TextPrimary
                                )
                            ) {
                                Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // پاکسازی حافظه بازی
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "مدیریت حافظه و داده‌ها:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonLoss
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonLoss),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CrimsonLoss))
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "پاک کردن بازی ذخیره‌شده فعلی", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(text = "حذف بازی ذخیره‌شده", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(text = "آیا مطمئن هستید که می‌خواهید بازی ذخیره‌شده قبلی را پاک کنید؟ این عملیات غیرقابل بازگشت است.", color = TextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        // پاکسازی
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonLoss)
                ) {
                    Text(text = "تایید و حذف", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(text = "انصراف", color = TextSecondary)
                }
            },
            containerColor = NavyDark
        )
    }
}
