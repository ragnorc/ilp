/******************************************************************************
 *  Class:       Drone
 *  Author:  	 s1614102 
 *  Description: Abstract drone class that implements the attributes and methods 
 *  			 that are shared by both the stateless and the stafeful drone.
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Queue;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;


abstract class Drone {

	protected Position position;
	protected Position startPosition;
	protected double power;
	protected double coins = 0;
	protected ArrayList<Feature> features;
	protected ArrayList<Feature> orginalFeatures;
	protected String mapSource;
	protected Random random;
	protected int numMoves = 0;
	protected ArrayList<Point> visitedPoints;
	protected String flightProtocol;
	protected BufferedWriter flightBWriter;
	protected String fileNamePrefix;
	protected int seed;
	protected LinkedList<Direction> path = new LinkedList<Direction>();

	Drone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix) throws IOException {
		this.position = startPosition;
		this.startPosition = startPosition;
		this.power = power;
		this.seed = seed;
		this.mapSource = mapSource;
		this.random = new Random(seed);
		this.features = (ArrayList<Feature>) FeatureCollection.fromJson(this.mapSource).features();
		this.orginalFeatures = (ArrayList<Feature>) FeatureCollection.fromJson(this.mapSource).features();
		this.visitedPoints = new ArrayList<Point>();
		Point currentPoint = Point.fromLngLat(startPosition.longitude, startPosition.latitude);
		visitedPoints.add(currentPoint);
		this.fileNamePrefix = fileNamePrefix;
		FileWriter fr = new FileWriter(fileNamePrefix + "txt", false);
		this.flightBWriter = new BufferedWriter(fr);
	}

	/**
	 * Method: nextMoves 
	 * Description: The following method implements the different
	 * type of drone's strategy of determining the next move. It is defined as
	 * abstract since the strategy is not general but depends on the drone type and
	 * hence needs to be implemented by the subclasses.
	 */

	abstract Queue<Direction> nextMoves() throws IOException;

	/**
	 * Method: move (polymorphic) 
	 * Description: The following method is overloaded
	 * and when receiving a single direction as an input it performs the actions
	 * that are required by the rules of the game when moving in a certain
	 * direction. It is implemented in the drone super class because the procedure
	 * is the same for both the stateful and stateless drone. Given a direction that
	 * the drone has decided to move to, this method updates the position of the
	 * drone and decreases its power by 1.25 as specified in the instructions and
	 * performs the exchange of coins and power in case a station is in reach.
	 * Additionally, this methods writes the current move to a text file as required
	 * for the submission.
	 *
	 */

	void move(Direction direction) throws IOException {
		Move move = this.getMoveInDirection(this.position, direction);
		String writeString = this.position.latitude + "," + this.position.longitude + "," + move.direction.name();
		this.position = this.position.nextPosition(move.direction);
		this.path.add(move.direction);
		writeString += "," + this.position.latitude + "," + this.position.longitude;
		Point nextPoint = Point.fromLngLat(this.position.longitude, this.position.latitude);
		visitedPoints.add(nextPoint);
		this.power = this.power + move.powerGain - 1.25;
		this.coins = this.coins + move.coinGain;
		writeString += String.format(",%f,%f", this.coins, this.power);
		this.numMoves++;
		if (move.feature != null) {
			double oldCoins = move.feature.getProperty("coins").getAsDouble();
			double oldPower = move.feature.getProperty("power").getAsDouble();

			move.feature.removeProperty("coins");
			move.feature.removeProperty("power");
			move.feature.addStringProperty("coins", Double.toString(oldCoins - move.coinGain));
			move.feature.addStringProperty("power", Double.toString(oldPower - move.powerGain));

		}

		this.flightBWriter.write(writeString);
		this.flightBWriter.newLine();

	}

	/**
	 * Method: 		move (polymorphic) 
	 * Description: The following overloaded methods accepts a queue of moves 
	 * that the drone has planned to move to. The method iteratively pops 
	 * a direction from the queue passes it to the other move function which 
	 * performs the required actions. In the case of the stateless
	 * drone, this function is always passed a singleton queue since the stateless
	 * is only allowed limited look-ahead.
	 */

	boolean move(Queue<Direction> moves) throws IOException {

		while (moves.size() > 0) {

			if (this.numMoves < 250 && this.power > 0) {
				this.move(moves.poll());
			} else {
				return true;
			}

		}

		return this.numMoves == 250;

	}

	/**
	 * Method: 		getUtilityOfStation (polymorphic) 
	 * Description:	The following overloaded methods compute the utility of a station.
	 * 				The first version is for the stateless drone because distance is not considered 
	 * 				as it is equal in all directions. The utility depends on the power of the station,
	 * 				its coins and the number of number of moves required to travel to that station. 
	 * 				The bias parameters can be tweaked according to the type of maps that can be expected.
	 * 				In the maps given by the SoI servers, lack of power is not really an issue, so it gets less 
	 * 				importance. The number of moves required to go to a station is highly penalised because
	 * 				bigger distance increases the risk of encountering a negative station on the way.
	 * 				
	 * 			 
	 */

	double getUtilityOfStation(double stationCoins, double stationPower) {

		return (stationPower)  + (stationCoins);

	}

	double getUtilityOfStation(double stationCoins, double stationPower, int numMovesToStation) {

		return 0.5*stationPower  +  5*stationCoins - 20*numMovesToStation;


	}
	
	Feature getNearestStation(Position position){
		Feature ret = null;
		
		double nearestDistance = Double.POSITIVE_INFINITY;

		
		for (Integer i = 0; i < this.features.size(); i++) {
			Feature feature = this.features.get(i);
			double longitude = ((Point) feature.geometry()).coordinates().get(0);
			double latitude = ((Point) feature.geometry()).coordinates().get(1);
			Position featurePosition = new Position(latitude, longitude);

			double distanceToStation = position.getDistanceToPosition(featurePosition);

			//Get nearest station
			if (distanceToStation <= 0.00025 && distanceToStation < nearestDistance) {

				
				ret =  feature;
				

			}

		}
		
		
		
		
		return ret;
		
	}
	
	
	/**
	 * Method: 		getMoveInDirection (polymorphic) 
	 * Description:	The method returns a move object containing the properties of the move such as coin and power gain.
	 * 				or utility. The coin and power gain is determined  by the station the drone will be closest to
	 * 				after moving from the given position in the given direction.
	 * 				
	 */ 

	Move getMoveInDirection(Position position, Direction direction) {

		// A position with no features has a basis utility of 0.
		double utility = 0.0;
		Move move = new Move(direction, 0.0, 0.0, utility, null);
        
		Feature station = this.getNearestStation(position);
		
		if(station != null) {
			double coinGain = station.getProperty("coins").getAsDouble();
			double powerGain = station.getProperty("power").getAsDouble();

			
				if (coinGain < 0) {
					// Drone can only loose as much coins as it owns. No debt allowed
					coinGain = Math.max(coinGain, (-1) * this.coins); 
																		
				}

				if (powerGain < 0) {
					// Drone can only loose as much power as it owns. No debt allowed.
					powerGain = Math.max(powerGain, (-1) * this.power); 
				}

				utility = this.getUtilityOfStation(coinGain, powerGain);

				move = new Move(direction, coinGain, powerGain, utility, station);
				

			
		}
		

		return move;

	}

	public LinkedList<Direction> getPathToPosition(Position currentPosition, Position goalPosition) {

		LinkedList<Direction> path = new LinkedList<Direction>();

		double distanceToGoal = Double.POSITIVE_INFINITY;
		int i = 0;
        Set<Double> hash_Set = new TreeSet<Double>(); 

		while (distanceToGoal > 0.00025) {
			if (hash_Set.contains(distanceToGoal)) {
				return null;
				
			}
			hash_Set.add(distanceToGoal);
			
            //System.out.println(distanceToGoal);
			Direction shortestDirection = null;
			Double shortestDistance = null;

			for (Direction direction : Direction.values()) {
				Position potentialPosition = currentPosition.nextPosition(direction);
				if (!potentialPosition.inPlayArea()) {
					continue;
				}
				double distance = potentialPosition.getDistanceToPosition(goalPosition);
				if (shortestDirection == null) {
					shortestDirection = direction;
					shortestDistance = distance;
				}
				Feature station = this.getNearestStation(potentialPosition);
				if (distance <= shortestDistance && ((station == null /*|| i > 50*/) || (station.getProperty("coins").getAsDouble() > -2) )) {
					//System.out.println(move.utility+" "+ distance);

					shortestDistance = distance;
					shortestDirection = direction;

				}

			}
			currentPosition = currentPosition.nextPosition(shortestDirection);
			distanceToGoal = currentPosition.getDistanceToPosition(goalPosition);
			
			path.add(shortestDirection);
			i++;

		}

		return path;

	}
	
	/** 
	 *	Method: 		getBestRandomDirection 
	 *	Description:	This method computes the best direction to take my maximising the utility. It first picks the move with highest non-zero
	 *					utility among the moves that encounter a station. In the case that the highest utility is negative (due to a negative station)
	 *					it tries to randomly pick a move that does non encounter any station (zero utility). In case no such move exists,
	 *					it must stay with the negative-utility move. It is the fundamental
	 *					method used by the stateless drone for the selection of the next move but is also used by the stateful drone
	 *					after having visited all stations.
	 */

	Direction getBestRandomDirection() {

		List<Move> zeroUtilityMoves = new ArrayList<Move>();
		double highestNonZeroUtility = Double.NEGATIVE_INFINITY;
		Move currentBestMove = null;
		for (Direction potentialDirection : Direction.values()) {
			Position potentialPosition = this.position.nextPosition(potentialDirection);

			if (potentialPosition.inPlayArea()) {
				Move potentialMove = this.getMoveInDirection(potentialPosition, potentialDirection);

				if (potentialMove.utility == 0) {
					zeroUtilityMoves.add(potentialMove);
				}

				else if (potentialMove.utility > highestNonZeroUtility) {
					currentBestMove = potentialMove;

				}

			}

		}

		/** Pick random zero-utility move if there exists one and if no station with
		 * positive utility was found. Otherwise stick with previously compute negative-utility currentBestMove
         */
		if (currentBestMove == null || (currentBestMove.utility < 0 && zeroUtilityMoves.size() > 0)) {

			if (zeroUtilityMoves.size() == 1) {
				currentBestMove = zeroUtilityMoves.get(0);
			} else if (zeroUtilityMoves.size() != 0) {
				

				currentBestMove = zeroUtilityMoves.get(this.random.nextInt(zeroUtilityMoves.size() - 1));

			}

		}

		return currentBestMove.direction;

	}
	
		/** 
	 *	Method: 		writeFlightPath
	 *	Description:	This method is responsible for writing the flight path after the drone has completed
	 *					its trajectory. The method takes the preserved original features of the map,
	 *					adds the visited locations (instance variable visitedPoints) as a feature and then writes
	 *					its to a geojson file.
	 *					
	 *					
	 */
	void writeFlightPath() throws IOException {

		LineString dronePath = LineString.fromLngLats(this.visitedPoints);
		Feature dronePathFeature = Feature.fromGeometry(dronePath);
		this.orginalFeatures.add(dronePathFeature);
		FeatureCollection featureCollection = FeatureCollection.fromFeatures(this.orginalFeatures);
		String newMap = featureCollection.toJson().toString();
		try (FileWriter file = new FileWriter(this.fileNamePrefix + "geojson")) {
			file.write(newMap);
		}

	}

}
