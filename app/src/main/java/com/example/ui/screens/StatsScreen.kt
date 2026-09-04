package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.ui.components.PersianUtils
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: GameViewModel
) {
    val stats by viewModel.generalStats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "آمار و کارنامه اقتصادی",
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
                .padding(16.dp)
        ) {
            // کارت خلاصه برد و باخت
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "مجموع بازی‌ها", fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = PersianUtils.formatNumber(stats.gamesPlayed),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "پیروزی‌ها 🏆", fontSize = 12.sp, color = EmeraldProfit)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = PersianUtils.formatNumber(stats.wins),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldProfit
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "شکست‌ها", fontSize = 12.sp, color = CrimsonLoss)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = PersianUtils.formatNumber(stats.losses),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CrimsonLoss
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "شاخص‌های عملکرد مالی و توسعه:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // شبکه آمار
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    StatMetricCard(
                        title = "بیشترین ثروت ثبت‌شده",
                        value = PersianUtils.formatCurrency(stats.maxWealthAchieved.toInt()),
                        icon = Icons.Default.Savings,
                        accentColor = GoldPrimary
                    )
                }

                item {
                    StatMetricCard(
                        title = "مجموع عواید و درآمد",
                        value = PersianUtils.formatCurrency(stats.totalCashEarned.toInt()),
                        icon = Icons.Default.MonetizationOn,
                        accentColor = EmeraldProfit
                    )
                }

                item {
                    StatMetricCard(
                        title = "اجاره پرداخت‌شده به رقبا",
                        value = PersianUtils.formatCurrency(stats.totalRentPaid.toInt()),
                        icon = Icons.Default.Payment,
                        accentColor = CrimsonLoss
                    )
                }

                item {
                    StatMetricCard(
                        title = "اسناد خریداری شده",
                        value = "${PersianUtils.formatNumber(stats.propertiesPurchased)} ملک",
                        icon = Icons.Default.HomeWork,
                        accentColor = Color(0xFF38BDF8)
                    )
                }

                item {
                    StatMetricCard(
                        title = "خانه‌های احداث شده",
                        value = "${PersianUtils.formatNumber(stats.housesBuilt)} خانه",
                        icon = Icons.Default.House,
                        accentColor = EmeraldProfit
                    )
                }

                item {
                    StatMetricCard(
                        title = "هتل‌های مجلل تأسیس شده",
                        value = "${PersianUtils.formatNumber(stats.hotelsBuilt)} هتل",
                        icon = Icons.Default.Apartment,
                        accentColor = GoldLight
                    )
                }

                item {
                    StatMetricCard(
                        title = "معاملات موفق دوجانبه",
                        value = "${PersianUtils.formatNumber(stats.tradesCompleted)} معامله",
                        icon = Icons.Default.Handshake,
                        accentColor = PurpleAccent
                    )
                }

                item {
                    StatMetricCard(
                        title = "دفعات پرتاب تاس",
                        value = "${PersianUtils.formatNumber(stats.diceRolled)} بار",
                        icon = Icons.Default.Casino,
                        accentColor = GoldAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}
