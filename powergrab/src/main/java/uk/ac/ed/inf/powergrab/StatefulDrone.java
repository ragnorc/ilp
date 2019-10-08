/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

// Lack of modifier indicates that the following class is package-private

class StatefulDrone extends Drone {

	StatefulDrone(Position startPosition, String mapSource, int seed) {
		super(startPosition,mapSource, seed);

	}

	Move nextMove() {
		return new Move(Direction.N,0.0,0.0,null,);

	}

}