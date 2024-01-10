package com.example.videocutapp

fun formatTime(seconds: Double): String {
    return String.format("%d:%02d:%02d", seconds.toInt() / 3600, (seconds.toInt() % 3600) / 60, (seconds.toInt() % 60))
}
