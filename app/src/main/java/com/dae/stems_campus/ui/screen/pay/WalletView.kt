package com.dae.stems_campus.ui.screen.pay

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.utils.calculateDuration
import com.dae.stems_campus.utils.toLocalDateTimeText
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.viewmodel.PayViewModel
import com.dae.stems_campus.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.listOf


@Composable
fun walletScreen(mainNavController: NavController, profileViewModel: ProfileViewModel = hiltViewModel(), payViewModel: PayViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {
    val walletNavController = rememberNavController()

    val backStackEntry by walletNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        onShowTabBarChange(currentRoute == null || currentRoute == "Wallet")
    }

    NavHost(navController = walletNavController, startDestination = "Wallet") {
        composable("Wallet") {
            walletMainLoad(mainNavController = mainNavController, navController = walletNavController, profileViewModel, payViewModel, onShowTabBarChange = {})
        }
        composable("TopUpBinding") {
            topUpBindingScreen(navController = walletNavController, onShowTabBarChange = {})
        }
        composable("TopUpInfo/{depositCode}") { backStackEntry ->
            val depositCode = backStackEntry.arguments?.getString("depositCode")
            topUpInfoScreen(navController = walletNavController, depositCode = depositCode ?: "", onShowTabBarChange = {})
        }
        composable("Refund") {
            refundInfoScreen(navController = walletNavController, onShowTabBarChange = {})
        }
        composable("RefundCash") {
            refundCashDetailScreen(navController = walletNavController, onShowTabBarChange = {})
        }
        composable("RefundEasy") {
            refundEasyDetailScreen(navController = walletNavController, onShowTabBarChange = {})
        }
        composable(
            route = "RefundBinding/{refundID}",
            arguments = listOf(
                navArgument("refundID") { type = NavType.IntType },
        )) { backStackEntry ->
            val refundID = backStackEntry.arguments?.getInt("refundID")
            refundBindingScreen(navController = walletNavController, refundID = refundID ?: 0, onShowTabBarChange = {})
        }
        composable(
            route = "RefundDeposit/{depositCode}/{refundID}",
            arguments = listOf(
                navArgument("depositCode") { type = NavType.StringType },
                navArgument("refundID") { type = NavType.IntType })
        ) { backStackEntry ->
            val depositCode = backStackEntry.arguments?.getString("depositCode")
            val refundID = backStackEntry.arguments?.getInt("refundID")
            refundDepositScreen(navController = walletNavController, depositCode = depositCode ?: "", refundID = refundID ?: 0 , onShowTabBarChange = {})
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun walletMainLoad(mainNavController: NavController, navController: NavHostController, profileViewModel: ProfileViewModel, payViewModel: PayViewModel,  onShowTabBarChange: (Boolean) -> Unit) {

    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val showLoadingView by profileViewModel.showLoadingView.collectAsState()
    val resGetProfileInfoSuccessFlag by profileViewModel.resGetProfileInfoSuccessFlag.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()

    val resDisbursementSuccessFlag by payViewModel.resDisbursementSuccessFlag.collectAsState()
    val showDisbursementFailDialogFlag by payViewModel.showDisbursementFailDialogFlag.collectAsState()
    val showDisbursementFailMsg by payViewModel.showDisbursementFailMsg.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getProfileInfoAction()
    }

    walletContent(mainNavController = mainNavController,
        navController = navController,
        profileInfo = profileInfo,
        onConfirmHandled = { walletId, amount ->
            payViewModel.disbursementAction(aWalletId = walletId, aAmount = amount)
        },
        resDisbursementSuccessFlag = resDisbursementSuccessFlag,
        showDisbursementFailDialogFlag = showDisbursementFailDialogFlag,
        showDisbursementFailMsg = showDisbursementFailMsg,
        onResDisbursementSuccessDismissed = {
            payViewModel.resetResDisbursementSuccessFlag(false)
            profileViewModel.getProfileInfoAction()
        },
        onDisbursementFailDismissed = {
            payViewModel.resetShowDisbursementFailDialogFlag(false)
        })


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
    profileInfo: ProfileModel.ProfileData? = null,
    onConfirmHandled: (walletId: Int, amount: Int) -> Unit,
    resDisbursementSuccessFlag: Boolean = false,
    showDisbursementFailDialogFlag: Boolean = false,
    showDisbursementFailMsg: String? = null,
    onResDisbursementSuccessDismissed: () -> Unit = {},
    onDisbursementFailDismissed: () -> Unit = {},) {

    var showPayBottomSheet by remember { mutableStateOf(false) }

    var showDisbursementBottomSheet by remember { mutableStateOf(false) }
    var showDisbursementConfirmBottomSheet by remember { mutableStateOf(false) }

    var showDisbursementInputFieldFlag by remember { mutableStateOf(false) }
    var showDisbursementInputFieldMsg by remember { mutableStateOf("") }

    val paySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val disbursementSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val disbursementConfirmSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val selectedWallet = remember { mutableStateOf<ProfileModel.Wallets?>(null) }
    val inputAmountValue = remember { mutableStateOf("") }


    val otherWallets = (profileInfo?.wallets ?: emptyList()).filter { it.type != "personal" }

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
                        Spacer(modifier = Modifier.width(30.dp))
                        Text(stringResource(R.string.personal_wallet), color = Color(0xFF2D859D), style = MaterialTheme.typography.titleMedium)
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
//                    Row {
//                        Spacer(modifier = Modifier.width(20.dp))
//                        Surface (modifier = Modifier
//                            .weight(1f),
//                            color = Color.White,
//                            shape = RoundedCornerShape(0.dp)){
//
//                            Row (verticalAlignment = Alignment.CenterVertically){
//                                Spacer(modifier = Modifier.width(20.dp))
//
//                                Surface (
//                                    modifier = Modifier
//                                        .align(Alignment.CenterVertically)
//                                        .weight(0.9f),
//                                    color = Color.Unspecified
//                                ){
//                                    Column {
//                                        Spacer(modifier = Modifier.height(20.dp))
//                                        Text(stringResource(R.string.mobile_payment_top_up), color = Color.Black, style = MaterialTheme.typography.titleMedium)
//                                        Spacer(modifier = Modifier.height(20.dp))
//                                    }
//                                }
//                                Surface (
//                                    modifier = Modifier
//                                        .align(Alignment.CenterVertically)
//                                        .weight(0.1f),
//                                    color = Color.Unspecified
//                                ){
//                                    Icon(
//                                        painter = painterResource(id = R.drawable.caretright_b),
//                                        tint = Color.Unspecified,
//                                        contentDescription = "Localized description"
//                                    )
//                                }
//                                Spacer(modifier = Modifier.width(20.dp))
//                            }
//                        }
//                        Spacer(modifier = Modifier.width(20.dp))
//                    }
//                    Spacer(modifier = Modifier.height(2.dp))



                    // <-----申請退款----->
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate("Refund")
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
                    Spacer(modifier = Modifier.height(20.dp))

                    //共用錢包
                    Row {
                        Spacer(modifier = Modifier.width(30.dp))
                        Text(stringResource(R.string.shared_wallet), color = Color(0xFF2D859D), style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    //<-----撥款----->
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showPayBottomSheet = true
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
                                        Text(stringResource(R.string.disbursement), color = Color.Black, style = MaterialTheme.typography.titleMedium)
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
                                        tint = Color.Unspecified,
                                        contentDescription = "Localized description"
                                    )
                                }


                                Spacer(modifier = Modifier.width(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        if (showPayBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showPayBottomSheet = false
                },
                sheetState = paySheetState,
                containerColor = Color.White
            ) {
                payListView(
                    aWallets = otherWallets,
                    onItemClick = { value ->
                        showPayBottomSheet = false
                        showDisbursementBottomSheet = true
                        selectedWallet.value = value
                })
            }
        }

        //輸入撥款金額
        if (showDisbursementBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDisbursementBottomSheet = false
                },
                sheetState = disbursementSheetState,
                containerColor = Color.White
            ) {
                disbursementBottomSheetView(
                    selectPayType = selectedWallet.value?.name ?: "",
                    onInputText = { value ->
                        if (value.isEmpty()) {
                            showDisbursementInputFieldFlag = true
                            showDisbursementInputFieldMsg = "尚未輸入金額"
                        }else if (value == "0") {
                            showDisbursementInputFieldFlag = true
                            showDisbursementInputFieldMsg = "請勿輸入0元"
                        }else if ((value.toDoubleOrNull() ?: 0.0) > Math.round(profileInfo?.balance ?: 0.0)) {
                            //撥款金額不可超過個人錢包餘額，比的是畫面上四捨五入後的數字（同 toAmountString 的 HALF_UP 0 位小數）
                            showDisbursementInputFieldFlag = true
                            showDisbursementInputFieldMsg = "已超過個人錢包金額"
                        }else {
                            showDisbursementConfirmBottomSheet = true
                            inputAmountValue.value = value
                        }
                    },
                    onCancelHandled = {
                        showDisbursementBottomSheet = false
                    },
                    showInputFailDialogFlag = showDisbursementInputFieldFlag,
                    showInputFailMsg = showDisbursementInputFieldMsg,
                    onInputFailDismissed = {
                        showDisbursementInputFieldFlag = false
                    }
                )
            }
        }

        //確認撥款
        if (showDisbursementConfirmBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showDisbursementConfirmBottomSheet = false
                },
                sheetState = disbursementConfirmSheetState,
                containerColor = Color.White
            ) {
                disbursementConfirmBottomSheetView(
                    amount = inputAmountValue.value,
                    onDisbursementHandled = {
                        onConfirmHandled(selectedWallet.value?.walletId ?: -1, inputAmountValue.value.toInt())
                    },
                    onCancelHandled = {
                        showDisbursementConfirmBottomSheet = false
                    }
                )
            }
        }

        if (resDisbursementSuccessFlag) {
            textTNoButtonAlert(
                onDismissRequest = {},
                dialogTitle = "已完成撥款"
            )
            // 在 Dialog 顯示後啟動計時器
            LaunchedEffect(Unit) {
                delay(1500) // 延遲 1.5 秒
                onResDisbursementSuccessDismissed()
                showDisbursementConfirmBottomSheet = false
                showDisbursementBottomSheet = false
            }
        }

        if (showDisbursementFailDialogFlag) {
            textTNoButtonAlert(
                onDismissRequest = {},
                dialogTitle = parseDialogMsg(showDisbursementFailMsg ?: "")
            )
            // 在 Dialog 顯示後啟動計時器
            LaunchedEffect(Unit) {
                delay(1500) // 延遲 1.5 秒
                onDisbursementFailDismissed()
            }
        }
    }
}

@Composable
private fun infoViewByTeacher(profileInfo: ProfileModel.ProfileData?) {

    val otherWallets = (profileInfo?.wallets ?: emptyList()).filter { it.type != "personal" }

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
                            Text(stringResource(R.string.remaining_hours), color = Color.White,style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("${profileInfo?.hoursBalance?.calculateDuration()}", color = Color.White,style = MaterialTheme.typography.titleLarge,fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        val matched = getMatchingExpireDetail(
                            hoursExpiresAt = profileInfo?.hoursExpiresAt ?: "",
                            hoursExpireDetail = profileInfo?.hoursExpireDetail ?: emptyList()
                        )
                        if (matched != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(30.dp))
                                Text(
                                    "${(matched.hours ?: 0).calculateDuration()} 將於 ${formatExpiresAt(matched.expiresAt ?: "")} 到期",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
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

    val shape = when {
        otherWallets.isEmpty() -> RoundedCornerShape(9.dp)
        else -> RoundedCornerShape(
                topStart = 9.dp, topEnd = 9.dp,
        bottomStart = 0.dp, bottomEnd = 0.dp
            )
    }
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable {  },
            color = Color(0xFF2D859D),
            shape = shape
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
                                painter = painterResource(id = R.drawable.tipjar),
                                contentDescription = "",
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(stringResource(R.string.personal_wallet), color = Color.White,style = MaterialTheme.typography.bodyLarge)
                            Row (verticalAlignment = Alignment.Bottom){
                                Spacer(modifier = Modifier.width(20.dp))
                                Text("${profileInfo?.balance?.toAmountString()}", color = Color.White, style = MaterialTheme.typography.headlineLarge,fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(15.dp))
                                Text(stringResource(R.string.currency_unit), color = Color.White, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row (verticalAlignment = Alignment.CenterVertically){
                            Spacer(modifier = Modifier.width(20.dp))
                            Text("顯示金額皆取整數", color = Color.White,style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }

    otherWallets.forEachIndexed { index, item ->
        val shape = when {
            index == otherWallets.lastIndex -> RoundedCornerShape(
                topStart = 0.dp, topEnd = 0.dp,
                bottomStart = 9.dp, bottomEnd = 9.dp
            )
            else -> RoundedCornerShape(0.dp)
        }
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {  },
                color = Color.White,
                shape = shape
            ) {
                Row (verticalAlignment = Alignment.CenterVertically){
                    Surface (modifier = Modifier
                        .weight(1f)
                        , color = Color.White){
                        Column (){
                            Spacer(modifier = Modifier.height(20.dp))
                            Row (verticalAlignment = Alignment.CenterVertically){
                                Spacer(modifier = Modifier.width(15.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.house_pay),
                                    contentDescription = "",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("${item.name}", color = Color.Black,style = MaterialTheme.typography.bodyLarge)
                                Row (verticalAlignment = Alignment.Bottom){
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text("${item?.balance?.toAmountString()}", color = Color(0xFF2D859D), style = MaterialTheme.typography.headlineLarge,fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(15.dp))
                                    Text(stringResource(R.string.currency_unit), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Row (verticalAlignment = Alignment.CenterVertically){
                                Spacer(modifier = Modifier.width(20.dp))
                                Text("由個人錢包撥款，不可退款", color = Color.Black,style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Surface (
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.3f)
                            .height(80.dp),
                        color = Color.White
                    ){
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.weight(1f))
                            Text(stringResource(R.string.shared_wallet), color = Color(0xFF2D859D),style = MaterialTheme.typography.bodyMedium, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFF2D859D) , RoundedCornerShape(15.dp)).padding(5.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }


}

@Composable
private fun infoViewByStudent(profileInfo: ProfileModel.ProfileData?) {
    val otherWallets = (profileInfo?.wallets ?: emptyList()).filter { it.type != "personal" }

    val shape = when {
        otherWallets.isEmpty() -> RoundedCornerShape(9.dp)
        else -> RoundedCornerShape(
            topStart = 9.dp, topEnd = 9.dp,
            bottomStart = 0.dp, bottomEnd = 0.dp
        )
    }
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable {  },
            color = Color(0xFF2D859D),
            shape = shape
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
                        Spacer(modifier = Modifier.height(5.dp))
                        Row (verticalAlignment = Alignment.CenterVertically){
                            Spacer(modifier = Modifier.width(20.dp))
                            Text("顯示金額皆取整數", color = Color.White,style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
    otherWallets.forEachIndexed { index, item ->
        val shape = when {
            index == otherWallets.lastIndex -> RoundedCornerShape(
                topStart = 0.dp, topEnd = 0.dp,
                bottomStart = 9.dp, bottomEnd = 9.dp
            )
            else -> RoundedCornerShape(0.dp)
        }
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable {  },
                color = Color.White,
                shape = shape
            ) {
                Row (verticalAlignment = Alignment.CenterVertically){
                    Surface (modifier = Modifier
                        .weight(1f)
                        , color = Color.White){
                        Column (){
                            Spacer(modifier = Modifier.height(20.dp))
                            Row (verticalAlignment = Alignment.CenterVertically){
                                Spacer(modifier = Modifier.width(15.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.house_pay),
                                    contentDescription = "",
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("${item.name}", color = Color.Black,style = MaterialTheme.typography.bodyLarge)
                                Row (verticalAlignment = Alignment.Bottom){
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Text("${item?.balance?.toAmountString()}", color = Color(0xFF2D859D), style = MaterialTheme.typography.headlineLarge,fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(15.dp))
                                    Text(stringResource(R.string.currency_unit), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Row (verticalAlignment = Alignment.CenterVertically){
                                Spacer(modifier = Modifier.width(20.dp))
                                Text("由個人錢包撥款，不可退款", color = Color.Black,style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Surface (
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(0.3f)
                            .height(80.dp),
                        color = Color.White
                    ){
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Spacer(Modifier.weight(1f))
                            Text(stringResource(R.string.shared_wallet), color = Color(0xFF2D859D),style = MaterialTheme.typography.bodyMedium, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFF2D859D) , RoundedCornerShape(15.dp)).padding(5.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Composable
private fun payListView(aWallets: List<ProfileModel.Wallets>,onItemClick: (ProfileModel.Wallets) -> Unit = {}) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.please_select_disbursement_wallet), style = MaterialTheme.typography.titleMedium)
        }
        Spacer(modifier = Modifier.height(15.dp))
        LazyColumn() {
            items(aWallets) { item ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(top = 3.dp),
                    color = Color.White
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.name ?: "",
                                color = Color.Black,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Spacer(modifier = Modifier.width(30.dp))
                            Spacer(
                                modifier = Modifier
                                    .height(1.dp)
                                    .weight(1f)
                                    .background(color = Color(0xFF414141))
                            )
                            Spacer(modifier = Modifier.width(30.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun disbursementBottomSheetView(selectPayType: String, onInputText:(String) -> Unit, onCancelHandled: () -> Unit, showInputFailDialogFlag: Boolean, showInputFailMsg: String?, onInputFailDismissed: () -> Unit) {
    var inputPasswordText by remember { mutableStateOf("") }
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Text("${stringResource(R.string.personal_wallet)}", color = Color(0xFF2D859D), style = MaterialTheme.typography.titleLarge)
            Text("撥款至", color = Color.Black, style = MaterialTheme.typography.titleLarge)
            Text(selectPayType, color = Color(0xFF2D859D), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Text("*宿舍錢包不可退費，請謹慎撥款", color = Color(0xFFE54343), style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.height(20.dp))

        Row {
            BasicTextField(
                value = inputPasswordText,
                onValueChange = { inputPasswordText = it },
                textStyle = TextStyle( color = Color.Black),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier
                    .fillMaxWidth().padding(start = 20.dp, end = 20.dp) ,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 2.dp,
                                color = if (showInputFailDialogFlag) Color(0xFFE54343) else Color(0xFF999999),
                                shape = RoundedCornerShape(10.dp)
                            ).padding(15.dp)
                    ) {
                        if (inputPasswordText.isEmpty()) {
                            Text(
                                text = "請輸入撥款金額",
                                color = Color(0xFFAAAAAA)
                            )
                        }else{
                            onInputFailDismissed()
                        }
                        innerTextField()
                    }
                }
            )

        }

        //處理錯誤輸入顯示
        if (showInputFailDialogFlag) {
            Spacer(modifier = Modifier.height( 10.dp))
            Row {
                Spacer(modifier = Modifier.width(20.dp))
                Text(parseDialogMsg(showInputFailMsg ?: ""), color = Color(0xFFE54343))
            }
        }
        Spacer(modifier = Modifier.height( 35.dp))
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
//                        settingViewModel.passwordAuthenticationAction(inputPasswordText)
                        onInputText(inputPasswordText)
                        inputPasswordText = ""
                    },

                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.confirm) + stringResource(R.string.disbursement),
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
private fun disbursementConfirmBottomSheetView(amount: String, onDisbursementHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column (modifier = Modifier.background(Color.White)){
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.White){
                Column (horizontalAlignment = Alignment.CenterHorizontally, ){
                    Spacer(modifier = Modifier.height(25.dp))
                    Text("是否確定將 $${amount}撥款至 宿舍錢包？", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("撥款後將無法退回", color = Color(0xFFC82C2C), style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height( 20.dp))
        Row {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        onDisbursementHandled()
                    },
                color = Color.White
            ) {
                Text(
                    text = stringResource(R.string.confirm) + stringResource(R.string.disbursement),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color(0xFF2D859D)
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row {
            Spacer(modifier = Modifier.width(5.dp))
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1f)
                    .background(color = Color(0xFFF4F4F4))
            )
            Spacer(modifier = Modifier.width(5.dp))
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
                    text = stringResource(R.string.back),
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

private fun getMatchingExpireDetail(
    hoursExpiresAt: String,
    hoursExpireDetail: List<ProfileModel.HoursExpireDetail>
): ProfileModel.HoursExpireDetail? {
    if (hoursExpiresAt.isEmpty()) return null
    return hoursExpireDetail.firstOrNull { it.expiresAt == hoursExpiresAt }
}

private fun formatExpiresAt(expiresAt: String): String {
    if (expiresAt.isEmpty()) return ""
    return expiresAt.toLocalDateTimeText("yyyy-MM-dd").ifEmpty { expiresAt }
}

// 預覽UI

@Preview(showBackground = true)
@Composable
private fun WalletPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    walletContent (navController,navController,null,{_,_ ->})
}