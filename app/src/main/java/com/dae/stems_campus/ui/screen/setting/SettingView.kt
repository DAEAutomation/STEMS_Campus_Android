package com.dae.stems_campus.ui.screen.setting

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
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
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.BuildConfig
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.ui.components.BiometricHelper
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.viewmodel.LoginViewModel
import com.dae.stems_campus.viewmodel.ProfileViewModel
import com.dae.stems_campus.viewmodel.SettingViewModel
import kotlinx.coroutines.delay


@Composable
fun settingScreen(mainNavController: NavController, loginViewModel: LoginViewModel = hiltViewModel(), profileViewModel: ProfileViewModel = hiltViewModel(), settingViewModel: SettingViewModel = hiltViewModel(), onShowTabBarChange: (Boolean) -> Unit) {
    val settingNavController = rememberNavController()

    // 監聽內層 navController route 變化，子頁進入時隱藏 tab bar
    val backStackEntry by settingNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        onShowTabBarChange(currentRoute == null || currentRoute == "Setting")
    }

    NavHost(navController = settingNavController, startDestination = "Setting") {
        composable("Setting") {
            settingMainLoad(mainNavController = mainNavController,
                navController = settingNavController,
                loginViewModel = loginViewModel,
                profileViewModel = profileViewModel,
                settingViewModel = settingViewModel,
                onShowTabBarChange = {})
        }
        composable("changePassword") {
            changePasswordScreen(
                mainNavController = mainNavController,
                navController = settingNavController,
                settingViewModel = settingViewModel
            )
        }
        composable("UserCard") {
            userCardInfoViewScreen(
                mainNavController = mainNavController,
                navController = settingNavController,
                settingViewModel = settingViewModel,
                profileViewModel = profileViewModel
            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun settingMainLoad(mainNavController: NavController, navController: NavHostController, loginViewModel: LoginViewModel, profileViewModel: ProfileViewModel, settingViewModel: SettingViewModel, onShowTabBarChange: (Boolean) -> Unit) {

    val context = LocalContext.current



//    val accountName by settingViewModel.accountName.collectAsState()
//    val email by settingViewModel.email.collectAsState()
//    val rToken by settingViewModel.rToken.collectAsState()

    val uuid by settingViewModel.UUID.collectAsState()
    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val profileShowLoadingView by profileViewModel.showLoadingView.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()

    val resLogoutSuccessFlag by loginViewModel.resLogoutSuccessFlag.collectAsState()
    val showLogoutFailDialogFlag by loginViewModel.showLogoutFailDialogFlag.collectAsState()
    val showLogoutFailMsg by loginViewModel.showLogoutFailMsg.collectAsState()

    val resVerifyPasswordSuccessFlag by loginViewModel.resVerifyPasswordSuccessFlag.collectAsState()
    val showVerifyPasswordFailDialogFlag by loginViewModel.showVerifyPasswordFailDialogFlag.collectAsState()
    val showVerifyPasswordFailMsg by loginViewModel.showVerifyPasswordFailMsg.collectAsState()
//
//    val showLoadingView by settingViewModel.showLoadingView.collectAsState()

//
    val isBiometric by settingViewModel.isBiometricEnabled.collectAsState()


//
//    val resPwAuthenticationSuccessFlag by settingViewModel.resPwAuthenticationSuccessFlag.collectAsState()
//    val showPwAuthenticationFailMsgDialogFlag by settingViewModel.showPwAuthenticationFailMsgDialogFlag.collectAsState()
//    val showPwAuthenticationFailMsg by settingViewModel.showPwAuthenticationFailMsg.collectAsState()
//    val showPwAuthenticationInputFailFlag by settingViewModel.showPwAuthenticationInputFailFlag.collectAsState()
//    val showPwAuthenticationInputFailMsg by settingViewModel.showPwAuthenticationInputFailMsg.collectAsState()
//
//    val currentLanguageCode by settingViewModel.currentLanguageCode.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.getProfileInfoAction()
        settingViewModel.getBiometricValue()
    }

    settingContent(mainNavController = mainNavController,
        navController = navController,
        profileInfo = profileInfo,
        onShowTabBarChange = { value -> },
        onLogoutClick = { loginViewModel.logoutAction()},
        onVerifyPasswordHandled = { value ->  loginViewModel.verifyPasswordAction(value,"biometric",uuid)},
        resVerifyPasswordSuccessFlag = resVerifyPasswordSuccessFlag,
        showVerifyPasswordFailDialogFlag = showVerifyPasswordFailDialogFlag,
        showVerifyPasswordFailMsg = showVerifyPasswordFailMsg,
        onVerifyPasswordFailDismissed = { loginViewModel.resetShowVerifyPasswordFailDialogFlag(false)},
        isBiometric = isBiometric,
        updateBiometricValue = { value -> settingViewModel.updateBiometricValue(value)},
        onResVerifyPasswordSuccessFlagDismissed = { loginViewModel.resetResVerifyPasswordSuccessFlag(false) })


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


    if (resLogoutSuccessFlag) {
        mainNavController.navigate("signIn") {
            popUpTo(0) {
                inclusive = true // 包含起始頁一起清掉
            }
            launchSingleTop = true
        }
        loginViewModel.resetResLogoutSuccessFlag(false)
    }

    if (showLogoutFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showLogoutFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            loginViewModel.resetShowLogoutFailDialogFlag(false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun settingContent(
    mainNavController: NavController,
    navController: NavHostController,
    profileInfo: ProfileModel.ProfileData? = null,
    onShowTabBarChange: (Boolean) -> Unit,
    onLogoutClick: () -> Unit,
    onVerifyPasswordHandled:(String) -> Unit = {},
    resVerifyPasswordSuccessFlag: Boolean = false,
    showVerifyPasswordFailDialogFlag: Boolean = false,
    showVerifyPasswordFailMsg: String? = null,
    onVerifyPasswordFailDismissed: () -> Unit = {},
    isBiometric: Boolean = false,
    updateBiometricValue:(Boolean) -> Unit = {},
    onResVerifyPasswordSuccessFlagDismissed: () -> Unit = {}) {

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val LogoutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val InputPasswordOpenBiometricSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val InputPasswordCloseBiometricSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLogoutBottomSheet by remember { mutableStateOf(false) }

    var showInputPasswordOpenBiometricBottomSheet by remember { mutableStateOf(false) }
    var showInputPasswordCloseBiometricBottomSheet by remember { mutableStateOf(false) }
    var inputPasswordText by remember { mutableStateOf("") }

    var showBiometricFailDialogFlag by remember { mutableStateOf(false) }
    var showBiometricFailMsg by remember { mutableStateOf("") }

    val biometricHelper = remember(activity) {
        activity?.let { BiometricHelper(it) }
    }

    // 進入畫面時隱藏
    LaunchedEffect(Unit) {
        onShowTabBarChange(false)
//        settingViewModel.loadLoginPreferences()
//        settingViewModel.getBiometricValue()
//        settingViewModel.getCurrentLocale(context)
    }

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
                        Text("${stringResource(R.string.settings)}", color = Color.Black,
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
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(stringResource(R.string.user_or_card), color = Color(0xFF2D859D), style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    if (profileInfo?.role.equals("staff")) {
                        userInfoByTeacherView(profileInfo = profileInfo)
                    }else if (profileInfo?.role.equals("student")){
                        userInfoByStudentView(profileInfo = profileInfo, onClick = { navController.navigate("UserCard")})
                    }


                    Spacer(modifier = Modifier.height(40.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(stringResource(R.string.password), color = Color(0xFF2D859D), style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    //<-----變更密碼----->
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navController.navigate("changePassword")
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
                                        Text(stringResource(R.string.change_password), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Icon(
                                            painter = painterResource(id = R.drawable.vector),
                                            tint = Color.Black,
                                            contentDescription = "Localized description"
                                        )
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
                                        painter = painterResource(id = R.drawable.caretright),
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
                    //<-----Face ID----->
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f),
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
                                        Text(stringResource(R.string.biometric_login), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                                        Spacer(modifier = Modifier.height(15.dp))
                                        Text(stringResource(R.string.use_face_or_fingerprint_verification), color = Color.Black, style = MaterialTheme.typography.bodySmall)
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                                Surface (modifier = Modifier.padding(start = 10.dp),color = Color.Unspecified){
                                    Switch(
                                        checked = isBiometric,
                                        onCheckedChange = { checked ->
                                            if (checked) {
                                                if (biometricHelper != null) {
                                                    when (biometricHelper.canAuthenticate()) {
                                                        BiometricManager.BIOMETRIC_SUCCESS -> {
                                                            showInputPasswordOpenBiometricBottomSheet = true
                                                        }
                                                        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                                                            showBiometricFailDialogFlag = true
                                                            showBiometricFailMsg = "BiometricNoneEnrolled"
                                                        }
                                                        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                                                            showBiometricFailDialogFlag = true
                                                            showBiometricFailMsg = "BiometricUnavailable"
                                                        }
                                                        else -> {
                                                            showBiometricFailDialogFlag = true
                                                            showBiometricFailMsg = "BiometricNotSupportedOrDisabled"
                                                        }
                                                    }
                                                }else{
                                                    showBiometricFailDialogFlag = true
                                                    showBiometricFailMsg = "BiometricNotSupported"
                                                }
                                            } else {
                                                showInputPasswordCloseBiometricBottomSheet = true
                                            }
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    //<-----語言設定----->
//                        Row {
//                            Spacer(modifier = Modifier.width(20.dp))
//                            Surface (modifier = Modifier
//                                .weight(1f)
//                                .clickable {
//                                    navController.navigate("language")
//                                },
//
//                                color = Color(0xCC2C2C2C),
//                                shape = RoundedCornerShape(18.dp)){
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
//                                            Text(stringResource(R.string.language_settings), color = Color(0xFFC4C4C4), style = MaterialTheme.typography.bodyLarge)
//                                            Spacer(modifier = Modifier.height(10.dp))
//                                            Text("", color = Color.White, style = MaterialTheme.typography.titleLarge)
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
//                                            painter = painterResource(id = R.drawable.caretright),
//                                            tint = Color.Unspecified,
//                                            contentDescription = "Localized description"
//                                        )
//                                    }
//
//                                    Spacer(modifier = Modifier.width(20.dp))
//                                }
//                            }
//                            Spacer(modifier = Modifier.width(30.dp))
//                        }
//                        Spacer(modifier = Modifier.height(10.dp))
                    //<-----推播----->
//                Row {
//                    Spacer(modifier = Modifier.width(20.dp))
//                    Surface (modifier = Modifier
//                        .weight(1f),
//
//                        color = Color(0xCC2C2C2C),
//                        shape = RoundedCornerShape(18.dp)){
//
//                        Row (verticalAlignment = Alignment.CenterVertically){
//                            Spacer(modifier = Modifier.width(20.dp))
//
//                            Surface (
//                                modifier = Modifier
//                                    .align(Alignment.CenterVertically)
//                                    .weight(0.9f),
//                                color = Color.Unspecified
//                            ){
//                                Column {
//                                    Spacer(modifier = Modifier.height(20.dp))
//                                    Text(stringResource(R.string.push_notification_toggle), color = Color(0xFFC4C4C4), style = MaterialTheme.typography.bodyLarge)
//                                    Spacer(modifier = Modifier.height(10.dp))
//                                    Text("", color = Color.White, style = MaterialTheme.typography.titleLarge)
//                                    Spacer(modifier = Modifier.height(20.dp))
//                                }
//                            }
//                            Surface (
//                                modifier = Modifier
//                                    .align(Alignment.CenterVertically)
//                                    .weight(0.1f),
//                                color = Color.Unspecified
//                            ){
//                                Icon(
//                                    painter = painterResource(id = R.drawable.caretright),
//                                    tint = Color.Unspecified,
//                                    contentDescription = "Localized description"
//                                )
//                            }
//
//                            Spacer(modifier = Modifier.width(20.dp))
//                        }
//                    }
//                    Spacer(modifier = Modifier.width(30.dp))
//                }
//
//                Spacer(modifier = Modifier.height(40.dp))
//                Row {
//                    Spacer(modifier = Modifier.width(20.dp))
//                    Text(stringResource(R.string.system_settings), color = Color.White, style = MaterialTheme.typography.titleMedium)
//                }
//                Spacer(modifier = Modifier.height(20.dp))

                    Spacer(modifier = Modifier.height(40.dp))
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(stringResource(R.string.account_and_system_settings), color = Color(0xFF2D859D), style = MaterialTheme.typography.titleMedium)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    //<-----版本----->
                    Row {
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier
                            .weight(1f),

                            color = Color.White,
                            shape = RoundedCornerShape(9.dp)){

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
                                        Text(stringResource(R.string.version), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                                        Spacer(modifier = Modifier.height(10.dp))
                                            Text("DAE ${getVersionName()}", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    //<-----其它----->
//                        Row {
//                            Spacer(modifier = Modifier.width(20.dp))
//                            Surface (modifier = Modifier
//                                .weight(1f)
//                                .clickable {
//                                    navController.navigate("other")
//                                },
//                                color = Color(0xCC2C2C2C),
//                                shape = RoundedCornerShape(18.dp)){
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
//                                            Spacer(modifier = Modifier.height(30.dp))
//                                            Text(stringResource(R.string.other), color = Color.White, style = MaterialTheme.typography.bodyLarge)
//                                            Spacer(modifier = Modifier.height(30.dp))
//                                        }
//                                    }
//                                    Surface (
//                                        modifier = Modifier
//                                            .align(Alignment.CenterVertically)
//                                            .weight(0.1f),
//                                        color = Color.Unspecified
//                                    ){
//                                        Icon(
//                                            painter = painterResource(id = R.drawable.caretright),
//                                            tint = Color.Unspecified,
//                                            contentDescription = "Localized description"
//                                        )
//                                    }
//
//                                    Spacer(modifier = Modifier.width(20.dp))
//                                }
//                            }
//                            Spacer(modifier = Modifier.width(30.dp))
//                        }
                    Spacer(modifier = Modifier.height(10.dp))
                    //<-----登出----->
                    Row {
                        Surface (modifier = Modifier
                            .weight(1f)
                            .clickable {
                                showLogoutBottomSheet = true
                            },
                            color = Color(0xFFE54343),
                            shape = RoundedCornerShape(0.dp)){

                            Row (verticalAlignment = Alignment.CenterVertically){
                                Surface (
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .weight(0.9f),
                                    color = Color.Unspecified
                                ){
                                    Column (horizontalAlignment = Alignment.CenterHorizontally){
                                        Spacer(modifier = Modifier.height(20.dp))
                                        Text(stringResource(R.string.logout), color = Color.White, style = MaterialTheme.typography.bodyLarge)
                                        Spacer(modifier = Modifier.height(20.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
//                if (showLoadingView) {
//                    LoadingView() {
//
//                    }
//                }

            if (showLogoutBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showLogoutBottomSheet = false
                    },
                    sheetState = LogoutSheetState,
                    containerColor = Color.White
                ) {
                    logoutBottomSheetView(onLogoutHandled = {
                        onLogoutClick()
                        showLogoutBottomSheet = false
                    }, onCancelHandled = {
                        showLogoutBottomSheet = false
                    })
                }
            }

            if (resVerifyPasswordSuccessFlag) {
                onResVerifyPasswordSuccessFlagDismissed()
                if (showInputPasswordOpenBiometricBottomSheet) {
                    showInputPasswordOpenBiometricBottomSheet = false
                    updateBiometricValue(true)
                }else if (showInputPasswordCloseBiometricBottomSheet) {
                    showInputPasswordCloseBiometricBottomSheet = false
                    updateBiometricValue(false)
                }
            }

            // 開啟生物辨識
            if (showInputPasswordOpenBiometricBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showInputPasswordOpenBiometricBottomSheet = false
                    },
                    sheetState = InputPasswordOpenBiometricSheetState,
                    containerColor = Color.White
                ) {

                    inputPasswordBottomSheetView(stringResource(R.string.enable_touch_face_id_login),onInputText = { value ->
                        onVerifyPasswordHandled(value)
                    }, onCancelHandled = {
                        showInputPasswordOpenBiometricBottomSheet = false
                    }, showVerifyPasswordFailDialogFlag = showVerifyPasswordFailDialogFlag,
                        showVerifyPasswordFailMsg = showVerifyPasswordFailMsg,
                        onVerifyPasswordFailDismissed = {
                            onVerifyPasswordFailDismissed()
                        })
                }
            }

            // 關閉生物辨識
            if (showInputPasswordCloseBiometricBottomSheet) {

                ModalBottomSheet(
                    onDismissRequest = {
                        showInputPasswordCloseBiometricBottomSheet = false
                    },
                    sheetState = InputPasswordCloseBiometricSheetState,
                    containerColor = Color.White
                ) {

                    inputPasswordBottomSheetView(stringResource(R.string.disable_touch_face_id_login),onInputText = { value ->
                        onVerifyPasswordHandled(value)
                    }, onCancelHandled = {
                        showInputPasswordCloseBiometricBottomSheet = false
                    }, showVerifyPasswordFailDialogFlag = showVerifyPasswordFailDialogFlag,
                        showVerifyPasswordFailMsg = showVerifyPasswordFailMsg,
                        onVerifyPasswordFailDismissed = {
                            onVerifyPasswordFailDismissed()
                        })
                }
            }
            if (showBiometricFailDialogFlag) {
                textTNoButtonAlert(
                    onDismissRequest = {},
                    dialogTitle = parseDialogMsg(showBiometricFailMsg)
                )
                // 在 Dialog 顯示後啟動計時器
                LaunchedEffect(Unit) {
                    delay(1500) // 延遲 1.5 秒
                    showBiometricFailDialogFlag = false // 自動關閉 Dialog
                }
            }



//            if (showingInUseBottomSheet) {
//                ModalBottomSheet(
//                    onDismissRequest = {
//                        showingInUseBottomSheet = false
//                    },
//                    sheetState = inUseStudentSheetState,
//                    containerColor = Color.White
//                ) {
//                    inUseBottomSheetView(scanInfo, onCancelHandled = {
//                        showingInUseBottomSheet = false
//                    })
//                }
//            }





            //輸入密碼
//            if (showingInputPasswordBottomSheet) {
//                ModalBottomSheet(
//                    onDismissRequest = {
//                        showingInputPasswordBottomSheet = false
//                    },
//                    sheetState = inputPasswordSheetState,
//                    containerColor = Color.White
//                ) {
//                    inputPasswordAndLinkBottomSheetView(onInputText = { value ->
//                        onVerifyPasswordHandled(value)
//                    }, onCancelHandled = {
//                        showingInputPasswordBottomSheet = false
//                    }, showVerifyPasswordFailDialogFlag = showVerifyPasswordFailDialogFlag,
//                        showVerifyPasswordFailMsg = showVerifyPasswordFailMsg,
//                        onVerifyPasswordFailDismissed = {
//                            onVerifyPasswordFailDismissed()
//                        })
//                }
//            }



        }
    }

}

@Composable
private fun userInfoByTeacherView(profileInfo: ProfileModel.ProfileData? = null) {
    Column{
        // <-----姓名----->
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface (modifier = Modifier
                .weight(1f)
                .clickable {
//                                    navController.navigate("changeName/${accountName}")
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
                            Text(stringResource(R.string.name), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(profileInfo?.name ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }


                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        //<-----單位----->
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
                            Text(stringResource(R.string.organization), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(profileInfo?.department ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        //<-----職稱----->
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
                            Text(stringResource(R.string.job_title), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(profileInfo?.jobTitle ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        // <-----實體卡ID----->
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface (modifier = Modifier
                .weight(1f)
                .clickable {
//                                    navController.navigate("changeName/${accountName}")
                },

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
                            Text(stringResource(R.string.physical_card_id), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            if (profileInfo?.uid.equals("")){
                                Text("尚未綁定", color = Color(0xFFE54343), style = MaterialTheme.typography.titleLarge)
                            }else{
                                Text(profileInfo?.uid ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
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
//                        Icon(
//                            painter = painterResource(id = R.drawable.caretright),
//                            tint = Color.Unspecified,
//                            contentDescription = "Localized description"
//                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        // <-----宿舍綁定----->
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface (modifier = Modifier
                .weight(1f)
                .clickable {
//                                    navController.navigate("changeName/${accountName}")
                },

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
                            Text(stringResource(R.string.dormitory_binding), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            if (profileInfo?.uid.equals("")){
                                Text("尚未綁定", color = Color(0xFFE54343), style = MaterialTheme.typography.titleLarge)
                            }else{
                                Text(profileInfo?.uid ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
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
                            painter = painterResource(id = R.drawable.caretright),
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
        // <-----信箱----->
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
                            Text(stringResource(R.string.mail), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(profileInfo?.email ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun userInfoByStudentView(profileInfo: ProfileModel.ProfileData? = null, onClick:() -> Unit ) {
    Column{
        // <-----姓名----->
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface (modifier = Modifier
                .weight(1f)
                .clickable {
//                                    navController.navigate("changeName/${accountName}")
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
                            Text(stringResource(R.string.name), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(profileInfo?.name ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        //<-----系所----->
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
                            Text("系所", color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(profileInfo?.department ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        //<-----身份----->
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
                            Text(stringResource(R.string.identity), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(profileInfo?.studentType ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(2.dp))
        // <-----實體卡ID----->
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface (modifier = Modifier
                .weight(1f)
                .clickable {
                    onClick()
                },

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
                            Text(stringResource(R.string.physical_card_id), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            if (profileInfo?.uid.equals("")){
                                Text("尚未綁定", color = Color(0xFFE54343), style = MaterialTheme.typography.titleLarge)
                            }else{
                                Text(profileInfo?.uid ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
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
                            painter = painterResource(id = R.drawable.caretright),
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
        // <-----宿舍綁定----->
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Surface (modifier = Modifier
                .weight(1f)
                .clickable {
//                                    navController.navigate("changeName/${accountName}")
                },

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
                            Text(stringResource(R.string.dormitory_binding), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            if (profileInfo?.uid.equals("")){
                                Text("尚未綁定", color = Color(0xFFE54343), style = MaterialTheme.typography.titleLarge)
                            }else{
                                Text(profileInfo?.uid ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
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
                            painter = painterResource(id = R.drawable.caretright),
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
        // <-----信箱----->
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
                            Text(stringResource(R.string.mail), color = Color(0xFF303236), style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(profileInfo?.email ?: "", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun logoutBottomSheetView(onLogoutHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column (modifier = Modifier.background(Color.White)){
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.White){
                Column (horizontalAlignment = Alignment.CenterHorizontally, ){
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(stringResource(R.string.confirm_logout), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                        onLogoutHandled()
                    },
                color = Color.White
            ) {
                Text(
                    text = stringResource(R.string.logout),
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
private fun inputPasswordBottomSheetView(title: String, onInputText:(String) -> Unit, onCancelHandled: () -> Unit, showVerifyPasswordFailDialogFlag: Boolean, showVerifyPasswordFailMsg: String?, onVerifyPasswordFailDismissed: () -> Unit) {
    var inputPasswordText by remember { mutableStateOf("") }
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
                value = inputPasswordText,
                onValueChange = { inputPasswordText = it },
                textStyle = TextStyle( color = Color.Black),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
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
                                color = if (showVerifyPasswordFailDialogFlag) Color(0xFFE54343) else Color(0xFF999999),
                                shape = RoundedCornerShape(10.dp)
                            ).padding(15.dp)
                    ) {
                        if (inputPasswordText.isEmpty()) {
                            Text(
                                text = stringResource(R.string.enter_password),
                                color = Color(0xFFAAAAAA)
                            )
                        }else{
                            onVerifyPasswordFailDismissed()
                        }
                        innerTextField()
                    }
                },
                visualTransformation = PasswordVisualTransformation()
            )

        }

        //處理錯誤輸入顯示
        if (showVerifyPasswordFailDialogFlag) {
            Spacer(modifier = Modifier.height( 10.dp))
            Row {
                Spacer(modifier = Modifier.width(20.dp))
                Text(parseDialogMsg(showVerifyPasswordFailMsg ?: ""), color = Color(0xFFE54343))
                Spacer(modifier = Modifier.width(20.dp))
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
                    text = stringResource(R.string.confirm),
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


@Composable
private fun parseDialogMsg(aMsg: String):(String){
    var msg: String = ""
    if (aMsg == "PleaseReLogin") {
        msg = stringResource(id = R.string.please_re_login)
    }else if (aMsg == "BiometricLoginNotEnabled") {
        msg = stringResource(R.string.biometric_login_not_enabled)
    }else if (aMsg == "BiometricNotSupportedOrDisabled") {
        msg = stringResource(id = R.string.biometric_not_supported_or_disabled)
    }else if (aMsg == "BiometricNotSupported") {
        msg = stringResource(id = R.string.biometric_not_supported)
    }else if (aMsg == "BiometricNoneEnrolled") {
        msg = stringResource(id = R.string.biometric_none_enrolled)
    }else if (aMsg == "BiometricUnavailable") {
        msg = stringResource(id = R.string.biometric_unavailable)
    }else if(aMsg == "PasswordNotEntered") {
        msg = stringResource(R.string.password_not_entered)
    }else {
        msg = aMsg
    }
    return msg
}

private fun getVersionName(): String {
    return try {
        val currentVersion = BuildConfig.VERSION_NAME
        currentVersion ?: "未知版本"
    } catch (e: PackageManager.NameNotFoundException) {
        "未知版本"
    }
}

private fun getVersionCode(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.longVersionCode.toInt().toString()
    } catch (e: PackageManager.NameNotFoundException) {
        "?"
    }
}

// 預覽UI

@Preview(showBackground = true)
@Composable
private fun SettingPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    settingContent (navController,navController,null,{},{})
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetViewPreview() {
    STEMS_CampusTheme {
//        logoutBottomSheetView({},{})
        inputPasswordBottomSheetView("XXXXX",{},{},false,"",{})
    }

}
