package nl.hanze.bordspelai.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.net.Server;

public class LoginController implements Controller {

  @FXML
  private TextField username;

  public LoginController() {

  }

  @FXML
  private void login() {
    Server server = BordspelAI.getServer();
    System.out.println("Username: " + username.getText());

    if (server.sendCommand(Command.LOGIN, username.getText())) {
      GameManager.getInstance().setState(GameState.LOBBY);
      GameManager.getInstance().setUsername(username.getText());
      SceneManager.switchScene("/views/lobby.fxml", new LobbyController(SceneManager.getLobbyModel()));
    } else {
      Alert alert = new Alert(Alert.AlertType.ERROR);

      alert.setHeaderText(null);
      alert.setContentText(server.getLastError());
      alert.show();
    }
  }
}
