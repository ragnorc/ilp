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
		this.power = this.power + move.powerGain - 1.25;
		this.coins = this.coins + move.coinGain;
		numMoves++;
		if (move.feature != null) {
			double oldCoins = this.features.get((int)move.featureIndex).getProperty("coins").getAsDouble();
			double oldPower = this.features.get((int)move.featureIndex).getProperty("power").getAsDouble();
			//Feature updatedFeature = move.feature);
			//int featureIndex = this.features.indexOf(move.feature);
			System.out.println(this.features.get((int)move.featureIndex));
			this.features.get((int)move.featureIndex).removeProperty("coins");
			this.features.get((int)move.featureIndex).removeProperty("power");
			this.features.get((int)move.featureIndex).addStringProperty("coins",Double.toString(oldCoins-move.coinGain));
			this.features.get((int)move.featureIndex).addStringProperty("power",Double.toString(oldPower-move.powerGain));
			System.out.println("test"+move.powerGain);
			System.out.println(this.features.get((int)move.featureIndex));
			//this.features.set(featureIndex,move.feature);
			//this.features.add(updatedFeature);

			// System.out.println(this.features.get(move.feature.indexOf()));
		}

		System.out.println(move.direction);
		System.out.println(this.coins);
		System.out.println(this.power);
		System.out.println(this.numMoves);

	}

	double getUtility(double stationCoins, double stationPower) {

		return (stationPower / this.power) + (stationCoins / this.coins);

	}

}