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
import java.util.Queue;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

// Lack of modifier indicates that the following class is package-private



abstract class Drone implements Cloneable {

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
	 *	Method:	    	nextMoves
	 *	Description:	The following method implements the different type of drone's strategy of determining the next move.
	 *                  It is defined as abstract since the strategy is not general but depends on the drone type and hence 
	 *                  needs to be implemented by the subclasses.
	 */
	
	abstract Queue<Direction> nextMoves() throws IOException;
	
	
	/** 
	 *	Method: 		move
	 *	Description:	The following method performs the actions that are required by the rules of the game
	 *  				when moving in a certain direction. It is implemented in the drone super class because
	 *  				the procedure is the same for both the stateful and stateless drone.
	 *  				Given a direction that the drone has decided to move to, this method decreases the power 
	 *  				of the drone by 1.25 as specified in the instructions and performs the exchange of coins 
	 *  				and power in case a station is in reach. Additionally, this methods writes the current move
	 * 					to a text file as required for the submission.
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

	boolean move(Queue<Direction> moves) throws IOException {

		while (moves.size() > 0) {

			if (this.numMoves < 250 && this.power > 0) {
				this.move(moves.poll());
			} else {
				return false;
			}

		}

		return true;

	}

	void writeFlightPath() throws IOException {

		LineString dronePath = LineString.fromLngLats(this.visitedPoints);
		Feature dronePathFeature = Feature.fromGeometry(dronePath);
		this.orginalFeatures.add(dronePathFeature);
		FeatureCollection featureCollection = FeatureCollection.fromFeatures(this.orginalFeatures);
		String newMap = featureCollection.toJson().toString();
		// System.out.println(newMap);
		try (FileWriter file = new FileWriter(this.fileNamePrefix + "geojson")) {
			file.write(newMap);
			// System.out.println("Successfully wrote flight path to file.");

		}

	}

//TODO: Improve utility function. add distance penalization and pass feature instead of coins/power
	double getUtilityOfStation(double stationCoins, double stationPower) {

		return (stationPower / this.power) + (stationCoins);

	}

	double getUtilityOfStation(double stationCoins, double stationPower, double distance) {

		return ((stationPower - distance * 1.25) / this.power) + stationCoins;

	}

	

	Move getMoveInDirection(Position position, Direction direction) {

		// A position with no features has a basis utility of 0.
		double utility = 0.0;
		double nearestDistance = Double.POSITIVE_INFINITY;
		Move move = new Move(direction, 0.0, 0.0, utility, null);

		for (Integer i = 0; i < this.features.size(); i++) {
			Feature feature = this.features.get(i);
			double longitude = ((Point) feature.geometry()).coordinates().get(0);
			double latitude = ((Point) feature.geometry()).coordinates().get(1);
			Position featurePosition = new Position(latitude, longitude);

			double distanceToStation = position.getDistanceToPosition(featurePosition);

			if (distanceToStation <= 0.00025 && distanceToStation < nearestDistance) {

				double coinGain = feature.getProperty("coins").getAsDouble();
				double powerGain = feature.getProperty("power").getAsDouble();
				if (coinGain < 0) {

					coinGain = Math.max(coinGain, (-1) * this.coins); // Drone can only loose as much coins as
																		// it has. No debt.
				}

				if (powerGain < 0) {

					powerGain = Math.max(powerGain, (-1) * this.power); // Drone can only loose as much power as
																		// it has. No debt.
				}

				utility = this.getUtilityOfStation(coinGain, powerGain);

				move = new Move(direction, coinGain, powerGain, utility, feature);
				nearestDistance = distanceToStation;

			}

		}

		return move;

	}

	public LinkedList<Direction> getPathToPosition(Position currentPosition, Position goalPosition) {

		LinkedList<Direction> path = new LinkedList<Direction>();

		double distanceToGoal = Double.POSITIVE_INFINITY;
		while (distanceToGoal > 0.00025) {

			Direction shortestDirection = null;

			double shortestDistance = Double.POSITIVE_INFINITY;

			for (Direction direction : Direction.values()) {
				Position potentialPosition = currentPosition.nextPosition(direction);
				double distance = potentialPosition.getDistanceToPosition(goalPosition);

				if (distance <= shortestDistance) {

					shortestDistance = distance;
					shortestDirection = direction;

				}

			}

			currentPosition = currentPosition.nextPosition(shortestDirection);

			distanceToGoal = currentPosition.getDistanceToPosition(goalPosition);

			path.add(shortestDirection);

		}

		return path;

	}

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

		// Pick random zero-utility move if there exists one and if no station with
		// positive utility was found

		if (currentBestMove == null || (currentBestMove.utility < 0 && zeroUtilityMoves.size() > 0)) {
			// System.out.println("Size" + zeroUtilityMoves.size());

			if (zeroUtilityMoves.size() == 1) {
				currentBestMove = zeroUtilityMoves.get(0);
			} else if (zeroUtilityMoves.size() != 0) {
				currentBestMove = zeroUtilityMoves.get(0);

				currentBestMove = zeroUtilityMoves.get(this.random.nextInt(zeroUtilityMoves.size() - 1));

			}

		}

		return currentBestMove.direction;

	}

}