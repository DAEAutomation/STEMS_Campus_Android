package com.dae.stems_campus.ui.screen.setting

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.SettingModel
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.viewmodel.SettingViewModel
import kotlinx.coroutines.delay


@Composable
fun dormitoryInfoScreen(navController: NavHostController, settingViewModel: SettingViewModel) {


    val myDormitoryData by settingViewModel.myDormitoryData.collectAsState()
    val resDormUnBindingSuccessFlag by settingViewModel.resDormUnBindingSuccessFlag.collectAsState()
    val showDormUnBindingFailDialogFlag by settingViewModel.showDormUnBindingFailDialogFlag.collectAsState()
    val showDormUnBindingFailMsg by settingViewModel.showDormUnBindingFailMsg.collectAsState()

    LaunchedEffect(Unit) {
        settingViewModel.getMyDormitoryDataAction(true)
    }

    dormitoryInfoContent(
        navController = navController,
        myDormitoryData = myDormitoryData,
        onUnbindingHandled = {
            settingViewModel.dormUnBindingAction(myDormitoryData?.roomId ?: 0)
        }
    )

    if (resDormUnBindingSuccessFlag) {
        settingViewModel.resetResDormUnBindingSuccessFlag(false)
        navController.navigateUp()
    }

    if (showDormUnBindingFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showDormUnBindingFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            settingViewModel.resetShowDormUnBindingFailDialogFlag(false)
        }
    }


}

//名單1~8 的標題，最多 8 位
private val dormitoryListTitles = listOf(
    R.string.dormitory_list_1,
    R.string.dormitory_list_2,
    R.string.dormitory_list_3,
    R.string.dormitory_list_4,
    R.string.dormitory_list_5,
    R.string.dormitory_list_6,
    R.string.dormitory_list_7,
    R.string.dormitory_list_8
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dormitoryInfoContent(
    navController: NavHostController,
    myDormitoryData: SettingModel.MyDormitoryData? = null,
    onUnbindingHandled:() -> Unit) {

    //列數依 memberMax 決定，上限 8；沒帶回來就以 members 數量顯示
    val memberMax = (myDormitoryData?.memberMax ?: myDormitoryData?.members?.size ?: 0)
        .coerceIn(0, dormitoryListTitles.size)
    val members = myDormitoryData?.members ?: emptyList()

    var showingUnbindingBottomSheet by remember { mutableStateOf(false) }
    val unbindingSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.dormitory_binding),
                    navController = navController
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

                        //還沒拿到資料前不畫空卡片
                        if (memberMax > 0) Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f),

                                color = Color.White,
                                shape = RoundedCornerShape(9.dp)){

                                Column {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    //依 memberMax 排出固定列數，前面幾列填 members，其餘留空
                                    for (index in 0 until memberMax) {
                                        Row (verticalAlignment = Alignment.CenterVertically){
                                            Spacer(modifier = Modifier.width(20.dp))
                                            Text(
                                                text = stringResource(dormitoryListTitles[index]),
                                                color = Color.Black,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = members.getOrNull(index)?.name ?: "",
                                                color = Color.Black,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.weight(1.4f)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(20.dp))
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
                                        .clickable { showingUnbindingBottomSheet = true },
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
                        //停止供電頁面
                        if (showingUnbindingBottomSheet) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    showingUnbindingBottomSheet = false
                                },
                                sheetState = unbindingSheetState,
                                containerColor = Color.White
                            ) {
                                unbindingDormitoryBottomSheetView(
                                    aMyDormitory = myDormitoryData,
                                    onUnbindingHandled = {
                                        showingUnbindingBottomSheet = false
                                        onUnbindingHandled()
                                    },
                                    onCancelHandled = {
                                        showingUnbindingBottomSheet = false
                                    })
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
private fun unbindingDormitoryBottomSheetView(aMyDormitory: SettingModel.MyDormitoryData?, onUnbindingHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(35.dp))
                    Text("是否退出此宿舍？", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }
        Row (){
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 90.dp), // 預設 90dp，系統字體放大時跟著 Column 內容往下長
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 15.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${"樓棟："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${aMyDormitory?.buildingName}-${aMyDormitory?.floorName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Row(verticalAlignment = Alignment.Top) {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${"房間："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${aMyDormitory?.roomNumber}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                        onUnbindingHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.leave_dormitory),
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
private fun TopTitleBar(navTitle: String, navController: NavHostController) {
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
    dormitoryInfoContent (navController,
        SettingModel.MyDormitoryData(
            bound = true,
            memberMax = 8,
            members = listOf(
                SettingModel.Members(name = "曾冠其"),
                SettingModel.Members(name = "王世宇"),
                SettingModel.Members(name = "陳家駿"),
                SettingModel.Members(name = "王曉明")
            )
        ),
        {})
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetViewPreview() {
    STEMS_CampusTheme {
        unbindingDormitoryBottomSheetView(null,{},{})
    }

}