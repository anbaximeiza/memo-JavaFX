package anbaximeiza.memo;

import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ContentCell {
    
    private AnchorPane holder;
    private String mainGoal;
    private ArrayList<String> subGoals;
    //colors
    private String date;

    public ContentCell(AnchorPane holder){
        this.holder = holder;
    }

    public ContentCell(AnchorPane holder, String date){
        this.holder = holder;
        this.date =date;
        selfUpdate();
    }

    public AnchorPane getHolder() {
        return holder;
    }

    public void setHolder(AnchorPane holder) {
        this.holder = holder;
    }

    public void setMainGoal(String mainGoal) {
        this.mainGoal = mainGoal;
    }
    public ArrayList<String> getSubGoals() {
        return subGoals;
    }

    private void selfUpdate(){
        ((Label)holder.getChildren().get(1)).setText(date);

    }
}
