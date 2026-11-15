
package com.streampulse.gaming

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
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
import androidx.compose.ui.viewinterop.AndroidView

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
    val mediaUri: Uri? = null,
    val mediaType: String = ""
)

private fun encodeScene(value: String): String {
    return Base64.encodeToString(
        value.toByteArray(Charsets.UTF_8),
        Base64.NO_WRAP
    )
}

private fun decodeScene(value: String): String {
    return try {
        String(
            Base64.decode(value, Base64.NO_WRAP),
            Charsets.UTF_8
        )
    } catch (_: Exception) {
        ""
    }
}

private fun loadScenes(
    context: android.content.Context
): List<StreamScene> {
    val prefs = context.getSharedPreferences(
        "streampulse_scenes",
        android.content.Context.MODE_PRIVATE
    )

    val saved = prefs.getStringSet("scenes", null)

    if (saved.isNullOrEmpty()) {
        return listOf(
            StreamScene("Starting Soon"),
            StreamScene("Gameplay"),
            StreamScene("BRB"),
            StreamScene("Ending")
        )
    }

    return saved.mapNotNull { record ->
        val parts = record.split("~")
        if (parts.size != 3) return@mapNotNull null

        val name = decodeScene(parts[0])
        val uriString = decodeScene(parts[1])
        val type = decodeScene(parts[2])

        if (name.isBlank()) return@mapNotNull null

        StreamScene(
            name = name,
            mediaUri = uriString.takeIf { it.isNotBlank() }?.let {
                Uri.parse(it)
            },
            mediaType = type
        )
    }.sortedBy { scene ->
        listOf("Starting Soon", "Gameplay", "BRB", "Ending")
            .indexOf(scene.name)
            .let { if (it < 0) 100 else it }
    }
}

private fun saveScenes(
    context: android.content.Context,
    scenes: List<StreamScene>
) {
    val records = scenes.map { scene ->
        listOf(
            encodeScene(scene.name),
            encodeScene(scene.mediaUri?.toString() ?: ""),
            encodeScene(scene.mediaType)
        ).joinToString("~")
    }.toSet()

    context.getSharedPreferences(
        "streampulse_scenes",
        android.content.Context.MODE_PRIVATE
    ).edit()
        .putStringSet("scenes", records)
        .apply()
}

@Composable
fun StudioHub(onBack: () -> Unit) {
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf("Games") }

    val selectedGames = remember {
        mutableStateListOf<String>()
    }

    val scenes = remember {
        mutableStateListOf<StreamScene>().apply {
            addAll(loadScenes(context))
        }
    }

    var selectedScene by remember {
        mutableStateOf("Gameplay")
    }

    var customSceneName by remember {
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

    var sceneMessage by remember {
        mutableStateOf("")
    }

    val currentScene = scenes.firstOrNull {
        it.name == selectedScene
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

    val mediaPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val mime = context.contentResolver.getType(uri) ?: ""

            val type = when {
                mime.startsWith("image/") -> "image"
                mime.startsWith("video/") -> "video"
                else -> ""
            }

            if (type.isNotEmpty()) {
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
                        mediaUri = uri,
                        mediaType = type
                    )
                    sceneMessage = "Media selected. Press SAVE SCENE."
                }
            } else {
                sceneMessage = "Please select an image or video file."
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
                            Column(Modifier.weight(1f)) {
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
                    "Custom image and video backgrounds",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(scenes) { scene ->
                        HubItem(
                            title = scene.name,
                            subtitle = when {
                                scene.name == selectedScene -> "Selected scene"
                                scene.mediaType == "video" -> "Video background saved"
                                scene.mediaType == "image" -> "Image background saved"
                                else -> "No background"
                            },
                            selected = selectedScene == scene.name,
                            onClick = {
                                selectedScene = scene.name
                                sceneMessage = ""
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    "CREATE CUSTOM SCENE",
                    color = HubPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = customSceneName,
                    onValueChange = { customSceneName = it },
                    label = { Text("Custom scene name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = hubFieldColors()
                )

                Spacer(Modifier.height(6.dp))

                Button(
                    onClick = {
                        val name = customSceneName.trim()

                        if (
                            name.isNotEmpty() &&
                            scenes.none { it.name.equals(name, true) }
                        ) {
                            scenes.add(StreamScene(name))
                            selectedScene = name
                            customSceneName = ""
                            saveScenes(context, scenes.toList())
                            sceneMessage = "New scene created."
                        } else {
                            sceneMessage = "Enter a unique scene name."
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HubPurple
                    )
                ) {
                    Text("＋ CREATE SCENE")
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "EDIT: $selectedScene",
                    color = HubText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                // Background preview
                if (currentScene?.mediaUri != null) {
                    if (currentScene.mediaType == "image") {
                        AndroidView(
                            factory = { ctx ->
                                ImageView(ctx).apply {
                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        220
                                    )
                                }
                            },
                            update = { view ->
                                view.setImageURI(currentScene.mediaUri)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(HubCard, RoundedCornerShape(12.dp))
                        )
                    } else if (currentScene.mediaType == "video") {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    setOnPreparedListener { player ->
                                        player.isLooping = true
                                        start()
                                    }
                                }
                            },
                            update = { view ->
                                if (view.tag != currentScene.mediaUri.toString()) {
                                    view.tag = currentScene.mediaUri.toString()
                                    view.setVideoURI(currentScene.mediaUri)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(170.dp)
                                .background(HubCard, RoundedCornerShape(12.dp))
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(
                                HubCard,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No background selected",
                            color = HubMuted
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = {
                        mediaPicker.launch(
                            arrayOf("image/*", "video/*")
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("＋ SELECT IMAGE OR VIDEO")
                }

                Text(
                    "Supported: images and Android-compatible videos",
                    color = HubMuted,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        saveScenes(context, scenes.toList())
                        sceneMessage = "Scene saved successfully."
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HubPurple
                    )
                ) {
                    Text("SAVE SCENE")
                }

                if (sceneMessage.isNotBlank()) {
                    Text(
                        sceneMessage,
                        color = HubPurple,
                        fontSize = 12.sp
                    )
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
                    "Audio playback and mixing will be added with the streaming engine. Use music you have permission to stream.",
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

                HubVolume("Game Audio", gameVolume) {
                    gameVolume = it
                }

                HubVolume("Microphone", micVolume) {
                    micVolume = it
                }

                HubVolume("Background Music", musicVolume) {
                    musicVolume = it
                }

                Text(
                    "These are interface settings. Actual audio mixing is not connected yet.",
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
                "▶ START STREAM",
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
                    "Screen capture, encoding and YouTube/Kick broadcasting still need to be connected.",
                    color = HubMuted
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showStartInfo = false }
                ) {
                    Text("OK", color = HubPurple)
                }
            }
        )
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
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
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
            Text("✓", color = HubPurple, fontSize = 20.sp)
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
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                title,
                color = HubText,
                fontWeight = FontWeight.SemiBold
            )
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
   
