package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;

//Lack of modifier indicates that the following class is package-private

class StatefulDrone extends Drone {

	StatefulDrone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);
		this.pathToFollow = new ArrayList<Direction>();

	}

	Move nextMove() throws IOException {

		// Run previous path to update available features, power and coins

		if (this.numMoves < 250 && this.power > 0) {

			if (this.pathToFollow.size() > 0) {
				// System.out.println("Perform path sequence to node.");
				// Perform moves to reach position of current node
				// TODO: check time complexity of removing

				return this.getMoveInDirection(this.position, this.pathToFollow.remove(0));

			}

			else {
				int i = 0;
				MonteCarloDrone bestRolloutDrone = null;

				while (i < 100) {

					MonteCarloDrone rolloutDrone = new MonteCarloDrone(this.startPosition, 250, this.mapSource,
							1000 + this.random.nextInt(9000), new ArrayList<Direction>(), "mcsim.");

					Move nextMove = rolloutDrone.nextMove();
					int m = 1;
					while (nextMove != null) {
						rolloutDrone.move(nextMove);
						nextMove = rolloutDrone.nextMove();
						m++;

					}

					if (bestRolloutDrone == null || rolloutDrone.coins > bestRolloutDrone.coins) {

						bestRolloutDrone = rolloutDrone;

					}

					rolloutDrone.writeFlightPath();

					rolloutDrone.flightBWriter.close();
					i++;

				}
				this.pathToFollow.addAll(bestRolloutDrone.path);
				return this.getMoveInDirection(this.position, this.pathToFollow.remove(0));

			}

		}

		else {

			return null;
		}

	}

}