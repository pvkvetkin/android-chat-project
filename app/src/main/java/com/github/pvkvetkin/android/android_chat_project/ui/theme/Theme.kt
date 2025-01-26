package com.github.pvkvetkin.android.android_chat_project.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1E88E5),
    onPrimary = Color.White,
    secondary = Color(0xFF6D4C41),
    onSecondary = Color.White,
    background = Color(0xFFFDFDFD),
    onBackground = Color(0xFF1B1B1B),
    surface = Color.White,
    onSurface = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color.Black,
    secondary = Color(0xFFBCAAA4),
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFECECEC),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFECECEC)
)

@Composable
fun AndroidchatprojectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
