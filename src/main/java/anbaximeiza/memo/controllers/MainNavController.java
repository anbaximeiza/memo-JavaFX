package anbaximeiza.memo.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainNavController implements Initializable{

    @FXML private ListView<String> projectList;
    @FXML private AnchorPane toolPane;
    @FXML private TextField nameInputTextField;
    @FXML private VBox messageBox;
    @FXML private TabPane contentDisplayPane;
    @FXML private AnchorPane plusSignPane;

    HashSet<String> projectNameSet;
    HashSet<String> openedProjectSet;
    HashMap<String,Tab> projectTabMap;
    HashMap<String,ArrayList<Label>> projectContentMap;
    String previousName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectNameSet = new HashSet<>();
        projectTabMap = new HashMap<>();
        openedProjectSet = new HashSet<>(); 
        projectContentMap = new HashMap<>();
    }

    
    public void createNewProjectName(){
        projectList.setEditable(true);
        projectList.setCellFactory(TextFieldListCell.forListView());
        addProjectList(getDefaultName());
        projectList.scrollTo(projectList.getItems().size()-1);
    }

    //need to work on further: create a new project space when the name is created
    public void addProjectList(String itemName){
        projectList.getItems().add(itemName);
        Tab contentPane = PaneMaker.getContentTab(itemName);
        contentPane.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Tab temp = (Tab) event.getSource();
                openedProjectSet.remove(temp.getText());
                if (contentDisplayPane.getTabs().size()==0){
                    plusSignPane.setVisible(false);
                }
            }
            
        });
        projectTabMap.put(itemName, contentPane);
        projectContentMap.put(itemName, new ArrayList<Label>());
        
    }

    //generate a default name
    public String getDefaultName(){
        int temp = 0;
        while (projectNameSet.contains("Untitled-" + String.valueOf(temp))) {
            temp++;
        }
        projectNameSet.add("Untitled-" + String.valueOf(temp));
        return "Untitled-" + String.valueOf(temp);
    }

    //when the user exit editing the name
    public void onCancelProjectName(ListView.EditEvent<String> event){
        String newName = event.getNewValue();
        if (newName==null){//when no change is detected
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
            if (openedProjectSet.contains(previousName)){
                openedProjectSet.remove(previousName);
                openedProjectSet.add(newName);
            }
            projectNameSet.remove(previousName);
            projectTabMap.put(newName, projectTabMap.remove(previousName));
            projectList.getItems().set(event.getIndex(), newName);
            projectNameSet.add(newName);
            
        }
    }

    //save the previous name so it can be restored
    public void onClickProjectName(){
        plusSignPane.setVisible(true);
        previousName = projectList.getSelectionModel().getSelectedItem();
        if (!openedProjectSet.contains(previousName)){
            Tab temp = projectTabMap.get(previousName);
            contentDisplayPane.getTabs().add(temp);
            openedProjectSet.add(previousName);
        }

    }


    //message animations
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


    //Called when a message is needed to be displayed(pop up on the down right corner)
    public void displayMessage(String message, MessageType type ){
        AnchorPane messagePane = PaneMaker.getAnchorPane(message, type);
        messageBox.getChildren().add(messagePane);

        KeyFrame f1 = new KeyFrame(Duration.millis(0), e -> messageSlideIn(messagePane));
        KeyFrame f2 = new KeyFrame(Duration.millis(1500), e -> messageSlideOut(messagePane));
        KeyFrame f3 = new KeyFrame(Duration.millis(1850), e -> messageBox.getChildren().remove(0));
        Timeline display =  new Timeline(f1,f2,f3);
        display.play();

    }

    public void onClickNewNote(){
        Tab currentTab = contentDisplayPane.getSelectionModel().getSelectedItem();
        GridPane selectedPane = (GridPane)((ScrollPane)currentTab.getContent()).getContent();
        int temp = projectContentMap.get(currentTab.getText()).size();
        Label ugood = new Label("You good over there?");
        if (temp%5 == 0){
            selectedPane.getRowConstraints().add(new RowConstraints(130));
        }
        selectedPane.add(ugood,temp%5,temp/5);
        projectContentMap.get(currentTab.getText()).add(ugood);
    }
    
}
