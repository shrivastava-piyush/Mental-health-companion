package com.wellness.companion.ui.components

import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun MirrorAvatar(modifier: Modifier = Modifier) {
    val videoUrl = "https://assets.mixkit.co/videos/preview/mixkit-abstract-flowing-teal-and-blue-smoke-24151-large.mp4"
    
    Surface(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape),
        color = Color.White.copy(alpha = 0.05f),
        shape = CircleShape,
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(listOf(Color.Cyan.copy(alpha = 0.5f), Color.Transparent))
        ),
        shadowElevation = 20.dp
    ) {
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    setVideoURI(Uri.parse(videoUrl))
                    setOnPreparedListener { it.isLooping = true }
                    start()
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .blur(2.dp)
        )
    }
}
