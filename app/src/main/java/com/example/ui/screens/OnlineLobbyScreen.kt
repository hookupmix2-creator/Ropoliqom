package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OnlineRoom
import com.example.model.ScreenRoute
import com.example.ui.components.PersianUtils
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

/**
 * صفحه لابی و لیست اتاق‌های آنلاین با طراحی تیره لوکس (Elegant Dark)
 */
@Composable
fun OnlineLobbyScreen(
    viewModel: GameViewModel
) {
    val onlineManager = viewModel.onlineManager
    val currentUser by onlineManager.currentUser.collectAsState()
    val rooms by onlineManager.availableRooms.collectAsState()
    val focusManager = LocalFocusManager.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var inputCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
            .background(ElegantDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // سربرگ بالای صفحه: پروفایل بازیکن و دکمه خروج
            Surface(
                color = ElegantCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.navigateTo(ScreenRoute.MAIN_MENU)
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0x14FFFFFF))
                        ) {
                            Text(text = "✕", fontSize = 16.sp, color = TextPrimary)
                        }

                        // اطلاعات بازیکن
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color(0x26F59E0B))
                                .border(1.dp, AmberLight, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = currentUser?.avatarEmoji ?: "👑", fontSize = 20.sp)
                        }

                        Column {
                            Text(
                                text = currentUser?.displayName ?: "کاربر آنلاین",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "موجودی: ${PersianUtils.formatNumber(currentUser?.coins ?: 0)}",
                                    fontSize = 10.sp,
                                    color = AmberLight
                                )
                                Text(text = "سکه 💰", fontSize = 9.sp, color = TextMuted)
                            }
                        }
                    }

                    // دکمه خروج از حساب
                    IconButton(
                        onClick = {
                            onlineManager.logout()
                            viewModel.navigateTo(ScreenRoute.ONLINE_AUTH)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FFFFFF))
                    ) {
                        Text(text = "🚪", fontSize = 16.sp)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))

                    // دکمه بازی سریع و ساخت اتاق
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val res = onlineManager.quickMatch()
                                if (res.isSuccess) {
                                    viewModel.navigateTo(ScreenRoute.ONLINE_ROOM)
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberPrimary,
                                contentColor = ElegantDarkBg
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "⚡", fontSize = 18.sp)
                                Text(text = "بازی سریع", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = { showCreateDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0x14FFFFFF),
                                contentColor = TextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "➕", fontSize = 16.sp)
                                Text(text = "ساخت اتاق", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    // کارت پیوستن با کد ۶ رقمی
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, ElegantCardBorder, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = ElegantCardBg)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "🔑 ورود با کد دعوت اتاق:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = inputCode,
                                    onValueChange = { if (it.length <= 6) inputCode = it },
                                    placeholder = { Text("کد ۶ رقمی اتاق") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AmberPrimary,
                                        unfocusedBorderColor = Color(0x33FFFFFF)
                                    )
                                )

                                Button(
                                    onClick = {
                                        focusManager.clearFocus()
                                        if (inputCode.length == 6) {
                                            val res = onlineManager.joinRoomByCode(inputCode)
                                            if (res.isSuccess) {
                                                viewModel.navigateTo(ScreenRoute.ONLINE_ROOM)
                                            } else {
                                                errorMessage = res.exceptionOrNull()?.message
                                            }
                                        } else {
                                            errorMessage = "لطفاً کد ۶ رقمی کامل را وارد کنید"
                                        }
                                    },
                                    modifier = Modifier.height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF38BDF8),
                                        contentColor = ElegantDarkBg
                                    )
                                ) {
                                    Text(text = "پیوستن", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            errorMessage?.let { err ->
                                Text(
                                    text = err,
                                    fontSize = 11.sp,
                                    color = CrimsonLoss,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "اتاق‌های عمومی فعال",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Text(
                            text = "${PersianUtils.formatNumber(rooms.size)} اتاق باز",
                            fontSize = 11.sp,
                            color = AmberLight
                        )
                    }
                }

                if (rooms.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "در حال حاضر اتاقی وجود ندارد.\nیک اتاق جدید بسازید یا بازی سریع را آغاز کنید!",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                } else {
                    items(rooms) { room ->
                        RoomCardItem(
                            room = room,
                            onJoin = {
                                val res = onlineManager.joinRoomByCode(room.code)
                                if (res.isSuccess) {
                                    viewModel.navigateTo(ScreenRoute.ONLINE_ROOM)
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message
                                }
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // دیالوگ ساخت اتاق جدید
        if (showCreateDialog) {
            CreateRoomDialog(
                onDismiss = { showCreateDialog = false },
                onCreate = { title, maxPlayers ->
                    val res = onlineManager.createRoom(title, maxPlayers)
                    showCreateDialog = false
                    if (res.isSuccess) {
                        viewModel.navigateTo(ScreenRoute.ONLINE_ROOM)
                    } else {
                        errorMessage = res.exceptionOrNull()?.message
                    }
                }
            )
        }
    }
}

@Composable
private fun RoomCardItem(
    room: OnlineRoom,
    onJoin: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, ElegantCardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = ElegantCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x14FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎲", fontSize = 22.sp)
                }

                Column {
                    Text(
                        text = room.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "میزبان: ${room.hostName}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                        Text(text = "•", fontSize = 10.sp, color = TextMuted)
                        Text(
                            text = "کد: ${room.code}",
                            fontSize = 10.sp,
                            color = AmberLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // نشان ظرفیت
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x26F59E0B))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${PersianUtils.formatNumber(room.players.size)} / ${PersianUtils.formatNumber(room.maxPlayers)} نفر",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberLight
                    )
                }

                Button(
                    onClick = onJoin,
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberPrimary,
                        contentColor = ElegantDarkBg
                    )
                ) {
                    Text(text = "ورود", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CreateRoomDialog(
    onDismiss: () -> Unit,
    onCreate: (title: String, maxPlayers: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedPlayers by remember { mutableIntStateOf(4) }
    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = {
            focusManager.clearFocus()
            onDismiss()
        },
        title = {
            Text(
                text = "ساخت اتاق آنلاین جدید",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        focusManager.clearFocus()
                    },
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان اتاق") },
                    placeholder = { Text("مثال: اتاق سرمایه‌گذاران تهران") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberPrimary,
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    )
                )

                Text(
                    text = "ظرفیت بازیکنان:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(2, 3, 4).forEach { count ->
                        val isSelected = selectedPlayers == count
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AmberPrimary else Color(0x14FFFFFF))
                                .clickable {
                                    focusManager.clearFocus()
                                    selectedPlayers = count
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${PersianUtils.formatNumber(count)} نفره",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) ElegantDarkBg else TextPrimary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onCreate(title, selectedPlayers)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary, contentColor = ElegantDarkBg),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "ایجاد اتاق", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = {
                focusManager.clearFocus()
                onDismiss()
            }) {
                Text(text = "انصراف", color = TextSecondary)
            }
        },
        containerColor = ElegantCardBg,
        shape = RoundedCornerShape(20.dp)
    )
}
