package com.example.videocutapp

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import javafx.stage.Stage

class VideoCutApp : Application() {
    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(VideoCutApp::class.java.getResource("main-view.fxml"))
        val scene = Scene(fxmlLoader.load(), 800.0, 600.0)
        scene.addEventFilter(KeyEvent.KEY_PRESSED, fxmlLoader.getController<MainController>().keyEventHandler)
        stage.title = "Video Cut App"
        stage.scene = scene
        stage.isMaximized = true
        stage.minWidth = 1280.0
        stage.minHeight = 720.0
        stage.show()
    }
}

fun main() {
    Application.launch(VideoCutApp::class.java)
}
