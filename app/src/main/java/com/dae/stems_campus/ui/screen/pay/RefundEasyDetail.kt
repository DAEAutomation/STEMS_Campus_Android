package com.dae.stems_campus.ui.screen.pay

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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.utils.toLocalDateTimeText
import com.dae.stems_campus.viewmodel.RefundViewModel
import kotlinx.coroutines.delay

@Composable
fun refundEasyDetailScreen(navController: NavHostController, refundViewModel: RefundViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {

    val showLoadingView by refundViewModel.showLoadingView.collectAsState()
    val resRefundRequestSuccessFlag by refundViewModel.resRefundRequestSuccessFlag.collectAsState()
    val showRefundRequestFailDialogFlag by refundViewModel.showRefundRequestFailDialogFlag.collectAsState()
    val showRefundRequestFailMsg by refundViewModel.showRefundRequestFailMsg.collectAsState()
    val refundRequestData by refundViewModel.refundRequestData.collectAsState()

    val resFetchRefundStatusSuccessFlag by refundViewModel.resFetchRefundStatusSuccessFlag.collectAsState()
    val showFetchRefundStatusFailDialogFlag by refundViewModel.showFetchRefundStatusFailDialogFlag.collectAsState()
    val showFetchRefundStatusFailMsg by refundViewModel.showFetchRefundStatusFailMsg.collectAsState()
    val refundStatusData by refundViewModel.refundStatusData.collectAsState()

    val resCancelRefundSuccessFlag by refundViewModel.resCancelRefundSuccessFlag.collectAsState()
    val showCancelRefundFailDialogFlag by refundViewModel.showCancelRefundFailDialogFlag.collectAsState()
    val showCancelRefundFailMsg by refundViewModel.showCancelRefundFailMsg.collectAsState()

    var refundID by remember { mutableStateOf(0) }
    var refundCode by remember { mutableStateOf("") }
    var refundCreatedAt by remember { mutableStateOf("") }
    var refundNo by remember { mutableStateOf("") }
    var refundAmount by remember { mutableStateOf(0.0) }
    var refundStatus by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        refundViewModel.refundRequestAction(2)
    }

    refundEasyDetailContent(
        navController = navController,
        onShowTabBarChange = {},
        refundID = refundID,
        refundCode = refundCode,
        refundCreatedAt = refundCreatedAt,
        refundNo = refundNo,
        refundAmount = refundAmount,
        refundStatus = refundStatus,
        cancelHandled = {
            refundViewModel.cancelRefundAction(refundID)
        }
    )

    if (showLoadingView) {
        LoadingView() {}
    }

    if (resRefundRequestSuccessFlag) {
        refundViewModel.resetResRefundRequestSuccessFailDialogFlag(false)
        refundID = refundRequestData?.refundId ?: 0
        refundCode = refundRequestData?.refundCode ?: ""
        refundCreatedAt = refundRequestData?.createdAt ?: ""
        refundNo = refundRequestData?.refundNo ?: ""
        refundAmount = refundRequestData?.amount ?: 0.0
        refundStatus = refundRequestData?.status ?: ""
    }

    if (showRefundRequestFailDialogFlag) {
        if (showRefundRequestFailMsg == "已有待處理的退款申請") {
            refundViewModel.fetchRefundStatusAction()
        }else{
            textTNoButtonAlert(
                onDismissRequest = {},
                dialogTitle = parseDialogMsg(showRefundRequestFailMsg ?: "")
            )
            // 在 Dialog 顯示後啟動計時器
            LaunchedEffect(Unit) {
                delay(1500) // 延遲 1.5 秒
                refundViewModel.resetShowRefundRequestFailDialogFlag(false)
                navController.navigateUp()
            }
        }
    }

    if (resFetchRefundStatusSuccessFlag) {
        refundViewModel.resetShowFetchRefundStatusFailDialogFlag(false)
        refundID = refundStatusData?.refundId ?: 0
        refundCode = refundStatusData?.refundCode ?: ""
        refundCreatedAt = refundStatusData?.createdAt ?: ""
        refundNo = refundStatusData?.refundNo ?: ""
        refundAmount = refundStatusData?.amount ?: 0.0
        refundStatus = refundStatusData?.status ?: ""
    }

    if (showFetchRefundStatusFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showFetchRefundStatusFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            refundViewModel.resetShowFetchRefundStatusFailDialogFlag(false)
            navController.navigateUp()
        }
    }

    if (resCancelRefundSuccessFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = "退款申請已取消"
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            refundViewModel.resetResCancelRefundSuccessFailDialogFlag(false)
            navController.navigateUp()
        }
    }

    if (showCancelRefundFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showCancelRefundFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            refundViewModel.resetShowCancelRefundFailDialogFlag(false)
            navController.navigateUp()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun refundEasyDetailContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    refundID: Int? = null,
    refundCode: String? = null,
    refundCreatedAt: String? = null,
    refundNo: String? = null,
    refundAmount: Double? = null,
    refundStatus: String? = null,
    cancelHandled:() -> Unit) {

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.easy_card_refund),
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
                                Column (){
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Row (verticalAlignment = Alignment.CenterVertically){
                                        Spacer(modifier = Modifier.width(20.dp))
                                        Icon(
                                            painter = painterResource(id = R.drawable.wallet_w),
                                            contentDescription = "",
                                            tint = Color.Unspecified
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(stringResource(R.string.wallet), color = Color.White,style = MaterialTheme.typography.bodyLarge)
                                        Row (verticalAlignment = Alignment.Bottom){
                                            Spacer(modifier = Modifier.width(20.dp))
                                            Text("${refundAmount?.toAmountString()}", color = Color.White, style = MaterialTheme.typography.headlineLarge,fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.width(15.dp))
                                            Text(stringResource(R.string.currency_unit), color = Color.White, style = MaterialTheme.typography.bodyLarge)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        //<-----申請日期----->
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    navController.navigate("")
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
                                            val dateText = refundCreatedAt?.toLocalDateTimeText("yyyy-MM-dd HH:mm:ss") ?: ""
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Text(stringResource(R.string.application_date), color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyMedium)
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(dateText, color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                                            Spacer(modifier = Modifier.height(20.dp))
                                        }
                                    }
                                    Surface (
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .weight(0.1f),
                                        color = Color.Unspecified
                                    ){

                                    }


                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        // <-----申請單號----->
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
                                            Text(stringResource(R.string.application_number), color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyMedium)
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(refundNo ?: "", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                                            Spacer(modifier = Modifier.height(20.dp))
                                        }
                                    }
                                    Surface (
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .weight(0.1f),
                                        color = Color.Unspecified
                                    ){

                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(80.dp))
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .align(Alignment.CenterVertically)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF2D859D)
                                    )
                                    .clickable {
                                        navController.navigate("RefundBinding/${refundID}")
                                    },

                                color = Color.Transparent
                            ) {
                                Text(
                                    text = "立即退款",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.wrapContentHeight(),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .align(Alignment.CenterVertically)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFE54343)
                                    )
                                    .clickable {
                                        cancelHandled()
                                    },

                                color = Color.Transparent
                            ) {
                                Text(
                                    text = "取消申請",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.wrapContentHeight(),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(30.dp))
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
private fun refundEasyDetailPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    refundEasyDetailContent (navController,{},0,"","","",0.0,"",{})
}