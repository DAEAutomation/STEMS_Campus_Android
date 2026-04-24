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
import com.dae.stems_campus.ui.screen.login.login
import com.dae.stems_campus.ui.screen.initialize.selectSchoolScreen
import com.dae.stems_campus.ui.screen.initialize.serviceUnavailableScreen
import com.dae.stems_campus.ui.screen.initialize.needUpdateScreen
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.viewmodel.AuthState
import com.dae.stems_campus.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import com.dae.stems_campus.ui.screen.mainTabScreen

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
//                composable("ForgotPw") {
//                    forgotPwScreen(navController)
//                }
//                composable("ForgotPwVerificationCode/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email")
//                    forgotPwVerificationCodeScreen(navController, email = email ?: "")
//                }
//                composable ("ResetPw/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email")
//                    resetPwScreen(navController, email = email ?: "")
//                }
//                composable ("VerificationCode/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email")
//                    verificationCodeScreen(navController, email = email ?: "")
//                }
//                composable ("SetUsernamePassword/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email")
//                    setUsernamePasswordScreen(navController, email = email ?: "")
//                }
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
//                composable("ForgotPw") {
//                    forgotPwScreen(navController)
//                }
//                composable("ForgotPwVerificationCode/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email")
//                    forgotPwVerificationCodeScreen(navController, email = email ?: "")
//                }
//                composable ("ResetPw/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email")
//                    resetPwScreen(navController, email = email ?: "")
//                }
//                composable ("VerificationCode/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email")
//                    verificationCodeScreen(navController, email = email ?: "")
//                }
//                composable ("SetUsernamePassword/{email}") { backStackEntry ->
//                    val email = backStackEntry.arguments?.getString("email")
//                    setUsernamePasswordScreen(navController, email = email ?: "")
//                }
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