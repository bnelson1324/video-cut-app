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
import javafx.scene.layout.AnchorPane
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.stage.DirectoryChooser
import javafx.util.Duration
import java.io.File
import java.net.URL
import java.util.*

class MainController : Initializable {
    // open directory
    private var openDirectory: File? = null

    @FXML
    private lateinit var openDirectoryLabel: Label

    // media player
    private var videoList: Array<File> = arrayOf()

    private var mediaPlayerIndex = 0

    @FXML
    private lateinit var mediaView: MediaView

    @FXML
    private lateinit var mediaProgressSlider: Slider

    @FXML
    private lateinit var mediaLabel: Label

    // start/end time
    private var startTime: Duration = Duration.ZERO
        set(value) {
            field = value
            mediaView.mediaPlayer?.startTime = value
            startTimeLabel.text = "Start Time: ${formatTime(field.toSeconds())}"
        }

    private var endTime: Duration = Duration.ZERO
        set(value) {
            field = value
            mediaView.mediaPlayer?.stopTime = value
            endTimeLabel.text = "End Time: ${formatTime(endTime.toSeconds())}"
        }

    @FXML
    private lateinit var startTimeLabel: Label

    @FXML
    private lateinit var endTimeLabel: Label

    // misc
    @FXML
    private lateinit var root: AnchorPane


    override fun initialize(url: URL?, resourceBundle: ResourceBundle?) {
        // set up slider
        val sliderMouseEvent = EventHandler { _: MouseEvent ->
            mediaView.mediaPlayer?.pause()
            mediaView.mediaPlayer?.seek(Duration(mediaProgressSlider.value * 1000))
        }
        mediaProgressSlider.addEventHandler(MouseEvent.MOUSE_CLICKED, sliderMouseEvent)
        mediaProgressSlider.addEventHandler(MouseEvent.MOUSE_DRAGGED, sliderMouseEvent)
        mediaProgressSlider.addEventHandler(MouseEvent.MOUSE_RELEASED) { root.requestFocus() }
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

            // update startTime and endTime
            startTime = Duration.ZERO
            endTime = media.duration
        }

        mediaPlayer.onEndOfMedia = Runnable {
            mediaPlayer.stop()
            mediaPlayer.seek(startTime)
        }
    }

    val keyEventHandler = object : EventHandler<KeyEvent> {
        override fun handle(ke: KeyEvent) {
            when (ke.code) {
                KeyCode.SPACE -> {
                    val mediaPlayer = mediaView.mediaPlayer
                    if (mediaPlayer?.media == null) return

                    if (mediaPlayer.status == MediaPlayer.Status.PLAYING) {
                        // pause
                        mediaPlayer.pause()
                    } else {
                        // unpause
                        mediaPlayer.play()
                        if (startTime > mediaPlayer.currentTime) {
                            mediaPlayer.seek(startTime)
                        }
                    }
                }

                KeyCode.Q -> {
                    if (mediaView.mediaPlayer?.media == null) return

                    startTime = if (!ke.isControlDown) {
                        mediaView.mediaPlayer.currentTime
                    } else {
                        Duration.ZERO
                    }

                }

                KeyCode.E -> {
                    if (mediaView.mediaPlayer?.media == null) return

                    endTime = if (!ke.isControlDown) {
                        mediaView.mediaPlayer.currentTime
                    } else {
                        mediaView.mediaPlayer.media.duration
                    }
                }

                else -> return
            }
        }
    }
}
