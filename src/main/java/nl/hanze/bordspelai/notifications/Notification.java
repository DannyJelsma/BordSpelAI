package nl.hanze.bordspelai.notifications;

import java.util.*;

public abstract class Notification {

    public Notification(String message) {
        parse(message);
    }

    protected abstract String getData();

    protected abstract void parse(String message);

    public abstract String getNotificationType();

    public boolean isMap() {
        if (getData() == null) {
            return false;
        }

        return getData().contains("{");
    }

    public boolean isList() {
        if (getData() == null) {
            return false;
        }

        return getData().contains("[");
    }

    public Map<String, String> getDataMap() {
        if (isMap()) {
            Map<String, String> map = new HashMap<>();
            String dataString = getData().replace("{", "").replace("}", "");
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
            String dataString = getData().replace("[", "").replace("]", "").replace("\"", "");
            String[] split = dataString.split(", ");

            return new ArrayList<>(Arrays.asList(split));
        } else {
            return null;
        }
    }
}
