package com.foxfocus.app.ui.screens.pairing

import android.Manifest
import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.foxfocus.app.ui.components.PrimaryButton
import com.foxfocus.app.ui.components.SecondaryButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QrScannerScreen(onCodeScanned: (String) -> Unit, onCancel: () -> Unit) {
  val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

  Box(Modifier.fillMaxSize().background(Color.Black)) {
    when {
      cameraPermissionState.status.isGranted -> {
        CameraPreview(onCodeScanned = onCodeScanned)
        Column(
          Modifier.fillMaxSize().padding(20.dp),
          verticalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Box(
            Modifier
              .padding(top = 80.dp)
              .size(240.dp)
              .background(Color.Transparent)
              .then(Modifier.border(2.dp, Color.White, RoundedCornerShape(20.dp)))
          )
          Text(
            "وجّه الكاميرا نحو رمز QR الظاهر على شاشة الكمبيوتر",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
          )
        }
      }

      cameraPermissionState.status.shouldShowRationale -> {
        PermissionExplainer(onGrant = { cameraPermissionState.launchPermissionRequest() }, onCancel = onCancel)
      }

      else -> {
        LaunchedEffect(Unit) { cameraPermissionState.launchPermissionRequest() }
        PermissionExplainer(onGrant = { cameraPermissionState.launchPermissionRequest() }, onCancel = onCancel)
      }
    }
  }
}

@Composable
private fun PermissionExplainer(onGrant: () -> Unit, onCancel: () -> Unit) {
  Column(
    Modifier.fillMaxSize().padding(24.dp),
    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      "يحتاج فوكس فوكس إذن الكاميرا لمسح رمز الربط بالكمبيوتر فقط.",
      style = MaterialTheme.typography.bodyMedium,
      color = Color.White,
    )
    androidx.compose.foundation.layout.Spacer(Modifier.padding(8.dp))
    PrimaryButton(text = "السماح باستخدام الكاميرا", onClick = onGrant)
    SecondaryButton(text = "إلغاء", onClick = onCancel)
  }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun CameraPreview(onCodeScanned: (String) -> Unit) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  var handled by remember { mutableStateOf(false) }

  AndroidView(
    modifier = Modifier.fillMaxSize(),
    factory = { ctx ->
      val previewView = PreviewView(ctx)
      val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
      val executor = Executors.newSingleThreadExecutor()
      val scanner = BarcodeScanning.getClient()

      cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().apply {
          setSurfaceProvider(previewView.surfaceProvider)
        }

        val analysis = ImageAnalysis.Builder()
          .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
          .build()

        analysis.setAnalyzer(executor) { imageProxy ->
          val mediaImage = imageProxy.image
          if (mediaImage != null && !handled) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
              .addOnSuccessListener { barcodes ->
                val value = barcodes.firstOrNull { it.valueType == Barcode.TYPE_TEXT || it.rawValue != null }?.rawValue
                if (value != null && !handled) {
                  handled = true
                  onCodeScanned(value)
                }
              }
              .addOnCompleteListener { imageProxy.close() }
          } else {
            imageProxy.close()
          }
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
          lifecycleOwner,
          CameraSelector.DEFAULT_BACK_CAMERA,
          preview,
          analysis,
        )
      }, ContextCompat.getMainExecutor(ctx))

      previewView
    },
  )
}
