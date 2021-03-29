package nl.hanze.bordspelai;

import nl.hanze.bordspelai.net.ClientCommand;
import nl.hanze.bordspelai.net.Server;

public class BordspelAI {

    public static void main(String[] args) {
        Server server = new Server("95.216.161.219", 7789);

        if (server.connect()) {
            server.sendCommand(ClientCommand.LOGIN, "client");
            server.sendCommand(ClientCommand.SUBSCRIBE, "Tic-tac-toe");
            if (!server.sendCommand(ClientCommand.CHALLENGE, "test", "Tic-tac-toe")) {
                // Zou je kunnen displayen in de UI.
                System.out.println("Error while executing this command: " + server.getLastError());
            }
        }

        while (true) {
            // Debug purposes
            System.out.println(server.waitForServerReply());
        }
    }
}
