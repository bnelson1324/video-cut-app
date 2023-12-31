package com.example.videocutapp

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.stage.DirectoryChooser
import java.io.File

class MainController {
    private var openDirectory: File? = null

    private var videoList: Array<File> = arrayOf()

    private var mediaPlayerIndex = 0


    @FXML
    private lateinit var mediaView: MediaView

    @FXML
    private lateinit var openDirectoryLabel: Label

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
        mediaView.mediaPlayer = MediaPlayer(Media(videoList[mediaPlayerIndex].toURI().toString()))
    }
}
