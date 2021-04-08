package nl.hanze.bordspelai.builder;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AlertBuilder {

    private String title = null;
    private String header = null;
    private String content = null;
    private final List<ButtonType> buttons = new ArrayList<>();
    private Alert.AlertType type;

    public AlertBuilder(Alert.AlertType type) {
        this.type = type;
    }

    public Alert build() {
        Alert alert = new Alert(type);

        alert.setTitle(title);
        alert.setContentText(content);
        alert.setHeaderText(header);

        if (!buttons.isEmpty()) {
            alert.getButtonTypes().setAll(buttons);
        }

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.setAlwaysOnTop(true);

        return alert;
    }

    public String getTitle() {
        return title;
    }

    public AlertBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getHeader() {
        return header;
    }

    public AlertBuilder setHeader(String header) {
        this.header = header;
        return this;
    }

    public String getContent() {
        return content;
    }

    public AlertBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public AlertBuilder addButton(ButtonType buttonType) {
        buttons.add(buttonType);
        return this;
    }

    public AlertBuilder addButtons(ButtonType... buttonTypes) {
        Collections.addAll(buttons, buttonTypes);
        return this;
    }

    public Alert.AlertType getType() {
        return type;
    }

    public AlertBuilder setType(Alert.AlertType type) {
        this.type = type;
        return this;
    }
}
