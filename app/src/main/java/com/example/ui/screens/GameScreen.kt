package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PlayerType
import com.example.model.ScreenRoute
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    val gameState by viewModel.gameState.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val gameLogs by viewModel.gameLogs.collectAsState()
    val isRolling by viewModel.isRollingDice.collectAsState()

    val activeCard by viewModel.activeCard.collectAsState()
    val buyPrompt by viewModel.buyPropertyPrompt.collectAsState()
    val auctionState by viewModel.auctionState.collectAsState()
    val isBuildingOpen by viewModel.isBuildingDialogOpen.collectAsState()
    val isTradeOpen by viewModel.isTradeDialogOpen.collectAsState()
    val inspectingTile by viewModel.inspectingTile.collectAsState()

    var showLogsSheet by remember { mutableStateOf(false) }

    val state = gameState
    if (state == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(NavyDarkest),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GoldPrimary)
        }
        return
    }

    val currPlayer = state.players.getOrNull(state.currentTurnIndex)
    val isHumanTurn = currPlayer?.type == PlayerType.HUMAN && !state.isGameOver

    Scaffold(
        topBar = {
            Surface(
                color = ElegantCardBg.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElegantCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // آیکون زرین لوگو و عنوان
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(AmberPrimary, AmberYellowGold)
                                    )
                                )
                                .shadow(8.dp, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ث",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = ElegantDarkBg
                            )
                        }

                        Column {
                            Text(
                                text = "شهر ثروت",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                lineHeight = 20.sp
                            )
                            Text(
                                text = "بورس و سرمایه‌گذاری • ${state.mode.titleFa}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = AmberLight,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // دکمه‌های اکشن بالا: تنظیمات، گزارش، صدا
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // گزارش وقایع
                        IconButton(
                            onClick = { showLogsSheet = !showLogsSheet },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x14FFFFFF))
                        ) {
                            BadgedBox(
                                badge = {
                                    if (gameLogs.isNotEmpty()) {
                                        Badge(containerColor = AmberPrimary) {
                                            Text(
                                                text = PersianUtils.formatNumber(gameLogs.size.coerceAtMost(99)),
                                                color = ElegantDarkBg
                                            )
                                        }
                                    }
                                }
                            ) {
                                Text(text = "📜", fontSize = 16.sp)
                            }
                        }

                        // صدا
                        IconButton(
                            onClick = { viewModel.toggleSound() },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x14FFFFFF))
                        ) {
                            Text(
                                text = if (settings.soundEnabled) "🔊" else "🔇",
                                fontSize = 16.sp
                            )
                        }

                        // منو و تنظیمات
                        IconButton(
                            onClick = { viewModel.navigateTo(ScreenRoute.MAIN_MENU) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0x14FFFFFF))
                        ) {
                            Text(text = "⚙️", fontSize = 16.sp)
                        }
                    }
                }
            }
        },
        bottomBar = {
            // نوار اقدامات پایینی Elegant Dark
            ElegantDarkBottomDock(
                state = state,
                currPlayer = currPlayer,
                isHumanTurn = isHumanTurn,
                hasRolled = state.hasRolled,
                isRolling = isRolling,
                onRollDice = { viewModel.rollDice() },
                onEndTurn = { viewModel.endTurn() },
                onPayJailBail = { currPlayer?.let { viewModel.payJailBail(it.id) } },
                onOpenBuild = { viewModel.openBuildingDialog() },
                onOpenTrade = { viewModel.openTradeDialog() },
                onOpenStats = { viewModel.navigateTo(ScreenRoute.STATS) },
                onOpenHistory = { showLogsSheet = true }
            )
        },
        containerColor = ElegantDarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // ردیف وضعیت بازیکنان
            PlayerStatusPanel(
                players = state.players,
                currentTurnPlayerId = currPlayer?.id ?: 0,
                allTiles = state.tiles,
                onPlayerClick = { /* باز کردن مشخصات */ }
            )

            // نوار آخرین پیام گزارش
            val latestLog = gameLogs.firstOrNull()
            if (latestLog != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(NavyCard.copy(alpha = 0.8f))
                        .border(1.dp, NavyBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = latestLog.icon, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = latestLog.text,
                        fontSize = 11.sp,
                        color = latestLog.highlightColorHex?.let { Color(it) } ?: TextPrimary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // تخته بازی
            BoardView(
                state = state,
                onTileClick = { viewModel.setTileInspection(it) },
                onRollDice = { viewModel.rollDice() },
                isRolling = isRolling,
                modifier = Modifier.weight(1f)
            )
        }
    }

    // ================= پنجره‌های تعاملی =================

    // بررسی سند ملک
    inspectingTile?.let { tile ->
        val owner = state.players.find { it.id == tile.ownerId }
        TileDetailDialog(
            tile = tile,
            owner = owner,
            onDismiss = { viewModel.setTileInspection(null) }
        )
    }

    // پیشنهاد خرید ملک
    buyPrompt?.let { tile ->
        if (currPlayer != null) {
            BuyPropertyDialog(
                tile = tile,
                player = currPlayer,
                discountMultiplier = state.activeGlobalEvent.buyDiscountMultiplier,
                onBuy = { viewModel.buyProperty(currPlayer.id, tile.id) },
                onDecline = { viewModel.declineBuyProperty() }
            )
        }
    }

    // حراج
    auctionState?.let { auction ->
        val tile = state.tiles[auction.propertyId]
        AuctionDialog(
            auction = auction,
            tile = tile,
            players = state.players,
            onPlaceBid = { pId, amount -> viewModel.placeAuctionBid(pId, amount) },
            onPass = { pId -> viewModel.passAuction(pId) }
        )
    }

    // کارت فعال
    activeCard?.let { card ->
        CardPopupDialog(
            card = card,
            onConfirm = { viewModel.executeActiveCard() }
        )
    }

    // ساخت و ساز
    if (isBuildingOpen && currPlayer != null) {
        BuildingDialog(
            player = currPlayer,
            allTiles = state.tiles,
            onBuildHouse = { viewModel.buildHouse(it) },
            onBuildHotel = { viewModel.buildHotel(it) },
            onBuildSkyscraper = { viewModel.buildSkyscraper(it) },
            onDismiss = { viewModel.closeBuildingDialog() }
        )
    }

    // معامله
    if (isTradeOpen && currPlayer != null) {
        val others = state.players.filter { it.id != currPlayer.id && !it.isBankrupt }
        TradeDialog(
            currentPlayer = currPlayer,
            otherPlayers = others,
            allTiles = state.tiles,
            onProposeTrade = { viewModel.proposeTrade(it) },
            onDismiss = { viewModel.closeTradeDialog() }
        )
    }

    // پیروزی بازی
    if (state.isGameOver) {
        val winner = state.players.find { it.id == state.winnerId }
        VictoryDialog(
            winner = winner,
            allTiles = state.tiles,
            onRestartGame = { viewModel.startNewGame() },
            onReturnToMenu = { viewModel.navigateTo(ScreenRoute.MAIN_MENU) }
        )
    }

    // کشوی گزارش تاریخچه بازی
    if (showLogsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLogsSheet = false },
            containerColor = NavyDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.6f)
                    .padding(16.dp)
            ) {
                Text(
                    text = "گزارش زنده وقایع بازی 📜",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(gameLogs) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(NavyCard)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = entry.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = entry.text,
                                fontSize = 12.sp,
                                color = entry.highlightColorHex?.let { Color(it) } ?: TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ElegantDarkBottomDock(
    state: com.example.model.GameState,
    currPlayer: com.example.model.Player?,
    isHumanTurn: Boolean,
    hasRolled: Boolean,
    isRolling: Boolean,
    onRollDice: () -> Unit,
    onEndTurn: () -> Unit,
    onPayJailBail: () -> Unit,
    onOpenBuild: () -> Unit,
    onOpenTrade: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val currentTile = currPlayer?.let { state.tiles.getOrNull(it.position) }
    val ownedCount = currPlayer?.propertiesOwned?.size ?: 0
    val totalBuildings = currPlayer?.propertiesOwned?.sumOf { id ->
        val t = state.tiles[id]
        t.houses + (if (t.hasHotel) 5 else 0)
    } ?: 0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ElegantCardBg)
                .border(1.dp, ElegantCardBorder, RoundedCornerShape(24.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // سطر سه شاخص سریع: املاک، موقعیت فعلی، ساختمان
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // املاک
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "املاک", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${PersianUtils.formatNumber(ownedCount)} ملک",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberLight
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color(0x1AFFFFFF))
                )

                // موقعیت
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "موقعیت", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = currentTile?.name ?: "خانه شروع",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(28.dp)
                        .background(Color(0x1AFFFFFF))
                )

                // ساختمان
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "ساختمان", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${PersianUtils.formatNumber(totalBuildings)} واحد",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA)
                    )
                }
            }

            // سطر دکمه‌های اصلی (پرتاب تاس و معامله)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // پرتاب تاس یا پایان نوبت
                if (hasRolled && isHumanTurn) {
                    Button(
                        onClick = onEndTurn,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleAccent,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "پایان نوبت 🏁",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (currPlayer?.inJail == true && isHumanTurn) {
                    Button(
                        onClick = onPayJailBail,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFB923C),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "وثیقه آزادی (۵۰k)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Button(
                        onClick = onRollDice,
                        enabled = isHumanTurn && !hasRolled && !isRolling,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = ElegantDarkBg,
                            disabledContainerColor = AmberPrimary.copy(alpha = 0.35f),
                            disabledContentColor = TextMuted
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🎲", fontSize = 18.sp)
                            Text(
                                text = if (isRolling) "در حال چرخش..." else "پرتاب تاس",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // دکمه معامله
                OutlinedButton(
                    onClick = onOpenTrade,
                    enabled = isHumanTurn,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0x14FFFFFF),
                        contentColor = TextPrimary,
                        disabledContainerColor = Color(0x0AFFFFFF),
                        disabledContentColor = TextMuted
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🤝", fontSize = 18.sp)
                        Text(
                            text = "معامله",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // سطر چهارگانه آیکون‌های دسترسی سریع: املاک، کارتها، ساخت، آمار
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickActionItem(icon = "🏢", label = "املاک", onClick = onOpenBuild)
                QuickActionItem(icon = "🃏", label = "کارت‌ها", onClick = onOpenHistory)
                QuickActionItem(icon = "🏗️", label = "ساخت", onClick = onOpenBuild)
                QuickActionItem(icon = "📊", label = "آمار", onClick = onOpenStats)
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x14FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = icon, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
    }
}
