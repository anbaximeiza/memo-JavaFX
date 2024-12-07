package anbaximeiza.memo.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.text.Font;

enum MessageType {
    ERROR,
    SUCCESS,
    WARNNING
}

public class MessagePane {
    public static AnchorPane getAnchorPane(String message, MessageType type) {
        AnchorPane result = new AnchorPane();
        result.setPrefSize(238.4, 80.8);
        result.setMaxHeight(80.8);
        result.setMaxWidth(238.4);

        Label messageLabel = new Label();
        messageLabel.setText(message);
        messageLabel.setAlignment(Pos.TOP_LEFT);
        messageLabel.setLayoutX(81);
        messageLabel.setLayoutY(12);
        messageLabel.setFont(Font.font("Arial", 15));
        messageLabel.setWrapText(true);

        // placeholder for image

        result.getChildren().add(messageLabel);
        result.setLayoutX(881);
        result.setLayoutY(476);
        
        //
        switch (type) {
            case ERROR:
                result.setStyle(
                        "-fx-border-color: red; -fx-border-radius: 15; -fx-background-color: transparent; -fx-border-width: 3;");
                break;
            case SUCCESS:
                result.setStyle(
                        "-fx-border-color: green; -fx-border-radius: 15; -fx-background-color: transparent; -fx-border-width: 3;");
            default:// left for warning
                break;
        }

        return result;
    }
}
