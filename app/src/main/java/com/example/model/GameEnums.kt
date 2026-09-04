package com.example.model

/**
 * انواع خانه‌های صفحه بازی شهر ثروت
 */
enum class TileType(val persianTitle: String) {
    START("شروع"),
    PROPERTY("ملک مسکونی و تجاری"),
    CHANCE("کارت شانس"),
    COMMUNITY_CHEST("کارت اتفاق"),
    JAIL("زندان و بازداشتگاه"),
    GO_TO_JAIL("رفتن به زندان"),
    FREE_PARKING("پارک آزاد"),
    TAX("مالیات"),
    UTILITY("شرکت خدمات عمومی"),
    RAILROAD("حمل و نقل شهری")
}

/**
 * گروه‌های رنگی املاک
 */
enum class PropertyGroup(val colorHex: Long, val groupName: String) {
    BROWN(0xFF795548, "قهوه‌ای"),
    CYAN(0xFF00BCD4, "فیروزه‌ای"),
    MAGENTA(0xFFE91E63, "سرخابی"),
    ORANGE(0xFFFF9800, "نارنجی"),
    RED(0xFFF44336, "قرمز"),
    YELLOW(0xFFFFEB3B, "زرد"),
    GREEN(0xFF4CAF50, "سبز"),
    BLUE(0xFF1E88E5, "سرمه‌ای"),
    RAILROAD(0xFF607D8B, "ایستگاه‌ها"),
    UTILITY(0xFF9E9E9E, "خدمات عمومی"),
    SPECIAL(0xFF000000, "ویژه")
}

/**
 * نوع بازیکن: انسان یا هوش مصنوعی
 */
enum class PlayerType(val persianTitle: String) {
    HUMAN("انسان"),
    AI("هوش مصنوعی")
}

/**
 * شخصیت‌ها و استراتژی‌های هوش مصنوعی
 */
enum class AiPersonality(
    val persianTitle: String,
    val description: String,
    val riskMultiplier: Float,
    val tradeWillingness: Float
) {
    AGGRESSIVE(
        "سرمایه‌گذار تهاجمی",
        "ریسک‌پذیر و علاقه‌مند به خرید حداکثری و حراج‌های سنگین",
        1.4f,
        0.8f
    ),
    CONSERVATIVE(
        "سرمایه‌گذار محافظه‌کار",
        "پول نقد خود را حفظ می‌کند و با احتیاط کامل معامله می‌کند",
        0.7f,
        0.4f
    ),
    PRO_TRADER(
        "معامله‌گر حرفه‌ای",
        "تمرکز اصلی روی تبادل هوشمندانه املاک و ساخت مونوپولی",
        1.0f,
        1.5f
    ),
    RISK_TAKER(
        "بازیکن ریسک‌پذیر",
        "تصمیم‌های غیرقابل پیش‌بینی و هیجانی می‌گیرد",
        1.6f,
        1.0f
    ),
    ECONOMIC_GENIUS(
        "نابغه اقتصادی",
        "بررسی دقیق بازده سرمایه‌گذاری، اجاره، نقدینگی و ارزش آینده",
        1.1f,
        1.1f
    );

    val titleFa: String get() = persianTitle
    val descriptionFa: String get() = description

    companion object {
        fun fromDifficultyIndex(index: Int): AiPersonality {
            return when (index) {
                0 -> CONSERVATIVE // آسان
                1 -> RISK_TAKER // معمولی
                2 -> PRO_TRADER // سخت
                3 -> AGGRESSIVE // حرفه‌ای
                else -> ECONOMIC_GENIUS // نابغه
            }
        }
    }
}

/**
 * حالت‌های بازی
 */
enum class GameMode(val persianTitle: String, val description: String, val startingCash: Int) {
    CLASSIC("کلاسیک", "قوانین اصیل و استاندارد بازی شهر ثروت", 1500000),
    FAST("سریع", "بازی پرسرعت‌تر با سرمایه اولیه بالاتر و ساخت سریع", 2500000),
    CHAOS("آشوب", "رویدادهای اقتصادی تصادفی فراوان و غیرمنتظره", 1800000),
    AI_CHALLENGE("چالش هوش مصنوعی", "نبرد تاکتیکی و هوشمند در برابر نخبه‌های هوش مصنوعی", 1500000),
    LOCAL_PASS_AND_PLAY("چندنفره محلی", "بازی دورهمی هیجان‌انگیز روی یک دستگاه", 1500000),
    ONLINE_MULTIPLAYER("چندنفره آنلاین", "رقابت آنلاین و بلادرنگ با دیگر بازیکنان", 1500000);

    val titleFa: String get() = persianTitle
    val descriptionFa: String get() = description
}

/**
 * مسیرهای ناوبری
 */
enum class ScreenRoute {
    MAIN_MENU,
    PLAYER_SETUP,
    GAME,
    STATS,
    ACHIEVEMENTS,
    RULES,
    SETTINGS,
    ONLINE_AUTH,
    ONLINE_LOBBY,
    ONLINE_ROOM
}

/**
 * رویدادهای تصادفی جهانی
 */
enum class GlobalEventType(
    val title: String,
    val description: String,
    val rentMultiplier: Float = 1.0f,
    val buyDiscountMultiplier: Float = 1.0f
) {
    NONE("وضعیت عادی", "بازار در آرامش و تعادل است"),
    MARKET_BOOM("رونق بازار", "قیمت اجاره کلیه املاک ۳۰٪ افزایش یافته است!", rentMultiplier = 1.3f),
    RECESSION("رکود اقتصادی", "درآمد اجاره‌ها ۲۰٪ کاهش یافته است", rentMultiplier = 0.8f),
    SHOPPING_FESTIVAL("جشنواره خرید", "خرید املاک مسکونی با ۲۰٪ تخفیف همراه است!", buyDiscountMultiplier = 0.8f),
    OLYMPICS_HOSTING("میزبانی مسابقات بین‌المللی", "رونق عظیم گردشگری و دو برابر شدن اجاره هتل‌ها", rentMultiplier = 1.5f),
    BANK_DIVIDEND("پاداش بانک", "بانک به همه سرمایه‌گذاران پاداش نقدی می‌دهد"),
    EMERGENCY_TAX("مالیات اضطراری", "عوارض غیرمترقبه شهری برای کلیه بازیکنان اعمال شد"),
    BIG_LUCK("شانس بزرگ", "فرصت‌های طلایی در شهر ثروت پدیدار شده است")
}
