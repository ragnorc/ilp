/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
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
		nextDirections.add(this.getBestRandomDirection());
		return nextDirections;
		

	}
}