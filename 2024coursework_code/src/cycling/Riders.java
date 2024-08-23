package cycling;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

public class Riders implements Serializable {

    private String Name;
    private int yearOfBirth;
    private int RiderId;



    //getters
    public int getYearOfBirth(){return yearOfBirth;}
    public String getName(){return Name;}
    public int getRiderId(){return RiderId;}


    //setters
    public void setName(String Name){this.Name = Name;}
    public void setRiderId(int RiderId){this.RiderId =RiderId;}
    public void setYearOfBirth(int yearOfBirth){this.yearOfBirth = yearOfBirth;}


    public Riders(){

        //Results = new ArrayList<>();
    }

    public int createRider(String name, int yearOfBirth){
        this.Name = name;
        int riderID = generateRiderID();

        this.yearOfBirth = yearOfBirth;
        this.RiderId = riderID;

        return riderID;
    }

    private int generateRiderID() {
        int id = this.Name.hashCode();
        if (id < 0){
            id = id * -1;
        }

        return id;
    }


}
