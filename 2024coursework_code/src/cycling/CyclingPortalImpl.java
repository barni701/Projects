package cycling;
import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


public class CyclingPortalImpl implements CyclingPortal, Serializable {

    public ArrayList<Team> Teams;
    public ArrayList<Riders> Riders;
    public ArrayList<Race> Races;


    public CyclingPortalImpl(){


        Teams = new ArrayList<>();

        Riders = new ArrayList<>();

        Races = new ArrayList<>();

    }

    @Override
    public void removeRaceByName(String name) throws NameNotRecognisedException {
        boolean namefound = false;

        for (int i = 0; i < Races.size(); i++) {
            Race temp = Races.get(i);
            if (Objects.equals(temp.getName(), name)) {
                Races.remove(i);
                namefound = true;
                break;
            }
        }

        if (!namefound){
            throw new NameNotRecognisedException("Name not recognised");
        }
    }

    @Override
    public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
        Race selectRace = null;
        boolean racefound = false;


        for(Race race : Races){
            if (race.getRaceID() == raceId){
                selectRace = race;
                racefound = true;
                break;
            }
        }

        if (!racefound)
            throw new IDNotRecognisedException("Race id not found");

        ArrayList<LocalTime> Times = new ArrayList<>();
        ArrayList<Integer> sortedRiderIDs = new ArrayList<>();


        Times.addAll(selectRace.getTotalTimes().values());

        //System.out.println(Times.size());

        LocalTimeComparator comparator = new LocalTimeComparator();
        Times.sort(comparator);

        //System.out.println(selectRace.getTotalTimes().values());

        for(int i =0; i<Times.size();i++){
            for(Integer id : selectRace.getTotalTimes().keySet()){
                if(Times.get(i) == selectRace.getTotalTimes().get(id)) {
                    sortedRiderIDs.add(id);
                }
            }
        }

        int[] RiderIDs = new int[sortedRiderIDs.size()];

        for (int i =0; i< sortedRiderIDs.size(); i++){
            RiderIDs[i] = sortedRiderIDs.get(i);
        }

        return RiderIDs;
    }

    @Override
    public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {

        HashMap<Integer, LocalTime> TotalAdjustedTimes = new HashMap<>();
        Race selectrace = null;
        boolean racefound = false;
        for(Race race:Races){
            if(race.getRaceID() == raceId){
                selectrace = race;
                racefound = true;
                break;
            }
        }

        if(!racefound)
            throw new IDNotRecognisedException("race not found");



        //make hashmap of total elapsed rider times --> every time is set to 0
        for(int i =0; i<selectrace.getStages().getFirst().getLeaderBoard().size(); i++){

            TotalAdjustedTimes.put(selectrace.getStages().getFirst().getLeaderBoard().get(i),LocalTime.MIN);

        }


        //hashmap for riderids to total-adjusted-times
        for(int i =0; i< selectrace.getStages().size(); i++) {

            ArrayList<Integer> Leaderboard = selectrace.getStages().get(i).getLeaderBoard();

            //gets the total time for every rider --> same order as Leaderboard
            for(int index =0; index < selectrace.getStages().get(i).getLeaderBoard().size(); index++) {

                int riderid = selectrace.getStages().get(i).getLeaderBoard().get(index);
                LocalTime riderTime = selectrace.getStages().get(i).getRiderTimes().get(riderid);
                LocalTime temp = LocalTime.MIN;
                long timeBetween = temp.until(riderTime, ChronoUnit.SECONDS);

                TotalAdjustedTimes.put(riderid, TotalAdjustedTimes.get(Leaderboard.get(index)).plusSeconds(timeBetween));
            }

        }

        // get the adjusted times in a sorted list
        ArrayList<LocalTime> adjustedTimesList = new ArrayList<>();

        adjustedTimesList.addAll(TotalAdjustedTimes.values());

        LocalTimeComparator comparator = new LocalTimeComparator();

        adjustedTimesList.sort(comparator);

        //get the riderids sorted by adjusted time
        ArrayList<Integer> riderids = new ArrayList<>();

        for(int i = 0; i< adjustedTimesList.size(); i++){
            for(Integer rider : TotalAdjustedTimes.keySet()){
                if(adjustedTimesList.get(i) == TotalAdjustedTimes.get(rider)){
                    riderids.add(rider);
                }
            }
        }

        LocalTime[] realtimes = new LocalTime[riderids.size()];
        //get the riders' real time from hashmap
        for(int i = 0; i < riderids.size(); i++){
            for(Integer rider : selectrace.getTotalTimes().keySet()){
                if(riderids.get(i) == rider){
                    realtimes[i] = selectrace.getTotalTimes().get(rider);
                }
            }
        }


        return realtimes;
    }

    @Override
    public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
    Race selectRace = null;
    boolean racefound = false;
    for (Race race: Races){ //find the race
        if(race.getRaceID() == raceId) {
            selectRace = race;
            racefound=true;
            break;
        }
    }

    if (!racefound){
        throw new IDNotRecognisedException("id not recognised");
    }


    //making the hashmap
    //0 points for everyone

    for(int i = 0; i<selectRace.getStages().getFirst().getLeaderBoard().size(); i++){

        selectRace.getAllPoints().put(selectRace.getStages().getFirst().getLeaderBoard().get(i), 0);
    }


    //make hashmap of total elapsed rider times --> every time is set to 0
    for(int i =0; i<selectRace.getStages().getFirst().getLeaderBoard().size(); i++){

        selectRace.getTotalTimes().put(selectRace.getStages().getFirst().getLeaderBoard().get(i),LocalTime.MIN);

    }


    //for every stage --> add points and times
    for(int i =0; i< selectRace.getStages().size(); i++) {

        ArrayList<Integer> Leaderboard = selectRace.getStages().get(i).getLeaderBoard();
        int[] StagePoints;

        StagePoints = getRidersPointsInStage(selectRace.getStages().get(i).getStageID());

        //adding points
        for (int index = 0; index < Leaderboard.size(); index++) {
            selectRace.getAllPoints().put(Leaderboard.get(index), selectRace.getAllPoints().get(Leaderboard.get(index)) + StagePoints[index]);

        }


        //gets the total time for every rider --> same order as Leaderboard
        for(int index =0; index < selectRace.getStages().get(i).getLeaderBoard().size(); index++) {

            int riderid = selectRace.getStages().get(i).getLeaderBoard().get(index);
            LocalTime riderTime = selectRace.getStages().get(i).getRiderTimes().get(riderid);
            LocalTime temp = LocalTime.MIN;
            long timeBetween = temp.until(riderTime, ChronoUnit.SECONDS);

            selectRace.getTotalTimes().put(riderid, selectRace.getTotalTimes().get(selectRace.getStages().getFirst().getLeaderBoard().get(index)).plusSeconds(timeBetween));
        }

    }


    //Arraylist of total times

    ArrayList<LocalTime> Times = new ArrayList<>();
    ArrayList<Integer> sortedRiderIDs = new ArrayList<>();


    Times.addAll(selectRace.getTotalTimes().values());
    LocalTimeComparator comparator = new LocalTimeComparator();
    Times.sort(comparator);


    for(int i =0; i<Times.size();i++){
        for(Integer id : selectRace.getTotalTimes().keySet()){
            if(Times.get(i) == selectRace.getTotalTimes().get(id))
                sortedRiderIDs.add(id);
        }
    }

    int[] sortedPoints = new int[sortedRiderIDs.size()];

    for(int i =0; i<sortedRiderIDs.size(); i++){
        sortedPoints[i] = selectRace.getAllPoints().get(sortedRiderIDs.get(i));
    }

    return sortedPoints;

}


    public class LocalTimeComparator implements Comparator<LocalTime> {

        @Override
        public int compare(LocalTime time1, LocalTime time2) {
            return time1.compareTo(time2);
        }
    }


    @Override
    public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
        Race selectRace = null;
        HashMap<Integer, Integer> MountainPointsHash = new HashMap<>(); //rider IDs to points
        boolean racefound = false;

        for(Race race : Races){
            if(race.getRaceID() == raceId){
                selectRace = race;
                racefound = true;
            }
        }

        if(!racefound){
            throw new IDNotRecognisedException("race ID not found");
        }

        //initiating a Hashmap
        for(int i =0; i< selectRace.getStages().getFirst().getLeaderBoard().size(); i++){
            Stage stage = selectRace.getStages().getFirst();
            MountainPointsHash.put(stage.getLeaderBoard().get(i), 0);
        }


        //adding points to the hashmap
        for(Stage stage : selectRace.getStages()){
            if(stage.getType() == StageType.MEDIUM_MOUNTAIN || stage.getType() == StageType.HIGH_MOUNTAIN){
                for(int index = 0; index < stage.getLeaderBoard().size(); index++){
                    MountainPointsHash.put(stage.getLeaderBoard().get(index), MountainPointsHash.get(stage.getLeaderBoard().get(index))+getRidersPointsInStage(stage.getStageID())[index]);
                }
            }
        }

        // making an array for the points
        int[] MountainPoints = new int[MountainPointsHash.values().size()];

        int i = 0;
        for(Integer point : MountainPointsHash.values()){

            MountainPoints[i] += point;
            i = i+1;
        }

        //sorting the array
        Arrays.sort(MountainPoints);

        for (int j = 0; j < MountainPoints.length / 2; j++) {
            int temp = MountainPoints[j];
            MountainPoints[j] = MountainPoints[MountainPoints.length - 1 - j];
            MountainPoints[MountainPoints.length - 1 - j] = temp;
        }

        return MountainPoints;
    }

    @Override
    public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {

        Race selectrace = null;
        boolean raceFound = false;

        for(Race race : Races){
            if (race.getRaceID() == raceId){
                selectrace = race;
                raceFound = true;
                break;
            }
        }

        if(!raceFound)
            throw new IDNotRecognisedException("ID not recognised");

        ArrayList<Integer> points = new ArrayList<>();

        //extract points to a list
        points.addAll(selectrace.getAllPoints().values());
        //sort list
        IntegerComparator comparator = new IntegerComparator();
        points.sort(comparator);
        points.reversed(); //pionts in descending order


        int[] riders = new int[points.size()];

        //doesnt work
        // iterate over values of getallpoints and order the keys
        // has to be in descending order

        for(int i =0; i < points.size(); i++){
            for(Integer id : selectrace.getAllPoints().keySet()){
                if(points.get(i) == selectrace.getAllPoints().get(id)){
                    riders[i] = id;
                }
            }
        }

        return riders;
    }

    public class IntegerComparator implements Comparator<Integer> {

        @Override
        public int compare(Integer int1, Integer int2) {
            return int1.compareTo(int2);
        }
    }

    @Override
    public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
        Race selectRace = null;
        HashMap<Integer, Integer> MountainPointsHash = new HashMap<>(); //rider IDs to points
        boolean racefound = false;
        ArrayList<Integer> Mountainpoints;
        int[] riderids;

        for(Race race : Races){
            if(race.getRaceID() == raceId){
                selectRace = race;
                racefound = true;
            }
        }

        if(!racefound){
            throw new IDNotRecognisedException("race ID not found");
        }

        //initiating a Hashmap
        for(int i =0; i< selectRace.getStages().getFirst().getLeaderBoard().size(); i++){
            Stage stage = selectRace.getStages().getFirst();
            MountainPointsHash.put(stage.getLeaderBoard().get(i), 0);
        }

        // Add all rider IDs from the first stage's leaderboard to the HashMap (assuming all riders participate in all stages)
        for (Integer riderID : selectRace.getStages().getFirst().getLeaderBoard()) {
            MountainPointsHash.putIfAbsent(riderID, 0);
        }

        //adding points to the hashmap
        for(Stage stage : selectRace.getStages()){
            if(stage.getType() == StageType.MEDIUM_MOUNTAIN || stage.getType() == StageType.HIGH_MOUNTAIN){
                for(int index = 0; index < stage.getLeaderBoard().size(); index++){
                    MountainPointsHash.replace(stage.getLeaderBoard().get(index), MountainPointsHash.get(stage.getLeaderBoard().get(index))+getRidersPointsInStage(stage.getStageID())[index]);
                }
            }
        }

        Mountainpoints = new ArrayList<>();
        for(Integer point : MountainPointsHash.values()){
            Mountainpoints.add(point);
        }
        IntegerComparator comparator = new IntegerComparator();
        Mountainpoints.sort(comparator);
        Mountainpoints.reversed();

        riderids = new int[Mountainpoints.size()];

        for(int i =0; i < Mountainpoints.size(); i++){
            for(Integer id : MountainPointsHash.keySet()){
                if(MountainPointsHash.get(id).equals(Mountainpoints.get(i))){
                    riderids[i] = id;
                }
            }
        }

        return riderids;
    }

    @Override
    public int[] getRaceIds() {
        int n = Races.size();
        int[] arrRaceIDs = new int[n];

        for (int i=0; i<n;i++){
            arrRaceIDs[i] = Races.get(i).getRaceID();
        }

        return arrRaceIDs;
    }

    @Override
    public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {

        if(RaceArrayCheck(name)){
            throw new IllegalNameException();
        }
        if (name.contains(" ") || name.length() > 32 || name.isEmpty()) {
            throw new InvalidNameException();
        }

        Race createdRace= new Race();
        int RaceId = createdRace.createRace(name, description);

        Races.add(createdRace);
        return RaceId;

    }

    public boolean RaceArrayCheck(String name) {
        boolean s = false;

        for (Race race : Races) {
            if (race.getName().equals(name)) {
                s = true;
                break;
            }
        }

        return s;
    }


    @Override
    public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
        int index = -1;

        for (int i = 0; i < Races.size(); i++) {
            Race temp = Races.get(i);
            if (temp.getRaceID() == raceId) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + raceId);
        }

        Race slectRace = Races.get(index);

        String formatString = slectRace.getDetails();

        return formatString;
    }

    @Override
    public void removeRaceById(int raceId) throws IDNotRecognisedException {
        int index = -1;

        for (int i = 0; i < Races.size(); i++) {
            Race temp = Races.get(i);
            if (temp.getRaceID() == raceId) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + raceId);
        }

        // Remove the race at the found index
        Races.remove(index);
    }

    @Override
    public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
        int index = -1;

        for (int i = 0; i < Races.size(); i++) {
            Race temp = Races.get(i);
            if (temp.getRaceID() == raceId) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + raceId);
        }

        Race slectRace = Races.get(index);

        return slectRace.getNoOfStages();
    }

    @Override
    public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime, StageType type) throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {

        if(stageName.contains(" ") || stageName.isEmpty() || stageName.length() > 32)
            throw new InvalidNameException();

        if(length < 5)
            throw new InvalidLengthException();


        for(Race race: Races){
            for(Stage stage : race.getStages()){
                if(stage.getName().equals(stageName)) {
                    throw new IllegalNameException();
                }
            }
        }

        int index = -1;

        for (int i = 0; i < Races.size(); i++) {
            Race temp = Races.get(i);
            if (temp.getRaceID() == raceId) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + raceId);
        }

        Race temp = Races.get(index);

        int StageId = temp.addStage(stageName, description, length, startTime, type);

        return StageId;
    }

    @Override
    public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
        int index = -1;
        boolean found = false;

        for (int i = 0; i < Races.size(); i++) {
            Race temp = Races.get(i);
            if (temp.getRaceID() == raceId) {
                index = i;
                found = true;
                break;
            }
        }

        if (!found)
            throw new IDNotRecognisedException();

        Race selectedrace = Races.get(index);


        int n = selectedrace.getStages().size();
        int[] arrStages = new int[n];

        for (int i=0; i<n;i++){
            arrStages[i] = selectedrace
                    .getStages().get(i)
                    .getStageID();
        }

        return arrStages;
    }

    @Override
    public double getStageLength(int stageId) throws IDNotRecognisedException {
        int index = -1;
        double length = 0;

        // Incrementing through Race Objects
        for (Race selectRace : Races) {
            // Incrementing through Stage Objects
            for (int j = 0; j < selectRace.getStages().size(); j++) {
                Stage selectStage = selectRace.getStages().get(j);

                if (selectStage.getStageID() == stageId) {
                    length = selectStage.getLength();
                    index = j;
                    break;
                }
            }


        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + stageId);
        }

        return length;
    }

    @Override
    public void removeStageById(int stageId) throws IDNotRecognisedException {
        boolean stageFound = false;

        // Incrementing through Race Objects
        for (Race selectRace : Races) {
            // Incrementing through Stage Objects
            for (int j = 0; j < selectRace.getStages().size(); j++) {
                Stage selectStage = selectRace.getStages().get(j);

                if (selectStage.getStageID() == stageId) {
                    selectRace.removeStage(stageId);
                    stageFound = true;
                    break;
                }
            }
        }

        if (!stageFound) {
            throw new IDNotRecognisedException("ID not recognized: " + stageId);
        }

    }

    @Override
    public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient, Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {

        Stage selectStage = null;
        boolean stageFound = false;
        int id = 0;

        // Incrementing through Race Objects
        for (Race selectRace : Races) {
            // Incrementing through Stage Objects
            for (int j = 0; j < selectRace.getStages().size(); j++) {
                selectStage = selectRace.getStages().get(j);

                if (selectStage.getStageID() == stageId) {
                    if(selectStage.getType() == StageType.TT)
                        throw new InvalidStageTypeException();

                    id = selectStage.addCheckpoint(location, type, averageGradient, length);
                    stageFound = true;
                    break;
                }
            }
        }

        if (!stageFound) {
            throw new IDNotRecognisedException("ID not recognized: " + stageId);
        }

        if(0 < location && location <=selectStage.getLength())
            throw new InvalidLocationException();

        if(selectStage.getState().isEmpty())
            throw new InvalidStageStateException();

        return id;
    }

    @Override
    public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
        boolean stagefound = false;
        int id = 0;
        Stage selectStage = null;

        // Incrementing through Race Objects
        for (Race selectRace : Races) {
            // Incrementing through Stage Objects
            for (int j = 0; j < selectRace.getStages().size(); j++) {
                selectStage = selectRace.getStages().get(j);

                if (selectStage.getStageID() == stageId) {
                    if(selectStage.getType() == StageType.TT)
                        throw new InvalidStageTypeException();

                    id = selectStage.addCheckpoint(location, CheckpointType.SPRINT, (double) 0, (double) 0);
                    stagefound =true;
                    break;
                }
            }
        }

        if(0 < location && location <=selectStage.getLength())
            throw new InvalidLocationException();

        if(selectStage.getState().isEmpty())
            throw new InvalidStageStateException();

        if (!stagefound) {
            throw new IDNotRecognisedException("ID not recognized: " + stageId);
        }



        return id;
    }

    @Override
    public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
        int index = -1;

        // Incrementing through Race Objects
        for (Race selectRace : Races) {
            // Incrementing through Stage Objects
            for (Stage stage : selectRace.getStages()) {
                // Incrementing through checkpoints
                for(int i = 0; i < stage.getCheckpoints().size(); i++){
                    if(stage.getCheckpoints().get(i).getCheckID() == checkpointId){
                        if(stage.getState().isEmpty())
                            throw new InvalidStageStateException();
                        index = i;
                        stage.getCheckpoints().remove(i);
                        break;
                    }

                }

            }

        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + checkpointId);
        }
    }

    @Override
    public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
        Stage selectstage;
        boolean found = false;


        for(Race race : Races){
            for(Stage stage : race.getStages()){
                if(stage.getStageID() == stageId){
                    selectstage = stage;
                    found = true;

                    if(selectstage.getState().equals("waiting for results"))
                        throw new InvalidStageStateException();

                    selectstage.setState_concluded("waiting for results");
                    break;
                }
            }
        }

        if(!found)
            throw new IDNotRecognisedException();
    }

    @Override
    public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {

        int[] arr = null;
        boolean found = false;
        Stage selectedStage = null;

        for(Race race : Races){
            for (Stage stage : race.getStages()){
                if (stage.getStageID() == stageId){
                    found = true;
                    selectedStage = stage;
                    break;
                }
            }
        }


        if (found){
            for (int j = 0; j < selectedStage.getCheckpoints().size(); j++){
                arr = new int[selectedStage.getCheckpoints().size()];
                arr[j] = selectedStage.getCheckpoints().get(j).getCheckID();
            }
            return arr;
        }

        else {
            throw new IDNotRecognisedException("ID not recognised: " + stageId);
        }
    }



    @Override
    public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
        Team createdTeam = new Team();

        createdTeam.createTeam(name, description);



        int TeamID = createdTeam.generateID();
        createdTeam.setTeamId(TeamID);

        if (name.contains(" ") || name.length()>32 || name.isEmpty()){
            throw new InvalidNameException();
        }

        if (TeamArrayCheck(name)){throw new IllegalNameException();}

        Teams.add(createdTeam);
        return TeamID;
    }

    public boolean TeamArrayCheck(String name) {
        boolean s = false;

        for (int i = 0; i < Teams.size(); i++){
            if (Teams.get(i).getName().equals(name)){
                s = true;
                break;
            }
        }

        return s;
    }

    @Override
    public void removeTeam(int teamId) throws IDNotRecognisedException {

        int index = -1;

        for (int i = 0; i < Teams.size(); i++) {
            Team team = Teams.get(i);


            if (team.getTeamId() == teamId) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + teamId);
        }

        // Remove the team at the found index
        Teams.remove(index);


    }

    @Override
    public int[] getTeams() {

        int[] IDs = new int[Teams.size()];

        for(int i = 0; i< Teams.size(); i++){
            IDs[i]=Teams.get(i).getTeamId();
        }
        return IDs;
    }

    @Override
    public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
        Team team = findTeamById(teamId);

        if (team == null) {
            throw new IDNotRecognisedException("ID not recognized: " + teamId);
        }

        return team.getRiders();
    }


    public Team findTeamById(int teamId) throws IDNotRecognisedException {
        boolean found= false;
        Team selectTeam = null;

        for (Team team : Teams) {
            if (team.getTeamId() == teamId) {
                found = true;
                selectTeam = team;
                break;
            }
        }
        if(!found)
            throw new IDNotRecognisedException();

        return selectTeam;
    }

    @Override
    public int createRider(int teamID, String name, int yearOfBirth) throws IDNotRecognisedException, IllegalArgumentException {
        Riders createdRider = new Riders();

        int RiderID = createdRider.createRider(name, yearOfBirth);

        Riders.add(createdRider);

        Team team = findTeamById(teamID);
        assert team != null;
        team.addRider(RiderID);
        return RiderID;
    }

    @Override
    public void removeRider(int riderId) throws IDNotRecognisedException {
        int index = -1;

        for (int i = 0; i < Riders.size(); i++) {
            Riders rider = Riders.get(i);
            if (rider.getRiderId() == riderId) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + riderId);
        }

        for(Race race: Races){
            race.getAllPoints().remove(riderId);
            race.getTotalTimes().remove(riderId);

            for(Stage stage : race.getStages()){
                for(int i =0; i< stage.getLeaderBoard().size(); i++){
                    if(stage.getLeaderBoard().get(i) == riderId){
                        stage.getLeaderBoard().remove(i);
                        stage.getAdjustedTimes().remove(i);
                        break;
                    }
                }
                stage.getRiderTimes().remove(riderId);
                for(Results result :stage.getResults()){
                    if(result.getRiderID() == riderId)
                        stage.getResults().remove(result);
                }
            }
        }

        // Remove the rider at the found index
        Riders.remove(index);

    }


    @Override
    public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpointTimes) throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException, InvalidStageStateException {
        Stage selectStage = null;
        boolean stagefound = false;
        LocalTime temp = LocalTime.MIN;


        for (Race race : Races){
            for (Stage stage : race.getStages()){
                if (stage.getStageID() == stageId){
                    selectStage = stage;
                    stagefound = true;
                    break;
                }
            }
            if(stagefound)
                break;
        }


        if (!stagefound) {
            throw new IDNotRecognisedException("ID not recognized: " + riderId);
        }

        if(selectStage.getState().isEmpty())
            throw new InvalidStageStateException();


        for(Results result : selectStage.getResults()){
            if(result.getRiderID() == riderId)
                throw new DuplicatedResultException();
        }


        for(int i =0; i < checkpointTimes.length-1; i++){
            if(checkpointTimes[i].until(checkpointTimes[i+1], ChronoUnit.SECONDS) < 0){
                throw new InvalidCheckpointTimesException();
            }
        }

        selectStage.addResults(riderId, checkpointTimes);
        selectStage.getRiderTimes().put(riderId, temp.plusSeconds(checkpointTimes[0].until(checkpointTimes[checkpointTimes.length-1], ChronoUnit.SECONDS)));
        selectStage.getSortedRiderIDsByElapsedTime();
    }

    @Override
    public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {

        Stage selectstage = null;
        int index = -1;
        Riders rider;
        boolean stagefound = false;

        for (int i = 0; i < Riders.size(); i++) {
            rider = Riders.get(i);
            if (rider.getRiderId() == riderId) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + riderId);
        }

        for(Race race : Races){
            for(Stage stage : race.getStages()){
                if (stage.getStageID() == stageId){
                    selectstage = stage;
                    stagefound = true;
                    break;
                }
            }
            if (stagefound)
                break;
        }

        for (Results result: selectstage.getResults()){
            if(result.getRiderID() == riderId){
                return result.getCheckpointTimes();
         }
        }

        return null;

    }

    @Override
    public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
        boolean riderfound = false;
        boolean stagefound = false;
        Stage selectStage = null;
        int PositionOfRider = -1;


        //find the stage
        for(Race race : Races){
            for(Stage stage : race.getStages()){
                if (stage.getStageID() == stageId){
                    stagefound = true;
                    selectStage = stage;
                    break;
                }
            }
            if (stagefound)
                break;
        }


        if(!stagefound){
            throw new IDNotRecognisedException("stage ID not found");
        }

        // find position of rider in the stage

        for (int j = 0; j < selectStage.getLeaderBoard().size(); j++) {
            if (selectStage.getLeaderBoard().get(j) == riderId) {
                riderfound = true;
                PositionOfRider = j;
                break;
            }
        }

        if (!riderfound){
            throw new IDNotRecognisedException("rider ID not found");
        }

        //set real elapsed time
        //elapsedTime = selectStage.getRiderTimes().get(riderId);

        //first adjusted time is the same as that riders real elapsed time
        selectStage.getAdjustedTimes().clear();
        selectStage.getAdjustedTimes().add(selectStage.getRiderTimes().get(selectStage.getLeaderBoard().getFirst()));



        //populate adjusted time Arraylist in stage class with either adjusted time or real time
        // depending on the time difference between riders
        for (int pos = 1; pos < selectStage.getLeaderBoard().size(); pos++) {
            if (selectStage.getRiderTimes().get(selectStage.getLeaderBoard()
                    .get(pos - 1)).until(selectStage.getRiderTimes()
                    .get(selectStage.getLeaderBoard().get(pos)), ChronoUnit.SECONDS) <= 1) {
                selectStage.getAdjustedTimes().add(selectStage.getAdjustedTimes().get(pos - 1));
            } else {
                selectStage.getAdjustedTimes().add(selectStage.getRiderTimes().get(selectStage.getLeaderBoard().get(pos)));
            }
        }

        //return Adjusted time of the rider
        return selectStage.getAdjustedTimes().get(PositionOfRider);

        //selectStage.getLeaderBoard().get(PositionOfRider)
    }

    @Override
    public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {

        Stage selectStage = null;
        int index = -1;
        Riders rider;
        int index2;
        boolean stagefound = false;
        int position;

        for (int i = 0; i < Riders.size(); i++) {
            rider = Riders.get(i);
            if (rider.getRiderId() == riderId) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IDNotRecognisedException("ID not recognized: " + riderId);
        }



        for(Race race : Races){
            for(Stage stage : race.getStages()){
                if (stage.getStageID() == stageId) {
                    stagefound = true;
                    selectStage = stage;
                    break;
                }
            }
            if (stagefound)
                break;
        }

        for (Results result : selectStage.getResults()) {
            if (result.getRiderID() == riderId) {
                index2 = selectStage.getResults().indexOf(result);
                selectStage.removeresults(index2);
            }
        }

        if (stagefound){
            for (int i = 0; i< selectStage.getLeaderBoard().size(); i++) {
                if (selectStage.getLeaderBoard().get(i) == riderId){
                    selectStage.getLeaderBoard().remove(i);
                    position = i;
                    selectStage.getAdjustedTimes().remove(position);
                    selectStage.getRiderTimes().remove(riderId);
                    break;
                }
            }


        }


    }

    @Override
    public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {

        int[] arrLeader;
        boolean stagefound = false;
        Stage selectstage = null;


        for(Race race : Races){
            for(Stage stage : race.getStages()){
                if(stage.getStageID() == stageId){
                    stagefound = true;
                    selectstage = stage;
                    break;
                }
            }
        }


        if (!stagefound)
            throw new IDNotRecognisedException("Id not recognised");


        else{
            arrLeader = new int[selectstage.getLeaderBoard().size()];
            for(int i = 0; i<selectstage.getLeaderBoard().size(); i++){
                arrLeader[i] = selectstage.getLeaderBoard().get(i);
            }
            return arrLeader;
        }

    }

    @Override
    public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
        Stage selectstage = null;
        boolean stagefound = false;


        for(Race race : Races){
            for(Stage stage : race.getStages()) {
                if (stage.getStageID() == stageId){
                    selectstage = stage;
                    stagefound = true;
                    break;
                }
            }
            if(stagefound)
                break;
        }

        if(!stagefound)
            throw new IDNotRecognisedException("id not found");


        LocalTime[] times = new LocalTime[selectstage.getAdjustedTimes().size()];
        int i=0;
        for(LocalTime time : selectstage.getAdjustedTimes()){
            times[i] = time;
            i++;
        }

        return times;
    }

    @Override
    public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {

        Stage selectStage = null;
        boolean stagefund = false;
        int numberOfRiders;

        for (Race race : Races){
            for(Stage stage : race.getStages()){
                if (stage.getStageID() == stageId){
                    selectStage = stage;
                    stagefund = true;
                }
            }
        }

        if (!stagefund){
            throw new IDNotRecognisedException("ID not recognised");
        }

        numberOfRiders = selectStage.getLeaderBoard().size();

        if(selectStage.getType() == StageType.MEDIUM_MOUNTAIN){
            return generatePointsMediumMountain(numberOfRiders);

        }
        else if (selectStage.getType() == StageType.FLAT) {

            return generatePointsFlat(numberOfRiders);

        }
        else if (selectStage.getType() ==StageType.TT) {
            return generatePointsTT(numberOfRiders);

        }
        else if (selectStage.getType() == StageType.HIGH_MOUNTAIN) {
            return generatePointsHighMountain(numberOfRiders);
        }


        return null;
    }

    public int[] generatePointsMediumMountain(int length){
        int[] Points = new int[length];
        for(int i =0; i< length; i++){
            Points[i] = 60/(i+2);
        }
        return Points;
    }

    public int[] generatePointsFlat(int length){
        int[] Points = new int[length];
        for(int i =0; i< length; i++){
            Points[i] = 100/(i+2);
        }
        return Points;
    }
    public int[] generatePointsHighMountain(int length){
        int[] Points = new int[length];
        for(int i =0; i< length; i++){
            Points[i] = 40/(i+2);
        }
        return Points;
    }

    public int[] generatePointsTT(int length){
        int[] Points = new int[length];
        for(int i =0; i< length; i++){
            Points[i] = 40/(i+2);
        }
        return Points;
    }


    @Override
    public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
        Stage selectstage = null;
        boolean stageFound = false;
        int[] MountainPointsInStage;

        for(Race race : Races){
            for (Stage stage : race.getStages()){
                if(stage.getStageID() == stageId){
                    selectstage = stage;
                    stageFound = true;
                    break;
                }
            }

            if(stageFound)
                break;
        }

        if (!stageFound)
            throw new IDNotRecognisedException("stage not found");


        MountainPointsInStage = getRidersPointsInStage(selectstage.getStageID());

        return MountainPointsInStage;
    }

    @Override
    public void eraseCyclingPortal() {

        this.Races.clear();
        this.Teams.clear();
        this.Riders.clear();

    }

    @Override
    public void saveCyclingPortal(String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(Teams);
            oos.writeObject(Races);
            oos.writeObject(Riders);
        } catch (IOException e) {
            System.err.println("Error saving cycling portal: " + e.getMessage());
            throw e;
        }

    }


    @Override
    public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Teams = (ArrayList<Team>) ois.readObject();
            Races = (ArrayList<Race>) ois.readObject();
            Riders = (ArrayList<Riders>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading cycling portal: " + e.getMessage());
            throw e;
        }
    }
}
