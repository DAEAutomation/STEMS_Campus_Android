package com.dae.stems_campus.ui.screen

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.ui.components.NavigationItem
import com.dae.stems_campus.ui.screen.history.historyScreen
import com.dae.stems_campus.ui.screen.home.HomeScreen
import com.dae.stems_campus.ui.screen.pay.walletScreen
import com.dae.stems_campus.ui.screen.setting.settingScreen

@Composable
fun mainTabScreen(mainNavController: NavController) {

    val context = LocalContext.current
    val activity = context as? Activity

    val bottomBarNavController = rememberNavController()
    // 創建一個狀態來保存當前頁面的標題
    val currentTitle = rememberSaveable { mutableStateOf(NavigationItem.Home.navTitle) }
    // 控制 bottomBar 是否顯示（子頁進入時隱藏）
    val showTabBar = rememberSaveable { mutableStateOf(true) }

    // 目前所在的 tab route（子頁時 tab bar 已隱藏，這裡仍是該 tab 的 route）
    val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Home tab 最上層：兩秒內再按一次實體 back 才離開 App。
    // 放在最外層 → 優先權低於各內層 NavHost，子頁的 back 仍由內層處理。
    val backPressedTime = remember { mutableStateOf(0L) }
    BackHandler(enabled = currentRoute == NavigationItem.Home.route) {
        val now = System.currentTimeMillis()
        if (now - backPressedTime.value < 2000) {
            activity?.finish()
        } else {
            backPressedTime.value = now
            Toast.makeText(context, "再按一次離開", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            if (showTabBar.value) {
                BottomNavigationBar(bottomBarNavController, onItemSelected = { title ->
                    // 更新標題
                    currentTitle.value = title
                })
            }
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Navigation(
                    bottomBarNavHostController = bottomBarNavController,
                    mainNavController = mainNavController,
                    startRote = NavigationItem.Home.route,
                    onShowTabBarChange = { showTabBar.value = it })
            }
        },
        containerColor = Color(0xFFF4F4F4) // Set background color to avoid the white flashing when you switch between screens
    )
}



@Composable
private fun BottomNavigationBar(bottomBarNavController: NavController, onItemSelected: (String) -> Unit) {
    var items: MutableList<NavigationItem>

    items = listOf(
        NavigationItem.Home,
        NavigationItem.Wallet,
        NavigationItem.History,
        NavigationItem.Setting,
    ).toMutableList()

    val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    // 監聽 currentRoute 的變化
    LaunchedEffect(currentRoute) {
        Log.d("BottomNavigation", "currentRoute updated to: $currentRoute")
    }
    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = colorResource(id = R.color.white),
        contentColor = Color.White
    ) {

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    // 根據是否選中顯示不同的圖示
                    val icon = if (currentRoute == item.route) {
                        painterResource(id = item.select_icon) // 選中時的圖示
                    } else {
                        painterResource(id = item.unSelect_icon) // 未選中時的圖示
                    }
                    Icon(painter = icon, contentDescription = stringResource(item.labelResId),tint = Color.Unspecified)
                },
                label = { Text(text = stringResource(item.labelResId), style = MaterialTheme.typography.labelSmall) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Unspecified,
                    unselectedIconColor = Color.Unspecified,
                    selectedTextColor = Color(0xFF2D859D),
                    unselectedTextColor = Color.Black,
                    indicatorColor = Color.Transparent
                ),
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    Log.d("BottomNavigation", "currentRoute: $currentRoute")
                    bottomBarNavController.navigate(item.route) {
//                        mainTabViewModel.updateSearchWidgetState(SearchWidgetState.CLOSED)
                        onItemSelected(item.navTitle)
                        // back stack 永遠維持 [Home, 當前tab]：
                        // pop 到 Home（不含 Home）→ 任何 tab 按實體 back 都一步回 Home。
                        popUpTo(NavigationItem.Home.route) { inclusive = false }
                        // 避免同一頁重複堆疊
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}


@Composable
private fun Navigation(
    bottomBarNavHostController: NavHostController,
    mainNavController: NavController,
    startRote: String,
    onShowTabBarChange: (Boolean) -> Unit
) {
    NavHost(bottomBarNavHostController, startDestination = startRote) {
        composable(NavigationItem.Home.route) {
            HomeScreen(
                mainNavController = mainNavController,
                onNavigateToSetting = {
                    bottomBarNavHostController.navigate(NavigationItem.Setting.route) {
                        popUpTo(NavigationItem.Setting.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToWallet = {
                    bottomBarNavHostController.navigate(NavigationItem.Wallet.route) {
                        popUpTo(NavigationItem.Wallet.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onShowTabBarChange = onShowTabBarChange
            )
        }
        composable(NavigationItem.Wallet.route) {
            walletScreen(mainNavController = mainNavController, onShowTabBarChange = onShowTabBarChange)
        }
        composable(NavigationItem.History.route) {
            historyScreen(mainNavController = mainNavController, onShowTabBarChange = onShowTabBarChange)
        }
        composable(NavigationItem.Setting.route) {
            settingScreen(mainNavController = mainNavController, onShowTabBarChange = onShowTabBarChange)
        }


    }
}

// 預覽UI

@Preview(showBackground = true)
@Composable
fun MainTabPreview() {

    // 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)


    mainTabScreen( mainNavController = navController)
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
//    BottomNavigationBar(navController)
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
//    TopBar(title = "")
}

@Preview(showBackground = true)
@Composable
fun TopBarSearchPreview() {

//    TopBar_Search()

}