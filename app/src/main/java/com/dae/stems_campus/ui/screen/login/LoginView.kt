package com.dae.stems_campus.ui.screen.login

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.ui.components.BiometricHelper
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.viewmodel.LoginViewModel
import com.dae.stems_campus.viewmodel.SettingViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun login(navController: NavHostController, loginViewModel: LoginViewModel = hiltViewModel(), settingViewModel: SettingViewModel = hiltViewModel()) {

    var loginOrRegister by remember { mutableStateOf(true) }
    var pwVisibility by remember { mutableStateOf(false) }

    val focusRegisterEmail = remember { FocusRequester() }
//    val registerEmail by accountViewModel.emailText.collectAsState()
//    val resRegisterSuccessFlag by accountViewModel.resRegisterSuccessFlag.collectAsState()
//    val showRegisterFailMsgDialogFlag by accountViewModel.showRegisterFailMsgDialogFlag.collectAsState()
//    val showRegisterInputFailFlag by accountViewModel.showRegisterInputFailFlag.collectAsState()
//    val showRegisterFailMsg by accountViewModel.showRegisterFailMsg.collectAsState()
//    val showRegisterInputFailMsg by accountViewModel.showRegisterInputFailMsg.collectAsState()
//    val showLoadingView by accountViewModel.showLoadingView.collectAsState()
//
    val userNameText by loginViewModel.userNameText.collectAsState()
    val passwordText by loginViewModel.passwordText.collectAsState()
    val uuidText by loginViewModel.UUID.collectAsState()

    val showLoadingView by loginViewModel.showLoadingView.collectAsState()

    val resLoginSuccessFlag by loginViewModel.resLoginSuccessFlag.collectAsState()
    val showLoginFailMsgDialogFlag by loginViewModel.showLoginFailMsgDialogFlag.collectAsState()
    val showLoginFailMsg by loginViewModel.showLoginFailMsg.collectAsState()

    val rememberLoginInfoChecked by loginViewModel.rememberLoginInfoChecked.collectAsState()

    val isBiometricFlag by settingViewModel.isBiometricEnabled.collectAsState()

    LaunchedEffect(Unit) {
        settingViewModel.getBiometricValue()
    }

    LoginContent(
        navController = navController,
        userNameText = userNameText,
        passwordText = passwordText,
        rememberLoginInfoChecked = rememberLoginInfoChecked,
        showLoadingView = showLoadingView,
        resLoginSuccessFlag = resLoginSuccessFlag,
        showLoginFailMsgDialogFlag = showLoginFailMsgDialogFlag,
        showLoginFailMsg = showLoginFailMsg,
        onUserNameChange = { loginViewModel.updateInputAccount(it) },
        onPasswordChange = { loginViewModel.updateInputPassword(it) },
        onLoginClick = { loginViewModel.loginAction(userNameText, passwordText, uuidText) },
        onRememberCheckedChange = { loginViewModel.updateRememberLoginInfoChecked(it)},
        onLoginSuccessHandled = { loginViewModel.resetLoginSuccessFlag(false) },
        onLoginFailDismissed = { loginViewModel.resetShowLoginFailMsgDialogFlag(false)
        },
        isBiometricFlag = isBiometricFlag,
        onBiometricLoginHandled = { loginViewModel.loginAction(userNameText, passwordText, uuidText)}
    )
}

@Composable
private fun LoginContent(navController: NavHostController,
                         userNameText: String = "",
                         passwordText: String = "",
                         rememberLoginInfoChecked: Boolean = false,
                         showLoadingView: Boolean = false,
                         resLoginSuccessFlag: Boolean = false,
                         showLoginFailMsgDialogFlag: Boolean = false,
                         showLoginFailMsg: String? = null,
                         onUserNameChange: (String) -> Unit = {},
                         onPasswordChange: (String) -> Unit = {},
                         onLoginClick: () -> Unit = {},
                         onRememberCheckedChange: (Boolean) -> Unit = {},
                         onLoginSuccessHandled: () -> Unit = {},
                         onLoginFailDismissed: () -> Unit = {},
                         isBiometricFlag: Boolean = false,
                         onBiometricLoginHandled: () -> Unit) {

    var loginOrRegister by remember { mutableStateOf(true) }
    var pwVisibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val biometricHelper = remember(activity) {
        activity?.let { BiometricHelper(it) }
    }
    var showBiometricFailDialogFlag by remember { mutableStateOf(false) }
    var showBiometricFailMsg by remember { mutableStateOf("") }


    Column (){
        Surface(modifier = Modifier
            .weight(3f)
            .fillMaxWidth(),color = Color(0xFFF4F4F4),){

            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Spacer(modifier = Modifier.height(90.dp))
                Surface (modifier = Modifier, color = Color.Unspecified){
                    Image(painter = painterResource(id = R.drawable.stems_capus_logo_2), contentDescription = "")
                }
                Spacer(modifier = Modifier.height(70.dp))

            }

        }
        Surface (modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
            color = Color(0xFFF4F4F4)){
//            Row {
//                Spacer(modifier = Modifier.width(250.dp))
//                Surface(
//                    modifier = Modifier
//                        .weight(1f)
//                        .height(40.dp)
//                        .align(Alignment.CenterVertically)
//                        .clip(RoundedCornerShape(20.dp))
//                        .clickable {
//                            loginOrRegister = !loginOrRegister
//                        },
//
//                    color = Color.Transparent
//                ) {
//                    if (loginOrRegister) {
//                        Surface (modifier = Modifier,
//                            color = Color.Unspecified){
//                            Image(painter = painterResource(id = R.drawable.loginswitch_2), contentDescription = "")
//                        }
//                    }else {
//                        Surface (modifier = Modifier,
//                            color = Color.Unspecified){
//                            Image(painter = painterResource(id = R.drawable.loginswitch_1), contentDescription = "")
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.width(30.dp))
//            }
        }

        if (loginOrRegister) {
            Surface (modifier = Modifier
                .weight(7f)
                .fillMaxWidth(),
                color = Color(0xFFF4F4F4)){
                Column {
                    Spacer(modifier = Modifier.width(30.dp))

                    Row {
                        Spacer(modifier = Modifier.width(30.dp))
                        Surface (modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f), color = Color(0xFFF4F4F4),
                            shape = RoundedCornerShape(30.dp),
                        ){
                            Column {
                                Spacer(modifier = Modifier.height(40.dp))
                                Row {
                                    Spacer(modifier = Modifier.width(30.dp))
                                    TextField(
                                        value = userNameText,  // 使用狀態作為輸入值
                                        onValueChange = {
                                            onUserNameChange(it)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(0.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                        ),
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color.Black
                                        ),
                                        placeholder = {
                                            Text(stringResource(id = R.string.account),
                                                color = Color(0xFF979797)
                                            )
                                        },
                                        singleLine = true,
                                    )
                                    Spacer(modifier = Modifier.width(30.dp))
                                }

                                Row {
                                    Spacer(modifier = Modifier.width(30.dp))
                                    Spacer(modifier = Modifier
                                        .height(1.dp)
                                        .weight(1f)
                                        .background(color = Color(0xFF303236)))
                                    Spacer(modifier = Modifier.width(30.dp))
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Row (verticalAlignment = Alignment.CenterVertically){
                                    Spacer(modifier = Modifier.width(30.dp))
                                    TextField(
                                        value = passwordText,  // 使用狀態作為輸入值
                                        onValueChange = {
                                            onPasswordChange(it)
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(0.dp),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                        ),
                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                                            color = Color.Black
                                        ),
                                        placeholder = {
                                            Text(stringResource(id = R.string.password),
                                                color = Color(0xFF979797)
                                            )
                                        },
                                        singleLine = true,
                                        visualTransformation = if (pwVisibility) VisualTransformation.None else PasswordVisualTransformation()
                                    )

                                    IconButton(
                                        modifier = Modifier.size(24.dp),
                                        onClick = { pwVisibility = !pwVisibility }
                                    ) {
                                        if (pwVisibility) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.eye),
                                                tint = Color(0xFF303236),
                                                contentDescription = "Localized description"
                                            )
                                        }else {
                                            Icon(
                                                painter = painterResource(id = R.drawable.eyeclosed),
                                                tint = Color(0xFF303236),
                                                contentDescription = "Localized description"
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(30.dp))
                                }

                                Row {
                                    Spacer(modifier = Modifier.width(30.dp))
                                    Spacer(modifier = Modifier
                                        .height(1.dp)
                                        .weight(1f)
                                        .background(color = Color(0xFF303236)))
                                    Spacer(modifier = Modifier.width(30.dp))
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Row (){
                                    Surface (modifier = Modifier.weight(0.6f), color = Color.Unspecified){
                                        Row (verticalAlignment = Alignment.CenterVertically){
                                            Spacer(modifier = Modifier.width(20.dp))
                                            Checkbox(
                                                checked = rememberLoginInfoChecked,
                                                onCheckedChange = { isCheck ->
                                                    onRememberCheckedChange(isCheck)
                                                }
                                            )
                                            Text(text = stringResource(id = R.string.remember),
                                                color = Color(0xFF303236))
                                        }

                                    }
//                                    Surface (modifier = Modifier.weight(0.4f), color = Color.Unspecified){
//                                        TextButton(
//                                            onClick = {
//                                                navController.navigate("ForgotPw")
//                                            }
//                                        ) {
//                                            Text(stringResource(id = R.string.forgot_password),style = TextStyle(textDecoration = TextDecoration.Underline), color = Color(0xFF303236),)
//                                        }
//                                    }
                                }

                                Spacer(modifier = Modifier.height(30.dp))
                                Row {
                                    Spacer(modifier = Modifier.width(30.dp))
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .align(Alignment.CenterVertically)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Color(0xFF2D859D)
                                            )
                                            .clickable {
                                                onLoginClick()
                                            },

                                        color = Color.Transparent
                                    ) {
                                        Text(
                                            text = stringResource(R.string.login),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.wrapContentHeight(),
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(30.dp))
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Row {
                                    Spacer(modifier = Modifier.width(30.dp))
                                    Surface (modifier = Modifier.weight(0.4f), color = Color.Unspecified){
                                        TextButton(
                                            onClick = {
                                                if (isBiometricFlag) {
                                                    if (biometricHelper != null) {
                                                        if (biometricHelper.canAuthenticate()) {
                                                            biometricHelper?.authenticate(
                                                                onSuccess = {
                                                                    onBiometricLoginHandled()
                                                                },
                                                                onError = {
                                                                    Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                                                                }
                                                            )
                                                        } else {
                                                            showBiometricFailDialogFlag = true
                                                            showBiometricFailMsg = "BiometricNotSupportedOrDisabled"
                                                        }
                                                    }else{
                                                        showBiometricFailDialogFlag = true
                                                        showBiometricFailMsg = "BiometricNotSupported"
                                                    }

                                                }else{
                                                    showBiometricFailDialogFlag = true
                                                    showBiometricFailMsg = "BiometricLoginNotEnabled"
                                                }
                                            }
                                        ) {
                                            Text(stringResource(id = R.string.biometric_login),style = TextStyle(textDecoration = TextDecoration.Underline), color = Color(0xFF303236),)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(30.dp))
                                }

                            }
                        }
                        Spacer(modifier = Modifier.width(30.dp))
                    }

                    if (showLoadingView) {
                        LoadingView() {}
                    }
                    if (resLoginSuccessFlag) {
                        onLoginSuccessHandled()
                        navController.navigate("first")
                    }
                    if (showLoginFailMsgDialogFlag) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = parseDialogMsg(showLoginFailMsg ?: "")
                        )
                        // 在 Dialog 顯示後啟動計時器
                        LaunchedEffect(Unit) {
                            delay(1500) // 延遲 1.5 秒
                            onLoginFailDismissed()
                        }
                    }

                    if (showBiometricFailDialogFlag) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = parseDialogMsg(
                                showBiometricFailMsg
                            )
                        )
                        // 在 Dialog 顯示後啟動計時器
                        LaunchedEffect(Unit) {
                            delay(1500) // 延遲 1.5 秒
                            showBiometricFailDialogFlag = false // 自動關閉 Dialog
                        }
                    }


                }
            }
            Surface (modifier = Modifier
                .weight(3f)
                .navigationBarsPadding()
                .fillMaxWidth(),
                color = Color(0xFFF4F4F4)){
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
        }else{
//            Surface (modifier = Modifier
//                .weight(5f)
//                .fillMaxWidth(),
//                color = Color.Black){
//                Column {
//                    Spacer(modifier = Modifier.width(30.dp))
//                    Row {
//                        Spacer(modifier = Modifier.width(30.dp))
//                        Surface (modifier = Modifier
//                            .height(230.dp)
//                            .weight(1f), color = Color(0xFF2C2C2C),
//                            shape = RoundedCornerShape(30.dp),
//                        ){
//                            Column {
//                                Spacer(modifier = Modifier.height(40.dp))
//                                Row {
//                                    Spacer(modifier = Modifier.width(30.dp))
//                                    TextField(
//                                        value = registerEmail,  // 使用狀態作為輸入值
//                                        onValueChange = {
//                                            accountViewModel.updateInputEmail(it)
//                                        },
//                                        modifier = Modifier
//                                            .weight(1f)
//                                            .padding(0.dp)
//                                            .focusRequester(focusRegisterEmail)
//                                            .onFocusChanged { focusState ->
//                                                if (focusState.isFocused) {
//                                                    accountViewModel.resetShowRegisterInputFailFlag(false)
//                                                }
//                                            },
//                                        colors = TextFieldDefaults.colors(
//                                            focusedContainerColor = Color.Transparent,
//                                            unfocusedContainerColor = Color.Transparent,
//                                            focusedIndicatorColor = Color.Transparent,
//                                            unfocusedIndicatorColor = Color.Transparent,
//                                            disabledIndicatorColor = Color.Transparent,
//                                        ),
//                                        textStyle = MaterialTheme.typography.bodyLarge.copy(
//                                            color = Color.White
//                                        ),
//                                        placeholder = {
//                                            Text(stringResource(id = R.string.please_enter_email),
//                                                color = Color(0xFF979797)
//                                            )
//                                        },
//                                        singleLine = true,
//                                    )
//                                    Spacer(modifier = Modifier.width(30.dp))
//                                }
//                                if (showRegisterInputFailFlag) {
//                                    Row {
//                                        Spacer(modifier = Modifier.width(30.dp))
//                                        Spacer(modifier = Modifier
//                                            .height(1.dp)
//                                            .weight(1f)
//                                            .background(color = Color(0xFFF55454)))
//                                        Spacer(modifier = Modifier.width(30.dp))
//                                    }
//                                    Spacer(modifier = Modifier.height(10.dp))
//                                    Row {
//                                        Spacer(modifier = Modifier.width(50.dp))
//                                        Text(parseDialogMsg(showRegisterInputFailMsg ?: ""),
//                                            color = Color(0xFFF55454),
//                                            style = MaterialTheme.typography.titleSmall)
//                                    }
//                                }else{
//                                    Row {
//                                        Spacer(modifier = Modifier.width(30.dp))
//                                        Spacer(modifier = Modifier
//                                            .height(1.dp)
//                                            .weight(1f)
//                                            .background(color = Color.White))
//                                        Spacer(modifier = Modifier.width(30.dp))
//                                    }
//                                }
//                                Spacer(modifier = Modifier.height(30.dp))
//                                Row {
//                                    Spacer(modifier = Modifier.width(60.dp))
//                                    Surface(
//                                        modifier = Modifier
//                                            .weight(1f)
//                                            .height(40.dp)
//                                            .align(Alignment.CenterVertically)
//                                            .clip(RoundedCornerShape(20.dp))
//                                            .background(
//                                                brush = Brush.linearGradient(
//                                                    colors = listOf(Color(0xFF7B58DB), Color(0xFF7B357F))
//                                                )
//                                            )
//                                            .clickable {
////                                                accountViewModel.registerAction(registerEmail)
////                                                navController.navigate("VerificationCode")
//                                            },
//
//                                        color = Color.Transparent
//                                    ) {
//                                        Text(
//                                            text = stringResource(R.string.next_step),
//                                            textAlign = TextAlign.Center,
//                                            modifier = Modifier.wrapContentHeight(),
//                                            color = Color.White
//                                        )
//                                    }
//                                    Spacer(modifier = Modifier.width(60.dp))
//                                }
//
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(30.dp))
//                    }

//                    if (showLoadingView) {
//                        LoadingView() {
//
//                        }
//                    }
//                    if (showRegisterFailMsgDialogFlag) {
//                        textTNoButtonAlert(
//                            onDismissRequest = {},
//                            dialogTitle = parseDialogMsg(showRegisterFailMsg ?: "")
//                        )
//                        // 在 Dialog 顯示後啟動計時器
//                        LaunchedEffect(Unit) {
//                            delay(1500) // 延遲 1.5 秒
//                            accountViewModel.resetShowRegisterFailMsgDialogFlag(false) // 自動關閉 Dialog
//                            accountViewModel.resetShowRegisterInputFailFlag(false)
//                        }
//                    }
//                    if (resRegisterSuccessFlag) {
//                        accountViewModel.resetResRegisterSuccessFlag(false) // 自動關閉 Dialog
//                        navController.navigate("VerificationCode/${registerEmail}")
//                    }
//                }
//            }
            Surface (modifier = Modifier
                .weight(3f)
                .navigationBarsPadding()
                .fillMaxWidth(),
                color = Color.Black){
                Column (modifier = Modifier
                    .fillMaxSize(),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Row (){
                        MyCircle(selectColor = Color(0xFF4E35A9))
                        Spacer(modifier = Modifier.width(5.dp))
                        MyCircle(selectColor = Color(0xFFB6B6B6))
                        Spacer(modifier = Modifier.width(5.dp))
                        MyCircle(selectColor = Color(0xFFB6B6B6))
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

}

private fun getCurrentYear(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy") // 設定日期格式
    val today = LocalDate.now() // 取得今天的日期
    // 格式化輸出
    val yearStr = today.format(formatter)
    return yearStr
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
    if (aMsg == "AccountNotEntered") {
        msg = stringResource(id = R.string.account_not_entered)
    }else if (aMsg == "PasswordNotEntered") {
        msg = stringResource(id = R.string.password_not_entered)
    }else if (aMsg == "PasswordInvalidFormat") {
        msg = "僅能輸入數字及英文字母"
    }else if (aMsg == "BiometricLoginNotEnabled") {
        msg = stringResource(R.string.biometric_login_not_enabled)
    }else if (aMsg == "BiometricNotSupportedOrDisabled") {
        msg = stringResource(id = R.string.biometric_not_supported_or_disabled)
    }else if (aMsg == "BiometricNotSupported") {
        msg = stringResource(id = R.string.biometric_not_supported)
    }else if (aMsg == "EmailNotEntered") {

    }else {
        msg = aMsg
    }
    return msg
}

@Preview(showBackground = true)
@Composable
private fun loginView() {
    val navController = TestNavHostController(LocalContext.current)
    // 使用輔助函式建立假物件
//    val fakeApiService = rememberFakeApiService()
//    val fakeCredentialRepository = rememberFakeCredentialRepository()
//    val fakeTokenManager = rememberFakeTokenManager()
//
//    val previewAccountViewModel = remember {
//        AccountViewModel(
//            accountRepository = FakeRepositoryManager.FakeAccountRepository(fakeApiService,fakeTokenManager)
//        )
//    }
//
//    val previewLoginViewModel = LoginViewModel (
//        loginRepository = FakeRepositoryManager.FakeLoginRepository(fakeApiService,fakeTokenManager),
//        userPreferences = FakeRepositoryManager.FakeUserPreferencesRepository(LocalContext.current),
//        credentialRepository = FakeRepositoryManager.FakeCredentialRepository(LocalContext.current)
//    )

    LoginContent(navController = navController,"","",false,false,false,false,"",{},{},{},{},{},{},false,{})
}