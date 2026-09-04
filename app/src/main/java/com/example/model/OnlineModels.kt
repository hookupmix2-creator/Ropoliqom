package com.example.model

/**
 * وضعیت اتاق آنلاین
 */
enum class RoomStatus(val titleFa: String) {
    WAITING("در انتظار بازیکنان"),
    STARTING("در حال شروع"),
    PLAYING("در حال بازی"),
    FINISHED("پایان یافته")
}

/**
 * پروفایل بازیکن آنلاین
 */
data class OnlineUser(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val avatarEmoji: String = "👑",
    val coins: Int = 50000,
    val gamesWon: Int = 0,
    val totalGames: Int = 0,
    val rankTitle: String = "سرمایه‌گذار نوپا",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * اطلاعات بازیکن در اتاق آنلاین
 */
data class OnlineRoomPlayer(
    val uid: String = "",
    val name: String = "",
    val avatarEmoji: String = "👑",
    val colorHex: Long = 0xFF3B82F6,
    val isHost: Boolean = false,
    val isReady: Boolean = false,
    val isConnected: Boolean = true
)

/**
 * مدل اتاق بازی آنلاین
 */
data class OnlineRoom(
    val id: String = "",
    val code: String = "",
    val title: String = "",
    val hostUid: String = "",
    val hostName: String = "",
    val maxPlayers: Int = 4,
    val status: RoomStatus = RoomStatus.WAITING,
    val players: List<OnlineRoomPlayer> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * پیام چت درون اتاق بازی آنلاین
 */
data class RoomChatMessage(
    val id: String = "",
    val senderUid: String = "",
    val senderName: String = "",
    val senderAvatar: String = "👑",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
