package com.dae.stems_campus.ui.screen.initialize

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.HistoryModel
import com.dae.stems_campus.data.model.SchoolHostModel
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.viewmodel.SelectSchoolViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun selectSchoolScreen(
    navController: NavHostController,
    onConfirmed: () -> Unit = {},
    selectSchoolViewModel: SelectSchoolViewModel = hiltViewModel()
) {
    val schools by selectSchoolViewModel.schools.collectAsState()
    val confirmedFlag by selectSchoolViewModel.confirmedFlag.collectAsState()
    val errorMessage by selectSchoolViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        selectSchoolViewModel.loadSchools()
    }

    LaunchedEffect(confirmedFlag) {
        if (confirmedFlag) {
            selectSchoolViewModel.resetConfirmedFlag()
            onConfirmed()
        }
    }

    selectSchoolContent(
        navController = navController,
        schoolList = schools,
        onConfirmClick = { school ->
            school.host?.let { selectSchoolViewModel.confirmSchool(it) }
        }
    )

    if (errorMessage != null) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = errorMessage ?: ""
        )
        LaunchedEffect(errorMessage) {
            delay(1500)
            selectSchoolViewModel.clearErrorMessage()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun selectSchoolContent(
    navController: NavHostController,
    schoolList: List<SchoolHostModel>,
    onConfirmClick: (SchoolHostModel) -> Unit = {}
    ) {

    val focusEmailText = remember { FocusRequester() }

    var showSchoolListBottomSheet by remember { mutableStateOf(false) }
    val schoolSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSchool by remember { mutableStateOf<SchoolHostModel?>(null) }

    Column {
        Surface (modifier = Modifier.weight(1.5f).fillMaxWidth(), color = Color(0xFFF4F4F4)){}
        Surface (modifier = Modifier.weight(5f).fillMaxWidth(), color = Color(0xFFF4F4F4)){
            Column {
                Row {
                    Spacer(modifier = Modifier.width(60.dp))
                    Text(stringResource(R.string.select_school),
                        color = Color(0xFF303236),
                        style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                }
                Spacer(modifier = Modifier.height(30.dp))
                Row {
                    Spacer(modifier = Modifier.width(30.dp))
                    Surface (modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f), color = Color(0xFFF4F4F4),
                        shape = RoundedCornerShape(30.dp),
                    ){
                        Column {
                            Spacer(modifier = Modifier.height(40.dp))

                            Row {
                                Spacer(modifier = Modifier.width(20.dp))
                                Surface (modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        showSchoolListBottomSheet = true
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
                                                Spacer(modifier = Modifier.height(10.dp))
                                                Text(
                                                    text = selectedSchool?.schoolName
                                                        ?: stringResource(R.string.please_select_school),
                                                    color = if (selectedSchool == null) Color.Gray else Color.Black,
                                                    style = MaterialTheme.typography.titleMedium
                                                )
                                                Spacer(modifier = Modifier.height(10.dp))
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



                            Spacer(modifier = Modifier.height(50.dp))
                            Row {
                                Spacer(modifier = Modifier.width(30.dp))
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(if (selectedSchool != null) Color(0xFF2D859D) else Color.Gray)
                                        .clickable(enabled = selectedSchool != null) {
                                            selectedSchool?.let { onConfirmClick(it) }
                                        },

                                    color = Color.Transparent
                                ) {
                                    Text(
                                        text = stringResource(R.string.next_step),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.wrapContentHeight(),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(30.dp))
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                }

                if (showSchoolListBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showSchoolListBottomSheet = false
                        },
                        sheetState = schoolSheetState,
                        containerColor = Color.White
                    ) {
                        schoolListBottomSheetView(aSchoolList = schoolList) { school ->
                            selectedSchool = school
                            showSchoolListBottomSheet = false
                        }
                    }
                }
            }
        }
        Surface (modifier = Modifier
            .weight(3f)
            .navigationBarsPadding()
            .fillMaxWidth(),
            color = Color(0xFFF4F4F4)){
            Column (modifier = Modifier
                .fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally){
                Surface (modifier = Modifier,
                    color = Color.Unspecified){
                    Image(painter = painterResource(id = R.drawable.dae_logo_logo_3_1), contentDescription = "")
                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Copyright © "+ getCurrentYear() +" DAE instrument CO., Ltd. All rights reserved",
                    textAlign = TextAlign.Center,
                    modifier = Modifier,
                    color = Color(0xFF000000),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(15.dp))
            }
        }


    }
}

@Composable
private fun schoolListBottomSheetView(aSchoolList: List<SchoolHostModel>, onItemClick: (SchoolHostModel) -> Unit = {}) {
    LazyColumn() {
        items(aSchoolList) { item ->
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
                            text = item.schoolName ?: "",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.width(20.dp))
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

private fun getCurrentYear(): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy") // 設定日期格式
    val today = LocalDate.now() // 取得今天的日期
    // 格式化輸出
    val yearStr = today.format(formatter)
    return yearStr
}

@Composable
private fun parseDialogMsg(aMsg: String):(String){
    var msg: String = ""
    if (aMsg == "EmailNotEntered") {
        msg = stringResource(id = R.string.dormitory_usage_history)
    }else {
        msg = aMsg
    }
    return msg
}


@Preview(showBackground = true)
@Composable
private fun selectSchoolPreview() {
    val navController = TestNavHostController(LocalContext.current)
    selectSchoolContent(navController,emptyList())
}