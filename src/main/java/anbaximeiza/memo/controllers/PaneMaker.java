package anbaximeiza.memo.controllers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import anbaximeiza.memo.ContentCell;;

enum MessageType {
    ERROR,
    SUCCESS,
    WARNNING
}

public class PaneMaker {
    public AnchorPane getAnchorPane(String message, MessageType type) {
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

    public Tab getContentTab(String name){
        Tab result = new Tab(name);
        Random rand = new Random();

        ScrollPane scrollPane = new ScrollPane();
        GridPane gridPane = new GridPane();
        scrollPane.setLayoutX(0);
        scrollPane.setLayoutY(0);
        scrollPane.setMaxSize(672, 530.4);
        scrollPane.setMinHeight(530.4);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(530);
        gridPane.setPrefWidth(656.8);
        gridPane.setLayoutX(0);
        gridPane.setLayoutY(0);
        scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
        
        String hex = String.format("#%02x%02x%02x", rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
        gridPane.setStyle("-fx-background-color: "+hex+";");

        ColumnConstraints cc = new ColumnConstraints();
        cc.setPercentWidth(20);
        gridPane.getColumnConstraints().addAll(cc,cc,cc,cc,cc);
        gridPane.getRowConstraints().add(new RowConstraints(130));

        //gridPane.setGridLinesVisible(true);

        
        scrollPane.setContent(gridPane);
        result.setContent(scrollPane);
        return result;
    }

    public AnchorPane getContentHolder() throws MalformedURLException, IOException{
        return FXMLLoader.load(getClass().getResource("/fxml/contentHolderTemp.fxml"));
    }

    public ContentCell getContentCell() throws MalformedURLException, IOException{
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
        String formattedDate = currentDate.format(formatter);
        return new ContentCell(getContentHolder(),formattedDate);
    }

    public AnchorPane getContentCellZoomUp() throws IOException{
        return FXMLLoader.load(getClass().getResource("/fxml/contentCellZoomUp.fxml"));
    }
}
