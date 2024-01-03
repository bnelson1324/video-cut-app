package com.example.videocutapp

import javafx.util.Duration
import java.io.File

private const val crf: Int = 18

private const val relativeOutputDirectory = "vca-output"
private fun getOutputDirectory(openFile: File) = File(openFile.absoluteFile, relativeOutputDirectory)
private fun getOutputPath(openDirectory: File, mediaPath: File) =
    File(getOutputDirectory(openDirectory), mediaPath.name)

fun cutVideo(workingDirectory: File, videoPath: File, startTime: Duration, endTime: Duration) {
    val outputPath = getOutputPath(workingDirectory, videoPath)
    val builder =
        ProcessBuilder(
            *getShellCommand(),
            "ffmpeg -i ${videoPath.absolutePath} -ss ${startTime.toFFmpegTime()} -to ${endTime.toFFmpegTime()} -crf $crf -y $outputPath"
        )
            .directory(workingDirectory.absoluteFile)

    getOutputDirectory(workingDirectory).mkdirs()
    builder.start()
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
