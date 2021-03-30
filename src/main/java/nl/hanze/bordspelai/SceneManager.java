package nl.hanze.bordspelai;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.hanze.bordspelai.controllers.Controller;

public class SceneManager {
  private static SceneManager _instance = null;
  private Stage stage;

  private SceneManager () {}
  public SceneManager(Stage stage) {
    this.stage = stage;
  } 

  public void switchScene(String path, Controller controller) {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource(path));
    loader.setController(controller);
    
    Parent root;
    try {
      root = loader.load();
      Scene scene = new Scene(root);

      stage.setScene(scene);
      stage.show();
    } catch (Throwable e) {
      e.printStackTrace(System.out);
    }
  }

  private synchronized static void createInstance () {
      if (_instance == null) _instance = new SceneManager ();
  }

  public static SceneManager getInstance () {
      if (_instance == null) createInstance ();
      return _instance;
  }
}
