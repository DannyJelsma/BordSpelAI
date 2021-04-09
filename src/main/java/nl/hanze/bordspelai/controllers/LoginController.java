package nl.hanze.bordspelai.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import nl.hanze.bordspelai.BordspelAI;
import nl.hanze.bordspelai.enums.Command;
import nl.hanze.bordspelai.enums.GameState;
import nl.hanze.bordspelai.enums.Mode;
import nl.hanze.bordspelai.managers.GameManager;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.net.Server;
import nl.hanze.bordspelai.views.LobbyView;

public class LoginController implements Controller {

  @FXML
  private TextField username;

  @FXML
  private ComboBox<String> mode;

  public LoginController() {

  }

  @FXML
  public void initialize() {
    mode.setItems(FXCollections.observableArrayList("Multiplayer", "Singleplayer"));
    mode.getSelectionModel().selectFirst();
  }

  @FXML
  private void login() {
      Server server = BordspelAI.getServer();
      GameManager manager = GameManager.getInstance();
      Mode selectedMode = Mode.valueOf(mode.getSelectionModel().getSelectedItem().toUpperCase());
      System.out.println("Username: " + username.getText());

      manager.setMode(selectedMode);
      server.sendCommand(Command.LOGIN, username.getText());
      manager.setState(GameState.LOBBY);
      manager.setUsername(username.getText());
      LobbyView view = new LobbyView("/views/lobby.fxml", new LobbyController(SceneManager.getLobbyModel()));
      SceneManager.switchScene(view);
/*  } else {
    Alert alert = new AlertBuilder(Alert.AlertType.ERROR)
            .setTitle("Error!")
            .setContent(server.getLastError())
            .build();

    alert.show();*/
  }
}
