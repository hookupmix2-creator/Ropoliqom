package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.AiDecisionEngine
import com.example.audio.SoundManager
import com.example.data.GameDefaults
import com.example.data.GameStorage
import com.example.model.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = GameStorage(application.applicationContext)
    val onlineManager = com.example.data.OnlineGameManager(application.applicationContext)

    // مسیر جاری صفحه
    private val _currentRoute = MutableStateFlow(ScreenRoute.MAIN_MENU)
    val currentRoute: StateFlow<ScreenRoute> = _currentRoute.asStateFlow()

    // وضعیت بازی فعال
    private val _gameState = MutableStateFlow<GameState?>(null)
    val gameState: StateFlow<GameState?> = _gameState.asStateFlow()

    // وضعیت تنظیمات و آمارها
    private val _settings = MutableStateFlow(storage.loadSettings())
    val settings: StateFlow<SettingsState> = _settings.asStateFlow()

    private val _generalStats = MutableStateFlow(storage.loadStats())
    val generalStats: StateFlow<GeneralStats> = _generalStats.asStateFlow()

    private val _achievements = MutableStateFlow(storage.loadAchievements())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    // گزارش رخدادهای بازی
    private val _gameLogs = MutableStateFlow<List<GameLogEntry>>(emptyList())
    val gameLogs: StateFlow<List<GameLogEntry>> = _gameLogs.asStateFlow()

    // پنجره‌های فعال
    private val _activeCard = MutableStateFlow<GameCard?>(null)
    val activeCard: StateFlow<GameCard?> = _activeCard.asStateFlow()

    private val _buyPropertyPrompt = MutableStateFlow<Tile?>(null)
    val buyPropertyPrompt: StateFlow<Tile?> = _buyPropertyPrompt.asStateFlow()

    private val _auctionState = MutableStateFlow<AuctionState?>(null)
    val auctionState: StateFlow<AuctionState?> = _auctionState.asStateFlow()

    private val _activeTradeOffer = MutableStateFlow<TradeOffer?>(null)
    val activeTradeOffer: StateFlow<TradeOffer?> = _activeTradeOffer.asStateFlow()

    private val _isBuildingDialogOpen = MutableStateFlow(false)
    val isBuildingDialogOpen: StateFlow<Boolean> = _isBuildingDialogOpen.asStateFlow()

    private val _isTradeDialogOpen = MutableStateFlow(false)
    val isTradeDialogOpen: StateFlow<Boolean> = _isTradeDialogOpen.asStateFlow()

    private val _inspectingTile = MutableStateFlow<Tile?>(null)
    val inspectingTile: StateFlow<Tile?> = _inspectingTile.asStateFlow()

    // وضعیت انیمیشن تاس و حرکت
    private val _isRollingDice = MutableStateFlow(false)
    val isRollingDice: StateFlow<Boolean> = _isRollingDice.asStateFlow()

    private val _isMovingPawn = MutableStateFlow(false)
    val isMovingPawn: StateFlow<Boolean> = _isMovingPawn.asStateFlow()

    // تنظیمات پیش‌نیاز صفحه انتخاب بازیکنان
    private val _playerSetupList = MutableStateFlow<List<Player>>(createInitialSetupPlayers(4))
    val playerSetupList: StateFlow<List<Player>> = _playerSetupList.asStateFlow()

    private val _selectedGameMode = MutableStateFlow(GameMode.CLASSIC)
    val selectedGameMode: StateFlow<GameMode> = _selectedGameMode.asStateFlow()

    private val allCards = GameDefaults.createCards()

    init {
        SoundManager.isSoundEnabled = _settings.value.soundEnabled
    }

    // ================= ناوبری =================

    fun navigateTo(route: ScreenRoute) {
        _currentRoute.value = route
    }

    fun hasSavedGame(): Boolean = storage.hasSavedGame()

    fun continueSavedGame() {
        val loaded = storage.loadGameState()
        if (loaded != null && !loaded.isGameOver) {
            _gameState.value = loaded
            _currentRoute.value = ScreenRoute.GAME
            addLog("بازی قبلی با موفقیت بازیابی شد.", 0xFF10B981, "📂")
        }
    }

    // ================= راه‌اندازی بازی جدید =================

    fun updateSetupPlayerCount(count: Int) {
        val current = _playerSetupList.value.toMutableList()
        if (count > current.size) {
            for (i in current.size until count) {
                val name = GameDefaults.DEFAULT_NAMES.getOrElse(i) { "بازیکن ${i + 1}" }
                val avatar = GameDefaults.AVATARS.getOrElse(i) { "🌟" }
                val color = GameDefaults.PLAYER_COLORS.getOrElse(i) { 0xFF1E88E5 }
                val personality = AiPersonality.entries[i % AiPersonality.entries.size]
                current.add(
                    Player(
                        id = i,
                        name = name,
                        avatarEmoji = avatar,
                        colorHex = color,
                        type = if (i == 0) PlayerType.HUMAN else PlayerType.AI,
                        aiPersonality = personality,
                        cash = _selectedGameMode.value.startingCash
                    )
                )
            }
        } else if (count < current.size && count >= 2) {
            while (current.size > count) {
                current.removeAt(current.size - 1)
            }
        }
        _playerSetupList.value = current
    }

    fun updateSetupPlayer(index: Int, updated: Player) {
        val list = _playerSetupList.value.toMutableList()
        if (index in list.indices) {
            list[index] = updated
            _playerSetupList.value = list
        }
    }

    fun setGameMode(mode: GameMode) {
        _selectedGameMode.value = mode
        val updatedList = _playerSetupList.value.map { it.copy(cash = mode.startingCash) }
        _playerSetupList.value = updatedList
    }

    fun startNewGame(mode: GameMode? = null) {
        val effectiveMode = mode ?: _selectedGameMode.value
        val players = _playerSetupList.value.mapIndexed { idx, p ->
            p.copy(
                id = idx,
                cash = effectiveMode.startingCash,
                position = 0,
                inJail = false,
                jailTurns = 0,
                isBankrupt = false,
                propertiesOwned = emptyList()
            )
        }

        val board = GameDefaults.createDefaultBoard()
        val newState = GameState(
            mode = effectiveMode,
            players = players,
            tiles = board,
            currentTurnIndex = 0,
            dice1 = 1,
            dice2 = 1,
            hasRolled = false,
            freeParkingJackpot = 100000,
            activeGlobalEvent = if (effectiveMode == GameMode.CHAOS) GlobalEventType.MARKET_BOOM else GlobalEventType.NONE,
            globalEventTurnsRemaining = if (effectiveMode == GameMode.CHAOS) 3 else 0,
            turnNumber = 1
        )

        _gameState.value = newState
        _gameLogs.value = emptyList()
        _currentRoute.value = ScreenRoute.GAME
        storage.saveGameState(newState)

        addLog("بازی بزرگ شهر ثروت آغاز شد! نوبت نخست با «${players[0].name}» است.", 0xFFFFD700, "🎉")

        // بررسی نوبت نخست اگر هوش مصنوعی باشد
        checkAiTurn()
    }

    /**
     * آغاز بازی از اتاق آنلاین چندنفره
     */
    fun startOnlineGameFromRoom(room: OnlineRoom) {
        val currentUid = onlineManager.currentUser.value?.uid ?: ""
        val players = room.players.mapIndexed { idx, rp ->
            Player(
                id = idx,
                name = rp.name,
                avatarEmoji = rp.avatarEmoji,
                colorHex = rp.colorHex,
                type = if (rp.uid == currentUid) PlayerType.HUMAN else PlayerType.AI,
                aiPersonality = AiPersonality.PRO_TRADER,
                cash = 1500000,
                position = 0,
                inJail = false,
                jailTurns = 0,
                isBankrupt = false,
                propertiesOwned = emptyList()
            )
        }

        val board = GameDefaults.createDefaultBoard()
        val newState = GameState(
            mode = GameMode.ONLINE_MULTIPLAYER,
            players = players,
            tiles = board,
            currentTurnIndex = 0,
            dice1 = 1,
            dice2 = 1,
            hasRolled = false,
            freeParkingJackpot = 100000,
            activeGlobalEvent = GlobalEventType.NONE,
            globalEventTurnsRemaining = 0,
            turnNumber = 1
        )

        _gameState.value = newState
        _gameLogs.value = emptyList()
        _currentRoute.value = ScreenRoute.GAME
        storage.saveGameState(newState)

        addLog("🌐 بازی آنلاین در «${room.title}» آغاز شد! نوبت نخست: «${players[0].name}»", 0xFF38BDF8, "🎮")
        checkAiTurn()
    }

    // ================= چرخه نوبت و پرتاب تاس =================

    fun rollDice() {
        val state = _gameState.value ?: return
        if (state.hasRolled || _isRollingDice.value || _isMovingPawn.value) return

        val currentPlayer = state.players.getOrNull(state.currentTurnIndex) ?: return
        if (currentPlayer.isBankrupt) {
            endTurn()
            return
        }

        viewModelScope.launch {
            _isRollingDice.value = true
            SoundManager.playDiceRoll()

            // انیمیشن چرخش تاس
            for (i in 0 until 5) {
                _gameState.update { s ->
                    s?.copy(
                        dice1 = (1..6).random(),
                        dice2 = (1..6).random()
                    )
                }
                delay(70)
            }

            val d1 = (1..6).random()
            val d2 = (1..6).random()
            val isDoubles = d1 == d2
            val diceSum = d1 + d2

            _isRollingDice.value = false

            _gameState.update { s ->
                s?.copy(
                    dice1 = d1,
                    dice2 = d2,
                    hasRolled = true,
                    doublesCount = if (isDoubles) (s.doublesCount + 1) else 0
                )
            }

            // آمار تاس
            _generalStats.update { it.copy(diceRolled = it.diceRolled + 1) }

            addLog(
                "«${currentPlayer.name}» تاس انداخت: [$d1] و [$d2] (مجموع: $diceSum)" +
                        if (isDoubles) " — جفت آمد! 🎲" else "",
                currentPlayer.colorHex,
                "🎲"
            )

            // بررسی ۳ بار جفت آمدن متوالی -> انتقال مستقیم به زندان
            if (state.doublesCount + (if (isDoubles) 1 else 0) >= 3) {
                addLog("«${currentPlayer.name}» ۳ بار متوالی تاس جفت آورد و به زندان فرستاده شد!", 0xFFEF4444, "🚨")
                sendPlayerToJail(currentPlayer.id)
                return@launch
            }

            // اگر در زندان است
            if (currentPlayer.inJail) {
                handleJailTurn(currentPlayer, isDoubles, diceSum)
            } else {
                movePlayerSteps(currentPlayer.id, diceSum)
            }
        }
    }

    private fun handleJailTurn(player: Player, isDoubles: Boolean, diceSum: Int) {
        if (isDoubles) {
            addLog("«${player.name}» با آوردن تاس جفت با موفقیت از زندان آزاد شد!", 0xFF10B981, "🔓")
            SoundManager.playChanceCard()
            _gameState.update { s ->
                val updatedPlayers = s!!.players.map { p ->
                    if (p.id == player.id) p.copy(inJail = false, jailTurns = 0) else p
                }
                s.copy(players = updatedPlayers)
            }
            movePlayerSteps(player.id, diceSum)
        } else {
            val nextJailTurns = player.jailTurns + 1
            if (nextJailTurns >= 3) {
                // پرداخت اجباری ۵۰٬۰۰۰ تومان جریمه
                val fine = 50000
                addLog("پس از ۳ نوبت، «${player.name}» با پرداخت ۵۰٬۰۰۰ تومان از زندان آزاد شد.", 0xFFEF4444, "🔓")
                deductCash(player.id, fine)
                _gameState.update { s ->
                    val updatedPlayers = s!!.players.map { p ->
                        if (p.id == player.id) p.copy(inJail = false, jailTurns = 0) else p
                    }
                    s.copy(players = updatedPlayers)
                }
                movePlayerSteps(player.id, diceSum)
            } else {
                addLog("«${player.name}» نتوانست تاس جفت بیاورد و در زندان باقی ماند (نوبت $nextJailTurns از ۳).", 0xFF9E9E9E, "🔒")
                _gameState.update { s ->
                    val updatedPlayers = s!!.players.map { p ->
                        if (p.id == player.id) p.copy(jailTurns = nextJailTurns) else p
                    }
                    s.copy(players = updatedPlayers)
                }
                // پایان نوبت در زندان
                if (player.type == PlayerType.AI) {
                    viewModelScope.launch {
                        delay(_settings.value.aiThinkingDelayMs)
                        endTurn()
                    }
                }
            }
        }
    }

    fun payJailBail(playerId: Int) {
        val state = _gameState.value ?: return
        val player = state.players.find { it.id == playerId } ?: return
        if (!player.inJail) return

        if (player.getOutOfJailCards > 0) {
            _gameState.update { s ->
                val updatedPlayers = s!!.players.map { p ->
                    if (p.id == playerId) p.copy(inJail = false, jailTurns = 0, getOutOfJailCards = p.getOutOfJailCards - 1) else p
                }
                s.copy(players = updatedPlayers)
            }
            addLog("«${player.name}» با استفاده از کارت آزادی از زندان رها شد.", 0xFF10B981, "🎫")
            SoundManager.playChanceCard()
        } else if (player.cash >= 50000) {
            deductCash(playerId, 50000)
            _gameState.update { s ->
                val updatedPlayers = s!!.players.map { p ->
                    if (p.id == playerId) p.copy(inJail = false, jailTurns = 0) else p
                }
                s.copy(players = updatedPlayers, freeParkingJackpot = s.freeParkingJackpot + 50000)
            }
            addLog("«${player.name}» با پرداخت ۵۰٬۰۰۰ تومان وثیقه از زندان آزاد شد.", 0xFF10B981, "🔓")
            SoundManager.playMoneyPaid()
        }
    }

    private fun movePlayerSteps(playerId: Int, steps: Int) {
        viewModelScope.launch {
            _isMovingPawn.value = true
            val state = _gameState.value ?: return@launch
            val player = state.players.find { it.id == playerId } ?: return@launch
            val oldPos = player.position
            val newPos = (oldPos + steps) % 40
            val passedStart = (oldPos + steps) >= 40

            // عبور قدم به قدم
            val delayPerStep = if (_settings.value.fastAnimations) 30L else 70L
            var curr = oldPos
            val stepCount = if (steps > 0) steps else (steps + 40)
            for (i in 0 until stepCount) {
                curr = (curr + 1) % 40
                _gameState.update { s ->
                    val updatedPlayers = s!!.players.map { p ->
                        if (p.id == playerId) p.copy(position = curr) else p
                    }
                    s.copy(players = updatedPlayers)
                }
                delay(delayPerStep)
            }

            _isMovingPawn.value = false

            // عبور از خانه شروع (دریافت ۲۰۰٬۰۰۰ تومان)
            if (passedStart) {
                val bonus = 200000
                addCash(playerId, bonus)
                addLog("«${player.name}» از خانه شروع عبور کرد و ۲۰۰٬۰۰۰ تومان پاداش گرفت!", 0xFF10B981, "🏁")
                SoundManager.playMoneyReceived()
            }

            // بررسی فرود روی خانه جدید
            onLandOnTile(playerId, newPos)
        }
    }

    private fun onLandOnTile(playerId: Int, tilePos: Int) {
        val state = _gameState.value ?: return
        val player = state.players.find { it.id == playerId } ?: return
        val tile = state.tiles[tilePos]

        addLog("«${player.name}» وارد خانه «${tile.name}» شد.", player.colorHex, "📍")

        when (tile.type) {
            TileType.PROPERTY, TileType.RAILROAD, TileType.UTILITY -> {
                if (tile.ownerId == null) {
                    // ملک بدون مالک
                    val effectiveCost = (tile.cost * state.activeGlobalEvent.buyDiscountMultiplier).toInt()
                    if (player.type == PlayerType.HUMAN) {
                        if (player.cash >= effectiveCost) {
                            _buyPropertyPrompt.value = tile
                        } else {
                            addLog("موجودی شما برای خرید «${tile.name}» کافی نیست. ملک وارد حراج شد.", 0xFFFF9800, "⚖️")
                            startAuction(tile.id)
                        }
                    } else {
                        // تصمیم هوش مصنوعی
                        val willBuy = AiDecisionEngine.shouldBuyProperty(player, tile, state.tiles, state.activeGlobalEvent)
                        if (willBuy && player.cash >= effectiveCost) {
                            buyProperty(player.id, tile.id)
                        } else {
                            addLog("«${player.name}» از خرید «${tile.name}» صرف‌نظر کرد. ملک وارد حراج می‌شود!", 0xFFFF9800, "⚖️")
                            startAuction(tile.id)
                        }
                    }
                } else if (tile.ownerId != playerId) {
                    // پرداخت اجاره به مالک
                    val owner = state.players.find { it.id == tile.ownerId }
                    if (owner != null && !owner.inJail && !tile.isMortgaged) {
                        val isMonopoly = checkIsMonopoly(tile.group, owner.id)
                        val diceSum = state.dice1 + state.dice2
                        val rent = tile.calculateRent(isMonopoly, diceSum, state.activeGlobalEvent.rentMultiplier)
                        addLog("«${player.name}» باید مبلغ ${rent} تومان اجاره به «${owner.name}» بپردازد.", 0xFFEF4444, "💸")

                        transferCash(fromPlayerId = playerId, toPlayerId = owner.id, amount = rent)
                        SoundManager.playMoneyPaid()

                        // آمار اجاره
                        _generalStats.update { it.copy(totalRentPaid = it.totalRentPaid + rent) }
                    }
                    checkAiAutoEndTurn(player)
                } else {
                    // ملک خود بازیکن
                    checkAiAutoEndTurn(player)
                }
            }

            TileType.START -> {
                checkAiAutoEndTurn(player)
            }

            TileType.TAX -> {
                val tax = tile.cost
                addLog("«${player.name}» مالیات به مبلغ ${tax} تومان پرداخت کرد و به صندوق پارک آزاد افزوده شد.", 0xFFEF4444, "🏛️")
                deductCash(playerId, tax)
                _gameState.update { s -> s?.copy(freeParkingJackpot = s.freeParkingJackpot + tax) }
                SoundManager.playMoneyPaid()
                checkAiAutoEndTurn(player)
            }

            TileType.FREE_PARKING -> {
                val jackpot = state.freeParkingJackpot
                if (jackpot > 0) {
                    addLog("«${player.name}» در پارک آزاد متوقف شد و کل صندوق جوایز به ارزش ${jackpot} تومان را برد! 🎉", 0xFFFFD700, "🎁")
                    addCash(playerId, jackpot)
                    _gameState.update { s -> s?.copy(freeParkingJackpot = 50000) }
                    SoundManager.playVictory()
                    unlockAchievement("lucky_roller")
                }
                checkAiAutoEndTurn(player)
            }

            TileType.GO_TO_JAIL -> {
                addLog("«${player.name}» به دستور پلیس راهی بازداشتگاه شد!", 0xFFEF4444, "🚨")
                sendPlayerToJail(playerId)
            }

            TileType.JAIL -> {
                addLog("«${player.name}» فقط در حال ملاقات و بازدید از زندان است.", 0xFF9E9E9E, "👀")
                checkAiAutoEndTurn(player)
            }

            TileType.CHANCE -> {
                val chanceCards = allCards.filter { it.isChance }
                val drawnCard = chanceCards.random()
                _activeCard.value = drawnCard
                SoundManager.playChanceCard()
                addLog("کارت شانس کشیده شد: «${drawnCard.title}»", 0xFFFFD700, "✨")
            }

            TileType.COMMUNITY_CHEST -> {
                val chestCards = allCards.filter { !it.isChance }
                val drawnCard = chestCards.random()
                _activeCard.value = drawnCard
                SoundManager.playChanceCard()
                addLog("کارت اتفاق کشیده شد: «${drawnCard.title}»", 0xFF00BCD4, "📜")
            }
        }
    }

    // ================= اجرای کارت =================

    fun executeActiveCard() {
        val card = _activeCard.value ?: return
        val state = _gameState.value ?: return
        val currentPlayer = state.players[state.currentTurnIndex]
        _activeCard.value = null

        when (val action = card.action) {
            is CardAction.AddCash -> {
                addCash(currentPlayer.id, action.amount)
                SoundManager.playMoneyReceived()
            }
            is CardAction.DeductCash -> {
                deductCash(currentPlayer.id, action.amount)
                SoundManager.playMoneyPaid()
            }
            is CardAction.MoveToTile -> {
                val passedStart = action.canCollectStartBonus && action.targetTileId < currentPlayer.position
                if (passedStart) {
                    addCash(currentPlayer.id, 200000)
                    addLog("«${currentPlayer.name}» از شروع عبور کرد و ۲۰۰٬۰۰۰ تومان گرفت.", 0xFF10B981, "🏁")
                }
                _gameState.update { s ->
                    val updated = s!!.players.map { p ->
                        if (p.id == currentPlayer.id) p.copy(position = action.targetTileId) else p
                    }
                    s.copy(players = updated)
                }
                onLandOnTile(currentPlayer.id, action.targetTileId)
                return
            }
            is CardAction.MoveRelative -> {
                movePlayerSteps(currentPlayer.id, action.steps)
                return
            }
            is CardAction.GoToJail -> {
                sendPlayerToJail(currentPlayer.id)
                return
            }
            is CardAction.GetOutOfJailFree -> {
                _gameState.update { s ->
                    val updated = s!!.players.map { p ->
                        if (p.id == currentPlayer.id) p.copy(getOutOfJailCards = p.getOutOfJailCards + 1) else p
                    }
                    s.copy(players = updated)
                }
            }
            is CardAction.PayPerBuilding -> {
                val playerOwned = state.tiles.filter { it.ownerId == currentPlayer.id }
                var totalCost = 0
                for (tile in playerOwned) {
                    totalCost += tile.houses * action.perHouse
                    if (tile.hasHotel) totalCost += action.perHotel
                }
                if (totalCost > 0) {
                    deductCash(currentPlayer.id, totalCost)
                    addLog("«${currentPlayer.name}» بابت بازسازی ساختمان‌ها مبلغ ${totalCost} تومان پرداخت کرد.", 0xFFEF4444, "🔨")
                }
            }
            is CardAction.CollectFromPlayers -> {
                for (p in state.players) {
                    if (p.id != currentPlayer.id && !p.isBankrupt) {
                        transferCash(p.id, currentPlayer.id, action.amountPerPlayer)
                    }
                }
                SoundManager.playMoneyReceived()
            }
            is CardAction.PayToPlayers -> {
                for (p in state.players) {
                    if (p.id != currentPlayer.id && !p.isBankrupt) {
                        transferCash(currentPlayer.id, p.id, action.amountPerPlayer)
                    }
                }
                SoundManager.playMoneyPaid()
            }
            is CardAction.TriggerGlobalEvent -> {
                _gameState.update { s ->
                    s?.copy(
                        activeGlobalEvent = action.event,
                        globalEventTurnsRemaining = 3
                    )
                }
                addLog("رویداد جدید جهانی فعال شد: «${action.event.title}» - ${action.event.description}", 0xFFFF9800, "🌍")
            }
        }

        checkAiAutoEndTurn(currentPlayer)
    }

    // ================= خرید و مدیریت املاک =================

    fun buyProperty(playerId: Int, tileId: Int) {
        val state = _gameState.value ?: return
        val player = state.players.find { it.id == playerId } ?: return
        val tile = state.tiles.getOrNull(tileId) ?: return
        val effectiveCost = (tile.cost * state.activeGlobalEvent.buyDiscountMultiplier).toInt()

        if (player.cash >= effectiveCost && tile.ownerId == null) {
            deductCash(playerId, effectiveCost)
            _gameState.update { s ->
                val updatedTiles = s!!.tiles.map { t ->
                    if (t.id == tileId) t.copy(ownerId = playerId) else t
                }
                val updatedPlayers = s.players.map { p ->
                    if (p.id == playerId) p.copy(propertiesOwned = p.propertiesOwned + tileId) else p
                }
                s.copy(tiles = updatedTiles, players = updatedPlayers)
            }
            _buyPropertyPrompt.value = null
            SoundManager.playBuyProperty()
            addLog("«${player.name}» سند مالکیت «${tile.name}» را به ارزش ${effectiveCost} تومان خرید.", player.colorHex, "📜")

            // دستاورد و آمار
            _generalStats.update { it.copy(propertiesPurchased = it.propertiesPurchased + 1) }
            unlockAchievement("first_property")
            if (checkIsMonopoly(tile.group, playerId)) {
                unlockAchievement("monopoly_king")
                addLog("تبریک! «${player.name}» تمام املاک گروه «${tile.group.groupName}» را تصاحب کرد و مونوپولی ساخت!", 0xFFFFD700, "👑")
            }
        }
        checkAiAutoEndTurn(player)
    }

    fun declineBuyProperty() {
        val tile = _buyPropertyPrompt.value ?: return
        _buyPropertyPrompt.value = null
        addLog("ملک «${tile.name}» توسط بازیکن رد شد و وارد حراج عمومی گردید.", 0xFFFF9800, "⚖️")
        startAuction(tile.id)
    }

    // ================= حراج املاک =================

    fun startAuction(propertyId: Int) {
        val state = _gameState.value ?: return
        val tile = state.tiles[propertyId]
        val activePlayers = state.players.filter { !it.isBankrupt && it.cash >= 10000 }
        if (activePlayers.isEmpty()) return

        val startingBid = (tile.cost * 0.5f).toInt().coerceAtLeast(10000)
        val initialAuction = AuctionState(
            propertyId = propertyId,
            currentBid = startingBid,
            highestBidderId = null,
            participatingPlayerIds = activePlayers.map { it.id },
            currentTurnPlayerId = activePlayers.first().id
        )
        _auctionState.value = initialAuction
        addLog("حراج ملک «${tile.name}» با قیمت پایه ${startingBid} تومان آغاز شد!", 0xFFFF9800, "🔨")

        triggerNextAuctionStep()
    }

    fun placeAuctionBid(playerId: Int, bidAmount: Int) {
        val currentAuction = _auctionState.value ?: return
        val state = _gameState.value ?: return
        val player = state.players.find { it.id == playerId } ?: return

        if (bidAmount > currentAuction.currentBid && player.cash >= bidAmount) {
            _auctionState.value = currentAuction.copy(
                currentBid = bidAmount,
                highestBidderId = playerId,
                consecutivePassCount = 0
            )
            addLog("«${player.name}» پیشنهاد قیمت ${bidAmount} تومان داد!", player.colorHex, "💰")
            SoundManager.playBuyProperty()
            advanceAuctionTurn()
        }
    }

    fun passAuction(playerId: Int) {
        val currentAuction = _auctionState.value ?: return
        val nextPassCount = currentAuction.consecutivePassCount + 1
        val state = _gameState.value ?: return
        val player = state.players.find { it.id == playerId }
        addLog("«${player?.name ?: ""}» از ادامه رقابت در این نوبت حراج انصراف داد.", 0xFF9E9E9E, "⏹️")

        if (nextPassCount >= currentAuction.participatingPlayerIds.size) {
            finishAuction()
        } else {
            _auctionState.value = currentAuction.copy(consecutivePassCount = nextPassCount)
            advanceAuctionTurn()
        }
    }

    private fun advanceAuctionTurn() {
        val currentAuction = _auctionState.value ?: return
        val participants = currentAuction.participatingPlayerIds
        val currIdx = participants.indexOf(currentAuction.currentTurnPlayerId)
        val nextIdx = (currIdx + 1) % participants.size
        val nextPlayerId = participants[nextIdx]

        _auctionState.value = currentAuction.copy(currentTurnPlayerId = nextPlayerId)
        triggerNextAuctionStep()
    }

    private fun triggerNextAuctionStep() {
        val currentAuction = _auctionState.value ?: return
        val state = _gameState.value ?: return
        val currentBidder = state.players.find { it.id == currentAuction.currentTurnPlayerId } ?: return
        val tile = state.tiles[currentAuction.propertyId]

        if (currentBidder.type == PlayerType.AI) {
            viewModelScope.launch {
                delay(600)
                val aiBid = AiDecisionEngine.decideAuctionBid(currentBidder, tile, currentAuction.currentBid, state.tiles)
                if (aiBid != null && aiBid > currentAuction.currentBid) {
                    placeAuctionBid(currentBidder.id, aiBid)
                } else {
                    passAuction(currentBidder.id)
                }
            }
        }
    }

    private fun finishAuction() {
        val currentAuction = _auctionState.value ?: return
        val state = _gameState.value ?: return
        val winnerId = currentAuction.highestBidderId
        val tile = state.tiles[currentAuction.propertyId]

        if (winnerId != null) {
            val winner = state.players.find { it.id == winnerId }!!
            deductCash(winnerId, currentAuction.currentBid)
            _gameState.update { s ->
                val updatedTiles = s!!.tiles.map { t ->
                    if (t.id == tile.id) t.copy(ownerId = winnerId) else t
                }
                val updatedPlayers = s.players.map { p ->
                    if (p.id == winnerId) p.copy(propertiesOwned = p.propertiesOwned + tile.id) else p
                }
                s.copy(tiles = updatedTiles, players = updatedPlayers)
            }
            addLog("حراج پایان یافت! «${winner.name}» برنده ملک «${tile.name}» به مبلغ ${currentAuction.currentBid} تومان شد.", 0xFF10B981, "🏆")
            SoundManager.playVictory()
            unlockAchievement("deal_maker")
        } else {
            addLog("هیچ پیشنهادی برای خرید «${tile.name}» ثبت نشد و ملک در اختیار بانک ماند.", 0xFF9E9E9E, "🏛️")
        }
        _auctionState.value = null
        checkAiAutoEndTurn(state.players[state.currentTurnIndex])
    }

    // ================= سیستم ساخت خانه و هتل =================

    fun openBuildingDialog() {
        _isBuildingDialogOpen.value = true
    }

    fun closeBuildingDialog() {
        _isBuildingDialogOpen.value = false
    }

    fun buildHouse(tileId: Int) {
        val state = _gameState.value ?: return
        val tile = state.tiles[tileId]
        val owner = state.players.find { it.id == tile.ownerId } ?: return

        if (owner.cash >= tile.houseCost && tile.canBuildHouse) {
            deductCash(owner.id, tile.houseCost)
            _gameState.update { s ->
                val updatedTiles = s!!.tiles.map { t ->
                    if (t.id == tileId) t.copy(houses = t.houses + 1) else t
                }
                s.copy(tiles = updatedTiles)
            }
            addLog("«${owner.name}» یک خانه جدید در «${tile.name}» ساخت (مجموع خانه: ${tile.houses + 1})", owner.colorHex, "🏡")
            SoundManager.playBuyProperty()
            _generalStats.update { it.copy(housesBuilt = it.housesBuilt + 1) }
        }
    }

    fun buildHotel(tileId: Int) {
        val state = _gameState.value ?: return
        val tile = state.tiles[tileId]
        val owner = state.players.find { it.id == tile.ownerId } ?: return

        if (owner.cash >= tile.houseCost && tile.canBuildHotel) {
            deductCash(owner.id, tile.houseCost)
            _gameState.update { s ->
                val updatedTiles = s!!.tiles.map { t ->
                    if (t.id == tileId) t.copy(houses = 0, hasHotel = true) else t
                }
                s.copy(tiles = updatedTiles)
            }
            addLog("«${owner.name}» یک هتل لوکس در «${tile.name}» احداث کرد! 🏨", 0xFFFFD700, "🏨")
            SoundManager.playVictory()
            _generalStats.update { it.copy(hotelsBuilt = it.hotelsBuilt + 1) }
            unlockAchievement("hotel_magnate")
        }
    }

    fun buildSkyscraper(tileId: Int) {
        val state = _gameState.value ?: return
        val tile = state.tiles[tileId]
        val owner = state.players.find { it.id == tile.ownerId } ?: return

        val skyscraperCost = tile.houseCost * 2
        if (owner.cash >= skyscraperCost && tile.canBuildSkyscraper) {
            deductCash(owner.id, skyscraperCost)
            _gameState.update { s ->
                val updatedTiles = s!!.tiles.map { t ->
                    if (t.id == tileId) t.copy(hasSkyscraper = true) else t
                }
                s.copy(tiles = updatedTiles)
            }
            addLog("شاهکار معماری! «${owner.name}» آسمان‌خراش مجلل در «${tile.name}» بنا کرد! 🌆", 0xFFFFD700, "🌆")
            SoundManager.playVictory()
        }
    }

    // ================= سیستم معامله =================

    fun openTradeDialog() {
        _isTradeDialogOpen.value = true
    }

    fun closeTradeDialog() {
        _isTradeDialogOpen.value = false
        _activeTradeOffer.value = null
    }

    fun proposeTrade(offer: TradeOffer) {
        val state = _gameState.value ?: return
        val targetPlayer = state.players.find { it.id == offer.toPlayerId } ?: return
        val fromPlayer = state.players.find { it.id == offer.fromPlayerId } ?: return

        _activeTradeOffer.value = offer

        if (targetPlayer.type == PlayerType.AI) {
            viewModelScope.launch {
                delay(800)
                val accepted = AiDecisionEngine.evaluateTradeOffer(targetPlayer, offer, state.tiles)
                if (accepted) {
                    executeTrade(offer)
                    addLog("«${targetPlayer.name}» پیشنهاد معامله «${fromPlayer.name}» را پذیرفت!", 0xFF10B981, "🤝")
                } else {
                    addLog("«${targetPlayer.name}» پیشنهاد معامله «${fromPlayer.name}» را رد کرد.", 0xFFEF4444, "❌")
                }
                closeTradeDialog()
            }
        }
    }

    fun acceptTrade() {
        val offer = _activeTradeOffer.value ?: return
        executeTrade(offer)
        closeTradeDialog()
    }

    fun rejectTrade() {
        _activeTradeOffer.value = null
        closeTradeDialog()
    }

    private fun executeTrade(offer: TradeOffer) {
        val state = _gameState.value ?: return
        val p1 = state.players.find { it.id == offer.fromPlayerId } ?: return
        val p2 = state.players.find { it.id == offer.toPlayerId } ?: return

        // انتقال پول
        if (offer.offeredCash > 0) transferCash(p1.id, p2.id, offer.offeredCash)
        if (offer.requestedCash > 0) transferCash(p2.id, p1.id, offer.requestedCash)

        // انتقال املاک
        _gameState.update { s ->
            val updatedTiles = s!!.tiles.map { t ->
                when (t.id) {
                    in offer.offeredProperties -> t.copy(ownerId = p2.id)
                    in offer.requestedProperties -> t.copy(ownerId = p1.id)
                    else -> t
                }
            }
            val updatedPlayers = s.players.map { p ->
                when (p.id) {
                    p1.id -> p.copy(
                        propertiesOwned = (p.propertiesOwned - offer.offeredProperties.toSet()) + offer.requestedProperties
                    )
                    p2.id -> p.copy(
                        propertiesOwned = (p.propertiesOwned - offer.requestedProperties.toSet()) + offer.offeredProperties
                    )
                    else -> p
                }
            }
            s.copy(tiles = updatedTiles, players = updatedPlayers)
        }

        addLog("معامله رسمی با موفقیت بین «${p1.name}» و «${p2.name}» منعقد شد.", 0xFF10B981, "🤝")
        SoundManager.playChanceCard()
        _generalStats.update { it.copy(tradesCompleted = it.tradesCompleted + 1) }
        unlockAchievement("master_trader")
    }

    // ================= پایان نوبت و هوش مصنوعی =================

    fun endTurn() {
        val state = _gameState.value ?: return
        if (_isRollingDice.value || _isMovingPawn.value || _auctionState.value != null || _buyPropertyPrompt.value != null) return

        // بررسی رویداد جهانی با انقضا
        val newTurnsRemaining = if (state.globalEventTurnsRemaining > 0) state.globalEventTurnsRemaining - 1 else 0
        val newGlobalEvent = if (newTurnsRemaining == 0) GlobalEventType.NONE else state.activeGlobalEvent

        // پیدا کردن بازیکن زنده بعدی
        val activePlayers = state.players.filter { !it.isBankrupt }
        if (activePlayers.size <= 1) {
            handleGameOver(activePlayers.firstOrNull()?.id)
            return
        }

        val currIndex = state.currentTurnIndex
        var nextIndex = (currIndex + 1) % state.players.size
        while (state.players[nextIndex].isBankrupt) {
            nextIndex = (nextIndex + 1) % state.players.size
        }

        _gameState.update { s ->
            s?.copy(
                currentTurnIndex = nextIndex,
                hasRolled = false,
                doublesCount = 0,
                activeGlobalEvent = newGlobalEvent,
                globalEventTurnsRemaining = newTurnsRemaining,
                turnNumber = s.turnNumber + 1
            )
        }

        storage.saveGameState(_gameState.value!!)

        val nextPlayer = state.players[nextIndex]
        addLog("نوبت «${nextPlayer.name}» آغاز شد.", nextPlayer.colorHex, "⏱️")

        checkAiTurn()
    }

    private fun checkAiTurn() {
        val state = _gameState.value ?: return
        val currPlayer = state.players[state.currentTurnIndex]

        if (currPlayer.type == PlayerType.AI && !currPlayer.isBankrupt && !state.isGameOver) {
            viewModelScope.launch {
                delay(_settings.value.aiThinkingDelayMs)

                // هوش مصنوعی در زندان
                if (currPlayer.inJail) {
                    if (AiDecisionEngine.shouldPayToLeaveJail(currPlayer)) {
                        payJailBail(currPlayer.id)
                        delay(400)
                    }
                }

                // ارتقا املاک قبل یا بعد از حرکت
                val tileToUpgrade = AiDecisionEngine.decidePropertyToUpgrade(currPlayer, state.tiles)
                if (tileToUpgrade != null) {
                    if (tileToUpgrade.canBuildHouse) {
                        buildHouse(tileToUpgrade.id)
                    } else if (tileToUpgrade.canBuildHotel) {
                        buildHotel(tileToUpgrade.id)
                    }
                    delay(300)
                }

                // پرتاب تاس
                rollDice()
            }
        }
    }

    private fun checkAiAutoEndTurn(player: Player) {
        if (player.type == PlayerType.AI) {
            viewModelScope.launch {
                delay(_settings.value.aiThinkingDelayMs)
                // اگر در حراج یا دیالوگ نیست، پایان نوبت
                if (_auctionState.value == null && _buyPropertyPrompt.value == null && _activeCard.value == null) {
                    endTurn()
                }
            }
        }
    }

    // ================= جابجایی مالی و ورشکستگی =================

    fun addCash(playerId: Int, amount: Int) {
        _gameState.update { s ->
            val updated = s!!.players.map { p ->
                if (p.id == playerId) p.copy(cash = p.cash + amount) else p
            }
            s.copy(players = updated)
        }
        val p = _gameState.value?.players?.find { it.id == playerId }
        if (p != null) {
            val netWorth = p.calculateNetWorth(_gameState.value!!.tiles)
            if (netWorth > 3000000) unlockAchievement("millionaire")
            if (netWorth > 10000000) unlockAchievement("billionaire")
            _generalStats.update {
                it.copy(
                    totalCashEarned = it.totalCashEarned + amount,
                    maxWealthAchieved = maxOf(it.maxWealthAchieved, netWorth.toLong())
                )
            }
        }
    }

    fun deductCash(playerId: Int, amount: Int) {
        _gameState.update { s ->
            val updated = s!!.players.map { p ->
                if (p.id == playerId) p.copy(cash = p.cash - amount) else p
            }
            s.copy(players = updated)
        }
        checkBankruptcy(playerId)
    }

    fun transferCash(fromPlayerId: Int, toPlayerId: Int, amount: Int) {
        deductCash(fromPlayerId, amount)
        addCash(toPlayerId, amount)
    }

    private fun checkBankruptcy(playerId: Int) {
        val state = _gameState.value ?: return
        val player = state.players.find { it.id == playerId } ?: return

        if (player.cash < 0) {
            // اگر ارزش دارایی‌های نقدشدنی نیز کمتر از بدهی باشد ورشکسته می‌شود
            val netWorth = player.calculateNetWorth(state.tiles)
            if (netWorth < 0 || (player.propertiesOwned.isEmpty() && player.cash < 0)) {
                declareBankruptcy(playerId)
            }
        }
    }

    fun declareBankruptcy(playerId: Int) {
        val state = _gameState.value ?: return
        val player = state.players.find { it.id == playerId } ?: return

        _gameState.update { s ->
            // بازگرداندن املاک به بانک
            val updatedTiles = s!!.tiles.map { t ->
                if (t.ownerId == playerId) t.copy(ownerId = null, houses = 0, hasHotel = false, hasSkyscraper = false) else t
            }
            val updatedPlayers = s.players.map { p ->
                if (p.id == playerId) p.copy(isBankrupt = true, cash = 0, propertiesOwned = emptyList()) else p
            }
            s.copy(tiles = updatedTiles, players = updatedPlayers)
        }

        addLog("«${player.name}» ورشکسته شد و از بازی شهر ثروت خارج گردید!", 0xFFEF4444, "💥")
        SoundManager.playBankrupt()

        val remainingActive = _gameState.value!!.players.filter { !it.isBankrupt }
        if (remainingActive.size <= 1) {
            handleGameOver(remainingActive.firstOrNull()?.id)
        } else if (_gameState.value!!.currentTurnIndex == playerId) {
            endTurn()
        }
    }

    private fun handleGameOver(winnerId: Int?) {
        _gameState.update { s ->
            s?.copy(isGameOver = true, winnerId = winnerId)
        }
        val winner = _gameState.value?.players?.find { it.id == winnerId }
        if (winner != null) {
            addLog("پیروزی بزرگ! «${winner.name}» قهرمان شهر ثروت شد! 🏆", 0xFFFFD700, "👑")
            SoundManager.playVictory()
            unlockAchievement("champion")
            _generalStats.update {
                it.copy(
                    gamesPlayed = it.gamesPlayed + 1,
                    wins = if (winner.type == PlayerType.HUMAN) it.wins + 1 else it.wins,
                    losses = if (winner.type == PlayerType.AI) it.losses + 1 else it.losses
                )
            }
        }
        storage.clearSavedGame()
    }

    private fun sendPlayerToJail(playerId: Int) {
        _gameState.update { s ->
            val updated = s!!.players.map { p ->
                if (p.id == playerId) p.copy(position = 10, inJail = true, jailTurns = 0) else p
            }
            s.copy(players = updated, doublesCount = 0)
        }
        SoundManager.playJail()
        checkAiAutoEndTurn(_gameState.value!!.players.find { it.id == playerId }!!)
    }

    private fun checkIsMonopoly(group: PropertyGroup, playerId: Int): Boolean {
        if (group == PropertyGroup.SPECIAL || group == PropertyGroup.RAILROAD || group == PropertyGroup.UTILITY) return false
        val state = _gameState.value ?: return false
        val groupTiles = state.tiles.filter { it.group == group }
        return groupTiles.isNotEmpty() && groupTiles.all { it.ownerId == playerId }
    }

    // ================= تنظیمات و ابزارها =================

    fun toggleSound() {
        val newSound = !_settings.value.soundEnabled
        _settings.update { it.copy(soundEnabled = newSound) }
        SoundManager.isSoundEnabled = newSound
        storage.saveSettings(_settings.value)
    }

    fun updateSettings(newSettings: SettingsState) {
        _settings.value = newSettings
        SoundManager.isSoundEnabled = newSettings.soundEnabled
        storage.saveSettings(newSettings)
    }

    fun setTileInspection(tile: Tile?) {
        _inspectingTile.value = tile
    }

    private fun addLog(text: String, colorHex: Long? = null, icon: String = "📢") {
        val entry = GameLogEntry(text = text, highlightColorHex = colorHex, icon = icon)
        _gameLogs.update { (listOf(entry) + it).take(50) }
    }

    private fun unlockAchievement(id: String) {
        _achievements.update { list ->
            list.map { ach ->
                if (ach.id == id && !ach.isUnlocked) {
                    ach.copy(isUnlocked = true, progress = ach.maxProgress)
                } else ach
            }
        }
        storage.saveAchievements(_achievements.value)
    }

    private fun createInitialSetupPlayers(count: Int): List<Player> {
        val list = mutableListOf<Player>()
        for (i in 0 until count) {
            val name = GameDefaults.DEFAULT_NAMES.getOrElse(i) { "بازیکن ${i + 1}" }
            val avatar = GameDefaults.AVATARS.getOrElse(i) { "🌟" }
            val color = GameDefaults.PLAYER_COLORS.getOrElse(i) { 0xFF1E88E5 }
            val personality = AiPersonality.entries[i % AiPersonality.entries.size]
            list.add(
                Player(
                    id = i,
                    name = name,
                    avatarEmoji = avatar,
                    colorHex = color,
                    type = if (i == 0) PlayerType.HUMAN else PlayerType.AI,
                    aiPersonality = personality,
                    cash = 1500000
                )
            )
        }
        return list
    }
}
