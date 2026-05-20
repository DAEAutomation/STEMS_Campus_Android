package com.dae.stems_campus.ui.screen.history

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.utils.calculateDuration
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.utils.toLocalDateTimeText
import com.dae.stems_campus.viewmodel.HistoryViewModel
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun walletTopUpDetailScreen(navController: NavHostController, walletHistory: HistoryModel.WalletHistory?, historyViewModel: HistoryViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {

    val context = LocalContext.current

    val showLoadingView by historyViewModel.showLoadingView.collectAsState()
    val topUpHistoryDownload by historyViewModel.topUpHistoryDownload.collectAsState()
    val resTopUpHistoryDownloadSuccessFlag by historyViewModel.resTopUpHistoryDownloadSuccessFlag.collectAsState()
    val showTopUpHistoryDownloadFailDialogFlag by historyViewModel.showTopUpHistoryDownloadFailDialogFlag.collectAsState()
    val showTopUpHistoryDownloadFailMsg by historyViewModel.showTopUpHistoryDownloadFailMsg.collectAsState()

    walletTopUpDetailContent(
        navController = navController,
        onShowTabBarChange = {},
        walletHistory = walletHistory,
        onDownloadClick = { transactionId ->
            historyViewModel.getTopUpHistoryDownloadAction(transactionId)
        }
    )

    if (showLoadingView) {
        LoadingView() {}
    }

    if (resTopUpHistoryDownloadSuccessFlag) {
        LaunchedEffect(Unit) {
            topUpHistoryDownload?.download_url?.takeIf { it.isNotEmpty() }?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
            historyViewModel.resetResTopUpHistoryDownloadSuccessFlag(false)
        }
    }

    if (showTopUpHistoryDownloadFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showTopUpHistoryDownloadFailMsg ?: "")
        )
        LaunchedEffect(Unit) {
            delay(1500)
            historyViewModel.resetShowTopUpHistoryDownloadFailDialogFlag(false)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun walletTopUpDetailContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    walletHistory: HistoryModel.WalletHistory?,
    onDownloadClick: (Int) -> Unit) {

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.wallet_top_up_history),
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
                                    val createdAtText = walletHistory?.createdAt?.toLocalDateTimeText("yyyy-MM-dd HH:mm:ss") ?: ""
                                    Text(createdAtText, color = Color.White, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text("$${walletHistory?.amount}", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text("${walletHistory?.paymentMethod}", color = Color.White, style = MaterialTheme.typography.titleMedium)
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
                                                Text("${stringResource(R.string.applicant)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${walletHistory?.userName}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Row {
                                                Text("${stringResource(R.string.applicant_account)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${walletHistory?.account}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
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
                                                Text("${stringResource(R.string.balance_after_top_up)}", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("$${walletHistory?.balanceAfter?.toAmountString()}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Row {
                                                Text("${stringResource(R.string.top_up_machine_name)}", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${walletHistory?.kioskName ?: "--"}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Row {
                                                Text("${stringResource(R.string.status)}", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text("${walletHistory?.txStatus ?: "--"}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                            }

                                            Spacer(modifier = Modifier.height(20.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Spacer(modifier = Modifier.width(30.dp))
                            Surface(
                                modifier = Modifier
                                    .weight(0.13f)
                                    .height(45.dp)
                                    .align(Alignment.CenterVertically)
                                    .clickable {
                                        onDownloadClick(walletHistory?.id ?: 0)
                                    },
                                shape = RoundedCornerShape(2.dp),
                                color = Color(0xFF2D859D)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.download_top_up_receipt),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.wrapContentHeight(),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(30.dp))
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
private fun walletTopUpDetailPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    walletTopUpDetailContent (navController,{},null,{})
}