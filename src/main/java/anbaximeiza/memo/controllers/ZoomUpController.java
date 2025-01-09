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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ZoomUpController implements Initializable{

    @FXML VBox subGoalBox;
    @FXML AnchorPane root;

    private AnchorPane selectedGoal;

    private PaneMaker paneMaker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneMaker = new PaneMaker();
        root.visibleProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
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
        ((Label)selectedGoal.getChildren().get(6)).setText("Delete");
        ((Label)selectedGoal.getChildren().get(5)).setText("Priority");
        ((ImageView)selectedGoal.getChildren().get(9)).setVisible(false);
        selectedGoal=null;
    }
}
