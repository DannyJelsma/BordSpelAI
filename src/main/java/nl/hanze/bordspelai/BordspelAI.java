package nl.hanze.bordspelai;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.hanze.bordspelai.events.NetEventManager;
import nl.hanze.bordspelai.listeners.TestNetListener;
import nl.hanze.bordspelai.net.ClientCommand;
import nl.hanze.bordspelai.net.GameNotification;
import nl.hanze.bordspelai.net.Server;

public class BordspelAI extends Application {

    public static void main(String[] args) {
        // javafx
        //launch(args);

        Server server = new Server("95.216.161.219", 7789);
        NetEventManager netEventMgr = new NetEventManager();
        netEventMgr.register(new TestNetListener());

        if (server.connect()) {
            server.sendCommand(ClientCommand.LOGIN, "client");
            server.sendCommand(ClientCommand.SUBSCRIBE, "Tic-tac-toe");

            if (!server.sendCommand(ClientCommand.CHALLENGE, "test", "Tic-tac-toe")) {
                System.out.println("ERROR: " + server.getLastError());
            }
        }

        while (true) {
            // Debugging
            GameNotification notification = server.waitForNotifications();
            netEventMgr.notify(notification);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("stfu");
        stage.show();
    }
}
