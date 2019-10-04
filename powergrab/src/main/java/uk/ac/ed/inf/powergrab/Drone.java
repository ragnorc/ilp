/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

// Lack of modifier indicates that the following class is package-private

abstract class Drone {

   private Position position;

	Drone(Position startPosition) {
		this.position = startPosition;
	}
	
	abstract String nextMove();

}