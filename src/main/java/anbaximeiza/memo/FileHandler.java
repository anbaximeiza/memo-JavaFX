package anbaximeiza.memo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import anbaximeiza.memo.controllers.PaneMaker;

public class FileHandler {

    PaneMaker paneMaker;
    HashSet<String> projectNameSet;
    HashMap<String,ArrayList<ContentCell>> projectContentMap;
    HashMap<String,Boolean> isProjectLocked;

    public FileHandler(){
        paneMaker = new PaneMaker();
        projectNameSet = new HashSet<>();
        projectContentMap = new HashMap<>();
        isProjectLocked = new HashMap<>();
    }

    //import from existing folder, return as the project content map in mainNav
    public HashMap<String,ArrayList<ContentCell>> importExisting(){
        File dir = new File("src/main/java/anbaximeiza/memo/saveFile");
        File[] directoryListing = dir.listFiles();
        if (directoryListing == null){
            System.out.println("null found");
            return null;
        }
        for (File f : directoryListing){
            String name = f.getName();
            projectContentMap.put(
                name.replace(".txt", "")
                ,loadSingleProject(name)
                );
            projectNameSet.add(name.replace(".txt", ""));
            
        }
        return projectContentMap;
    }

    public ArrayList<ContentCell> loadSingleProject(String projectName){
        ArrayList<ContentCell> result =  new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/java/anbaximeiza/memo/saveFile/"+projectName));
            String[] temp = reader.readLine().split(",");
            int listSize = Integer.parseInt(temp[0]);
            if (temp[1].equals("true")){
                isProjectLocked.put(projectName.replace(".txt", ""), true);
            } else{
                isProjectLocked.put(projectName.replace(".txt", ""), false);
            }
            for (int i = 0; i< listSize; i++){
                ContentCell holder = new ContentCell(paneMaker.getContentHolder());
                String[] date = reader.readLine().split(",");
                holder.setEndDate(date[1]);
                holder.setCreatetDate(date[0]);
                holder.setMainGoal(reader.readLine());
                holder.setMainGoalSpec(reader.readLine());
                int subGoalSize = Integer.parseInt(reader.readLine());
                //detail[0]: whether it's completed; detail[1]: priority
                for (int j =0; j< subGoalSize; j++){
                    SubGoal sub;
                    String[] detail = reader.readLine().split(",");
                    if (detail[0].equals("0")){
                        sub = new SubGoal(Integer.parseInt(detail[1]),false);
                    } else{
                        sub = new SubGoal(Integer.parseInt(detail[1]),true);
                    }
                    sub.setContent(reader.readLine());
                    holder.appendSubGoals(sub);
                    holder.selfUpdate(i);
                }
                result.add(holder);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("opps error");
            e.printStackTrace();
            return result;
        }
        return result;
    }

    public HashSet<String> getProjectNameSet(){
        return projectNameSet;
    }

    public void exportFile(ArrayList<ContentCell> content, Boolean isLocked, String projectName){
        LinkedList<String> file =  new LinkedList<>();
        file.add(content.size()+","+String.valueOf(isLocked));
        for (ContentCell cell : content){
            file.add(cell.getCreateDate()+","+cell.getEndDate());
            file.add(cell.getMainGoal());
            file.add(cell.getMainGoalSpec());
            ArrayList<SubGoal> subs = cell.getSubGoals();
            file.add(String.valueOf(subs.size()));
            for (SubGoal sub: subs){
                if (sub.isCompleted()){
                    file.add("1,"+sub.getPriority());
                } else{
                    file.add("0,"+sub.getPriority());
                }
                file.add(sub.getContent());
            }
        }
        String result = String.join("\n", file);
        try {
            File textFile = new File(
                "src/main/java/anbaximeiza/memo/saveFile/"+
                projectName+
                ".txt");
            if (textFile.createNewFile()) {
                System.out.println("File created: " + textFile.getName());
            } else {
                System.out.println("File already exists.");
            }
            FileWriter fw = new FileWriter("src/main/java/anbaximeiza/memo/saveFile/"+
                                            projectName+
                                            ".txt");
            fw.write(result);
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void clearSavedFile(){
        File dir = new File("src/main/java/anbaximeiza/memo/saveFile");
        File[] directoryListing = dir.listFiles();
        for (File f : directoryListing){
            f.delete();
        }
    }

    public Boolean getIsLockedStatus(String Key){
        return isProjectLocked.get(Key);
    }
    
}
