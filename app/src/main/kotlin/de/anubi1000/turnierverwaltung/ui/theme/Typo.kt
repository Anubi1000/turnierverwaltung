package de.anubi1000.turnierverwaltung.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

private val inter = FontFamily(
    listOf(FontWeight.W100, FontWeight.W200, FontWeight.W300, FontWeight.W400, FontWeight.W500, FontWeight.W600, FontWeight.W700, FontWeight.W800, FontWeight.W900).map { weight ->
        Font(
            resource = "fonts/inter/${weight.weight}.ttf",
            weight = weight,
            style = FontStyle.Normal,
        )
    },
)

private val base = Typography()

val AppTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = inter),
    displayMedium = base.displayMedium.copy(fontFamily = inter),
    displaySmall = base.displaySmall.copy(fontFamily = inter),
    headlineLarge = base.headlineLarge.copy(fontFamily = inter),
    headlineMedium = base.headlineMedium.copy(fontFamily = inter),
    headlineSmall = base.headlineSmall.copy(fontFamily = inter),
    titleLarge = base.titleLarge.copy(fontFamily = inter),
    titleMedium = base.titleMedium.copy(fontFamily = inter),
    titleSmall = base.titleSmall.copy(fontFamily = inter),
    bodyLarge = base.bodyLarge.copy(fontFamily = inter),
    bodyMedium = base.bodyMedium.copy(fontFamily = inter),
    bodySmall = base.bodySmall.copy(fontFamily = inter),
    labelLarge = base.labelLarge.copy(fontFamily = inter),
    labelMedium = base.labelMedium.copy(fontFamily = inter),
    labelSmall = base.labelSmall.copy(fontFamily = inter),
)
