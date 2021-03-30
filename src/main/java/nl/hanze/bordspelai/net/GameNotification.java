package nl.hanze.bordspelai.net;

import java.util.*;

public class GameNotification {

    // TODO: Verbeteren...
    private String notificationType;
    private String data;

    public GameNotification(String message) {
        parse(message);
    }

    private void parse(String message) {
        String[] split = message.split(" ", 4);

        if (!split[0].equals("SVR") || split.length < 4 || split[1].equals("HELP")) {
            return;
        }

        for (int i = 0; i < split.length; i++) {
            if (split[i].equals("SVR") || split[i].equals("GAME")) {
                continue;
            }

            if (i == 2) {
                notificationType = split[i];
            } else if (i == 3) {
                data = split[i];
            }
        }
    }

    public String getNotificationType() {
        return notificationType;
    }

    public boolean isMap() {
        if (data == null) {
            return false;
        }

        return data.contains("{");
    }

    public boolean isList() {
        if (data == null) {
            return false;
        }

        return data.contains("[");
    }

    public Map<String, String> getDataMap() {
        if (isMap()) {
            Map<String, String> map = new HashMap<>();
            String dataString = data.replace("{", "").replace("}", "");
            String[] split = dataString.split(", ");

            for (String arg : split) {
                String[] argSplit = arg.split(": ");

                if (argSplit.length == 2) {
                    String key = argSplit[0].replace("\"", "");
                    String value = argSplit[1].replace("\"", "");

                    map.put(key, value);
                }
            }

            return map;
        } else {
            return null;
        }
    }

    public List<String> getDataList() {
        if (isList()) {
            String dataString = data.replace("[", "").replace("]", "").replace("\"", "");
            String[] split = dataString.split(", ");

            return new ArrayList<>(Arrays.asList(split));
        } else {
            return null;
        }
    }
}
