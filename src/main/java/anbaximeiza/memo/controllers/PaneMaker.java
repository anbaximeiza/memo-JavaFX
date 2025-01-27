package anbaximeiza.memo.controllers;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import anbaximeiza.memo.ContentCell;
import anbaximeiza.memo.SubGoal;;

enum MessageType {
    ERROR,
    SUCCESS,
    WARNNING
}

public class PaneMaker {
    private static final String TAB_DRAG_KEY = "anchorpane";
    ObjectProperty<AnchorPane> draggingTab;
    public PaneMaker(){
        draggingTab = new SimpleObjectProperty<>();
    }

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
        messageLabel.setMinHeight(59.2);
        messageLabel.setMaxHeight(59.2);
        messageLabel.setMinWidth(150.4);
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
                        "-fx-padding: 2;-fx-border-color: red; -fx-border-radius: 15; -fx-background-color: white; -fx-border-width: 3;");
                break;
            case SUCCESS:
                result.setStyle(
                        "-fx-padding: 2; -fx-border-color: green; -fx-border-radius: 15; -fx-background-color: white; -fx-border-width: 3;");
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        String formattedDate = currentDate.format(formatter);
        return new ContentCell(getContentHolder(),formattedDate);
    }

    public AnchorPane getContentCellZoomUp() throws IOException{
        return FXMLLoader.load(getClass().getResource("/fxml/contentCellZoomUp.fxml"));
    }

    public AnchorPane getNewProjectCell(String name) throws IOException{
        AnchorPane result = FXMLLoader.load(getClass().getResource("/fxml/projectCell.fxml"));
        ((Label)result.getChildren().get(0)).setText(name);
        makeDraggable(result);
        return result;
    }

    public AnchorPane getSubGoalCell(SubGoal goal) throws IOException{
        String content;
        Boolean selected;
        String priority;
        if (goal == null){
            content = "double click to edit...";
            selected = false;
            priority = "4";
        } else {
            content = goal.getContent();
            selected = goal.isCompleted();
            priority = Integer.toString(goal.getPriority());
        }

        AnchorPane result = FXMLLoader.load(getClass().getResource("/fxml/subGoalCell.fxml"));
        ((Label)result.getChildren().get(1)).setText(content);
        ((CheckBox)result.getChildren().get(2)).setSelected(selected);
        ((Label)((AnchorPane)result.getChildren().get(7)).getChildren().get(6)).setText("current: "+priority);
        Image temp =  new Image(getClass().getResourceAsStream("/img/"+ priority+"_icon.png"));
        ((ImageView)result.getChildren().get(4)).setImage(temp);
        result.setId(priority);
        makeDraggable(result);
        return result;
    }


    //used to load existing record
    public void loadSubGoalVBox(VBox holder, ContentCell cell) throws IOException{
        ArrayList<SubGoal> temp = cell.getSubGoals();
        if (temp.size()==0){
            return;
        }
        for(int i = 0; i< temp.size(); i++){
           holder.getChildren().add( getSubGoalCell(temp.get(i)));
        }
    }


    //Amazing code from stackoverflow, slightly modified. source:
    //https://stackoverflow.com/questions/18929161/how-to-move-items-with-in-vboxchange-order-by-dragging-in-javafx
    public void makeDraggable(AnchorPane result){
        result.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                final Dragboard dragboard = event.getDragboard();
                if (dragboard.hasString()
                        && TAB_DRAG_KEY.equals(dragboard.getString())
                        && draggingTab.get() != null) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    event.consume();
                }
            }
        });
        result.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(final DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasString()) {
                    Pane parent = (Pane) result.getParent();
                    Object source = event.getGestureSource();
                    int sourceIndex = parent.getChildren().indexOf(source);
                    int targetIndex = parent.getChildren().indexOf(result);
                    List<Node> nodes = new ArrayList<Node>(parent.getChildren());
                    if (sourceIndex < targetIndex) {
                        Collections.rotate(
                                nodes.subList(sourceIndex, targetIndex + 1), -1);
                    } else {
                        Collections.rotate(
                                nodes.subList(targetIndex, sourceIndex + 1), 1);
                    }
                    parent.getChildren().clear();
                    parent.getChildren().addAll(nodes);
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
        result.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard dragboard = result.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(TAB_DRAG_KEY);
                dragboard.setContent(clipboardContent);
                draggingTab.set(result);
                event.consume();
            }
        }); 
    }
}
