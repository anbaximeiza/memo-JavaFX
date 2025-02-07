package anbaximeiza.memo.controllers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class ZoomUpController implements Initializable{

    @FXML private VBox subGoalBox;
    @FXML private AnchorPane root;

    @FXML private Label titleLabel;
    @FXML private Label specLabel;
    @FXML private Label ddlLabel;

    @FXML private ImageView titleEdit;
    @FXML private ImageView specEdit;
    @FXML private TextArea titleTextArea;
    @FXML private TextArea specTextArea;
    
    @FXML private AnchorPane calenderPane;

    @FXML private ImageView loadingImage;
    @FXML private VBox  orderDisplayBox;

    @FXML private AnchorPane statusPane;

    @FXML private Label ratioLabel;

    private AnchorPane selectedGoal;
    private DatePicker ddlPicker;

    private PaneMaker paneMaker;

    private ImageView hoveredEdit;
    private EventHandler<KeyEvent> textAreaHandler;

    private HashMap<String, ArrayList<Node>> priorityList;

    private Button status;

    private int totalSubgoals;
    private int completedSubgoals;
    //default style for status button
    private final String buttonStyle = "-fx-border-color: white; -fx-border-radius: 5; -fx-border-width: 2; -fx-background-radius: 5;";

    
    
    private VBox messageBox = null;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(()->{
            setToolTip(root.getChildren().get(9), "New sub goal");
            setToolTip(root.getChildren().get(12), "Reselect Deadline");
            setToolTip(root.getChildren().get(21), "Sort from low to high");
            setToolTip(root.getChildren().get(22), "Sort from high to low");
        });
        paneMaker = new PaneMaker();
        priorityList = new HashMap<>(); 
        priorityList.put("4", new ArrayList<>());
        priorityList.put("3", new ArrayList<>());
        priorityList.put("2", new ArrayList<>());
        priorityList.put("1", new ArrayList<>());
        priorityList.put("0", new ArrayList<>());
        root.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                onTextAreaEditingFinish();
                if (oldValue == false){
                    if (messageBox ==null){
                        messageBox = (VBox) ((AnchorPane)root.getParent()).getChildren().get(5);
                    }

                    KeyFrame f1 = new KeyFrame(Duration.millis(200), e->{
                        totalSubgoals = subGoalBox.getChildren().size();
                        completedSubgoals = 0;
                        for (Node i : subGoalBox.getChildren()){
                            addSubGoalListener((AnchorPane) i);
                            priorityList.get(i.getId()).add(i);
                            CheckBox temp = (CheckBox) ((AnchorPane)i).getChildren().get(2);
                            if (temp.isSelected()){
                                completedSubgoals++;
                            }
                        }
                        ratioLabel.setText(totalSubgoals+"/"+completedSubgoals);
                    });

                    //initializing status button style
                    KeyFrame f2 = new KeyFrame(Duration.millis(210), e->{
                        int index = Integer.parseInt(statusPane.getId());
                        if (index==0){
                            status = null;
                            return;
                        }
                        Button select = (Button) statusPane.getChildren().get(index-1);
                        switch (index) {
                            case 1:
                                select.setStyle(buttonStyle+"-fx-background-color: #00d5ff;");
                                break;
                            case 2:
                                select.setStyle(buttonStyle+"-fx-background-color: #00ff00;");
                                break;
                            case 4:
                                select.setStyle(buttonStyle+"-fx-background-color: #ff0022;");
                                break;
                            case 3:
                                select.setStyle(buttonStyle+"-fx-background-color: #ee00ff;");
                                break;
                            default:
                                status = null;
                                return;
                        }
                        status = select;
                        select.setTextFill(Color.WHITE);
                    });
                    Timeline tl = new Timeline(f1,f2);
                    tl.play();
                } else{
                    calenderPane.setVisible(false);
                    KeyFrame f1 = new KeyFrame(Duration.millis(200), e->{
                        priorityList.get("0").clear();
                        priorityList.get("1").clear();
                        priorityList.get("2").clear();
                        priorityList.get("3").clear();
                        priorityList.get("4").clear();
                    });
                    for (Node node : statusPane.getChildren()){
                        node.setStyle(buttonStyle);
                        ((Button)node).setTextFill(Color.BLACK);
                    }
                    Timeline tl = new Timeline(f1);
                    tl.play();
                }
            }
            
        });

        textAreaHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)){
                    onTextAreaEditingFinish();
                }
            }
            
        };

        ddlPicker = new DatePicker(LocalDate.now());
        DatePickerSkin datePickerSkin = new DatePickerSkin(ddlPicker);
        Node popupContent = datePickerSkin.getPopupContent();
        calenderPane.getChildren().add(popupContent);

        ddlPicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
            @Override
            public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
                    LocalDate newValue) {
                String current  = ddlLabel.getText().replaceFirst("^Deadline: ","");
                if (current.equals("null")){
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                    ddlLabel.setText("Deadline: "+newValue.format(formatter));
                    calenderPane.setVisible(false);
                    return;
                }
                DateTimeFormatter temp = DateTimeFormatter.ofPattern("dd-MM-yy");
                LocalDate currentDDL = LocalDate.parse(current,temp);
                if (newValue.isEqual(currentDDL)){
                    return;
                } else if (newValue.isBefore(LocalDate.now())){
                    displayMessage("Deadline already pass!",
                    MessageType.ERROR);
                    calenderPane.setVisible(false);
                   return;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
                ddlLabel.setText("Deadline: "+newValue.format(formatter));
                calenderPane.setVisible(false);
            }
            
        });
    }

    //helper function from other controller
    public void iconChange(ImageView node, String imgName){
        Image buffer= new Image(getClass().getResourceAsStream("/img/"+ imgName+"_icon.png"));
        node.setImage(buffer);
    }

    public void addSubGoalListener(AnchorPane goal){
        EventHandler<MouseEvent> temp = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                AnchorPane newClick =  (AnchorPane) ((Node)event.getSource());
                if (selectedGoal == null){
                    selectedGoal = newClick;
                    return;
                }
                if (newClick!=selectedGoal){
                    Platform.runLater(()->{
                        resetSubGoalCell();
                        selectedGoal = newClick;
                    });
                }   
            } 
        };

        goal.getChildren().get(9).addEventHandler(MouseEvent.MOUSE_CLICKED,
            new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                totalSubgoals--;
                AnchorPane deletedPane = (AnchorPane) ((Node)event.getSource()).getParent();
                priorityList.get(deletedPane.getId()).remove(deletedPane);
                CheckBox cb = (CheckBox) deletedPane.getChildren().get(2);
                if (cb.isSelected()){
                    completedSubgoals--;
                }
                ratioLabel.setText(totalSubgoals+"/"+completedSubgoals);
            }
            
        });

        goal.setOnMouseClicked(temp);
        goal.idProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                priorityList.get(oldValue).remove(goal);
                priorityList.get(newValue).add(goal);
            }
        });

        CheckBox cb = (CheckBox) goal.getChildren().get(2);
        cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    completedSubgoals = completedSubgoals+1;
                } else{
                    completedSubgoals = completedSubgoals-1;
                }
                ratioLabel.setText(totalSubgoals+"/"+completedSubgoals);
            }
            
        });
    }

    public void onPlusImageClicked() throws IOException{
        Platform.runLater(()->{
            resetSubGoalCell();
        });
        AnchorPane cell = paneMaker.getSubGoalCell(null);
        subGoalBox.getChildren().add(cell);
        priorityList.get("4").add(cell);
        addSubGoalListener(cell);
        totalSubgoals++;
        ratioLabel.setText(totalSubgoals+"/"+completedSubgoals);
    }

    public void resetSubGoalCell(){
        if (selectedGoal == null){
            return;
        }
        selectedGoal.getChildren().get(8).setVisible(false);
        selectedGoal.getChildren().get(7).setVisible(false);
        selectedGoal=null;
    }

    public void onTitleHovered(){
        titleEdit.setVisible(true);
        titleEdit.setOpacity(0.25);
    }

    public void onSpecHovered(){
        specEdit.setVisible(true);
        specEdit.setOpacity(0.25);
    }

    public void onLabelUnHovered(){
        Platform.runLater(()->{
            if (hoveredEdit==null){
                titleEdit.setVisible(false);
                specEdit.setVisible(false);
            }
        });
    }

    public void onTitleEditHovered(){
        hoveredEdit = titleEdit;
        hoveredEdit.setOpacity(1);

    }

    public void onEditUnHovered(){
        hoveredEdit = null;
    }

    public void onSpecEditHovered(){
        hoveredEdit = specEdit;
        hoveredEdit.setOpacity(1);
    }

    public boolean onTextAreaEditingFinish(){
        if (titleTextArea.isVisible()){
            titleTextArea.setVisible(false);
            titleLabel.setText(titleTextArea.getText().strip());
            iconChange(titleEdit, "rename");
            titleTextArea.removeEventHandler(KeyEvent.KEY_PRESSED, textAreaHandler);
            return true;
        }
        if (specTextArea.isVisible()){
            specTextArea.setVisible(false);
            specLabel.setText(specTextArea.getText().strip());
            iconChange(specEdit, "rename");
            specTextArea.removeEventHandler(KeyEvent.KEY_PRESSED, textAreaHandler);
            return true;
        }
        return false;
    }

    public void onTitleEditClicked(){
        if (!onTextAreaEditingFinish()){
            titleTextArea.setVisible(true);
            titleTextArea.setText(titleLabel.getText());
            iconChange(titleEdit, "yes");
            titleTextArea.addEventHandler(KeyEvent.KEY_PRESSED, textAreaHandler);
            titleTextArea.requestFocus();
        }

    }

    
    public void onSpecEditClicked(){
        if (!onTextAreaEditingFinish()){
            specTextArea.setVisible(true);
            specTextArea.setText(specLabel.getText());
            iconChange(specEdit, "yes");
            specTextArea.addEventHandler(KeyEvent.KEY_PRESSED, textAreaHandler);
            specTextArea.requestFocus();
        }
    }

    public void onReselectDeadLine(){
        if (calenderPane.isVisible()){
            calenderPane.setVisible(false);
            return;
        }
        String current  = ddlLabel.getText().replaceFirst("^Deadline: ","");
        if (current.equals("null")){
            ddlPicker.setValue(LocalDate.now());
        } else{
            DateTimeFormatter temp = DateTimeFormatter.ofPattern("dd-MM-yy");
            LocalDate currentDate = LocalDate.parse(current,temp);
            ddlPicker.setValue(currentDate);
        }
        calenderPane.setVisible(true);
    }

    public void sortLowToHigh(){
        subGoalBox.getChildren().clear();
        subGoalBox.getChildren().addAll(priorityList.get("4"));
        subGoalBox.getChildren().addAll(priorityList.get("3"));
        subGoalBox.getChildren().addAll(priorityList.get("2"));
        subGoalBox.getChildren().addAll(priorityList.get("1"));
        subGoalBox.getChildren().addAll(priorityList.get("0"));
    }

    public void sortHighToLow(){
        subGoalBox.getChildren().clear();
        subGoalBox.getChildren().addAll(priorityList.get("0"));
        subGoalBox.getChildren().addAll(priorityList.get("1"));
        subGoalBox.getChildren().addAll(priorityList.get("2"));
        subGoalBox.getChildren().addAll(priorityList.get("3"));
        subGoalBox.getChildren().addAll(priorityList.get("4"));
    }

    public void sinkCompleted(){
        ArrayList<AnchorPane> completed = new ArrayList<>();
        for (int i=0; i< subGoalBox.getChildren().size(); i++){
            AnchorPane node = (AnchorPane) subGoalBox.getChildren().get(i);
            Boolean isCompleted = ((CheckBox)node.getChildren().get(2)).isSelected();
            if (isCompleted){
                System.out.println(i);
                completed.add(node);
                subGoalBox.getChildren().remove(node);
                i--;
            }
        }
        subGoalBox.getChildren().addAll(completed);
    }

    public void onStatusButtonClicked(ActionEvent event){
        onTextAreaEditingFinish();
        Button select = (Button) event.getSource();
        if (select==status){
            statusPane.setId("0");
            status.setStyle(buttonStyle);
            status.setTextFill(Color.BLACK);
            status = null;
            return;
        }
        switch (select.getId()) {
            case "b1":
                select.setStyle(buttonStyle+"-fx-background-color: #00d5ff;");
                break;
            case "b2":
                select.setStyle(buttonStyle+"-fx-background-color: #00ff00;");
                break;
            case "b4":
                select.setStyle(buttonStyle+"-fx-background-color: #ff0022;");
                break;
            default:
                select.setStyle(buttonStyle+"-fx-background-color: #ee00ff;");
                break;
        }
        if (status!=null){
            status.setStyle(buttonStyle);
            status.setTextFill(Color.BLACK);
        }
        statusPane.setId(select.getId().replace("b", ""));
        select.setTextFill(Color.WHITE);
        status = select;
    }

    public void onStatusButtonEnter(MouseEvent event){
        Button select= (Button) event.getSource();
        switch (select.getId()) {
            case "b1":
            select.setStyle(buttonStyle+"-fx-background-color: #00d5ff;");
            break;
        case "b2":
            select.setStyle(buttonStyle+"-fx-background-color: #00ff00;");
            break;
        case "b4":
            select.setStyle(buttonStyle+"-fx-background-color: #ff0022;");
            break;
        default:
            select.setStyle(buttonStyle+"-fx-background-color: #ee00ff;");
            break;
        }
    }

    public void onStatusButtonExit(MouseEvent event){
        Button select = ((Button)event.getSource());
        if (select!=status){
            select.setStyle(buttonStyle);
        }
   }

    //copy from MainNav
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

    public void setToolTip(Node node, String text){
        Tooltip tp = new Tooltip(text);
        Tooltip.install(node,tp);
    }
}
