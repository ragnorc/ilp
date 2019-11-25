/******************************************************************************
 *  Class:       StatelessDrone
 *  Author:  	 s1614102 
 *  Description: The following class is a subclass of Drone and implements the 
 *  			 strategy of the stateless drone for determining the best next moves.
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;


// Lack of modifier indicates that the following class is package-private

class StatelessDrone extends Drone {

	StatelessDrone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);

	}

	Queue<Direction> nextMoves() throws IOException {

		LinkedList<Direction> nextDirections  = new LinkedList<Direction>();
		// Computes the best direction among all 16 available directions (The logic of that method is explained in the Drone class and the report).
		nextDirections.add(this.getBestRandomDirection());
		return nextDirections;
		

	}
}