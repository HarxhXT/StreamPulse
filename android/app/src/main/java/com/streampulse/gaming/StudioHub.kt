
package com.streampulse.gaming

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HubBg = Color(0xFF090912)
private val HubCard = Color(0xFF171725)
private val HubPurple = Color(0xFF9B5CFF)
private val HubText = Color(0xFFF4F0FF)
private val HubMuted = Color(0xFFAAA5BC)

private data class InstalledGame(
    val name: String,
    val packageName: String
)

@Composable
fun StudioHub(onBack: () -> Unit) {
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf("Games") }
    var selectedGame by remember { mutableStateOf("No game selected") }
    var selectedScene by remember { mutableStateOf("Gameplay") }

    var customScene by remember { mutableStateOf("") }
    val scenes = remember {
        mutableStateListOf(
            "Starting Soon",
            "Gameplay",
            "BRB",
            "Ending"
        )
    }

    var musicName by remember { mutableStateOf("No music imported") }
    var musicUri by remember { mutableStateOf<Uri?>(null) }

    var gameVolume by remember { mutableFloatStateOf(80f) }
    var micVolume by remember { mutableFloatStateOf(70f) }
    var musicVolume by remember { mutableFloatStateOf(25f) }

    val musicPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {
                // Some file providers do not support persistent permissions.
            }

            musicUri = uri

            val cursor = context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )

            musicName = cursor?.use {
                if (it.moveToFirst()) {
                    it.getString(0) ?: "Imported audio"
                } else {
                    "Imported audio"
                }
            } ?: "Imported audio"
        }
    }

    val installedGames = remember {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        context.packageManager
            .queryIntentActivities(intent, 0)
            .filter {
                it.activityInfo.packageName != context.packageName
            }
            .map {
                InstalledGame(
                    name = it.loadLabel(context.packageManager).toString(),
                    packageName = it.activityInfo.packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HubBg)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "‹",
                color = HubPurple,
                fontSize = 34.sp,
                modifier = Modifier.clickable { onBack() }
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    "CREATOR STUDIO",
                    color = HubText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "StreamPulse • Studio Hub",
                    color = HubMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            listOf("Games", "Scenes", "Music", "Mixer").forEach { tab ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (selectedTab == tab) HubPurple else HubCard,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 13.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        tab,
                        color = HubText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        when (selectedTab) {
            "Games" -> {
                Text(
                    "SELECT YOUR GAME",
                    color = HubText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Installed apps on your device",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Selected: $selectedGame",
                    color = HubPurple,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    items(installedGames) { game ->
                        HubItem(
                            title = game.name,
                            subtitle = game.packageName,
                            selected = selectedGame == game.name,
                            onClick = { selectedGame = game.name }
                        )
                    }
                }
            }

            "Scenes" -> {
                Text(
                    "SCENE STUDIO",
                    color = HubText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Select or create your stream scenes",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    items(scenes) { scene ->
                        HubItem(
                            title = scene,
                            subtitle = if (scene == selectedScene)
                                "Currently selected"
                            else "Tap to select",
                            selected = selectedScene == scene,
                            onClick = { selectedScene = scene }
                        )
                    }
                }

                OutlinedTextField(
                    value = customScene,
                    onValueChange = { customScene = it },
                    label = { Text("Custom scene name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = hubFieldColors()
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        val name = customScene.trim()
                        if (name.isNotEmpty() && !scenes.contains(name)) {
                            scenes.add(name)
                            selectedScene = name
                            customScene = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HubPurple
                    )
                ) {
                    Text("＋  CREATE SCENE")
                }
            }

            "Music" -> {
                Text(
                    "MUSIC STUDIO",
                    color = HubText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Import audio from your phone",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(18.dp))

                HubItem(
                    title = musicName,
                    subtitle = if (musicUri != null)
                        "Audio file imported"
                    else "No audio selected",
                    selected = musicUri != null,
                    onClick = {}
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        musicPicker.launch(arrayOf("audio/*"))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HubPurple
                    )
                ) {
                    Text("＋  IMPORT MUSIC")
                }

                Spacer(Modifier.height(15.dp))

                Text(
                    "Supported formats depend on Android and the selected file provider. Use music you have permission to stream.",
                    color = HubMuted,
                    fontSize = 12.sp
                )
            }

            "Mixer" -> {
                Text(
                    "AUDIO MIXER",
                    color = HubText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Independent volume controls",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(18.dp))

                HubVolume(
                    title = "Game Audio",
                    value = gameVolume,
                    onChange = { gameVolume = it }
                )

                HubVolume(
                    title = "Microphone",
                    value = micVolume,
                    onChange = { micVolume = it }
                )

                HubVolume(
                    title = "Background Music",
                    value = musicVolume,
                    onChange = { musicVolume = it }
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "These are currently interface controls. Actual audio capture and mixing will be implemented in the streaming engine.",
                    color = HubMuted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun HubItem(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) Color(0xFF302047) else HubCard,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                color = HubText,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                subtitle,
                color = HubMuted,
                fontSize = 11.sp
            )
        }

        if (selected) {
            Text(
                "✓",
                color = HubPurple,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun HubVolume(
    title: String,
    value: Float,
    onChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HubCard, RoundedCornerShape(14.dp))
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = HubText, fontWeight = FontWeight.SemiBold)
            Text("${value.toInt()}%", color = HubPurple)
        }

        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = HubPurple,
                activeTrackColor = HubPurple
            )
        )
    }

    Spacer(Modifier.height(12.dp))
}

@Composable
private fun hubFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = HubText,
    unfocusedTextColor = HubText,
    focusedBorderColor = HubPurple,
    unfocusedBorderColor = HubMuted,
    focusedLabelColor = HubPurple,
    unfocusedLabelColor = HubMuted,
    cursorColor = HubPurple
)

