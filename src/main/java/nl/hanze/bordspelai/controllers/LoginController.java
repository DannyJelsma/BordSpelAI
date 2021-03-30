package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.SceneManager;
import nl.hanze.bordspelai.net.Command;

public class LoginController implements Controller {
  @FXML
  private TextField username;

  @FXML
  private void login() {
    System.out.println("Username: " + username.getText());

    BordspelAI.getServer().sendCommand(Command.LOGIN, username.getText());
    SceneManager.switchScene("/views/lobby.fxml", new LobbyController());
  }
}
