package nl.hanze.bordspelai.net;

import nl.hanze.bordspelai.notifications.GameNotification;
import nl.hanze.bordspelai.notifications.Notification;
import nl.hanze.bordspelai.notifications.ResultNotification;
import nl.hanze.bordspelai.notifications.ServerNotification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Server {

    private final String ip;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean connect() {
        try {
            this.socket = new Socket(ip, port);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // Get rid of copyright
            in.readLine();
            in.readLine();

            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception ignored) {
        }
    }

    public void sendCommand(Command command, String... params) {
        //try {
        StringBuilder commandBuilder = new StringBuilder(command.getCommand() + " ");

        if (params != null) {
            for (String param : params) {
                if (params.length > 1) {
                    commandBuilder.append("\"");
                }

                commandBuilder.append(param);

                if (params.length > 1) {
                    commandBuilder.append("\"");
                }

                commandBuilder.append(" ");
            }
        }

        String commandString = commandBuilder.toString().trim();
        out.println(commandString);
        System.out.println("> " + commandString);
/*            String reply = in.readLine();

            System.out.println("< " + reply);

            if (reply.equals("OK")) {
                return true;
            } else if (reply.startsWith("ERR")) {
                lastError = reply.split(" ", 2)[1];
                return false;
            } else {
                // Voor het geval berichten in de verkeerde volgorde binnen komen.
                NetEventManager manager = NetEventManager.getInstance();
                if (reply.startsWith("SVR GAME")) {
                    manager.notify(new GameNotification(reply));
                } else if (reply.startsWith("SVR")) {
                    manager.notify(new ServerNotification(reply));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }

    public Notification waitForNotifications() {
        String message = waitForMessage();

        if (message.startsWith("SVR GAME")) {
            return new GameNotification(message);
        } else if (message.startsWith("SVR")) {
            System.out.println(message);
            return new ServerNotification(message);
        } else if (message.startsWith("OK") || message.startsWith("ERR")) {
            return new ResultNotification(message);
        }

        return null;
    }

    public String waitForMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
