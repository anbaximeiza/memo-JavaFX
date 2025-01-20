package anbaximeiza.memo.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
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

public class ZoomUpController implements Initializable{

    @FXML private VBox subGoalBox;
    @FXML private AnchorPane root;

    @FXML private Label titleLabel;
    @FXML private Label specLabel;

    @FXML private ImageView titleEdit;
    @FXML private ImageView specEdit;
    @FXML private TextArea titleTextArea;
    @FXML private TextArea specTextArea;


    private AnchorPane selectedGoal;

    private PaneMaker paneMaker;

    private ImageView hoveredEdit;
    private EventHandler<KeyEvent> textAreaHandler;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneMaker = new PaneMaker();
        root.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                onTextAreaEditingFinish();
                if (oldValue == false){
                    KeyFrame f1 = new KeyFrame(Duration.millis(200), e->{
                        for (Node i : subGoalBox.getChildren()){
                            addSubGoalListener((AnchorPane) i);
                        }
                    });
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

        goal.setOnMouseClicked(temp);
    }

    public void onPlusImageClicked() throws IOException{
        Platform.runLater(()->{
            resetSubGoalCell();
        });
        AnchorPane cell = paneMaker.getSubGoalCell(null);
        subGoalBox.getChildren().add(cell);
        addSubGoalListener(cell);
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
            titleLabel.setText(titleTextArea.getText());
            iconChange(titleEdit, "rename");
            titleTextArea.removeEventHandler(KeyEvent.KEY_PRESSED, textAreaHandler);
            return true;
        }
        if (specTextArea.isVisible()){
            specTextArea.setVisible(false);
            specLabel.setText(specTextArea.getText());
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


}
