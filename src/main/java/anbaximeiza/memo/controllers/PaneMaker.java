package anbaximeiza.memo.controllers;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Random;
import java.util.random.*;;

enum MessageType {
    ERROR,
    SUCCESS,
    WARNNING
}

public class PaneMaker {
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
        messageLabel.setMaxHeight(59.2);
        messageLabel.setMaxWidth(150.4);
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
                        "-fx-padding: 2;-fx-border-color: red; -fx-border-radius: 15; -fx-background-color: transparent; -fx-border-width: 3;");
                break;
            case SUCCESS:
                result.setStyle(
                        "-fx-padding: 2; -fx-border-color: green; -fx-border-radius: 15; -fx-background-color: transparent; -fx-border-width: 3;");
            default:// left for warning
                break;
        }

        return result;
    }

    public static Tab getContentTab(String name){
        Tab result = new Tab(name);
        Random rand = new Random();

        ScrollPane scrollPane = new ScrollPane();
        AnchorPane anchorPane = new AnchorPane();
        scrollPane.setLayoutX(0);
        scrollPane.setLayoutY(0);

        String hex = String.format("#%02x%02x%02x", rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        anchorPane.setStyle("-fx-background-color: "+hex+";");
        anchorPane.setLayoutX(0);
        anchorPane.setLayoutY(0);
        anchorPane.setPrefHeight(100);
        anchorPane.setPrefWidth(100);
        scrollPane.setContent(anchorPane);

        result.setContent(scrollPane);
        return result;
    }
}
