package com.streampulse.gaming

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF090A12)
private val CardBg = Color(0xFF151625)
private val Purple = Color(0xFF9065FF)
private val Muted = Color(0xFF9292A8)
private val White = Color(0xFFF7F5FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                StreamPulseApp {
                    Toast.makeText(
                        this,
                        "Screen capture setup will be added next",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun StreamPulseApp(onContinue: () -> Unit) {
    var currentPage by remember { mutableStateOf(false) }

    if (currentPage) {
        StreamSetupScreen(
            onBack = { currentPage = false },
            onContinue = onContinue
        )
    } else {
        PremiumDashboard(
            onPrepare = { currentPage = true }
        )
    }
}

@Composable
fun PremiumDashboard(onPrepare: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).background(
                    Brush.linearGradient(listOf(Purple, Color(0xFF4930A5))),
                    RoundedCornerShape(15.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                Text("S", color = White, fontSize = 27.sp,
                    fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("StreamPulse", color = White, fontSize = 23.sp,
                    fontWeight = FontWeight.Bold)
                Text("GAMING STUDIO", color = Purple,
                    fontSize = 10.sp, letterSpacing = 2.sp)
            }
            Text("FREE", color = Muted, fontSize = 11.sp)
        }

        Spacer(Modifier.height(30.dp))
        Text("YOUR CONTROL CENTER", color = Muted,
            fontSize = 11.sp, letterSpacing = 2.sp)
        Text("Ready to go live?", color = White, fontSize = 29.sp,
            fontWeight = FontWeight.Bold)
        Text("Everything you need for your next stream.",
            color = Muted, fontSize = 13.sp)

        Spacer(Modifier.height(22.dp))
        Box(
            Modifier.fillMaxWidth().background(
                Brush.linearGradient(
                    listOf(Color(0xFF35216F), Color(0xFF19152F))
                ), RoundedCornerShape(24.dp)
            ).padding(21.dp)
        ) {
            Column {
                Text("● OFFLINE", color = Color(0xFFFF7777),
                    fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(17.dp))
                Text("Your next stream.", color = White, fontSize = 25.sp,
                    fontWeight = FontWeight.Bold)
                Text("Your gaming audience is waiting.",
                    color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(20.dp))
                Text("LIVE VIEWERS       STREAM TIME",
                    color = Muted, fontSize = 10.sp)
                Text("--                         --:--",
                    color = White, fontSize = 17.sp)
            }
        }

        Spacer(Modifier.height(25.dp))
        Text("PLATFORMS", color = White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PlatformTile("▶", "YouTube", "Not connected",
                Color.Red, Modifier.weight(1f))
            PlatformTile("K", "Kick", "Not connected",
                Color(0xFF54D68A), Modifier.weight(1f))
        }

        Spacer(Modifier.height(25.dp))
        Text("STREAM CONFIGURATION", color = White,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        SettingTile("◈", "Video Quality", "Auto")
        SettingTile("▣", "Resolution", "720p target")
        SettingTile("◷", "Frame Rate", "30 FPS")
        SettingTile("▤", "Orientation", "Landscape")
        SettingTile("♫", "Microphone", "Not configured")
        SettingTile("✦", "Scenes", "Starting Soon · Gameplay · BRB")

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = onPrepare,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(17.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple
            )
        ) {
            Text("PREPARE STREAM  →", color = White,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StreamSetupScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var title by remember { mutableStateOf("My Gaming Stream") }
    var platform by remember { mutableStateOf("YouTube") }
    var quality by remember { mutableStateOf("Auto") }
    var resolution by remember { mutableStateOf("720p") }
    var fps by remember { mutableStateOf("30") }
    var orientation by remember { mutableStateOf("Landscape") }
    var microphone by remember { mutableStateOf(true) }
    var scene by remember { mutableStateOf("Starting Soon") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            "‹  BACK",
            color = Purple,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onBack() }
        )

        Spacer(Modifier.height(24.dp))

        Text("STREAM SETUP", color = Purple,
            fontSize = 11.sp, letterSpacing = 2.sp)
        Text("Configure your stream",
            color = White, fontSize = 28.sp,
            fontWeight = FontWeight.Bold)
        Text("Set up your broadcast before going live.",
            color = Muted, fontSize = 13.sp)

        Spacer(Modifier.height(25.dp))

        SetupSection("STREAM TITLE")
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            label = { Text("Enter stream title") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                focusedBorderColor = Purple,
                unfocusedBorderColor = CardBg,
                focusedLabelColor = Purple,
                unfocusedLabelColor = Muted,
                cursorColor = Purple
            )
        )

        Spacer(Modifier.height(22.dp))
        SetupSection("STREAM PLATFORM")
        ChoiceRow(listOf("YouTube", "Kick", "Both"),
            platform) { platform = it }

        Spacer(Modifier.height(22.dp))
        SetupSection("VIDEO QUALITY")
        ChoiceRow(listOf("Auto", "Manual"), quality) { quality = it }

        Spacer(Modifier.height(22.dp))
        SetupSection("RESOLUTION")
        ChoiceRow(listOf("480p", "720p", "1080p"),
            resolution) { resolution = it }

        Spacer(Modifier.height(22.dp))
        SetupSection("FRAME RATE")
        ChoiceRow(listOf("30", "60"), fps) { fps = it }
        Text("FPS", color = Muted, fontSize = 11.sp)

        Spacer(Modifier.height(22.dp))
        SetupSection("ORIENTATION")
        ChoiceRow(listOf("Landscape", "Portrait"),
            orientation) { orientation = it }

        Spacer(Modifier.height(22.dp))
        SetupSection("MICROPHONE")
        Row(
            Modifier.fillMaxWidth().background(
                CardBg, RoundedCornerShape(14.dp)
            ).padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Microphone audio", color = White,
                modifier = Modifier.weight(1f))
            Switch(
                checked = microphone,
                onCheckedChange = { microphone = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = Purple
                )
            )
        }

        Spacer(Modifier.height(22.dp))
        SetupSection("STARTING SCENE")
        ChoiceRow(
            listOf("Starting Soon", "Gameplay", "BRB", "Ending"),
            scene
        ) { scene = it }

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple
            )
        ) {
            Text("CONTINUE TO SCREEN CAPTURE  →",
                color = White, fontWeight = FontWeight.Bold,
                fontSize = 12.sp)
        }

        Spacer(Modifier.height(20.dp))
        Text(
            "Settings are currently local UI selections. Platform login and actual broadcast are not connected yet.",
            color = Muted, fontSize = 11.sp
        )
    }
}

@Composable
fun SetupSection(title: String) {
    Text(title, color = Muted, fontSize = 11.sp,
        letterSpacing = 1.4.sp,
        fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(10.dp))
}

@Composable
fun ChoiceRow(
    choices: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        choices.forEach { option ->
            val active = selected == option
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (active) Color(0xFF38265F) else CardBg,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(option) }
                    .padding(vertical = 14.dp, horizontal = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    option,
                    color = if (active) White else Muted,
                    fontSize = 11.sp,
                    fontWeight = if (active)
                        FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun PlatformTile(
    symbol: String,
    name: String,
    status: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.background(CardBg, RoundedCornerShape(19.dp))
            .padding(16.dp)
    ) {
        Text(symbol, color = accent, fontSize = 23.sp)
        Spacer(Modifier.height(10.dp))
        Text(name, color = White, fontSize = 17.sp,
            fontWeight = FontWeight.Bold)
        Text(status, color = Muted, fontSize = 11.sp)
    }
}

@Composable
fun SettingTile(icon: String, title: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 9.dp)
            .background(CardBg, RoundedCornerShape(15.dp))
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, color = Purple, fontSize = 19.sp)
        Spacer(Modifier.width(13.dp))
        Text(title, color = White, fontSize = 13.sp,
            modifier = Modifier.weight(1f))
        Text(value, color = Purple, fontSize = 12.sp)
    }
}
