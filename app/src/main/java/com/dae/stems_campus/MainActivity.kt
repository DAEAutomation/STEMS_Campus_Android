package com.dae.stems_campus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dae.stems_campus.ui.screen.login.forgotPwScreen
import com.dae.stems_campus.ui.screen.login.forgotPwVerificationCodeScreen
import com.dae.stems_campus.ui.screen.login.login
import com.dae.stems_campus.ui.screen.login.registerVerificationCodeScreen
import com.dae.stems_campus.ui.screen.login.resetPwScreen
import com.dae.stems_campus.ui.screen.login.setPasswordScreen
import com.dae.stems_campus.ui.screen.login.setUserNameInfoScreen
import com.dae.stems_campus.ui.screen.initialize.selectSchoolScreen
import com.dae.stems_campus.ui.screen.initialize.serviceUnavailableScreen
import com.dae.stems_campus.ui.screen.initialize.needUpdateScreen
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.viewmodel.AuthState
import com.dae.stems_campus.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.dae.stems_campus.network.SessionEventBus
import com.dae.stems_campus.ui.screen.mainTabScreen
import com.dae.stems_campus.viewmodel.PushNotificationViewModel
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            STEMS_CampusTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent () {
    val navController = rememberNavController()
    val viewModel: AuthViewModel = hiltViewModel()
    val pushNotificationViewModel: PushNotificationViewModel = hiltViewModel()
    val authState by viewModel.authState.collectAsState()

    val context = LocalContext.current
    var showPushNotificationSettingDialogFlag by remember { mutableStateOf(false) }

    // 啟動時檢查 token 與通知權限
    LaunchedEffect(Unit) {
        viewModel.checkToken()

        // 沒有開啟通知 → 跳 dialog 導使用者去設定頁
        val notificationsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
        if (!notificationsEnabled) {
            showPushNotificationSettingDialogFlag = true
        }
    }

    // 已登入狀態下才主動同步 FCM token；避免未登入時送 token 觸發 401 / forceLogout flash
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            pushNotificationViewModel.fcm()
        }
    }

    // BaseRepository 走到 Unauthorized 時會 emit forceLogout。
    // 不能依賴 _authState 變化來驅動導航（登入流程沒有把它切到 Authenticated，
    // 因此真的過期時 state 不會改變，LaunchedEffect(authState) 不會觸發）。
    // 直接 collect event 自己 navigate，1.5 秒延遲跟畫面端 dialog 對齊。
    LaunchedEffect(Unit) {
        SessionEventBus.forceLogout.collect {
            delay(1500)
            navController.navigate("signIn") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    if (showPushNotificationSettingDialogFlag) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("需要通知權限") },
            text = { Text("請開啟通知權限以接收重要訊息") },
            confirmButton = {
                TextButton(onClick = {
                    val intent = Intent().apply {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                    context.startActivity(intent)
                    showPushNotificationSettingDialogFlag = false
                }) {
                    Text("前往設定")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showPushNotificationSettingDialogFlag = false
                }) {
                    Text("稍後")
                }
            }
        )
    }

    when (authState) {
        is AuthState.Loading -> {

        }
        is AuthState.NeedSchoolSelection -> {
            NavHost(navController = navController, startDestination = "selectSchool") {
                composable("selectSchool") {
                    selectSchoolScreen(
                        navController = navController,
                        onConfirmed = {
                            // 選完學校後重新檢查（會走到 Unauthenticated）
                            viewModel.checkToken()
                        }
                    )
                }
            }
        }
        is AuthState.ServiceUnavailable -> {
            serviceUnavailableScreen(message = (authState as AuthState.ServiceUnavailable).message)
        }
        is AuthState.NeedAppUpdate -> {
            val state = authState as AuthState.NeedAppUpdate
            needUpdateScreen(
                currentVersion = state.currentVersion,
                requiredVersion = state.requiredVersion
            )
        }
        is AuthState.Authenticated -> {
            NavHost(navController = navController, startDestination = "first") {
                composable ("signIn"){
                    login(navController)
                }
                composable ("VerificationCode/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    registerVerificationCodeScreen(navController, email = email ?: "")
                }
                composable ("ForgotPw") {
                    forgotPwScreen(navController)
                }
                composable ("ForgotPwVerificationCode/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    forgotPwVerificationCodeScreen(navController, email = email ?: "")
                }
                composable ("ResetPw/{email}/{verifiedToken}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    val verifiedToken = backStackEntry.arguments?.getString("verifiedToken")
                    resetPwScreen(navController, email = email ?: "", verifiedToken = verifiedToken ?: "")
                }
                composable ("SetUserNameInfo/{email}/{verifiedToken}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    val verifiedToken = backStackEntry.arguments?.getString("verifiedToken")
                    setUserNameInfoScreen(navController, email = email ?: "", verifiedToken = verifiedToken ?: "")
                }
                composable ("SetPassword/{email}/{verifiedToken}/{studentId}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    val verifiedToken = backStackEntry.arguments?.getString("verifiedToken")
                    val studentId = backStackEntry.arguments?.getString("studentId")
                    setPasswordScreen(navController, email = email ?: "", verifiedToken = verifiedToken ?: "", studentId = studentId ?: "")
                }
                composable ("first"){
                    mainTabScreen(navController)
                }

            }
        }
        is AuthState.Unauthenticated -> {
            NavHost(navController = navController, startDestination = "signIn") {
                composable ("signIn"){
                    login(navController)
                }
                composable ("VerificationCode/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    registerVerificationCodeScreen(navController, email = email ?: "")
                }
                composable ("ForgotPw") {
                    forgotPwScreen(navController)
                }
                composable ("ForgotPwVerificationCode/{email}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    forgotPwVerificationCodeScreen(navController, email = email ?: "")
                }
                composable ("ResetPw/{email}/{verifiedToken}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    val verifiedToken = backStackEntry.arguments?.getString("verifiedToken")
                    resetPwScreen(navController, email = email ?: "", verifiedToken = verifiedToken ?: "")
                }
                composable ("SetUserNameInfo/{email}/{verifiedToken}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    val verifiedToken = backStackEntry.arguments?.getString("verifiedToken")
                    setUserNameInfoScreen(navController, email = email ?: "", verifiedToken = verifiedToken ?: "")
                }
                composable ("SetPassword/{email}/{verifiedToken}/{studentId}") { backStackEntry ->
                    val email = backStackEntry.arguments?.getString("email")
                    val verifiedToken = backStackEntry.arguments?.getString("verifiedToken")
                    val studentId = backStackEntry.arguments?.getString("studentId")
                    setPasswordScreen(navController, email = email ?: "", verifiedToken = verifiedToken ?: "", studentId = studentId ?: "")
                }
                composable ("first"){
                    mainTabScreen(navController)
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    STEMS_CampusTheme {
        Greeting("Android")
    }
}