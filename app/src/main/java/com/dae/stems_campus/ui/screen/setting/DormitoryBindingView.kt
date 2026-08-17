package com.dae.stems_campus.ui.screen.setting

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.data.model.ScanModel
import com.dae.stems_campus.data.model.SettingModel
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.ui.theme.STEMS_CampusTheme
import com.dae.stems_campus.viewmodel.SettingViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay

@Composable
fun dormitoryBindingScreen(navController: NavHostController,settingViewModel: SettingViewModel) {

//    val resScanDepositSuccessFlag by topUpViewModel.resScanDepositSuccessFlag.collectAsState()
//    val showScanDepositFailDialogFlag by topUpViewModel.showScanDepositFailDialogFlag.collectAsState()
//    val showScanDepositFailMsg by topUpViewModel.showScanDepositFailMsg.collectAsState()
//
//    var depositCode by remember { mutableStateOf("") }
//
//    refundBindingContent(navController = navController,
//        onShowTabBarChange = {},
//        onScanCodeHandled = { value ->
//            depositCode = value
//            topUpViewModel.scanDepositAction(value)})
//


    val resDormScanInfoSuccessFlag by settingViewModel.resDormScanInfoSuccessFlag.collectAsState()
    val dormScanInfoData by settingViewModel.dormScanInfoData.collectAsState()
    val showDormScanInfoFailDialogFlag by settingViewModel.showDormScanInfoFailDialogFlag.collectAsState()

    val resDormBindingSuccessFlag by settingViewModel.resDormBindingSuccessFlag.collectAsState()
    val showDormBindingFailDialogFlag by settingViewModel.showDormBindingFailDialogFlag.collectAsState()
    val showDormBindingFailMsg by settingViewModel.showDormBindingFailMsg.collectAsState()


    //掃描開關，掃到就停、關掉 BottomSheet 或掃描失敗就恢復
    var isScanning by remember { mutableStateOf(true) }
    var scanQRCode by remember { mutableStateOf("") }
    dormitoryBindingContent(
        navController = navController,
        onScanCodeHandled = { value ->
            scanQRCode = value
            settingViewModel.dormScanInfoAction(value)
        },
        resScanSuccessFlag = resDormScanInfoSuccessFlag,
        dormScanInfoData = dormScanInfoData,
        isScanning = isScanning,
        onScanningChange = { value -> isScanning = value },
        onBindingHandled = {
            settingViewModel.dormBindingAction(scanQRCode)
        },
        onCancelHandled = {
            settingViewModel.resetResDormScanInfoSuccessFlag(false)
        }
    )

    //掃描失敗也要恢復掃描，否則相機會一直停住
    if (showDormScanInfoFailDialogFlag) {
        LaunchedEffect(Unit) {
            delay(1500)
            settingViewModel.resetShowDormScanInfoFailDialogFlag(false)
            isScanning = true
        }
    }

    if (resDormBindingSuccessFlag) {
        settingViewModel.resetResDormBindingSuccessFlag(false)
        navController.navigateUp()
    }

    if (showDormBindingFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showDormBindingFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            settingViewModel.resetShowDormBindingFailDialogFlag(false)
        }
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun dormitoryBindingContent(
    navController: NavHostController,
    onScanCodeHandled:(String) -> Unit,
    resScanSuccessFlag: Boolean = false,
    dormScanInfoData: SettingModel.DormScanInfoData? = null,
    isScanning: Boolean = true,
    onScanningChange: (Boolean) -> Unit,
    onBindingHandled: () -> Unit,
    onCancelHandled: () -> Unit) {

    var showInputCode by remember { mutableStateOf(false) }
    val scanDormitorySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.dormitory_binding),
                    navController = navController
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
                    Column (){
                        Spacer(modifier = Modifier.height(30.dp))

                        qrScanView(
                            isScanning = isScanning,
                            onScanCodeHandled = { value ->
                                //掃到先停掃描，避免同一張 QR 連續觸發 API
                                onScanningChange(false)
                                onScanCodeHandled(value)
                            })


                        //掃描宿舍
                        if (resScanSuccessFlag) {
                            ModalBottomSheet(
                                onDismissRequest = {
                                    onCancelHandled()
                                    onScanningChange(true)
                                },
                                sheetState = scanDormitorySheetState,
                                containerColor = Color.White
                            ) {
                                scanDormitoryBottomSheetView(
                                    aData = dormScanInfoData,
                                    onBindingHandled = {
                                        onCancelHandled()
                                        onBindingHandled()
                                    },
                                    onCancelHandled = {
                                        onCancelHandled()
                                        onScanningChange(true)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    //處理手機上的Back按鍵
    BackHandler {

    }

}

@Composable
private fun qrScanView(isScanning: Boolean = true, onScanCodeHandled:(String) -> Unit) {
    Spacer(modifier = Modifier.height(40.dp))

    //QR掃描
    Row (verticalAlignment = Alignment.CenterVertically){
        Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Spacer(modifier = Modifier.height(10.dp))

                Text(stringResource(R.string.dormitory_binding_description), color = Color.Black, style = MaterialTheme.typography.bodyLarge)
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
                .width(250.dp)
                .height(250.dp)
                .clip(RoundedCornerShape(1.dp))
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                isScanning = isScanning,
                onCodeScanned = {
                    onScanCodeHandled(it)
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

@Composable
private fun CameraPreview(modifier: Modifier = Modifier, isScanning: Boolean = true, onCodeScanned: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    //analyzer 只在 LaunchedEffect(Unit) 建立一次，直接捕捉 Boolean 會永遠是初始值，
    //所以包成 State 讓 analyzer 每一幀讀到最新的開關與 callback
    val scanningState = rememberUpdatedState(isScanning)
    val onCodeScannedState = rememberUpdatedState(onCodeScanned)

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
            if (scanningState.value) {
                processImageProxy(barcodeScanner, imageProxy) { result ->
                    // 掃到結果就丟出去，是否繼續掃描由外層的 isScanning 決定
                    if (scanningState.value) {
                        onCodeScannedState.value(result)
                    }
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
                //同一幀可能辨識到多組條碼，只取第一組避免重複觸發
                barcodes.firstNotNullOfOrNull { it.rawValue }?.let {
                    Log.d("DAE_Develop", "掃描成功：$it")
                    onCodeScanned(it)
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
private fun scanDormitoryBottomSheetView(aData: SettingModel.DormScanInfoData?, onBindingHandled: () -> Unit, onCancelHandled: () -> Unit) {
    Column {
        Row (verticalAlignment = Alignment.CenterVertically){
            Surface (modifier = Modifier.weight(1f), color = Color.Unspecified){
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height(20.dp))
                    Surface (modifier = Modifier, color = Color.Unspecified){
                        Image(painter = painterResource(id = R.drawable.checkcircle_g), contentDescription = "")
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                    Text( stringResource(R.string.dormitory) + stringResource(R.string.scan_success), color = Color.Black, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
                    .heightIn(min = 200.dp), // 字體放大時跟著 Column 內容往下長
                color = Color(0xFFE8E8E8),
                shape = RoundedCornerShape(9.dp)
            ) {
                Column (horizontalAlignment = Alignment.CenterHorizontally){
                    Spacer(modifier = Modifier.height( 30.dp))
                    Text(stringResource(R.string.dormitory), color = Color(0xFF2D859D), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.background(Color.Unspecified).border(1.dp, Color(0xFF2D859D)).padding(2.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.roomNumber}", color = Color.Black,style = MaterialTheme.typography.headlineMedium,fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(15.dp))
                    Text("${aData?.buildingName} ${aData?.floorName}", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(15.dp))
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
                        onBindingHandled()
                    },
                color = Color.Transparent
            ) {
                Text(
                    text = stringResource(R.string.dormitory_binding),
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
private fun TopTitleBar(navTitle: String, navController: NavHostController) {
    CenterAlignedTopAppBar(
        windowInsets = WindowInsets(0),
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
private fun parseDialogMsg(aMsg: String):(String){
    var msg: String = ""
    if (aMsg == "PleaseReLogin") {
        msg = stringResource(id = R.string.please_re_login)
    }else {
        msg = aMsg
    }
    return msg
}

// 預覽UI

@Preview(showBackground = true)
@Composable
private fun dormitoryBindingPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    dormitoryBindingContent (navController,{},false,null,true,{},{},{})
}

@Preview(showBackground = true)
@Composable
private fun BottomSheetViewPreview() {
    STEMS_CampusTheme {
        scanDormitoryBottomSheetView(null,{},{})
    }

}