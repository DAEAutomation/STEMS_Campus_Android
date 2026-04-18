package com.dae.stems_campus.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.dae.stems_campus.R

// 定義 Noto Sans TC FontFamily
val NotoSansTC = FontFamily(
    Font(R.font.noto_sans_tc_regular, FontWeight.Normal),
    Font(R.font.noto_sans_tc_bold, FontWeight.Bold)
)

// 用預設 Typography 套用 NotoSansTC 字型
private val defaultTypography = Typography()

val Typography = Typography(
    displayLarge   = defaultTypography.displayLarge.copy(fontFamily = NotoSansTC),
    displayMedium  = defaultTypography.displayMedium.copy(fontFamily = NotoSansTC),
    displaySmall   = defaultTypography.displaySmall.copy(fontFamily = NotoSansTC),
    headlineLarge  = defaultTypography.headlineLarge.copy(fontFamily = NotoSansTC),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = NotoSansTC),
    headlineSmall  = defaultTypography.headlineSmall.copy(fontFamily = NotoSansTC),
    titleLarge     = defaultTypography.titleLarge.copy(fontFamily = NotoSansTC),
    titleMedium    = defaultTypography.titleMedium.copy(fontFamily = NotoSansTC),
    titleSmall     = defaultTypography.titleSmall.copy(fontFamily = NotoSansTC),
    bodyLarge      = defaultTypography.bodyLarge.copy(fontFamily = NotoSansTC),
    bodyMedium     = defaultTypography.bodyMedium.copy(fontFamily = NotoSansTC),
    bodySmall      = defaultTypography.bodySmall.copy(fontFamily = NotoSansTC),
    labelLarge     = defaultTypography.labelLarge.copy(fontFamily = NotoSansTC),
    labelMedium    = defaultTypography.labelMedium.copy(fontFamily = NotoSansTC),
    labelSmall     = defaultTypography.labelSmall.copy(fontFamily = NotoSansTC),
)
