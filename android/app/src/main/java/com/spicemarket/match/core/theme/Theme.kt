package com.spicemarket.match.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SpiceColorScheme = lightColorScheme(
    primary = SpicePrimary,
    onPrimary = SpiceOnDark,
    primaryContainer = SpicePrimaryDeep,
    onPrimaryContainer = SpiceOnDark,
    secondary = SpiceAccent,
    onSecondary = SpiceTextPrimary,
    secondaryContainer = SpiceAccentDeep,
    onSecondaryContainer = SpiceTextPrimary,
    tertiary = SpiceTerracotta,
    onTertiary = SpiceOnDark,
    background = SpiceBackground,
    onBackground = SpiceTextPrimary,
    surface = SpiceSurface,
    onSurface = SpiceTextPrimary,
    surfaceVariant = SpiceSurfaceVariant,
    onSurfaceVariant = SpiceTextPrimary,
    outline = SpiceSurfaceVariant,
    error = SpicePrimary,
    onError = SpiceOnDark
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SpiceColorScheme,
        typography = AppTypography,
        content = content
    )
}
