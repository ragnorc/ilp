package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

//Lack of modifier indicates that the following class is package-private

class RolloutDrone extends Drone {
	

	RolloutDrone(Position startPosition, double power, String mapSource, int seed, ArrayList<Direction> pathToFollow, String fileNamePrefix)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);
		this.pathToFollow = pathToFollow;

	}

	Move nextMove() {

		// Run previous path to update available features, power and coins

		if (this.numMoves < 250 && this.power > 0) {

			if (this.pathToFollow.size() > 0) {
			//	System.out.println("Perform path sequence to node.");
                 // Perform moves to reach position of current node
				// TODO: check time complexity of removing

				return this.getMoveInDirection(this.position, this.pathToFollow.remove(0));

			}

			else {
              //  System.out.println("Check Features");

                WeightedRandomCollection<ArrayList<Direction>> availablePaths = new WeightedRandomCollection<>(this.random);

				//double biggestUtility = Double.NEGATIVE_INFINITY;
				for (Feature feature : this.features) {
					double longitude = ((Point) feature.geometry()).coordinates().get(0);
					double latitude = ((Point) feature.geometry()).coordinates().get(1);
					Position featurePosition = new Position(latitude, longitude);
					ArrayList<Direction> path = this.position.getPathToPosition(featurePosition);
					if (path.size() < (250 - this.numMoves) && path.size() * 1.25 <= this.power) {

						double stationCoins = feature.getProperty("coins").getAsDouble();
						double stationPower = feature.getProperty("power").getAsDouble();

						availablePaths.add(this.getUtilityOfStation(stationCoins, stationPower,this.position.getDistanceToPosition(featurePosition)), path);

					}

				}
				// Rollout policy. Pick feature with probability proportional to its utility. Implement distance dependence

				if (availablePaths.size() > 0) {
                   // System.out.println("Probabilistc Move"+availablePaths.size());

					this.pathToFollow.addAll(availablePaths.next());
					return this.getMoveInDirection(this.position, this.pathToFollow.remove(0));

				}

				else {
                    //System.out.println("No features available. Return random move.");
					return this.getMoveInDirection(this.position, Direction.values()[this.random.nextInt(16)]);
                   

				}

			}

		}

		else {
           // System.out.println("End"+this.numMoves);

			return null;
		}

	}

}