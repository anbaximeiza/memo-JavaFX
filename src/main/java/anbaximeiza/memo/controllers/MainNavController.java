package anbaximeiza.memo.controllers;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainNavController implements Initializable{

    @FXML private ListView<String> projectList;
    @FXML private AnchorPane toolPane;
    @FXML private TextField nameInputTextField;
    @FXML private VBox messageBox;


    int projectCount = 0;
    HashSet<String> projectNameSet;
    String previousName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectNameSet = new HashSet<>();
    }

    
    public void createNewProjectName(){
        projectList.setEditable(true);
        projectList.setCellFactory(TextFieldListCell.forListView());
        addProjectList(getDefaultName());
        projectList.scrollTo(projectCount);
        projectCount++;
    }

    public void addProjectList(String itemName){
        projectList.getItems().add(itemName);
    }

    public String getDefaultName(){
        int temp = 0;
        while (projectNameSet.contains("Untitled-" + String.valueOf(temp))) {
            temp++;
        }
        projectNameSet.add("Untitled-" + String.valueOf(temp));
        return "Untitled-" + String.valueOf(temp);
    }

    public void onCancelProjectName(ListView.EditEvent<String> event){
        String newName = event.getNewValue();
        if (newName==null){
            displayMessage("Name cannot be empty!", MessageType.ERROR);
            projectList.getItems().set(event.getIndex(), previousName);
            return;
        }
        if (newName.strip().equals("")) {
            displayMessage("Name must not be all space!", MessageType.ERROR);
            projectList.getItems().set(event.getIndex(), previousName);
            return;
        }
        if (projectNameSet.contains(newName)){
            displayMessage("Name already exists!", MessageType.ERROR);
            projectList.getItems().set(event.getIndex(), previousName);
        } else{
            projectNameSet.remove(previousName);
            projectList.getItems().set(event.getIndex(), newName);
            projectNameSet.add(newName);
        }
    }

    public void onClickProjectName(){
        previousName = projectList.getSelectionModel().getSelectedItem();
    }

    public void messageSlideIn(AnchorPane message){
        TranslateTransition temp =  new TranslateTransition(Duration.millis(300));
        temp.setByX(-250);
        temp.setNode(message);
        temp.play();
    }

    public void messageSlideOut(AnchorPane message){
        TranslateTransition temp =  new TranslateTransition(Duration.millis(300));
        temp.setByX(250);
        temp.setNode(message);
        temp.play();
    }


    public void displayMessage(String message, MessageType type ){
        AnchorPane messagePane = MessagePane.getAnchorPane(message, type);
        messageBox.getChildren().add(messagePane);

        KeyFrame f1 = new KeyFrame(Duration.millis(0), e -> messageSlideIn(messagePane));
        KeyFrame f2 = new KeyFrame(Duration.millis(1500), e -> messageSlideOut(messagePane));
        KeyFrame f3 = new KeyFrame(Duration.millis(1850), e -> messageBox.getChildren().remove(0));
        Timeline display =  new Timeline(f1,f2,f3);
        display.play();

    }
    
}
