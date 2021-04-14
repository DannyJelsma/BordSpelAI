package nl.hanze.bordspelai;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import nl.hanze.bordspelai.controllers.LoginController;
import nl.hanze.bordspelai.events.NetEventManager;
import nl.hanze.bordspelai.listeners.*;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.net.Server;
import nl.hanze.bordspelai.notifications.Notification;
import nl.hanze.bordspelai.views.LoginView;
import nl.hanze.bordspelai.views.View;

public class BordspelAI extends Application {
    private static Server server = new Server("145.33.225.170", 7789);

    public static void main(String[] args) {
        BordspelAI bordspelAI = new BordspelAI();
        bordspelAI.setup();
        Application.launch(args);
    }

    private void setup() {
        NetEventManager netEventMgr = NetEventManager.getInstance();
        netEventMgr.register(new ChallengeReceiveListener());
        netEventMgr.register(new PlayerListUpdateListener(SceneManager.getLobbyModel()));
        netEventMgr.register(new WinLossListener());
        netEventMgr.register(new MatchStartListener());
        netEventMgr.register(new ErrorListener());

        if (!server.connect()) {
            throw new IllegalStateException("Could not connect to the server.");
        }

        new Thread(() -> {
            while (true) {
                Notification notification = server.waitForNotifications();

                netEventMgr.notify(notification);
            }
        }).start();
    }

    public void changeServer(String ip, int port) {
        server = new Server(ip, port);
        setup();
    }

    public static Server getServer() {
        return server;
    }

    @Override
    public void start(Stage stage) {
        View view = new LoginView("/views/login.fxml", new LoginController());
        SceneManager.setStage(stage);
        stage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        SceneManager.switchScene(view);
    }
}
