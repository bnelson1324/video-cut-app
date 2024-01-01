package com.example.videocutapp

import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.stage.DirectoryChooser
import javafx.util.Duration
import java.io.File

class MainController {
    private var openDirectory: File? = null

    @FXML
    private lateinit var openDirectoryLabel: Label


    private var videoList: Array<File> = arrayOf()

    private var mediaPlayerIndex = 0

    @FXML
    private lateinit var mediaView: MediaView

    @FXML
    private lateinit var mediaProgressSlider: Slider

    @FXML
    private lateinit var mediaLabel: Label


    private var startTime = 0

    private var endTime = 0

    @FXML
    private lateinit var startTimeLabel: Label

    @FXML
    private lateinit var endTimeLabel: Label

    @FXML
    fun onChooseDirectoryBtnClick(ae: ActionEvent) {
        // prompt user to choose a directory
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Choose folder with videos"
        openDirectory = directoryChooser.showDialog((ae.source as Node).scene.window)
        if (openDirectory == null)
            return

        // update video list and media player
        openDirectoryLabel.text = "Source Directory: $openDirectory"
        videoList = openDirectory!!.listFiles()!! // TODO: filter files to be video files only
        updateMediaPlayer()
    }

    private fun updateMediaPlayer() {
        val media = Media(videoList[mediaPlayerIndex].toURI().toString())
        val mediaPlayer = MediaPlayer(media)
        mediaView.mediaPlayer = mediaPlayer

        // update slider and text
        mediaPlayer.currentTimeProperty()
            .addListener { _: ObservableValue<out Duration?>, _: Duration, newValue: Duration ->
                mediaProgressSlider.value = newValue.toSeconds()
                mediaLabel.text = "${formatTime(newValue.toSeconds())} / ${formatTime(media.duration.toSeconds())}"
            }
        mediaPlayer.onReady = Runnable {
            mediaProgressSlider.max = mediaPlayer.totalDuration.toSeconds()
            mediaLabel.text = "${formatTime(0.0)} / ${formatTime(media.duration.toSeconds())}"
        }
    }

    val keyEventHandler = object : EventHandler<KeyEvent> {
        override fun handle(ke: KeyEvent) {
            when (ke.code) {
                KeyCode.SPACE -> {
                    if (mediaView.mediaPlayer?.media == null)
                        return

                    if (mediaView.mediaPlayer.status == MediaPlayer.Status.PLAYING) {
                        mediaView.mediaPlayer.stop()
                    } else {
                        mediaView.mediaPlayer.play()
                    }
                }

                else -> return
            }
        }
    }
}
