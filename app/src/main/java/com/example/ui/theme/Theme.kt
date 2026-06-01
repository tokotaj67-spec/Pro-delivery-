package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = FoodOrangePrimary,
    secondary = FoodOrangeSecondary,
    tertiary = FoodYellowRating,
    background = FoodDarkGray,
    surface = Color(0xFF282C34),
    onPrimary = FoodWhite,
    onSecondary = FoodWhite,
    onBackground = FoodWhite,
    onSurface = FoodWhite
)

private val LightColorScheme = lightColorScheme(
    primary = FoodOrangePrimary,
    secondary = FoodOrangeSecondary,
    tertiary = FoodYellowRating,
    background = FoodOffWhite,
    surface = FoodWhite,
    onPrimary = FoodWhite,
    onSecondary = FoodWhite,
    onBackground = FoodTextPrimary,
    onSurface = FoodTextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled by default to force our premium crafted brand colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
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
