package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val logoScale = remember { Animatable(0.2f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val splashAlpha = remember { Animatable(1f) }

    // Pulse animation for background glow
    val infiniteTransition = rememberInfiniteTransition(label = "Glow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    LaunchedEffect(Unit) {
        // Animate Logo
        launch {
            logoAlpha.animateTo(1f, animationSpec = tween(600))
        }
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

        // Animate Main Text
        delay(300)
        textAlpha.animateTo(1f, animationSpec = tween(700))

        // Animate Subtitle
        delay(200)
        subtitleAlpha.animateTo(1f, animationSpec = tween(500))

        // Wait before fade out
        delay(1800)

        // Fade Out Splash
        splashAlpha.animateTo(0f, animationSpec = tween(500))
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(splashAlpha.value)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B1120),
                        Color(0xFF0F172A),
                        Color(0xFF0284C7),
                        Color(0xFF0B1120)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Outer Glowing Aura
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(pulseScale)
                .alpha(pulseAlpha)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.8f),
                            Color(0xFF0284C7).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Animated Logo Container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF0284C7), Color(0xFF2563EB), Color(0xFF1D4ED8))
                        )
                    )
                    .border(3.dp, Color(0xFF38BDF8), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Facebook,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Text: ARAFAT FB CREATTOR
            Text(
                text = "ARAFAT FB CREATTOR",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Glowing Badge line
            Box(
                modifier = Modifier
                    .alpha(subtitleAlpha.value)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.8f))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "⚡ AUTOMATED FB CREATION TOOL ⚡",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
