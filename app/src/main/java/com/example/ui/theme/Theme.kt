package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = GeometricPrimaryBlue,
    secondary = GeometricBlueContainer,
    tertiary = GeometricTaxTeal,
    background = GeometricBackgroundDark,
    surface = GeometricSurfaceDark,
    onPrimary = GeometricDarkAccent,
    onSecondary = GeometricDarkAccent,
    onBackground = GeometricOnBackgroundDark,
    onSurface = GeometricOnBackgroundDark,
    surfaceVariant = GeometricCardBackgroundDark,
    onSurfaceVariant = GeometricOnDarkAccentText,
    outline = GeometricCardBorder,
    error = GeometricRebalanceRed
)

private val LightColorScheme = lightColorScheme(
    primary = GeometricPrimaryBlue,
    secondary = GeometricBlueContainer,
    tertiary = GeometricTaxTeal,
    background = GeometricBackgroundLight,
    surface = GeometricSurfaceLight,
    onPrimary = GeometricOnPrimary,
    onSecondary = GeometricOnBlueContainer,
    onBackground = GeometricOnBackgroundLight,
    onSurface = GeometricOnSurfaceLight,
    surfaceVariant = GeometricCardBackground,
    onSurfaceVariant = GeometricOnCardText,
    outline = GeometricCardBorder,
    error = GeometricRebalanceRed,
    onError = GeometricOnPrimary,
    errorContainer = GeometricRebalanceRedContainer,
    onErrorContainer = GeometricRebalanceRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We prioritize our Geometric Balance custom theme configuration
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
