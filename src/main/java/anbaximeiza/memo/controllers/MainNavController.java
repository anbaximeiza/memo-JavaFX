package anbaximeiza.memo.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

import anbaximeiza.memo.ContentCell;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class MainNavController implements Initializable{

    @FXML private ListView<String> projectList;
    @FXML private AnchorPane mainNavPane;
    @FXML private TextField nameInputTextField;
    @FXML private VBox messageBox;
    @FXML private TabPane contentDisplayPane;
    @FXML private AnchorPane plusSignPane;
    @FXML private ScrollBar contentScrollBar;

    HashSet<String> projectNameSet;
    HashSet<String> openedProjectSet;
    HashMap<String,Tab> projectTabMap;
    HashMap<String,ArrayList<ContentCell>> projectContentMap;
    AnchorPane contentZoomUpPane;
    String previousName;
    PaneMaker paneMaker;
    ContentCell selectedCell;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneMaker = new PaneMaker();
        projectNameSet = new HashSet<>();
        projectTabMap = new HashMap<>();
        openedProjectSet = new HashSet<>(); 
        projectContentMap = new HashMap<>();
        contentDisplayPane.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<Tab>() {
                @Override
                public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                    if (newValue == null){
                        return;
                    }
                    KeyFrame f1 = new KeyFrame(Duration.millis(250), e-> updateAndBindScrollBar((ScrollPane)newValue.getContent()));
                    Timeline ss = new Timeline(f1);
                    ss.play();
                }
            }
        );
        try {
            contentZoomUpPane=paneMaker.getContentCellZoomUp();
            contentZoomUpPane.setVisible(false);
            contentZoomUpPane.setLayoutX(202);
            mainNavPane.getChildren().add(contentZoomUpPane);
            contentZoomUpPane.getChildren().get(7).setOnMouseClicked(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                   onContentDisplayCellClose();
                }
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }

     //When the plus sign on the top of the ListView is clicked
    public void addProjectList(String itemName){
        projectList.getItems().add(itemName);
        Tab contentPane = paneMaker.getContentTab(itemName);
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
        projectContentMap.put(itemName, new ArrayList<ContentCell>());
        
    }

    public void createNewProjectName(){
        projectList.setEditable(true);
        projectList.setCellFactory(TextFieldListCell.forListView());
        addProjectList(getDefaultName());
        projectList.scrollTo(projectList.getItems().size()-1);
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
    //planning to only allow user to edit the name when the tab is opened
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

    //When the ListView for displaying the project names is clicked
    //save the previous name so it can be restored
    public void onClickProjectName(){
        previousName = projectList.getSelectionModel().getSelectedItem();
        if (previousName == null){
            return;
        }
        plusSignPane.setVisible(true);
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
        AnchorPane messagePane = paneMaker.getAnchorPane(message, type);
        messageBox.getChildren().add(messagePane);

        KeyFrame f1 = new KeyFrame(Duration.millis(0), e -> messageSlideIn(messagePane));
        KeyFrame f2 = new KeyFrame(Duration.millis(1500), e -> messageSlideOut(messagePane));
        KeyFrame f3 = new KeyFrame(Duration.millis(1850), e -> messageBox.getChildren().remove(0));
        Timeline display =  new Timeline(f1,f2,f3);
        display.play();

    }


    //When the blue plus sign on the down left corner of the content display pane is clicked
    public void onClickNewNote() throws MalformedURLException, IOException{
        Tab currentTab = contentDisplayPane.getSelectionModel().getSelectedItem();
        ScrollPane tempPane = ((ScrollPane)currentTab.getContent());
        GridPane selectedPane = (GridPane)tempPane.getContent();
        int temp = projectContentMap.get(currentTab.getText()).size();
        ContentCell ugood = paneMaker.getContentCell();
        ugood.selfUpdate(selectedPane.getChildren().size()+1);
        ((ImageView)ugood.getHolder().getChildren().get(0)).setId(currentTab.getText());
        ugood.getHolder().getChildren().get(8).setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                AnchorPane selectedPane = (AnchorPane) ((Rectangle)event.getSource()).getParent();
                String key = selectedPane.getChildren().get(0).getId();
                int col = GridPane.getColumnIndex(selectedPane);
                int row = GridPane.getRowIndex(selectedPane);
                selectedCell = projectContentMap.get(key).get(row*5+col);
                onContentDisplayCellClicked();

            }
            
        });
        if (temp%5 == 0){
            selectedPane.getRowConstraints().add(new RowConstraints(130));
            KeyFrame f1 = new KeyFrame(Duration.millis(80), e->setContentScrollBarVisAmount(tempPane));
            Timeline ss = new Timeline(f1);
            ss.play();
        }
        selectedPane.add(ugood.getHolder(),temp%5,temp/5);
        projectContentMap.get(currentTab.getText()).add(ugood);
    }

    //needs to be called with a delay otherwise when the scrollpane is not fully loaded
    //will not work
    public void setContentScrollBarVisAmount(ScrollPane currentPane){
        ScrollBar temp = (ScrollBar)currentPane.getChildrenUnmodifiable().get(1);
        contentScrollBar.setVisibleAmount(temp.getVisibleAmount());
        if (contentScrollBar.getVisibleAmount()<=1){
            contentScrollBar.setVisible(true);
        } else{
            contentScrollBar.setVisible(false);
        }
    }

    public void updateAndBindScrollBar(ScrollPane currentPane){
        currentPane.vvalueProperty().bindBidirectional(contentScrollBar.valueProperty());
        setContentScrollBarVisAmount(currentPane);
    }


    public void onContentDisplayCellClose(){
        selectedCell.setEndDate(((Label)contentZoomUpPane.getChildren().get(2)).getText());
        selectedCell.setMainGoal(((Label)contentZoomUpPane.getChildren().get(4)).getText());
        selectedCell.setMainGoalSpec(((Label)contentZoomUpPane.getChildren().get(5)).getText());

        contentZoomUpPane.setVisible(false);
    }

    //1.created date    2.deadline      3.reset button      4.main goal title
    //5.main goal specification     6.Scrollpane for sub goals      7.ImageView close icon

    public void onContentDisplayCellClicked(){
        contentZoomUpPane.setVisible(true);

        ((Label)contentZoomUpPane.getChildren().get(1)).setText("Created on: "+selectedCell.getCreateDate());
        ((Label)contentZoomUpPane.getChildren().get(2)).setText("Deadline: "+selectedCell.getEndDate());
        ((Label)contentZoomUpPane.getChildren().get(4)).setText(selectedCell.getMainGoal());
        ((Label)contentZoomUpPane.getChildren().get(5)).setText(selectedCell.getMainGoalSpec());
    }
}
