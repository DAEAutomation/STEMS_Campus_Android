package com.dae.stems_campus.ui.components

import androidx.compose.ui.res.stringResource
import com.dae.stems_campus.R

sealed class NavigationItem(
    val route: String,
    val labelResId: Int,
    val navTitle: String,
    val select_icon: Int,
    val unSelect_icon: Int
) {
    object Home: NavigationItem(
        route="home",
        labelResId= R.string.home,
        navTitle = "主頁",
        select_icon= R.drawable.house_g,
        unSelect_icon = R.drawable.house
    )
    object Wallet: NavigationItem(
        route="wallet",
        labelResId= R.string.wallet,
        navTitle = "錢包",
        select_icon= R.drawable.wallet_g,
        unSelect_icon = R.drawable.wallet
    )
    object History: NavigationItem(
        route="history",
        labelResId= R.string.history,
        navTitle = "紀錄",
        select_icon= R.drawable.notebook_g,
        unSelect_icon = R.drawable.notebook
    )
    object Setting: NavigationItem(
        route="setting",
        labelResId= R.string.settings,
        navTitle = "設定",
        select_icon= R.drawable.usergear_g,
        unSelect_icon = R.drawable.usergear
    )
}