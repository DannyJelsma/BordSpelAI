package nl.hanze.bordspelai.net;

import nl.hanze.bordspelai.enums.Command;
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
    private PrintWriter out;
    private BufferedReader in;
    private boolean waitingForCommand = false;
    private String lastError;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;

        connect();
    }

    public boolean connect() {
        try {
            Socket socket = new Socket(ip, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Get rid of copyright notice
            in.readLine();
            in.readLine();

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getLastError() {
        return lastError;
    }

    public boolean sendCommand(Command command, String... params) {
        try {
            waitingForCommand = true;
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
            String reply = in.readLine();
            System.out.println("> " + commandString);

            System.out.println("< " + reply);

            if (reply.equals("OK")) {
                return true;
            } else if (reply.startsWith("ERR")) {
                lastError = reply.split(" ", 2)[1];
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            waitingForCommand = false;
        }

        return false;
    }

    public boolean isReaderReady() {
        try {
            return in.ready() && !waitingForCommand;
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return false;
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
