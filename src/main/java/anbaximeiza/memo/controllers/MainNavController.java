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
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class MainNavController implements Initializable{

    @FXML private VBox projectList;
    @FXML private AnchorPane mainNavPane;
    @FXML private VBox messageBox;
    @FXML private TabPane contentDisplayPane;
    @FXML private AnchorPane plusSignPane;
    @FXML private ScrollBar contentScrollBar;

    //use for storing the projects
    HashSet<String> projectNameSet;
    HashMap<String,Tab> projectTabMap;
    HashMap<String,ArrayList<ContentCell>> projectContentMap;

    //for the project that is opened
    HashSet<String> openedProjectSet;

    //display pane on the right of the application
    AnchorPane contentZoomUpPane;

    //helper class for generating the panes
    PaneMaker paneMaker;

    //when a cell in the content display pane is clicked, it becomes the selected cell
    //mainly use for displaying on the zoomUp pane atm
    ContentCell selectedCell;

    //use for the project name cells
    AnchorPane currentEditing;
    AnchorPane currentDeleting;
    EventHandler<KeyEvent> projectEditHandlerKey;
    EventHandler<Event> projectEditHandler;
    EventHandler<Event> projectDeleteHandler;
    @FXML Label deletionWarning;

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

        projectEditHandlerKey =  new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)){
                    onFinishProjectName(currentEditing);
                }
            }   
        };

        projectEditHandler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                onFinishProjectName(currentEditing);
            }
            
        };

        projectDeleteHandler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                projectList.getChildren().remove(currentDeleting);
                String key = currentDeleting.getId();
                projectTabMap.remove(key);
                projectContentMap.remove(key);
                projectNameSet.remove(key);
                openedProjectSet.remove(key);
                currentDeleting = null;
            }
            
        };
        
    }

    //Actual logic of the function beneath
    public void addProjectList(String itemName) throws IOException{
        //generate new panes from the pane maker
        AnchorPane projectCell = paneMaker.getNewProjectCell(itemName);
        projectList.getChildren().add(projectCell);
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
        Platform.runLater(()->{
            resetProjectCellListener(projectCell);

            //handler when the project cell is clicked
            EventHandler<Event> temp = new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    String key = ((Label)event.getSource()).getParent().getId();
                    //only add when the tab is not opened otherwise could trigger null pointer
                    if (!openedProjectSet.contains(key)){
                        contentDisplayPane.getTabs().add(projectTabMap.get(key));
                        openedProjectSet.add(key);
                        plusSignPane.setVisible(true);
                    }
                }
            };

            //handler when the buttons are hovered
            EventHandler<Event> tt = new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    ((Rectangle)event.getSource()).setOpacity(0.6);
                }
                
            };

            //handler when the buttons are unhovered (no specific meaning on naming)
            EventHandler<Event> tte = new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    ((Rectangle)event.getSource()).setOpacity(0);
                }
                            
            };
            projectCell.getChildren().get(0).setOnMouseClicked(temp);
            projectCell.getChildren().get(6).setOnMouseClicked(temp);
            projectCell.getChildren().get(4).setOnMouseEntered(tt);
            projectCell.getChildren().get(5).setOnMouseEntered(tt);
            projectCell.getChildren().get(4).setOnMouseExited(tte);
            projectCell.getChildren().get(5).setOnMouseExited(tte);
        });

        projectTabMap.put(itemName, contentPane);
        projectContentMap.put(itemName, new ArrayList<ContentCell>());
        
    }

    //When the plus sign on the top of the ListView is clicked
    public void createNewProjectName() throws IOException{
        addProjectList(getDefaultName());
    }

    //reset the cell to initial state
    public void resetProjectCellListener(AnchorPane cell){
        //remove the 
        if (currentEditing!=null && currentEditing.equals(cell)){
            currentEditing.removeEventHandler(KeyEvent.KEY_PRESSED, projectEditHandlerKey);
            currentEditing.getChildren().get(4).removeEventHandler(MouseEvent.MOUSE_CLICKED, projectEditHandler);
        }
        if (currentDeleting!=null && currentDeleting.equals(cell)){
            currentDeleting.getChildren().get(4).removeEventHandler(MouseEvent.MOUSE_CLICKED, projectDeleteHandler);
        }
        cell.getChildren().get(6).setOpacity(0);;
        cell.getChildren().get(1).setVisible(false);
        iconChange((ImageView)cell.getChildren().get(2),"rename");
        iconChange((ImageView)cell.getChildren().get(3),"delete");
        cell.getChildren().get(4).setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                AnchorPane parent = (AnchorPane) ((Node)event.getSource()).getParent();
                onRenameButtonClick(parent);
            }
        });
        cell.getChildren().get(5).setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                AnchorPane parent = (AnchorPane) ((Node)event.getSource()).getParent();
                onDeleteButtonClick(parent);
            }
        });
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

    //0: Label project name     1:TextField     2:ImageView rename_icon
    //3: ImageView delete_icon      4:Rectangle for rename      5:Rectangle for delete

    public void onRenameButtonClick(AnchorPane clicked){
        if (currentEditing!=null){
            resetProjectCellListener(currentEditing);
        }
        if (currentDeleting!= null){
            resetProjectCellListener(currentDeleting);
            currentDeleting = null;
        }
        currentEditing = clicked;
        clicked.getChildren().get(1).setVisible(true);
        ((TextField)clicked.getChildren().get(1)).setText(clicked.getId());
        iconChange((ImageView)clicked.getChildren().get(2),"yes");
        iconChange((ImageView)clicked.getChildren().get(3),"undo");
        clicked.getChildren().get(4).setOnMouseClicked(projectEditHandler);
        clicked.setOnKeyPressed(projectEditHandlerKey);
        clicked.getChildren().get(5).setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                resetProjectCellListener(currentEditing);
                currentEditing.getChildren().get(5).removeEventHandler(MouseEvent.MOUSE_CLICKED, this);
            }
        });
    }

    public void onDeleteButtonClick(AnchorPane clicked){
        if (currentEditing!=null){
            resetProjectCellListener(currentEditing);
            currentEditing = null;
        }
        //button being clicked again
        if (currentDeleting!=null && currentDeleting.equals(clicked)){
            resetProjectCellListener(currentDeleting);
            currentDeleting = null;
            return;
        }
        if (currentDeleting != null){
           resetProjectCellListener(currentDeleting);
        }
        currentDeleting =clicked;
        iconChange((ImageView)clicked.getChildren().get(2),"yes");
        clicked.getChildren().get(6).setOpacity(1);
        clicked.getChildren().get(4).setOnMouseClicked(projectDeleteHandler);

    }

    public void iconChange(ImageView node, String imgName){
        Image buffer= new Image(getClass().getResourceAsStream("/img/"+ imgName+"_icon.png"));
        node.setImage(buffer);
    }

    //when the user exit editing the name
    //planning to only allow user to edit the name when the tab is opened
    public void onFinishProjectName(AnchorPane cell){
        String previousName = cell.getId();
        String newName = ((TextField)cell.getChildren().get(1)).getText();
        if (newName.equals(previousName)){//when no change is detected
            resetProjectCellListener(cell);
            return;
        }else if (newName.strip().equals("")) {
            displayMessage("Name must not be all space!", MessageType.ERROR);
        }else if (projectNameSet.contains(newName)){
            displayMessage("Name already exists!", MessageType.ERROR);
        } else{//new name accepted
            if (openedProjectSet.contains(previousName)){
                openedProjectSet.remove(previousName);
                openedProjectSet.add(newName);
            }
            projectNameSet.remove(previousName);
            projectTabMap.put(newName, projectTabMap.remove(previousName));
            projectTabMap.get(newName).setText(newName);
            projectNameSet.add(newName);
            //update on the cell
            cell.setId(newName);
            ((Label)cell.getChildren().get(0)).setText(newName);
        }
        resetProjectCellListener(cell);
        currentEditing = null;
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
                try {
                    onContentDisplayCellClicked();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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
        selectedCell.setEndDate(((Label)contentZoomUpPane.getChildren().get(2)).getText().replaceFirst("^Deadline: ",""));
        selectedCell.setMainGoal(((Label)contentZoomUpPane.getChildren().get(4)).getText());
        selectedCell.setMainGoalSpec(((Label)contentZoomUpPane.getChildren().get(5)).getText());

        contentZoomUpPane.setVisible(false);
    }

    //1.created date    2.deadline      3.reset button      4.main goal title
    //5.main goal specification     6.Scrollpane for sub goals      7.ImageView close icon
    public void onContentDisplayCellClicked() throws IOException{
        contentZoomUpPane.setVisible(true);

        ((Label)contentZoomUpPane.getChildren().get(1)).setText("Created on: "+selectedCell.getCreateDate());
        ((Label)contentZoomUpPane.getChildren().get(2)).setText("Deadline: "+selectedCell.getEndDate());
        ((Label)contentZoomUpPane.getChildren().get(4)).setText(selectedCell.getMainGoal());
        ((Label)contentZoomUpPane.getChildren().get(5)).setText(selectedCell.getMainGoalSpec());
        VBox subGoalBox = (VBox) ((ScrollPane)contentZoomUpPane.getChildren().get(6)).getContent();
        paneMaker.loadSubGoalVBox(subGoalBox, selectedCell);
    }
}
