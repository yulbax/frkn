package io.github.yulbax.frkn.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.yulbax.frkn.R


private val GoogleSans = FontFamily(
    Font(R.font.google_sans_regular, FontWeight.Normal),
    Font(R.font.google_sans_medium, FontWeight.Medium),
    Font(R.font.google_sans_bold, FontWeight.Bold)
)


private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = GoogleSans),
    displayMedium = base.displayMedium.copy(fontFamily = GoogleSans),
    displaySmall = base.displaySmall.copy(fontFamily = GoogleSans),
    headlineLarge = base.headlineLarge.copy(fontFamily = GoogleSans),
    headlineMedium = base.headlineMedium.copy(fontFamily = GoogleSans),
    headlineSmall = base.headlineSmall.copy(fontFamily = GoogleSans),
    titleLarge = base.titleLarge.copy(fontFamily = GoogleSans),
    titleMedium = base.titleMedium.copy(fontFamily = GoogleSans),
    titleSmall = base.titleSmall.copy(fontFamily = GoogleSans),
    bodyLarge = base.bodyLarge.copy(fontFamily = GoogleSans),
    bodyMedium = base.bodyMedium.copy(fontFamily = GoogleSans),
    bodySmall = base.bodySmall.copy(fontFamily = GoogleSans),
    labelLarge = base.labelLarge.copy(fontFamily = GoogleSans),
    labelMedium = base.labelMedium.copy(fontFamily = GoogleSans),
    labelSmall = base.labelSmall.copy(fontFamily = GoogleSans)
)
