package com.example.videocutapp

import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.stage.DirectoryChooser
import java.io.File

class MainController {
    private lateinit var openDirectory: File

    @FXML
    private lateinit var openDirectoryLabel: Label

    @FXML
    fun onChooseDirectoryBtnClick(ae: ActionEvent) {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = "Choose folder with videos"
        openDirectory = directoryChooser.showDialog((ae.source as Node).scene.window) ?: return
        openDirectoryLabel.text = openDirectory.toString()
    }
}
