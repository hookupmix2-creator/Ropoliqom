package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class GameStorage(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("city_of_wealth_prefs", Context.MODE_PRIVATE)

    // ================= Settings =================
    fun saveSettings(settings: SettingsState) {
        prefs.edit()
            .putBoolean("is_dark_theme", settings.isDarkTheme)
            .putBoolean("sound_enabled", settings.soundEnabled)
            .putBoolean("fast_animations", settings.fastAnimations)
            .putLong("ai_delay_ms", settings.aiThinkingDelayMs)
            .apply()
    }

    fun loadSettings(): SettingsState {
        return SettingsState(
            isDarkTheme = prefs.getBoolean("is_dark_theme", true),
            soundEnabled = prefs.getBoolean("sound_enabled", true),
            fastAnimations = prefs.getBoolean("fast_animations", false),
            aiThinkingDelayMs = prefs.getLong("ai_delay_ms", 800L)
        )
    }

    // ================= General Stats =================
    fun saveStats(stats: GeneralStats) {
        val json = JSONObject().apply {
            put("gamesPlayed", stats.gamesPlayed)
            put("wins", stats.wins)
            put("losses", stats.losses)
            put("totalCashEarned", stats.totalCashEarned)
            put("totalRentPaid", stats.totalRentPaid)
            put("propertiesPurchased", stats.propertiesPurchased)
            put("housesBuilt", stats.housesBuilt)
            put("hotelsBuilt", stats.hotelsBuilt)
            put("tradesCompleted", stats.tradesCompleted)
            put("diceRolled", stats.diceRolled)
            put("maxWealthAchieved", stats.maxWealthAchieved)
        }
        prefs.edit().putString("general_stats_json", json.toString()).apply()
    }

    fun loadStats(): GeneralStats {
        val raw = prefs.getString("general_stats_json", null) ?: return GeneralStats()
        return try {
            val json = JSONObject(raw)
            GeneralStats(
                gamesPlayed = json.optInt("gamesPlayed", 0),
                wins = json.optInt("wins", 0),
                losses = json.optInt("losses", 0),
                totalCashEarned = json.optLong("totalCashEarned", 0L),
                totalRentPaid = json.optLong("totalRentPaid", 0L),
                propertiesPurchased = json.optInt("propertiesPurchased", 0),
                housesBuilt = json.optInt("housesBuilt", 0),
                hotelsBuilt = json.optInt("hotelsBuilt", 0),
                tradesCompleted = json.optInt("tradesCompleted", 0),
                diceRolled = json.optInt("diceRolled", 0),
                maxWealthAchieved = json.optLong("maxWealthAchieved", 1500000L)
            )
        } catch (e: Exception) {
            GeneralStats()
        }
    }

    // ================= Achievements =================
    fun saveAchievements(achievements: List<Achievement>) {
        val array = JSONArray()
        for (ach in achievements) {
            val obj = JSONObject().apply {
                put("id", ach.id)
                put("title", ach.title)
                put("description", ach.description)
                put("icon", ach.icon)
                put("isUnlocked", ach.isUnlocked)
                put("progress", ach.progress)
                put("maxProgress", ach.maxProgress)
            }
            array.put(obj)
        }
        prefs.edit().putString("achievements_json", array.toString()).apply()
    }

    fun loadAchievements(): List<Achievement> {
        val raw = prefs.getString("achievements_json", null)
        val defaultList = GameDefaults.createDefaultAchievements()
        if (raw.isNullOrBlank()) return defaultList
        return try {
            val array = JSONArray(raw)
            val loadedMap = mutableMapOf<String, Achievement>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.getString("id")
                loadedMap[id] = Achievement(
                    id = id,
                    title = obj.optString("title", ""),
                    description = obj.optString("description", ""),
                    icon = obj.optString("icon", "star"),
                    isUnlocked = obj.optBoolean("isUnlocked", false),
                    progress = obj.optInt("progress", 0),
                    maxProgress = obj.optInt("maxProgress", 1)
                )
            }
            defaultList.map { def ->
                loadedMap[def.id] ?: def
            }
        } catch (e: Exception) {
            defaultList
        }
    }

    // ================= Saved Game State =================
    fun hasSavedGame(): Boolean {
        return prefs.contains("saved_game_state_json")
    }

    fun clearSavedGame() {
        prefs.edit().remove("saved_game_state_json").apply()
    }

    fun saveGameState(state: GameState) {
        try {
            val root = JSONObject()
            root.put("id", state.id)
            root.put("mode", state.mode.name)
            root.put("currentTurnIndex", state.currentTurnIndex)
            root.put("dice1", state.dice1)
            root.put("dice2", state.dice2)
            root.put("hasRolled", state.hasRolled)
            root.put("doublesCount", state.doublesCount)
            root.put("freeParkingJackpot", state.freeParkingJackpot)
            root.put("activeGlobalEvent", state.activeGlobalEvent.name)
            root.put("globalEventTurnsRemaining", state.globalEventTurnsRemaining)
            root.put("isGameOver", state.isGameOver)
            root.put("winnerId", state.winnerId ?: -1)
            root.put("turnNumber", state.turnNumber)

            // Players
            val playersArr = JSONArray()
            for (p in state.players) {
                val pObj = JSONObject().apply {
                    put("id", p.id)
                    put("name", p.name)
                    put("avatarEmoji", p.avatarEmoji)
                    put("colorHex", p.colorHex)
                    put("type", p.type.name)
                    put("aiPersonality", p.aiPersonality.name)
                    put("cash", p.cash)
                    put("position", p.position)
                    put("inJail", p.inJail)
                    put("jailTurns", p.jailTurns)
                    put("getOutOfJailCards", p.getOutOfJailCards)
                    put("isBankrupt", p.isBankrupt)

                    val propsArr = JSONArray()
                    p.propertiesOwned.forEach { propsArr.put(it) }
                    put("propertiesOwned", propsArr)
                }
                playersArr.put(pObj)
            }
            root.put("players", playersArr)

            // Tiles (ownership and buildings)
            val tilesArr = JSONArray()
            for (t in state.tiles) {
                val tObj = JSONObject().apply {
                    put("id", t.id)
                    put("ownerId", t.ownerId ?: -1)
                    put("houses", t.houses)
                    put("hasHotel", t.hasHotel)
                    put("hasSkyscraper", t.hasSkyscraper)
                    put("isMortgaged", t.isMortgaged)
                }
                tilesArr.put(tObj)
            }
            root.put("tiles", tilesArr)

            prefs.edit().putString("saved_game_state_json", root.toString()).apply()
        } catch (e: Exception) {
            // Save fallback
        }
    }

    fun loadGameState(): GameState? {
        val raw = prefs.getString("saved_game_state_json", null) ?: return null
        return try {
            val root = JSONObject(raw)
            val modeStr = root.optString("mode", GameMode.CLASSIC.name)
            val mode = runCatching { GameMode.valueOf(modeStr) }.getOrDefault(GameMode.CLASSIC)

            // Reconstruct default board and override dynamic fields
            val defaultTiles = GameDefaults.createDefaultBoard().toMutableList()
            val tilesArr = root.optJSONArray("tiles")
            if (tilesArr != null) {
                for (i in 0 until tilesArr.length()) {
                    val tObj = tilesArr.getJSONObject(i)
                    val id = tObj.getInt("id")
                    val ownerIdRaw = tObj.getInt("ownerId")
                    val ownerId = if (ownerIdRaw >= 0) ownerIdRaw else null
                    val houses = tObj.optInt("houses", 0)
                    val hasHotel = tObj.optBoolean("hasHotel", false)
                    val hasSkyscraper = tObj.optBoolean("hasSkyscraper", false)
                    val isMortgaged = tObj.optBoolean("isMortgaged", false)

                    if (id in defaultTiles.indices) {
                        defaultTiles[id] = defaultTiles[id].copy(
                            ownerId = ownerId,
                            houses = houses,
                            hasHotel = hasHotel,
                            hasSkyscraper = hasSkyscraper,
                            isMortgaged = isMortgaged
                        )
                    }
                }
            }

            // Players
            val playersList = mutableListOf<Player>()
            val playersArr = root.optJSONArray("players")
            if (playersArr != null) {
                for (i in 0 until playersArr.length()) {
                    val pObj = playersArr.getJSONObject(i)
                    val pTypeStr = pObj.optString("type", PlayerType.HUMAN.name)
                    val pType = runCatching { PlayerType.valueOf(pTypeStr) }.getOrDefault(PlayerType.HUMAN)
                    val aiStr = pObj.optString("aiPersonality", AiPersonality.CONSERVATIVE.name)
                    val ai = runCatching { AiPersonality.valueOf(aiStr) }.getOrDefault(AiPersonality.CONSERVATIVE)

                    val propsArr = pObj.optJSONArray("propertiesOwned")
                    val props = mutableListOf<Int>()
                    if (propsArr != null) {
                        for (k in 0 until propsArr.length()) {
                            props.add(propsArr.getInt(k))
                        }
                    }

                    playersList.add(
                        Player(
                            id = pObj.getInt("id"),
                            name = pObj.getString("name"),
                            avatarEmoji = pObj.optString("avatarEmoji", "👑"),
                            colorHex = pObj.optLong("colorHex", 0xFFE53935),
                            type = pType,
                            aiPersonality = ai,
                            cash = pObj.optInt("cash", 1500000),
                            position = pObj.optInt("position", 0),
                            inJail = pObj.optBoolean("inJail", false),
                            jailTurns = pObj.optInt("jailTurns", 0),
                            getOutOfJailCards = pObj.optInt("getOutOfJailCards", 0),
                            isBankrupt = pObj.optBoolean("isBankrupt", false),
                            propertiesOwned = props
                        )
                    )
                }
            }

            val winnerIdRaw = root.optInt("winnerId", -1)
            val winnerId = if (winnerIdRaw >= 0) winnerIdRaw else null
            val activeEventStr = root.optString("activeGlobalEvent", GlobalEventType.NONE.name)
            val activeEvent = runCatching { GlobalEventType.valueOf(activeEventStr) }.getOrDefault(GlobalEventType.NONE)

            GameState(
                id = root.optString("id", "game_restored"),
                mode = mode,
                players = playersList,
                tiles = defaultTiles,
                currentTurnIndex = root.optInt("currentTurnIndex", 0),
                dice1 = root.optInt("dice1", 1),
                dice2 = root.optInt("dice2", 1),
                hasRolled = root.optBoolean("hasRolled", false),
                doublesCount = root.optInt("doublesCount", 0),
                freeParkingJackpot = root.optInt("freeParkingJackpot", 100000),
                activeGlobalEvent = activeEvent,
                globalEventTurnsRemaining = root.optInt("globalEventTurnsRemaining", 0),
                isGameOver = root.optBoolean("isGameOver", false),
                winnerId = winnerId,
                turnNumber = root.optInt("turnNumber", 1)
            )
        } catch (e: Exception) {
            null
        }
    }
}
