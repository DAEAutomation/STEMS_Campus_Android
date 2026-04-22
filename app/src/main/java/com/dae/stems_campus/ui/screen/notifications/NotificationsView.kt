package com.dae.stems_campus.ui.screen.notifications

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.data.model.NotificationsModel
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.viewmodel.HistoryViewModel
import com.dae.stems_campus.viewmodel.NotificationViewModel
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
fun notificationsScreen(navController: NavHostController, notificationViewModel: NotificationViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {

    val resGetNotificationSuccessFlag by notificationViewModel.resGetNotificationSuccessFlag.collectAsState()
    val showGetNotificationFailDialogFlag by notificationViewModel.showGetNotificationFailDialogFlag.collectAsState()
    val showGetNotificationFailMsg by notificationViewModel.showGetNotificationFailMsg.collectAsState()
    val notificationList by notificationViewModel.notificationList.collectAsState()

    LaunchedEffect(Unit) {
        notificationViewModel.getNotificationAction()
    }

    notificationsContent(
        navController = navController,
        onShowTabBarChange = onShowTabBarChange,
        notificationList = notificationList,
        rowClickHandled = { detail ->
            notificationViewModel.notificationDetail = detail
            navController.navigate("NotificationDetail")
        }
    )

    if (showGetNotificationFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showGetNotificationFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            notificationViewModel.resetShowGetNotificationFailDialogFlag(false)
            navController.navigateUp()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun notificationsContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    notificationList: List<NotificationsModel.NotificationsData> = emptyList(),
    rowClickHandled:(NotificationsModel.NotificationsData) -> Unit) {

    val grouped = remember(notificationList) {
        notificationList
            .filter { !it.createdAt.isNullOrEmpty() }
            .groupBy {
                val dt = OffsetDateTime.parse(it.createdAt!!)
                YearMonth.of(dt.year, dt.monthValue)
            }
            .toSortedMap(compareByDescending { it })
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = "通知",
                    navController = navController,
                    onShowTabBarChange = onShowTabBarChange
                )

            },

            bottomBar = {

            },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Row {
                        Spacer(modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(color = Color(0xFFABABAB)))

                    }
                    Column (modifier = Modifier.verticalScroll(rememberScrollState())){
                        Spacer(modifier = Modifier.height(30.dp))

                        grouped.forEach { (ym, items) ->
                            Row {
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(
                                    "${ym.year}年${ym.monthValue}月",
                                    color = Color(0xFF2D859D),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Spacer(modifier = Modifier.height(20.dp))

                            items.forEachIndexed { index, item ->
                                val shape = when {
                                    items.size == 1 -> RoundedCornerShape(9.dp)
                                    index == 0 -> RoundedCornerShape(
                                        topStart = 9.dp, topEnd = 9.dp,
                                        bottomStart = 0.dp, bottomEnd = 0.dp
                                    )
                                    index == items.lastIndex -> RoundedCornerShape(
                                        topStart = 0.dp, topEnd = 0.dp,
                                        bottomStart = 9.dp, bottomEnd = 9.dp
                                    )
                                    else -> RoundedCornerShape(0.dp)
                                }
                                notificationsRow(item = item, shape = shape, rowClick = { detail -> rowClickHandled(detail) })
                                if (index != items.lastIndex) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        )
    }

    //處理手機上的Back按鍵
    BackHandler {

    }

}

@Composable
private fun notificationsRow(
    item: NotificationsModel.NotificationsData,
    shape: RoundedCornerShape,
    rowClick:(NotificationsModel.NotificationsData) -> Unit
) {
    val dateText = item.createdAt?.let {
        runCatching {
            OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        }.getOrDefault("")
    } ?: ""

    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier.weight(1f)
                .clickable {
                    rowClick(item)
                },
            color = Color.White,
            shape = shape
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(20.dp))
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.9f),
                    color = Color.Unspecified
                ) {
                    Row {
                        Column {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(dateText, color = Color.Black, style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(item.title ?: "", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.1f),
                    color = Color.Unspecified
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.caretright_b),
                        tint = Color.Unspecified,
                        contentDescription = null
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopTitleBar(navTitle: String, navController: NavHostController, onShowTabBarChange: (Boolean) -> Unit) {
    CenterAlignedTopAppBar(
        windowInsets = WindowInsets(0),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.Black,
        ),
        title = {
            Text(
                navTitle,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black   // W900，最粗
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                // 返回或離開時再顯示
                onShowTabBarChange(true)
                navController.navigateUp()
            }) {
                Icon(
                    painterResource(id = R.drawable.arrowbendupleft),
                    contentDescription = "Localized description",
                    tint = Color.Unspecified
                )
            }
        },
        actions = {},

        )
}

@Composable
private fun parseDialogMsg(aMsg: String):(String){
    var msg: String = ""
    if (aMsg == "PleaseReLogin") {
        msg = stringResource(id = R.string.please_re_login)
    }else {
        msg = aMsg
    }
    return msg
}

// 預覽UI

@Preview(showBackground = true)
@Composable
private fun notificationsPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    notificationsContent (navController,{}, emptyList(),{})
}