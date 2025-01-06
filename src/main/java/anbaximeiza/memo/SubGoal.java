package anbaximeiza.memo;

public class SubGoal {

    private String content;
    private Boolean completed;
    private int priority;

    public SubGoal(String content){
        this.content = content;
        completed = false;
        priority = 4; //default value;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
}
