package anbaximeiza.memo.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SubGoalController implements Initializable{

    @FXML Label contentLabel;
    @FXML CheckBox completedBox;
    @FXML ImageView leftImage;
    @FXML ImageView rightImage;
    @FXML AnchorPane priorityPane;
        @FXML Label priorityLabel;
    @FXML TextField contentEditingEntry;

    String priority;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    public void onLeftImageClicked(){
        priorityPane.setVisible(true);
    }

    public void onPriorityClicked(Event e){
        priority = ((Node)e.getSource()).getId();
        iconChange(leftImage, priority);
        priorityLabel.setText("current: "+ priority);
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

    

}
