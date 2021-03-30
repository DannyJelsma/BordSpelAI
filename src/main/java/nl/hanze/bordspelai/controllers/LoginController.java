package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import nl.hanze.bordspelai.SceneManager;

public class LoginController implements Controller {
  @FXML
  private TextField username;

  @FXML
  private void login() {
    System.out.println("Username: " + username.getText());

    SceneManager.switchScene("/views/test.fxml", new LoginController());
  }
}
