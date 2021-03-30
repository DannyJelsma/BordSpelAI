package nl.hanze.bordspelai;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.hanze.bordspelai.controllers.Controller;

public class SceneManager {
  private static Stage stage;

  public static void setStage(Stage newStage) {
    stage = newStage;
  }

  public static void switchScene(String path, Controller controller) {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(SceneManager.class.getResource(path));
    loader.setController(controller);
    
    Parent root;
    try {
      root = loader.load();
      Scene scene = new Scene(root);

      stage.setScene(scene);
      stage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
