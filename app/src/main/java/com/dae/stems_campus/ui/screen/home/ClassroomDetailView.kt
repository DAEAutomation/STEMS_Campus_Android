package com.dae.stems_campus.ui.screen.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R

@Composable
fun classroomDetailScreen(navController: NavHostController, onShowTabBarChange: (Boolean) -> Unit) {

    // 進入畫面時隱藏
    LaunchedEffect(Unit) {
        onShowTabBarChange(false)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.classroom_power_usage),
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
                    classroomDetailMainLoad()
                }
            }
        )
    }

    //處理手機上的Back按鍵
    BackHandler {
        onShowTabBarChange(true)
        navController.navigateUp()
    }

}

@Composable
private fun classroomDetailMainLoad() {
    Column {
        Spacer(modifier = Modifier.height(30.dp))
//        contentViewByTeacher()
        contentViewByStudent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopTitleBar(navTitle: String, navController: NavHostController, onShowTabBarChange: (Boolean) -> Unit) {
    CenterAlignedTopAppBar(
        modifier = Modifier.statusBarsPadding(),
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
private fun contentViewByTeacher() {
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface (modifier = Modifier
            .weight(1f)
            .height(400.dp),
            color = Color.White,
            shape = RoundedCornerShape(9.dp)){

            Column {
                Spacer(modifier = Modifier.height(25.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(R.string.classroom), color = Color(0xFFDF8927),style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFFDF8927)).padding(2.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(stringResource(R.string.general_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Black).padding(2.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(stringResource(R.string.air_conditioner_power_supply), color = Color.White, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color(0xFF2D859D)).padding(2.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("A101", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("XXXX", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(50.dp))
                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Spacer(modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(color = Color(0xFFABABAB)))

                    Spacer(modifier = Modifier.width(10.dp))
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.timer_b),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(stringResource(R.string.cumulative_time), color = Color.Black, style = MaterialTheme.typography.bodyLarge)

                }
                Spacer(modifier = Modifier.height(20.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("00:00:00", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.Unspecified
                            ),
                        color = Color.Transparent
                    ) {

                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFE54343))
//                                .clickable {
////                                                loginViewModel.loginAction(accountText,passwordText)
////                                                navController.navigate("first")
//                                },
                        ,
                        color = Color.Transparent
                    ) {
                        Text(
                            text = stringResource(R.string.stop_power_supply),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentHeight(),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }


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
                        Text("", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                }
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}

@Composable
private fun contentViewByStudent() {
    Row {
        Spacer(modifier = Modifier.width(20.dp))
        Surface (modifier = Modifier
            .weight(1f)
            .height(480.dp),
            color = Color.White,
            shape = RoundedCornerShape(9.dp)){

            Column {
                Spacer(modifier = Modifier.height(25.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(stringResource(R.string.classroom), color = Color(0xFFDF8927),style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFFDF8927)).padding(2.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(stringResource(R.string.general_power_supply), color = Color.White,style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Black).padding(2.dp))
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(stringResource(R.string.air_conditioner_power_supply), color = Color.White, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color(0xFF2D859D)).padding(2.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("A101", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("XXXX", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${stringResource(R.string.rate)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("3.5", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("${stringResource(R.string.currency_unit)}/${stringResource(R.string.minute)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(50.dp))
                Row {
                    Spacer(modifier = Modifier.width(10.dp))
                    Spacer(modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .background(color = Color(0xFFABABAB)))

                    Spacer(modifier = Modifier.width(10.dp))
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.timer_b),
                        contentDescription = "",
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(stringResource(R.string.cumulative_time), color = Color.Black, style = MaterialTheme.typography.bodyLarge)

                }
                Spacer(modifier = Modifier.height(20.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("00:00:00", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row (verticalAlignment = Alignment.CenterVertically){
                    Spacer(modifier = Modifier.width(20.dp))
                    Text("${stringResource(R.string.cumulative_deduction_amount)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("100", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("${stringResource(R.string.currency_unit)}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(40.dp))
                Row {
                    Spacer(modifier = Modifier.width(20.dp))
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFE54343))
//                                .clickable {
////                                                loginViewModel.loginAction(accountText,passwordText)
////                                                navController.navigate("first")
//                                },
                        ,
                        color = Color.Transparent
                    ) {
                        Text(
                            text = stringResource(R.string.stop_power_supply),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentHeight(),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(45.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFFE54343))
//                                .clickable {
////                                                loginViewModel.loginAction(accountText,passwordText)
////                                                navController.navigate("first")
//                                },
                        ,
                        color = Color.Transparent
                    ) {
                        Text(
                            text = stringResource(R.string.stop_power_supply),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.wrapContentHeight(),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }


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
                        Text("", color = Color.White, style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                }
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}


@Preview(showBackground = true)
@Composable
private fun classroomDetailPreview() {
    val navController = TestNavHostController(LocalContext.current)
    classroomDetailScreen(navController, onShowTabBarChange = {})
}