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
	ArrayList<Direction> path;

	RolloutDrone(Position startPosition, double power, String mapSource, int seed, ArrayList<Direction> path)
			throws IOException {
		super(startPosition, power, mapSource, seed, "simulation.");
		this.path = path;

	}

	Move nextMove() {

		// Run previous path to update available features, power and coins

		if (this.numMoves < 250 && this.power > 0) {

			if (this.path.size() > 0) {

				// TODO: check time complexity of removing

				return this.getMoveInDirection(this.position, this.path.remove(0));

			}

			else {
				RandomCollection<ArrayList<Direction>> availablePaths = new RandomCollection<>();

				double biggestUtility = Double.NEGATIVE_INFINITY;
				for (Feature feature : this.features) {
					double longitude = ((Point) feature.geometry()).coordinates().get(0);
					double latitude = ((Point) feature.geometry()).coordinates().get(1);
					Position featurePosition = new Position(latitude, longitude);
					ArrayList<Direction> path = this.position.getPathToPosition(featurePosition);
					if (path.size() < (250 - this.numMoves) && path.size() * 1.25 <= this.power) {

						double stationCoins = feature.getProperty("coins").getAsDouble();
						double stationPower = feature.getProperty("power").getAsDouble();

						availablePaths.add(this.getUtilityOfStation(stationCoins, stationPower), path);

					}

				}
				// Rollout policy. Pick feature with probability proportional to its utility

				if (availablePaths.size() > 0) {
					this.path.addAll(availablePaths.next());
					return this.getMoveInDirection(this.position, this.path.remove(0));

				}

				else {

					return this.getMoveInDirection(this.position, Direction.values()[this.random.nextInt(16)]);

				}

			}

		}

		else {
			return null;
		}

	}

}