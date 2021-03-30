package nl.hanze.bordspelai.net;

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
    private String lastError;

    public Server(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public boolean connect() {
        try {
            this.socket = new Socket(ip, port);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

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

    public boolean sendCommand(ClientCommand command, String... params) {
        try {
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
            String reply = in.readLine();

            if (reply.equalsIgnoreCase("OK")) {
                return true;
            } else {
                lastError = reply.split(" ", 2)[1];
                return false;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public String getLastError() {
        return lastError;
    }

    public GameNotification waitForNotifications() {
        try {
            return new GameNotification(in.readLine());
        } catch (IOException e) {
            return null;
        }
    }
}
