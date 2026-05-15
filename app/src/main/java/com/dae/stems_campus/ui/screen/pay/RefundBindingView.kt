package com.dae.stems_campus.ui.screen.pay

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.testing.TestNavHostController
import com.dae.stems_campus.R
import com.dae.stems_campus.ui.components.textTNoButtonAlert
import com.dae.stems_campus.viewmodel.TopUpViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.delay

@Composable
fun refundBindingScreen(navController: NavHostController, topUpViewModel: TopUpViewModel = hiltViewModel(), refundID: Int, onShowTabBarChange: (Boolean) -> Unit) {

    val resScanDepositSuccessFlag by topUpViewModel.resScanDepositSuccessFlag.collectAsState()
    val showScanDepositFailDialogFlag by topUpViewModel.showScanDepositFailDialogFlag.collectAsState()
    val showScanDepositFailMsg by topUpViewModel.showScanDepositFailMsg.collectAsState()

    var depositCode by remember { mutableStateOf("") }

    refundBindingContent(navController = navController,
        onShowTabBarChange = {},
        onScanCodeHandled = { value ->
            depositCode = value
            topUpViewModel.scanDepositAction(value)})

    if (resScanDepositSuccessFlag) {
        topUpViewModel.resetScanDepositSuccessFlag(false)
        navController.navigate("RefundDeposit/${depositCode}/${refundID}")
    }

    if (showScanDepositFailDialogFlag) {
        textTNoButtonAlert(
            onDismissRequest = {},
            dialogTitle = parseDialogMsg(showScanDepositFailMsg ?: "")
        )
        // 在 Dialog 顯示後啟動計時器
        LaunchedEffect(Unit) {
            delay(1500) // 延遲 1.5 秒
            topUpViewModel.resetScanDepositFailDialogFlag(false)
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun refundBindingContent(
    navController: NavHostController,
    onShowTabBarChange: (Boolean) -> Unit,
    onScanCodeHandled:(String) -> Unit) {

    var showInputCode by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
        Scaffold(
            containerColor = Color.Unspecified,
            contentWindowInsets = WindowInsets(0),
            topBar = {
                TopTitleBar(
                    navTitle = stringResource(R.string.easy_card_refund),
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
                    Column (){
                        Spacer(modifier = Modifier.height(30.dp))

                        if (showInputCode) {
                            Row {
                                Spacer(modifier = Modifier.width(20.dp))
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(RoundedCornerShape(20.dp))
                                        .border(1.dp, Color(0xFF2D859D), RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .clickable {
                                            showInputCode = false
                                        },

                                    color = Color.Transparent
                                ) {
                                    Text(
                                        text = stringResource(R.string.scan_qr_code),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.wrapContentHeight(),
                                        color = Color(0xFF2D859D)
                                    )
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFF2D859D))
                                        .clickable {
                                            showInputCode = true
                                        },

                                    color = Color.Transparent
                                ) {
                                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){

                                        Text(
                                            text = stringResource(R.string.enter_number),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.wrapContentHeight(),
                                            color = Color.White
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                            inputCodeView(onScanCodeHandled = { value -> onScanCodeHandled(value)})
                        }else{
                            Row {
                                Spacer(modifier = Modifier.width(20.dp))
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color(0xFF2D859D))
                                        .clickable {
                                            showInputCode = false
                                        },

                                    color = Color.Transparent
                                ) {
                                    Text(
                                        text = stringResource(R.string.scan_qr_code),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.wrapContentHeight(),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .align(Alignment.CenterVertically)
                                        .clip(RoundedCornerShape(20.dp))
                                        .border(1.dp, Color(0xFF2D859D), RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .clickable {
                                            showInputCode = true
                                        },

                                    color = Color.Transparent
                                ) {
                                    Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center){

                                        Text(
                                            text = stringResource(R.string.enter_number),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.wrapContentHeight(),
                                            color = Color(0xFF2D859D)
                                        )
                                    }

                                }
                                Spacer(modifier = Modifier.width(20.dp))
                            }
                            qrScanView(onScanCodeHandled = { value -> onScanCodeHandled(value)})
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
private fun qrScanView(onScanCodeHandled:(String) -> Unit) {
    Spacer(modifier = Modifier.height(40.dp))

    //QR掃描
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
                .width(250.dp)
                .height(250.dp)
                .clip(RoundedCornerShape(1.dp))
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
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
private fun inputCodeView(onScanCodeHandled:(String) -> Unit) {
    var inputText by remember { mutableStateOf("") }
    Spacer(modifier = Modifier.height(80.dp))
    Row {
        Spacer(modifier = Modifier.width(30.dp))
        TextField(
            value = inputText,  // 使用狀態作為輸入值
            onValueChange = {
                inputText = it
            },
            modifier = Modifier
                .weight(1f)
                .padding(0.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black
            ),
            placeholder = {
                Text(stringResource(id = R.string.enter_top_up_machine_number),
                    color = Color(0xFF979797)
                )
            },
            singleLine = true,
        )
        Spacer(modifier = Modifier.width(30.dp))
    }

    Row {
        Spacer(modifier = Modifier.width(30.dp))
        Spacer(modifier = Modifier
            .height(1.dp)
            .weight(1f)
            .background(color = Color(0xFF2D859D)))
        Spacer(modifier = Modifier.width(30.dp))
    }
    Spacer(modifier = Modifier.height(80.dp))
    Row {
        Spacer(modifier = Modifier.width(30.dp))
        Surface(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF2D859D)
                )
                .clickable {
                    onScanCodeHandled(inputText)
                },

            color = Color.Transparent
        ) {
            Text(
                text = stringResource(R.string.next_step),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.wrapContentHeight(),
                color = Color.White
            )
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
private fun TopTitleBar(navTitle: String, navController: NavHostController, onShowTabBarChange: (Boolean) -> Unit) {
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
private fun refundBindingPreview() {

// 創建一個模擬的 NavController
    val navController = TestNavHostController(LocalContext.current)
    refundBindingContent (navController,{},{})
}
