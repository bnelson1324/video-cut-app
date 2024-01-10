package com.example.videocutapp

import com.github.kokorin.jaffree.ffmpeg.FFmpeg
import com.github.kokorin.jaffree.ffmpeg.UrlInput
import com.github.kokorin.jaffree.ffmpeg.UrlOutput
import javafx.application.Platform
import javafx.scene.control.Label
import javafx.util.Duration
import java.io.File
import java.util.concurrent.TimeUnit

private const val crf: Int = 18

private const val relativeOutputDirectory = "vca-output"
fun getOutputDirectory(openDirectory: File) = File(openDirectory.absoluteFile, relativeOutputDirectory)
private fun getOutputPath(openDirectory: File, mediaPath: File) =
    File(getOutputDirectory(openDirectory), mediaPath.name)

fun cutVideo(workingDirectory: File, videoPath: File, startTime: Duration, endTime: Duration, progressLabel: Label) {
    val outputPath = getOutputPath(workingDirectory, videoPath)
    getOutputDirectory(workingDirectory).mkdirs()

    progressLabel.text = "Processing..."
    Thread {
        FFmpeg.atPath()
            .addInput(UrlInput
                .fromUrl(videoPath.absolutePath)
                .setPosition(startTime.toMillis(), TimeUnit.MILLISECONDS)
                .setDuration(endTime.subtract(startTime).toMillis(), TimeUnit.MILLISECONDS)
            )
            .setOverwriteOutput(true)
            .addOutput(UrlOutput.toUrl(outputPath.absolutePath))
            .execute()
        Platform.runLater { progressLabel.text = "Finished processing" }
    }.start()
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
