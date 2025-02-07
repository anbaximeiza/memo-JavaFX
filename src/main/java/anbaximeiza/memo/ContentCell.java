package anbaximeiza.memo;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ContentCell {
    
    //The saveStatus is stored as the id of the anchorpane

    private AnchorPane holder;
    private String mainGoal = "Deafult";
    private String mainGoalSpec = "Deafult";
    private ArrayList<SubGoal> subGoals;
    //colors
    private String createDate;
    private String endDate = "null";
    private int status = 0;

    public ContentCell(AnchorPane holder){
        this.holder = holder;
        subGoals = new ArrayList<>();
    }

    public ContentCell(AnchorPane holder, String createDate){
        this.holder = holder;
        this.createDate = createDate;
        subGoals = new ArrayList<>();
    }

    public void selfUpdate(int index){

    }

    public AnchorPane getHolder() {
        return holder;
    }

    public String getMainGoal() {
        return mainGoal;
    }

    public String getMainGoalSpec() {
        return mainGoalSpec;
    }

    public void setHolder(AnchorPane holder) {
        this.holder = holder;
    }

    public void setMainGoal(String mainGoal) {
        this.mainGoal = mainGoal;
    }
    public ArrayList<SubGoal> getSubGoals() {
        return subGoals;
    }

    public void setMainGoalSpec(String mainGoalSpec) {
        this.mainGoalSpec = mainGoalSpec;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void appendSubGoals(SubGoal newSG) {
        this.subGoals.add(newSG);
    }

    public void clearSubGoals(){
        this.subGoals.clear();
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setCreatetDate(String createDate) {
        this.createDate = createDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
