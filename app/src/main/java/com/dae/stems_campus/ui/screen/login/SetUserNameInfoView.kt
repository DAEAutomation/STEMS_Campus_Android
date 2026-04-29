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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.dae.stems_campus.R
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.viewmodel.AccountViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun setUserNameInfoScreen(
    navController: NavHostController,
    email: String,
    verifiedToken: String,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val studentId by accountViewModel.studentIdText.collectAsState()
    val name by accountViewModel.nameText.collectAsState()
    val phone by accountViewModel.phoneText.collectAsState()
    val department by accountViewModel.departmentText.collectAsState()
    val studentType by accountViewModel.studentTypeText.collectAsState()
    val showLoadingView by accountViewModel.showLoadingView.collectAsState()
    val resGetStudentInfoSuccessFlag by accountViewModel.resGetStudentInfoSuccessFlag.collectAsState()
    val showGetStudentInfoFailDialogFlag by accountViewModel.showGetStudentInfoFailDialogFlag.collectAsState()
    val showGetStudentInfoFailMsg by accountViewModel.showGetStudentInfoFailMsg.collectAsState()
    val isStudentVerified by accountViewModel.isStudentVerified.collectAsState()

    setUserNameInfoContent(
        navController = navController,
        email = email,
        studentId = studentId,
        name = name,
        phone = phone,
        department = department,
        studentType = studentType,
        showLoadingView = showLoadingView,
        resGetStudentInfoSuccessFlag = resGetStudentInfoSuccessFlag,
        showGetStudentInfoFailDialogFlag = showGetStudentInfoFailDialogFlag,
        showGetStudentInfoFailMsg = showGetStudentInfoFailMsg,
        onStudentIdChange = { accountViewModel.updateInputStudentId(it) },
        onVerifyStudentClick = { accountViewModel.getStudentInfoAction(email, studentId, verifiedToken) },
        onGetStudentInfoSuccessHandled = { accountViewModel.resetResGetStudentInfoSuccessFlag(false) },
        onGetStudentInfoFailDismissed = { accountViewModel.resetShowGetStudentInfoFailDialogFlag(false) },
        onNextStepClick = {
            if (isStudentVerified) {
                navController.navigate("SetPassword/${email}/${verifiedToken}/${studentId}")
            } else {
                accountViewModel.showStudentNotVerifiedAlert()
            }
        }
    )
}

@Composable
private fun setUserNameInfoContent(
    navController: NavHostController,
    email: String,
    studentId: String = "",
    name: String = "",
    phone: String = "",
    department: String = "",
    studentType: String = "",
    showLoadingView: Boolean = false,
    resGetStudentInfoSuccessFlag: Boolean = false,
    showGetStudentInfoFailDialogFlag: Boolean = false,
    showGetStudentInfoFailMsg: String? = null,
    onStudentIdChange: (String) -> Unit = {},
    onVerifyStudentClick: () -> Unit = {},
    onGetStudentInfoSuccessHandled: () -> Unit = {},
    onGetStudentInfoFailDismissed: () -> Unit = {},
    onNextStepClick: () -> Unit = {},
) {

    val focusRequester1 = remember { FocusRequester() }

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
                        Surface (modifier = Modifier.weight(9f).fillMaxWidth(), color = Color.Unspecified){
                            Column {
                                Row {
                                    Spacer(modifier = Modifier.width(60.dp))
                                    Text(stringResource(R.string.set_name_and_info),
                                        color = Color.Black,
                                        style = MaterialTheme.typography.headlineSmall)
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
                                            Spacer(modifier = Modifier.height(20.dp))
                                            //學號 + 驗證按鈕
                                            Row (verticalAlignment = Alignment.CenterVertically){
                                                Spacer(modifier = Modifier.width(30.dp))
                                                TextField(
                                                    value = studentId,
                                                    onValueChange = {
                                                        onStudentIdChange(it)
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
                                                        color = Color(0xFF303236)
                                                    ),
                                                    placeholder = {
                                                        Text(stringResource(id = R.string.student_id),
                                                            color = Color(0xFF979797)
                                                        )
                                                    },
                                                    singleLine = true,
                                                )
                                                Surface(
                                                    modifier = Modifier
                                                        .height(36.dp)
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(Color(0xFF2D859D))
                                                        .clickable {
                                                            onVerifyStudentClick()
                                                        },
                                                    color = Color.Transparent
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Text(
                                                            text = stringResource(R.string.verify),
                                                            color = Color.White,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        Spacer(modifier = Modifier.width(12.dp))
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

                                            //姓名（唯讀）
                                            Row {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                TextField(
                                                    value = name,
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    enabled = false,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(0.dp),
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.Transparent,
                                                        unfocusedContainerColor = Color.Transparent,
                                                        disabledContainerColor = Color.Transparent,
                                                        focusedIndicatorColor = Color.Transparent,
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        disabledIndicatorColor = Color.Transparent,
                                                        disabledTextColor = Color(0xFF303236),
                                                    ),
                                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color(0xFF303236)
                                                    ),
                                                    placeholder = {
                                                        Text(stringResource(id = R.string.name),
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

                                            //手機號碼（唯讀）
                                            Row {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                TextField(
                                                    value = phone,
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    enabled = false,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(0.dp),
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.Transparent,
                                                        unfocusedContainerColor = Color.Transparent,
                                                        disabledContainerColor = Color.Transparent,
                                                        focusedIndicatorColor = Color.Transparent,
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        disabledIndicatorColor = Color.Transparent,
                                                        disabledTextColor = Color(0xFF303236),
                                                    ),
                                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color(0xFF303236)
                                                    ),
                                                    placeholder = {
                                                        Text(stringResource(id = R.string.phone_number),
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

                                            //科系（唯讀）
                                            Row {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                TextField(
                                                    value = department,
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    enabled = false,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(0.dp),
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.Transparent,
                                                        unfocusedContainerColor = Color.Transparent,
                                                        disabledContainerColor = Color.Transparent,
                                                        focusedIndicatorColor = Color.Transparent,
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        disabledIndicatorColor = Color.Transparent,
                                                        disabledTextColor = Color(0xFF303236),
                                                    ),
                                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color(0xFF303236)
                                                    ),
                                                    placeholder = {
                                                        Text(stringResource(id = R.string.department),
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

                                            //身份（唯讀）
                                            Row {
                                                Spacer(modifier = Modifier.width(30.dp))
                                                TextField(
                                                    value = studentType,
                                                    onValueChange = {},
                                                    readOnly = true,
                                                    enabled = false,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(0.dp),
                                                    colors = TextFieldDefaults.colors(
                                                        focusedContainerColor = Color.Transparent,
                                                        unfocusedContainerColor = Color.Transparent,
                                                        disabledContainerColor = Color.Transparent,
                                                        focusedIndicatorColor = Color.Transparent,
                                                        unfocusedIndicatorColor = Color.Transparent,
                                                        disabledIndicatorColor = Color.Transparent,
                                                        disabledTextColor = Color(0xFF303236),
                                                    ),
                                                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                        color = Color(0xFF303236)
                                                    ),
                                                    placeholder = {
                                                        Text(stringResource(id = R.string.identity),
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

                                            Spacer(modifier = Modifier.height(50.dp))
                                            Row {
                                                Spacer(modifier = Modifier.width(30.dp))
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
                                                            onNextStepClick()
                                                        },

                                                    color = Color.Transparent
                                                ) {
                                                    Text(
                                                        text = stringResource(R.string.next_step),
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
                        Surface (modifier = Modifier.weight(1f).fillMaxWidth(), color = Color(0xFFF4F4F4)){
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

                        if (showGetStudentInfoFailDialogFlag) {
                            textTNoButtonAlert(
                                onDismissRequest = {},
                                dialogTitle = parseDialogMsg(showGetStudentInfoFailMsg ?: "")
                            )
                            LaunchedEffect(Unit) {
                                delay(1500)
                                onGetStudentInfoFailDismissed()
                            }
                        }
                        if (resGetStudentInfoSuccessFlag) {
                            onGetStudentInfoSuccessHandled()
                        }
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
    if (aMsg == "StudentIdNotEntered") {
        msg = "請輸入學號"
    }else if (aMsg == "StudentNotVerified") {
        msg = "請先驗證學號取得學生資料"
    }else {
        msg = aMsg
    }
    return msg
}

@Preview(showBackground = true)
@Composable
private fun setUserNameInfoPreview() {
    val navController = TestNavHostController(LocalContext.current)

    setUserNameInfoContent(navController = navController, email = "")
}