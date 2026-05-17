package com.dae.stems_campus

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
//    val pushNotificationViewModel: PushNotificationViewModel = hiltViewModel()
    val authState by viewModel.authState.collectAsState()

    // 啟動時檢查 token
    LaunchedEffect(Unit) {
//        pushNotificationViewModel.fcm()
        viewModel.checkToken()
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