package com.dae.stems_campus.ui.screen.pay

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.util.Log
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.RefundModel
import com.dae.stems_campus.network.MqttHelper
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.utils.toLocalDateTimeText
import com.dae.stems_campus.viewmodel.MqttVIewModel
import com.dae.stems_campus.viewmodel.ProfileViewModel
import com.dae.stems_campus.viewmodel.RefundViewModel
import com.dae.stems_campus.viewmodel.TopUpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun refundDepositScreen(navController: NavHostController, depositCode: String, refundID: Int, profileViewModel: ProfileViewModel = hiltViewModel(), mqttVIewModel: MqttVIewModel = hiltViewModel(), refundViewModel: RefundViewModel = hiltViewModel() , onShowTabBarChange: (Boolean) -> Unit) {

    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val showLoadingView by profileViewModel.showLoadingView.collectAsState()
    val resGetProfileInfoSuccessFlag by profileViewModel.resGetProfileInfoSuccessFlag.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()

    val subscriptionState by mqttVIewModel.subscriptionState.collectAsState()

    val resStartRefundSuccessFlag by refundViewModel.resStartRefundSuccessFlag.collectAsState()
    val showStartRefundFailDialogFlag by refundViewModel.showStartRefundFailDialogFlag.collectAsState()
    val showStartRefundFailMsg by refundViewModel.showStartRefundFailMsg.collectAsState()
    val startRefundData by refundViewModel.startRefundData.collectAsState()

    val refundStatusData by refundViewModel.refundStatusData.collectAsState()

    val mqttRefundTransactionFinishSuccessFlag by mqttVIewModel.mqttRefundTransactionFinishSuccessFlag.collectAsState()
    val mqttRefundTransactionFinishSuccessMsg by mqttVIewModel.mqttRefundTransactionFinishSuccessMsg.collectAsState()
    val mqttRefundTransactionFinishFailFlag by mqttVIewModel.mqttRefundTransactionFinishFailFlag.collectAsState()
    val mqttRefundTransactionFinishFailMsg by mqttVIewModel.mqttRefundTransactionFinishFailMsg.collectAsState()

    // 用 remember 記住是否已觸發過 startRefundAction
    var hasStartedRefund by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.getProfileInfoAction()
        refundViewModel.fetchRefundStatusAction()
    }

    // 拿到 profile 後就連線
    LaunchedEffect(resGetProfileInfoSuccessFlag) {

        if (resGetProfileInfoSuccessFlag) {
            profileViewModel.resetResGetProfileInfoSuccessFailDialogFlag(false)
            mqttVIewModel.connect()
        }
    }

    // 訂閱成功 + 還沒啟動過 → 啟動 topUp
    LaunchedEffect(subscriptionState, hasStartedRefund) {
        Log.d("DAE_Develop", "subscriptionState=$subscriptionState, hasStarted=$hasStartedRefund")
        if (subscriptionState == MqttHelper.SubscriptionState.SUCCESS &&
            !hasStartedRefund) {
            hasStartedRefund = true
            refundViewModel.startRefundAction(depositCode = depositCode, refundID = refundID)
        }
    }

    refundDepositContent(
        navController = navController,
        onShowTabBarChange = {},
        profileInfo = profileInfo,
        refundStatusData = refundStatusData,
        depositName = "",
        depositCode = depositCode,

    )

    if (resStartRefundSuccessFlag) {
        val successMsg = "${stringResource(R.string.kiosk_connected)}，${stringResource(R.string.follow_the_instructions_by_kiosk)}"
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = successMsg
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            refundViewModel.resetResStartRefundSuccessFailDialogFlag(false)

        }
    }

    if (showStartRefundFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showStartRefundFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            refundViewModel.resetShowStartRefundFailDialogFlag(false)
            navController.navigateUp()
        }
    }

    if (mqttRefundTransactionFinishSuccessFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(mqttRefundTransactionFinishSuccessMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            mqttVIewModel.resetMqttRefundTransactionFinishSuccessFlag(false)
            navController.popBackStack("Wallet", inclusive = false)
        }
    }

    if (mqttRefundTransactionFinishFailFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(mqttRefundTransactionFinishFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            mqttVIewModel.resetMqttRefundTransactionFinishFailFlag(false)
            navController.navigateUp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun refundDepositContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    profileInfo: ProfileModel.ProfileData? = null,
    refundStatusData: RefundModel.RefundStatusData? = null,
    depositName: String = "",
    depositCode: String = "",
    finalCreditAmount: Int = 0) {

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Image(painter = painterResource(id = R.drawable.sck400), contentDescription = "",modifier = Modifier
            .align(Alignment.TopCenter).offset(x = 120.dp, y = (150).dp)
        )
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
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row (verticalAlignment = Alignment.CenterVertically){
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(depositName,
                                color = Color.Black,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f),
                                color = Color.White,
                                shape = RoundedCornerShape(9.dp)){

                                Row (verticalAlignment = Alignment.CenterVertically){
                                    IconButton(
                                        modifier = Modifier,
                                        onClick = {  }
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.wallet),
                                            tint = Color.Unspecified,
                                            contentDescription = "Localized description"
                                        )
                                    }
                                    Text("$${profileInfo?.balance?.toAmountString()}", color = Color.Black, style = MaterialTheme.typography.headlineSmall)
                                    Spacer(modifier = Modifier.width(10.dp))

                                }
                            }
                            Spacer(modifier = Modifier.width(170.dp))
                        }
                        Spacer(modifier = Modifier.height(70.dp))
                        Row {
                            Spacer(modifier = Modifier.width(40.dp))
                            Text(stringResource(R.string.follow_top_up_machine_instructions), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Spacer(modifier = Modifier.width(40.dp))
                            Text("請靠卡退款", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f),
                                color = Color.Unspecified,
                                shape = RoundedCornerShape(9.dp)){

                                Surface (modifier = Modifier.fillMaxWidth(), color = Color.Unspecified){
                                    Column {
                                        //<-----申請日期----->
                                        Row {
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
                                                            val dateText = refundStatusData?.createdAt?.toLocalDateTimeText("yyyy-MM-dd HH:mm:ss") ?: ""
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
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        // <-----申請單號----->
                                        Row {
                                            Surface (modifier = Modifier
                                                .weight(1f)
                                                .clickable {
                                                },

                                                color = Color.White,
                                                shape = RoundedCornerShape(
                                                    topStart = 0.dp,
                                                    topEnd = 0.dp,
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
                                                            Text(stringResource(R.string.application_number), color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyMedium)
                                                            Spacer(modifier = Modifier.height(5.dp))
                                                            Text("${refundStatusData?.refundNo}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                                        }
                                    }

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
    }else if (aMsg == "RefundSuccess") {
        msg = stringResource(id = R.string.refund_success)
    }else if (aMsg == "TransactionCancellation") {
        msg = stringResource(id = R.string.transaction_cancellation)
    }else {
        msg = aMsg
    }
    return msg
}

// 預覽UI

@Preview(showBackground = true)
@Composable
private fun refundDepositPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    refundDepositContent (navController,{})
}