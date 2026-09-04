package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel

private data class RuleSection(
    val icon: String,
    val title: String,
    val content: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesScreen(
    viewModel: GameViewModel
) {
    val rules = listOf(
        RuleSection(
            icon = "🎯",
            title = "هدف کلی بازی شهر ثروت",
            content = "هدف بازی تبدیل شدن به قدرتمندترین سرمایه‌گذار شهر از طریق خرید زمین‌ها، خیابان‌ها، ایستگاه‌ها، ساخت خانه‌ها و هتل‌ها و ورشکست کردن رقبا تا رسیدن به عنوان تک‌قهرمان بازی است."
        ),
        RuleSection(
            icon = "🎲",
            title = "پرتاب تاس و حرکت",
            content = "هر بازیکن در نوبت خود دو تاس را پرتاب می‌کند و به اندازه مجموع آن‌ها روی تخته در جهت عقربه‌های ساعت حرکت می‌کند. اگر تاس جفت بیاید، بازیکن یک نوبت تاس‌ریزی پاداش می‌گیرد. اما اگر ۳ بار متوالی تاس جفت بیاورید، مستقیماً به بازداشتگاه فرستاده می‌شوید!"
        ),
        RuleSection(
            icon = "🏁",
            title = "عبور از خانه شروع",
            content = "هر بار که مهره شما از خانه شروع عبور کند یا مستقیماً روی آن متوقف شود، مبلغ ۲۰۰٬۰۰۰ تومان پاداش نقدی از بانک شهر دریافت خواهید کرد."
        ),
        RuleSection(
            icon = "📜",
            title = "خرید املاک و حراج عمومی",
            content = "هنگامی که روی ملکی بدون مالک متوقف شوید، می‌توانید سند آن را به قیمت مصوب بخرید. در صورتی که نخواهید یا نتوانید آن را بخرید، ملک به حراج عمومی گذاشته می‌شود و همه بازیکنان می‌توانند با ارائه بالاترین پیشنهاد، آن را تصاحب کنند."
        ),
        RuleSection(
            icon = "👑",
            title = "مونوپولی (تصاحب کل گروه رنگی)",
            content = "هرگاه تمام املاک یک گروه رنگی هم‌رنگ (مثلاً هر ۳ خیابان زرد یا هر ۲ خیابان سرمه‌ای) متعلق به یک بازیکن باشد، اجاره پایه زمین‌های خام آن گروه دو برابر می‌شود و مالک اجازه ساخت‌وساز پیدا می‌کند."
        ),
        RuleSection(
            icon = "🏡",
            title = "ساخت خانه، هتل و آسمان‌خراش",
            content = "پس از ساخت مونوپولی، می‌توانید روی املاک خود تا ۴ خانه بنا کنید. با پرداخت هزینه خانه پنجم، خانه‌ها به یک هتل لوکس تبدیل می‌شوند که سود اجاره را به اوج می‌رساند. در نسخه ویژه شهر ثروت، قابلیت احداث آسمان‌خراش سلطنتی نیز فراهم است!"
        ),
        RuleSection(
            icon = "🔒",
            title = "قوانین زندان و رهایی",
            content = "بازیکن به ۳ روش راهی زندان می‌شود: فرود روی خانه رفتن به زندان، کشیدن کارت زندان، یا ۳ تاس جفت متوالی. برای آزادی می‌توانید ۵۰٬۰۰۰ تومان وثیقه بپردازید، از کارت آزادی استفاده کنید، یا در ۳ نوبت متوالی شانس خود را برای آوردن تاس جفت امتحان کنید."
        ),
        RuleSection(
            icon = "🤝",
            title = "معامله و تبادل دارایی‌ها",
            content = "در نوبت خود می‌توانید با هر یک از رقبای دیگر یا هوش مصنوعی وارد مذاکره شوید و پیشنهاد تبادل ملک و پول نقد ارائه دهید. هوش مصنوعی استراتژی‌های پیشرفته‌ای برای ارزیابی سودآوری پیشنهادات دارد."
        ),
        RuleSection(
            icon = "🎁",
            title = "صندوق پارک آزاد و کارت‌ها",
            content = "تمام جرایم و مالیات‌های پرداخت‌شده به صندوق پارک آزاد افزوده می‌شوند. هر بازیکنی که روی پارک آزاد بایستد، صاحب تمام موجودی انباشته این صندوق خواهد شد!"
        ),
        RuleSection(
            icon = "💥",
            title = "ورشکستگی و پایان بازی",
            content = "اگر بازیکنی بدهکار شود و نقدینگی و ارزش فروش املاک او برای تسویه حساب کافی نباشد، ورشکسته اعلام شده و از بازی خارج می‌شود. آخرین بازیکن باقی‌مانده برنده بزرگ شهر ثروت خواهد بود."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "راهنما و قوانین بازی",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.navigateTo(ScreenRoute.MAIN_MENU) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDark)
            )
        },
        containerColor = NavyDarkest
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rules) { rule ->
                RuleCard(rule = rule)
            }
        }
    }
}

@Composable
private fun RuleCard(rule: RuleSection) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, NavyBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = NavyCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = rule.icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = rule.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = rule.content,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 22.sp
            )
        }
    }
}
