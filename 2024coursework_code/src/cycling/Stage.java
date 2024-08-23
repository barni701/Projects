package cycling;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Stage extends Team implements Serializable {
    private String Name;
    private String Description;
    private double Length;
    private int stageID;
    private StageType Type;
    private ArrayList<Checkpoint> Checkpoints;
    private ArrayList<Integer> LeaderBoard;
    private ArrayList<LocalTime> AdjustedTimes;
    private HashMap<Integer, LocalTime> RiderTimes;
    private ArrayList<Results> Results;
    private String state;







    public int createStage(String name, String description,
                           double length, LocalDateTime startTime,
                           StageType type){
        this.Name = name;

        int id = generateID(name);
        this.stageID = id;
        this.Results = new ArrayList<>();
        this.RiderTimes = new HashMap<>();
        this.AdjustedTimes = new ArrayList<>();
        this.Description = description;
        this.Length = length;
        this.Type = type;
        this.LeaderBoard = new ArrayList<>();
        this.Checkpoints = new ArrayList<>();
        this.state = "";


        return id;
    }

    public int addCheckpoint(Double location, CheckpointType type, Double averageGradient,
                             Double length){
        Checkpoint newCheck = new Checkpoint();
        int id = newCheck.createCheckpoint(location, type, averageGradient, length);

        Checkpoints.add(newCheck);

        return id;
    }


    public void addResults(int riderid, LocalTime... times){
        Results result = new Results();
        result.registerResults(riderid, times);
        Results.add(result);
    }
    private int generateID(String name){
        int id = name.hashCode();

        if(id < 0){
            id = id * -1;
        }

        return id;
    }

    public void getSortedRiderIDsByElapsedTime() {
        ArrayList<Integer> sortedRiderIDs = new ArrayList<>();

        // Create a new ArrayList to store the rider IDs and their elapsed times
        ArrayList<RiderTimeEntry> riderTimeEntries = new ArrayList<>();

        // Iterate through the Results ArrayList and create RiderTimeEntry objects
        for (Results result : Results) {
            int riderID = result.getRiderID();
            LocalTime elapsedTime = result.getElapsedTime();
            riderTimeEntries.add(new RiderTimeEntry(riderID, elapsedTime));
        }

        // Sort the riderTimeEntries list based on the elapsed time
        Collections.sort(riderTimeEntries);

        // Extract the sorted rider IDs from the riderTimeEntries list
        for (RiderTimeEntry entry : riderTimeEntries) {
            sortedRiderIDs.add(entry.getRiderID());
        }

        this.LeaderBoard = sortedRiderIDs;
    }



    public void removeresults(int riderid){
        Results.removeIf(result -> result.getRiderID() == riderid);
    }

    //getters

    public ArrayList<Results> getResults(){ return Results;}
    public ArrayList<LocalTime> getAdjustedTimes() {return AdjustedTimes;}

    public HashMap<Integer, LocalTime> getRiderTimes() {return RiderTimes;}

    public String getName(){
        return Name;
    };

    public double getLength() {
        return Length;
    }

    public String getDescription() {
        return Description;
    }


    public int getStageID() {
        return stageID;
    }

    public StageType getType() {
        return Type;
    }

    public ArrayList<Checkpoint> getCheckpoints(){
        return Checkpoints;
    }

    public ArrayList<Integer> getLeaderBoard() {return LeaderBoard; }


    public String getState(){return state;}

    //setters
    public void setCheckpoints(ArrayList<Checkpoint> checkpoints) {
        Checkpoints = checkpoints;
    }

    public void setDescription(String description) {
        this.Description = description;
    }


    public void setLeaderBoard(ArrayList<Integer> leaderBoard) {
        this.LeaderBoard = leaderBoard;
    }

    public void setLength(double length) {
        this.Length = length;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public void setStageID(int stageID) {
        this.stageID = stageID;
    }



    public void setState_concluded(String state_concluded) {this.state = state_concluded;}

    // Inner class to store rider ID and elapsed time for sorting
    private static class RiderTimeEntry implements Comparable<RiderTimeEntry> {
        private int riderID;
        private LocalTime elapsedTime;

        public RiderTimeEntry(int riderID, LocalTime elapsedTime) {
            this.riderID = riderID;
            this.elapsedTime = elapsedTime;
        }

        public int getRiderID() {
            return riderID;
        }

        @Override
        public int compareTo(RiderTimeEntry other) {
            return this.elapsedTime.compareTo(other.elapsedTime);
        }
    }

}


