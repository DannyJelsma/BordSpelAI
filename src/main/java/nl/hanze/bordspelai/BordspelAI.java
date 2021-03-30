package nl.hanze.bordspelai;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.hanze.bordspelai.events.NetEventManager;
import nl.hanze.bordspelai.listeners.ChallengeReceiveListener;
import nl.hanze.bordspelai.listeners.NetMessageListener;
import nl.hanze.bordspelai.net.Command;
import nl.hanze.bordspelai.net.GameNotification;
import nl.hanze.bordspelai.net.Server;

import java.util.concurrent.ForkJoinPool;

public class BordspelAI extends Application {

    private static final ForkJoinPool pool = ForkJoinPool.commonPool();
    private static final Server server = new Server("95.216.161.219", 7789);

    public static void main(String[] args) {
        // javafx
        launch(args);

        NetEventManager netEventMgr = NetEventManager.getInstance();
        netEventMgr.register(new ChallengeReceiveListener());
        netEventMgr.register(new NetMessageListener());

        if (server.connect()) {
            server.sendCommand(Command.LOGIN, "client");
            server.sendCommand(Command.SUBSCRIBE, "Tic-tac-toe");
        }

        getThreadPool().submit(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                GameNotification notification = server.waitForNotifications();

                netEventMgr.notify(notification);
            }
        }).join();
    }

    public static Server getServer() {
        return server;
    }

    public static ForkJoinPool getThreadPool() {
        return pool;
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("stfu");
        stage.show();
    }
}
