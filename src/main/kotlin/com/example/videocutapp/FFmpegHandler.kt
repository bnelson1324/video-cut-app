package com.example.videocutapp

import javafx.util.Duration
import java.io.File

private const val relativeOutputDirectory = "vca-output"

fun cutVideo(workingDirectory: File, videoPath: File, startTime: Duration, endTime: Duration) {
    val outputDirectory = File(workingDirectory.absoluteFile, relativeOutputDirectory)
    val outputPath = File(outputDirectory, videoPath.name)
    val builder =
        ProcessBuilder(
            *getShellCommand(),
            "ffmpeg -i ${videoPath.absolutePath} -ss ${startTime.toFFmpegTime()} -to ${endTime.toFFmpegTime()} $outputPath"
        )
            .directory(workingDirectory.absoluteFile)

    outputDirectory.mkdirs()
    builder.start()
}

private fun getShellCommand(): Array<String> {
    val osName = System.getProperty("os.name")

    if (osName.startsWith("Windows"))
        return arrayOf("cmd.exe", "/C")
    return arrayOf("/bin/bash", "-c")
}
