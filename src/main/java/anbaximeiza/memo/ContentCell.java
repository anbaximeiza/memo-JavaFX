package anbaximeiza.memo;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ContentCell {
    
    //The projectName(key) that's used for lookup the arrayList for cell
    //is stored as the id of the first children of the anchorpane

    private AnchorPane holder;
    private String mainGoal = "Deafult";
    private String mainGoalSpec = "Deafult";
    private ArrayList<SubGoal> subGoals;
    //colors
    private String createDate;
    private String endDate = "null";

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

        ((Label)holder.getChildren().get(2)).setText(createDate+ "|" + endDate);
        ((Label)holder.getChildren().get(4)).setText("1- ssssss");

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
}
