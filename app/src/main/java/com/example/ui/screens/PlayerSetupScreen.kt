package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameDefaults
import com.example.model.AiPersonality
import com.example.model.GameMode
import com.example.model.Player
import com.example.model.PlayerType
import com.example.model.ScreenRoute
import com.example.ui.components.PersianUtils
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSetupScreen(
    viewModel: GameViewModel
) {
    val setupPlayers by viewModel.playerSetupList.collectAsState()
    val selectedMode by viewModel.selectedGameMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "تنظیمات و چیدمان بازی",
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
        bottomBar = {
            Surface(
                color = NavyDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NavyBorder)
            ) {
                Button(
                    onClick = { viewModel.startNewGame(selectedMode) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = NavyDarkest)
                ) {
                    Text(
                        text = "ورود به بازی و آغاز رقابت 🎲",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = NavyDarkest
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // انتخاب حالت بازی
            item {
                Text(
                    text = "انتخاب حالت بازی:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameMode.entries.take(3).forEach { mode ->
                        val isSelected = mode == selectedMode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setGameMode(mode) },
                            label = {
                                Text(
                                    text = mode.titleFa,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = NavyDarkest,
                                containerColor = NavyCard,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    GameMode.entries.drop(3).forEach { mode ->
                        val isSelected = mode == selectedMode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setGameMode(mode) },
                            label = {
                                Text(
                                    text = mode.titleFa,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = NavyDarkest,
                                containerColor = NavyCard,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                Text(
                    text = selectedMode.descriptionFa,
                    fontSize = 12.sp,
                    color = GoldLight,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            // تعداد بازیکنان (۲ تا ۶)
            item {
                Divider(color = NavyBorder)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "تعداد بازیکنان:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        (2..6).forEach { count ->
                            val isSelected = count == setupPlayers.size
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoldPrimary else NavyCard)
                                    .border(1.dp, if (isSelected) GoldPrimary else NavyBorder, CircleShape)
                                    .clickable { viewModel.updateSetupPlayerCount(count) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = PersianUtils.toPersianDigits(count),
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) NavyDarkest else TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // فهرست بازیکنان
            item {
                Divider(color = NavyBorder)
                Text(
                    text = "مشخصات و هوش مصنوعی بازیکنان:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            itemsIndexed(setupPlayers) { index, player ->
                PlayerSetupCard(
                    player = player,
                    index = index,
                    onPlayerChange = { updated -> viewModel.updateSetupPlayer(index, updated) }
                )
            }
        }
    }
}

@Composable
private fun PlayerSetupCard(
    player: Player,
    index: Int,
    onPlayerChange: (Player) -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }
    var nameInput by remember(player.name) { mutableStateOf(player.name) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // سطر اول: آواتار + نام + انتخاب انسان / هوش مصنوعی
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(player.colorHex))
                            .border(1.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = player.avatarEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    if (isEditingName) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                onPlayerChange(player.copy(name = it))
                            },
                            modifier = Modifier.width(130.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = NavyBorder
                            )
                        )
                    } else {
                        Column(
                            modifier = Modifier.clickable { isEditingName = true }
                        ) {
                            Text(
                                text = player.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(text = "برای ویرایش نام لمس کنید", fontSize = 10.sp, color = TextMuted)
                        }
                    }
                }

                // سوییچ انسان / ربات
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (player.type == PlayerType.HUMAN) "انسان" else "هوش مصنوعی",
                        fontSize = 11.sp,
                        color = if (player.type == PlayerType.HUMAN) EmeraldProfit else PurpleAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = player.type == PlayerType.AI,
                        onCheckedChange = { isAi ->
                            onPlayerChange(player.copy(type = if (isAi) PlayerType.AI else PlayerType.HUMAN))
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PurpleAccent,
                            checkedTrackColor = NavyDark,
                            uncheckedThumbColor = EmeraldProfit,
                            uncheckedTrackColor = NavyDark
                        )
                    )
                }
            }

            // انتخاب آواتار ایموجی
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "آواتار:", fontSize = 11.sp, color = TextSecondary)
                GameDefaults.AVATARS.take(6).forEach { avatar ->
                    val isSelected = player.avatarEmoji == avatar
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) PurpleAccent else NavyDark)
                            .border(1.dp, if (isSelected) GoldPrimary else Color.Transparent, CircleShape)
                            .clickable { onPlayerChange(player.copy(avatarEmoji = avatar)) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = avatar, fontSize = 14.sp)
                    }
                }
            }

            // انتخاب استراتژی هوش مصنوعی در صورت AI بودن
            if (player.type == PlayerType.AI) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = NavyBorder.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "شخصیت و استراتژی هوش مصنوعی:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AiPersonality.entries.forEach { personality ->
                        val isSelected = player.aiPersonality == personality
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PurpleAccent else NavyDark)
                                .border(1.dp, if (isSelected) GoldPrimary else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { onPlayerChange(player.copy(aiPersonality = personality)) }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = personality.titleFa.split(" ").firstOrNull() ?: "",
                                fontSize = 10.sp,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Text(
                    text = "ویژگی: ${player.aiPersonality.descriptionFa}",
                    fontSize = 10.sp,
                    color = GoldLight,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
