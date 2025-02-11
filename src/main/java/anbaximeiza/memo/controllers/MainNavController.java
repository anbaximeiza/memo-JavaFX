package anbaximeiza.memo.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

import anbaximeiza.memo.ContentCell;
import anbaximeiza.memo.FileHandler;
import anbaximeiza.memo.SubGoal;
import anbaximeiza.memo.test;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class MainNavController implements Initializable{

    @FXML private VBox projectList;
    @FXML private AnchorPane mainNavPane;
    @FXML private AnchorPane loadingPane;
    @FXML private VBox messageBox;
    @FXML private TabPane contentDisplayPane;
    @FXML private AnchorPane plusSignPane;
    @FXML private ScrollBar contentScrollBar;
    @FXML private Label deletionWarning;
    @FXML private AnchorPane deleteConfirm;

    @FXML private AnchorPane loadingCell;

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
    EventHandler<MouseEvent> projectLockHandler;

    FileHandler fh;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fh = new FileHandler();
        projectContentMap = fh.importExisting();
        projectNameSet = fh.getProjectNameSet();
        projectTabMap = new HashMap<>();
        paneMaker = new PaneMaker();
        try {
            initializeProjectTabMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
        openedProjectSet = new HashSet<>(); 
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
            Platform.runLater(()->{
                mainNavPane.getChildren().add(4,contentZoomUpPane);
            });
            contentZoomUpPane.getChildren().get(7).setOnMouseClicked(new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                   onContentDisplayCellClose();
                }
                
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        projectLockHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                AnchorPane parent =  (AnchorPane) ((Node)event.getSource()).getParent();
                if (event.getClickCount() == 2){
                    parent.getChildren().get(8).removeEventHandler(MouseEvent.MOUSE_CLICKED, projectLockHandler);
                    parent.setId("lock");
                    Tooltip tp = new Tooltip("This project will always be save when application closed.");
                    Tooltip.install((parent.getChildren().get(8)),tp);
                    iconChange((ImageView) parent.getChildren().get(7), "lock");
                    displayMessage(
                        "Project will always be saved when application closed, ", 
                        MessageType.SUCCESS);
                    return;
                }

                if (parent.getId().equals("yes")){
                    parent.setId("no");
                    Tooltip tp = new Tooltip("This project will not be saved when application closed.");
                    Tooltip.install((parent.getChildren().get(8)),tp);
                } else{
                    parent.setId("yes");
                    Tooltip tp = new Tooltip("This project will be saved when application closed.");
                    Tooltip.install((parent.getChildren().get(8)),tp);
                }
                iconChange((ImageView) parent.getChildren().get(7), parent.getId()+"save");
            }
            
        };

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
        
        //setting up on close event handler
        Platform.runLater(()->{
            Stage root = (Stage)mainNavPane.getScene().getWindow();
            root.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    onApplicationClosed();
                }
            });
        });

    }

    public void initializeProjectTabMap() throws IOException{
        for (String key: projectNameSet){
            Tab displayingTab = paneMaker.getContentTab(key);
            ScrollPane scroll = (ScrollPane) displayingTab.getContent();
            GridPane grid = (GridPane)(scroll).getContent();
            int gridSize = 0;
            for (ContentCell cell: projectContentMap.get(key)){
                ((ImageView)cell.getHolder().getChildren().get(0)).setId(key);
                Platform.runLater(()->{
                    setContenCellDisplayHandlers(cell);
                });
                if (gridSize%5 == 0){
                    grid.getRowConstraints().add(new RowConstraints(130));
                }
                grid.add(cell.getHolder(),gridSize%5,gridSize/5);
                gridSize++;
            }
            projectTabMap.put(key, displayingTab);
            addProjectList(key);
        }
    }

    //Actual logic of the function beneath
    //this method is also used when initializing project tab map
    public void addProjectList(String itemName) throws IOException{
        //generate new panes from the pane maker
        AnchorPane projectCell = paneMaker.getNewProjectCell(itemName);

        //default the project will not be save
        projectCell.setId("no");
        
        Tooltip tp = new Tooltip("This project will not be saved when application closed.");
        Tooltip.install((projectCell.getChildren().get(8)),tp);
        projectList.getChildren().add(projectCell);
        Tab contentPane;
        //create new one is when a new project is created, else use the exisiting one(when starting the application)
        if (projectTabMap.containsKey(itemName)){
            contentPane = projectTabMap.get(itemName);
            if (fh.getIsLockedStatus(itemName)){
                projectCell.setId("lock");
                tp = new Tooltip("This project will always be save when application closed.");
                Tooltip.install((projectCell.getChildren().get(8)),tp);
                iconChange((ImageView) projectCell.getChildren().get(7), "lock");
            } else{
                projectCell.setId("yes");
                tp = new Tooltip("This project will be save when application closed.");
                Tooltip.install((projectCell.getChildren().get(8)),tp);
                iconChange((ImageView) projectCell.getChildren().get(7), "yessave");
            }
        } else{
            contentPane = paneMaker.getContentTab(itemName);
            projectContentMap.put(itemName, new ArrayList<ContentCell>());
        }

        //setId on the grid pane 
        ((ScrollPane)contentPane.getContent()).getContent().setId(itemName);
        

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

        //------------This section is for setting up the event handler for the project name cell solely
        // i.e. the cells that appears on the left of the interface
        Platform.runLater(()->{

            //complicated logic setting
            resetProjectCellListener(projectCell);

            //handler when the project cell is clicked
            EventHandler<Event> temp = new EventHandler<Event>() {
                @Override
                public void handle(Event event) {
                    onContentDisplayCellClose();
                    AnchorPane root =  (AnchorPane) ((Label)event.getSource()).getParent();
                    String key = ((Label)root.getChildren().get(0)).getText();
                    //only add when the tab is not opened otherwise could trigger null pointer
                    if (!openedProjectSet.contains(key)){
                        System.out.println(key);
                        Tab tab = projectTabMap.get(key);
                        contentDisplayPane.getTabs().add(tab);
                        openedProjectSet.add(key);
                        plusSignPane.setVisible(true);
                        contentDisplayPane.getSelectionModel().select(tab);
                        KeyFrame f1 = new KeyFrame(Duration.millis(80), e->setContentScrollBarVisAmount((ScrollPane) tab.getContent()));
                        Timeline ss = new Timeline(f1);
                        ss.play();
                    } else{
                        contentDisplayPane.getSelectionModel().select(projectTabMap.get(key));
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
            //when label is clicked opening new tab if not already
            projectCell.getChildren().get(0).setOnMouseClicked(temp);
            projectCell.getChildren().get(6).setOnMouseClicked(temp);

            //Rectangle listeners
            projectCell.getChildren().get(4).setOnMouseEntered(tt);
            projectCell.getChildren().get(5).setOnMouseEntered(tt);
            projectCell.getChildren().get(8).setOnMouseEntered(tt);

            projectCell.getChildren().get(4).setOnMouseExited(tte);
            projectCell.getChildren().get(5).setOnMouseExited(tte);
            projectCell.getChildren().get(8).setOnMouseExited(tte);

        });

        projectTabMap.put(itemName, contentPane);
        
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
        if (!cell.getId().equals("lock")){
            cell.getChildren().get(8).addEventHandler(MouseEvent.MOUSE_CLICKED, projectLockHandler);
        }

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
        ((TextField)clicked.getChildren().get(1)).setText(((Label)clicked.getChildren().get(0)).getText());
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
        String previousName = ((Label)cell.getChildren().get(0)).getText();
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
            System.out.println(newName+" "+previousName);
            Tab tt = projectTabMap.remove(previousName);
            ((ScrollPane)tt.getContent()).getContent().setId(newName);
            System.out.println(tt);
            projectTabMap.put(newName,tt);
            projectTabMap.get(newName).setText(newName);
            projectContentMap.put(newName, projectContentMap.remove(previousName));
            projectNameSet.add(newName);
            //update on the cell
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
        ugood.selfUpdate(selectedPane.getChildren().size()+1);//the parameter passing in is pointless at the moment
        setContenCellDisplayHandlers(ugood);
        if (temp%5 == 0){
            selectedPane.getRowConstraints().add(new RowConstraints(130));
            KeyFrame f1 = new KeyFrame(Duration.millis(80), e->setContentScrollBarVisAmount(tempPane));
            Timeline ss = new Timeline(f1);
            ss.play();
        }
        selectedPane.add(ugood.getHolder(),temp%5,temp/5);
        projectContentMap.get(currentTab.getText()).add(ugood);
    }

    public void setContenCellDisplayHandlers(ContentCell cell){
        //last node of the cell holder is the close Icon;
        Node closeIcon = cell.getHolder().getChildren().get(cell.getHolder().getChildren().size()-1);
        EventHandler<Event> onClickEvent = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                AnchorPane selectedPane = (AnchorPane) ((Rectangle)event.getSource()).getParent();
                String key = selectedPane.getParent().getId();
                int col = GridPane.getColumnIndex(selectedPane);
                int row = GridPane.getRowIndex(selectedPane);
                selectedCell = projectContentMap.get(key).get(row*5+col);
                try {
                    onContentDisplayCellClicked();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            
        };

        EventHandler<Event> onRecHover = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                closeIcon.setVisible(true);
                closeIcon.setOpacity(0.25);
            }
        };

        EventHandler<Event> onCloseIconHover = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
               closeIcon.setOpacity(1);
            }
            
        };

        EventHandler<Event> onRecExit = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Platform.runLater(()->{
                    if(closeIcon.getOpacity()!=1){
                        closeIcon.setVisible(false);
                    }
                });
            }
            
        };

        EventHandler<Event> onCloseIconExit = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                closeIcon.setOpacity(0.25);
            }
            
        };

        EventHandler<Event> onCloseIconClicked = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                AnchorPane selectedPane = (AnchorPane) ((ImageView)event.getSource()).getParent();
                String key = selectedPane.getParent().getId();
                int col = GridPane.getColumnIndex(selectedPane);
                int row = GridPane.getRowIndex(selectedPane);
                selectedCell = projectContentMap.get(key).get(row*5+col);
                //TODO mark 1
                deleteConfirm.setVisible(true);
                contentDisplayPane.setDisable(true);
                plusSignPane.setDisable(true);

            }
            
        };

        ObservableList<Node> temp = cell.getHolder().getChildren();
        temp.get(temp.size()-2).addEventHandler(MouseEvent.MOUSE_CLICKED, onClickEvent);
        temp.get(temp.size()-2).addEventHandler(MouseEvent.MOUSE_ENTERED, onRecHover);
        temp.get(temp.size()-2).addEventHandler(MouseEvent.MOUSE_EXITED, onRecExit);

        temp.get(temp.size()-1).addEventHandler(MouseEvent.MOUSE_CLICKED, onCloseIconClicked);
        temp.get(temp.size()-1).addEventHandler(MouseEvent.MOUSE_ENTERED, onCloseIconHover);
        temp.get(temp.size()-1).addEventHandler(MouseEvent.MOUSE_EXITED, onCloseIconExit);
    }

    public void onDeleteConfirmClick(){
        AnchorPane holder = selectedCell.getHolder();
        GridPane parentGridPane = ((GridPane)holder.getParent());
        String key = parentGridPane.getId();
        System.out.println(key);
        parentGridPane.getChildren().remove(holder);
        projectContentMap.get(key).remove(selectedCell);
        displayMessage("Goal removed", MessageType.SUCCESS);
        onDeleteCancelClick();
    }

    public void onDeleteCancelClick(){
        plusSignPane.setDisable(false);
        contentDisplayPane.setDisable(false);
        deleteConfirm.setVisible(false);
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
        if (!contentZoomUpPane.isVisible() || contentZoomUpPane == null){
            return;
        }
        selectedCell.setStatus(Integer.parseInt(contentZoomUpPane.getChildren().get(0).getId()));
        selectedCell.setEndDate(((Label)contentZoomUpPane.getChildren().get(2)).getText().replaceFirst("^Deadline: ",""));
        selectedCell.setMainGoal(((Label)contentZoomUpPane.getChildren().get(4)).getText());
        selectedCell.setMainGoalSpec(((Label)contentZoomUpPane.getChildren().get(5)).getText());

        //empty the list first
        selectedCell.clearSubGoals();

        ObservableList<Node> subGoalList = ((VBox) ((ScrollPane)contentZoomUpPane.getChildren().get(6)).getContent()).getChildren();
        for (int i = 0; i< subGoalList.size(); i++){
            AnchorPane goal = (AnchorPane) subGoalList.get(i);
            SubGoal sg = new SubGoal(
                Integer.parseInt(goal.getId()),
                ((CheckBox)goal.getChildren().get(2)).isSelected());
            sg.setContent(((Label)goal.getChildren().get(1)).getText().strip());
            selectedCell.appendSubGoals(sg);
        }

        //remove all the subgoals within the subgoal display
        subGoalList.clear();
        
        selectedCell.selfUpdate(0);
        contentZoomUpPane.setVisible(false);

    }

    //1.created date    2.deadline      3.reset button      4.main goal title
    //5.main goal specification     6.Scrollpane for sub goals      7.ImageView close icon
    public void onContentDisplayCellClicked() throws IOException{
        loadingCell.setVisible(true);
        Thread temp = new Thread(()->{
            Platform.runLater(()->{
                contentZoomUpPane.setVisible(true);
                contentZoomUpPane.getChildren().get(0).setId(String.valueOf(selectedCell.getStatus()));
                ((Label)contentZoomUpPane.getChildren().get(1)).setText("Created on: "+selectedCell.getCreateDate());
                ((Label)contentZoomUpPane.getChildren().get(2)).setText("Deadline: "+selectedCell.getEndDate());
                ((Label)contentZoomUpPane.getChildren().get(4)).setText(selectedCell.getMainGoal());
                ((Label)contentZoomUpPane.getChildren().get(5)).setText(selectedCell.getMainGoalSpec());
            });
            VBox subGoalBox = (VBox) ((ScrollPane)contentZoomUpPane.getChildren().get(6)).getContent();
            System.out.println("working now");
            Platform.runLater(()->{
                try {
                    paneMaker.loadSubGoalVBox(subGoalBox, selectedCell);
                    loadingCell.setVisible(false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });
        });
        temp.start();
            
    }

    public void onApplicationClosed(){
        onContentDisplayCellClose();
        loadingPane.setVisible(true);
        projectList.setDisable(true);
        contentDisplayPane.setDisable(true);
        fh.clearSavedFile();
        Platform.runLater(()->{
            ObservableList<Node> projects = projectList.getChildren();
            for (int i = 0; i< projects.size(); i++){
                AnchorPane current = (AnchorPane) projects.get(i);
                Boolean isLocked;
                switch (current.getId()) {
                    case "yes":
                        isLocked = false;
                        break;
                    case "lock":
                        isLocked = true;
                        break;
                    default:
                        continue;
                }
                System.out.println(((Label)current.getChildren().get(0)).getText());
                fh.exportFile(
                    projectContentMap.get(((Label)current.getChildren().get(0)).getText()), 
                    isLocked,
                    ((Label)current.getChildren().get(0)).getText());
            }
            //close the application when everything is done
            ((Stage)mainNavPane.getScene().getWindow()).close();
        });
    }
}
