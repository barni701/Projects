//import cycling.*;
//
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//
///**
// * A short program to illustrate an app testing some minimal functionality of a
// * concrete implementation of the CyclingPortal interface -- note you
// * will want to increase these checks, and run it on your CyclingPortalImpl class
// * (not the BadCyclingPortal class).
// *
// *
// * @author Diogo Pacheco
// * @version 2.0
// */
//public class CyclingPortalTestApp {
//
//	/**
//	 * Test method.
//	 *
//	 * @param args not used
//	 */
//	public static void main(String[] args) {
//		System.out.println("The system compiled and started the execution...");
//
//		// TODO replace BadMiniCyclingPortalImpl by CyclingPortalImpl
//		CyclingPortalImpl portal1 = new CyclingPortalImpl();
//		CyclingPortalImpl portal2 = new CyclingPortalImpl();
//
//		int firstID =-1;
//
//		assert (portal1.getRaceIds().length == 0)
//				: "Innitial Portal not empty as required or not returning an empty array.";
//		assert (portal1.getTeams().length == 0)
//				: "Innitial Portal not empty as required or not returning an empty array.";
//
//		try {
//			firstID = portal1.createTeam("TeamOne", "My favorite");
//			portal2.createTeam("TeamOne", "My favorite");
//
//		} catch (IllegalNameException e) {
//			e.printStackTrace();
//		} catch (InvalidNameException e) {
//			e.printStackTrace();
//		}
//
//		try{
//			Race selectrace = null;
//			Stage selectstage = null;
//			int raceid = portal1.createRace("raceOne", "longest");
//			int stageid = portal1.addStageToRace(raceid,"1st_Stage","long", 30, LocalDateTime.of(2022, 12, 23, 14, 0), StageType.FLAT);
//			int riderid = portal1.createRider(firstID ,"giorgio", 1999);
//			int[] ids = portal1.getTeamRiders(firstID);
//
//			for(Race race:portal1.Races){
//				if(race.getRaceID() == raceid) {
//					selectrace = race;
//					break;
//				}
//			}
//
//			for(Stage stage : selectrace.getStages()){
//				if(stage.getStageID() ==stageid){
//					selectstage = stage;
//				}
//			}
//			LocalTime[] chekpoints = new LocalTime[selectstage.getResults().getFirst().getCheckpointTimes().length];
//			for(Results result : selectstage.getResults()){
//				if (result.getRiderID() == riderid)
//					chekpoints = result.getCheckpointTimes();
//			}
//
//			portal1.registerRiderResultsInStage(stageid, riderid, chekpoints );
//
//
//		}
//		catch (InvalidStageStateException e){
//			e.printStackTrace();
//		}
//		catch (InvalidCheckpointTimesException e){
//			e.printStackTrace();
//		}
//		catch (DuplicatedResultException e){
//			e.printStackTrace();
//		}
//
//		catch (InvalidLengthException e){
//			e.printStackTrace();
//		}
//		catch (IllegalNameException e){
//			e.printStackTrace();
//		}
//		catch (InvalidNameException e){
//			e.printStackTrace();
//		}
//		catch (IDNotRecognisedException e){
//			e.printStackTrace();
//		}
//
//		assert (portal1.getTeams().length == 1)
//				: "Portal1 should have one team.";
//
//		assert (portal2.getTeams().length == 1)
//				: "Portal2 should have one team.";
//
//		assert(portal1.getRaceIds().length ==1);
//	}
//
//}
import cycling.*;

import javax.sound.sampled.Port;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalTime;
import java.util.List;

public class CyclingPortalTestApp {
	public static void main(String[] args) {
		CyclingPortalImpl portal = new CyclingPortalImpl();

		try {
			// Create a new race
			int raceId = portal.createRace("Tour_de_France", "The biggest cycling race in the world.");
			System.out.println("Created race with ID: " + raceId);

			// Add stages to the race
			int stage1Id = portal.addStageToRace(raceId, "Stage1", "Flat stage", 200.0, LocalDateTime.now(), StageType.FLAT);
			int stage2Id = portal.addStageToRace(raceId, "Stage2", "Mountain stage", 150.0, LocalDateTime.now().plusDays(1), StageType.HIGH_MOUNTAIN);

			// Create teams
			int teamId1 = portal.createTeam("Team_A", "Professional cycling team");
			int teamId2 = portal.createTeam("Team_B", "Amateur cycling team");

			// Create riders and assign them to teams
			int riderId1 = portal.createRider(teamId1, "John Doe", 1990);
			int riderId2 = portal.createRider(teamId1, "Jane Smith", 1995);
			int riderId3 = portal.createRider(teamId2, "Bob Johnson", 1988);
			int riderId4 = portal.createRider(teamId2, "Alice Williams", 1992);


			portal.concludeStagePreparation(stage1Id);
			portal.concludeStagePreparation(stage2Id);
			// Register rider results in a stage
			portal.registerRiderResultsInStage(stage1Id, riderId1, LocalTime.of(3, 30, 0), LocalTime.of(4, 0, 0));
			portal.registerRiderResultsInStage(stage1Id, riderId2, LocalTime.of(3, 30, 0), LocalTime.of(4, 0, 2));
			portal.registerRiderResultsInStage(stage1Id, riderId3, LocalTime.of(3, 40, 0), LocalTime.of(4, 10, 3));
			portal.registerRiderResultsInStage(stage1Id, riderId4, LocalTime.of(3, 45, 0), LocalTime.of(4, 15, 4));

			// Register rider results in stage 2
			portal.registerRiderResultsInStage(stage2Id, riderId1, LocalTime.of(4, 0, 0), LocalTime.of(5, 30, 0));
			portal.registerRiderResultsInStage(stage2Id, riderId2, LocalTime.of(4, 5, 0), LocalTime.of(5, 35, 0));
			portal.registerRiderResultsInStage(stage2Id, riderId3, LocalTime.of(4, 10, 0), LocalTime.of(5, 40, 0));
			portal.registerRiderResultsInStage(stage2Id, riderId4, LocalTime.of(4, 15, 0), LocalTime.of(5, 45, 0));


			portal.getRidersPointsInRace(raceId);

			ArrayList<Integer> s =portal.Races.getFirst().getStages().getFirst().getLeaderBoard();
			// Get riders' ranking in a stage

			LocalTime shit = portal.getRiderAdjustedElapsedTimeInStage(stage1Id, riderId4);
			System.out.println(shit);

			int[] stageRanking = portal.getRidersRankInStage(stage1Id);
			System.out.println("Stage 1 ranking: " + Arrays.toString(stageRanking));

			// Get riders' points in a stage
			int[] stagePoints = portal.getRidersPointsInStage(stage1Id);
			System.out.println("Stage 1 points: " + Arrays.toString(stagePoints));

			// Get riders' mountain points in a stage
			int[] stageMountainPoints = portal.getRidersMountainPointsInStage(stage2Id);
			System.out.println("Stage 2 mountain points: " + Arrays.toString(stageMountainPoints));


			int[] stage2Ranking = portal.getRidersRankInStage(stage2Id);
			System.out.println("Stage 1 ranking: " + Arrays.toString(stage2Ranking));

			// Get riders' general classification ranking in a race
			int[] raceRanking = portal.getRidersGeneralClassificationRank(raceId);
			System.out.println("Race general classification ranking: " + Arrays.toString(raceRanking));

			// Get riders' points classification ranking in a race
			int[] racePointsRanking = portal.getRidersPointClassificationRank(raceId);
			System.out.println("Race points classification ranking: " + Arrays.toString(racePointsRanking));

			// Get riders' mountain points classification ranking in a race
			int[] raceMountainPointsRanking = portal.getRidersMountainPointClassificationRank(raceId);
			System.out.println("Race mountain points classification ranking: " + Arrays.toString(raceMountainPointsRanking));

			//gets all the mountain points in race
			int[] MountainpointsInRace = portal.getRidersMountainPointsInRace(raceId);
			System.out.println("Mountain Points in race: "+ Arrays.toString(MountainpointsInRace));

			//general classification
			int[] clas = portal.getRidersGeneralClassificationRank(raceId);
			System.out.println(Arrays.toString(clas));

			//race details
			String detail = portal.viewRaceDetails(raceId);
			System.out.println(detail);

			// all race IDs
			int [] races = portal.getRaceIds();
			System.out.println(Arrays.toString(races));

			//
			int[] stages = portal.getRaceStages(raceId);
			System.out.println(Arrays.toString(stages));


			portal.saveCyclingPortal("data.txt");
			portal.eraseCyclingPortal();
			portal.loadCyclingPortal("data.txt");

			System.out.println(Arrays.toString(List.of(portal.Races).toArray()));


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
