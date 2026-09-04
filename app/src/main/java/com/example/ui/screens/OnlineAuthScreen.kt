package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScreenRoute
import com.example.ui.theme.*
import com.example.viewmodel.GameViewModel
import kotlinx.coroutines.launch

/**
 * صفحه ورود و ثبت‌نام آنلاین با تم لوکس تیره (Elegant Dark)
 */
@Composable
fun OnlineAuthScreen(
    viewModel: GameViewModel
) {
    val onlineManager = viewModel.onlineManager
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var isRegisterMode by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedAvatar by remember { mutableStateOf("👑") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val avatarList = listOf("👑", "🦁", "💎", "🏎️", "🎩", "💼", "🏰", "🦅")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
            }
            .background(
                Brush.verticalGradient(
                    colors = listOf(ElegantDarkBg, Color(0xFF161B33), ElegantDarkBg)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // نوار بازگشت
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.navigateTo(ScreenRoute.MAIN_MENU)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0x14FFFFFF))
                ) {
                    Text(text = "✕", fontSize = 18.sp, color = TextPrimary)
                }

                // وضعیت سرور فایربیس
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (onlineManager.isFirebaseConfigured)
                                Color(0x2610B981)
                            else
                                Color(0x2638BDF8)
                        )
                        .border(
                            1.dp,
                            if (onlineManager.isFirebaseConfigured) EmeraldProfit else Color(0xFF38BDF8),
                            CircleShape
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (onlineManager.isFirebaseConfigured) "● سرور ابری فعال" else "● سرور شبیه‌ساز فعال",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (onlineManager.isFirebaseConfigured) EmeraldProfit else Color(0xFF38BDF8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // لوگوی شاخص شهر ثروت
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AmberPrimary, AmberYellowGold)
                        )
                    )
                    .shadow(16.dp, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🌐", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "باشگاه آنلاین شهر ثروت",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "ثبت‌نام کنید و با سرمایه‌گذاران سراسر کشور رقابت نمایید",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // تب‌های سوییچ بین ورود و ثبت‌نام
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x14FFFFFF))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isRegisterMode) AmberPrimary else Color.Transparent)
                        .clickable {
                            focusManager.clearFocus()
                            isRegisterMode = true
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ثبت‌نام کاربر جدید",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRegisterMode) ElegantDarkBg else TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (!isRegisterMode) AmberPrimary else Color.Transparent)
                        .clickable {
                            focusManager.clearFocus()
                            isRegisterMode = false
                            errorMessage = null
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ورود به حساب",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isRegisterMode) ElegantDarkBg else TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // کارت فرم ورودی
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, ElegantCardBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = ElegantCardBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (isRegisterMode) {
                        // انتخاب آواتار
                        Text(
                            text = "نشان و نماد اختصاصی شما:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            avatarList.forEach { av ->
                                val isSelected = av == selectedAvatar
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) AmberPrimary else Color(0x14FFFFFF))
                                        .border(
                                            1.dp,
                                            if (isSelected) AmberYellowGold else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable {
                                            focusManager.clearFocus()
                                            selectedAvatar = av
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = av, fontSize = 18.sp)
                                }
                            }
                        }

                        // فیلد نام کاربری
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("نام نمایشی یا لقب تجاری") },
                            placeholder = { Text("مثال: مهرداد سرمایه‌دار") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AmberPrimary,
                                unfocusedBorderColor = Color(0x33FFFFFF)
                            )
                        )
                    }

                    // فیلد ایمیل
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("آدرس ایمیل") },
                        placeholder = { Text("example@domain.com") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberPrimary,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )

                    // فیلد رمز عبور
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("گذرواژه (حداقل ۶ نویسه)") },
                        placeholder = { Text("••••••••") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(text = if (passwordVisible) "👁️" else "🔒", fontSize = 16.sp)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AmberPrimary,
                            unfocusedBorderColor = Color(0x33FFFFFF)
                        )
                    )

                    // نمایش پیام خطا
                    errorMessage?.let { err ->
                        Text(
                            text = err,
                            fontSize = 11.sp,
                            color = CrimsonLoss,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // دکمه اقدام اصلی
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            errorMessage = null
                            if (isRegisterMode) {
                                if (name.isBlank()) {
                                    errorMessage = "لطفاً نام نمایشی خود را وارد کنید"
                                    return@Button
                                }
                                if (email.isBlank() || !email.contains("@")) {
                                    errorMessage = "لطفاً یک آدرس ایمیل معتبر وارد کنید"
                                    return@Button
                                }
                                if (password.length < 6) {
                                    errorMessage = "گذرواژه باید حداقل ۶ حرف باشد"
                                    return@Button
                                }

                                isLoading = true
                                coroutineScope.launch {
                                    val res = onlineManager.registerUser(name, email, password, selectedAvatar)
                                    isLoading = false
                                    if (res.isSuccess) {
                                        viewModel.navigateTo(ScreenRoute.ONLINE_LOBBY)
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.message ?: "خطا در ثبت‌نام"
                                    }
                                }
                            } else {
                                if (email.isBlank() || password.isBlank()) {
                                    errorMessage = "ایمیل و گذرواژه را وارد کنید"
                                    return@Button
                                }
                                isLoading = true
                                coroutineScope.launch {
                                    val res = onlineManager.loginUser(email, password)
                                    isLoading = false
                                    if (res.isSuccess) {
                                        viewModel.navigateTo(ScreenRoute.ONLINE_LOBBY)
                                    } else {
                                        errorMessage = res.exceptionOrNull()?.message ?: "خطا در ورود"
                                    }
                                }
                            }
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmberPrimary,
                            contentColor = ElegantDarkBg
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = ElegantDarkBg)
                        } else {
                            Text(
                                text = if (isRegisterMode) "ایجاد حساب و ورود به لابی" else "ورود به باشگاه بازی",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ورود مهمان بدون ثبت‌نام
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    onlineManager.loginAsGuest(
                        guestName = if (name.isNotBlank()) name else "بازیکن مهمان",
                        avatar = selectedAvatar
                    )
                    viewModel.navigateTo(ScreenRoute.ONLINE_LOBBY)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0x14FFFFFF),
                    contentColor = TextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x26FFFFFF))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "⚡", fontSize = 18.sp)
                    Text(
                        text = "ورود فوری بدون ثبت‌نام (حساب مهمان)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
