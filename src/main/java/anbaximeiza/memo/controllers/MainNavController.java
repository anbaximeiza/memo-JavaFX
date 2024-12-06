package anbaximeiza.memo.controllers;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.AnchorPane;

public class MainNavController implements Initializable{

    @FXML private ListView<String> projectList;
    @FXML private AnchorPane toolPane;
    @FXML private TextField nameInputTextField;

    int projectCount = 0;
    HashSet<String> projectNameSet;
    String previousName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        projectNameSet = new HashSet<>();
    }

    
    public void tempListener(){
        projectList.setEditable(true);
        projectList.setCellFactory(TextFieldListCell.forListView());
        addProjectList(getDefaultName());
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
        if (newName.equals(previousName)){
            return;
        }
        if (projectNameSet.contains(newName)){
            //report error
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
    
}
