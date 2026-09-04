package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OnlineRoomPlayer
import com.example.model.RoomStatus
import com.example.model.ScreenRoute
import com.example.ui.components.PersianUtils
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

/**
 * صفحه اتاق انتظار آنلاین (Game Lobby / Waiting Room)
 */
@Composable
fun OnlineRoomScreen(
    viewModel: GameViewModel
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val onlineManager = viewModel.onlineManager
    val currentRoom by onlineManager.currentRoom.collectAsState()
    val currentUser by onlineManager.currentUser.collectAsState()
    val messages by onlineManager.roomMessages.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // هدایت خودکار هنگام تغییر وضعیت اتاق به حالت در حال بازی (PLAYING)
    LaunchedEffect(currentRoom?.status) {
        val room = currentRoom
        if (room != null && room.status == RoomStatus.PLAYING) {
            focusManager.clearFocus()
            viewModel.startOnlineGameFromRoom(room)
        }
    }

    val room = currentRoom
    if (room == null) {
        LaunchedEffect(Unit) {
            viewModel.navigateTo(ScreenRoute.ONLINE_LOBBY)
        }
        return
    }

    val isHost = room.hostUid == currentUser?.uid
    val myPlayer = room.players.find { it.uid == currentUser?.uid }
    val isMyReady = myPlayer?.isReady == true

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
            // سربرگ
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
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            onlineManager.leaveCurrentRoom()
                            viewModel.navigateTo(ScreenRoute.ONLINE_LOBBY)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FFFFFF))
                    ) {
                        Text(text = "✕", fontSize = 16.sp, color = TextPrimary)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = room.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "اتاق انتظار بازی آنلاین",
                            fontSize = 10.sp,
                            color = AmberLight
                        )
                    }

                    // وضعیت اتاق
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x2610B981))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${PersianUtils.formatNumber(room.players.size)} / ${PersianUtils.formatNumber(room.maxPlayers)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldProfit
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // باکس دعوت با کد اختصاصی اتاق
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, AmberPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = ElegantCardBg)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "کد دعوت اختصاصی این اتاق:",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = room.code,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AmberYellowGold,
                                    letterSpacing = 4.sp
                                )
                            }

                            Button(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("کد اتاق شهر ثروت", room.code)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "کد اتاق کپی شد", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x26F59E0B),
                                    contentColor = AmberLight
                                )
                            ) {
                                Text(text = "📋 کپی کد", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "جایگاه بازیکنان در اتاق:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // اسلات‌های بازیکنان (از ۱ تا maxPlayers)
                items(room.maxPlayers) { index ->
                    val player = room.players.getOrNull(index)
                    PlayerSlotCard(player = player, slotIndex = index + 1)
                }

                item {
                    Text(
                        text = "گفتگوی درون اتاق (چت):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                // لیست پیام‌های چت
                if (messages.isEmpty()) {
                    item {
                        Text(
                            text = "پیامی ارسال نشده است. پیامی بفرستید!",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                } else {
                    items(messages) { msg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x0DFFFFFF))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = msg.senderAvatar, fontSize = 16.sp)
                            Column {
                                Text(
                                    text = msg.senderName,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberLight
                                )
                                Text(
                                    text = msg.text,
                                    fontSize = 12.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                item {
                    // سطر ارسال پیام چت
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatInput,
                            onValueChange = { chatInput = it },
                            placeholder = { Text("ارسال پیام در اتاق...") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (chatInput.isNotBlank()) {
                                    onlineManager.sendChatMessage(chatInput)
                                    chatInput = ""
                                    focusManager.clearFocus()
                                }
                            }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberPrimary,
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (chatInput.isNotBlank()) {
                                    onlineManager.sendChatMessage(chatInput)
                                    chatInput = ""
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0x26F59E0B),
                                contentColor = AmberLight
                            ),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text(text = "ارسال", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    errorMessage?.let { err ->
                        Text(
                            text = err,
                            fontSize = 12.sp,
                            color = CrimsonLoss,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // نوار اقدامات پایینی
            Surface(
                color = ElegantCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // دکمه اعلام آمادگی
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            onlineManager.toggleReady()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isMyReady) Color(0xFF10B981) else Color(0x26FFFFFF),
                            contentColor = TextPrimary
                        )
                    ) {
                        Text(
                            text = if (isMyReady) "آماده‌ام ✅" else "اعلام آمادگی ⏳",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // دکمه شروع بازی (فقط برای میزبان)
                    if (isHost) {
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                val res = onlineManager.startOnlineGame()
                                if (res.isSuccess) {
                                    viewModel.startOnlineGameFromRoom(res.getOrThrow())
                                } else {
                                    errorMessage = res.exceptionOrNull()?.message
                                }
                            },
                            enabled = room.players.size >= 2,
                            modifier = Modifier
                                .weight(1.3f)
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberPrimary,
                                contentColor = ElegantDarkBg,
                                disabledContainerColor = AmberPrimary.copy(alpha = 0.35f),
                                disabledContentColor = TextMuted
                            )
                        ) {
                            Text(
                                text = "شروع بازی 🚀",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerSlotCard(
    player: OnlineRoomPlayer?,
    slotIndex: Int
) {
    if (player != null) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    1.dp,
                    if (player.isReady) EmeraldProfit.copy(alpha = 0.5f) else ElegantCardBorder,
                    RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = ElegantCardBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(player.colorHex))
                            .border(1.dp, AmberLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = player.avatarEmoji, fontSize = 20.sp)
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = player.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (player.isHost) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0x26F59E0B))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "میزبان 👑",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AmberLight
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (player.isConnected) "متصل به سرور" else "در حال اتصال...",
                            fontSize = 10.sp,
                            color = if (player.isConnected) EmeraldProfit else TextMuted
                        )
                    }
                }

                // برچسب وضعیت آمادگی
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (player.isReady) Color(0x2610B981) else Color(0x1AFFFFFF))
                        .border(
                            1.dp,
                            if (player.isReady) EmeraldProfit else Color(0x33FFFFFF),
                            CircleShape
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (player.isReady) "آماده ✅" else "در انتظار ⏳",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (player.isReady) EmeraldProfit else TextSecondary
                    )
                }
            }
        }
    } else {
        // اسلات خالی در انتظار بازیکن
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0x08FFFFFF))
                .border(1.dp, Color(0x1FFFFFFF), RoundedCornerShape(16.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "⏳", fontSize = 16.sp)
                Text(
                    text = "جایگاه $slotIndex: در انتظار پیوستن بازیکن جدید...",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}
