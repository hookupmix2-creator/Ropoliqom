package com.example.data

import android.content.Context
import com.example.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

suspend fun <T> com.google.android.gms.tasks.Task<T>.await(): T {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) throw java.util.concurrent.CancellationException("Task was cancelled")
            result
        } else {
            throw e
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener { task ->
            val e = task.exception
            if (e == null) {
                if (task.isCanceled) cont.cancel() else cont.resume(task.result)
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}

/**
 * مدیر و ارائه‌دهنده سرویس‌های بازی آنلاین چندنفره
 * با پشتیبانی از فایربیس و مکانیزم فال‌بک هوشمند
 */
class OnlineGameManager(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val prefs = context.getSharedPreferences("city_of_wealth_online", Context.MODE_PRIVATE)

    // بررسی آمادگی سرویس ابری فایربیس
    val isFirebaseConfigured: Boolean by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context) != null
            } else {
                true
            }
        } catch (_: Exception) {
            false
        }
    }

    private val auth: FirebaseAuth? by lazy {
        if (isFirebaseConfigured) {
            try { FirebaseAuth.getInstance() } catch (_: Exception) { null }
        } else null
    }

    private val firestore: FirebaseFirestore? by lazy {
        if (isFirebaseConfigured) {
            try { FirebaseFirestore.getInstance() } catch (_: Exception) { null }
        } else null
    }

    private val _currentUser = MutableStateFlow<OnlineUser?>(null)
    val currentUser: StateFlow<OnlineUser?> = _currentUser.asStateFlow()

    private val _availableRooms = MutableStateFlow<List<OnlineRoom>>(emptyList())
    val availableRooms: StateFlow<List<OnlineRoom>> = _availableRooms.asStateFlow()

    private val _currentRoom = MutableStateFlow<OnlineRoom?>(null)
    val currentRoom: StateFlow<OnlineRoom?> = _currentRoom.asStateFlow()

    private val _roomMessages = MutableStateFlow<List<RoomChatMessage>>(emptyList())
    val roomMessages: StateFlow<List<RoomChatMessage>> = _roomMessages.asStateFlow()

    private var roomListenerRegistration: ListenerRegistration? = null
    private var messagesListenerRegistration: ListenerRegistration? = null

    // حافظه محلی برای شبیه‌ساز اتاق‌ها در صورت عدم اتصال ابری
    private val simulatedRooms = mutableListOf<OnlineRoom>()

    init {
        loadSavedUser()
        populateInitialSimulatedRooms()
    }

    private fun loadSavedUser() {
        val uid = prefs.getString("user_uid", null)
        val name = prefs.getString("user_name", null)
        if (uid != null && name != null) {
            _currentUser.value = OnlineUser(
                uid = uid,
                displayName = name,
                email = prefs.getString("user_email", "") ?: "",
                avatarEmoji = prefs.getString("user_avatar", "👑") ?: "👑",
                coins = prefs.getInt("user_coins", 50000),
                gamesWon = prefs.getInt("user_wins", 0),
                totalGames = prefs.getInt("user_total", 0),
                rankTitle = prefs.getString("user_rank", "سرمایه‌گذار نوپا") ?: "سرمایه‌گذار نوپا"
            )
        }
    }

    private fun saveUserToPrefs(user: OnlineUser) {
        prefs.edit()
            .putString("user_uid", user.uid)
            .putString("user_name", user.displayName)
            .putString("user_email", user.email)
            .putString("user_avatar", user.avatarEmoji)
            .putInt("user_coins", user.coins)
            .putInt("user_wins", user.gamesWon)
            .putInt("user_total", user.totalGames)
            .putString("user_rank", user.rankTitle)
            .apply()
    }

    private fun populateInitialSimulatedRooms() {
        if (simulatedRooms.isEmpty()) {
            simulatedRooms.add(
                OnlineRoom(
                    id = "room_tehran_1",
                    code = "104820",
                    title = "بزرگراه همت • رقابت حرفه‌ای‌ها",
                    hostUid = "sim_host_1",
                    hostName = "آرش سرمایه‌دار",
                    maxPlayers = 4,
                    status = RoomStatus.WAITING,
                    players = listOf(
                        OnlineRoomPlayer("sim_host_1", "آرش سرمایه‌دار", "👑", 0xFF3B82F6, isHost = true, isReady = true),
                        OnlineRoomPlayer("sim_p2", "سارا تاجر", "💎", 0xFF10B981, isHost = false, isReady = true)
                    )
                )
            )
            simulatedRooms.add(
                OnlineRoom(
                    id = "room_shiraz_2",
                    code = "582914",
                    title = "باغ ارم • دورهمی دوستانه",
                    hostUid = "sim_host_2",
                    hostName = "بهرام خان",
                    maxPlayers = 4,
                    status = RoomStatus.WAITING,
                    players = listOf(
                        OnlineRoomPlayer("sim_host_2", "بهرام خان", "🦁", 0xFFF59E0B, isHost = true, isReady = true)
                    )
                )
            )
            _availableRooms.value = simulatedRooms.toList()
        }
    }

    /**
     * ثبت‌نام با نام، ایمیل و کلمه عبور
     */
    suspend fun registerUser(name: String, email: String, pass: String, avatar: String): Result<OnlineUser> {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return Result.failure(Exception("لطفاً نام نمایشی خود را وارد کنید"))

        val firebaseAuth = auth
        if (isFirebaseConfigured && firebaseAuth != null && email.isNotBlank() && pass.length >= 6) {
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), pass).await()
                val uid = authResult.user?.uid ?: UUID.randomUUID().toString()
                val newUser = OnlineUser(
                    uid = uid,
                    displayName = trimmedName,
                    email = email.trim(),
                    avatarEmoji = avatar,
                    coins = 50000
                )
                _currentUser.value = newUser
                saveUserToPrefs(newUser)
                saveUserToFirestore(newUser)
                return Result.success(newUser)
            } catch (e: Exception) {
                // در صورت بروز خطای فایربیس، ورود محلی انجام می‌شود تا تجربه کاربر متوقف نشود
                val fallbackUser = OnlineUser(
                    uid = "usr_" + UUID.randomUUID().toString().take(8),
                    displayName = trimmedName,
                    email = email.trim(),
                    avatarEmoji = avatar
                )
                _currentUser.value = fallbackUser
                saveUserToPrefs(fallbackUser)
                return Result.success(fallbackUser)
            }
        } else {
            // ثبت‌نام سریع و محلی
            val localUser = OnlineUser(
                uid = "usr_" + UUID.randomUUID().toString().take(8),
                displayName = trimmedName,
                email = email.trim(),
                avatarEmoji = avatar
            )
            _currentUser.value = localUser
            saveUserToPrefs(localUser)
            return Result.success(localUser)
        }
    }

    /**
     * ورود به حساب کاربری
     */
    suspend fun loginUser(email: String, pass: String): Result<OnlineUser> {
        val firebaseAuth = auth
        if (isFirebaseConfigured && firebaseAuth != null && email.isNotBlank() && pass.isNotBlank()) {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(email.trim(), pass).await()
                val uid = authResult.user?.uid ?: ""
                val existing = _currentUser.value?.copy(uid = uid, email = email.trim())
                    ?: OnlineUser(
                        uid = uid,
                        displayName = authResult.user?.displayName ?: "بازیکن آنلاین",
                        email = email.trim()
                    )
                _currentUser.value = existing
                saveUserToPrefs(existing)
                return Result.success(existing)
            } catch (e: Exception) {
                return Result.failure(Exception("خطا در ورود: بررسی ایمیل و گذرواژه"))
            }
        } else {
            // ورود آفلاین / مهمان
            val name = if (email.contains("@")) email.substringBefore("@") else "سرمایه‌گذار محترم"
            val user = OnlineUser(
                uid = "usr_" + UUID.randomUUID().toString().take(8),
                displayName = name,
                email = email.trim(),
                avatarEmoji = "👑"
            )
            _currentUser.value = user
            saveUserToPrefs(user)
            return Result.success(user)
        }
    }

    /**
     * ورود فوری به عنوان مهمان
     */
    fun loginAsGuest(guestName: String = "بازیکن مهمان", avatar: String = "👑"): OnlineUser {
        val user = OnlineUser(
            uid = "guest_" + Random.nextInt(1000, 9999),
            displayName = "$guestName ${Random.nextInt(100, 999)}",
            avatarEmoji = avatar,
            coins = 30000
        )
        _currentUser.value = user
        saveUserToPrefs(user)
        return user
    }

    fun logout() {
        auth?.signOut()
        prefs.edit().clear().apply()
        _currentUser.value = null
        leaveCurrentRoom()
    }

    private fun saveUserToFirestore(user: OnlineUser) {
        val db = firestore ?: return
        db.collection("users").document(user.uid).set(user)
    }

    /**
     * ایجاد اتاق آنلاین جدید
     */
    fun createRoom(title: String, maxPlayers: Int = 4): Result<OnlineRoom> {
        val user = _currentUser.value ?: return Result.failure(Exception("ابتدا وارد حساب کاربری شوید"))
        val roomCode = (100000 + Random.nextInt(900000)).toString()
        val roomId = "room_" + UUID.randomUUID().toString().take(8)

        val hostPlayer = OnlineRoomPlayer(
            uid = user.uid,
            name = user.displayName,
            avatarEmoji = user.avatarEmoji,
            colorHex = 0xFF3B82F6,
            isHost = true,
            isReady = true
        )

        val newRoom = OnlineRoom(
            id = roomId,
            code = roomCode,
            title = if (title.isBlank()) "اتاق ${user.displayName}" else title.trim(),
            hostUid = user.uid,
            hostName = user.displayName,
            maxPlayers = maxPlayers.coerceIn(2, 4),
            status = RoomStatus.WAITING,
            players = listOf(hostPlayer)
        )

        simulatedRooms.add(0, newRoom)
        _availableRooms.value = simulatedRooms.toList()
        _currentRoom.value = newRoom
        _roomMessages.value = listOf(
            RoomChatMessage(
                id = UUID.randomUUID().toString(),
                senderUid = "system",
                senderName = "سیستم",
                senderAvatar = "📢",
                text = "اتاق با موفقیت ساخته شد. کد اتاق برای دعوت: $roomCode"
            )
        )

        // همگام‌سازی ابری در صورت در دسترس بودن
        firestore?.collection("rooms")?.document(roomId)?.set(newRoom)

        return Result.success(newRoom)
    }

    /**
     * پیوستن به اتاق با کد ۶ رقمی
     */
    fun joinRoomByCode(code: String): Result<OnlineRoom> {
        val user = _currentUser.value ?: return Result.failure(Exception("ابتدا وارد شوید"))
        val target = simulatedRooms.find { it.code == code.trim() }
            ?: return Result.failure(Exception("اتاقی با این کد یافت نشد"))

        if (target.players.size >= target.maxPlayers) {
            return Result.failure(Exception("ظرفیت این اتاق تکمیل شده است"))
        }

        if (target.players.any { it.uid == user.uid }) {
            _currentRoom.value = target
            return Result.success(target)
        }

        val colors = listOf(0xFF10B981, 0xFFF59E0B, 0xFFEF4444, 0xFF8B5CF6)
        val playerColor = colors.getOrElse(target.players.size) { 0xFF6366F1 }

        val newPlayer = OnlineRoomPlayer(
            uid = user.uid,
            name = user.displayName,
            avatarEmoji = user.avatarEmoji,
            colorHex = playerColor,
            isHost = false,
            isReady = false
        )

        val updated = target.copy(players = target.players + newPlayer)
        val idx = simulatedRooms.indexOfFirst { it.id == target.id }
        if (idx != -1) simulatedRooms[idx] = updated
        _availableRooms.value = simulatedRooms.toList()
        _currentRoom.value = updated

        sendSystemMessage("${user.displayName} به اتاق پیوست 👋")
        return Result.success(updated)
    }

    /**
     * جستجوی سریع و ورود خودکار به اولین اتاق با ظرفیت خالی
     */
    fun quickMatch(): Result<OnlineRoom> {
        val available = simulatedRooms.firstOrNull { it.players.size < it.maxPlayers && it.status == RoomStatus.WAITING }
        return if (available != null) {
            joinRoomByCode(available.code)
        } else {
            createRoom("اتاق بازی سریع ⚡", 4)
        }
    }

    /**
     * تغییر وضعیت آمادگی بازیکن در اتاق
     */
    fun toggleReady() {
        val user = _currentUser.value ?: return
        val room = _currentRoom.value ?: return

        val updatedPlayers = room.players.map { p ->
            if (p.uid == user.uid) p.copy(isReady = !p.isReady) else p
        }
        val updated = room.copy(players = updatedPlayers)
        updateRoomInternal(updated)
    }

    /**
     * ارسال پیام در چت اتاق
     */
    fun sendChatMessage(text: String) {
        val user = _currentUser.value ?: return
        if (text.isBlank()) return

        val msg = RoomChatMessage(
            id = UUID.randomUUID().toString(),
            senderUid = user.uid,
            senderName = user.displayName,
            senderAvatar = user.avatarEmoji,
            text = text.trim()
        )
        _roomMessages.value = _roomMessages.value + msg
    }

    private fun sendSystemMessage(text: String) {
        val msg = RoomChatMessage(
            id = UUID.randomUUID().toString(),
            senderUid = "system",
            senderName = "سیستم",
            senderAvatar = "📢",
            text = text
        )
        _roomMessages.value = _roomMessages.value + msg
    }

    /**
     * شروع بازی توسط میزبان
     */
    fun startOnlineGame(): Result<OnlineRoom> {
        val user = _currentUser.value ?: return Result.failure(Exception("کاربر نامعتبر"))
        val room = _currentRoom.value ?: return Result.failure(Exception("اتاقی یافت نشد"))

        if (room.hostUid != user.uid) {
            return Result.failure(Exception("فقط میزبان اتاق می‌تواند بازی را آغاز کند"))
        }

        if (room.players.size < 2) {
            return Result.failure(Exception("حداقل به ۲ بازیکن برای آغاز بازی آنلاین نیاز است"))
        }

        val updated = room.copy(status = RoomStatus.PLAYING)
        updateRoomInternal(updated)
        sendSystemMessage("بازی آنلاین آغاز شد! موفق باشید 🎲")
        return Result.success(updated)
    }

    /**
     * خروج از اتاق جاری
     */
    fun leaveCurrentRoom() {
        val user = _currentUser.value
        val room = _currentRoom.value
        if (user != null && room != null) {
            val remaining = room.players.filterNot { it.uid == user.uid }
            if (remaining.isEmpty()) {
                simulatedRooms.removeAll { it.id == room.id }
            } else {
                val updated = room.copy(
                    players = remaining,
                    hostUid = if (room.hostUid == user.uid) remaining.first().uid else room.hostUid,
                    hostName = if (room.hostUid == user.uid) remaining.first().name else room.hostName
                )
                val idx = simulatedRooms.indexOfFirst { it.id == room.id }
                if (idx != -1) simulatedRooms[idx] = updated
            }
            _availableRooms.value = simulatedRooms.toList()
        }
        _currentRoom.value = null
        _roomMessages.value = emptyList()
    }

    private fun updateRoomInternal(room: OnlineRoom) {
        _currentRoom.value = room
        val idx = simulatedRooms.indexOfFirst { it.id == room.id }
        if (idx != -1) simulatedRooms[idx] = room
        _availableRooms.value = simulatedRooms.toList()

        firestore?.collection("rooms")?.document(room.id)?.set(room)
    }
}
