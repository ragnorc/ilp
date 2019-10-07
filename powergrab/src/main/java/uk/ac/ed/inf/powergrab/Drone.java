/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

// Lack of modifier indicates that the following class is package-private

abstract class Drone {

   protected Position position;
   protected double power = 250;
   protected double coins = 0;
   protected ArrayList<Feature> features;
   protected String mapSource;
   protected Random random;
   protected int numMoves = 0;

	Drone(Position startPosition, String mapSource, int seed) {
		this.position = startPosition;
		this.mapSource = mapSource;
		this.random = new Random(seed);
		this.features = (ArrayList<Feature>) FeatureCollection.fromJson(this.mapSource).features();
	}
	
	abstract Move nextMove();
	
	void move() {
		Move move = this.nextMove();
		this.position = this.position.nextPosition(move.direction);
		this.power = this.power + move.powerGain;
		this.coins = this.coins + move.coinGain;
		numMoves++;
		if (move.featureIndex != null) {
		this.features.remove((int)move.featureIndex);
		System.out.println(this.features.get(move.featureIndex));
		}
		
		
		System.out.println(move.direction);
		System.out.println(move.powerGain);
		System.out.println(move.coinGain);
		System.out.println(this.numMoves);
		
	}
	
	 double getUtility(double stationCoins, double stationPower) {
		
		return (stationPower/this.power) + (stationCoins/this.coins);
		
	}

}