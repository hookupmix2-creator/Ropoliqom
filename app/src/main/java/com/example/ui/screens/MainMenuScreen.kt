package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameMode
import com.example.model.ScreenRoute
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@Composable
fun MainMenuScreen(
    viewModel: GameViewModel
) {
    val settings by viewModel.settings.collectAsState()
    val hasSavedGame = viewModel.hasSavedGame()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NavyDarkest, NavyDark, PurpleDark)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // نوار بالای صفحه: تنظیمات صدا
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.toggleSound() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NavyCard)
                        .border(1.dp, NavyBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = if (settings.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "صدا",
                        tint = if (settings.soundEnabled) GoldPrimary else TextMuted
                    )
                }

                IconButton(
                    onClick = { viewModel.navigateTo(ScreenRoute.SETTINGS) },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NavyCard)
                        .border(1.dp, NavyBorder, CircleShape)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "تنظیمات", tint = TextPrimary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // لوگوی لوکس بازی «شهر ثروت»
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .shadow(16.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GoldPrimary, GoldDark, Color(0xFF92400E))
                        )
                    )
                    .border(3.dp, GoldLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "👑", fontSize = 48.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "شهر ثروت",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GoldPrimary,
                letterSpacing = 1.sp
            )

            Text(
                text = "بازی هوشمند خرید، معامله و سرمایه‌گذاری املاک",
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // دکمه‌های اصلی منو
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // بازی آنلاین با دیگران
                val onlineUser by viewModel.onlineManager.currentUser.collectAsState()
                MainMenuButton(
                    title = "بازی آنلاین با دیگران 🌐",
                    subtitle = if (onlineUser != null)
                        "لابی اتاق‌ها • خوش‌آمدید «${onlineUser?.displayName}»"
                    else
                        "عضویت، ایجاد اتاق و رقابت آنلاین با بازیکنان",
                    icon = Icons.Default.Public,
                    containerColor = AmberPrimary,
                    contentColor = ElegantDarkBg,
                    onClick = {
                        if (onlineUser != null) {
                            viewModel.navigateTo(ScreenRoute.ONLINE_LOBBY)
                        } else {
                            viewModel.navigateTo(ScreenRoute.ONLINE_AUTH)
                        }
                    }
                )

                // شروع بازی آفلاین جدید
                MainMenuButton(
                    title = "شروع بازی آفلاین",
                    subtitle = "انتخاب حالت بازی و چیدمان بازیکنان و هوش مصنوعی",
                    icon = Icons.Default.PlayArrow,
                    containerColor = Color(0x26FFFFFF),
                    contentColor = TextPrimary,
                    borderColor = Color(0x33FFFFFF),
                    onClick = { viewModel.navigateTo(ScreenRoute.PLAYER_SETUP) }
                )

                // ادامه بازی قبلی
                if (hasSavedGame) {
                    MainMenuButton(
                        title = "ادامه بازی قبلی",
                        subtitle = "بازیابی وضعیت بازی ذخیره‌شده",
                        icon = Icons.Default.Restore,
                        containerColor = EmeraldProfit,
                        contentColor = Color.White,
                        onClick = { viewModel.continueSavedGame() }
                    )
                }

                // چالش مستقیم با هوش مصنوعی
                MainMenuButton(
                    title = "چالش هوش مصنوعی",
                    subtitle = "رقابت تک‌نفره با ۴ نابغه اقتصادی",
                    icon = Icons.Default.SmartToy,
                    containerColor = PurpleAccent,
                    contentColor = Color.White,
                    onClick = {
                        viewModel.setGameMode(GameMode.AI_CHALLENGE)
                        viewModel.navigateTo(ScreenRoute.PLAYER_SETUP)
                    }
                )

                // بازی چندنفره محلی (Pass & Play)
                MainMenuButton(
                    title = "چندنفرۀ آفلاین (گوشی مشترک)",
                    subtitle = "بازی دورهمی با دوستان و خانواده ۲ تا ۶ نفره",
                    icon = Icons.Default.People,
                    containerColor = NavyCard,
                    contentColor = TextPrimary,
                    borderColor = GoldPrimary,
                    onClick = {
                        viewModel.setGameMode(GameMode.LOCAL_PASS_AND_PLAY)
                        viewModel.navigateTo(ScreenRoute.PLAYER_SETUP)
                    }
                )

                // بخش اطلاعات، آمار و راهنما
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SmallMenuCard(
                        title = "آمار و رکوردها",
                        icon = Icons.Default.BarChart,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenRoute.STATS) }
                    )

                    SmallMenuCard(
                        title = "دستاوردها",
                        icon = Icons.Default.EmojiEvents,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenRoute.ACHIEVEMENTS) }
                    )

                    SmallMenuCard(
                        title = "قوانین بازی",
                        icon = Icons.Default.MenuBook,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(ScreenRoute.RULES) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "نسخه ۲.۰ • طراحی‌شده با موتور هوشمند مونوپولی پارسی",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MainMenuButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color? = null,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.5.dp,
                borderColor ?: Color.Transparent,
                RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(contentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = contentColor)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = null,
                tint = contentColor.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun SmallMenuCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = GoldPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
