package com.example.videocutapp

import javafx.scene.media.Media
import javafx.util.Duration
import java.io.File

private val videoDataMap: HashMap<File, VideoData> = HashMap()

fun initializeVideoData(mediaPath: File, media: Media) {
    if (!videoDataMap.containsKey(mediaPath))
        videoDataMap[mediaPath] = VideoData(Duration.ZERO, media.duration)
}

fun getVideoData(mediaPath: File): VideoData {
    return videoDataMap[mediaPath]!!
}

fun isVideoDataInitialized(mediaPath: File): Boolean {
    return videoDataMap.contains(mediaPath)
}

data class VideoData(var startTime: Duration, var endTime: Duration)
