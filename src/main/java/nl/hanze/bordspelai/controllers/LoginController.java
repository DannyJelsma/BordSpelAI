package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.models.LobbyModel;
import nl.hanze.bordspelai.models.LoginModel;
import nl.hanze.bordspelai.net.Server;

public class LoginController implements Controller {

  @FXML
  private TextField username;

  public LoginController(LoginModel model) {

  }

  @FXML
  private void login() {
    Server server = BordspelAI.getServer();
    System.out.println("Username: " + username.getText());

    if (server.sendCommand(Command.LOGIN, username.getText())) {
      SceneManager.switchScene("/views/lobby.fxml", new LobbyController(new LobbyModel()));
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);

      alert.setContentText(server.getLastError());
      alert.show();
    }
  }
}
