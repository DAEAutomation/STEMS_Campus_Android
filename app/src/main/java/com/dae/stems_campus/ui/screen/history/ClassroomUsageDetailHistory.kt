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
import com.dae.stems_campus.utils.computeDuration
import com.dae.stems_campus.utils.computeDurationAtLeastOneMinute
import com.dae.stems_campus.utils.toAmountString
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun classroomDetailScreen(navController: NavHostController, classroomHistory: HistoryModel.ClassroomHistory?, role: String, onShowTabBarChange: (Boolean) -> Unit) {

    classroomDetailContent(
        navController = navController,
        onShowTabBarChange = {},
        classroomHistory = classroomHistory,
        role = role
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun classroomDetailContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    classroomHistory: HistoryModel.ClassroomHistory?,
    role: String) {

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.classroom_usage_history),
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
                                    val createdAtText = classroomHistory?.general?.startTime?.let {
                                        runCatching {
                                            OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        }.getOrDefault("")
                                    } ?: ""
                                    Text(createdAtText, color = Color.White, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text("${classroomHistory?.spaceName}", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(20.dp))
                                    Text("${classroomHistory?.buildingName}", color = Color.White, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(30.dp))

                        if (role == "staff") {
                            contentViewByTeacher(classroomHistory)
                        }else if (role == "student"){
                            contentViewByStudent(classroomHistory)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
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
private fun contentViewByTeacher(classroomHistory: HistoryModel.ClassroomHistory?) {
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface (modifier = Modifier
            .weight(1f),

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
                        Row {
                            val startTimeText = classroomHistory?.startTime?.let {
                                runCatching {
                                    OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                }.getOrDefault("")
                            } ?: ""
                            Text("${stringResource(R.string.start_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(startTimeText, color = Color.Black, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            val endTimeText = classroomHistory?.endTime?.let {
                                runCatching {
                                    OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                }.getOrDefault("")
                            } ?: ""
                            Text("${stringResource(R.string.end_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(endTimeText, color = Color.Black, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Text("${stringResource(R.string.usage_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text("${computeDurationAtLeastOneMinute(classroomHistory?.startTime ?: "", classroomHistory?.endTime ?: "")}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
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

@Composable
private fun contentViewByStudent(classroomHistory: HistoryModel.ClassroomHistory?) {
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface (modifier = Modifier
            .weight(1f),

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
                            Text("${stringResource(R.string.power_supply_mode)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(stringResource(R.string.general_power_supply), color = Color.White,style = MaterialTheme.typography.titleMedium, modifier = Modifier.background(Color.Black).padding(2.dp))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            val startTimeText = classroomHistory?.general?.startTime?.let {
                                runCatching {
                                    OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                }.getOrDefault("")
                            } ?: ""
                            Text("${stringResource(R.string.start_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(startTimeText, color = Color.Black, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            val endTimeText = classroomHistory?.general?.endTime?.let {
                                runCatching {
                                    OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                }.getOrDefault("")
                            } ?: ""
                            Text("${stringResource(R.string.end_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(endTimeText, color = Color.Black, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Text("${stringResource(R.string.usage_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text("${computeDurationAtLeastOneMinute(classroomHistory?.general?.startTime ?: "", classroomHistory?.general?.endTime ?: "")}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Text("${stringResource(R.string.mode_rate)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text("${classroomHistory?.general?.rate ?: ""}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                            Text("${stringResource(R.string.currency_unit)}/${stringResource(R.string.minute)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(3.dp))
    if (classroomHistory?.ac?.durationMinutes != 0) {
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface (modifier = Modifier
                .weight(1f),

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
                            Row {
                                Text("${stringResource(R.string.power_supply_mode)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(stringResource(R.string.air_conditioner_power_supply), color = Color.White, style = MaterialTheme.typography.titleMedium, modifier = Modifier.background(Color(0xFF2D859D)).padding(2.dp))
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row {
                                val startTimeText = classroomHistory?.ac?.startTime?.let {
                                    runCatching {
                                        OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                    }.getOrDefault("")
                                } ?: ""
                                Text("${stringResource(R.string.start_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(startTimeText, color = Color.Black, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row {
                                val endTimeText = classroomHistory?.ac?.endTime?.let {
                                    runCatching {
                                        OffsetDateTime.parse(it).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                    }.getOrDefault("")
                                } ?: ""
                                Text("${stringResource(R.string.end_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                Spacer(modifier = Modifier.width(20.dp))
                                Text(endTimeText, color = Color.Black, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row {
                                Text("${stringResource(R.string.usage_time)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                Spacer(modifier = Modifier.width(20.dp))
                                Text("${computeDurationAtLeastOneMinute(classroomHistory?.ac?.startTime ?: "", classroomHistory?.ac?.endTime ?: "")}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                            Row {
                                Text("${stringResource(R.string.mode_rate)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                                Spacer(modifier = Modifier.width(20.dp))
                                Text("${classroomHistory?.ac?.rate ?: ""}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                                Text("${stringResource(R.string.currency_unit)}/${stringResource(R.string.minute)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
    Spacer(modifier = Modifier.height(3.dp))
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface (modifier = Modifier
            .weight(1f),

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
                            Text("${stringResource(R.string.cumulative_deduction_amount)}：", color = Color.Black, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(110.dp))
                            Spacer(modifier = Modifier.width(20.dp))
                            Text("${classroomHistory?.totalAmount?.toAmountString()}", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                            Text("${stringResource(R.string.currency_unit)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
private fun classroomDetailPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    classroomDetailContent (navController,{},null,"student")
}