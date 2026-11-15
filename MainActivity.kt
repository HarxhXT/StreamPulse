package com.streampulse.gaming

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.streampulse.gaming.capture.ScreenCaptureService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                StreamPulseHome(
                    onStartCapture = {
                        val manager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                        captureLauncher.launch(manager.createScreenCaptureIntent())
                    },
                    onStopCapture = {
                        startService(Intent(this, ScreenCaptureService::class.java).setAction(ScreenCaptureService.ACTION_STOP))
                    }
                )
            }
        }
    }

    private val captureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
                action = ScreenCaptureService.ACTION_START
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, result.data)
            }
            startForegroundService(serviceIntent)
        }
    }
}

@Composable
private fun StreamPulseHome(onStartCapture: () -> Unit, onStopCapture: () -> Unit) {
    var status by remember { mutableStateOf("Ready — no stream is running") }
    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("StreamPulse", style = MaterialTheme.typography.headlineLarge)
            Text("Gaming Creator Studio", style = MaterialTheme.typography.titleMedium)
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Stream status", style = MaterialTheme.typography.titleMedium)
                    Text(status)
                    Text("Milestone 1: capture permission and service foundation.")
                }
            }
            Button(
                onClick = {
                    status = "Waiting for Android screen-capture permission…"
                    onStartCapture()
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Test Screen Capture Permission") }
            OutlinedButton(onClick = {
                onStopCapture()
                status = "Capture service stop requested"
            }, modifier = Modifier.fillMaxWidth()) { Text("Stop Capture Service") }
            Text(
                "Important: this starter does not stream to YouTube or Kick yet. " +
                    "Encoding, scenes, audio, OAuth and RTMPS will be added in later milestones.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
