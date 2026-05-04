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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun forgotPwScreen(
    navController: NavHostController,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val email by accountViewModel.emailText.collectAsState()
    val showLoadingView by accountViewModel.showLoadingView.collectAsState()
    val resForgotPwSendEmailSuccessFlag by accountViewModel.resForgotPwSendEmailSuccessFlag.collectAsState()
    val showForgotPwSendEmailFailDialogFlag by accountViewModel.showForgotPwSendEmailFailDialogFlag.collectAsState()
    val showForgotPwSendEmailFailMsg by accountViewModel.showForgotPwSendEmailFailMsg.collectAsState()
    val showForgotPwEmailInputFailFlag by accountViewModel.showForgotPwEmailInputFailFlag.collectAsState()
    val showForgotPwEmailInputFailMsg by accountViewModel.showForgotPwEmailInputFailMsg.collectAsState()

    forgotPwContent(
        navController = navController,
        email = email,
        showLoadingView = showLoadingView,
        showForgotPwEmailInputFailFlag = showForgotPwEmailInputFailFlag,
        showForgotPwEmailInputFailMsg = showForgotPwEmailInputFailMsg,
        resForgotPwSendEmailSuccessFlag = resForgotPwSendEmailSuccessFlag,
        showForgotPwSendEmailFailDialogFlag = showForgotPwSendEmailFailDialogFlag,
        showForgotPwSendEmailFailMsg = showForgotPwSendEmailFailMsg,
        onEmailChange = { accountViewModel.updateInputEmail(it) },
        onEmailFocused = { accountViewModel.resetShowForgotPwEmailInputFailFlag(false) },
        onSendEmailClick = { accountViewModel.forgotPwSendEmailAction(email) },
        onSendEmailSuccessHandled = {
            accountViewModel.resetResForgotPwSendEmailSuccessFlag(false)
            accountViewModel.updateInputEmail("")
        },
        onSendEmailFailDismissed = {
            accountViewModel.resetShowForgotPwSendEmailFailDialogFlag(false)
            accountViewModel.resetShowForgotPwEmailInputFailFlag(false)
        }
    )
}

@Composable
private fun forgotPwContent(
    navController: NavHostController,
    email: String = "",
    showLoadingView: Boolean = false,
    showForgotPwEmailInputFailFlag: Boolean = false,
    showForgotPwEmailInputFailMsg: String? = null,
    resForgotPwSendEmailSuccessFlag: Boolean = false,
    showForgotPwSendEmailFailDialogFlag: Boolean = false,
    showForgotPwSendEmailFailMsg: String? = null,
    onEmailChange: (String) -> Unit = {},
    onEmailFocused: () -> Unit = {},
    onSendEmailClick: () -> Unit = {},
    onSendEmailSuccessHandled: () -> Unit = {},
    onSendEmailFailDismissed: () -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }

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
                        Surface(modifier = Modifier
                            .weight(1.5f)
                            .fillMaxWidth(), color = Color.Unspecified) {}

                        Surface(modifier = Modifier
                            .weight(7f)
                            .fillMaxWidth(),
                            color = Color(0xFFF4F4F4)) {
                            Column {
                                Row {
                                    Spacer(modifier = Modifier.width(60.dp))
                                    Text(
                                        stringResource(R.string.forgot_password),
                                        color = Color.Black,
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                }
                                Spacer(modifier = Modifier.height(20.dp))
                                Row {
                                    Spacer(modifier = Modifier.width(30.dp))
                                    Surface(modifier = Modifier
                                        .fillMaxHeight()
                                        .weight(1f),
                                        color = Color(0xFFF4F4F4),
                                        shape = RoundedCornerShape(30.dp),
                                    ) {
                                        Column {
                                            Spacer(modifier = Modifier.height(40.dp))
                                            Row {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                TextField(
                                                    value = email,
                                                    onValueChange = { onEmailChange(it) },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(0.dp)
                                                        .focusRequester(focusRequester)
                                                        .onFocusChanged { focusState ->
                                                            if (focusState.isFocused) {
                                                                onEmailFocused()
                                                            }
                                                        },
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
                                                        Text(
                                                            stringResource(id = R.string.please_enter_email),
                                                            color = Color(0xFF979797)
                                                        )
                                                    },
                                                    singleLine = true,
                                                )
                                                Spacer(modifier = Modifier.width(30.dp))
                                            }
                                            if (showForgotPwEmailInputFailFlag) {
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
                                                    Spacer(modifier = Modifier.width(50.dp))
                                                    Text(
                                                        parseDialogMsg(showForgotPwEmailInputFailMsg ?: ""),
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
                                            Spacer(modifier = Modifier.height(30.dp))
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
                                                            onSendEmailClick()
                                                        },
                                                    color = Color.Transparent
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.send_verification_code),
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

                        Surface(modifier = Modifier
                            .weight(3f)
                            .fillMaxWidth(),
                            color = Color(0xFFF4F4F4)) {
                            Column(modifier = Modifier
                                .fillMaxSize(),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.CenterHorizontally) {
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
                        if (showForgotPwSendEmailFailDialogFlag) {
                            textTNoButtonAlert(
                                onDismissRequest = {},
                                dialogTitle = parseDialogMsg(showForgotPwSendEmailFailMsg ?: "")
                            )
                            LaunchedEffect(Unit) {
                                delay(1500)
                                onSendEmailFailDismissed()
                            }
                        }
                        if (resForgotPwSendEmailSuccessFlag) {
                            onSendEmailSuccessHandled()
                            navController.navigate("ForgotPwVerificationCode/${email}")
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
    if (aMsg == "EmailNotEntered") {
        msg = "請輸入 E-mail"
    } else if (aMsg == "EmailFormatInvalid") {
        msg = "E-mail 格式錯誤"
    } else {
        msg = aMsg
    }
    return msg
}

@Preview(showBackground = true)
@Composable
private fun forgotPwPreview() {
    val navController = TestNavHostController(LocalContext.current)
    forgotPwContent(navController = navController, email = "")
}
