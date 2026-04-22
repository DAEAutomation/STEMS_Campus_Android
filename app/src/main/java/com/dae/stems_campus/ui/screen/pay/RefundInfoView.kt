package com.dae.stems_campus.ui.screen.pay

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.viewmodel.ProfileViewModel
import com.dae.stems_campus.viewmodel.RefundViewModel
import kotlinx.coroutines.delay

@Composable
fun refundInfoScreen(navController: NavHostController, profileViewModel: ProfileViewModel = hiltViewModel(), refundViewModel: RefundViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {

    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val showLoadingView by profileViewModel.showLoadingView.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()



    LaunchedEffect(Unit) {
        profileViewModel.getProfileInfoAction()
    }

    refundInfoContent(
        navController = navController,
        onShowTabBarChange = {},
        profileInfo = profileInfo,
    )

    if (showLoadingView) {
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
            navController.navigateUp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun refundInfoContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    profileInfo: ProfileModel.ProfileData? = null,
    ) {

    var showingRefundBottomSheet by remember { mutableStateOf(false) }
    val refundSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.apply_for_refund),
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
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {  },
                                color = Color(0xFF2D859D),
                                shape = RoundedCornerShape(9.dp)
                            ) {
                                Row (verticalAlignment = Alignment.CenterVertically){
                                    Surface (modifier = Modifier
                                        .weight(1f)
                                        , color = Color(0xFF2D859D)){
                                        Column (){
                                            Spacer(modifier = Modifier.height(20.dp))
                                            Row (verticalAlignment = Alignment.CenterVertically){
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Icon(
                                                    painter = painterResource(id = R.drawable.wallet_w),
                                                    contentDescription = "",
                                                    tint = Color.Unspecified
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(stringResource(R.string.wallet), color = Color.White,style = MaterialTheme.typography.bodyLarge)
                                                Row (verticalAlignment = Alignment.Bottom){
                                                    Spacer(modifier = Modifier.width(20.dp))
                                                    Text("${profileInfo?.balance?.toAmountString()}", color = Color.White, style = MaterialTheme.typography.headlineLarge,fontWeight = FontWeight.Bold)
                                                    Spacer(modifier = Modifier.width(15.dp))
                                                    Text(stringResource(R.string.currency_unit), color = Color.White, style = MaterialTheme.typography.bodyLarge)
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(20.dp))
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        //<-----現金退款----->
                        Row {
                            Spacer(modifier = Modifier.width(20.dp))
                            Surface (modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    showingRefundBottomSheet = true
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
                                            Text(stringResource(R.string.cash_refund), color = Color.Black, style = MaterialTheme.typography.titleLarge)
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(stringResource(R.string.cash_refund_contact_notice), color = Color.Black, style = MaterialTheme.typography.bodySmall)
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
                                            painter = painterResource(id = R.drawable.caretright_b),
                                            tint = Color.Unspecified,
                                            contentDescription = "Localized description"
                                        )
                                    }


                                    Spacer(modifier = Modifier.width(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                        Spacer(modifier = Modifier.height(2.dp))

                        // <-----悠遊卡退款----->
//                        Row {
//                            Spacer(modifier = Modifier.width(20.dp))
//                            Surface (modifier = Modifier
//                                .weight(1f)
//                                .clickable {
////                                    navController.navigate("changeName/${accountName}")
//                                },
//
//                                color = Color.White,
//                                shape = RoundedCornerShape(
//                                    topStart = 0.dp,
//                                    topEnd = 0.dp,
//                                    bottomStart = 9.dp,
//                                    bottomEnd = 9.dp)){
//
//                                Row (verticalAlignment = Alignment.CenterVertically){
//                                    Spacer(modifier = Modifier.width(20.dp))
//
//                                    Surface (
//                                        modifier = Modifier
//                                            .align(Alignment.CenterVertically)
//                                            .weight(0.9f),
//                                        color = Color.Unspecified
//                                    ){
//                                        Column {
//                                            Spacer(modifier = Modifier.height(20.dp))
//                                            Text(stringResource(R.string.easy_card_refund), color = Color.Black, style = MaterialTheme.typography.titleLarge)
//                                            Spacer(modifier = Modifier.height(5.dp))
//                                            Text("請至校內自助儲值機操作", color = Color.Black, style = MaterialTheme.typography.bodySmall)
//                                            Spacer(modifier = Modifier.height(20.dp))
//                                        }
//                                    }
//                                    Surface (
//                                        modifier = Modifier
//                                            .align(Alignment.CenterVertically)
//                                            .weight(0.1f),
//                                        color = Color.Unspecified
//                                    ){
//                                        Icon(
//                                            painter = painterResource(id = R.drawable.caretright_b),
//                                            tint = Color.Unspecified,
//                                            contentDescription = "Localized description"
//                                        )
//                                    }
//                                    Spacer(modifier = Modifier.width(20.dp))
//                                }
//                            }
//                            Spacer(modifier = Modifier.width(20.dp))
//                        }


                        if (showingRefundBottomSheet) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    showingRefundBottomSheet = false
                                },
                                sheetState = refundSheetState,
                                containerColor = Color.White
                            ) {
                                refundBottomSheetView(profileInfo, onRefundHandled = {
                                    showingRefundBottomSheet = false
                                    navController.navigate("RefundCash")
                                }, onCancelHandled = {
                                    showingRefundBottomSheet = false
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
private fun refundBottomSheetView(aProfileData: ProfileModel.ProfileData?, onRefundHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface (modifier = Modifier, color = Color.Unspecified){
                        Image(painter = painterResource(id = R.drawable.currencycircledollar), contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text("即將申請全額退款", color = Color.Black, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("*退款期間不得儲值及用電", color = Color(0xFFE54343), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }
        Row (){
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp),
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 15.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${stringResource(R.string.applicant)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = "${aProfileData?.name}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${"餘額："}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${aProfileData?.balance?.toAmountString()}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Text("${stringResource(R.string.currency_unit)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                        onRefundHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.apply_for_refund),
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
private fun refundInfoPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    refundInfoContent (navController,{},null)
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetViewPreview() {
    STEMS_CampusTheme {
        refundBottomSheetView(null, {}, {})
    }

}