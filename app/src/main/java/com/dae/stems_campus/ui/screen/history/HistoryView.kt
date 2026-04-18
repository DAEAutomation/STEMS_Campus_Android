package com.dae.stems_campus.ui.screen.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R

@Composable
fun historyScreen(mainNavController: NavController, onShowTabBarChange: (Boolean) -> Unit) {
    val settingNavController = rememberNavController()

    NavHost(navController = settingNavController, startDestination = "History") {
        composable("History") {

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun historyMainLoad(mainNavController: NavController, navController: NavHostController,  onShowTabBarChange: (Boolean) -> Unit) {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun historyContent(
    mainNavController: NavController,
    navController: NavHostController) {

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
                        Text("${stringResource(R.string.history)}", color = Color.Black,
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

// 預覽UI

@Preview(showBackground = true)
@Composable
private fun historyPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    historyContent (navController,navController)
}