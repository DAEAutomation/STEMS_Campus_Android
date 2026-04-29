package com.dae.stems_campus.ui.screen.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.viewmodel.AccountViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun registerVerificationCodeScreen(
    navController: NavHostController,
    email: String,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val verificationCode1 by accountViewModel.verificationCode1.collectAsState()
    val verificationCode2 by accountViewModel.verificationCode2.collectAsState()
    val verificationCode3 by accountViewModel.verificationCode3.collectAsState()
    val verificationCode4 by accountViewModel.verificationCode4.collectAsState()
    val verifiedToken by accountViewModel.verifiedToken.collectAsState()
    val showLoadingView by accountViewModel.showLoadingView.collectAsState()
    val resVerifyCodeSuccessFlag by accountViewModel.resVerifyCodeSuccessFlag.collectAsState()
    val showVerifyCodeFailDialogFlag by accountViewModel.showVerifyCodeFailDialogFlag.collectAsState()
    val showVerifyCodeFailMsg by accountViewModel.showVerifyCodeFailMsg.collectAsState()
    val showVerifyCodeInputFailFlag by accountViewModel.showVerifyCodeInputFailFlag.collectAsState()
    val showVerifyCodeInputFailMsg by accountViewModel.showVerifyCodeInputFailMsg.collectAsState()

    registerVerificationCodeContent(
        navController = navController,
        email = email,
        verifiedToken = verifiedToken,
        verificationCode1 = verificationCode1,
        verificationCode2 = verificationCode2,
        verificationCode3 = verificationCode3,
        verificationCode4 = verificationCode4,
        showLoadingView = showLoadingView,
        showVerificationInputFailFlag = showVerifyCodeInputFailFlag,
        showVerificationInputFailMsg = showVerifyCodeInputFailMsg,
        resVerificationSuccessFlag = resVerifyCodeSuccessFlag,
        showVerificationFailMsgDialogFlag = showVerifyCodeFailDialogFlag,
        showVerificationFailMsg = showVerifyCodeFailMsg,
        onCode1Change = { accountViewModel.updateInputVerificationCode1(it) },
        onCode2Change = { accountViewModel.updateInputVerificationCode2(it) },
        onCode3Change = { accountViewModel.updateInputVerificationCode3(it) },
        onCode4Change = { accountViewModel.updateInputVerificationCode4(it) },
        onCodeFocused = { accountViewModel.resetShowVerifyCodeInputFailFlag(false) },
        onVerifyClick = { accountViewModel.registerVerifyCodeAction(email) },
        onVerifySuccessHandled = { accountViewModel.resetResVerifyCodeSuccessFlag(false) },
        onVerifyFailDismissed = {
            accountViewModel.resetShowVerifyCodeFailDialogFlag(false)
            accountViewModel.resetShowVerifyCodeInputFailFlag(false)
        }
    )
}

@Composable
private fun registerVerificationCodeContent(
    navController: NavHostController,
    email: String,
    verifiedToken: String = "",
    verificationCode1: String = "",
    verificationCode2: String = "",
    verificationCode3: String = "",
    verificationCode4: String = "",
    showLoadingView: Boolean = false,
    showVerificationInputFailFlag: Boolean = false,
    showVerificationInputFailMsg: String? = null,
    resVerificationSuccessFlag: Boolean = false,
    showVerificationFailMsgDialogFlag: Boolean = false,
    showVerificationFailMsg: String? = null,
    onCode1Change: (String) -> Unit = {},
    onCode2Change: (String) -> Unit = {},
    onCode3Change: (String) -> Unit = {},
    onCode4Change: (String) -> Unit = {},
    onCodeFocused: () -> Unit = {},
    onVerifyClick: () -> Unit = {},
    onVerifySuccessHandled: () -> Unit = {},
    onVerifyFailDismissed: () -> Unit = {},
    ) {

    val focusRequester1 = remember { FocusRequester() }
    val focusRequester2 = remember { FocusRequester() }
    val focusRequester3 = remember { FocusRequester() }
    val focusRequester4 = remember { FocusRequester() }

//    val remainingTime by viewModel.remainingTime.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            topBar = {
                TopTitleBar(
                    navTitle = "",
                    navController = navController
                )
            },

            bottomBar = {
            },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Column {
                        Surface (modifier = Modifier.weight(1.5f).fillMaxWidth(), color = Color.Unspecified){}
                        Surface (modifier = Modifier.weight(5f).fillMaxWidth(), color = Color.Unspecified){
                            Column {
                                Row {
                                    Spacer(modifier = Modifier.width(60.dp))
                                    Text(stringResource(R.string.please_enter_verification_code),
                                        color = Color.Black,
                                        style = MaterialTheme.typography.headlineSmall)
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Row {
                                    Spacer(modifier = Modifier.width(60.dp))
                                    Text(stringResource(R.string.verification_code_sent),
                                        color = Color(0xFF2D859D),
                                        style = MaterialTheme.typography.bodySmall)
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Row {
                                    Spacer(modifier = Modifier.width(30.dp))
                                    Surface (modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f), color = Color(0xFFF4F4F4),
                                        shape = RoundedCornerShape(30.dp),
                                    ){
                                        Column {
                                            Spacer(modifier = Modifier.height(30.dp))
                                            Row {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                Surface (modifier = Modifier.weight(1f).height(60.dp), color = Color.Unspecified){
                                                    Column {
                                                        // <-----第一碼----->
                                                        TextField(
                                                            value = verificationCode1,  // 使用狀態作為輸入值
                                                            onValueChange = { newValue ->
                                                                // 只接受數字且最多一個字符
                                                                if (newValue.all { it.isDigit() }) {
                                                                    // 如果輸入了一個字符，自動跳到下一個 TextField
                                                                    if (newValue.length == 1) {
                                                                        onCode1Change(newValue)
                                                                        focusRequester2.requestFocus()
                                                                    }else if (newValue.length > 1) {
                                                                        focusRequester2.requestFocus()
                                                                    }else if (newValue.isEmpty()) {
                                                                        onCode1Change(newValue)
                                                                        focusRequester1.requestFocus()
                                                                    }
                                                                }
                                                            },
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .padding(0.dp)
                                                                .focusRequester(focusRequester1)
                                                                .onFocusChanged { focusState ->
                                                                    if (focusState.isFocused) {
                                                                        onCodeFocused()
                                                                    }
                                                                },
                                                            colors = TextFieldDefaults.colors(
                                                                focusedContainerColor = Color.Transparent,
                                                                unfocusedContainerColor = Color.Transparent,
                                                                focusedIndicatorColor = Color.Transparent,
                                                                unfocusedIndicatorColor = Color.Transparent,
                                                                disabledIndicatorColor = Color.Transparent,
                                                            ),
                                                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                                                textAlign = TextAlign.Center,
                                                                color = Color.Black
                                                            ),
                                                            placeholder = {

                                                            },
                                                            keyboardOptions = KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                            ),
                                                            singleLine = true,
                                                        )
                                                        if (showVerificationInputFailFlag) {
                                                            Row {
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Spacer(modifier = Modifier
                                                                    .height(1.dp)
                                                                    .weight(1f)
                                                                    .background(color = Color(0xFFF55454)))
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                            }
                                                        }else{
                                                            Row {
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Spacer(modifier = Modifier
                                                                    .height(1.dp)
                                                                    .weight(1f)
                                                                    .background(color = Color.Black))
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                            }
                                                        }
                                                    }
                                                }
                                                Surface (modifier = Modifier.weight(1f).height(60.dp), color = Color.Unspecified){
                                                    Column {
                                                        // <-----第二碼----->
                                                        TextField(
                                                            value = verificationCode2,  // 使用狀態作為輸入值
                                                            onValueChange = { newValue ->
                                                                // 只接受數字且最多一個字符
                                                                if (newValue.all { it.isDigit() }) {
                                                                    // 如果輸入了一個字符，自動跳到下一個 TextField
                                                                    if (newValue.length == 1) {
                                                                        onCode2Change(newValue)
                                                                        focusRequester3.requestFocus()
                                                                    }else if (newValue.length > 1) {
                                                                        focusRequester3.requestFocus()
                                                                    }else if (newValue.isEmpty() && verificationCode2.isNotEmpty()) {
                                                                        onCode2Change(newValue)
                                                                        focusRequester1.requestFocus()
                                                                    }
                                                                }
                                                            },
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .padding(0.dp)
                                                                .focusRequester(focusRequester2)
                                                                .onFocusChanged { focusState ->
                                                                    if (focusState.isFocused) {
                                                                        onCodeFocused()
                                                                    }
                                                                }
                                                                .onPreviewKeyEvent { keyEvent ->
                                                                    // 當欄位已經是空的，且按下刪除鍵
                                                                    if (verificationCode2.isEmpty() &&
                                                                        keyEvent.key == Key.Backspace &&
                                                                        keyEvent.type == KeyEventType.KeyDown
                                                                    ) {
                                                                        focusRequester1.requestFocus()
                                                                        true // 消費此事件
                                                                    } else {
                                                                        false // 不消費，讓正常流程處理
                                                                    }
                                                                },
                                                            colors = TextFieldDefaults.colors(
                                                                focusedContainerColor = Color.Transparent,
                                                                unfocusedContainerColor = Color.Transparent,
                                                                focusedIndicatorColor = Color.Transparent,
                                                                unfocusedIndicatorColor = Color.Transparent,
                                                                disabledIndicatorColor = Color.Transparent,
                                                            ),
                                                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                                                textAlign = TextAlign.Center,
                                                                color = Color.Black
                                                            ),
                                                            placeholder = {

                                                            },
                                                            keyboardOptions = KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                            ),
                                                            singleLine = true,
                                                        )
                                                        if (showVerificationInputFailFlag) {
                                                            Row {
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Spacer(modifier = Modifier
                                                                    .height(1.dp)
                                                                    .weight(1f)
                                                                    .background(color = Color(0xFFF55454)))
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                            }
                                                        }else{
                                                            Row {
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Spacer(modifier = Modifier
                                                                    .height(1.dp)
                                                                    .weight(1f)
                                                                    .background(color = Color.Black))
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                            }
                                                        }
                                                    }
                                                }
                                                Surface (modifier = Modifier.weight(1f).height(60.dp), color = Color.Unspecified){
                                                    Column {
                                                        // <-----第三碼----->
                                                        TextField(
                                                            value = verificationCode3,  // 使用狀態作為輸入值
                                                            onValueChange = { newValue ->
                                                                // 只接受數字且最多一個字符
                                                                if (newValue.all { it.isDigit() }) {
                                                                    // 如果輸入了一個字符，自動跳到下一個 TextField
                                                                    if (newValue.length == 1) {
                                                                        onCode3Change(newValue)
                                                                        focusRequester4.requestFocus()
                                                                    }else if (newValue.length > 1) {
                                                                        focusRequester4.requestFocus()
                                                                    }else if (newValue.isEmpty()) {
                                                                        onCode3Change(newValue)
                                                                        focusRequester2.requestFocus()
                                                                    }
                                                                }
                                                            },
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .padding(0.dp)
                                                                .focusRequester(focusRequester3)
                                                                .onFocusChanged { focusState ->
                                                                    if (focusState.isFocused) {
                                                                        onCodeFocused()
                                                                    }
                                                                }
                                                                .onPreviewKeyEvent { keyEvent ->
                                                                    // 當欄位已經是空的，且按下刪除鍵
                                                                    if (verificationCode3.isEmpty() &&
                                                                        keyEvent.key == Key.Backspace &&
                                                                        keyEvent.type == KeyEventType.KeyDown
                                                                    ) {
                                                                        focusRequester2.requestFocus()
                                                                        true // 消費此事件
                                                                    } else {
                                                                        false // 不消費，讓正常流程處理
                                                                    }
                                                                },
                                                            colors = TextFieldDefaults.colors(
                                                                focusedContainerColor = Color.Transparent,
                                                                unfocusedContainerColor = Color.Transparent,
                                                                focusedIndicatorColor = Color.Transparent,
                                                                unfocusedIndicatorColor = Color.Transparent,
                                                                disabledIndicatorColor = Color.Transparent,
                                                            ),
                                                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                                                textAlign = TextAlign.Center,
                                                                color = Color.Black
                                                            ),
                                                            placeholder = {

                                                            },
                                                            keyboardOptions = KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                            ),
                                                            singleLine = true,
                                                        )
                                                        if (showVerificationInputFailFlag) {
                                                            Row {
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Spacer(modifier = Modifier
                                                                    .height(1.dp)
                                                                    .weight(1f)
                                                                    .background(color = Color(0xFFF55454)))
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                            }
                                                        }else{
                                                            Row {
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Spacer(modifier = Modifier
                                                                    .height(1.dp)
                                                                    .weight(1f)
                                                                    .background(color = Color.Black))
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                            }
                                                        }
                                                    }
                                                }
                                                Surface (modifier = Modifier.weight(1f).height(60.dp), color = Color.Unspecified){
                                                    Column {
                                                        // <-----第四碼----->
                                                        TextField(
                                                            value = verificationCode4,  // 使用狀態作為輸入值
                                                            onValueChange = { newValue ->
                                                                // 只接受數字且最多一個字符
                                                                if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                                                    onCode4Change(newValue)

                                                                    if (newValue.isEmpty()) {
                                                                        focusRequester3.requestFocus()
                                                                    }
                                                                }
                                                            },
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .padding(0.dp)
                                                                .focusRequester(focusRequester4)
                                                                .onFocusChanged { focusState ->
                                                                    if (focusState.isFocused) {
                                                                        onCodeFocused()
                                                                    }
                                                                }
                                                                .onPreviewKeyEvent { keyEvent ->
                                                                    // 當欄位已經是空的，且按下刪除鍵
                                                                    if (verificationCode4.isEmpty() &&
                                                                        keyEvent.key == Key.Backspace &&
                                                                        keyEvent.type == KeyEventType.KeyDown
                                                                    ) {
                                                                        focusRequester3.requestFocus()
                                                                        true // 消費此事件
                                                                    } else {
                                                                        false // 不消費，讓正常流程處理
                                                                    }
                                                                },
                                                            colors = TextFieldDefaults.colors(
                                                                focusedContainerColor = Color.Transparent,
                                                                unfocusedContainerColor = Color.Transparent,
                                                                focusedIndicatorColor = Color.Transparent,
                                                                unfocusedIndicatorColor = Color.Transparent,
                                                                disabledIndicatorColor = Color.Transparent,
                                                            ),
                                                            textStyle = MaterialTheme.typography.titleLarge.copy(
                                                                textAlign = TextAlign.Center,
                                                                color = Color.Black
                                                            ),
                                                            placeholder = {

                                                            },
                                                            keyboardOptions = KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                            ),
                                                            singleLine = true,
                                                        )
                                                        if (showVerificationInputFailFlag) {
                                                            Row {
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Spacer(modifier = Modifier
                                                                    .height(1.dp)
                                                                    .weight(1f)
                                                                    .background(color = Color(0xFFF55454)))
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                            }
                                                        }else{
                                                            Row {
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                                Spacer(modifier = Modifier
                                                                    .height(1.dp)
                                                                    .weight(1f)
                                                                    .background(color = Color.Black))
                                                                Spacer(modifier = Modifier.width(10.dp))
                                                            }
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(30.dp))
                                            }

                                            if (showVerificationInputFailFlag){
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Row {
                                                    Spacer(modifier = Modifier.width(50.dp))
                                                    Text(parseDialogMsg(showVerificationInputFailMsg ?: ""),
                                                        color = Color(0xFFF55454),
                                                        style = MaterialTheme.typography.titleSmall)
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(50.dp))
                                            Row {
                                                Spacer(modifier = Modifier.width( 30.dp))
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(40.dp)
                                                        .align(Alignment.CenterVertically)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(
                                                            Color(0xFF2D859D)
                                                        )
                                                        .clickable {
                                                            onVerifyClick()
                                                        },

                                                    color = Color.Transparent
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.verify),
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.wrapContentHeight(),
                                                        color = Color.White
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(30.dp))
                                            }

                                        }
                                    }
                                    Spacer(modifier = Modifier.width(30.dp))
                                }
                            }
                        }
                        Surface (modifier = Modifier.weight(4f).fillMaxWidth(), color = Color(0xFFF4F4F4)){
                            Column (modifier = Modifier,horizontalAlignment = Alignment.CenterHorizontally){
//                                TextButton(
//                                    onClick = {
//                                        if (remainingTime == 0) {
//                                            viewModel.reSendVerificationAction("register",email)
//                                            viewModel.startCountdown(30)
//                                        }
//
//                                    }
//                                ) {
//                                    if (remainingTime > 0) {
//                                        Text("${remainingTime}${stringResource(id = R.string.second)}", color = Color.White)
//                                    }else{
//                                        Text(stringResource(id = R.string.resend_verification_code),style = TextStyle(textDecoration = TextDecoration.Underline), color = Color.White)
//                                    }
//                                }
                            }
                            Column (modifier = Modifier
                                .fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally){
                                Surface (modifier = Modifier,
                                    color = Color.Unspecified){
                                    Image(painter = painterResource(id = R.drawable.dae_logo_logo_3_1), contentDescription = "")
                                }
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    text = "Copyright © "+ getCurrentYear() +" DAE instrument CO., Ltd. All rights reserved",
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier,
                                    color = Color(0xFF000000),
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(15.dp))
                            }

                        }

                        if (showLoadingView) {
                            LoadingView() {}
                        }

                        if (showVerificationFailMsgDialogFlag) {
                            textTNoButtonAlert(
                                onDismissRequest = {},
                                dialogTitle = parseDialogMsg(showVerificationFailMsg ?: "")
                            )
                            // 在 Dialog 顯示後啟動計時器
                            LaunchedEffect(Unit) {
                                delay(1500) // 延遲 1.5 秒
                                onVerifyFailDismissed()
                            }
                        }
                        if(resVerificationSuccessFlag) {
                            textTNoButtonAlert(
                                onDismissRequest = {},
                                dialogTitle = stringResource(R.string.email_verify_success)
                            )
                            // 在 Dialog 顯示後啟動計時器
                            LaunchedEffect(Unit) {
                                delay(1500) // 延遲 1.5 秒
                                onVerifySuccessHandled()
                                navController.navigate("SetUserNameInfo/${email}/${verifiedToken}")
                            }
                        }
//
//                        if (resReSendSuccessFlag) {
//                            textTNoButtonAlert(
//                                onDismissRequest = {},
//                                dialogTitle = stringResource(id = R.string.verification_code_sent)
//                            )
//                            // 在 Dialog 顯示後啟動計時器
//                            LaunchedEffect(Unit) {
//                                delay(1500) // 延遲 1.5 秒
//                                viewModel.resetReSendSuccessFlag(false) // 自動關閉 Dialog
//                            }
//                        }
//
//                        if (showReSendFailMsgDialogFlag) {
//                            textTNoButtonAlert(
//                                onDismissRequest = {},
//                                dialogTitle = showReSendFailMsg ?: ""
//                            )
//                            // 在 Dialog 顯示後啟動計時器
//                            LaunchedEffect(Unit) {
//                                delay(1500) // 延遲 1.5 秒
//                                viewModel.resetShowReSendFailMsgDialogFlag(false) // 自動關閉 Dialog
//                            }
//                        }
                    }
                }
            }
        )
    }


}

private fun getCurrentYear(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy") // 設定日期格式
    val today = LocalDate.now() // 取得今天的日期
    // 格式化輸出
    val yearStr = today.format(formatter)
    return yearStr
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopTitleBar(navTitle: String, navController: NavHostController) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                navTitle,
                maxLines = 1,

                )
        },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
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
private fun MyCircle(selectColor: Color){
    Canvas(modifier = Modifier.size(6.dp), onDraw = {
        drawCircle(color = selectColor)
    })
}

@Composable
private fun parseDialogMsg(aMsg: String):(String){
    var msg: String = ""
    if (aMsg == "VerificationCodeNotEntered") {
        msg = "請輸入完整驗證碼"
    }else {
        msg = aMsg
    }
    return msg
}

@Preview(showBackground = true)
@Composable
private fun registerVerificationCodePreview() {
    val navController = TestNavHostController(LocalContext.current)

//    registerVerificationCodeContent(navController,"","","","","",false)
}