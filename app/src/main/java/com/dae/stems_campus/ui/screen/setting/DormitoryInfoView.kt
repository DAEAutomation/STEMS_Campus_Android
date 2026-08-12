package com.dae.stems_campus.ui.screen.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R


@Composable
fun dormitoryInfoScreen(navController: NavHostController, refundID: Int, onShowTabBarChange: (Boolean) -> Unit) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dormitoryInfoContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    onScanCodeHandled:(String) -> Unit) {

    var showInputCode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.dormitory_binding),
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
                    Column (){
                        Spacer(modifier = Modifier.height(30.dp))

                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(stringResource(R.string.dormitory_list), color = Color(0xFF2D859D), style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(20.dp))

                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f),

                                color = Color.White,
                                shape = RoundedCornerShape(9.dp)){

                                Row (verticalAlignment = Alignment.CenterVertically){
                                    Spacer(modifier = Modifier.width(20.dp))

                                    Surface (
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .weight(0.9f),
                                        color = Color.Unspecified
                                    ){
                                        Column {
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Text(stringResource(R.string.dormitory_list_1), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                                            Spacer(modifier = Modifier.height(10.dp))

                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Column (modifier = Modifier.padding(horizontal = 20.dp)){
                            // 退出宿舍 按鈕
                            Row {
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(45.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White)
                                        .clickable { },
                                    color = Color.Transparent
                                ) {
                                    Text(
                                        text = stringResource(R.string.leave_dormitory),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight(Alignment.CenterVertically),
                                        color = Color(0xFFE54343),
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
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
private fun dormitoryInfoPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    dormitoryInfoContent (navController,{},{})
}