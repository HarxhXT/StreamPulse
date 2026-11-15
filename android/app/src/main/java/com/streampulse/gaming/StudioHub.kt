
package com.streampulse.gaming

import android.content.Intent
import android.content.pm.ApplicationInfo
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

private data class StreamScene(
    val name: String,
    val backgroundUri: Uri? = null,
    val overlayText: String = ""
)

@Composable
fun StudioHub(onBack: () -> Unit) {
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf("Games") }

    val selectedGames = remember {
        mutableStateListOf<String>()
    }

    var selectedScene by remember {
        mutableStateOf("Gameplay")
    }

    var customScene by remember {
        mutableStateOf("")
    }

    var sceneText by remember {
        mutableStateOf("")
    }

    var musicName by remember {
        mutableStateOf("No music imported")
    }

    var musicUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var gameVolume by remember { mutableFloatStateOf(80f) }
    var micVolume by remember { mutableFloatStateOf(70f) }
    var musicVolume by remember { mutableFloatStateOf(25f) }

    var showStartInfo by remember {
        mutableStateOf(false)
    }

    val scenes = remember {
        mutableStateListOf(
            StreamScene("Starting Soon", overlayText = "STREAM STARTING SOON"),
            StreamScene("Gameplay"),
            StreamScene("BRB", overlayText = "BE RIGHT BACK"),
            StreamScene("Ending", overlayText = "THANKS FOR WATCHING")
        )
    }

    val musicPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}

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

    val backgroundPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}

            val index = scenes.indexOfFirst {
                it.name == selectedScene
            }

            if (index >= 0) {
                scenes[index] = scenes[index].copy(
                    backgroundUri = uri
                )
            }
        }
    }

    val installedGames = remember {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        context.packageManager
            .queryIntentActivities(intent, 0)
            .mapNotNull { resolveInfo ->
                val appInfo = resolveInfo.activityInfo.applicationInfo

                if (appInfo.packageName == context.packageName) {
                    return@mapNotNull null
                }

                val isSystemApp =
                    (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0

                val isGame =
                    appInfo.category == ApplicationInfo.CATEGORY_GAME

                if (isSystemApp && !isGame) {
                    return@mapNotNull null
                }

                InstalledGame(
                    name = resolveInfo.loadLabel(
                        context.packageManager
                    ).toString(),
                    packageName = appInfo.packageName
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.name.lowercase() }
    }

    val currentScene = scenes.firstOrNull {
        it.name == selectedScene
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HubBg)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "‹",
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

        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                        .padding(vertical = 14.dp),
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

        Spacer(Modifier.height(16.dp))

        when (selectedTab) {

            "Games" -> {
                Text(
                    "SELECT APPS & GAMES",
                    color = HubText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Choose one or multiple installed apps",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Selected: ${selectedGames.size}",
                    color = HubPurple,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            selectedGames.clear()
                            selectedGames.addAll(
                                installedGames.map { it.packageName }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HubPurple
                        )
                    ) {
                        Text("SELECT ALL")
                    }

                    OutlinedButton(
                        onClick = { selectedGames.clear() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CLEAR")
                    }
                }

                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(installedGames) { game ->
                        val checked =
                            selectedGames.contains(game.packageName)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (checked) Color(0xFF302047)
                                    else HubCard,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    if (checked) {
                                        selectedGames.remove(game.packageName)
                                    } else {
                                        selectedGames.add(game.packageName)
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    game.name,
                                    color = HubText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Text(
                                    game.packageName,
                                    color = HubMuted,
                                    fontSize = 10.sp
                                )
                            }

                            Checkbox(
                                checked = checked,
                                onCheckedChange = { value ->
                                    if (value) {
                                        selectedGames.add(game.packageName)
                                    } else {
                                        selectedGames.remove(game.packageName)
                                    }
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = HubPurple
                                )
                            )
                        }
                    }
                }
            }

            "Scenes" -> {
                Text(
                    "SCENE STUDIO",
                    color = HubText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Create and customize your stream scenes",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(scenes) { scene ->
                        HubItem(
                            title = scene.name,
                            subtitle = if (scene.name == selectedScene)
                                "Currently selected"
                            else "Tap to edit",
                            selected = selectedScene == scene.name,
                            onClick = {
                                selectedScene = scene.name
                                sceneText = scene.overlayText
                            }
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "CREATE CUSTOM SCENE",
                    color = HubPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = customScene,
                    onValueChange = { customScene = it },
                    label = { Text("Scene name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = hubFieldColors()
                )

                Spacer(Modifier.height(6.dp))

                Button(
                    onClick = {
                        val name = customScene.trim()

                        if (
                            name.isNotEmpty() &&
                            scenes.none { it.name.equals(name, true) }
                        ) {
                            scenes.add(StreamScene(name))
                            selectedScene = name
                            sceneText = ""
                            customScene = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HubPurple
                    )
                ) {
                    Text("＋ CREATE SCENE")
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    "EDIT SELECTED SCENE: $selectedScene",
                    color = HubText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = sceneText,
                    onValueChange = { sceneText = it },
                    label = { Text("Text shown on scene") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = hubFieldColors()
                )

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        backgroundPicker.launch(arrayOf("image/*"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("＋ CHOOSE BACKGROUND IMAGE")
                }

                Text(
                    if (currentScene?.backgroundUri != null)
                        "Background image selected"
                    else
                        "No background image selected",
                    color = HubMuted,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        val index = scenes.indexOfFirst {
                            it.name == selectedScene
                        }

                        if (index >= 0) {
                            scenes[index] = scenes[index].copy(
                                overlayText = sceneText
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HubPurple
                    )
                ) {
                    Text("SAVE SCENE CHANGES")
                }
            }

            "Music" -> {
                Text(
                    "MUSIC STUDIO",
                    color = HubText,
                    fontSize = 18.sp,
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
                    Text("＋ IMPORT MUSIC")
                }

                Spacer(Modifier.height(15.dp))

                Text(
                    "Music import is available. Audio playback and mixing will be added with the streaming engine. Use music you have permission to stream.",
                    color = HubMuted,
                    fontSize = 12.sp
                )
            }

            "Mixer" -> {
                Text(
                    "AUDIO MIXER",
                    color = HubText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Independent volume controls",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(18.dp))

                HubVolume(
                    "Game Audio",
                    gameVolume
                ) { gameVolume = it }

                HubVolume(
                    "Microphone",
                    micVolume
                ) { micVolume = it }

                HubVolume(
                    "Background Music",
                    musicVolume
                ) { musicVolume = it }

                Text(
                    "These sliders are currently interface settings. Actual audio mixing is not connected yet.",
                    color = HubMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = { showStartInfo = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HubPurple
            ),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(
                "▶  START STREAM",
                color = HubText,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            "Broadcast engine and platform login are not connected yet.",
            color = HubMuted,
            fontSize = 10.sp
        )
    }

    if (showStartInfo) {
        AlertDialog(
            onDismissRequest = { showStartInfo = false },
            containerColor = HubCard,
            title = {
                Text("Streaming engine not ready", color = HubText)
            },
            text = {
                Text(
        
