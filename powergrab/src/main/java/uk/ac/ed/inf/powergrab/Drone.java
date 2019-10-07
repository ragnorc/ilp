/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.util.Random;

// Lack of modifier indicates that the following class is package-private

abstract class Drone {

   protected Position position;
   protected double power = 250;
   protected double coins = 0;
   protected String mapSource;
   protected Random random;
   protected int numMoves = 0;

	Drone(Position startPosition, String mapSource, int seed) {
		this.position = startPosition;
		this.mapSource = mapSource;
		this.random = new Random(seed);
	}
	
	abstract Move nextMove();
	
	void move(Direction direction) {
		Move move = this.nextMove();
		this.position = this.position.nextPosition(move.direction);
		this.power = this.power + move.powerGain;
		this.coins = this.coins + move.coinGain;
		numMoves++;
		System.out.println(move.direction);
		
	}
	
	 double getUtility(double stationPower, double stationCoins) {
		
		return (this.power/stationPower) + (this.coins/stationCoins);
		
	}

}