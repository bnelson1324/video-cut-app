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
import java.nio.file.Files
import java.util.*

class MainController : Initializable {
    // open directory
    private var openDirectory: File? = null

    @FXML
    private lateinit var openDirectoryLabel: Label

    // media player
    private var videoList: List<File> = listOf()

    private var mediaPlayerIndex = 0
        set(value) {
            field = value

            // prevent index out of bounds exception
            if (videoList.isNotEmpty())
                field = (field + videoList.size) % videoList.size
        }

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
            getVideoData(videoList[mediaPlayerIndex])!!.startTime = value
            startTimeLabel.text = "Start Time: ${formatTime(field.toSeconds())}"
        }

    private var endTime: Duration = Duration.ZERO
        set(value) {
            field = value
            mediaView.mediaPlayer?.stopTime = value
            getVideoData(videoList[mediaPlayerIndex])!!.endTime = value
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
        val newOpenDirectory = directoryChooser.showDialog((ae.source as Node).scene.window) ?: return
        openDirectory = newOpenDirectory

        // update video list and media player
        openDirectoryLabel.text = "Source Directory: $openDirectory"
        videoList = openDirectory!!.listFiles()!!
            .filter {
                // only keep video files
                val contentType: String? = Files.probeContentType(it.toPath())
                return@filter if (contentType != null) contentType.split('/')[0] == "video" else false
            }
        updateMediaPlayer()
    }

    private fun updateMediaPlayer() {
        // pause current media player
        mediaView.mediaPlayer?.pause()

        // update media player
        val mediaPath = videoList[mediaPlayerIndex]
        val media = Media(mediaPath.toURI().toString())
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

            // load values from videoData
            initializeVideoData(mediaPath, media)
            startTime = getVideoData(mediaPath)!!.startTime
            endTime = getVideoData(mediaPath)!!.endTime
        }

        mediaPlayer.onEndOfMedia = Runnable {
            mediaPlayer.stop()
            mediaPlayer.seek(startTime)
        }

        mediaProgressSlider.value = 0.0
    }

    @FXML
    fun onProcessVideosBtnClick(ae: ActionEvent) {
        for (mediaPath in videoList) {
            val videoData = getVideoData(mediaPath)
            if (videoData != null && videoData.modified) {
                cutVideo(openDirectory!!, mediaPath, videoData.startTime, videoData.endTime)
            } else {
                copyVideo(openDirectory!!, mediaPath)
            }
        }
    }

    val keyEventHandler = object : EventHandler<KeyEvent> {
        override fun handle(ke: KeyEvent) {
            val mediaPlayer: MediaPlayer? = mediaView.mediaPlayer
            if (mediaPlayer?.media == null) return

            when (ke.code) {
                KeyCode.SPACE -> {

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
                    startTime = if (!ke.isControlDown) {
                        mediaPlayer.currentTime
                    } else {
                        Duration.ZERO
                    }
                    getVideoData(videoList[mediaPlayerIndex])!!.modified = true
                }

                KeyCode.E -> {
                    endTime = if (!ke.isControlDown) {
                        mediaPlayer.currentTime
                    } else {
                        mediaPlayer.media.duration
                    }
                    getVideoData(videoList[mediaPlayerIndex])!!.modified = true
                }

                KeyCode.LEFT -> {
                    mediaPlayer.seek(mediaPlayer.currentTime.subtract(Duration(if (!ke.isControlDown) 5000.0 else 15000.0)))
                }

                KeyCode.RIGHT -> {
                    mediaPlayer.seek(mediaPlayer.currentTime.add(Duration(if (!ke.isControlDown) 5000.0 else 15000.0)))
                }

                KeyCode.A -> {
                    mediaPlayerIndex -= 1
                    updateMediaPlayer()
                }

                KeyCode.D -> {
                    mediaPlayerIndex += 1
                    updateMediaPlayer()
                }

                else -> return
            }
        }
    }
}
