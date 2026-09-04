package com.dae.stems_campus.ui.screen.initialize

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.utils.toLocalDateTimeText
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun serviceUnavailableScreen(message: String, endTime: String) {

    Column (modifier = Modifier.fillMaxSize()) {
        Surface(modifier = Modifier.weight(0.2f).fillMaxWidth(), color = Color(0xFF2D859D)) {}
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color(0xFF2D859D),
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(90.dp))
                Surface(modifier = Modifier, color = Color.Unspecified) {
                    Image(
                        painter = painterResource(id = R.drawable.stems_capus_w_s_1),
                        contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row (modifier = Modifier.background(Color(0xFF2D859D))){
                    Spacer(modifier = Modifier.width(30.dp))
                    Surface(
                        modifier = Modifier
                            .weight(1f),
                        color = Color.White,
                        shape = RoundedCornerShape(9.dp)
                    ) {

                        Column(
                            modifier = Modifier,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(30.dp))
                            Image(painter = painterResource(id = R.drawable.gear), contentDescription = "")
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = buildMaintenanceText(
                                    message.ifEmpty { "系統維護中\nUnder maintenance" }
                                ),
                                color = Color(0xFF2D859D),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                            Text(
                                text = "預計恢復時間",
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            val dateTimeText = endTime?.toLocalDateTimeText("yyyy-MM-dd HH:mm") ?: "--"
                            Text(
                                text = dateTimeText,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                }
            }

        }

        //底部logo
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = Color(0xFF2D859D),
        ) {
            Column (modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally){
                Surface (modifier = Modifier,
                    color = Color.Unspecified){
                    Image(painter = painterResource(id = R.drawable.dae_logo_w), contentDescription = "")
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Copyright © "+ getCurrentYear() +" DAE instrument CO., Ltd. All rights reserved",
                    textAlign = TextAlign.Center,
                    modifier = Modifier,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }
    }
}

private fun getCurrentYear(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy") // 設定日期格式
    val today = LocalDate.now() // 取得今天的日期
    // 格式化輸出
    val yearStr = today.format(formatter)
    return yearStr
}

@Preview(showBackground = true)
@Composable
private fun serviceUnavailablePreview() {
    serviceUnavailableScreen("","")
}


/**
 * 把 "系統維護中\nUnder maintenance" 這種多行訊息，
 * 第一行用大字（粗體），其餘行用小字。
 */
@Composable
private fun buildMaintenanceText(message: String): AnnotatedString {
    val titleStyle = MaterialTheme.typography.headlineMedium
        .toSpanStyle()
        .copy(fontWeight = FontWeight.Bold)

    val detailStyle = MaterialTheme.typography.bodyLarge
        .toSpanStyle()


    val lines = message.split("\n")
    return buildAnnotatedString {
        lines.forEachIndexed { index, line ->
            if (index > 0) append("\n")
            if (index == 0) {
                withStyle(titleStyle) { append(line) }
            } else {
                withStyle(detailStyle) { append(line) }
            }
        }
    }
}
