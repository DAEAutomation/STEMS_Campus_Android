package com.dae.stems_campus.ui.screen.history

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.utils.toAmountString
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun refundDetailScreen(navController: NavHostController, refundHistory: HistoryModel.RefundHistory?, onShowTabBarChange: (Boolean) -> Unit) {

    refundDetailContent(
        navController = navController,
        onShowTabBarChange = {},
        refundHistory = refundHistory
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun refundDetailContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    refundHistory: HistoryModel.RefundHistory?) {

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.refund_history),
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

                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (
                                modifier = Modifier
                                    .weight(0.9f),
                                color = Color(0xFF2D859D),
                                shape = RoundedCornerShape(9.dp)
                            ){
                                Column (horizontalAlignment = Alignment.CenterHorizontally) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    val createdAtText = refundHistory?.createdAt?.let {
                                        runCatching {
                                            OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        }.getOrDefault("")
                                    } ?: ""
                                    Text(createdAtText, color = Color.White, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text("$${refundHistory?.amount?.toAmountString()}", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text("${refundHistory?.refundTypeLabel}", color = Color.White, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(30.dp))

                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    navController.navigate("TopUpBinding")
                                },

                                color = Color.White,
                                shape = RoundedCornerShape(
                                    topStart = 9.dp,
                                    topEnd = 9.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp)){

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
                                            Row {
                                                Text("${stringResource(R.string.application_number)}", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${refundHistory?.refundNo}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            val createdAtText = refundHistory?.createdAt?.let {
                                                runCatching {
                                                    OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                                }.getOrDefault("")
                                            } ?: "--"
                                            Row {
                                                Text("${stringResource(R.string.application_time)}", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${createdAtText}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            val completedAtText = refundHistory?.completedAt?.let {
                                                runCatching {
                                                    OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                                }.getOrDefault("")
                                            } ?: "--"
                                            Row {
                                                Text("${stringResource(R.string.transaction_time)}", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${completedAtText}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Row {
                                                Text("(校方退現時間)", color = Color(0xFF2D859D), style = MaterialTheme.typography.bodySmall, modifier = Modifier.width(110.dp))
                                            }

                                            Spacer(modifier = Modifier.height(20.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f)
                                .clickable {
//                                    navController.navigate("changeName/${accountName}")
                                },

                                color = Color.White,
                                shape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomStart = 9.dp,
                                    bottomEnd = 9.dp)){

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
                                            Row {
                                                Text("${stringResource(R.string.applicant)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${refundHistory?.applicantName}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Row {
                                                Text("${stringResource(R.string.applicant_account)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${refundHistory?.applicantNo}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Row {
                                                Text("${stringResource(R.string.status)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${refundHistory?.statusLabel}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
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
private fun refundDetailPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    refundDetailContent (navController,{},null)
}