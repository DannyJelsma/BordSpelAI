package nl.hanze.bordspelai;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.hanze.bordspelai.controllers.GameController;
import nl.hanze.bordspelai.controllers.LoginController;
import nl.hanze.bordspelai.events.NetEventManager;
import nl.hanze.bordspelai.listeners.ChallengeReceiveListener;
import nl.hanze.bordspelai.listeners.NetMessageListener;
import nl.hanze.bordspelai.listeners.PlayerListUpdateListener;
import nl.hanze.bordspelai.managers.SceneManager;
import nl.hanze.bordspelai.models.LoginModel;
import nl.hanze.bordspelai.models.ReversiModel;
import nl.hanze.bordspelai.models.TicTacToeModel;
import nl.hanze.bordspelai.net.Server;
import nl.hanze.bordspelai.notifications.Notification;

import java.util.concurrent.ForkJoinPool;

public class BordspelAI extends Application {
    private static final ForkJoinPool pool = ForkJoinPool.commonPool();
    private static final Server server = new Server("95.216.161.219", 7789);

    public static void main(String[] args) {
        NetEventManager netEventMgr = NetEventManager.getInstance();
        netEventMgr.register(new ChallengeReceiveListener());
        netEventMgr.register(new NetMessageListener());
        netEventMgr.register(new PlayerListUpdateListener(SceneManager.getLobbyModel()));

        if (!server.connect()) {
            throw new IllegalStateException("Could not connect to the server.");
        }

        getThreadPool().submit(() -> Application.launch(args));
        // Application.launch(args);

        // Moet als laatste runnen!
        //noinspection InfiniteLoopStatement
        while (true) {
            if (getServer().isReaderReady()) {
                Notification notification = server.waitForNotifications();

                netEventMgr.notify(notification);
            }
        }
    }

    public static Server getServer() {
        return server;
    }

    public static ForkJoinPool getThreadPool() {
        return pool;
    }

    @Override
    public void start(Stage stage) {
        SceneManager.setStage(stage);
        // SceneManager.switchScene("/views/login.fxml", new LoginController(new LoginModel()));

        SceneManager.switchScene("/views/game.fxml", new GameController(new TicTacToeModel()));
    }
}
