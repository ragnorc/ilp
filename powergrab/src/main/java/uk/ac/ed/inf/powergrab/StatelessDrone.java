/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

// Lack of modifier indicates that the following class is package-private

class StatelessDrone extends Drone {

	StatelessDrone(Position startPosition, String mapSource, int seed) {
		super(startPosition, mapSource, seed);

	}

	Move nextMove() {
		List<Direction> randomDirections = Arrays.asList(Direction.values());
		Move currentBestMove = null;
		for (Direction potentialDirection : Direction.values()) {

			Position potentialPosition = this.position.nextPosition(potentialDirection);
			if (potentialPosition.inPlayArea()) {

				List<Feature> features = FeatureCollection.fromJson(mapSource).features();
				List<Feature> featuresInReach = new ArrayList<Feature>();
				double currentBiggestUtility = 0; // 0 utility corresponds to moving somewhere where there is nothing

				for (Feature feature : features) {
					double longitude = ((Point) feature.geometry()).coordinates().get(0);
					double latitude = ((Point) feature.geometry()).coordinates().get(1);

					if ((Math.pow(longitude - potentialPosition.longitude, 2)
							+ Math.pow(latitude - potentialPosition.latitude, 2)) <= Math.pow(0.00025, 2)) {

						randomDirections.removeIf(p -> p.equals(potentialDirection)); // Don't consider points where
																						// there are features for random
																						// selection of move
						double coinGain = feature.getProperty("coins").getAsDouble();
						double powerGain = feature.getProperty("power").getAsDouble();
						double utility = this.getUtility(coinGain, powerGain);

						if (utility > currentBiggestUtility) {
							currentBestMove = new Move(potentialDirection, coinGain, powerGain - 1.25);

						}

						System.out.println(feature);

					}

				}

			}

		}
		;

		if (currentBestMove == null) {
			currentBestMove = new Move(randomDirections.get(this.random.nextInt(randomDirections.size() - 1)), 0.0,
					-1.25);
		}

		return currentBestMove;

	}
}