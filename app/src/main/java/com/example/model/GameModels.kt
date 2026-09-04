package com.example.model

/**
 * مدل هر خانه از صفحه ۴۰ تایی بازی
 */
data class Tile(
    val id: Int,
    val name: String,
    val type: TileType,
    val group: PropertyGroup = PropertyGroup.SPECIAL,
    val cost: Int = 0,
    val baseRent: Int = 0,
    val houseRents: List<Int> = emptyList(), // rents for 1, 2, 3, 4 houses
    val hotelRent: Int = 0,
    val skyscraperRent: Int = 0,
    val houseCost: Int = 0,
    val ownerId: Int? = null,
    val houses: Int = 0, // 0 to 4
    val hasHotel: Boolean = false,
    val hasSkyscraper: Boolean = false,
    val isMortgaged: Boolean = false
) {
    /**
     * محاسبه مبلغ اجاره بر اساس سطح ارتقا و وضعیت مونوپولی
     */
    fun calculateRent(isMonopoly: Boolean, diceRollSum: Int = 7, globalMultiplier: Float = 1.0f): Int {
        if (isMortgaged) return 0
        val rawRent = when (type) {
            TileType.PROPERTY -> {
                when {
                    hasSkyscraper -> skyscraperRent
                    hasHotel -> hotelRent
                    houses > 0 && houses <= houseRents.size -> houseRents[houses - 1]
                    isMonopoly -> baseRent * 2
                    else -> baseRent
                }
            }
            TileType.RAILROAD -> {
                // Rent increases based on number of railroads owned (handled externally or base)
                baseRent
            }
            TileType.UTILITY -> {
                diceRollSum * baseRent
            }
            else -> 0
        }
        return (rawRent * globalMultiplier).toInt()
    }

    val canBuildHouse: Boolean
        get() = type == TileType.PROPERTY && ownerId != null && !hasHotel && !isMortgaged && houses < 4

    val canBuildHotel: Boolean
        get() = type == TileType.PROPERTY && ownerId != null && houses == 4 && !hasHotel && !isMortgaged

    val canBuildSkyscraper: Boolean
        get() = type == TileType.PROPERTY && ownerId != null && hasHotel && !hasSkyscraper && !isMortgaged
}

/**
 * آمار عملکرد تک‌تک بازیکنان در طول یک دست بازی
 */
data class PlayerGameStats(
    val rentCollected: Long = 0L,
    val rentPaid: Long = 0L,
    val propertiesPurchased: Int = 0,
    val housesBuilt: Int = 0,
    val hotelsBuilt: Int = 0,
    val tradesCompleted: Int = 0,
    val timesInJail: Int = 0
)

/**
 * مدل بازیکن
 */
data class Player(
    val id: Int,
    val name: String,
    val avatarEmoji: String,
    val colorHex: Long,
    val type: PlayerType = PlayerType.HUMAN,
    val aiPersonality: AiPersonality = AiPersonality.CONSERVATIVE,
    val cash: Int = 1500000,
    val position: Int = 0,
    val inJail: Boolean = false,
    val jailTurns: Int = 0,
    val getOutOfJailCards: Int = 0,
    val isBankrupt: Boolean = false,
    val propertiesOwned: List<Int> = emptyList(),
    val stats: PlayerGameStats = PlayerGameStats()
) {
    fun calculateNetWorth(allTiles: List<Tile>): Int {
        var worth = cash
        for (tileId in propertiesOwned) {
            val tile = allTiles.getOrNull(tileId) ?: continue
            worth += tile.cost
            worth += tile.houses * tile.houseCost
            if (tile.hasHotel) worth += tile.houseCost
            if (tile.hasSkyscraper) worth += tile.houseCost * 2
        }
        return worth
    }
}

/**
 * عملیات و اثرات کارت شانس و اتفاق
 */
sealed class CardAction {
    data class AddCash(val amount: Int) : CardAction()
    data class DeductCash(val amount: Int) : CardAction()
    data class MoveToTile(val targetTileId: Int, val canCollectStartBonus: Boolean = true) : CardAction()
    data class MoveRelative(val steps: Int) : CardAction()
    object GoToJail : CardAction()
    object GetOutOfJailFree : CardAction()
    data class PayPerBuilding(val perHouse: Int, val perHotel: Int) : CardAction()
    data class CollectFromPlayers(val amountPerPlayer: Int) : CardAction()
    data class PayToPlayers(val amountPerPlayer: Int) : CardAction()
    data class TriggerGlobalEvent(val event: GlobalEventType) : CardAction()
}

/**
 * مدل کارت بازی
 */
data class GameCard(
    val id: Int,
    val isChance: Boolean, // true = کارت شانس, false = کارت اتفاق
    val title: String,
    val description: String,
    val iconName: String,
    val action: CardAction
)

/**
 * پیشنهاد معامله بین دو بازیکن
 */
data class TradeOffer(
    val fromPlayerId: Int,
    val toPlayerId: Int,
    val offeredCash: Int = 0,
    val offeredProperties: List<Int> = emptyList(),
    val requestedCash: Int = 0,
    val requestedProperties: List<Int> = emptyList(),
    val isAccepted: Boolean = false,
    val isRejected: Boolean = false
)

/**
 * وضعیت حراج ملک
 */
data class AuctionState(
    val propertyId: Int,
    val currentBid: Int,
    val highestBidderId: Int?,
    val participatingPlayerIds: List<Int>,
    val currentTurnPlayerId: Int,
    val consecutivePassCount: Int = 0,
    val isFinished: Boolean = false
)

/**
 * گزارش رویدادهای بازی برای نمایش در لاگ
 */
data class GameLogEntry(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val highlightColorHex: Long? = null,
    val icon: String = "📢"
)

/**
 * دستاوردهای بازی شهر ثروت
 */
data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean = false,
    val progress: Int = 0,
    val maxProgress: Int = 1
)

/**
 * آمار کلی ثبت شده در دستگاه
 */
data class GeneralStats(
    val gamesPlayed: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val totalCashEarned: Long = 0L,
    val totalRentPaid: Long = 0L,
    val propertiesPurchased: Int = 0,
    val housesBuilt: Int = 0,
    val hotelsBuilt: Int = 0,
    val tradesCompleted: Int = 0,
    val diceRolled: Int = 0,
    val maxWealthAchieved: Long = 1500000L
)

/**
 * تنظیمات کاربر
 */
data class SettingsState(
    val isDarkTheme: Boolean = true,
    val soundEnabled: Boolean = true,
    val fastAnimations: Boolean = false,
    val aiThinkingDelayMs: Long = 800L
)

/**
 * کل وضعیت یک بازی برای ذخیره و بارگذاری خودکار
 */
data class GameState(
    val id: String = "game_${System.currentTimeMillis()}",
    val mode: GameMode = GameMode.CLASSIC,
    val players: List<Player> = emptyList(),
    val tiles: List<Tile> = emptyList(),
    val currentTurnIndex: Int = 0,
    val dice1: Int = 1,
    val dice2: Int = 1,
    val hasRolled: Boolean = false,
    val doublesCount: Int = 0,
    val freeParkingJackpot: Int = 100000,
    val activeGlobalEvent: GlobalEventType = GlobalEventType.NONE,
    val globalEventTurnsRemaining: Int = 0,
    val isGameOver: Boolean = false,
    val winnerId: Int? = null,
    val turnNumber: Int = 1
)
