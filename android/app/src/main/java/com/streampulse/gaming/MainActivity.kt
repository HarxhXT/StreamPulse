package com.streampulse.gaming

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF10131B)
private val Panel = Color(0xFF1B2030)
private val Accent = Color(0xFF7657FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                StreamPulseDashboard {
                    Toast.makeText(
                        this,
                        "Streaming pipeline is under development",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun StreamPulseDashboard(onPrepare: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                "StreamPulse",
                color = Color.White,
                fontSize = 29.sp
            )

            Text(
                "GAMING CREATOR STUDIO",
                color = Accent,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(28.dp))

            Text(
                "Your streaming studio",
                color = Color.White,
                fontSize = 22.sp
            )

            Spacer(Modifier.height(18.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Panel
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text("STREAM STATUS", color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "●  Not Live",
                        color = Color(0xFFFF6262),
                        fontSize = 22.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Your next gaming stream starts here.",
                        color = Color.LightGray
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Text("PLATFORMS", color = Color.Gray)

            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PlatformCard("YouTube", Modifier.weight(1f))
                PlatformCard("Kick", Modifier.weight(1f))
            }

            Spacer(Modifier.height(22.dp))

            Text("STREAM SETTINGS", color = Color.Gray)

            Spacer(Modifier.height(10.dp))

            SettingCard("Video Quality", "Auto")
            SettingCard("Orientation", "Landscape")
            SettingCard("Microphone", "Not configured")
            SettingCard("Streaming Scenes", "Starting Soon · Gameplay · BRB")

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onPrepare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("PREPARE STREAM", color = Color.White)
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "StreamPulse • Gaming Streaming Studio",
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun PlatformCard(name: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(name, color = Color.White, fontSize = 18.sp)
            Text("Not connected", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun SettingCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Panel),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = Color.White)
            Text(value, color = Accent)
        }
    }
}
