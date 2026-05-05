package com.dae.stems_campus.ui.screen.login

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
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
fun resetPwScreen(
    navController: NavHostController,
    email: String,
    verifiedToken: String,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val pw by accountViewModel.passwordText.collectAsState()
    val confirmPw by accountViewModel.confirmPasswordText.collectAsState()
    val showLoadingView by accountViewModel.showLoadingView.collectAsState()
    val resResetPasswordSuccessFlag by accountViewModel.resResetPasswordSuccessFlag.collectAsState()
    val showResetPasswordFailDialogFlag by accountViewModel.showResetPasswordFailDialogFlag.collectAsState()
    val showResetPasswordFailMsg by accountViewModel.showResetPasswordFailMsg.collectAsState()
    val showResetPasswordInputFailTag by accountViewModel.showResetPasswordInputFailTag.collectAsState()
    val showResetPasswordInputFailMsg by accountViewModel.showResetPasswordInputFailMsg.collectAsState()

    resetPwContent(
        navController = navController,
        email = email,
        pw = pw,
        confirmPasswordText = confirmPw,
        showLoadingView = showLoadingView,
        resResetPasswordSuccessFlag = resResetPasswordSuccessFlag,
        showResetPasswordFailDialogFlag = showResetPasswordFailDialogFlag,
        showResetPasswordFailMsg = showResetPasswordFailMsg,
        showInputFailTag = showResetPasswordInputFailTag,
        showInputFailMsg = showResetPasswordInputFailMsg ?: "",
        onPasswordChange = { accountViewModel.updateInputPassword(it) },
        onConfirmPasswordChange = { accountViewModel.updateInputConfirmPassword(it) },
        onSubmitClick = {
            accountViewModel.resetPasswordAction(email, pw, confirmPw, verifiedToken)
        },
        onResetPasswordSuccessHandled = { accountViewModel.resetResResetPasswordSuccessFlag(false) },
        onResetPasswordFailDismissed = {
            accountViewModel.resetShowResetPasswordFailDialogFlag(false)
            accountViewModel.resetShowResetPasswordInputFailTag(0)
        }
    )
}

@Composable
private fun resetPwContent(
    navController: NavHostController,
    email: String,
    pw: String = "",
    confirmPasswordText: String = "",
    showLoadingView: Boolean = false,
    resResetPasswordSuccessFlag: Boolean = false,
    showResetPasswordFailDialogFlag: Boolean = false,
    showResetPasswordFailMsg: String? = null,
    showInputFailMsg: String = "",
    showInputFailTag: Int = 0,
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onResetPasswordSuccessHandled: () -> Unit = {},
    onResetPasswordFailDismissed: () -> Unit = {},
) {
    var pwVisibility by remember { mutableStateOf(false) }
    var confirmPwVisibility by remember { mutableStateOf(false) }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            topBar = {
                TopTitleBar(
                    navTitle = "",
                    navController = navController
                )
            },
            bottomBar = {},
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    Column {
                        Surface(modifier = Modifier.weight(1.5f).fillMaxWidth(), color = Color.Unspecified) {}
                        Surface(modifier = Modifier.weight(9f).fillMaxWidth(), color = Color.Unspecified) {
                            Column {
                                Row {
                                    Spacer(modifier = Modifier.width(60.dp))
                                    Text(
                                        stringResource(R.string.reset_password),
                                        color = Color.Black,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Row {
                                    Spacer(modifier = Modifier.width(30.dp))
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .weight(1f),
                                        color = Color(0xFFF4F4F4),
                                        shape = RoundedCornerShape(30.dp),
                                    ) {
                                        Column {
                                            Spacer(modifier = Modifier.height(20.dp))
                                            //新密碼
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                TextField(
                                                    value = pw,
                                                    onValueChange = { onPasswordChange(it) },
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
                                                        color = Color(0xFF303236)
                                                    ),
                                                    placeholder = {
                                                        Text(
                                                            stringResource(id = R.string.new_password),
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
                                                    } else {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.eyeclosed),
                                                            tint = Color(0xFF303236),
                                                            contentDescription = "Localized description"
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(30.dp))
                                            }
                                            //新密碼錯誤提示 / 規則提示
                                            if (showInputFailTag == 1) {
                                                Row {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Spacer(modifier = Modifier
                                                        .height(1.dp)
                                                        .weight(1f)
                                                        .background(color = Color(0xFFF55454)))
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Row {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Text(
                                                        parseDialogMsg(showInputFailMsg),
                                                        color = Color(0xFFF55454),
                                                        style = MaterialTheme.typography.titleSmall
                                                    )
                                                }
                                            } else {
                                                Row {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Spacer(modifier = Modifier
                                                        .height(1.dp)
                                                        .weight(1f)
                                                        .background(color = Color(0xFF303236)))
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Row {
                                                    Spacer(modifier = Modifier.width(50.dp))
                                                    Text(
                                                        stringResource(R.string.password_rule),
                                                        color = Color(0xFF303236),
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(20.dp))

                                            //再次輸入新密碼
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                TextField(
                                                    value = confirmPasswordText,
                                                    onValueChange = { onConfirmPasswordChange(it) },
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
                                                        color = Color(0xFF303236)
                                                    ),
                                                    placeholder = {
                                                        Text(
                                                            stringResource(id = R.string.reenter_new_password),
                                                            color = Color(0xFF979797)
                                                        )
                                                    },
                                                    singleLine = true,
                                                    visualTransformation = if (confirmPwVisibility) VisualTransformation.None else PasswordVisualTransformation()
                                                )
                                                IconButton(
                                                    modifier = Modifier.size(24.dp),
                                                    onClick = { confirmPwVisibility = !confirmPwVisibility }
                                                ) {
                                                    if (confirmPwVisibility) {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.eye),
                                                            tint = Color(0xFF303236),
                                                            contentDescription = "Localized description"
                                                        )
                                                    } else {
                                                        Icon(
                                                            painter = painterResource(id = R.drawable.eyeclosed),
                                                            tint = Color(0xFF303236),
                                                            contentDescription = "Localized description"
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(30.dp))
                                            }
                                            if (showInputFailTag == 2) {
                                                Row {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Spacer(modifier = Modifier
                                                        .height(1.dp)
                                                        .weight(1f)
                                                        .background(color = Color(0xFFF55454)))
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                }
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Row {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Text(
                                                        parseDialogMsg(showInputFailMsg),
                                                        color = Color(0xFFF55454),
                                                        style = MaterialTheme.typography.titleSmall
                                                    )
                                                }
                                            } else {
                                                Row {
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                    Spacer(modifier = Modifier
                                                        .height(1.dp)
                                                        .weight(1f)
                                                        .background(color = Color(0xFF303236)))
                                                    Spacer(modifier = Modifier.width(30.dp))
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(20.dp))
                                            Spacer(modifier = Modifier.height(50.dp))

                                            //送出
                                            Row {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                Surface(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(40.dp)
                                                        .align(Alignment.CenterVertically)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(Color(0xFF2D859D))
                                                        .clickable {
                                                            onSubmitClick()
                                                        },
                                                    color = Color.Transparent
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.confirm),
                                                        textAlign = TextAlign.Center,
                                                        modifier = Modifier.wrapContentHeight(),
                                                        color = Color.White
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(30.dp))
                                            }

                                            Spacer(modifier = Modifier.height(30.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(30.dp))
                                }
                            }
                        }

                        Surface(modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                            color = Color(0xFFF4F4F4)) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Surface(modifier = Modifier, color = Color.Unspecified) {
                                    Image(
                                        painter = painterResource(id = R.drawable.dae_logo_logo_3_1),
                                        contentDescription = ""
                                    )
                                }
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    text = "Copyright © " + getCurrentYear() + " DAE instrument CO., Ltd. All rights reserved",
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

                        if (showResetPasswordFailDialogFlag) {
                            textTNoButtonAlert(
                                onDismissRequest = {},
                                dialogTitle = parseDialogMsg(showResetPasswordFailMsg ?: "")
                            )
                            LaunchedEffect(Unit) {
                                delay(1500)
                                onResetPasswordFailDismissed()
                            }
                        }
                        if (resResetPasswordSuccessFlag) {
                            textTNoButtonAlert(
                                onDismissRequest = {},
                                dialogTitle = "密碼重設成功"
                            )
                            LaunchedEffect(Unit) {
                                delay(1500)
                                onResetPasswordSuccessHandled()
                                navController.popBackStack("signIn", inclusive = false)
                            }
                        }
                    }
                }
            }
        )
    }
}

private fun getCurrentYear(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy")
    val today = LocalDate.now()
    return today.format(formatter)
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
private fun parseDialogMsg(aMsg: String): (String) {
    var msg: String = ""
    if (aMsg == "PasswordNotEntered") {
        msg = stringResource(id = R.string.password_not_entered)
    } else if (aMsg == "PasswordInvalidFormat") {
        msg = "僅能輸入英文、數字及特殊符號"
    } else if (aMsg == "PasswordMismatch") {
        msg = "兩次輸入密碼不一致"
    } else {
        msg = aMsg
    }
    return msg
}

@Preview(showBackground = true)
@Composable
private fun resetPwPreview() {
    val navController = TestNavHostController(LocalContext.current)
    resetPwContent(navController = navController, email = "")
}
