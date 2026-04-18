package com.dae.stems_campus.ui.screen.pay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.utils.calculateDuration
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay


@Composable
fun walletScreen(mainNavController: NavController, profileViewModel: ProfileViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {
    val walletNavController = rememberNavController()

    NavHost(navController = walletNavController, startDestination = "Wallet") {
        composable("Wallet") {
            walletMainLoad(mainNavController = mainNavController, navController = walletNavController, profileViewModel, onShowTabBarChange = {})
        }
        composable("TopUpBinding") {
            topUpBindingScreen(navController = walletNavController, onShowTabBarChange = {})
        }
        composable("TopUpInfo/{depositCode}") { backStackEntry ->
            val depositCode = backStackEntry.arguments?.getString("depositCode")
            topUpInfoScreen(navController = walletNavController, depositCode = depositCode ?: "", onShowTabBarChange = {})
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun walletMainLoad(mainNavController: NavController, navController: NavHostController, profileViewModel: ProfileViewModel,  onShowTabBarChange: (Boolean) -> Unit) {

    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val showLoadingView by profileViewModel.showLoadingView.collectAsState()
    val resGetProfileInfoSuccessFlag by profileViewModel.resGetProfileInfoSuccessFlag.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getProfileInfoAction()
    }

    walletContent(mainNavController = mainNavController,
        navController = navController,
        profileInfo = profileInfo)

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
private fun walletContent(
    mainNavController: NavController,
    navController: NavHostController,
    profileInfo: ProfileModel.ProfileData? = null,) {

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF4F4F4))) {

        Column {
            Surface (modifier = Modifier
                .weight(0.15f)
                .fillMaxWidth(),  color = Color.Unspecified){
                Column {
                    Spacer(modifier = Modifier.height(50.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(25.dp))
                        Text("${stringResource(R.string.wallet)}", color = Color.Black,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)

//                        Surface (modifier = Modifier.weight(1f)){  }
//                        Surface (modifier = Modifier, color = Color.Unspecified){
//                            Surface (modifier = Modifier
//                                .clickable {
//                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
//                                }
//                                , color = Color.Unspecified)
//                            {
//                                Image(painter = painterResource(id = R.drawable.qrcode), contentDescription = "")
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(20.dp))
//                        Surface (modifier = Modifier, color = Color.Unspecified){
//                            Surface (modifier = Modifier
//                                , color = Color.Unspecified)
//                            {
//                                Image(painter = painterResource(id = R.drawable.bell), contentDescription = "")
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Surface (modifier = Modifier.weight(0.9f).verticalScroll(rememberScrollState()), color = Color.Unspecified) {
                Column {
                    if (profileInfo?.role.equals("staff")) {
                        infoViewByTeacher(profileInfo)
                    }else if (profileInfo?.role.equals("student")){
                        infoViewByStudent(profileInfo)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(stringResource(R.string.wallet_top_up), color = Color(0xFF2D859D), style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    //<-----儲值機儲值----->
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate("TopUpBinding")
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
                                        Text(stringResource(R.string.top_up_machine_top_up), color = Color.Black, style = MaterialTheme.typography.titleMedium)
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
                    //<-----行動支付儲值----->
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f),
                            color = Color.White,
                            shape = RoundedCornerShape(0.dp)){

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
                                        Text(stringResource(R.string.mobile_payment_top_up), color = Color.Black, style = MaterialTheme.typography.titleMedium)
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



                    // <-----申請退款----->
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .clickable {
//                                    navController.navigate("changeName/${accountName}")
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
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Text(stringResource(R.string.apply_for_refund), color = Color(0xFFC82C2C), style = MaterialTheme.typography.titleMedium)
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
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun infoViewByTeacher(profileInfo: ProfileModel.ProfileData?) {
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable {  },
            color = Color(0xFFD08024),
            shape = RoundedCornerShape(9.dp)
        ) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Surface (modifier = Modifier
                    .weight(1f)
                    , color = Color(0xFFD08024)){
                    Column (){
                        Row (verticalAlignment = Alignment.CenterVertically){
                            Spacer(modifier = Modifier.width(15.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.stopcircle),
                                contentDescription = "",
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(stringResource(R.string.available_hours), color = Color.White,style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("${profileInfo?.hoursBalance?.calculateDuration()}", color = Color.White,style = MaterialTheme.typography.titleLarge,fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row (verticalAlignment = Alignment.CenterVertically){
                            Spacer(modifier = Modifier.width(30.dp))
                            Text("13小時42分鐘 將於 2026-12-31 到期", color = Color.White,style = MaterialTheme.typography.bodySmall)
                        }

                    }
                }
                Surface (
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.3f)
                        .height(80.dp),
                    color = Color(0xFFD08024)
                ){
                    Row (verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.weight(1f))
                        Text(stringResource(R.string.school_issued_points), color = Color.White,style = MaterialTheme.typography.bodyMedium, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color.White , RoundedCornerShape(15.dp)).padding(5.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                }
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
    Spacer(modifier = Modifier.height(20.dp))
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
                            Spacer(modifier = Modifier.width(15.dp))
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
}

@Composable
private fun infoViewByStudent(profileInfo: ProfileModel.ProfileData?) {
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
private fun WalletPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    walletContent (navController,navController)
}