package nl.hanze.bordspelai;

import nl.hanze.bordspelai.net.ClientCommand;
import nl.hanze.bordspelai.net.Server;
import nl.hanze.bordspelai.net.ServerReply;

public class BordspelAI {

    public static void main(String[] args) {
        Server server = new Server("95.216.161.219", 7789);

        if (server.connect()) {
            server.sendCommand(ClientCommand.LOGIN, "client");
            server.sendCommand(ClientCommand.SUBSCRIBE, "Tic-tac-toe");

            if (!server.sendCommand(ClientCommand.CHALLENGE, "test", "Tic-tac-toe")) {
                System.out.println("ERROR: " + server.getLastError());
            }
        }

        while (true) {
            // Debugging
            printServerReply(server);
        }
    }

    // Debugging
    public static void printServerReply(Server server) {
        ServerReply reply = server.waitForServerReply();

        if (reply.isList()) {
            System.out.println(reply.getReplyType() + ": " + reply.getDataList());
        }

        if (reply.isMap()) {
            System.out.println(reply.getReplyType() + ": " + reply.getDataMap());
        }
    }
}
