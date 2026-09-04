package com.example.data

import com.example.model.*

object GameDefaults {

    /**
     * صفحه ۴۰ خانه‌ای رسمی شهر ثروت
     */
    fun createDefaultBoard(): List<Tile> {
        return listOf(
            Tile(
                id = 0,
                name = "خانه شروع",
                type = TileType.START,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 1,
                name = "میدان راه‌آهن",
                type = TileType.PROPERTY,
                group = PropertyGroup.BROWN,
                cost = 60000,
                baseRent = 4000,
                houseRents = listOf(20000, 60000, 180000, 320000),
                hotelRent = 450000,
                skyscraperRent = 600000,
                houseCost = 50000
            ),
            Tile(
                id = 2,
                name = "صندوق اتفاق",
                type = TileType.COMMUNITY_CHEST,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 3,
                name = "بازار بزرگ",
                type = TileType.PROPERTY,
                group = PropertyGroup.BROWN,
                cost = 60000,
                baseRent = 8000,
                houseRents = listOf(40000, 100000, 300000, 450000),
                hotelRent = 550000,
                skyscraperRent = 750000,
                houseCost = 50000
            ),
            Tile(
                id = 4,
                name = "مالیات بر ثروت",
                type = TileType.TAX,
                group = PropertyGroup.SPECIAL,
                cost = 100000 // Tax amount
            ),
            Tile(
                id = 5,
                name = "ایستگاه راه‌آهن",
                type = TileType.RAILROAD,
                group = PropertyGroup.RAILROAD,
                cost = 200000,
                baseRent = 25000
            ),
            Tile(
                id = 6,
                name = "بهارستان",
                type = TileType.PROPERTY,
                group = PropertyGroup.CYAN,
                cost = 100000,
                baseRent = 6000,
                houseRents = listOf(30000, 90000, 270000, 400000),
                hotelRent = 550000,
                skyscraperRent = 750000,
                houseCost = 50000
            ),
            Tile(
                id = 7,
                name = "کارت شانس",
                type = TileType.CHANCE,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 8,
                name = "میدان آزادی",
                type = TileType.PROPERTY,
                group = PropertyGroup.CYAN,
                cost = 100000,
                baseRent = 6000,
                houseRents = listOf(30000, 90000, 270000, 400000),
                hotelRent = 550000,
                skyscraperRent = 750000,
                houseCost = 50000
            ),
            Tile(
                id = 9,
                name = "خیابان جمهوری",
                type = TileType.PROPERTY,
                group = PropertyGroup.CYAN,
                cost = 120000,
                baseRent = 8000,
                houseRents = listOf(40000, 100000, 300000, 450000),
                hotelRent = 600000,
                skyscraperRent = 800000,
                houseCost = 50000
            ),
            Tile(
                id = 10,
                name = "بازداشتگاه",
                type = TileType.JAIL,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 11,
                name = "خیابان انقلاب",
                type = TileType.PROPERTY,
                group = PropertyGroup.MAGENTA,
                cost = 140000,
                baseRent = 10000,
                houseRents = listOf(50000, 150000, 450000, 620000),
                hotelRent = 750000,
                skyscraperRent = 950000,
                houseCost = 100000
            ),
            Tile(
                id = 12,
                name = "سازمان برق",
                type = TileType.UTILITY,
                group = PropertyGroup.UTILITY,
                cost = 150000,
                baseRent = 4000 // Multiplied by dice roll
            ),
            Tile(
                id = 13,
                name = "کارگر شمالی",
                type = TileType.PROPERTY,
                group = PropertyGroup.MAGENTA,
                cost = 140000,
                baseRent = 10000,
                houseRents = listOf(50000, 150000, 450000, 620000),
                hotelRent = 750000,
                skyscraperRent = 950000,
                houseCost = 100000
            ),
            Tile(
                id = 14,
                name = "بلوار کشاورز",
                type = TileType.PROPERTY,
                group = PropertyGroup.MAGENTA,
                cost = 160000,
                baseRent = 12000,
                houseRents = listOf(60000, 180000, 500000, 700000),
                hotelRent = 900000,
                skyscraperRent = 1100000,
                houseCost = 100000
            ),
            Tile(
                id = 15,
                name = "فرودگاه مهرآباد",
                type = TileType.RAILROAD,
                group = PropertyGroup.RAILROAD,
                cost = 200000,
                baseRent = 25000
            ),
            Tile(
                id = 16,
                name = "خیابان ولیعصر",
                type = TileType.PROPERTY,
                group = PropertyGroup.ORANGE,
                cost = 180000,
                baseRent = 14000,
                houseRents = listOf(70000, 200000, 550000, 750000),
                hotelRent = 950000,
                skyscraperRent = 1200000,
                houseCost = 100000
            ),
            Tile(
                id = 17,
                name = "صندوق اتفاق",
                type = TileType.COMMUNITY_CHEST,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 18,
                name = "خیابان میرداماد",
                type = TileType.PROPERTY,
                group = PropertyGroup.ORANGE,
                cost = 180000,
                baseRent = 14000,
                houseRents = listOf(70000, 200000, 550000, 750000),
                hotelRent = 950000,
                skyscraperRent = 1200000,
                houseCost = 100000
            ),
            Tile(
                id = 19,
                name = "خیابان جردن",
                type = TileType.PROPERTY,
                group = PropertyGroup.ORANGE,
                cost = 200000,
                baseRent = 16000,
                houseRents = listOf(80000, 220000, 600000, 800000),
                hotelRent = 1000000,
                skyscraperRent = 1300000,
                houseCost = 100000
            ),
            Tile(
                id = 20,
                name = "پارک آزاد",
                type = TileType.FREE_PARKING,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 21,
                name = "خیابان گاندی",
                type = TileType.PROPERTY,
                group = PropertyGroup.RED,
                cost = 220000,
                baseRent = 18000,
                houseRents = listOf(90000, 250000, 700000, 875000),
                hotelRent = 1050000,
                skyscraperRent = 1350000,
                houseCost = 150000
            ),
            Tile(
                id = 22,
                name = "کارت شانس",
                type = TileType.CHANCE,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 23,
                name = "خیابان سهروردی",
                type = TileType.PROPERTY,
                group = PropertyGroup.RED,
                cost = 220000,
                baseRent = 18000,
                houseRents = listOf(90000, 250000, 700000, 875000),
                hotelRent = 1050000,
                skyscraperRent = 1350000,
                houseCost = 150000
            ),
            Tile(
                id = 24,
                name = "خیابان پاسداران",
                type = TileType.PROPERTY,
                group = PropertyGroup.RED,
                cost = 240000,
                baseRent = 20000,
                houseRents = listOf(100000, 300000, 750000, 925000),
                hotelRent = 1100000,
                skyscraperRent = 1400000,
                houseCost = 150000
            ),
            Tile(
                id = 25,
                name = "متروی پایتخت",
                type = TileType.RAILROAD,
                group = PropertyGroup.RAILROAD,
                cost = 200000,
                baseRent = 25000
            ),
            Tile(
                id = 26,
                name = "یوسف‌آباد",
                type = TileType.PROPERTY,
                group = PropertyGroup.YELLOW,
                cost = 260000,
                baseRent = 22000,
                houseRents = listOf(110000, 330000, 800000, 975000),
                hotelRent = 1150000,
                skyscraperRent = 1450000,
                houseCost = 150000
            ),
            Tile(
                id = 27,
                name = "میدان تجریش",
                type = TileType.PROPERTY,
                group = PropertyGroup.YELLOW,
                cost = 260000,
                baseRent = 22000,
                houseRents = listOf(110000, 330000, 800000, 975000),
                hotelRent = 1150000,
                skyscraperRent = 1450000,
                houseCost = 150000
            ),
            Tile(
                id = 28,
                name = "سازمان آب",
                type = TileType.UTILITY,
                group = PropertyGroup.UTILITY,
                cost = 150000,
                baseRent = 4000
            ),
            Tile(
                id = 29,
                name = "شهرک غرب",
                type = TileType.PROPERTY,
                group = PropertyGroup.YELLOW,
                cost = 280000,
                baseRent = 24000,
                houseRents = listOf(120000, 360000, 850000, 1025000),
                hotelRent = 1200000,
                skyscraperRent = 1550000,
                houseCost = 150000
            ),
            Tile(
                id = 30,
                name = "رفتن به زندان",
                type = TileType.GO_TO_JAIL,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 31,
                name = "سعادت‌آباد",
                type = TileType.PROPERTY,
                group = PropertyGroup.GREEN,
                cost = 300000,
                baseRent = 26000,
                houseRents = listOf(130000, 390000, 900000, 1100000),
                hotelRent = 1275000,
                skyscraperRent = 1650000,
                houseCost = 200000
            ),
            Tile(
                id = 32,
                name = "کامرانیه",
                type = TileType.PROPERTY,
                group = PropertyGroup.GREEN,
                cost = 300000,
                baseRent = 26000,
                houseRents = listOf(130000, 390000, 900000, 1100000),
                hotelRent = 1275000,
                skyscraperRent = 1650000,
                houseCost = 200000
            ),
            Tile(
                id = 33,
                name = "صندوق اتفاق",
                type = TileType.COMMUNITY_CHEST,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 34,
                name = "فرمانیه",
                type = TileType.PROPERTY,
                group = PropertyGroup.GREEN,
                cost = 320000,
                baseRent = 28000,
                houseRents = listOf(150000, 450000, 1000000, 1200000),
                hotelRent = 1400000,
                skyscraperRent = 1800000,
                houseCost = 200000
            ),
            Tile(
                id = 35,
                name = "پایانه جنوب",
                type = TileType.RAILROAD,
                group = PropertyGroup.RAILROAD,
                cost = 200000,
                baseRent = 25000
            ),
            Tile(
                id = 36,
                name = "کارت شانس",
                type = TileType.CHANCE,
                group = PropertyGroup.SPECIAL
            ),
            Tile(
                id = 37,
                name = "نیاوران",
                type = TileType.PROPERTY,
                group = PropertyGroup.BLUE,
                cost = 350000,
                baseRent = 35000,
                houseRents = listOf(175000, 500000, 1100000, 1300000),
                hotelRent = 1500000,
                skyscraperRent = 2000000,
                houseCost = 200000
            ),
            Tile(
                id = 38,
                name = "مالیات تجملات",
                type = TileType.TAX,
                group = PropertyGroup.SPECIAL,
                cost = 75000
            ),
            Tile(
                id = 39,
                name = "برج میلاد",
                type = TileType.PROPERTY,
                group = PropertyGroup.BLUE,
                cost = 400000,
                baseRent = 50000,
                houseRents = listOf(200000, 600000, 1400000, 1700000),
                hotelRent = 2000000,
                skyscraperRent = 2500000,
                houseCost = 200000
            )
        )
    }

    /**
     * کارت‌های شانس و اتفاق (بیش از ۲۰ کارت کاملاً فارسی با جلوه و اثر واقعی)
     */
    fun createCards(): List<GameCard> {
        return listOf(
            // کارت‌های شانس (Chance)
            GameCard(
                id = 1,
                isChance = true,
                title = "سرمایه‌گذاری طلایی",
                description = "به دلیل سرمایه‌گذاری پربازده در بازار سهام، ۵۰٬۰۰۰ تومان سود به حساب شما واریز شد.",
                iconName = "trending_up",
                action = CardAction.AddCash(50000)
            ),
            GameCard(
                id = 2,
                isChance = true,
                title = "سرعت غیرمجاز",
                description = "به علت تخلف رانندگی و عبور از خط ویژه، ۲۵٬۰۰۰ تومان جریمه شدید.",
                iconName = "warning",
                action = CardAction.DeductCash(25000)
            ),
            GameCard(
                id = 3,
                isChance = true,
                title = "کارت آزادی از زندان",
                description = "این کارت را نزد خود نگه دارید یا در مواقع لزوم برای رهایی رایگان از بازداشتگاه استفاده کنید.",
                iconName = "lock_open",
                action = CardAction.GetOutOfJailFree
            ),
            GameCard(
                id = 4,
                isChance = true,
                title = "حرکت به سوی برج میلاد",
                description = "مستقیماً به برج میلاد، مجلل‌ترین ملک شهر ثروت نقل مکان کنید!",
                iconName = "apartment",
                action = CardAction.MoveToTile(39)
            ),
            GameCard(
                id = 5,
                isChance = true,
                title = "پیشروی تا خانه شروع",
                description = "سریعاً به خانه شروع بروید و مبلغ ۲۰۰٬۰۰۰ تومان پاداش دریافت نمایید.",
                iconName = "flag",
                action = CardAction.MoveToTile(0)
            ),
            GameCard(
                id = 6,
                isChance = true,
                title = "بازسازی و نوسازی املاک",
                description = "جهت تعمیرات شهری، برای هر خانه ۱۵٬۰۰۰ تومان و برای هر هتل ۴۰٬۰۰۰ تومان پرداخت کنید.",
                iconName = "build",
                action = CardAction.PayPerBuilding(15000, 40000)
            ),
            GameCard(
                id = 7,
                isChance = true,
                title = "توقیف خودرو و انتقال به زندان",
                description = "مستقیماً به بازداشتگاه هدایت شدید! حق عبور از شروع و دریافت پاداش را ندارید.",
                iconName = "gavel",
                action = CardAction.GoToJail
            ),
            GameCard(
                id = 8,
                isChance = true,
                title = "رونق بازار مسکن",
                description = "موجی از سرمایه‌گذاری جدید آغاز شد! کرایه‌ها افزایش چشمگیری می‌یابند.",
                iconName = "monetization_on",
                action = CardAction.TriggerGlobalEvent(GlobalEventType.MARKET_BOOM)
            ),
            GameCard(
                id = 9,
                isChance = true,
                title = "پاداش کارآفرین برتر",
                description = "شما به عنوان کارآفرین نمونه پایتخت برگزیده شدید و ۱۰۰٬۰۰۰ تومان پاداش گرفتید!",
                iconName = "emoji_events",
                action = CardAction.AddCash(100000)
            ),
            GameCard(
                id = 10,
                isChance = true,
                title = "عقب‌گرد اضطراری",
                description = "به دلیل مسدودی بزرگراه، ۳ خانه به عقب بازگردید.",
                iconName = "undo",
                action = CardAction.MoveRelative(-3)
            ),
            GameCard(
                id = 11,
                isChance = true,
                title = "سفر تفریحی به نیاوران",
                description = "جهت گذراندن تعطیلات به سمت منطقه خوش آب‌وهوای نیاوران حرکت کنید.",
                iconName = "local_florist",
                action = CardAction.MoveToTile(37)
            ),

            // کارت‌های اتفاق (Community Chest)
            GameCard(
                id = 12,
                isChance = false,
                title = "جایزه خوش‌حسابی بانک",
                description = "بانک مرکزی بابت رتبه اعتباری عالی، ۷۰٬۰۰۰ تومان هدیه به حساب شما افزود.",
                iconName = "account_balance",
                action = CardAction.AddCash(70000)
            ),
            GameCard(
                id = 13,
                isChance = false,
                title = "هزینه درمان و بیمه",
                description = "بابت هزینه‌های سالیانه درمانی و چکاپ کامل، ۴۵٬۰۰۰ تومان بپردازید.",
                iconName = "local_hospital",
                action = CardAction.DeductCash(45000)
            ),
            GameCard(
                id = 14,
                isChance = false,
                title = "جشن میلاد و هدایای دوستان",
                description = "امروز سالروز تولد شماست! از هر بازیکن مبلغ ۱۵٬۰۰۰ تومان هدیه دریافت کنید.",
                iconName = "cake",
                action = CardAction.CollectFromPlayers(15000)
            ),
            GameCard(
                id = 15,
                isChance = false,
                title = "مشارکت در امور خیریه",
                description = "جهت کمک به صندوق رفاه شهر ثروت، ۳۰٬۰۰۰ تومان به پارک آزاد اهدا کنید.",
                iconName = "volunteer_activism",
                action = CardAction.DeductCash(30000)
            ),
            GameCard(
                id = 16,
                isChance = false,
                title = "تخفیف ویژه شهرداری",
                description = "بخشودگی عوارض شهری شامل حال شما شد و ۴۰٬۰۰۰ تومان مسترد گردید.",
                iconName = "stars",
                action = CardAction.AddCash(40000)
            ),
            GameCard(
                id = 17,
                isChance = false,
                title = "جشنواره بزرگ خرید",
                description = "جشنواره فصلی تخفیف املاک در سراسر شهر ثروت کلید خورد!",
                iconName = "shopping_bag",
                action = CardAction.TriggerGlobalEvent(GlobalEventType.SHOPPING_FESTIVAL)
            ),
            GameCard(
                id = 18,
                isChance = false,
                title = "سود مشارکت در نیروگاه",
                description = "بهره‌برداری از پروژه‌های زیرساختی ۸۵٬۰۰۰ تومان درآمد برایتان به همراه داشت.",
                iconName = "bolt",
                action = CardAction.AddCash(85000)
            ),
            GameCard(
                id = 19,
                isChance = false,
                title = "پاداش سهامداران",
                description = "مجمع عمومی شرکت‌ها تصمیم گرفت به شما مبلغ ۵۵٬۰۰۰ تومان سود نقدی پرداخت کند.",
                iconName = "card_giftcard",
                action = CardAction.AddCash(55000)
            ),
            GameCard(
                id = 20,
                isChance = false,
                title = "عوارض اضطراری شهری",
                description = "شورای شهر عوارض غیرمنتظره‌ای برای بازسازی معابر وضع کرده است.",
                iconName = "assignment_late",
                action = CardAction.TriggerGlobalEvent(GlobalEventType.EMERGENCY_TAX)
            ),
            GameCard(
                id = 21,
                isChance = false,
                title = "شانس بزرگ سرمایه‌گذاری",
                description = "یکی از املاک شما ناگهان با رشد انفجاری مواجه شد و ۱۲۰٬۰۰۰ تومان سود آورد!",
                iconName = "diamond",
                action = CardAction.AddCash(120000)
            ),
            GameCard(
                id = 22,
                isChance = false,
                title = "تعمیر تأسیسات و لوله‌کشی",
                description = "برای هر خانه ۱۰٬۰۰۰ تومان و برای هر هتل ۲۵٬۰۰۰ تومان هزینه عایق‌بندی پرداخت کنید.",
                iconName = "plumbing",
                action = CardAction.PayPerBuilding(10000, 25000)
            )
        )
    }

    /**
     * دستاوردهای جامع بازی
     */
    fun createDefaultAchievements(): List<Achievement> {
        return listOf(
            Achievement("first_property", "اولین سند مالکیت", "خریداری نخستین ملک در شهر ثروت", "home", maxProgress = 1),
            Achievement("millionaire", "سرمایه‌دار بزرگ", "رسیدن به ثروت خالص بیش از ۳٬۰۰۰٬۰۰۰ تومان", "military_tech", maxProgress = 1),
            Achievement("monopoly_king", "سلطان املاک", "تصاحب تمام املاک یک گروه رنگی و ایجاد مونوپولی", "workspace_premium", maxProgress = 1),
            Achievement("master_trader", "معامله‌گر حرفه‌ای", "انجام ۳ معامله موفقیت‌آمیز با سایر بازیکنان", "handshake", maxProgress = 3),
            Achievement("lucky_roller", "خوش‌شانس پایتخت", "آوردن تاس جفت و یا برد در صندوق پارک آزاد", "casino", maxProgress = 1),
            Achievement("hotel_magnate", "برج‌ساز نامدار", "ساختن حداقل ۲ هتل مجلل در املاک شخصی", "domain", maxProgress = 2),
            Achievement("deal_maker", "استاد مذاکره", "موفقیت در خرید یک ملک از طریق حراج رقابتی", "gavel", maxProgress = 1),
            Achievement("undefeated", "شکست‌ناپذیر", "پیروزی در بازی بدون نیاز به رهن گذاشتن حتی یک ملک", "shield", maxProgress = 1),
            Achievement("billionaire", "میلیاردر شهر ثروت", "رسیدن به مجموع نقدینگی و دارایی بالای ۱۰٬۰۰۰٬۰۰۰ تومان", "diamond", maxProgress = 1),
            Achievement("champion", "قهرمان شهر ثروت", "پیروزی قاطع در برابر تمامی رقبای بازی رومیزی", "trophy", maxProgress = 1)
        )
    }

    /**
     * مهره‌ها، آواتارها و رنگ‌های پیش‌فرض
     */
    val AVATARS = listOf("👑", "🎩", "🚗", "🚀", "💎", "🦁", "🏆", "🌟")

    val PLAYER_COLORS = listOf(
        0xFFE53935, // قرمز یاقوتی
        0xFF1E88E5, // آبی رویال
        0xFF43A047, // سبز زمردی
        0xFFFB8C00, // نارنجی کهربایی
        0xFF8E24AA, // بنفش اشرافی
        0xFF00ACC1  // فیروزه‌ای خلیج
    )

    val DEFAULT_NAMES = listOf(
        "آرش",
        "سارا",
        "کیان",
        "نیلوفر",
        "بهرام",
        "پریناز"
    )
}
