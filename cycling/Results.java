package cycling;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Results implements Serializable {

    private int RiderID;
    private LocalTime[] checkPointTimes;
    private LocalTime ElapsedTime = LocalTime.MIN;
    private LocalTime AdjustedTime;



    public void registerResults(int riderID, LocalTime... checkpointTimes){
        this.RiderID = riderID;
        this.checkPointTimes = checkpointTimes;
        long temp = checkpointTimes[0].until(checkpointTimes[checkpointTimes.length-1], ChronoUnit.MINUTES);
        ElapsedTime = ElapsedTime.plusMinutes(temp);

    }


    //getters

    public int getRiderID() {
        return RiderID;
    }

    public LocalTime getfinishtime(){
        return checkPointTimes[checkPointTimes.length-1];
    }

    public LocalTime[] getCheckpointTimes() {
        return checkPointTimes;
    }

    public LocalTime getAdjustedTime() {return AdjustedTime;}

    public LocalTime getElapsedTime() {return ElapsedTime;}


    //setters
    public void setAdjustedTime(LocalTime adjustedTime) {AdjustedTime = adjustedTime;}

    public void setElapsedTime(LocalTime elapsedTime) {ElapsedTime = elapsedTime;}
}
