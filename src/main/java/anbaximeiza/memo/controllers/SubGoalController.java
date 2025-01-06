package anbaximeiza.memo.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SubGoalController implements Initializable{

    @FXML AnchorPane root;
    @FXML Label contentLabel;
    @FXML Label leftLabel;
    @FXML Label rightLabel;
    @FXML CheckBox completedBox;
    @FXML ImageView leftImage;
    @FXML ImageView rightImage;
    @FXML AnchorPane priorityPane;
        @FXML Label priorityLabel;
    @FXML TextArea contentEditingEntry;

    String priority;
    Boolean delete = false;
    EventHandler<Event> temp;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        temp = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
               Node temp = root.getParent();
                    ((VBox)temp).getChildren().remove(root);
               }
        };
        KeyFrame f1 = new KeyFrame(Duration.millis(100), e->{
            priority = root.getId();
        });
        Timeline tl = new Timeline(f1);
        tl.play();
    }
    
    public void onLeftImageClicked(){
        priorityPane.setVisible(true);
    }

    public void onPriorityClicked(Event e){
        priority = ((Node)e.getSource()).getId();
        iconChange(leftImage, priority);
        priorityLabel.setText("current: "+ priority);
        root.setId(priority);
        priorityPane.setVisible(false);
    }

    public void iconChange(ImageView node, String name){
        Image temp =  new Image(getClass().getResourceAsStream("/img/"+ name+"_icon.png"));
        node.setImage(temp);
    }

    public void onContentLabelClicked(MouseEvent event){
        if (event.getClickCount() ==2){
            contentEditingEntry.setVisible(true);
            contentEditingEntry.setText(contentLabel.getText());
            contentEditingEntry.requestFocus();
            contentEditingEntry.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode().equals(KeyCode.ENTER)){
                        contentLabel.setText(contentEditingEntry.getText());
                        contentEditingEntry.setVisible(false);
                        contentEditingEntry.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    }
                }  
            });
        }
    }

    public void onRightImageClicked(){
        if (!delete){
            delete = true;
            iconChange(leftImage, "yes");
            leftLabel.setText("Confirm?");
            rightLabel.setText("Cancel");
            leftImage.setOnMouseClicked(temp);
        } else {
            delete = false;
            iconChange(leftImage, priority);
            leftLabel.setText("Priority");
            rightLabel.setText("Delete");
            leftImage.removeEventHandler(MouseEvent.MOUSE_CLICKED, temp);
            leftImage.removeEventHandler(MouseEvent.MOUSE_CLICKED, temp);
        }
    }    

}
