package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.text.DecimalFormat

object PersianUtils {

    private val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    /**
     * تبدیل اعداد انگلیسی به ارقام فارسی
     */
    fun toPersianDigits(number: Any): String {
        val str = number.toString()
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * فرمت‌بندی مبالغ پولی با جداکننده سه‌رقمی و نماد تومان فارسی
     */
    fun formatCurrency(amount: Long, includeUnit: Boolean = true): String {
        val formatter = DecimalFormat("#,###")
        val formatted = formatter.format(amount)
        val persianFormatted = toPersianDigits(formatted)
        return if (includeUnit) "$persianFormatted تومان" else persianFormatted
    }

    fun formatCurrency(amount: Int, includeUnit: Boolean = true): String {
        return formatCurrency(amount.toLong(), includeUnit)
    }

    /**
     * فرمت تعداد یا شمارنده با ارقام فارسی
     */
    fun formatNumber(number: Int): String {
        val formatter = DecimalFormat("#,###")
        return toPersianDigits(formatter.format(number))
    }
}

/**
 * ایجاد ساختار کاملاً راست‌به‌چپ (RTL) برای رابط کاربری فارسی
 */
@Composable
fun ProvidePersianRtlLayout(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        content()
    }
}
