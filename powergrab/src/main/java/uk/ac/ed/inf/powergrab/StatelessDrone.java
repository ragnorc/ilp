/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

// Lack of modifier indicates that the following class is package-private

class StatelessDrone extends Drone {

	StatelessDrone(Position startPosition, String mapSource, int seed, String fileNamePrefix) throws IOException {
		super(startPosition, mapSource, seed, fileNamePrefix);

	}

	Move nextMove() {

		List<Direction> randomDirections = new ArrayList<Direction>(EnumSet.allOf(Direction.class));
		Move currentBestMove = null;
		for (Direction potentialDirection : Direction.values()) {

			Position potentialPosition = this.position.nextPosition(potentialDirection);
			if (potentialPosition.inPlayArea()) {
				List<Feature> featuresInReach = new ArrayList<Feature>();
				double currentBiggestUtility = 0; // 0 utility corresponds to moving somewhere where there is nothing

				for (Integer i = 0; i < this.features.size(); i++) {
					Feature feature = this.features.get(i);
					double longitude = ((Point) feature.geometry()).coordinates().get(0);
					double latitude = ((Point) feature.geometry()).coordinates().get(1);

					if ((Math.pow(longitude - potentialPosition.longitude, 2)
							+ Math.pow(latitude - potentialPosition.latitude, 2)) <= Math.pow(0.00025, 2)) {

						randomDirections.removeIf(p -> p.equals(potentialDirection)); // Don't consider points where
																						// there are features for random
																						// selection of move
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

						double utility = this.getUtility(coinGain, powerGain);

						if (utility > currentBiggestUtility) {
							currentBestMove = new Move(potentialDirection, coinGain, powerGain, feature);

						}

						// System.out.println(feature);

					}

				}

			}

		}
		;

		if (currentBestMove == null) {
			currentBestMove = new Move(randomDirections.get(this.random.nextInt(randomDirections.size() - 1)), 0.0,0.0, null);
		}

		return currentBestMove;

	}
}