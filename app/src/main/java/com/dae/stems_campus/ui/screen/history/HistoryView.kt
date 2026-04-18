package com.dae.stems_campus.ui.screen.history

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.viewmodel.HistoryViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun historyScreen(mainNavController: NavController, historyViewModel: HistoryViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {
    val historyNavController = rememberNavController()

    NavHost(navController = historyNavController, startDestination = "History") {
        composable("History") {
            historyMainLoad(mainNavController = mainNavController,
                navController = historyNavController,
                historyViewModel = historyViewModel,
                onShowTabBarChange = {})
        }
        composable("WalletHistory") {
            val walletTopUpHistoryList = historyViewModel.walletHistoryList
            walletTopUpHistoryScreen(navController = historyNavController, walletTopUpHistoryList.value ?: emptyList(), onShowTabBarChange = {})
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun historyMainLoad(mainNavController: NavController, navController: NavHostController, historyViewModel: HistoryViewModel,  onShowTabBarChange: (Boolean) -> Unit) {

    val resWalletHistorySuccessFlag by historyViewModel.resWalletHistorySuccessFlag.collectAsState()
    val showWalletHistoryFailDialogFlag by historyViewModel.showWalletHistoryFailDialogFlag.collectAsState()
    val showWalletHistoryFailMsg by historyViewModel.showWalletHistoryFailMsg.collectAsState()
    val walletHistoryList by historyViewModel.walletHistoryList.collectAsState()

    historyContent(
        mainNavController = mainNavController,
        navController = navController,
        queryHistoryHandle = { value ->
            if (value == "WalletTopUpHistory") {
                historyViewModel.getWalletHistoryAction("","")
            }
        }
    )


    if (resWalletHistorySuccessFlag) {
        historyViewModel.resetResWalletHistorySuccessFailDialogFlag(false)
        navController.navigate("WalletHistory")
    }

    if (showWalletHistoryFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showWalletHistoryFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            historyViewModel.resetShowWalletHistoryFailDialogFlag(false)
            navController.navigateUp()
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun historyContent(
    mainNavController: NavController,
    navController: NavHostController,
    queryHistoryHandle:(String) -> Unit) {

    // 取得當前螢幕的寬度 (dp)
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val boxWith = (screenWidth * 0.8f)
    val boxStart = (screenWidth * 0.2f / 2)

    val beginDateTitle = stringResource(id = R.string.select_start_date)
    val endDateTitle = stringResource(id = R.string.select_end_date)
    var beginDate by remember { mutableStateOf(beginDateTitle) }
    var endDate by remember { mutableStateOf(endDateTitle) }

    var showHistoryTypeBottomSheet by remember { mutableStateOf(false) }
    val historyTypeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectQueryType by remember { mutableStateOf("") }
    var selectQueryTitle by remember { mutableStateOf("") }

    var showBeginSelectDate by remember { mutableStateOf(false) }
    var showEndSelectDate by remember { mutableStateOf(false) }
    var showNotSelectDate by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF4F4F4))) {

        Column {
            Surface (modifier = Modifier
                .weight(0.15f)
                .fillMaxWidth(),  color = Color.Unspecified){
                Column {
                    Spacer(modifier = Modifier.height(50.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(25.dp))
                        Text("${stringResource(R.string.history)}", color = Color.Black,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)

//                        Surface (modifier = Modifier.weight(1f)){  }
//                        Surface (modifier = Modifier, color = Color.Unspecified){
//                            Surface (modifier = Modifier
//                                .clickable {
//                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
//                                }
//                                , color = Color.Unspecified)
//                            {
//                                Image(painter = painterResource(id = R.drawable.qrcode), contentDescription = "")
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(20.dp))
//                        Surface (modifier = Modifier, color = Color.Unspecified){
//                            Surface (modifier = Modifier
//                                , color = Color.Unspecified)
//                            {
//                                Image(painter = painterResource(id = R.drawable.bell), contentDescription = "")
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Surface (modifier = Modifier.weight(0.9f).verticalScroll(rememberScrollState()), color = Color.Unspecified) {
                Column {
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(color = Color(0xFFF4F4F4)){
                            Text(text = stringResource(R.string.report_type), style = MaterialTheme.typography.bodyLarge, color = Color(0xFF2D859D))
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showHistoryTypeBottomSheet = true
                            },
                            color = Color.White,
                            shape = RoundedCornerShape(
                                topStart = 9.dp,
                                topEnd = 9.dp,
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
                                        Text(when (selectQueryType) {
                                            "WalletTopUpHistory" -> stringResource(R.string.wallet_top_up_history)
                                            "HourAllocationHistory" -> stringResource(R.string.hour_allocation_history)
                                            "ClassroomUsageHistory" -> stringResource(R.string.classroom_usage_history)
                                            "DormitoryUsageHistory" -> stringResource(R.string.dormitory_usage_history)
                                            else -> ""}, color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                                Surface (
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(0.1f),
                                    color = Color.Unspecified
                                ){
                                    Icon(
                                        painter = painterResource(id = R.drawable.caretdown),
                                        tint = Color.Unspecified,
                                        contentDescription = "Localized description"
                                    )
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }


                    Spacer(modifier = Modifier.height(25.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(color = Color(0xFFF4F4F4)){
                            Text(text = stringResource(id = R.string.select_or_enter_query_range), color = Color(0xFF2D859D),
                                style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(color = Color(0xFFF4F4F4)){
                            Text(text = stringResource(id = R.string.query_range_limit),
                                color = Color.Black,
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .height(35.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
//                                beginDate = reportViewModel.lastSevenDays().first
//                                endDate = reportViewModel.lastSevenDays().second
//                                reportViewModel.updateSelectDate(
//                                    beginValue = beginDate,
//                                    endValue = endDate
//                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black){
                            Text(text = stringResource(id = R.string.last_seven_days), style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center,modifier = Modifier.wrapContentHeight(), color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .height(35.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
//                                beginDate = reportViewModel.lastMonth().first
//                                endDate = reportViewModel.lastMonth().second
//                                reportViewModel.updateSelectDate(
//                                    beginValue = beginDate,
//                                    endValue = endDate
//                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black){
                            Text(text = stringResource(id = R.string.last_month), style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center,modifier = Modifier.wrapContentHeight(), color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .height(35.dp)
                            .align(Alignment.CenterVertically)
                            .clickable {
//                                beginDate = reportViewModel.lastThreeMonths().first
//                                endDate = reportViewModel.lastThreeMonths().second
//                                reportViewModel.updateSelectDate(
//                                    beginValue = beginDate,
//                                    endValue = endDate
//                                )
                            },
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Black){
                            Text(text = stringResource(id = R.string.last_three_months), style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center,modifier = Modifier.wrapContentHeight(), color = Color.White)
                        }
                        Surface (modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .wrapContentHeight(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF00A36A)
                        ){

                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(color = Color(0xFFF4F4F4)){
                            Text(text = stringResource(R.string.start_date), style = MaterialTheme.typography.titleMedium, color = Color(0xFF2D859D))
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Row {
                        Surface (modifier = Modifier
                            .width(20.dp)
                            .height(50.dp), color = Color.White){
                        }
                        Surface (modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp), color = Color.White){
                            Text(text = beginDate,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Black,
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .clickable {
                                        showBeginSelectDate = true
                                    })
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface(color = Color(0xFFF4F4F4)){
                            Text(text = stringResource(R.string.end_date), style = MaterialTheme.typography.titleMedium, color = Color(0xFF2D859D))
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Row {
                        Surface (modifier = Modifier
                            .width(20.dp)
                            .height(50.dp), color = Color.White){
                        }
                        Surface (modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp), color = Color.White){
                            Text(text = endDate,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.Black,
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .clickable {
                                        showEndSelectDate = true
                                    })
                        }
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
                                    queryHistoryHandle(selectQueryType)
                                    if (beginDate == beginDateTitle || endDate == endDateTitle) {
                                        showNotSelectDate = true
                                    } else {
                                        showNotSelectDate = false

//                                        navController.navigate("ReportDetail/$aTitle/$aGatewayID/$aRoomID/$aTypeTitleID/$selectChannelID/$selectChannelName/$selectChannelType/$beginDate/$endDate")
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF2D859D)
                        ) {
                            Text(
                                text = stringResource(id = R.string.query),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.wrapContentHeight(),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(30.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    if (showHistoryTypeBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showHistoryTypeBottomSheet = false
                            },
                            sheetState = historyTypeSheetState,
                            containerColor = Color.White
                        ) {
                            historyTypeListView { value ->
                                showHistoryTypeBottomSheet = false
                                selectQueryType = value

                            }
                        }
                    }
                    if (showNotSelectDate) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = "尚未選擇日期"
                        )
                        // 在 Dialog 顯示後啟動計時器
                        LaunchedEffect(Unit) {
                            delay(1500) // 延遲 1.5 秒
                            showNotSelectDate = false
                        }
                    }

                    if (showBeginSelectDate) {
                        val datePickerState = rememberDatePickerState()
                        val selectedDate = datePickerState.selectedDateMillis?.let {
                            convertMillisToDate(it)
                        } ?: stringResource(id = R.string.select_start_date)
//                        reportViewModel.updateSelectDate(beginValue = selectedDate, endValue = endDate)
                        beginDate = selectedDate
                        DatePickerDialog(
                            onDismissRequest = {showBeginSelectDate = false },
                            confirmButton = {
                                TextButton(onClick = {
//                    onDateSelected(datePickerState.selectedDateMillis)
                                    showBeginSelectDate = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick =  {showBeginSelectDate = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                    if (showEndSelectDate) {
                        val datePickerState = rememberDatePickerState()
                        val selectedDate = datePickerState.selectedDateMillis?.let {
                            convertMillisToDate(it)
                        } ?: stringResource(id = R.string.select_end_date)
                        println("e = $selectedDate")
//                        reportViewModel.updateSelectDate(beginValue = beginDate, endValue = selectedDate)
                        endDate = selectedDate
                        DatePickerDialog(
                            onDismissRequest = {showEndSelectDate = false },
                            confirmButton = {
                                TextButton(onClick = {
//                    onDateSelected(datePickerState.selectedDateMillis)
                                    showEndSelectDate = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick =  {showEndSelectDate = false }) {
                                    Text("Cancel")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun historyTypeListView(onItemClick: (String) -> Unit = {}) {
    val items = listOf(
        HistoryModel.HistoryTypeItem(R.string.wallet_top_up_history, "WalletTopUpHistory"),
        HistoryModel.HistoryTypeItem(R.string.hour_allocation_history, "HourAllocationHistory"),
        HistoryModel.HistoryTypeItem(R.string.classroom_usage_history, "ClassroomUsageHistory"),
        HistoryModel.HistoryTypeItem(R.string.dormitory_usage_history, "DormitoryUsageHistory"),
    )

    LazyColumn() {
        items(items) { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item.route) }
                    .padding(top = 3.dp),
                color = Color.White
            ) {
                Column {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = item.titleRes),
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row {
                        Spacer(modifier = Modifier.width(30.dp))
                        Spacer(
                            modifier = Modifier
                                .height(1.dp)
                                .weight(1f)
                                .background(color = Color(0xFF414141))
                        )
                        Spacer(modifier = Modifier.width(30.dp))
                    }
                }
            }
        }
    }
}

private fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(millis))
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
private fun historyPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    historyContent (navController,navController,{})
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetViewPreview() {
    STEMS_CampusTheme {
        historyTypeListView {  }
    }

}