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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.MqttModel
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.network.MqttHelper
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.viewmodel.MqttVIewModel
import com.dae.stems_campus.viewmodel.ProfileViewModel
import com.dae.stems_campus.viewmodel.TopUpViewModel
import kotlinx.coroutines.delay

@Composable
fun topUpInfoScreen(navController: NavHostController, depositCode: String, profileViewModel: ProfileViewModel = hiltViewModel(), mqttVIewModel: MqttVIewModel = hiltViewModel(), topUpViewModel: TopUpViewModel = hiltViewModel() , onShowTabBarChange: (Boolean) -> Unit) {

    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val showLoadingView by profileViewModel.showLoadingView.collectAsState()
    val resGetProfileInfoSuccessFlag by profileViewModel.resGetProfileInfoSuccessFlag.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()

    val resStartTopUpSuccessFlag by topUpViewModel.resStartTopUpSuccessFlag.collectAsState()
    val startTopUpData by topUpViewModel.startTopUpData.collectAsState()
    val showStartTopUpFailDialogFlag by topUpViewModel.showStartTopUpFailDialogFlag.collectAsState()
    val showStartTopUpFailMsg by topUpViewModel.showStartTopUpFailMsg.collectAsState()
    val uuid by topUpViewModel.UUID.collectAsState()

    val subscriptionState by mqttVIewModel.subscriptionState.collectAsState()

    val mqttTransactionReceiptedSuccessFlag by mqttVIewModel.mqttTransactionReceiptedSuccessFlag.collectAsState()
    val mqttTransactionReceiptedFailFlag by mqttVIewModel.mqttTransactionReceiptedFailFlag.collectAsState()
    val mqttTransactionReceiptedFailMsg by mqttVIewModel.mqttTransactionReceiptedFailMsg.collectAsState()
    val finalCreditAmount by mqttVIewModel.finalCreditAmount.collectAsState()
    val transactionState by mqttVIewModel.transactionState.collectAsState()

    val mqttTransactionFinishSuccessFlag by mqttVIewModel.mqttTransactionFinishSuccessFlag.collectAsState()
    val mqttTransactionFinishSuccessMsg by mqttVIewModel.mqttTransactionFinishSuccessMsg.collectAsState()
    val mqttTransactionFinishFailFlag by mqttVIewModel.mqttTransactionFinishFailFlag.collectAsState()
    val mqttTransactionFinishFailMsg by mqttVIewModel.mqttTransactionFinishFailMsg.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // 用 remember 記住是否已觸發過 startTopUpAction
    var hasStartedTopUp by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.getProfileInfoAction()
    }

    // 拿到 profile 後就連線
    LaunchedEffect(resGetProfileInfoSuccessFlag) {

        if (resGetProfileInfoSuccessFlag) {
            profileViewModel.resetResGetProfileInfoSuccessFailDialogFlag(false)
            mqttVIewModel.connect()
        }
    }

    // 訂閱成功 + 還沒啟動過 → 啟動 topUp
    LaunchedEffect(subscriptionState, hasStartedTopUp) {
        Log.d("DAE_Develop", "subscriptionState=$subscriptionState, hasStarted=$hasStartedTopUp")
        if (subscriptionState == MqttHelper.SubscriptionState.SUCCESS &&
            !hasStartedTopUp) {
            hasStartedTopUp = true
            topUpViewModel.startTopUpAction(
                profileInfo?.id.toString(), depositCode, uuid
            )
        }
    }


    topUpInfoContent(navController = navController,
        onShowTabBarChange = {},
        profileInfo = profileInfo,
        depositName = startTopUpData?.depositName ?: "",
        finalCreditAmount = finalCreditAmount
        )

//    if (resGetProfileInfoSuccessFlag) {
//        profileViewModel.resetResGetProfileInfoSuccessFailDialogFlag(false)
//        mqttVIewModel.connect()
//        topUpViewModel.startTopUpAction(profileInfo?.id.toString(),depositCode,uuid)
//    }

    if (showGetProfileInfoFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showGetProfileInfoFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            profileViewModel.resetShowGetProfileInfoFailDialogFlag(false)
            navController.navigateUp()
        }
    }

    if (resStartTopUpSuccessFlag) {
        val successMsg = "${stringResource(R.string.follow_the_instructions_by_kiosk)}\n${stringResource(R.string.max_top_up_amount_is)} ${startTopUpData?.maxCreditAmount}${stringResource(R.string.currency_unit)}"
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = successMsg
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            topUpViewModel.resetStartTopUpSuccessFlag(false)

        }
    }

    if (showStartTopUpFailDialogFlag) {
        topUpViewModel.resetStartTopUpFailDialogFlag(false)
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showStartTopUpFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            topUpViewModel.resetStartTopUpFailDialogFlag(false)
            navController.navigateUp()
        }
    }

    // MQTT

    val currentTransactionState = transactionState
    if (mqttTransactionReceiptedSuccessFlag && currentTransactionState is MqttModel.TransactionState.Receipted) {
        val snackbarMessage = "${stringResource(R.string.received_payment)}${currentTransactionState.txAmt}${stringResource(R.string.currency_unit)}"
        // 當 showMessage 變為 true 時顯示
        LaunchedEffect(mqttTransactionReceiptedSuccessFlag) {
            if (mqttTransactionReceiptedSuccessFlag) {
                snackbarHostState.showSnackbar(
                    message = snackbarMessage,
                    duration = SnackbarDuration.Short
                )
                mqttVIewModel.resetMqttTransactionReceiptedSuccessFlag(false)
            }
        }
    }

    if (mqttTransactionReceiptedFailFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(
                mqttTransactionReceiptedFailMsg
            )
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            mqttVIewModel.resetMqttTransactionReceiptedFailFlag(false) // 自動關閉 Dialog
            navController.navigateUp()
        }
    }

    if (mqttTransactionFinishSuccessFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(
                mqttTransactionFinishSuccessMsg
            )
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            mqttVIewModel.resetMqttTransactionFinishSuccessFlag(false) // 自動關閉 Dialog
            navController.popBackStack("Wallet", inclusive = false)
        }
    }

    if (mqttTransactionFinishFailFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(
                mqttTransactionFinishFailMsg
            )
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            mqttVIewModel.resetMqttTransactionFinishFailFlag(false) // 自動關閉 Dialog
            navController.navigateUp()
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun topUpInfoContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    profileInfo: ProfileModel.ProfileData? = null,
    depositName: String = "",
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
                    navTitle = stringResource(R.string.top_up_machine_top_up),
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
                            Text(stringResource(R.string.verify_amount_below), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f),
                                color = Color.White,
                                shape = RoundedCornerShape(9.dp)){

                                Row (verticalAlignment = Alignment.CenterVertically){
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Surface (modifier = Modifier.fillMaxWidth(), color = Color.Unspecified){
                                        Column (){
                                            Spacer(modifier = Modifier.height(30.dp))
                                            Text(stringResource(R.string.current_top_up_amount), color = Color.Black,style = MaterialTheme.typography.bodyLarge)
                                            Spacer(modifier = Modifier.height(15.dp))
                                            Row {
                                                Surface(
                                                    modifier = Modifier
                                                        .align(Alignment.Bottom)
                                                        .wrapContentHeight(), color = Color.Unspecified
                                                ) {
                                                    Text("$", color = Color.Black,style = MaterialTheme.typography.bodyLarge)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text("$finalCreditAmount", color = Color(0xFF2D859D), style = MaterialTheme.typography.displaySmall)
                                                Spacer(modifier = Modifier.width(15.dp))

                                            }
                                            Spacer(modifier = Modifier.height(30.dp))
                                        }

                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Surface (modifier = Modifier.weight(1f),color = Color.Unspecified){}
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
    }else if (aMsg == "TransactionCancellation") {
        msg = stringResource(id = R.string.transaction_cancellation)
    }else if (aMsg == "TransactionSuccessful") {
        msg = stringResource(id = R.string.transaction_successful)
    }else {
        msg = aMsg
    }
    return msg
}

// 預覽UI

@Preview(showBackground = true)
@Composable
private fun topUpInfoPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    topUpInfoContent (navController,{})
}