package cycling;

import java.io.Serializable;
import java.util.ArrayList;

public class Team implements Serializable {

    private String Name;
    private String Description;
    private int TeamId;
    private ArrayList<Integer> RiderIDs;



    //getters
    public String getName(){return Name;}
    public String getDescription(){return Description;}
    public int getTeamId(){return TeamId;}
    public ArrayList<Integer> getRiderIDs(){return RiderIDs;}


    //setters
    public void setName(String Name){this.Name = Name;}
    public void setDescription(String Description){this.Description = Description;}
    public void setTeamId(int TeamId){this.TeamId = TeamId;}

    public void addRider(int riderID){this.RiderIDs.add(riderID);}


    public int[] getRiders(){
        int n = this.RiderIDs.size();
        int[] arrRiders = new int[n];

        for (int i=0; i<n;i++){
            arrRiders[i] = this.RiderIDs.get(i);
        }

        return arrRiders;
    }

    public int createTeam(String name, String description){


        RiderIDs = new ArrayList<>();
        this.Name = name;
        int teamID = generateID();
        this.Description = description;
        this.TeamId = teamID;

        return teamID;
    }

    public int generateID(){

        int id = this.Name.hashCode();
        if (id < 0){
            id = id * -1;
        }

        return id;
    }



    @Override
    public String toString() {
        return "Team "+Name+": "+ Description+ ": "+TeamId;
    }
}

