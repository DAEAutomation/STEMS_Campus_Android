package com.dae.stems_campus.ui.screen.home

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ProfileModel
import com.dae.stems_campus.ui.components.LoadingView
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.utils.calculateDuration
import com.dae.stems_campus.utils.toAmountString
import com.dae.stems_campus.viewmodel.ProfileViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

@Composable
fun HomeScreen(mainNavController: NavController, profileViewModel: ProfileViewModel = hiltViewModel()) {
    val homeNavController = rememberNavController()

    NavHost(navController = homeNavController, startDestination = "Home") {
        composable("Home") {
            homeMainLoad(mainNavController = mainNavController,homeNavController, profileViewModel, onShowTabBarChange = {})
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun homeMainLoad(mainNavController: NavController, navHostController: NavHostController, profileViewModel: ProfileViewModel, onShowTabBarChange: (Boolean) -> Unit) {

    val profileInfo by profileViewModel.profileInfo.collectAsState()
    val showLoadingView by profileViewModel.showLoadingView.collectAsState()
    val resGetProfileInfoSuccessFlag by profileViewModel.resGetProfileInfoSuccessFlag.collectAsState()
    val showGetProfileInfoFailDialogFlag by profileViewModel.showGetProfileInfoFailDialogFlag.collectAsState()
    val showGetProfileInfoFailMsg by profileViewModel.showGetProfileInfoFailMsg.collectAsState()

    LaunchedEffect(Unit) {
        while (true) {
            profileViewModel.getProfileInfoAction()
            delay(30_000L)  // 30 秒
        }
    }
    homeContent(mainNavController = mainNavController,
        navHostController = navHostController,
        profileInfo = profileInfo,
        showLoadingView = showLoadingView,
        resGetProfileInfoSuccessFlag = resGetProfileInfoSuccessFlag,
        showGetProfileInfoFailDialogFlag = showGetProfileInfoFailDialogFlag,
        showGetProfileInfoFailMsg = showGetProfileInfoFailMsg,
        onGetProfileInfoFailDismissed = { profileViewModel.resetShowGetProfileInfoFailDialogFlag(false)})
}

@Composable
private fun homeContent(mainNavController: NavController,
                        navHostController: NavHostController,
                        profileInfo: ProfileModel.ProfileData? = null,
                        showLoadingView: Boolean = false,
                        resGetProfileInfoSuccessFlag: Boolean = false,
                        showGetProfileInfoFailDialogFlag: Boolean = false,
                        showGetProfileInfoFailMsg: String? = null,
                        onGetProfileInfoFailDismissed: () -> Unit = {}) {

    var showScanBottomSheet by remember { mutableStateOf(false) }

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
                        Text("${stringResource(R.string.home)}", color = Color.Black,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold)

                        Surface (modifier = Modifier.weight(1f)){  }
                        Surface (modifier = Modifier, color = Color.Unspecified){
                            Surface (modifier = Modifier
                                , color = Color.Unspecified)
                            {
                                Image(painter = painterResource(id = R.drawable.qrcode), contentDescription = "")
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Surface (modifier = Modifier, color = Color.Unspecified){
                            Surface (modifier = Modifier
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

            Surface (modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),  color = Color.Unspecified){
                Column {
                    if (profileInfo?.role.equals("staff")) {
                        infoViewByTeacher(profileInfo?.activeSession?.hasActive ?: false, profileInfo)
                    }else if (profileInfo?.role.equals("student")){
                        infoViewByStudent(profileInfo?.activeSession?.hasActive ?: false, profileInfo)
                    }
                }
            }
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
                    onGetProfileInfoFailDismissed()
                }
            }
        }
    }
}

@Composable
private fun infoViewByTeacher(hasDevice: Boolean, aData: ProfileModel.ProfileData?) {
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
                    painter = painterResource(id = R.drawable.stopcircle),
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
    Spacer(modifier = Modifier.height(20.dp))
    if (hasDevice) {
        Column {
            aData?.activeSession?.sessions?.forEach { item ->
                when (item.space?.spaceType) {
                    "classroom" -> {
                        classroomView(aData = item)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                    "dormitory" -> {
                        dormitoryView(aData = item)
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
                        .clickable { },
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
private fun infoViewByStudent(hasDevice: Boolean, aData: ProfileModel.ProfileData?) {
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

    Spacer(modifier = Modifier.height(20.dp))
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
                    .clickable { },
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

@Composable
private fun classroomView(aData: ProfileModel.UsingDeviceData?) {
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
                .clickable { },
            color = Color(0xFFD08024),
            shape = RoundedCornerShape(9.dp)
        ) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Surface (modifier = Modifier
                    .weight(0.9f)
                    .height(170.dp), color = Color(0xFFD08024)){
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
private fun dormitoryView(aData: ProfileModel.UsingDeviceData?) {
    Row {
        Spacer(modifier = Modifier.width(30.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { },
            color = Color(0xFF2D859D),
            shape = RoundedCornerShape(9.dp)
        ) {
            Row (verticalAlignment = Alignment.CenterVertically){
                Surface (modifier = Modifier
                    .weight(0.9f)
                    .height(170.dp), color = Color(0xFF2D859D)){
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
                            Text("${aData?.billing?.general?.totalAmount?.toAmountString()}", color = Color.White,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)
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
fun CameraPreview(modifier: Modifier = Modifier, onCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var isScanning by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = androidx.camera.core.Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val barcodeScanner = BarcodeScanning.getClient()
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
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                analysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
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
                        Log.d("QRCode", "掃描成功：$it")
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
private fun scanQRBottomSheetView() {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(stringResource(R.string.scan_location_qr_code_to_start), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(35.dp))

                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
            ) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onCodeScanned = {

                    }
                )

                // 四角直角線條
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cornerLength = 40.dp.toPx()   // 線條長度
                    val strokeWidth = 4.dp.toPx()      // 線條粗細
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
private fun classroomPowerSupplyByTeacherBottomSheetView() {
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
                    .height(190.dp),
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 35.dp))
                    Text(stringResource(R.string.classroom), color = Color(0xFFDF8927),style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFFDF8927)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("A101", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("XXXX", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                    ,

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
private fun dormitoryPowerSupplyByTeacherBottomSheetView() {
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
                    .height(230.dp),
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 30.dp))
                    Text(stringResource(R.string.dormitory), color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFF2D859D)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("A101", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("XXXX", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(15.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${stringResource(R.string.rate)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("3.5", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                ,

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
private fun classroomPowerSupplyByStudentBottomSheetView() {
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
                    .height(230.dp),
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 30.dp))
                    Text(stringResource(R.string.classroom), color = Color(0xFFD08024), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFFD08024)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("A101", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("XXXX", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(15.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${stringResource(R.string.rate)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("3.5", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                Switch(
                    checked = true,
                    onCheckedChange = { checked ->
                        if (checked) {

                        } else {

                        }
                    }
                )
            }
            Spacer(modifier = Modifier.width(40.dp))
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
                ,

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
private fun dormitoryPowerSupplyByStudentBottomSheetView() {
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
                    .height(230.dp),
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 30.dp))
                    Text(stringResource(R.string.dormitory), color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFF2D859D)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("A101", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("XXXX", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(15.dp))
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("${stringResource(R.string.rate)}：", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("3.5", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                ,

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
private fun inputPasswordAndLinkBottomSheetView(inputText: String) {
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
                value = inputText,
                onValueChange = { it },
                textStyle = TextStyle( color = Color.White),
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
                                color = if (true) Color(0xFFE54343) else Color(0xFF999999),
                                shape = RoundedCornerShape(10.dp)
                            ).padding(15.dp)
                    ) {
//                        if (inputPasswordText.isEmpty()) {
//                            Text(
//                                text = stringResource(R.string.enter_password),
//                                color = Color(0xFFAAAAAA)
//                            )
//                        }else{
////                            settingViewModel.resetShowPwAuthenticationInputFailFlag(false)
//                        }
                        innerTextField()
                    }
                },
                visualTransformation = PasswordVisualTransformation()
            )
        }
        //處理錯誤輸入顯示
        if (true) {
            Spacer(modifier = Modifier.height( 10.dp))
            Row {
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = stringResource(R.string.go_to_enable_biometric),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
                    color = Color.Black,
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
                Spacer(Modifier.weight(1f))
                Text("xxxxx", color = Color(0xFFE54343))
                Spacer(modifier = Modifier.width(20.dp))
            }
        }else {
            Spacer(modifier = Modifier.height( 10.dp))
            Row {
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = stringResource(R.string.go_to_enable_biometric),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.wrapContentHeight(),
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
//                        inputPasswordText = ""
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
//                        showChargingPowerOffBottomSheet = false
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
private fun powerEnabledBottomSheetView() {
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
                ,

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
private fun inUseBottomSheetView() {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(30.dp))

                    Text("該空間已被使用", color = Color.Black, style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(35.dp))
                    Text("如需用電，請至 ${"xxxx"} \n" + "按下「Enter按鈕」", color = Color.Black, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center,)
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

    homeContent(navController,navController)

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
        scanQRBottomSheetView()
    }

}