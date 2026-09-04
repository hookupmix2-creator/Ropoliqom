package com.example.ai

import com.example.model.*

object AiDecisionEngine {

    /**
     * تصمیم‌گیری برای خرید ملکی که روی آن متوقف شده
     */
    fun shouldBuyProperty(
        player: Player,
        tile: Tile,
        allTiles: List<Tile>,
        globalEvent: GlobalEventType
    ): Boolean {
        if (tile.cost <= 0 || player.cash < tile.cost) return false

        val personality = player.aiPersonality
        // حداقل ذخیره نقدینگی ضروری پس از خرید
        val safetyBuffer = when (personality) {
            AiPersonality.AGGRESSIVE -> 50000
            AiPersonality.RISK_TAKER -> 80000
            AiPersonality.PRO_TRADER -> 150000
            AiPersonality.ECONOMIC_GENIUS -> 200000
            AiPersonality.CONSERVATIVE -> 350000
        }

        val effectiveCost = (tile.cost * globalEvent.buyDiscountMultiplier).toInt()
        val remainingCash = player.cash - effectiveCost
        if (remainingCash < safetyBuffer) {
            // اگر با خرید این ملک، یک مونوپولی رنگی کامل می‌شود، نابغه اقتصادی یا معامله‌گر حتی با ریسک هم می‌خرند
            val wouldCompleteMonopoly = checkCompletesMonopoly(player, tile, allTiles)
            if (wouldCompleteMonopoly && remainingCash > 10000) {
                return true
            }
            return false
        }

        // بررسی تمایل شخصیت‌ها
        return when (personality) {
            AiPersonality.AGGRESSIVE, AiPersonality.RISK_TAKER -> true
            AiPersonality.CONSERVATIVE -> remainingCash >= 300000
            AiPersonality.PRO_TRADER, AiPersonality.ECONOMIC_GENIUS -> {
                // اگر ایستگاه یا شرکت یا ملک استراتژیک است همیشه خریداری شود
                tile.type == TileType.RAILROAD || tile.type == TileType.UTILITY || checkHasPairInGroup(player, tile, allTiles) || remainingCash >= 150000
            }
        }
    }

    /**
     * تصمیم‌گیری برای پیشنهاد قیمت در حراج
     */
    fun decideAuctionBid(
        player: Player,
        tile: Tile,
        currentBid: Int,
        allTiles: List<Tile>
    ): Int? {
        val minNextBid = currentBid + 10000
        if (player.cash < minNextBid) return null

        val completesMonopoly = checkCompletesMonopoly(player, tile, allTiles)
        val hasPair = checkHasPairInGroup(player, tile, allTiles)

        // حداکثر ارزشی که این هوش مصنوعی حاضر است برای این ملک بپردازد
        val multiplier = when (player.aiPersonality) {
            AiPersonality.AGGRESSIVE -> 1.35f
            AiPersonality.RISK_TAKER -> 1.50f
            AiPersonality.PRO_TRADER -> if (completesMonopoly) 1.60f else 1.10f
            AiPersonality.ECONOMIC_GENIUS -> if (completesMonopoly) 1.50f else (if (hasPair) 1.20f else 0.95f)
            AiPersonality.CONSERVATIVE -> if (completesMonopoly) 1.15f else 0.85f
        }

        val maxValuation = (tile.cost * multiplier).toInt()
        val safetyBuffer = 100000
        val maxAffordable = (player.cash - safetyBuffer).coerceAtLeast(0)

        val upperLimit = minOf(maxValuation, maxAffordable)
        if (minNextBid <= upperLimit) {
            // افزایش تصادفی هوشمند (۱۰ تا ۳۰ هزار تومان بالاتر)
            val step = if (player.aiPersonality == AiPersonality.AGGRESSIVE || player.aiPersonality == AiPersonality.RISK_TAKER) {
                listOf(10000, 20000, 30000).random()
            } else {
                10000
            }
            val proposedBid = currentBid + step
            return if (proposedBid <= upperLimit) proposedBid else minNextBid
        }
        return null // انصراف از حراج
    }

    /**
     * بررسی تصمیم ساخت خانه و هتل در نوبت
     */
    fun decidePropertyToUpgrade(
        player: Player,
        allTiles: List<Tile>
    ): Tile? {
        val ownedTiles = allTiles.filter { it.ownerId == player.id }
        // پیدا کردن گروه‌های مونوپولی شده
        val monopolyGroups = getPlayerMonopolyGroups(player, allTiles)
        if (monopolyGroups.isEmpty()) return null

        val safetyBuffer = when (player.aiPersonality) {
            AiPersonality.AGGRESSIVE -> 100000
            AiPersonality.RISK_TAKER -> 150000
            AiPersonality.PRO_TRADER -> 250000
            AiPersonality.ECONOMIC_GENIUS -> 300000
            AiPersonality.CONSERVATIVE -> 500000
        }

        // املاک قابل ساخت در گروه‌های کامل شده
        val buildableTiles = ownedTiles.filter { tile ->
            tile.group in monopolyGroups && (tile.canBuildHouse || tile.canBuildHotel || tile.canBuildSkyscraper) &&
                    (player.cash - tile.houseCost >= safetyBuffer)
        }

        // اولویت با توسعه هماهنگ و بیشترین بازده اجاره
        return buildableTiles.minByOrNull { it.houses + (if (it.hasHotel) 5 else 0) }
    }

    /**
     * ارزیابی پیشنهاد معامله دریافتی توسط هوش مصنوعی
     */
    fun evaluateTradeOffer(
        aiPlayer: Player,
        tradeOffer: TradeOffer,
        allTiles: List<Tile>
    ): Boolean {
        // محاسبه ارزش دارایی‌های پیشنهادی و درخواستی
        var offeredValue = tradeOffer.offeredCash
        for (propId in tradeOffer.offeredProperties) {
            val tile = allTiles.getOrNull(propId) ?: continue
            var valMultiplier = 1.0f
            if (checkCompletesMonopoly(aiPlayer, tile, allTiles)) {
                valMultiplier = 2.2f // ارزش فوق‌العاده برای کامل کردن گروه
            } else if (checkHasPairInGroup(aiPlayer, tile, allTiles)) {
                valMultiplier = 1.4f
            }
            offeredValue += (tile.cost * valMultiplier).toInt()
        }

        var requestedValue = tradeOffer.requestedCash
        for (propId in tradeOffer.requestedProperties) {
            val tile = allTiles.getOrNull(propId) ?: continue
            var valMultiplier = 1.0f
            val monopolyGroups = getPlayerMonopolyGroups(aiPlayer, allTiles)
            if (tile.group in monopolyGroups) {
                valMultiplier = 2.5f // دادن ملکی از گروه مونوپولی شده هزینه دارد
            }
            requestedValue += (tile.cost * valMultiplier).toInt()
        }

        // مقایسه بر اساس روحیه معامله‌گری شخصیت
        val thresholdMultiplier = when (aiPlayer.aiPersonality) {
            AiPersonality.PRO_TRADER -> 0.95f // علاقه‌مند به معامله
            AiPersonality.AGGRESSIVE -> 1.05f
            AiPersonality.ECONOMIC_GENIUS -> 1.10f // سود قطعی می‌خواهد
            AiPersonality.RISK_TAKER -> 0.90f // اهل ریسک
            AiPersonality.CONSERVATIVE -> 1.30f // بسیار محتاط
        }

        return offeredValue >= (requestedValue * thresholdMultiplier)
    }

    /**
     * بررسی خروج از زندان (پرداخت ۵۰٬۰۰۰ تومان یا استفاده از کارت)
     */
    fun shouldPayToLeaveJail(player: Player): Boolean {
        if (player.getOutOfJailCards > 0) return true
        if (player.cash < 50000) return false

        return when (player.aiPersonality) {
            AiPersonality.AGGRESSIVE, AiPersonality.RISK_TAKER -> true
            AiPersonality.PRO_TRADER -> player.cash > 250000
            AiPersonality.ECONOMIC_GENIUS -> player.cash > 350000
            AiPersonality.CONSERVATIVE -> player.jailTurns >= 2 && player.cash > 400000
        }
    }

    // ================= کمکی‌های تحلیلی =================

    private fun checkCompletesMonopoly(player: Player, targetTile: Tile, allTiles: List<Tile>): Boolean {
        if (targetTile.group == PropertyGroup.SPECIAL) return false
        val groupTiles = allTiles.filter { it.group == targetTile.group }
        val playerOwnedInGroup = groupTiles.count { it.ownerId == player.id }
        return playerOwnedInGroup == groupTiles.size - 1
    }

    private fun checkHasPairInGroup(player: Player, targetTile: Tile, allTiles: List<Tile>): Boolean {
        if (targetTile.group == PropertyGroup.SPECIAL) return false
        return allTiles.any { it.group == targetTile.group && it.ownerId == player.id && it.id != targetTile.id }
    }

    fun getPlayerMonopolyGroups(player: Player, allTiles: List<Tile>): Set<PropertyGroup> {
        val groups = PropertyGroup.entries.filter {
            it != PropertyGroup.SPECIAL && it != PropertyGroup.RAILROAD && it != PropertyGroup.UTILITY
        }
        val monopolies = mutableSetOf<PropertyGroup>()
        for (group in groups) {
            val groupTiles = allTiles.filter { it.group == group }
            if (groupTiles.isNotEmpty() && groupTiles.all { it.ownerId == player.id }) {
                monopolies.add(group)
            }
        }
        return monopolies
    }
}
