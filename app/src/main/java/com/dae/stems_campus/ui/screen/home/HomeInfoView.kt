package com.dae.stems_campus.ui.screen.home

import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.ui.components.BiometricHelper
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.screen.notifications.notificationDetailScreen
import com.dae.stems_campus.ui.screen.notifications.notificationsScreen
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.utils.calculateDuration
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.utils.toTwoDecimalString
import com.dae.stems_campus.viewmodel.HomeInfoViewModel
import com.dae.stems_campus.viewmodel.LoginViewModel
import com.dae.stems_campus.viewmodel.NotificationViewModel
import com.dae.stems_campus.viewmodel.ProfileViewModel
import com.dae.stems_campus.viewmodel.SettingViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(mainNavController: NavController, onNavigateToSetting: () -> Unit = {}, onNavigateToWallet: () -> Unit = {}, onShowTabBarChange: (Boolean) -> Unit = {}, profileViewModel: ProfileViewModel = hiltViewModel(), homeInfoViewModel: HomeInfoViewModel = hiltViewModel(), loginViewModel: LoginViewModel = hiltViewModel(), settingViewModel: SettingViewModel = hiltViewModel(), notificationViewModel: NotificationViewModel = hiltViewModel()) {
    val homeNavController = rememberNavController()

    // 監聽內層 navController route 變化，子頁進入時隱藏 tab bar
    val backStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    LaunchedEffect(currentRoute) {
        onShowTabBarChange(currentRoute == null || currentRoute == "Home")
    }

    NavHost(navController = homeNavController, startDestination = "Home") {
        composable("Home") {
            homeMainLoad(mainNavController = mainNavController,homeNavController, profileViewModel, homeInfoViewModel, loginViewModel, settingViewModel, onShowTabBarChange = {}, onNavigateToSetting = { onNavigateToSetting() }, onNavigateToWallet = { onNavigateToWallet() })
        }
        composable("ClassroomDetail/{deviceCode}") { backStackEntry ->
            val deviceCode = backStackEntry.arguments?.getString("deviceCode")
            classroomDetailScreen(navController = homeNavController, selectDeviceCode = deviceCode ?: "", onShowTabBarChange = {})
        }
        composable("DormitoryDetail/{deviceCode}") { backStackEntry ->
            val deviceCode = backStackEntry.arguments?.getString("deviceCode")
            dormitoryDetailScreen(navController = homeNavController, selectDeviceCode = deviceCode ?: "", onShowTabBarChange = {})
        }
        composable("Notifications") {
            notificationsScreen(navController = homeNavController, notificationViewModel = notificationViewModel, onShowTabBarChange = {})
        }
        composable("NotificationDetail") {
            val notificationDetail = notificationViewModel.notificationDetail
            notificationDetailScreen (navController = homeNavController,  notificationDetail, onShowTabBarChange = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun homeMainLoad(
    mainNavController: NavController,
    navHostController: NavHostController,
    profileViewModel: ProfileViewModel,
    homeInfoViewModel: HomeInfoViewModel,
    loginViewModel: LoginViewModel,
    settingViewModel: SettingViewModel,
    onShowTabBarChange: (Boolean) -> Unit,
    onNavigateToSetting: () -> Unit,
    onNavigateToWallet: () -> Unit) {

    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val showLoadingView by profileViewModel.showLoadingView.collectAsState()
    val resGetProfileInfoSuccessFlag by profileViewModel.resGetProfileInfoSuccessFlag.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()

    val uuid by homeInfoViewModel.UUID.collectAsState()
    val scanInfo by homeInfoViewModel.scanInfo.collectAsState()
    val resScanInfoSuccessFlag by homeInfoViewModel.resScanInfoSuccessFlag.collectAsState()
    val showScanInfoFailDialogFlag by homeInfoViewModel.showScanInfoFailDialogFlag.collectAsState()
    val showScanInfoFailMsg by homeInfoViewModel.showScanInfoFailMsg.collectAsState()

    val resVerifyPasswordSuccessFlag by loginViewModel.resVerifyPasswordSuccessFlag.collectAsState()
    val showVerifyPasswordFailDialogFlag by loginViewModel.showVerifyPasswordFailDialogFlag.collectAsState()
    val showVerifyPasswordFailMsg by loginViewModel.showVerifyPasswordFailMsg.collectAsState()

    val resStartPowerSuccessFlag by homeInfoViewModel.resStartPowerSuccessFlag.collectAsState()
    val showStartPowerFailDialogFlag by homeInfoViewModel.showStartPowerFailDialogFlag.collectAsState()
    val showStartPowerFailMsg by homeInfoViewModel.showStartPowerFailMsg.collectAsState()
    val showLoadingViewByStartPowerStudent by homeInfoViewModel.showLoadingViewByStartPowerStudent.collectAsState()

    var isAcControl by remember { mutableStateOf(false) }

    val isBiometricFlag by settingViewModel.isBiometricEnabled.collectAsState()
    val biometricHelper = remember(activity) {
        activity?.let { BiometricHelper(it) }
    }
    val passwordText by loginViewModel.passwordText.collectAsState()

    // Toast 在非 composable 的 lambda 裡顯示，文字先在這裡取好
    val msgLoginNotEnabled = stringResource(R.string.biometric_login_not_enabled)
    val msgNotSupported = stringResource(R.string.biometric_not_supported)
    val msgNoneEnrolled = stringResource(R.string.biometric_none_enrolled)
    val msgUnavailable = stringResource(R.string.biometric_unavailable)
    val msgNotSupportedOrDisabled = stringResource(R.string.biometric_not_supported_or_disabled)

    //生物辨識判斷（含裝置密碼，密碼入口由系統辨識畫面自帶）
    val handleBiometricStartPower = {
        if (!isBiometricFlag) {
            Toast.makeText(context, msgLoginNotEnabled, Toast.LENGTH_SHORT).show()
        } else if (biometricHelper == null) {
            Toast.makeText(context, msgNotSupported, Toast.LENGTH_SHORT).show()
        } else {
            when (biometricHelper.canAuthenticate()) {
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    biometricHelper.authenticate(
                        onSuccess = {
                            if (profileInfo?.role.equals("staff")) {
                                homeInfoViewModel.startPowerAction(scanInfo?.deviceCode ?: "",uuid,scanInfo?.sessionToken ?: "")
                            }else if (profileInfo?.role.equals("student")){
                                homeInfoViewModel.startPowerByStudentAction(scanInfo?.deviceCode ?: "",uuid,scanInfo?.sessionToken ?: "", isAcControl)
                            }
                        },
                        onError = {
                            Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    Toast.makeText(context, msgNoneEnrolled, Toast.LENGTH_SHORT).show()
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    Toast.makeText(context, msgUnavailable, Toast.LENGTH_SHORT).show()
                else ->
                    Toast.makeText(context, msgNotSupportedOrDisabled, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        settingViewModel.getBiometricValue()
    }

    homeContent(mainNavController = mainNavController,
        navHostController = navHostController,
        profileInfo = profileInfo,
        onRefreshProfile = { profileViewModel.getProfileInfoAction() },
        showLoadingView = showLoadingView,
        resGetProfileInfoSuccessFlag = resGetProfileInfoSuccessFlag,
        showGetProfileInfoFailDialogFlag = showGetProfileInfoFailDialogFlag,
        showGetProfileInfoFailMsg = showGetProfileInfoFailMsg,
        onGetProfileInfoFailDismissed = { profileViewModel.resetShowGetProfileInfoFailDialogFlag(false)},
        onScanInfoFinishHandled = { code -> homeInfoViewModel.getScanInfoAction(aQrCode = code, aDeviceID = uuid)},
        scanInfo = scanInfo,
        resScanInfoSuccessFlag = resScanInfoSuccessFlag,
        showScanInfoFailDialogFlag = showScanInfoFailDialogFlag,
        showScanInfoFailMsg = showScanInfoFailMsg,
        onScanInfoFailDismissed = { homeInfoViewModel.resetScanInfoFailDialogFlag(false)},
        onScanInfoSuccessFlagReset = { homeInfoViewModel.resetScanInfoSuccessFlag(false)},
        onVerifyPasswordHandled = { value ->  loginViewModel.verifyPasswordAction(value,"bind_device",uuid)},
        resVerifyPasswordSuccessFlag = resVerifyPasswordSuccessFlag,
        showVerifyPasswordFailDialogFlag = showVerifyPasswordFailDialogFlag,
        showVerifyPasswordFailMsg = showVerifyPasswordFailMsg,
        onVerifyPasswordFailDismissed = { loginViewModel.resetShowVerifyPasswordFailDialogFlag(false)},
        resStartPowerSuccessFlag = resStartPowerSuccessFlag,
        acControlHandled = { value -> isAcControl = value },
        isBiometricFlag = isBiometricFlag,
        biometricHelper = biometricHelper,
        onBiometricsStartPowerSupplyHandled = { handleBiometricStartPower() },
        onNavigateToSetting = { onNavigateToSetting() },
        onNavigateToWallet = { onNavigateToWallet() })

    if (showLoadingViewByStartPowerStudent) {
        LoadingView() {}
    }

    //密碼驗證成功後
    if (resVerifyPasswordSuccessFlag) {

        if (scanInfo?.sessionToken ?: "" == "") {
            textTNoButtonAlert(
                onDismissRequest = {},
                dialogTitle = parseDialogMsg(scanInfo?.canStartReason ?: "")
            )
            // 在 Dialog 顯示後啟動計時器
            LaunchedEffect(Unit) {
                delay(1500) // 延遲 1.5 秒
                loginViewModel.resetResVerifyPasswordSuccessFlag(false)
            }
        }else{
            loginViewModel.resetResVerifyPasswordSuccessFlag(false)
            if (profileInfo?.role.equals("staff")) {
                homeInfoViewModel.startPowerAction(scanInfo?.deviceCode ?: "",uuid,scanInfo?.sessionToken ?: "")
            }else if (profileInfo?.role.equals("student")){
                homeInfoViewModel.startPowerByStudentAction(scanInfo?.deviceCode ?: "",uuid,scanInfo?.sessionToken ?: "", isAcControl)
            }
        }

    }

    if (resStartPowerSuccessFlag) {
        homeInfoViewModel.resetStartPowerSuccessFlag(false)
        profileViewModel.getProfileInfoAction()
    }
    if (showStartPowerFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showStartPowerFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            homeInfoViewModel.resetStartPowerFailDialogFlag(false)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun homeContent(mainNavController: NavController,
                        navHostController: NavHostController,
                        profileInfo: ProfileModel.ProfileData? = null,
                        onRefreshProfile: () -> Unit = {},
                        showLoadingView: Boolean = false,
                        resGetProfileInfoSuccessFlag: Boolean = false,
                        showGetProfileInfoFailDialogFlag: Boolean = false,
                        showGetProfileInfoFailMsg: String? = null,
                        onGetProfileInfoFailDismissed: () -> Unit = {},
                        onScanInfoFinishHandled:(String) -> Unit = {},
                        scanInfo: ScanModel.ScanData? = null,
                        resScanInfoSuccessFlag: Boolean = false,
                        showScanInfoFailDialogFlag: Boolean = false,
                        showScanInfoFailMsg: String? = null,
                        onScanInfoSuccessFlagReset: () -> Unit = {},
                        onScanInfoFailDismissed: () -> Unit = {},
                        onVerifyPasswordHandled:(String) -> Unit = {},
                        resVerifyPasswordSuccessFlag: Boolean = false,
                        showVerifyPasswordFailDialogFlag: Boolean = false,
                        showVerifyPasswordFailMsg: String? = null,
                        onVerifyPasswordFailDismissed: () -> Unit = {},
                        resStartPowerSuccessFlag: Boolean = false,
                        acControlHandled:(Boolean) -> Unit = {},
                        isBiometricFlag: Boolean = false,
                        biometricHelper: BiometricHelper? = null,
                        onBiometricsStartPowerSupplyHandled: () -> Unit,
                        onNavigateToSetting: () -> Unit = {},
                        onNavigateToWallet: () -> Unit = {}) {

    var isRefreshing by remember { mutableStateOf(false) }
    // VM 的 showLoadingView 在 API call 結束時會切回 false → 同步把下拉刷新的 indicator 收掉
    LaunchedEffect(showLoadingView) {
        if (!showLoadingView && isRefreshing) {
            isRefreshing = false
        }
    }

    val classroomTeacherSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dormitoryTeacherSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val classroomStudentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dormitoryStudentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val inUseStudentSheetState = rememberModalBottomSheetState()
    val powerEnableStudentSheetState = rememberModalBottomSheetState()
    val inputPasswordSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showClassroomPowerSupplyByTeacherBottomSheet by remember { mutableStateOf(false) }
    var showDormitoryPowerSupplyByTeacherBottomSheet by remember { mutableStateOf(false) }
    var showClassroomPowerSupplyByStudentBottomSheet by remember { mutableStateOf(false) }
    var showDormitoryPowerSupplyByStudentBottomSheet by remember { mutableStateOf(false) }
    var showingInUseBottomSheet by remember { mutableStateOf(false) }
    var showingPowerEnableBottomSheet by remember { mutableStateOf(false) }
    var showingInputPasswordBottomSheet by remember { mutableStateOf(false) }
    var showScanBottomSheet by remember { mutableStateOf(false) }
    val scanSheetState = rememberModalBottomSheetState()
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showScanBottomSheet = true
        }
    }

    // 判斷是否有任何 BottomSheet 正在顯示
    val isAnyBottomSheetShowing = showClassroomPowerSupplyByTeacherBottomSheet ||
        showDormitoryPowerSupplyByTeacherBottomSheet ||
        showClassroomPowerSupplyByStudentBottomSheet ||
        showDormitoryPowerSupplyByStudentBottomSheet ||
        showingInUseBottomSheet ||
        showingPowerEnableBottomSheet ||
        showingInputPasswordBottomSheet ||
        showScanBottomSheet

    // BottomSheet 沒開的時候才每 30 秒刷新
    LaunchedEffect(isAnyBottomSheetShowing) {
        if (!isAnyBottomSheetShowing) {
            while (true) {
                onRefreshProfile()
                delay(30_000L)
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF4F4F4))) {

        Column {
            Surface (modifier = Modifier
                .weight(0.2f)
                .fillMaxWidth(),  color = Color.Unspecified){
                Column {
                    Spacer(modifier = Modifier.height(50.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(25.dp))
                        Text("${stringResource(R.string.home)}", color = Color.Black,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)

                        Surface (modifier = Modifier.weight(1f)){  }
                        Surface (modifier = Modifier, color = Color.Unspecified){
                            Surface (modifier = Modifier
                                .clickable {
                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                }
                                , color = Color.Unspecified)
                            {
                                Image(painter = painterResource(id = R.drawable.qrcode), contentDescription = "")
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier, color = Color.Unspecified){
                            Surface (modifier = Modifier
                                .clickable {
                                    navHostController.navigate("Notifications")
                                }
                                , color = Color.Unspecified)
                            {
                                Image(painter = painterResource(id = R.drawable.bell), contentDescription = "")
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                    }
                }
            }
            Row {
                Spacer(modifier = Modifier.width(25.dp))
                if (profileInfo?.role.equals("staff")) {
                    Text(text = "Hi,${profileInfo?.name} ${profileInfo?.jobTitle}", color = Color(0xFF656565), style = MaterialTheme.typography.titleMedium)
                }else if (profileInfo?.role.equals("student")){
                    Text(text = "Hi,${profileInfo?.name} 同學", color = Color(0xFF656565), style = MaterialTheme.typography.titleMedium)
                }

            }
            Spacer(modifier = Modifier.height(30.dp))

            // 錢包 / 時數 / 餘額警告 - 固定區，不參與下拉位移
            if (profileInfo?.role.equals("staff")) {
                walletHoursHeaderByTeacher(aData = profileInfo, onNavigateToWalletClick = { onNavigateToWallet() })
            } else if (profileInfo?.role.equals("student")) {
                walletHoursHeaderByStudent(aData = profileInfo, onNavigateToWalletClick = { onNavigateToWallet() })
            }

            val pullState = rememberPullToRefreshState()
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .weight(0.8f)
                    .fillMaxWidth()
                    .pullToRefresh(
                        isRefreshing = isRefreshing,
                        state = pullState,
                        enabled = profileInfo?.activeSession?.hasActive ?: false, // 未有裝置時不下拉
                        onRefresh = {
                            isRefreshing = true
                            onRefreshProfile()
                        }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .graphicsLayer {
                            // 下拉時跟著手指下移；下拉到 threshold 時整塊位移 80dp
                            translationY = pullState.distanceFraction * with(density) { 80.dp.toPx() }
                        }
                ) {
                    if (profileInfo?.role.equals("staff")) {
                        infoViewByTeacher(profileInfo?.activeSession?.hasActive ?: false, profileInfo, onScanClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }, onGetUsingDeviceDetailByClassroomHandled = { value -> navHostController.navigate("ClassroomDetail/${value}")}, onGetUsingDeviceDetailByDormitoryHandled = { value -> navHostController.navigate("DormitoryDetail/${value}")}, onNavigateToWalletClick = { onNavigateToWallet()})
                    }else if (profileInfo?.role.equals("student")){
                        infoViewByStudent(profileInfo?.activeSession?.hasActive ?: false, profileInfo, onScanClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }, onGetUsingDeviceDetailByClassroomHandled = { value -> navHostController.navigate("ClassroomDetail/${value}")}, onGetUsingDeviceDetailByDormitoryHandled = { value -> navHostController.navigate("DormitoryDetail/${value}")}, onNavigateToWalletClick = { onNavigateToWallet()})
                    }
                }
                PullToRefreshDefaults.Indicator(
                    state = pullState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
            // 下拉刷新時不再額外蓋全螢幕 LoadingView，indicator 自己會顯示
            if (showLoadingView && !isRefreshing) {
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
                    onGetProfileInfoFailDismissed()
                }
            }

            if (resScanInfoSuccessFlag) {
                when (scanInfo?.usageStatus) {
                    0 -> {
                        if (profileInfo?.role.equals("staff")) {
                            if (scanInfo?.spaceType.equals("classroom")) {
                                showClassroomPowerSupplyByTeacherBottomSheet = true
                            }else if (scanInfo?.spaceType.equals("dormitory")){
                                showDormitoryPowerSupplyByTeacherBottomSheet = true
                            }
                        }else if (profileInfo?.role.equals("student")){
                            if (scanInfo?.spaceType.equals("classroom")) {
                                showClassroomPowerSupplyByStudentBottomSheet = true
                            }else if (scanInfo?.spaceType.equals("dormitory")){
                                showDormitoryPowerSupplyByStudentBottomSheet = true
                            }
                        }
                        onScanInfoSuccessFlagReset()
                    }
                    1 -> {
                        showingInUseBottomSheet = true
                        onScanInfoSuccessFlagReset()
                    }
                    2 -> {
                        showingPowerEnableBottomSheet = true
                        onScanInfoSuccessFlagReset()
                    }
                    3 -> {
                        textTNoButtonAlert(
                            onDismissRequest = {},
                            dialogTitle = scanInfo.canStartReason ?: ""
                        )
                        // 在 Dialog 顯示後啟動計時器
                        LaunchedEffect(Unit) {
                            delay(1500) // 延遲 1.5 秒
                            onScanInfoSuccessFlagReset()
                        }
                    }
                }

            }

            if (showScanInfoFailDialogFlag) {
                textTNoButtonAlert(
                    onDismissRequest = {},
                    dialogTitle = parseDialogMsg(showScanInfoFailMsg ?: "")
                )
                // 在 Dialog 顯示後啟動計時器
                LaunchedEffect(Unit) {
                    delay(1500) // 延遲 1.5 秒
                    onScanInfoFailDismissed()
                }
            }

            //老師開教室
            if (showClassroomPowerSupplyByTeacherBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showClassroomPowerSupplyByTeacherBottomSheet = false
                    },
                    sheetState = classroomTeacherSheetState,
                    containerColor = Color.White
                ) {
                    classroomPowerSupplyByTeacherBottomSheetView(scanInfo,
                        onPowerSupplyHandled = {
                            if (isBiometricFlag) {
                                onBiometricsStartPowerSupplyHandled()
                            }else{
                                showingInputPasswordBottomSheet = true
                            }

                    }, onCancelHandled = {
                        showClassroomPowerSupplyByTeacherBottomSheet = false
                    })
                }
            }

            //老師開宿舍
            if (showDormitoryPowerSupplyByTeacherBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showDormitoryPowerSupplyByTeacherBottomSheet = false
                    },
                    sheetState = dormitoryTeacherSheetState,
                    containerColor = Color.White
                ) {
                    dormitoryPowerSupplyByTeacherBottomSheetView(scanInfo,
                        onPowerSupplyHandled = {
                            if (isBiometricFlag) {
                                onBiometricsStartPowerSupplyHandled()
                            }else{
                                showingInputPasswordBottomSheet = true
                            }
                    }, onCancelHandled = {
                        showDormitoryPowerSupplyByTeacherBottomSheet = false
                    })
                }
            }

            //學生開教室
            if (showClassroomPowerSupplyByStudentBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showClassroomPowerSupplyByStudentBottomSheet = false
                    },
                    sheetState = classroomStudentSheetState,
                    containerColor = Color.White
                ) {
                    classroomPowerSupplyByStudentBottomSheetView(scanInfo,
                        onPowerSupplyHandled = { value ->
                            if (isBiometricFlag) {
                                acControlHandled(value)
                                onBiometricsStartPowerSupplyHandled()
                            }else{
                                acControlHandled(value)
                                showingInputPasswordBottomSheet = true
                            }

                        }, onCancelHandled = {
                            showClassroomPowerSupplyByStudentBottomSheet = false
                        })
                }
            }

            //學生開宿舍
            if (showDormitoryPowerSupplyByStudentBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showDormitoryPowerSupplyByStudentBottomSheet = false
                    },
                    sheetState = dormitoryStudentSheetState,
                    containerColor = Color.White
                ) {
                    dormitoryPowerSupplyByStudentBottomSheetView(scanInfo,
                        onPowerSupplyHandled = {
                            if (isBiometricFlag) {
                                onBiometricsStartPowerSupplyHandled()
                            }else{
                                showingInputPasswordBottomSheet = true
                            }
                    }, onCancelHandled = {
                        showDormitoryPowerSupplyByStudentBottomSheet = false
                    })
                }
            }

            if (showingInUseBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showingInUseBottomSheet = false
                    },
                    sheetState = inUseStudentSheetState,
                    containerColor = Color.White
                ) {
                    inUseBottomSheetView(scanInfo, onCancelHandled = {
                        showingInUseBottomSheet = false
                    })
                }
            }

            if (showingPowerEnableBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showingPowerEnableBottomSheet = false
                    },
                    sheetState = powerEnableStudentSheetState,
                    containerColor = Color.White
                ) {
                    powerEnabledBottomSheetView(onCancelHandled = {
                        showingPowerEnableBottomSheet = false
                    })
                }
            }

            if (resVerifyPasswordSuccessFlag) {
                showingInputPasswordBottomSheet = false
                showDormitoryPowerSupplyByTeacherBottomSheet = false
                showClassroomPowerSupplyByTeacherBottomSheet = false
                showClassroomPowerSupplyByStudentBottomSheet = false
                showDormitoryPowerSupplyByStudentBottomSheet = false
            }

            // 生物辨識開電不會經過密碼驗證（resVerifyPasswordSuccessFlag 不會被設 true），
            // 改用開電成功 flag 統一關閉所有 power supply sheet，兩條路徑都涵蓋
            if (resStartPowerSuccessFlag) {
                showingInputPasswordBottomSheet = false
                showDormitoryPowerSupplyByTeacherBottomSheet = false
                showClassroomPowerSupplyByTeacherBottomSheet = false
                showClassroomPowerSupplyByStudentBottomSheet = false
                showDormitoryPowerSupplyByStudentBottomSheet = false
            }

            //輸入密碼
            if (showingInputPasswordBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showingInputPasswordBottomSheet = false
                    },
                    sheetState = inputPasswordSheetState,
                    containerColor = Color.White
                ) {
                    inputPasswordAndLinkBottomSheetView(onInputText = { value ->
                        onVerifyPasswordHandled(value)
                    }, onCancelHandled = {
                            showingInputPasswordBottomSheet = false
                    }, showVerifyPasswordFailDialogFlag = showVerifyPasswordFailDialogFlag,
                        showVerifyPasswordFailMsg = showVerifyPasswordFailMsg,
                        onVerifyPasswordFailDismissed = {
                            onVerifyPasswordFailDismissed()
                        }, onNavigateToSetting = {
                            onNavigateToSetting()
                        })
                }
            }

            if (showScanBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showScanBottomSheet = false
                    },
                    sheetState = scanSheetState,
                    containerColor = Color.White
                ) {
                    scanQRBottomSheetView(onCodeScanned = { code ->
                        showScanBottomSheet = false
                        onScanInfoFinishHandled(code)
                    })
                }
            }

        }
    }
}

@Composable
private fun walletHoursHeaderByTeacher(
    aData: ProfileModel.ProfileData?,
    onNavigateToWalletClick: () -> Unit = {}
) {
    Row {
        Spacer(modifier = Modifier.width(25.dp))
        Surface(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .wrapContentHeight(),
            color = Color.Unspecified
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.wallet_main),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = stringResource(id = R.string.wallet), color = Color.Black, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(30.dp))
                Text(text = "$${aData?.balance?.toAmountString()}", color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
    Row {
        Spacer(modifier = Modifier.width(25.dp))
        Surface(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .wrapContentHeight(),
            color = Color.Unspecified
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.stopcircle_b),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = stringResource(id = R.string.available_hours), color = Color.Black, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(30.dp))
                Text(text = "${aData?.hoursBalance?.calculateDuration()}", color = Color(0xFFD08024), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
    aData?.balance?.let {
        if (it < 0 ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Spacer(modifier = Modifier.width(25.dp))
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .wrapContentHeight()
                        .clickable {
                            onNavigateToWalletClick()
                        },
                    color = Color.Unspecified
                ) {
                    Row {
                        Text(text = "無法使用計費設備，請 ", color = Color(0xFFE54343), style = MaterialTheme.typography.labelLarge)
                        Text(text = "立即儲值", color = Color(0xFFE54343),style = TextStyle(textDecoration = TextDecoration.Underline))
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun infoViewByTeacher(
    hasDevice: Boolean,
    aData: ProfileModel.ProfileData?,
    onScanClick: () -> Unit,
    onGetUsingDeviceDetailByClassroomHandled: (String) -> Unit,
    onGetUsingDeviceDetailByDormitoryHandled: (String) -> Unit,
    onNavigateToWalletClick: () -> Unit = {}) {

    if (hasDevice) {
        Column {
            aData?.activeSession?.sessions?.forEach { item ->
                when (item.space?.spaceType) {
                    "classroom" -> {
                        classroomView(aData = item, onDetailClick = { value ->
                            onGetUsingDeviceDetailByClassroomHandled(value)
                        })
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    "dormitory" -> {
                        dormitoryView(aData = item, onDetailClick = { value ->
                            onGetUsingDeviceDetailByDormitoryHandled(value)
                        })
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }else{
        //未有裝置時
        Row {
            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(230.dp)
                    .drawWithContent {
                        drawContent() // 先畫內容（Surface）
                        val strokeWidthPx = 1.dp.toPx()
                        val cornerRadiusPx = 9.dp.toPx()
                        val inset = strokeWidthPx / 2
                        drawRoundRect(
                            color = Color(0xFF656565),
                            topLeft = Offset(inset, inset),
                            size = Size(
                                width = size.width - strokeWidthPx,
                                height = size.height - strokeWidthPx
                            ),
                            style = Stroke(
                                width = strokeWidthPx,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                            ),
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                        )
                    }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onScanClick() },
                    color = Color.White,
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                            Column (horizontalAlignment = Alignment.CenterHorizontally){
                                Spacer(modifier = Modifier.height(20.dp))
                                Surface (modifier = Modifier, color = Color.Unspecified){
                                    Image(painter = painterResource(id = R.drawable.qrcode_blue), contentDescription = "")
                                }
                                Spacer(modifier = Modifier.height(25.dp))
                                Text(stringResource(R.string.scan_for_power_usage), color = Color.Black,style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(stringResource(R.string.scan_location_qr_code_to_start), color = Color(0xFF656565),style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(30.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun walletHoursHeaderByStudent(
    aData: ProfileModel.ProfileData?,
    onNavigateToWalletClick: () -> Unit = {}
) {
    Row {
        Spacer(modifier = Modifier.width(25.dp))
        Surface(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .wrapContentHeight(),
            color = Color.Unspecified
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.wallet_main),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = stringResource(id = R.string.wallet), color = Color.Black, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.width(30.dp))
                Text(text = "$${aData?.balance?.toAmountString()}", color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
    aData?.balance?.let {
        if (it < 0 ) {
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Spacer(modifier = Modifier.width(25.dp))
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .wrapContentHeight()
                        .clickable {
                            onNavigateToWalletClick()
                        },
                    color = Color.Unspecified
                ) {
                    Row {
                        Text(text = "無法使用計費設備，請 ", color = Color(0xFFE54343), style = MaterialTheme.typography.labelLarge)
                        Text(text = "立即儲值", color = Color(0xFFE54343),style = TextStyle(textDecoration = TextDecoration.Underline))
                    }
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun infoViewByStudent(
    hasDevice: Boolean,
    aData: ProfileModel.ProfileData?,
    onScanClick: () -> Unit,
    onGetUsingDeviceDetailByClassroomHandled: (String) -> Unit,
    onGetUsingDeviceDetailByDormitoryHandled: (String) -> Unit,
    onNavigateToWalletClick: () -> Unit = {}) {

    if (hasDevice) {
        Column {
            aData?.activeSession?.sessions?.forEach { item ->
                when (item.space?.spaceType) {
                    "classroom" -> {
                        classroomView(aData = item, onDetailClick = { value ->
                            onGetUsingDeviceDetailByClassroomHandled(value)
                        })
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    "dormitory" -> {
                        dormitoryView(aData = item, onDetailClick = { value ->
                            onGetUsingDeviceDetailByDormitoryHandled(value)
                        })
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }else{
        Row {
            Spacer(modifier = Modifier.width(30.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(230.dp)
                    .drawWithContent {
                        drawContent() // 先畫內容（Surface）
                        val strokeWidthPx = 1.dp.toPx()
                        val cornerRadiusPx = 9.dp.toPx()
                        val inset = strokeWidthPx / 2
                        drawRoundRect(
                            color = Color(0xFF656565),
                            topLeft = Offset(inset, inset),
                            size = Size(
                                width = size.width - strokeWidthPx,
                                height = size.height - strokeWidthPx
                            ),
                            style = Stroke(
                                width = strokeWidthPx,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                            ),
                            cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                        )
                    }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onScanClick() },
                    color = Color.White,
                    shape = RoundedCornerShape(9.dp)
                ) {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                            Column (horizontalAlignment = Alignment.CenterHorizontally){
                                Spacer(modifier = Modifier.height(20.dp))
                                Surface (modifier = Modifier, color = Color.Unspecified){
                                    Image(painter = painterResource(id = R.drawable.qrcode_blue), contentDescription = "")
                                }
                                Spacer(modifier = Modifier.height(25.dp))
                                Text(stringResource(R.string.scan_for_power_usage), color = Color.Black,style = MaterialTheme.typography.bodyLarge)
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(stringResource(R.string.scan_location_qr_code_to_start), color = Color(0xFF656565),style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(20.dp))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(30.dp))
        }
    }
}

@Composable
private fun classroomView(aData: ProfileModel.UsingDeviceData?, onDetailClick: (String) -> Unit) {
    var currentElapsed by remember {
        mutableStateOf(elapsedTime(aData?.billing?.general?.startTime ?: "")) }

    LaunchedEffect(aData?.billing?.general?.startTime) {
        while (true) {
            currentElapsed = elapsedTime(aData?.billing?.general?.startTime ?: "")
            delay(1000L)
        }
    }

    Row {
        Spacer(modifier = Modifier.width(30.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onDetailClick(aData?.device?.deviceCode ?: "") },
            color = Color(0xFFD08024),
            shape = RoundedCornerShape(9.dp)
        ) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Surface (modifier = Modifier
                    .weight(1f)
                    , color = Color(0xFFD08024)){
                    Column (){
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Spacer(modifier = Modifier.width(30.dp))
                            Text("${aData?.space?.spaceName}", color = Color.White,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row {
                            Spacer(modifier = Modifier.width(30.dp))
                            Text(stringResource(R.string.classroom), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color.White).padding(2.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(stringResource(R.string.general_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Black).padding(2.dp))
                            if (aData?.device?.powerSupply?.ac?.on == true) {
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(stringResource(R.string.air_conditioner_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color(0xFF2D859D)).padding(2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row (verticalAlignment = Alignment.CenterVertically){
                            Spacer(modifier = Modifier.width(30.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.timer_w),
                                contentDescription = "",
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(stringResource(R.string.cumulative_time), color = Color.White,style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(currentElapsed, color = Color.White,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                        }

                    }
                }
                Surface (
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.1f)
                        .height(170.dp),
                    color = Color(0xFFD08024)
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.caretright),
                        tint = Color.Unspecified,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(30.dp))
    }
}

@Composable
private fun dormitoryView(aData: ProfileModel.UsingDeviceData?, onDetailClick: (String) -> Unit) {
    Row {
        Spacer(modifier = Modifier.width(30.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onDetailClick(aData?.device?.deviceCode ?: "") },
            color = Color(0xFF2D859D),
            shape = RoundedCornerShape(9.dp)
        ) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Surface (modifier = Modifier
                    .weight(1f)
                    , color = Color(0xFF2D859D)){
                    Column (){
                        Spacer(modifier = Modifier.height(20.dp))
                        Row {
                            Spacer(modifier = Modifier.width(30.dp))
                            Text("${aData?.space?.spaceName}", color = Color.White,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row {
                            Spacer(modifier = Modifier.width(30.dp))
                            Text(stringResource(R.string.dormitory), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color.White).padding(2.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(stringResource(R.string.dormitory_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Black).padding(2.dp))
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Row (verticalAlignment = Alignment.CenterVertically){
                            Spacer(modifier = Modifier.width(30.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.lightning_w),
                                contentDescription = "",
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("${stringResource(R.string.cumulative_deduction_amount)}  $", color = Color.White,style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("${aData?.billing?.general?.totalAmount}", color = Color.White,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                        }

                    }
                }
                Surface (
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(0.1f)
                        .height(170.dp),
                    color = Color(0xFF2D859D)
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.caretright),
                        tint = Color.Unspecified,
                        contentDescription = "Localized description"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(30.dp))
    }
}

@Composable
private fun CameraPreview(modifier: Modifier = Modifier, onCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var isScanning by remember { mutableStateOf(true) }

    // 用 DisposableEffect 而非 LaunchedEffect：相機是綁在 Activity 的 lifecycleOwner 上，
    // 這個 composable 消失時不會自動解除，不 unbind 的話後鏡頭會一直開著，
    // 後續的臉部辨識搶不到相機（「相機正在使用中」）。
    DisposableEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val barcodeScanner = BarcodeScanning.getClient()
        var cameraProvider: ProcessCameraProvider? = null

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = androidx.camera.core.Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            analysis.setAnalyzer(Dispatchers.Default.asExecutor()) { imageProxy ->
                if (isScanning) {
                    processImageProxy(barcodeScanner, imageProxy) { result ->
                        // 掃到結果時停止掃描
                        isScanning = false
                        onCodeScanned(result)
                    }
                }else{
                    imageProxy.close()
                }
            }

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    analysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            cameraProvider?.unbindAll()
            barcodeScanner.close()
        }
    }

    AndroidView(factory = { previewView }, modifier = modifier)
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    scanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let {
                        Log.d("DAE_Develop", "掃描成功：$it")
                        onCodeScanned(it)
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun scanQRBottomSheetView(onCodeScanned: (String) -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(stringResource(R.string.scan_location_qr_code_to_start), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(25.dp))

                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(1.dp))
            ) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onCodeScanned = {
                        onCodeScanned(it)
                    }
                )

                // 四角直角線條
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cornerLength = 40.dp.toPx()   // 線條長度
                    val strokeWidth = 5.dp.toPx()      // 線條粗細
                    val color = Color(0xFF2D859D)             // 線條顏色
                    val offset = 0f                     // 離邊緣的距離（0 = 貼邊）

                    // 左上角
                    drawLine(color, Offset(offset, offset), Offset(offset + cornerLength,
                        offset), strokeWidth)
                    drawLine(color, Offset(offset, offset), Offset(offset, offset +
                            cornerLength), strokeWidth)

                    // 右上角
                    drawLine(color, Offset(size.width - offset, offset), Offset(size.width -
                            offset - cornerLength, offset), strokeWidth)
                    drawLine(color, Offset(size.width - offset, offset), Offset(size.width -
                            offset, offset + cornerLength), strokeWidth)

                    // 左下角
                    drawLine(color, Offset(offset, size.height - offset), Offset(offset +
                            cornerLength, size.height - offset), strokeWidth)
                    drawLine(color, Offset(offset, size.height - offset), Offset(offset,
                        size.height - offset - cornerLength), strokeWidth)

                    // 右下角
                    drawLine(color, Offset(size.width - offset, size.height - offset),
                        Offset(size.width - offset - cornerLength, size.height - offset), strokeWidth)
                    drawLine(color, Offset(size.width - offset, size.height - offset),
                        Offset(size.width - offset, size.height - offset - cornerLength), strokeWidth)
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun parseDialogMsg(aMsg: String):(String){
    var msg: String = ""
    if (aMsg == "PleaseReLogin") {
        msg = stringResource(id = R.string.please_re_login)
    }else if(aMsg == "PasswordNotEntered") {
        msg = stringResource(R.string.password_not_entered)
    }else {
        msg = aMsg
    }
    return msg
}

private fun elapsedTime(startTime: String): String {
    return try {
        val startDate = ZonedDateTime.parse(startTime).toInstant()
        val elapsed = ChronoUnit.SECONDS.between(startDate, Instant.now())

        if (elapsed < 0) return "00:00:00"

        val hours = elapsed / 3600
        val minutes = (elapsed % 3600) / 60
        val seconds = elapsed % 60

        String.format("%02d:%02d:%02d", hours, minutes, seconds)

    } catch (e: Exception) {
        "00:00:00"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun classroomPowerSupplyByTeacherBottomSheetView(aData: ScanModel.ScanData?, onPowerSupplyHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface (modifier = Modifier, color = Color.Unspecified){
                        Image(painter = painterResource(id = R.drawable.checkcircle_g), contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(stringResource(R.string.scan_success), color = Color.Black, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(stringResource(R.string.please_confirm_following_info), color = Color(0xFF656565), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        Row (){
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 190.dp), // 字體放大時跟著 Column 內容往下長
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 35.dp))
                    Text(stringResource(R.string.classroom), color = Color(0xFFDF8927),style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFFDF8927)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.spaceName}", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.buildingName} ${aData?.floorName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(35.dp))
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
                        color = Color(0xFF2D859D)
                    )
                    .clickable {
                        onPowerSupplyHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.start_power_supply),
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
private fun dormitoryPowerSupplyByTeacherBottomSheetView(aData: ScanModel.ScanData?, onPowerSupplyHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface (modifier = Modifier, color = Color.Unspecified){
                        Image(painter = painterResource(id = R.drawable.checkcircle_g), contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(stringResource(R.string.scan_success), color = Color.Black, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(stringResource(R.string.please_confirm_following_info), color = Color(0xFF656565), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        Row (){
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 230.dp), // 字體放大時跟著 Column 內容往下長
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 30.dp))
                    Text(stringResource(R.string.dormitory), color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFF2D859D)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.spaceName}", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.buildingName} ${aData?.floorName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(15.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${stringResource(R.string.rate)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${aData?.rate}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${stringResource(R.string.currency_unit)}/${stringResource(R.string.electricity_unit)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    }
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
                        color = Color(0xFF2D859D)
                    )
                    .clickable {
                        onPowerSupplyHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.start_power_supply),
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
private fun classroomPowerSupplyByStudentBottomSheetView(aData: ScanModel.ScanData?, onPowerSupplyHandled: (Boolean) -> Unit, onCancelHandled: () -> Unit) {
    var isAcControl by remember { mutableStateOf(false) }
    var showAcNotOpenAlert by remember { mutableStateOf(false) }
    val isAcOpenPeriod = aData?.isAcOpenPeriod == true
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface (modifier = Modifier, color = Color.Unspecified){
                        Image(painter = painterResource(id = R.drawable.checkcircle_g), contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(stringResource(R.string.scan_success), color = Color.Black, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(stringResource(R.string.please_confirm_following_info), color = Color(0xFF656565), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        Row (){
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 230.dp), // 字體放大時跟著 Column 內容往下長
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 30.dp))
                    Text(stringResource(R.string.classroom), color = Color(0xFFD08024), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFFD08024)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.spaceName}", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.buildingName} ${aData?.floorName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(15.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${stringResource(R.string.rate)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${aData?.rate}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${stringResource(R.string.currency_unit)}/${stringResource(R.string.minute)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    }
                }

            }
            Spacer(modifier = Modifier.width(30.dp))
        }
        Spacer(modifier = Modifier.height( 15.dp))
        Row (verticalAlignment = Alignment.CenterVertically){
            Spacer(modifier = Modifier.width(40.dp))
            Text("${stringResource(R.string.confirm_turn_on_air_conditioner)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.width(5.dp))
            Spacer(modifier = Modifier.weight(1f))
            Surface (modifier = Modifier.padding(start = 10.dp),color = Color.Unspecified){
                Box {
                    Switch(
                        checked = isAcControl,
                        onCheckedChange = { checked ->
                            isAcControl = checked
                        },
                        // 只有在冷氣開放時段、且非 freeMode（免費模式）時才可開啟
                        enabled = isAcOpenPeriod && aData?.freeMode != true
                    )
                    // 非開放時段 Switch 被 disable 收不到點擊，蓋一層透明區塊攔截點擊跳提示
                    if (!isAcOpenPeriod) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { showAcNotOpenAlert = true }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(40.dp))
        }

        if (showAcNotOpenAlert) {
            textTNoButtonAlert(
                onDismissRequest = {},
                dialogTitle = stringResource(R.string.ac_not_open_period)
            )
            // 在 Dialog 顯示後啟動計時器
            LaunchedEffect(Unit) {
                delay(1500) // 延遲 1.5 秒
                showAcNotOpenAlert = false
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
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        color = Color(0xFF2D859D)
                    )
                    .clickable {
                        onPowerSupplyHandled(isAcControl)
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.start_power_supply),
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
private fun dormitoryPowerSupplyByStudentBottomSheetView(aData: ScanModel.ScanData?, onPowerSupplyHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface (modifier = Modifier, color = Color.Unspecified){
                        Image(painter = painterResource(id = R.drawable.checkcircle_g), contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text(stringResource(R.string.scan_success), color = Color.Black, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(stringResource(R.string.please_confirm_following_info), color = Color(0xFF656565), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
        Row (){
            Spacer(modifier = Modifier.width(30.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 230.dp), // 字體放大時跟著 Column 內容往下長
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 30.dp))
                    Text(stringResource(R.string.dormitory), color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFF2D859D)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.spaceName}", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.buildingName} ${aData?.floorName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(15.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${stringResource(R.string.rate)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${aData?.rate}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("${stringResource(R.string.currency_unit)}/${stringResource(R.string.electricity_unit)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    }
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
                        color = Color(0xFF2D859D)
                    )
                    .clickable {
                        onPowerSupplyHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.start_power_supply),
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
private fun inputPasswordAndLinkBottomSheetView(onInputText:(String) -> Unit, onCancelHandled: () -> Unit, showVerifyPasswordFailDialogFlag: Boolean, showVerifyPasswordFailMsg: String?, onVerifyPasswordFailDismissed: () -> Unit, onNavigateToSetting: () -> Unit) {
    var inputPasswordText by remember { mutableStateOf("") }
    Column {
        Spacer(modifier = Modifier.height(40.dp))
        Row {
            Spacer(modifier = Modifier.width(20.dp))
            Text("${stringResource(R.string.enter_password)}", color = Color.Black, style = MaterialTheme.typography.titleLarge)
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
                Text(
                    text = stringResource(R.string.go_to_enable_biometric),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight().clickable{ onNavigateToSetting() },
                    color = Color.Black,
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
                Spacer(Modifier.weight(1f))
                Text(parseDialogMsg(showVerifyPasswordFailMsg ?: ""), color = Color(0xFFE54343))
                Spacer(modifier = Modifier.width(20.dp))
            }
        }else {
            Spacer(modifier = Modifier.height( 10.dp))
            Row {
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = stringResource(R.string.go_to_enable_biometric),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight().clickable{ onNavigateToSetting() },
                    color = Color.Black,
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
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
                    text = stringResource(R.string.submit),
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
                    text = stringResource(R.string.previous_step),
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
private fun powerEnabledBottomSheetView(onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface (modifier = Modifier, color = Color.Unspecified){
                        Image(painter = painterResource(id = R.drawable.lightningslash), contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text("此空間目前不開放用電", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(5.dp))
                }
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
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        color = Color(0xFF2D859D)
                    )
                    .clickable {
                        onCancelHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.back),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun inUseBottomSheetView(aData: ScanModel.ScanData?, onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(30.dp))

                    Text("該空間已被使用", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(35.dp))
                    Text("如需用電，請至 ${aData?.spaceName} \n" + "按下「Enter按鈕」", color = Color.Black, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center,)
                    Spacer(modifier = Modifier.height(20.dp))
                }
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
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        color = Color(0xFF2D859D)
                    )
                    .clickable {
                        onCancelHandled()
                    }
                ,

                color = Color.Transparent
            ) {
                Text(
                    text = "我知道了",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(30.dp))
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}


// 預覽UI

@Preview(showBackground = true)
@Composable
private fun HomePreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)

    //homeContent(navController,navController)

}

@Preview(showBackground = true)
@Composable
private fun BottomSheetViewPreview() {
    STEMS_CampusTheme {
//        classroomPowerSupplyByTeacherBottomSheetView()
//         dormitoryPowerSupplyByTeacherBottomSheetView()
//        classroomPowerSupplyByStudentBottomSheetView()
//        inputPasswordAndLinkBottomSheetView("")
//        powerEnabledBottomSheetView()
//        inUseBottomSheetView()
//        scanQRBottomSheetView()
    }

}