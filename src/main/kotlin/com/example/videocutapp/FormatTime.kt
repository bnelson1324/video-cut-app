package com.example.videocutapp

import javafx.util.Duration

fun formatTime(seconds: Double): String {
    return String.format("%d:%02d:%02d", seconds.toInt() / 3600, (seconds.toInt() % 3600) / 60, (seconds.toInt() % 60))
}

fun Duration.toFFmpegTime(): String {
    return formatTime(this.toSeconds())
}
