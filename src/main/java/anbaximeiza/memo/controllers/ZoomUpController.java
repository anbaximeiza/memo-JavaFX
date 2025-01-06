package anbaximeiza.memo.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ZoomUpController implements Initializable{

    @FXML VBox subGoalBox;

    private AnchorPane editingSubGoal;
    private AnchorPane deletingSUbGoal;

    private PaneMaker paneMaker;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paneMaker = new PaneMaker();
    }

    public void onPlusImageClicked() throws IOException{
        subGoalBox.getChildren().add(paneMaker.getSubGoalCell(null));
    }
}
