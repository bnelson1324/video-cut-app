package com.example.videocutapp

import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.stage.DirectoryChooser
import javafx.util.Duration
import java.io.File
import java.net.URL
import java.util.*

class MainController : Initializable {
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


    private var startTime: Duration = Duration.ZERO

    private var endTime: Duration = Duration.ZERO

    @FXML
    private lateinit var startTimeLabel: Label

    @FXML
    private lateinit var endTimeLabel: Label


    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        // set up slider
        mediaProgressSlider.addEventHandler(MouseEvent.MOUSE_DRAGGED) { _ ->
            mediaView.mediaPlayer?.pause()
            mediaView.mediaPlayer?.seek(Duration(mediaProgressSlider.value * 1000))
        }
    }

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

        mediaPlayer.onEndOfMedia = Runnable { mediaPlayer.stop() }
    }

    val keyEventHandler = object : EventHandler<KeyEvent> {
        override fun handle(ke: KeyEvent) {
            when (ke.code) {
                KeyCode.SPACE -> {
                    if (mediaView.mediaPlayer?.media == null) return

                    if (mediaView.mediaPlayer.status == MediaPlayer.Status.PLAYING) {
                        mediaView.mediaPlayer.pause()
                    } else {
                        mediaView.mediaPlayer.play()
                    }
                }

                KeyCode.Z -> {
                    if (mediaView.mediaPlayer?.media == null) return

                    startTime = mediaView.mediaPlayer.currentTime
                    startTimeLabel.text = "Start Time: ${formatTime(startTime.toSeconds())}"
                }

                KeyCode.X -> {
                    if (mediaView.mediaPlayer?.media == null) return

                    endTime = mediaView.mediaPlayer.currentTime
                    endTimeLabel.text = "End Time: ${formatTime(endTime.toSeconds())}"
                }

                else -> return
            }
        }
    }
}
