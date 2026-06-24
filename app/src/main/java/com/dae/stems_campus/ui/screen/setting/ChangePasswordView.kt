package com.dae.stems_campus.ui.screen.setting

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.viewmodel.SettingViewModel
import kotlinx.coroutines.delay

@Composable
fun changePasswordScreen(
    mainNavController: NavController,
    navController: NavHostController,
    settingViewModel: SettingViewModel
) {
    val oldPw by settingViewModel.oldPasswordText.collectAsState()
    val newPw by settingViewModel.newPasswordText.collectAsState()
    val confirmNewPw by settingViewModel.confirmNewPasswordText.collectAsState()
    val showLoadingView by settingViewModel.showLoadingView.collectAsState()
    val resChangePasswordSuccessFlag by settingViewModel.resChangePasswordSuccessFlag.collectAsState()
    val showChangePasswordFailDialogFlag by settingViewModel.showChangePasswordFailDialogFlag.collectAsState()
    val showChangePasswordFailMsg by settingViewModel.showChangePasswordFailMsg.collectAsState()
    val showChangePasswordInputFailTag by settingViewModel.showChangePasswordInputFailTag.collectAsState()
    val showChangePasswordInputFailMsg by settingViewModel.showChangePasswordInputFailMsg.collectAsState()

    // 離開頁面時清空輸入
    DisposableEffect(Unit) {
        onDispose { settingViewModel.resetChangePasswordInputs() }
    }

    changePasswordContent(
        navController = navController,
        oldPw = oldPw,
        newPw = newPw,
        confirmNewPw = confirmNewPw,
        showLoadingView = showLoadingView,
        resChangePasswordSuccessFlag = resChangePasswordSuccessFlag,
        showChangePasswordFailDialogFlag = showChangePasswordFailDialogFlag,
        showChangePasswordFailMsg = showChangePasswordFailMsg,
        showInputFailTag = showChangePasswordInputFailTag,
        showInputFailMsg = showChangePasswordInputFailMsg ?: "",
        onOldPasswordChange = { settingViewModel.updateInputOldPassword(it) },
        onNewPasswordChange = { settingViewModel.updateInputNewPassword(it) },
        onConfirmNewPasswordChange = { settingViewModel.updateInputConfirmNewPassword(it) },
        onSubmitClick = {
            settingViewModel.changePasswordAction(oldPw, newPw, confirmNewPw)
        },
        onChangePasswordSuccessHandled = {
            settingViewModel.resetResChangePasswordSuccessFlag(false)
            mainNavController.navigate("signIn") {
                popUpTo(0) {
                    inclusive = true // 包含起始頁一起清掉
                }
                launchSingleTop = true
            }
        },
        onChangePasswordFailDismissed = {
            settingViewModel.resetShowChangePasswordFailDialogFlag(false)
        }
    )
}

@Composable
private fun changePasswordContent(
    navController: NavHostController,
    oldPw: String = "",
    newPw: String = "",
    confirmNewPw: String = "",
    showLoadingView: Boolean = false,
    resChangePasswordSuccessFlag: Boolean = false,
    showChangePasswordFailDialogFlag: Boolean = false,
    showChangePasswordFailMsg: String? = null,
    showInputFailTag: Int = 0,
    showInputFailMsg: String = "",
    onOldPasswordChange: (String) -> Unit = {},
    onNewPasswordChange: (String) -> Unit = {},
    onConfirmNewPasswordChange: (String) -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onChangePasswordSuccessHandled: () -> Unit = {},
    onChangePasswordFailDismissed: () -> Unit = {},
) {
    var oldPwVisibility by remember { mutableStateOf(false) }
    var newPwVisibility by remember { mutableStateOf(false) }
    var confirmNewPwVisibility by remember { mutableStateOf(false) }

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
                    navTitle = stringResource(R.string.change_password),
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
                            .padding(horizontal = 25.dp)
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

                        // 舊密碼
                        passwordField(
                            label = stringResource(R.string.old_password),
                            value = oldPw,
                            placeholder = stringResource(R.string.enter_old_password),
                            isError = showInputFailTag == 1,
                            errorMsg = if (showInputFailTag == 1) parseChangePasswordMsg(showInputFailMsg) else "",
                            visibility = oldPwVisibility,
                            onValueChange = onOldPasswordChange,
                            onVisibilityToggle = { oldPwVisibility = !oldPwVisibility }
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        // 新密碼
                        passwordField(
                            label = stringResource(R.string.new_password),
                            value = newPw,
                            placeholder = stringResource(R.string.enter_new_password),
                            isError = showInputFailTag == 2,
                            errorMsg = if (showInputFailTag == 2) parseChangePasswordMsg(showInputFailMsg) else "",
                            hint = stringResource(R.string.password_rule),
                            visibility = newPwVisibility,
                            onValueChange = onNewPasswordChange,
                            onVisibilityToggle = { newPwVisibility = !newPwVisibility }
                        )

                        Spacer(modifier = Modifier.height(25.dp))

                        // 再次輸入新密碼
                        passwordField(
                            label = stringResource(R.string.reenter_new_password),
                            value = confirmNewPw,
                            placeholder = stringResource(R.string.reenter_new_password),
                            isError = showInputFailTag == 3,
                            errorMsg = if (showInputFailTag == 3) parseChangePasswordMsg(showInputFailMsg) else "",
                            visibility = confirmNewPwVisibility,
                            onValueChange = onConfirmNewPasswordChange,
                            onVisibilityToggle = { confirmNewPwVisibility = !confirmNewPwVisibility }
                        )

                        Spacer(modifier = Modifier.height(60.dp))

                        // 變更密碼 按鈕
                        Row {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(45.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF2D859D))
                                    .clickable { onSubmitClick() },
                                color = Color.Transparent
                            ) {
                                Text(
                                    text = stringResource(R.string.change_password),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(Alignment.CenterVertically),
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    if (showLoadingView) {
                        LoadingView() {}
                    }

                    if (showChangePasswordFailDialogFlag) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = parseChangePasswordMsg(showChangePasswordFailMsg ?: "")
                        )
                        LaunchedEffect(Unit) {
                            delay(1500)
                            onChangePasswordFailDismissed()
                        }
                    }

                    if (resChangePasswordSuccessFlag) {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = "密碼變更成功"
                        )
                        LaunchedEffect(Unit) {
                            delay(1500)
                            onChangePasswordSuccessHandled()
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

@Composable
private fun passwordField(
    label: String,
    value: String,
    placeholder: String,
    isError: Boolean,
    errorMsg: String,
    hint: String = "",
    visibility: Boolean,
    onValueChange: (String) -> Unit,
    onVisibilityToggle: () -> Unit,
) {
    Column {
        Text(
            text = label,
            color = Color.Black,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(color = Color(0xFF303236), fontSize = 16.sp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color(0xFF979797),
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
            IconButton(
                modifier = Modifier.size(24.dp),
                onClick = onVisibilityToggle
            ) {
                Icon(
                    painter = painterResource(
                        id = if (visibility) R.drawable.eye else R.drawable.eyeclosed
                    ),
                    tint = Color(0xFF303236),
                    contentDescription = "toggle password visibility"
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(if (isError) Color(0xFFF55454) else Color(0xFF303236))
        )
        if (isError && errorMsg.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = errorMsg,
                color = Color(0xFFF55454),
                style = MaterialTheme.typography.bodySmall
            )
        } else if (hint.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = hint,
                color = Color(0xFF979797),
                style = MaterialTheme.typography.bodySmall
            )
        }
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
private fun parseChangePasswordMsg(aMsg: String): String {
    return when (aMsg) {
        "OldPasswordNotEntered" -> "尚未輸入舊密碼"
        "NewPasswordNotEntered" -> "尚未輸入新密碼"
        "NewPasswordInvalidFormat" -> stringResource(R.string.password_rule)
        "PasswordMismatch" -> "兩次輸入密碼不一致"
        "PleaseReLogin" -> stringResource(id = R.string.please_re_login)
        else -> aMsg
    }
}

@Preview(showBackground = true)
@Composable
private fun changePasswordPreview() {
    val navController = TestNavHostController(LocalContext.current)
    changePasswordContent(navController = navController)
}
