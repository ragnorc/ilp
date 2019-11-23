package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

//Lack of modifier indicates that the following class is package-private

class MonteCarloDrone extends Drone {
	

	MonteCarloDrone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);

	}

	Queue<Direction> nextMoves() {

                WeightedRandomCollection<LinkedList<Direction>> availablePaths = new WeightedRandomCollection<>(this.random);


				for (Feature feature : this.features) {
					double longitude = ((Point) feature.geometry()).coordinates().get(0);
					double latitude = ((Point) feature.geometry()).coordinates().get(1);
					Position featurePosition = new Position(latitude, longitude);
					LinkedList<Direction> path = this.getPathToPosition(this.position,featurePosition);
					if (path.size() < (250 - this.numMoves) && path.size() * 1.25 <= this.power) {

						double stationCoins = feature.getProperty("coins").getAsDouble();
						double stationPower = feature.getProperty("power").getAsDouble();

						availablePaths.add(this.getUtilityOfStation(stationCoins, stationPower,this.position.getDistanceToPosition(featurePosition)), path);

					}

				}

				if (availablePaths.size() > 0) {
					
					// Rollout policy. Pick feature with probability proportional to its utility. Implement distance dependence

                   
					return availablePaths.next();

				}

				else {
					LinkedList<Direction> ret  = new LinkedList<Direction>();
					ret.add(this.getBestRandomDirection());
					return ret;
                   

				}

			}

		

	

}