package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun DicePairView(
    dice1: Int,
    dice2: Int,
    isRolling: Boolean,
    onRollClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dice_roll")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(250, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dice_spin"
    )

    val currentRotation1 = if (isRolling) rotation else 0f
    val currentRotation2 = if (isRolling) -rotation else 0f

    Row(
        modifier = modifier
            .clickable(enabled = enabled && !isRolling) { onRollClick() }
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SingleDieView(value = dice1, rotationAngle = currentRotation1)
        SingleDieView(value = dice2, rotationAngle = currentRotation2)
    }
}

@Composable
fun SingleDieView(
    value: Int,
    rotationAngle: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(54.dp)
            .rotate(rotationAngle)
            .shadow(12.dp, RoundedCornerShape(14.dp))
            .background(Color.White, RoundedCornerShape(14.dp))
            .border(2.dp, Color(0xFFE2E8F0), RoundedCornerShape(14.dp))
            .padding(bottom = 3.dp) // creates the 3d bottom border feel
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        DicePips(value = value)
    }
}

@Composable
private fun DicePips(value: Int) {
    val pipColor = ElegantDarkBg // کدر سرمه‌ای تیره مطابق دیزاین
    val dotSize = 8.dp

    when (value) {
        1 -> {
            Box(
                modifier = Modifier
                    .size(dotSize * 1.5f)
                    .background(Color(0xFFB91C1C), CircleShape)
            )
        }
        2 -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
            }
        }
        3 -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
            }
        }
        4 -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
            }
        }
        5 -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                        Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                        Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(dotSize)
                        .background(pipColor, CircleShape)
                )
            }
        }
        6 -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                    Box(modifier = Modifier.size(dotSize).background(pipColor, CircleShape))
                }
            }
        }
        else -> {
            Text(
                text = PersianUtils.toPersianDigits(value),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = pipColor
            )
        }
    }
}
