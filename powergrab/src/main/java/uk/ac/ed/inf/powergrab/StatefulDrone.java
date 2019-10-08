/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.io.IOException;

// Lack of modifier indicates that the following class is package-private

class StatefulDrone extends Drone {

	StatefulDrone(Position startPosition, String mapSource, int seed, String fileNamePrefix) throws IOException {
		super(startPosition, mapSource, seed, fileNamePrefix);
	}

	Move nextMove() {
		return new Move(Direction.N,0.0,0.0,0.0,null);

	}

}