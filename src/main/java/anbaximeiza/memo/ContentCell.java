package anbaximeiza.memo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        ((Label)holder.getChildren().get(1)).setText(endDate);
        ((Label)holder.getChildren().get(2)).setText(mainGoal);
        int completedCount = 0;
        for (SubGoal goal:subGoals){
            if (goal.isCompleted()){
                completedCount++;
            }
        }

        // index 4 & 5 for overdue hint
        if (!endDate.equals("null")){
            DateTimeFormatter temp = DateTimeFormatter.ofPattern("dd-MM-yy");
            LocalDate eD = LocalDate.parse(endDate,temp);
    
            if (eD.isBefore(LocalDate.now())){
                holder.getChildren().get(4).setVisible(true);
                holder.getChildren().get(5).setVisible(true);
            }
        }

        ((Label)holder.getChildren().get(3)).setText(subGoals.size()+"/"+completedCount);

        Image buffer= new Image(getClass().getResourceAsStream("/img/status_"+status+".png"));
        ((ImageView)holder.getChildren().get(0)).setImage(buffer);

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
