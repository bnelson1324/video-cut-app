package com.example.videocutapp

import com.github.kokorin.jaffree.ffmpeg.FFmpeg
import com.github.kokorin.jaffree.ffmpeg.UrlInput
import com.github.kokorin.jaffree.ffmpeg.UrlOutput
import javafx.util.Duration
import java.io.File
import java.util.concurrent.TimeUnit

private const val crf: Int = 18

private const val relativeOutputDirectory = "vca-output"
fun getOutputDirectory(openDirectory: File) = File(openDirectory.absoluteFile, relativeOutputDirectory)
private fun getOutputPath(openDirectory: File, mediaPath: File) =
    File(getOutputDirectory(openDirectory), mediaPath.name)

fun cutVideo(workingDirectory: File, videoPath: File, startTime: Duration, endTime: Duration) {
    val outputPath = getOutputPath(workingDirectory, videoPath)

    getOutputDirectory(workingDirectory).mkdirs()
    FFmpeg.atPath()
            .addInput(UrlInput
                .fromUrl(videoPath.absolutePath)
                .setPosition(startTime.toMillis(), TimeUnit.MILLISECONDS)
                .setDuration(endTime.subtract(startTime).toMillis(), TimeUnit.MILLISECONDS)
            )
            .setOverwriteOutput(true)
            .addOutput(UrlOutput.toUrl(outputPath.absolutePath))
            .execute()
}

fun copyVideo(openDirectory: File, videoPath: File) {
    videoPath.copyTo(getOutputPath(openDirectory, videoPath))
}

private fun getShellCommand(): Array<String> {
    val osName = System.getProperty("os.name")

    if (osName.startsWith("Windows"))
        return arrayOf("cmd.exe", "/C")
    return arrayOf("/bin/bash", "-c")
}
