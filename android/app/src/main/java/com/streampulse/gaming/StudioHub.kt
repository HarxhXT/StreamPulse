
package com.streampulse.gaming

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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

@Composable
fun StudioHub(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember {
        context.getSharedPreferences(
            "streampulse_starting_scene",
            android.content.Context.MODE_PRIVATE
        )
    }

    var selectedTab by remember { mutableStateOf("Games") }
    val selectedGames = remember { mutableStateListOf<String>() }

    var mediaUri by remember {
        mutableStateOf(
            prefs.getString("media_uri", null)?.let { Uri.parse(it) }
        )
    }

    var mediaType by remember {
        mutableStateOf(prefs.getString("media_type", "") ?: "")
    }

    var musicUri by remember {
        mutableStateOf(
            prefs.getString("music_uri", null)?.let { Uri.parse(it) }
        )
    }

    var musicName by remember {
        mutableStateOf(
            prefs.getString("music_name", "No music imported")
                ?: "No music imported"
        )
    }

    var message by remember { mutableStateOf("") }
    var gameVolume by remember { mutableFloatStateOf(80f) }
    var micVolume by remember { mutableFloatStateOf(70f) }
    var musicVolume by remember { mutableFloatStateOf(25f) }
    var showStartInfo by remember { mutableStateOf(false) }

    var previewPlayer by remember {
        mutableStateOf<MediaPlayer?>(null)
    }

    fun stopMusicPreview() {
        try {
            previewPlayer?.stop()
            previewPlayer?.release()
        } catch (_: Exception) {}
        previewPlayer = null
    }

    DisposableEffect(Unit) {
        onDispose {
            stopMusicPreview()
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

                mediaUri = uri
                mediaType = type
                message = "Media selected. Press SAVE STARTING SCENE."
            } else {
                message = "Please select an image or video."
            }
        }
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

            message = "Music selected. Save Starting Scene."
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

                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
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
             .verticalScroll(rememberScrollState())
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
                    "Choose the apps you want to stream.",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Available apps: ${installedGames.size}  •  Selected: ${selectedGames.size}",
                    color = HubPurple,
                    fontSize = 13.sp
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

                if (installedGames.isEmpty()) {
                    Text(
                        "No launchable apps found. Check the AndroidManifest.xml app visibility setting.",
                        color = HubMuted,
                        fontSize = 13.sp
                    )
                } else {
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
            }

            "Scenes" -> {
                Text(
                    "SCENE STUDIO",
                    color = HubText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Only one customizable scene",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF302047),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Starting Soon",
                            color = HubText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Custom image/video and music",
                            color = HubMuted,
                            fontSize = 11.sp
                        )
                    }
                    Text("✓", color = HubPurple, fontSize = 22.sp)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "BACKGROUND",
                    color = HubPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))

                if (mediaUri != null && mediaType == "image") {
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
                            view.setImageURI(mediaUri)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .background(HubCard, RoundedCornerShape(12.dp))
                    )
                } else if (mediaUri != null && mediaType == "video") {
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
                            if (view.tag != mediaUri.toString()) {
                                view.tag = mediaUri.toString()
                                view.setVideoURI(mediaUri)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .background(HubCard, RoundedCornerShape(12.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .background(HubCard, RoundedCornerShape(12.dp)),
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
                        mediaPicker.launch(arrayOf("image/*", "video/*"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("＋ SELECT IMAGE OR VIDEO")
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    "STARTING SOON MUSIC",
                    color = HubPurple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    musicName,
                    color = HubText,
                    fontSize = 13.sp
                )

                OutlinedButton(
                    onClick = {
                        musicPicker.launch(arrayOf("audio/*"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("＋ SELECT MUSIC")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            stopMusicPreview()
                            if (musicUri != null) {
                                try {
                                    previewPlayer = MediaPlayer().apply {
                                        setDataSource(
                                            context,
                                            musicUri!!
                                        )
                                        setOnPreparedListener { it.start() }
                                        prepareAsync()
                                    }
                                } catch (_: Exception) {
                                    message = "Unable to preview this audio."
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("▶ PREVIEW")
                    }

                    OutlinedButton(
                        onClick = {
                            stopMusicPreview()
                            message = "Music preview stopped."
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("STOP")
                    }
                }

                Spacer(Modifier.height(14.dp))

                Button(
                    onClick = {
                        prefs.edit()
                            .putString("media_uri", mediaUri?.toString())
                            .putString("media_type", mediaType)
                            .putString("music_uri", musicUri?.toString())
                            .putString("music_name", musicName)
                            .apply()

                        message = "Starting Soon scene saved."
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HubPurple
                    )
                ) {
                    Text("SAVE STARTING SCENE")
                }

                if (message.isNotBlank()) {
                    Text(
                        message,
                        color = HubPurple,
                        fontSize = 12.sp
                    )
                }
            }

            "Music" -> {
                Text(
                    "MUSIC",
                    color = HubText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Music is configured inside Starting Soon.",
                    color = HubMuted,
                    fontSize = 12.sp
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    musicName,
                    color = HubText,
                    fontSize = 14.sp
                )

                Button(
                    onClick = { selectedTab = "Scenes" },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HubPurple
                    )
                ) {
                    Text("OPEN STARTING SOON SETTINGS")
                }
            }

            "Mixer" -> {
                Text(
                    "AUDIO MIXER",
                    color = HubText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Volume controls (interface only)",
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
                    "Actual audio mixing will be connected with the streaming engine.",
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
                    "Screen capture, automatic BRB switching, encoding and YouTube/Kick broadcasting still need to be connected.",
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
