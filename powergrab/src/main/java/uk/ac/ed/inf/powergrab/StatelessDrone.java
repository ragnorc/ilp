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

	StatelessDrone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);

	}

	Move nextMove() {

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
			//System.out.println("Size" + zeroUtilityMoves.size());

			if (zeroUtilityMoves.size() == 1) {
				currentBestMove = zeroUtilityMoves.get(0);
			} else {
				currentBestMove = zeroUtilityMoves.get(this.random.nextInt(zeroUtilityMoves.size() - 1));

			}

		}

		return currentBestMove;

	}
}