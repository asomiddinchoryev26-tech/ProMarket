package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SteelPrimary,
    secondary = SteelSecondary,
    tertiary = SteelTertiary,
    background = ObsidianBackground,
    surface = ObsidianSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = ObsidianText,
    onSurface = ObsidianText
)

private val LightColorScheme = lightColorScheme(
    primary = LightCopper,
    secondary = SteelSecondary,
    tertiary = SteelTertiary,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = LightText,
    onTertiary = Color.Black,
    onBackground = LightText,
    onSurface = LightText
)

@Composable
fun MarketplaceTheme(
    darkTheme: Boolean = true, // Default to stunning dark mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
