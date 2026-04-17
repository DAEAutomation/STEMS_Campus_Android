package com.dae.stems_campus.ui.screen

import android.util.Log
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
import com.dae.stems_campus.ui.screen.home.HomeScreen
import com.dae.stems_campus.ui.screen.setting.settingScreen

@Composable
fun mainTabScreen(mainNavController: NavController) {

    val bottomBarNavController = rememberNavController()
    // 創建一個狀態來保存當前頁面的標題
    val currentTitle = rememberSaveable { mutableStateOf(NavigationItem.Home.navTitle) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(bottomBarNavController, onItemSelected = { title ->
                // 更新標題
                currentTitle.value = title
            })
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding)) {
                Navigation(
                    bottomBarNavHostController = bottomBarNavController,
                    mainNavController = mainNavController,
                    startRote = NavigationItem.Home.route)
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
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items

                        // 這行確保每次點擊都回到該頁面的 "第一頁"
                        popUpTo(item.route) { inclusive = true }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}


@Composable
private fun Navigation(bottomBarNavHostController: NavHostController,mainNavController: NavController, startRote: String) {
    NavHost(bottomBarNavHostController, startDestination = startRote) {
        composable(NavigationItem.Home.route) {
            HomeScreen(mainNavController = mainNavController, onNavigateToSetting = {
                bottomBarNavHostController.navigate(NavigationItem.Setting.route) {
                    popUpTo(NavigationItem.Setting.route) { inclusive = true }
                    launchSingleTop = true
                }
            })
        }
        composable(NavigationItem.Wallet.route) {

        }
        composable(NavigationItem.History.route) {

        }
        composable(NavigationItem.Setting.route) {
            settingScreen(mainNavController = mainNavController, onShowTabBarChange = {})
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