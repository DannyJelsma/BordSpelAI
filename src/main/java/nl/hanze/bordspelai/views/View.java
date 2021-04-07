package nl.hanze.bordspelai.views;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import nl.hanze.bordspelai.controllers.Controller;
import nl.hanze.bordspelai.managers.SceneManager;

public abstract class View {    
    private final Controller controller;
    private final String path;

    public View(String path, Controller controller) {
        this.controller = controller;
        this.path = path;
    }

    public FXMLLoader getLoader() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(SceneManager.class.getResource(path));
        loader.setController(controller);

        return loader;
    }
}
