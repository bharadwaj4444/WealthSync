package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Geometric Balance Color Palette

// Primary Accent Blue
val GeometricPrimaryBlue = Color(0xFF0061A4)
val GeometricOnPrimary = Color(0xFFFFFFFF)

// Soft Blue Container Accent
val GeometricBlueContainer = Color(0xFFD6E3FF)
val GeometricOnBlueContainer = Color(0xFF001B3E)

// Base Background & Surface (Light Mode)
val GeometricBackgroundLight = Color(0xFFFCFCFF)
val GeometricOnBackgroundLight = Color(0xFF1A1C1E)
val GeometricSurfaceLight = Color(0xFFFFFFFF)
val GeometricOnSurfaceLight = Color(0xFF1A1C1E)

// Card Layout Materials
val GeometricCardBackground = Color(0xFFF2F0F4) // Cool light-grey surfaces
val GeometricCardBorder = Color(0xFFC4C6CF)     // Geometric outline color
val GeometricOnCardText = Color(0xFF44474E)     // Subtext labels

// Rebalance & Tax Efficiency Accent Grid
val GeometricRebalanceRed = Color(0xFFBA1A1A)
val GeometricRebalanceRedContainer = Color(0xFFFFDAD6)

val GeometricTaxTeal = Color(0xFF006A6A)
val GeometricTaxTealContainer = Color(0xFFBCEBEB)

// Dark Contrasts (Quick Insights & Midnight Themes)
val GeometricDarkAccent = Color(0xFF1A1C1E)
val GeometricOnDarkAccentText = Color(0xFFD1E4FF)

// Dark Theme counterparts
val GeometricBackgroundDark = Color(0xFF111318)
val GeometricOnBackgroundDark = Color(0xFFE2E2E6)
val GeometricSurfaceDark = Color(0xFF1F2024)
val GeometricCardBackgroundDark = Color(0xFF2C2D31)

// Composable-driven Dynamic Bindings for Legacy Palette to achieve 100% theme fluidity:
val EmeraldNeon: Color
    @Composable
    get() = MaterialTheme.colorScheme.primary

val SageDarkSecondary: Color
    @Composable
    get() = MaterialTheme.colorScheme.secondary

val EmeraldMuted: Color
    @Composable
    get() = MaterialTheme.colorScheme.tertiary

val ObsidianDark: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val SlateMedium: Color
    @Composable
    get() = if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF2C2D31) else Color(0xFFD6E3FF)

val SlateCard: Color
    @Composable
    get() = MaterialTheme.colorScheme.surfaceVariant

val GoldAccent: Color
    @Composable
    get() = if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFFFFD043) else Color(0xFF0061A4)

val CoralAlert: Color
    @Composable
    get() = MaterialTheme.colorScheme.error

val PlatinumWhite: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurface

val SilverSlate: Color
    @Composable
    get() = MaterialTheme.colorScheme.onSurfaceVariant
