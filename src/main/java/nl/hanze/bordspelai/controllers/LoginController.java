package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {
  @FXML
  private TextField username;

  @FXML
  private void login() {
    System.out.println("Username: " + username.getText());
  }
}
