package anbaximeiza.memo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import anbaximeiza.memo.controllers.PaneMaker;
import javafx.scene.control.Tab;

public class FileHandler {

    PaneMaker paneMaker;
    HashSet<String> projectNameSet;
    HashMap<String,ArrayList<ContentCell>> projectContentMap;

    public FileHandler(){
        paneMaker = new PaneMaker();
        projectNameSet = new HashSet<>();
        projectContentMap = new HashMap<>();
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
            int listSize = Integer.parseInt(reader.readLine());
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
    
}
