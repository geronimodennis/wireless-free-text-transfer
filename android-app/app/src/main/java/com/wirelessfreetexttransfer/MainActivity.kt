package com.wirelessfreetexttransfer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    QrTransferScreen()
                }
            }
        }
    }
}

@Composable
private fun QrTransferScreen() {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showScanner by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                showScanner = true
            } else {
                Toast.makeText(
                    context,
                    "Camera permission is required to scan QR.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Wireless Free Text Transfer",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Type text, generate QR, or scan QR to import text. Works offline.",
            style = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Plaintext") },
            placeholder = { Text("Enter text to transfer") },
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (text.isBlank()) {
                        Toast.makeText(context, "Please enter text first.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    qrBitmap = generateQrBitmap(text)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Write QR")
            }

            Button(
                onClick = {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        showScanner = true
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Read QR")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    text = ""
                    qrBitmap = null
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }

            Button(
                onClick = {
                    if (text.isBlank()) {
                        Toast.makeText(context, "No text to copy.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val clipboard = ContextCompat.getSystemService(
                        context,
                        android.content.ClipboardManager::class.java
                    )
                    val clip = android.content.ClipData.newPlainText("transfer-text", text)
                    clipboard?.setPrimaryClip(clip)
                    Toast.makeText(context, "Text copied.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Copy Text")
            }
        }

        if (qrBitmap != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Generated QR",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = qrBitmap!!.asImageBitmap(),
                    contentDescription = "Generated QR",
                    modifier = Modifier.size(300.dp)
                )
            }
        }
    }

    if (showScanner) {
        Dialog(
            onDismissRequest = { showScanner = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            QrScannerView(
                onDecoded = { value ->
                    text = value
                    qrBitmap = null
                    showScanner = false
                    Toast.makeText(context, "QR decoded successfully.", Toast.LENGTH_SHORT).show()
                },
                onClose = { showScanner = false }
            )
        }
    }
}

@Composable
private fun QrScannerView(
    onDecoded: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val decodedLock = remember { AtomicBoolean(false) }

    val scannerOptions = remember {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    }
    val scanner = remember { BarcodeScanning.getClient(scannerOptions) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val mainExecutor = ContextCompat.getMainExecutor(context)

        val cameraListener = Runnable {
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                val mediaImage = imageProxy.image
                if (mediaImage == null) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                val inputImage = InputImage.fromMediaImage(
                    mediaImage,
                    imageProxy.imageInfo.rotationDegrees
                )

                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        if (decodedLock.get()) {
                            return@addOnSuccessListener
                        }

                        val decoded = barcodes
                            .firstOrNull { !it.rawValue.isNullOrBlank() }
                            ?.rawValue

                        if (!decoded.isNullOrBlank() && decodedLock.compareAndSet(false, true)) {
                            onDecoded(decoded)
                        }
                    }
                    .addOnCompleteListener {
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
            } catch (_: Exception) {
                Toast.makeText(context, "Unable to start camera scanner.", Toast.LENGTH_SHORT).show()
                onClose()
            }
        }

        cameraProviderFuture.addListener(cameraListener, mainExecutor)

        onDispose {
            runCatching {
                cameraProviderFuture.get().unbindAll()
            }
            scanner.close()
            cameraExecutor.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = "Point camera at QR code",
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
                .background(Color(0x88000000))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Button(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Text("Close Scanner")
        }
    }
}

private fun generateQrBitmap(text: String, size: Int = 900): Bitmap? {
    return runCatching {
        val bitMatrix = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            val offset = y * size
            for (x in 0 until size) {
                pixels[offset + x] = if (bitMatrix[x, y]) {
                    android.graphics.Color.BLACK
                } else {
                    android.graphics.Color.WHITE
                }
            }
        }

        Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).apply {
            setPixels(pixels, 0, size, 0, 0, size, size)
        }
    }.getOrNull()
}
