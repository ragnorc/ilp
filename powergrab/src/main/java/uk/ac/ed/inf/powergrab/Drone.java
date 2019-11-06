/******************************************************************************
 *  Class:   Drone 
 *  Description: Implements the attributes and methods that are shared by both the stateless and stafeful drone.
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

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
	protected ArrayList<Direction> path = new ArrayList<Direction>();
	//TODO: Replace arrayList by stack
	protected ArrayList<Direction> pathToFollow = new ArrayList<Direction>();

	Drone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix ) throws IOException {
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

	abstract Move nextMove() throws IOException, CloneNotSupportedException;

	void move(Move move) throws IOException {
		

		//System.out.println("Writestring"+i);
		String writeString = this.position.latitude + " " + this.position.longitude + " " + move.direction.name();
		this.position = this.position.nextPosition(move.direction);
		this.path.add(move.direction);
		writeString += " " + this.position.latitude + " " + this.position.longitude;
		Point nextPoint = Point.fromLngLat(this.position.longitude, this.position.latitude);
		visitedPoints.add(nextPoint);
		this.power = this.power + move.powerGain - 1.25;
		this.coins = this.coins + move.coinGain;
		writeString += String.format(" %f %f", this.coins, this.power);
		this.numMoves++;
		if (move.feature != null) {
			double oldCoins = move.feature.getProperty("coins").getAsDouble();
			double oldPower = move.feature.getProperty("power").getAsDouble();
			// Feature updatedFeature = move.feature);
			// int featureIndex = this.features.indexOf(move.feature);
			//System.out.println(move.feature);
			move.feature.removeProperty("coins");
			move.feature.removeProperty("power");
			move.feature.addStringProperty("coins", Double.toString(oldCoins - move.coinGain));
			move.feature.addStringProperty("power", Double.toString(oldPower - move.powerGain));
			//System.out.println("test" + move.powerGain);

		}

		this.flightBWriter.write(writeString);
		this.flightBWriter.newLine();

		//System.out.println(move.direction);
	//	System.out.println(this.coins);
		//System.out.println(this.power);
		//System.out.println(this.numMoves);

	}

	void writeFlightPath() throws IOException {

		LineString dronePath = LineString.fromLngLats(this.visitedPoints);
		Feature dronePathFeature = Feature.fromGeometry(dronePath);
		this.orginalFeatures.add(dronePathFeature);
		FeatureCollection featureCollection = FeatureCollection.fromFeatures(this.orginalFeatures);
		String newMap = featureCollection.toJson().toString();
		//System.out.println(newMap);
		try (FileWriter file = new FileWriter(this.fileNamePrefix + "geojson")) {
			file.write(newMap);
			//System.out.println("Successfully wrote flight path to file.");

		}

	}

//TODO: add distance penalization and pass feature instead of coins/power
	double getUtilityOfStation(double stationCoins, double stationPower) {

		return (stationPower / this.power) + (stationCoins / this.coins);

	}
	double getUtilityOfStation(double stationCoins, double stationPower, double distance) {

		return ((stationPower-distance*1.25) / this.power) + (stationCoins / this.coins);

	}
	
	// 

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
	
	public Drone clone() throws
    CloneNotSupportedException 
{ 
	Drone cop = (Drone) super.clone();
	//cop.features = (ArrayList<Feature>) cop.features.clone();
	
return cop; 
} 

}