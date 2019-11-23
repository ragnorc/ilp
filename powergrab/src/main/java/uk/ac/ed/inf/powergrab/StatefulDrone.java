/******************************************************************************
 *  Class:       StatefulDrone
 *  Author:  	 s1614102 
 *  Description: The following class implements the strategy of the stateful drone 
 *  			 for determining the best next moves.
 *
 ******************************************************************************/

package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.Queue;

//Lack of modifier indicates that the following class is package-private

class StatefulDrone extends Drone {
	
	int numSimulations;

	StatefulDrone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix, int numSimulations)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);
		this.numSimulations = numSimulations;

	}

	Queue<Direction> nextMoves() throws IOException {

		int i = 0;
		MonteCarloDrone bestRolloutDrone = null;

		while (i < this.numSimulations) {

			MonteCarloDrone rolloutDrone = new MonteCarloDrone(this.startPosition, 250, this.mapSource,
					1000 + this.random.nextInt(9000), "mcsim.");

			Queue<Direction> nextMoves = rolloutDrone.nextMoves();

			while (nextMoves.size() > 0 && rolloutDrone.move(nextMoves)) {

				nextMoves = rolloutDrone.nextMoves();

			}

			if (bestRolloutDrone == null || rolloutDrone.coins > bestRolloutDrone.coins) {

				bestRolloutDrone = rolloutDrone;

			}

			rolloutDrone.writeFlightPath();

			rolloutDrone.flightBWriter.close();
			i++;

		}

		return bestRolloutDrone.path;

	}

}