package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun BoardView(
    state: GameState,
    onTileClick: (Tile) -> Unit,
    onRollDice: () -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier
) {
    var boardMode by remember { mutableStateOf(0) } // 0: حلقه ۴۰ خانه، 1: ردیف‌های دسته‌بندی شده

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // انتخاب نمای نمایش تخته
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "زمین مونوپولی شهر ثروت",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary
            )

            TabRow(
                selectedTabIndex = boardMode,
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                containerColor = NavyCard,
                contentColor = GoldPrimary,
                indicator = {}
            ) {
                Tab(
                    selected = boardMode == 0,
                    onClick = { boardMode = 0 },
                    text = { Text("تخته کامل", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = boardMode == 1,
                    onClick = { boardMode = 1 },
                    text = { Text("نمای بخش‌ها", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (boardMode == 0) {
            // نمای تخته کامل مربعی با قابلیت اسکرول دوطرفه
            FullSquareBoard(
                state = state,
                onTileClick = onTileClick,
                onRollDice = onRollDice,
                isRolling = isRolling
            )
        } else {
            // نمای ۴ بخش تخته (جنوب، غرب، شمال، شرق) برای مشاهده بسیار راحت در موبایل
            QuarterSectionsBoard(
                state = state,
                onTileClick = onTileClick
            )
        }
    }
}

@Composable
private fun FullSquareBoard(
    state: GameState,
    onTileClick: (Tile) -> Unit,
    onRollDice: () -> Unit,
    isRolling: Boolean
) {
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    val boardSize = 580.dp
    val cornerSize = 70.dp
    val edgeTileWidth = 48.dp
    val edgeTileHeight = 70.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(BoardFeltBg.copy(alpha = 0.25f))
            .border(3.dp, BoardFeltBorder, RoundedCornerShape(28.dp))
            .horizontalScroll(hScroll)
            .verticalScroll(vScroll)
    ) {
        Box(
            modifier = Modifier
                .size(boardSize)
                .background(
                    Brush.radialGradient(
                        colors = listOf(BoardFeltBg.copy(alpha = 0.35f), ElegantDarkBg),
                        radius = 600f
                    )
                )
        ) {
            // ۴ گوشه تخته:
            // ۰: شروع (Corner جنوب-شرق) -> در RTL راست-پایین
            // ۱۰: بازداشتگاه (Corner جنوب-غرب) -> در RTL چپ-پایین
            // ۲۰: پارک آزاد (Corner شمال-غرب) -> در RTL چپ-بالا
            // ۳۰: رفتن به زندان (Corner شمال-شرق) -> در RTL راست-بالا

            // گوشه شروع (Tile 0) - پایین راست
            Box(
                modifier = Modifier
                    .size(cornerSize)
                    .align(Alignment.BottomEnd)
            ) {
                CornerTileView(tile = state.tiles[0], state = state, onClick = { onTileClick(state.tiles[0]) })
            }

            // گوشه بازداشتگاه (Tile 10) - پایین چپ
            Box(
                modifier = Modifier
                    .size(cornerSize)
                    .align(Alignment.BottomStart)
            ) {
                CornerTileView(tile = state.tiles[10], state = state, onClick = { onTileClick(state.tiles[10]) })
            }

            // گوشه پارک آزاد (Tile 20) - بالا چپ
            Box(
                modifier = Modifier
                    .size(cornerSize)
                    .align(Alignment.TopStart)
            ) {
                CornerTileView(tile = state.tiles[20], state = state, onClick = { onTileClick(state.tiles[20]) })
            }

            // گوشه فرستادن به زندان (Tile 30) - بالا راست
            Box(
                modifier = Modifier
                    .size(cornerSize)
                    .align(Alignment.TopEnd)
            ) {
                CornerTileView(tile = state.tiles[30], state = state, onClick = { onTileClick(state.tiles[30]) })
            }

            // لبه پایین (کاشی‌های ۱ تا ۹): از خانه ۹ تا ۱ از چپ به راست
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = cornerSize)
                    .height(edgeTileHeight),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (id in 9 downTo 1) {
                    val tile = state.tiles[id]
                    EdgeTileView(
                        tile = tile,
                        state = state,
                        isVertical = false,
                        modifier = Modifier.width(edgeTileWidth),
                        onClick = { onTileClick(tile) }
                    )
                }
            }

            // لبه بالا (کاشی‌های ۲۱ تا ۲۹): از ۲۱ تا ۲۹ از چپ به راست
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = cornerSize)
                    .height(edgeTileHeight),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (id in 21..29) {
                    val tile = state.tiles[id]
                    EdgeTileView(
                        tile = tile,
                        state = state,
                        isVertical = false,
                        modifier = Modifier.width(edgeTileWidth),
                        onClick = { onTileClick(tile) }
                    )
                }
            }

            // لبه چپ (کاشی‌های ۱۱ تا ۱۹): از بالا به پایین (۱۹ تا ۱۱)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(vertical = cornerSize)
                    .width(edgeTileHeight),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (id in 19 downTo 11) {
                    val tile = state.tiles[id]
                    EdgeTileView(
                        tile = tile,
                        state = state,
                        isVertical = true,
                        modifier = Modifier.height(edgeTileWidth),
                        onClick = { onTileClick(tile) }
                    )
                }
            }

            // لبه راست (کاشی‌های ۳۱ تا ۳۹): از بالا به پایین (۳۱ تا ۳۹)
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(vertical = cornerSize)
                    .width(edgeTileHeight),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                for (id in 31..39) {
                    val tile = state.tiles[id]
                    EdgeTileView(
                        tile = tile,
                        state = state,
                        isVertical = true,
                        modifier = Modifier.height(edgeTileWidth),
                        onClick = { onTileClick(tile) }
                    )
                }
            }

            // مرکز تخته (لوگوی شهر ثروت، صندوق پارک آزاد، رویداد فعال، تاس)
            CenterHub(
                state = state,
                onRollDice = onRollDice,
                isRolling = isRolling,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(cornerSize + 8.dp)
            )
        }
    }
}

@Composable
private fun CenterHub(
    state: GameState,
    onRollDice: () -> Unit,
    isRolling: Boolean,
    modifier: Modifier = Modifier
) {
    val currPlayer = state.players.getOrNull(state.currentTurnIndex)
    val isCurrentHuman = currPlayer?.type == PlayerType.HUMAN

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(2.dp, GoldBorderColor(state.activeGlobalEvent), RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = NavyCard.copy(alpha = 0.92f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // لوگو
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "👑", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "شهر ثروت",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // صندوق پارک آزاد
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2E1065))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "صندوق پارک آزاد: ${PersianUtils.formatCurrency(state.freeParkingJackpot)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight
                )
            }

            // رویداد جهانی فعال
            if (state.activeGlobalEvent != GlobalEventType.NONE) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🌍 رویداد فعال: ${state.activeGlobalEvent.title} (${PersianUtils.formatNumber(state.globalEventTurnsRemaining)} نوبت)",
                    fontSize = 10.sp,
                    color = EmeraldProfit,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // تاس‌ها
            DicePairView(
                dice1 = state.dice1,
                dice2 = state.dice2,
                isRolling = isRolling,
                enabled = isCurrentHuman && !state.hasRolled && !state.isGameOver,
                onRollClick = onRollDice
            )

            // دکمه تاس یا اعلان نوبت
            if (currPlayer != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val turnText = if (currPlayer.type == PlayerType.HUMAN) "نوبت شماست: ${currPlayer.name}" else "نوبت ${currPlayer.name}"
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0x26F59E0B))
                        .border(1.dp, Color(0x4DF59E0B), CircleShape)
                        .padding(horizontal = 14.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = turnText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberLight
                    )
                }
            }
        }
    }
}

@Composable
private fun GoldBorderColor(event: GlobalEventType): Color {
    return when (event) {
        GlobalEventType.MARKET_BOOM -> EmeraldProfit
        GlobalEventType.RECESSION -> CrimsonLoss
        GlobalEventType.OLYMPICS_HOSTING -> Color(0xFF38BDF8)
        else -> GoldPrimary
    }
}

@Composable
private fun CornerTileView(
    tile: Tile,
    state: GameState,
    onClick: () -> Unit
) {
    val playersOnTile = state.players.filter { it.position == tile.id && !it.isBankrupt }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, NavyBorder)
            .background(NavyCard)
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (tile.id) {
                    0 -> "🏁"
                    10 -> "🔒"
                    20 -> "🅿️"
                    else -> "🚨"
                },
                fontSize = 18.sp
            )
            Text(
                text = tile.name,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = GoldLight,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // مهره‌های روی این خانه
            if (playersOnTile.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                PlayerTokensRow(players = playersOnTile)
            }
        }
    }
}

@Composable
private fun EdgeTileView(
    tile: Tile,
    state: GameState,
    isVertical: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val playersOnTile = state.players.filter { it.position == tile.id && !it.isBankrupt }
    val owner = state.players.find { it.id == tile.ownerId }

    Box(
        modifier = modifier
            .border(0.5.dp, NavyBorder)
            .background(if (owner != null) Color(owner.colorHex).copy(alpha = 0.15f) else NavyDark)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // نوار رنگی گروه ملک
            if (tile.group != PropertyGroup.SPECIAL) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(tile.group.colorHex))
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }

            // آیکون یا خانه/هتل
            when {
                tile.hasSkyscraper -> Text(text = "🌆", fontSize = 10.sp)
                tile.hasHotel -> Text(text = "🏨", fontSize = 10.sp)
                tile.houses > 0 -> Text(text = "🏡".repeat(tile.houses.coerceAtMost(3)), fontSize = 8.sp)
                tile.type == TileType.RAILROAD -> Text(text = "🚂", fontSize = 10.sp)
                tile.type == TileType.UTILITY -> Text(text = "⚡", fontSize = 10.sp)
                tile.type == TileType.CHANCE -> Text(text = "❓", fontSize = 10.sp)
                tile.type == TileType.COMMUNITY_CHEST -> Text(text = "📦", fontSize = 10.sp)
                tile.type == TileType.TAX -> Text(text = "🏛️", fontSize = 10.sp)
                else -> Spacer(modifier = Modifier.height(4.dp))
            }

            // نام ملک
            Text(
                text = tile.name,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 2.dp)
            )

            // مهره‌های بازیکنان
            if (playersOnTile.isNotEmpty()) {
                PlayerTokensRow(players = playersOnTile)
            } else if (tile.cost > 0) {
                Text(
                    text = "${PersianUtils.formatNumber(tile.cost / 1000)}k",
                    fontSize = 7.sp,
                    color = GoldLight
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Composable
private fun PlayerTokensRow(players: List<Player>) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        players.take(3).forEach { p ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(Color(p.colorHex))
                    .border(0.5.dp, GoldPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = p.avatarEmoji, fontSize = 8.sp)
            }
        }
    }
}

@Composable
private fun QuarterSectionsBoard(
    state: GameState,
    onTileClick: (Tile) -> Unit
) {
    val sections = listOf(
        "بخش اول: از خانه شروع تا بازداشتگاه (خانه ۰ تا ۱۰)" to state.tiles.subList(0, 11),
        "بخش دوم: از بازداشتگاه تا پارک آزاد (خانه ۱۰ تا ۲۰)" to state.tiles.subList(10, 21),
        "بخش سوم: از پارک آزاد تا رفتن به زندان (خانه ۲۰ تا ۳۰)" to state.tiles.subList(20, 31),
        "بخش چهارم: از رفتن به زندان تا خانه شروع (خانه ۳۰ تا ۴۰)" to (state.tiles.subList(30, 40) + listOf(state.tiles[0]))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        sections.forEach { (title, tiles) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NavyCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tiles.forEach { tile ->
                            val owner = state.players.find { it.id == tile.ownerId }
                            val playersOnTile = state.players.filter { it.position == tile.id && !it.isBankrupt }

                            Card(
                                modifier = Modifier
                                    .width(76.dp)
                                    .clickable { onTileClick(tile) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (owner != null) Color(owner.colorHex).copy(alpha = 0.2f) else NavyDark
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (tile.group != PropertyGroup.SPECIAL) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(Color(tile.group.colorHex))
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    Text(
                                        text = tile.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (playersOnTile.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        PlayerTokensRow(players = playersOnTile)
                                    } else {
                                        Text(
                                            text = if (tile.cost > 0) PersianUtils.formatCurrency(tile.cost) else "-",
                                            fontSize = 9.sp,
                                            color = GoldLight,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
