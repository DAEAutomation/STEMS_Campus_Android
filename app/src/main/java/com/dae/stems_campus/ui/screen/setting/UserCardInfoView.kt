package com.dae.stems_campus.ui.screen.setting

import android.print.PrinterInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.viewmodel.ProfileViewModel
import com.dae.stems_campus.viewmodel.SettingViewModel
import kotlinx.coroutines.delay

@Composable
fun userCardInfoViewScreen(
    mainNavController: NavController,
    navController: NavHostController,
    settingViewModel: SettingViewModel,
    profileViewModel: ProfileViewModel
) {
    val uid by settingViewModel.uidText.collectAsState()
    val showLoadingView by settingViewModel.showLoadingView.collectAsState()
    val resCardBindingSuccessFlag by settingViewModel.resCardBindingSuccessFlag.collectAsState()
    val showCardBindingFailDialogFlag by settingViewModel.showCardBindingFailDialogFlag.collectAsState()
    val showCardBindingFailMsg by settingViewModel.showCardBindingFailMsg.collectAsState()
    val showCardBindingInputFailFlag by settingViewModel.showCardBindingInputFailFlag.collectAsState()
    val showCardBindingInputFailMsg by settingViewModel.showCardBindingInputFailMsg.collectAsState()
    val resCardUnBindingSuccessFlag by settingViewModel.resCardUnBindingSuccessFlag.collectAsState()
    val showCardUnBindingFailDialogFlag by settingViewModel.showCardUnBindingFailDialogFlag.collectAsState()
    val showCardUnBindingFailMsg by settingViewModel.showCardUnBindingFailMsg.collectAsState()

    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val profileShowLoadingView by profileViewModel.showLoadingView.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getProfileInfoAction()
    }

    // 離開頁面時清空輸入
    DisposableEffect(Unit) {
        onDispose { settingViewModel.resetCardBindingInputs() }
    }

    userCardInfoContent(
        navController = navController,
        profileData = profileInfo,
        uid = uid,
        showLoadingView = showLoadingView,
        resCardBindingSuccessFlag = resCardBindingSuccessFlag,
        showCardBindingFailDialogFlag = showCardBindingFailDialogFlag,
        showCardBindingFailMsg = showCardBindingFailMsg,
        showCardBindingInputFailFlag = showCardBindingInputFailFlag,
        showCardBindingInputFailMsg = showCardBindingInputFailMsg ?: "",
        resCardUnBindingSuccessFlag = resCardUnBindingSuccessFlag,
        showCardUnBindingFailDialogFlag = showCardUnBindingFailDialogFlag,
        showCardUnBindingFailMsg = showCardUnBindingFailMsg,
        onUidChange = { settingViewModel.updateInputUid(it) },
        onSubmitClick = { value ->
            settingViewModel.cardBindingAction(value)
        },
        onCardBindingSuccessHandled = {
            settingViewModel.resetResCardBindingSuccessFlag(false)
            settingViewModel.resetCardBindingInputs()
            profileViewModel.getProfileInfoAction()
        },
        onCardBindingFailDismissed = {
            settingViewModel.resetShowCardBindingFailDialogFlag(false)
        },
        onCardBindingInputFailDismissed = {
            settingViewModel.resetShowCardBindingInputFailFlag(false)
        },
        onUnBinding = {
            settingViewModel.cardUnBindingAction()
        },
        onCardUnBindingSuccessHandled = {
            settingViewModel.resetResCardUnBindingSuccessFlag(false)
            profileViewModel.getProfileInfoAction()
            navController.navigateUp()
        },
        onCardUnBindingFailDismissed = {
            settingViewModel.resetShowCardUnBindingFailDialogFlag(false)
        }
    )

    if (profileShowLoadingView) {
        LoadingView() {}
    }
    if (showGetProfileInfoFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showGetProfileInfoFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            profileViewModel.resetShowGetProfileInfoFailDialogFlag(false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun userCardInfoContent(
    navController: NavHostController,
    profileData: ProfileModel.ProfileData? = null,
    uid: String = "",
    showLoadingView: Boolean = false,
    resCardBindingSuccessFlag: Boolean = false,
    showCardBindingFailDialogFlag: Boolean = false,
    showCardBindingFailMsg: String? = null,
    showCardBindingInputFailFlag: Boolean = false,
    showCardBindingInputFailMsg: String = "",
    resCardUnBindingSuccessFlag: Boolean = false,
    showCardUnBindingFailDialogFlag: Boolean = false,
    showCardUnBindingFailMsg: String? = null,
    onUidChange: (String) -> Unit = {},
    onSubmitClick: (String) -> Unit = {},
    onCardBindingSuccessHandled: () -> Unit = {},
    onCardBindingFailDismissed: () -> Unit = {},
    onCardBindingInputFailDismissed: () -> Unit = {},
    onUnBinding: () -> Unit = {},
    onCardUnBindingSuccessHandled: () -> Unit = {},
    onCardUnBindingFailDismissed: () -> Unit = {},
) {
    val InputCardIDBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCardIDBottomSheet by remember { mutableStateOf(false) }
    val unBindingBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showUnBindingBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F4F4))
    ) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.physical_card_id),
                    navController = navController
                )
            },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Row {
                        Spacer(modifier = Modifier
                            .height(1.dp)
                            .weight(1f)
                            .background(color = Color(0xFFABABAB)))
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))

                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Text(stringResource(R.string.edit_physical_card_id), color = Color(0xFF2D859D), style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        // <-----實體卡ID----->
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showCardIDBottomSheet = true
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
                                            Text(stringResource(R.string.physical_card_id), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                                            Spacer(modifier = Modifier.height(10.dp))
                                            if (profileData?.uid.equals("")){
                                                Text("尚未綁定", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                                            }else{
                                                Text(profileData?.uid ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                                            }
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
                                            tint = Color.Black,
                                            contentDescription = "Localized description"
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        // <-----解除綁定----->
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showUnBindingBottomSheet = true
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
                                            Spacer(modifier = Modifier.height(30.dp))
                                            Text("解除綁定", color = Color(0xFFE54343), style = MaterialTheme.typography.titleLarge)
                                            Spacer(modifier = Modifier.height(30.dp))
                                        }
                                    }
                                    Surface (
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .weight(0.1f),
                                        color = Color.Unspecified
                                    ){
//                                        Icon(
//                                            painter = painterResource(id = R.drawable.caretright),
//                                            tint = Color.Black,
//                                            contentDescription = "Localized description"
//                                        )
                                    }

                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }

                        Spacer(modifier = Modifier.height(60.dp))


                    }

                    // 輸入實體卡ID
                    if (showCardIDBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showCardIDBottomSheet = false
                                onCardBindingInputFailDismissed()
                            },
                            sheetState = InputCardIDBottomSheetState,
                            containerColor = Color.White
                        ) {
                            inputCardIDBottomSheetView(
                                title = "綁定或變更實體卡ID",
                                uid = uid,
                                onUidChange = onUidChange,
                                onInputText = { value ->
                                    onSubmitClick(value)
                                },
                                onCancelHandled = {
                                    showCardIDBottomSheet = false
                                    onCardBindingInputFailDismissed()
                                },
                                isInputFailed = showCardBindingInputFailFlag,
                                inputFailMsg = showCardBindingInputFailMsg,
                                onInputFailDismissed = onCardBindingInputFailDismissed
                            )
                        }
                    }

                    //解除綁定
                    if (showUnBindingBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showUnBindingBottomSheet = false
                            },
                            sheetState = unBindingBottomSheetState,
                            containerColor = Color.White
                        ) {
                            unBindingBottomSheetView(onUnBindingHandled = {
                                onUnBinding()
                                showUnBindingBottomSheet = false
                            }, onCancelHandled = {
                                showUnBindingBottomSheet = false
                            })
                        }
                    }

                    if (showLoadingView) {
                        LoadingView() {}
                    }

                    if (showCardBindingFailDialogFlag) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = parseDialogMsg(showCardBindingFailMsg ?: "")
                        )
                        LaunchedEffect(Unit) {
                            delay(1500)
                            onCardBindingFailDismissed()
                        }
                    }

                    if (resCardBindingSuccessFlag) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = "綁定成功"
                        )
                        LaunchedEffect(Unit) {
                            delay(1500)
                            showCardIDBottomSheet = false
                            onCardBindingSuccessHandled()
                        }
                    }

                    if (showCardUnBindingFailDialogFlag) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = parseDialogMsg(showCardUnBindingFailMsg ?: "")
                        )
                        LaunchedEffect(Unit) {
                            delay(1500)
                            onCardUnBindingFailDismissed()
                        }
                    }

                    if (resCardUnBindingSuccessFlag) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = "解除綁定成功"
                        )
                        LaunchedEffect(Unit) {
                            delay(1500)
                            showUnBindingBottomSheet = false
                            onCardUnBindingSuccessHandled()
                        }
                    }
                }
            }
        )
    }

    BackHandler {
        navController.navigateUp()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun inputCardIDBottomSheetView(
    title: String,
    uid: String,
    onUidChange: (String) -> Unit,
    onInputText: (String) -> Unit,
    onCancelHandled: () -> Unit,
    isInputFailed: Boolean,
    inputFailMsg: String,
    onInputFailDismissed: () -> Unit,
) {
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Text(title, color = Color.Black, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row {
            BasicTextField(
                value = uid,
                onValueChange = {
                    onUidChange(it)
                    if (isInputFailed) onInputFailDismissed()
                },
                textStyle = TextStyle(color = Color.Black),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Ascii,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth().padding(start = 20.dp, end = 20.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = if (isInputFailed) Color(0xFFE54343) else Color(0xFF999999),
                                shape = RoundedCornerShape(10.dp)
                            ).padding(15.dp)
                    ) {
                        if (uid.isEmpty()) {
                            Text(
                                text = "UID",
                                color = Color(0xFFAAAAAA)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }

        // 錯誤輸入顯示
        if (isInputFailed) {
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Spacer(modifier = Modifier.width(20.dp))
                Text(parseDialogMsg(inputFailMsg), color = Color(0xFFE54343))
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
        Spacer(modifier = Modifier.height(35.dp))
        Row {
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .align(Alignment.CenterVertically)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        color = Color(0xFF2D859D)
                    )
                    .clickable {
                        onInputText(uid)
                    },

                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.submit),
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
                    .clickable {
                        onCancelHandled()
                    },
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
private fun unBindingBottomSheetView(onUnBindingHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column (modifier = Modifier.background(Color.White)){
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.White){
                Column (horizontalAlignment = Alignment.CenterHorizontally, ){
                    Spacer(modifier = Modifier.height(25.dp))
                    Text("是否確定將此 實體卡ID 解除？", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height( 35.dp))
        Row {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onUnBindingHandled()
                    },
                color = Color.White
            ) {
                Text(
                    text = "解除綁定",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color(0xFFC82C2C)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
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
                    fontWeight = FontWeight.Black
                )
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    painterResource(id = R.drawable.arrowbendupleft),
                    contentDescription = "back",
                    tint = Color.Unspecified
                )
            }
        },
        actions = {},
    )
}

@Composable
private fun parseDialogMsg(aMsg: String): String {
    return when (aMsg) {
        "UIDNotEntered" -> "尚未輸入卡號"
        "UIDInvalidFormat" -> "卡號格式錯誤，需8碼16進位(0-9, A-F)"
        "PleaseReLogin" -> stringResource(id = R.string.please_re_login)
        else -> aMsg
    }
}

@Preview(showBackground = true)
@Composable
private fun userCardInfoPreview() {
    val navController = TestNavHostController(LocalContext.current)
    userCardInfoContent(navController = navController)
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetViewPreview() {
    STEMS_CampusTheme {
        inputCardIDBottomSheetView(
            title = "XXXXX",
            uid = "",
            onUidChange = {},
            onInputText = {},
            onCancelHandled = {},
            isInputFailed = false,
            inputFailMsg = "",
            onInputFailDismissed = {}
        )
    }
}
