
package com.streampulse.gaming

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
                PremiumDashboard {
                    Toast.makeText(
                        this,
                        "Streaming engine is under development",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}

@Composable
fun PremiumDashboard(onPrepare: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Bg
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp, bottom = 28.dp)
        ) {

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Purple, Color(0xFF4930A5))
                            ),
                            RoundedCornerShape(15.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = White, fontSize = 27.sp,
                        fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text("StreamPulse", color = White,
                        fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text("GAMING STUDIO", color = Purple,
                        fontSize = 10.sp, letterSpacing = 2.sp)
                }

                Box(
                    modifier = Modifier
                        .background(CardBg, CircleShape)
                        .padding(horizontal = 13.dp, vertical = 9.dp)
                ) {
                    Text("FREE", color = Muted, fontSize = 11.sp,
                        fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(30.dp))

            Text("YOUR CONTROL CENTER",
                color = Muted, fontSize = 11.sp,
                letterSpacing = 2.sp)

            Spacer(Modifier.height(7.dp))

            Text("Ready to go live?",
                color = White, fontSize = 29.sp,
                fontWeight = FontWeight.Bold)

            Text("Everything you need for your next stream.",
                color = Muted, fontSize = 13.sp)

            Spacer(Modifier.height(22.dp))

            // Stream status hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF35216F),
                                Color(0xFF19152F),
                                Color(0xFF171827)
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(21.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("STREAM STATUS",
                            color = Color(0xFFC6B6FF),
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp)

                        Spacer(Modifier.weight(1f))

                        Box(
                            Modifier
                                .background(
                                    Color(0x33FF6666),
                                    CircleShape
                                )
                                .padding(horizontal = 11.dp, vertical = 6.dp)
                        ) {
                            Text("● OFFLINE",
                                color = Color(0xFFFF7777),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text("Your next stream.",
                        color = White, fontSize = 25.sp,
                        fontWeight = FontWeight.Bold)

                    Text("Your gaming audience is waiting.",
                        color = Color(0xFFB8B1CE), fontSize = 13.sp)

                    Spacer(Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MiniInfo("LIVE VIEWERS", "--")
                        MiniInfo("STREAM TIME", "--:--")
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            SectionTitle("PLATFORMS", "Connect your channels")

            Spacer(Modifier.height(13.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PlatformTile(
                    "▶",
                    "YouTube",
                    "Not connected",
                    Color(0xFFFF5555),
                    Modifier.weight(1f)
                )
                PlatformTile(
                    "K",
                    "Kick",
                    "Not connected",
                    Color(0xFF54D68A),
                    Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(28.dp))

            SectionTitle("STREAM CONFIGURATION", "Your broadcast setup")

            Spacer(Modifier.height(13.dp))

            SettingTile("◈", "Video Quality", "Auto")
            SettingTile("▣", "Resolution", "720p target")
            SettingTile("◷", "Frame Rate", "30 FPS")
            SettingTile("▤", "Orientation", "Landscape")
            SettingTile("♫", "Microphone", "Not configured")
            SettingTile("✦", "Scenes", "Starting Soon · Gameplay · BRB")

            Spacer(Modifier.height(27.dp))

            // Main CTA
            Button(
                onClick = onPrepare,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple
                )
            ) {
                Text("PREPARE STREAM  →",
                    color = White, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp)
            }

            Spacer(Modifier.height(13.dp))

            Text(
                "STREAM SMART. PLAY HARD.",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Muted,
                fontSize = 10.sp,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun MiniInfo(label: String, value: String) {
    Column(
        modifier = Modifier
            .background(
                Color(0x33202040),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 15.dp, vertical = 11.dp)
    ) {
        Text(label, color = Muted, fontSize = 9.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, color = White, fontSize = 16.sp,
            fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Column {
        Text(title, color = White, fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp)
        Spacer(Modifier.height(3.dp))
        Text(subtitle, color = Muted, fontSize = 12.sp)
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
        modifier = modifier
            .background(CardBg, RoundedCornerShape(19.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    accent.copy(alpha = 0.15f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(symbol, color = accent, fontSize = 21.sp,
                fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(13.dp))

        Text(name, color = White, fontSize = 17.sp,
            fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(5.dp))

        Text(status, color = Muted, fontSize = 11.sp)
    }
}

@Composable
fun SettingTile(icon: String, title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 9.dp)
            .background(CardBg, RoundedCornerShape(15.dp))
            .padding(horizontal = 15.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(37.dp)
                .background(
                    Color(0xFF28213F),
                    RoundedCornerShape(11.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = Purple, fontSize = 18.sp)
        }

        Spacer(Modifier.width(13.dp))

        Text(title, color = White, fontSize = 13.sp,
            modifier = Modifier.weight(1f))

        Text(value, color = Purple, fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold)
    }
}
