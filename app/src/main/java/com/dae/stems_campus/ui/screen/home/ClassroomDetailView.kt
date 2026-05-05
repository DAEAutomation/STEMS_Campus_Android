package com.dae.stems_campus.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.ProfileModel.BillingDetail
import com.dae.stems_campus.data.model.ProfileModel.ControlDetail
import com.dae.stems_campus.data.model.ProfileModel.DeviceDetail
import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.utils.computeDurationAtLeastOneMinute
import com.dae.stems_campus.utils.elapsedTime
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.utils.toTwoDecimalString
import com.dae.stems_campus.viewmodel.HomeInfoViewModel
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun classroomDetailScreen(navController: NavHostController, selectDeviceCode: String, homeInfoViewModel: HomeInfoViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {

    val uuid by homeInfoViewModel.UUID.collectAsState()

    val resUsingDeviceDetailSuccessFlag by homeInfoViewModel.resUsingDeviceDetailSuccessFlag.collectAsState()
    val usingDeviceDetail by homeInfoViewModel.usingDeviceDetail.collectAsState()
    val showUsingDeviceDetailFailDialogFlag by homeInfoViewModel.showUsingDeviceDetailFailDialogFlag.collectAsState()
    val showUsingDeviceDetailFailMsg by homeInfoViewModel.showUsingDeviceDetailFailMsg.collectAsState()

    // 直接從 usingDeviceDetail 取第一筆 session
    val firstSession = usingDeviceDetail?.sessions?.firstOrNull()

    val resStopPowerSuccessFlag by homeInfoViewModel.resStopPowerSuccessFlag.collectAsState()
    val stopPowerData by homeInfoViewModel.stopPowerData.collectAsState()
    val showStopPowerFailDialogFlag by homeInfoViewModel.showStopPowerFailDialogFlag.collectAsState()
    val showStopPowerFailMsg by homeInfoViewModel.showStopPowerFailMsg.collectAsState()

    val resControlAcSuccessFlag by homeInfoViewModel.resControlAcSuccessFlag.collectAsState()
    val showControlAcFailDialogFlag by homeInfoViewModel.showControlAcFailDialogFlag.collectAsState()
    val showControlAcFailMsg by homeInfoViewModel.showControlAcFailMsg.collectAsState()
    val controlAcCommand by homeInfoViewModel.controlAcCommand.collectAsState()

    // 進入畫面時隱藏
    LaunchedEffect(Unit) {
        onShowTabBarChange(false)
        while (true) {
            homeInfoViewModel.getUsingDeviceDetailAction(selectDeviceCode, uuid)
            delay(30_000L)
        }
    }

    classroomDetailContent(
        navController = navController,
        aRole = firstSession?.billing?.userRole,
        aSpaceDetail = firstSession?.space,
        aBillingDetail = firstSession?.billing,
        aDeviceDetail = firstSession?.device,
        aControlDetail = firstSession?.control,
        aSessionDetail = firstSession?.session,
        onStopPowerHandled = { value -> homeInfoViewModel.stopPowerAction(selectDeviceCode,uuid,value)},
        resStopPowerSuccessFlag = resStopPowerSuccessFlag,
        onResStopPowerSuccessDismissed = { homeInfoViewModel.resetStopPowerSuccessFlag(false)},
        aStopSessionDetail = stopPowerData,
        onShowTabBarChange = onShowTabBarChange,
        onControlAcHandled = { value -> homeInfoViewModel.controlAcAction(uuid,firstSession?.control?.controlToken ?: "",value)}
    )


    if (showUsingDeviceDetailFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showUsingDeviceDetailFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            homeInfoViewModel.resetUsingDeviceDetailFailDialogFlag(false)
        }
    }

    if (showStopPowerFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showStopPowerFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            homeInfoViewModel.resetStopPowerFailDialogFlag(false)
        }
    }

    //冷氣控制
    if (resControlAcSuccessFlag) {
        if (controlAcCommand.equals("ac_on")) {
            textTNoButtonAlert(
                onDismissRequest = {},
                dialogTitle = "冷氣已開啟\n 請等待一分鐘"
            )
            // 在 Dialog 顯示後啟動計時器
            LaunchedEffect(Unit) {
                delay(15000) // 延遲 1.5 秒
                homeInfoViewModel.resetControlAcSuccessFlag(false)
            }
        }else{
            textTNoButtonAlert(
                onDismissRequest = {},
                dialogTitle = "冷氣已關閉\n 請等待一分鐘"
            )
            // 在 Dialog 顯示後啟動計時器
            LaunchedEffect(Unit) {
                delay(15000) // 延遲 1.5 秒
                homeInfoViewModel.resetControlAcSuccessFlag(false)
            }
        }
    }

    if (showControlAcFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showControlAcFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            homeInfoViewModel.resetControlAcFailDialogFlag(false)
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun classroomDetailContent(navController: NavHostController,
                                   aRole: String? = "",
                                   aSpaceDetail: ProfileModel.SpaceDetail?,
                                   aControlDetail: ControlDetail?,
                                   aBillingDetail: BillingDetail?,
                                   aDeviceDetail: DeviceDetail?,
                                   aSessionDetail : ProfileModel.SessionDetail?,
                                   onStopPowerHandled:(String) -> Unit,
                                   resStopPowerSuccessFlag: Boolean = false,
                                   onResStopPowerSuccessDismissed: () -> Unit = {},
                                   aStopSessionDetail: ScanModel.StopPowerData?,
                                   onShowTabBarChange: (Boolean) -> Unit,
                                   onControlAcHandled:(String) -> Unit) {

    var showingStopPowerBottomSheet by remember { mutableStateOf(false) }
    var showingStopPowerInfoBottomSheet by remember { mutableStateOf(false) }
    var showingStopPowerInfoByStudentBottomSheet by remember { mutableStateOf(false) }

    val stopPowerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val stopPowerInfoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val stopPowerInfoByStudentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.classroom_power_usage),
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
                        if (aRole.equals("staff")) {
                            contentViewByTeacher(aControlDetail = aControlDetail, aDeviceDetail = aDeviceDetail, aBillingDetail = aBillingDetail, aSpaceDetail = aSpaceDetail, aSessionDetail = aSessionDetail, onStopPowerClick = { showingStopPowerBottomSheet = true})
                        }else if (aRole.equals("student")){
                            contentViewByStudent(aControlDetail = aControlDetail, aDeviceDetail = aDeviceDetail, aBillingDetail = aBillingDetail, aSpaceDetail = aSpaceDetail, aSessionDetail = aSessionDetail, onStopPowerClick = { showingStopPowerBottomSheet = true}, onControlAcClick = { value -> onControlAcHandled(value)})
                        }

                        //停止供電頁面
                        if (showingStopPowerBottomSheet) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    showingStopPowerBottomSheet = false
                                },
                                sheetState = stopPowerSheetState,
                                containerColor = Color.White
                            ) {
                                stopPowerBottomSheetView(aSpaceDetail, onStopPowerHandled = {
                                    onStopPowerHandled(aControlDetail?.controlToken ?: "")
                                }, onCancelHandled = {
                                    showingStopPowerBottomSheet = false
                                })
                            }
                        }

                        //停止供電完成資訊頁面
                        if (showingStopPowerInfoBottomSheet) {
                            showingStopPowerBottomSheet = false
                            ModalBottomSheet(
                                onDismissRequest = {
                                    showingStopPowerInfoBottomSheet = false
                                },
                                sheetState = stopPowerInfoSheetState,
                                containerColor = Color.White
                            ) {
                                stopPowerInfoBottomSheetView(aStopSessionDetail,{
                                    showingStopPowerInfoBottomSheet = false
                                    navController.navigateUp()
                                })
                            }
                        }

                        //停止供電完成資訊頁面:學生
                        if (showingStopPowerInfoByStudentBottomSheet) {
                            showingStopPowerBottomSheet = false
                            ModalBottomSheet(
                                onDismissRequest = {
                                    showingStopPowerInfoByStudentBottomSheet = false
                                },
                                sheetState = stopPowerInfoByStudentSheetState,
                                containerColor = Color.White
                            ) {
                                stopPowerInfoByStudentBottomSheetView(aBillingDetail,aStopSessionDetail,{
                                    showingStopPowerInfoByStudentBottomSheet = false
                                    navController.navigateUp()
                                })
                            }
                        }

                        if (resStopPowerSuccessFlag) {
                            if (aRole.equals("staff")) {
                                showingStopPowerInfoBottomSheet = true
                            }else if (aRole.equals("student")){
                                showingStopPowerInfoByStudentBottomSheet = true
                            }

                            onResStopPowerSuccessDismissed()
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
private fun contentViewByTeacher(aSpaceDetail: ProfileModel.SpaceDetail?,
                                 aControlDetail: ControlDetail?,
                                 aBillingDetail: BillingDetail?,
                                 aDeviceDetail: DeviceDetail?,
                                 aSessionDetail : ProfileModel.SessionDetail?,
                                 onStopPowerClick:() -> Unit
                                ) {
    var currentElapsed by remember {
        mutableStateOf(aBillingDetail?.general?.startTime?.elapsedTime()) }

    LaunchedEffect(aBillingDetail?.general?.startTime) {
        while (true) {
            currentElapsed = aBillingDetail?.general?.startTime?.elapsedTime()
            delay(1000L)
        }
    }

    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface (modifier = Modifier
            .weight(1f)
            ,
            color = Color.White,
            shape = RoundedCornerShape(9.dp)){

            Column {
                Spacer(modifier = Modifier.height(25.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(R.string.classroom), color = Color(0xFFDF8927),style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFFDF8927)).padding(2.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(stringResource(R.string.general_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Black).padding(2.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(stringResource(R.string.air_conditioner_power_supply), color = Color.White, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color(0xFF2D859D)).padding(2.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${aSpaceDetail?.spaceName}", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${aSpaceDetail?.buildingName}-${aSpaceDetail?.floorName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(50.dp))
                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Spacer(modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(color = Color(0xFFABABAB)))

                    Spacer(modifier = Modifier.width(10.dp))
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.timer_b),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(stringResource(R.string.cumulative_time), color = Color.Black, style = MaterialTheme.typography.bodyLarge)

                }
                Spacer(modifier = Modifier.height(20.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(currentElapsed ?: "", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
                if (aSessionDetail?.source.equals("card_reader")) {
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("欲操作或停止供電，請至卡機操作", color = Color(0xFFE54343), style = MaterialTheme.typography.bodyLarge)
                    }
                }else{
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .align(Alignment.CenterVertically)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Unspecified
                                ),
                            color = Color.Transparent
                        ) {

                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp)
                                .align(Alignment.CenterVertically)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFE54343))
                                .clickable {
                                    onStopPowerClick()
                                },
                            color = Color.Transparent
                        ) {
                            Text(
                                text = stringResource(R.string.stop_power_supply),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.wrapContentHeight(),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
private fun contentViewByStudent(aSpaceDetail: ProfileModel.SpaceDetail?,
                                 aControlDetail: ControlDetail?,
                                 aBillingDetail: BillingDetail?,
                                 aDeviceDetail: DeviceDetail?,
                                 aSessionDetail : ProfileModel.SessionDetail?,
                                 onStopPowerClick:() -> Unit,
                                 onControlAcClick:(String) -> Unit) {

    val isAcOn = aDeviceDetail?.powerSupply?.ac?.on == true
    val acButtonColor = if (isAcOn) Color(0xFF484C4F) else Color(0xFF2D859D)
    val acButtonText = if (isAcOn)
        stringResource(R.string.air_conditioner_power_off)    // 冷氣開著 → 顯示「關閉冷氣」
    else stringResource(R.string.air_conditioner_power_supply) // 冷氣關著 → 顯示「開啟冷氣」

    var currentElapsed by remember {
        mutableStateOf(aBillingDetail?.general?.startTime?.elapsedTime()) }

    LaunchedEffect(aBillingDetail?.general?.startTime) {
        while (true) {
            currentElapsed = aBillingDetail?.general?.startTime?.elapsedTime()
            delay(1000L)
        }
    }

    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface (modifier = Modifier
            .weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(9.dp)){

            Column {
                Spacer(modifier = Modifier.height(25.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(R.string.classroom), color = Color(0xFFDF8927),style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFFDF8927)).padding(2.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(stringResource(R.string.general_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Black).padding(2.dp))
                    if (aDeviceDetail?.powerSupply?.ac?.on == true) {
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(stringResource(R.string.air_conditioner_power_supply), color = Color.White, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color(0xFF2D859D)).padding(2.dp))
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${aSpaceDetail?.spaceName}", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${aSpaceDetail?.buildingName}-${aSpaceDetail?.floorName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${stringResource(R.string.rate)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("${aBillingDetail?.rate}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("${stringResource(R.string.currency_unit)}/${stringResource(R.string.minute)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(50.dp))
                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Spacer(modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(color = Color(0xFFABABAB)))

                    Spacer(modifier = Modifier.width(10.dp))
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.timer_b),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(stringResource(R.string.cumulative_time), color = Color.Black, style = MaterialTheme.typography.bodyLarge)

                }
                Spacer(modifier = Modifier.height(20.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(currentElapsed ?: "", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${stringResource(R.string.cumulative_deduction_amount)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("${aBillingDetail?.totalCharged?.toTwoDecimalString()}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("${stringResource(R.string.currency_unit)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(40.dp))
                if (aSessionDetail?.source.equals("card_reader")) {
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("欲操作或停止供電，請至卡機操作", color = Color(0xFFE54343), style = MaterialTheme.typography.bodyLarge)
                    }
                }else{
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp)
                                .align(Alignment.CenterVertically)
                                .clip(RoundedCornerShape(2.dp))
                                .background(acButtonColor)
                                .clickable {
                                    if (isAcOn) {
                                        onControlAcClick("ac_off")
                                    }else{
                                        onControlAcClick("ac_on")
                                    }
                                },
                            color = Color.Transparent
                        ) {
                            Text(
                                text = acButtonText,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.wrapContentHeight(),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(45.dp)
                                .align(Alignment.CenterVertically)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFE54343))
                                .clickable {
                                    onStopPowerClick()
                                },
                            color = Color.Transparent
                        ) {
                            Text(
                                text = stringResource(R.string.stop_power_supply),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.wrapContentHeight(),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun stopPowerBottomSheetView(aSpaceDetail: ProfileModel.SpaceDetail?, onStopPowerHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface (modifier = Modifier, color = Color.Unspecified){
                        Image(painter = painterResource(id = R.drawable.lightningslash), contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text("是否確定停止供電？", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }
        Row (){
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp),
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 15.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${"空間類型："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = when (aSpaceDetail?.spaceType) {
                            "classroom" -> stringResource(R.string.classroom)
                            "dormitory" -> stringResource(R.string.dormitory)
                            else -> ""
                        }, color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${"地點："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${aSpaceDetail?.buildingName}-${aSpaceDetail?.floorName}-${aSpaceDetail?.spaceName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }

            }
            Spacer(modifier = Modifier.width(30.dp))
        }

        Spacer(modifier = Modifier.height( 35.dp))
        Row {
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        color = Color(0xFFE54343)
                    )
                    .clickable {
                        onStopPowerHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.stop_power_supply),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Spacer(modifier = Modifier.width(50.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .align(Alignment.CenterVertically)
                    .clickable{
                        onCancelHandled()
                    }
                ,
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color.Black,
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
            }
            Spacer(modifier = Modifier.width(50.dp))
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun stopPowerInfoBottomSheetView(aData: ScanModel.StopPowerData?, onBackHomeHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(25.dp))
                    Text("教室已停止供電", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }
        Row (){
            Column (){
                Spacer(modifier = Modifier.height( 15.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(R.string.general_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Black).padding(2.dp))
                }
                Spacer(modifier = Modifier.height( 15.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"開始時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${formatIso8601(aData?.session?.startTime ?: "")}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"結束時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${formatIso8601(aData?.session?.endTime ?: "")}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"使用時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(computeDurationAtLeastOneMinute(aData?.session?.startTime ?: "",aData?.session?.endTime ?: ""), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Spacer(modifier = Modifier.height( 20.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(R.string.air_conditioner_power_supply), color = Color.White, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color(0xFF2D859D)).padding(2.dp))
                }
                Spacer(modifier = Modifier.height( 15.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"開始時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${formatIso8601(aData?.session?.acStartTime ?: "")}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"結束時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${formatIso8601(aData?.session?.acEndTime ?: "")}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"使用時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(computeDurationAtLeastOneMinute(aData?.session?.acStartTime ?: "",aData?.session?.acEndTime ?: ""), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Spacer(modifier = Modifier.height(15.dp))
            }

        }

        Spacer(modifier = Modifier.height( 35.dp))
        Row {
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        color = Color(0xFFD08024)
                    )
                    .clickable {
                        onBackHomeHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = "回到主頁",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun stopPowerInfoByStudentBottomSheetView(aBillingDetail : BillingDetail?, aStopPowerData: ScanModel.StopPowerData?, onBackHomeHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(25.dp))
                    Text("教室已停止供電", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }
        Row (){
            Column (){
                Spacer(modifier = Modifier.height( 15.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(R.string.general_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Black).padding(2.dp))
                }
                Spacer(modifier = Modifier.height( 15.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"開始時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${formatIso8601(aStopPowerData?.session?.startTime ?: "") }", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"結束時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${formatIso8601(aStopPowerData?.session?.endTime ?: "")}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"使用時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(computeDurationAtLeastOneMinute(aStopPowerData?.session?.startTime ?: "",aStopPowerData?.session?.endTime ?: ""), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Spacer(modifier = Modifier.height( 20.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(R.string.air_conditioner_power_supply), color = Color.White, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color(0xFF2D859D)).padding(2.dp))
                }
                Spacer(modifier = Modifier.height( 15.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"開始時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${formatIso8601(aStopPowerData?.session?.acStartTime ?: "--")}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"結束時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${formatIso8601(aStopPowerData?.session?.acEndTime ?: "--")}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"使用時間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(computeDurationAtLeastOneMinute(aStopPowerData?.session?.acStartTime ?: "--",aStopPowerData?.session?.acEndTime ?: ""), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }

                Spacer(modifier = Modifier.height( 35.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"當次費率："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${aBillingDetail?.rate}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("${stringResource(R.string.currency_unit)}/${stringResource(R.string.minute)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${"預估扣款："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("${aStopPowerData?.totalCharged?.toTwoDecimalString()}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("${stringResource(R.string.currency_unit)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(20.dp))
                }


                Spacer(modifier = Modifier.height(15.dp))
            }

        }

        Spacer(modifier = Modifier.height( 35.dp))
        Row {
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        color = Color(0xFFD08024)
                    )
                    .clickable {
                        onBackHomeHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = "回到主頁",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

private fun formatIso8601(isoString: String, zoneOffset: ZoneOffset = ZoneOffset.UTC): String {
    if (isoString.isBlank()) return "--"
    return try {
        val instant = Instant.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(zoneOffset)
        formatter.format(instant)
    } catch (e: Exception) {
        "--"
    }
}




@Preview(showBackground = true)
@Composable
private fun classroomDetailPreview() {
    val navController = TestNavHostController(LocalContext.current)

    classroomDetailContent(navController,"student",null,null,null,null,null,{},false,{},null, onShowTabBarChange = {}, onControlAcHandled = {})

}

@Preview(showBackground = true)
@Composable
private fun BottomSheetViewPreview() {
    STEMS_CampusTheme {
        stopPowerBottomSheetView(null,{}, {})
    }

}