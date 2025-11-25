package com.durgasoft.slot

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun MusicToggleButton() {
    val ctx = LocalContext.current
    var playing by remember { mutableStateOf(false) }

    IconButton(onClick = {
        val action = if (playing) MusicService.ACTION_PAUSE else MusicService.ACTION_PLAY
        ctx.startService(Intent(ctx, MusicService::class.java).apply {
            this.action = action
        })
        playing = !playing
    }) {
        Icon(
            imageVector = if (playing) Icons.Filled.MusicNote else Icons.Filled.MusicOff,
            contentDescription = if (playing) "Música activada" else "Música desactivada"
        )
    }
}
