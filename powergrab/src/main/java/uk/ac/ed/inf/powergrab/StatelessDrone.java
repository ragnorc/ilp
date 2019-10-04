/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

// Lack of modifier indicates that the following class is package-private

class StatelessDrone extends Drone {

	StatelessDrone(Position startPosition) {
		super(startPosition);

	}

	String nextMove() {
		return "";

	}
}