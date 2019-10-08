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
			double oldCoins = move.feature.getProperty("coins").getAsDouble();
			double oldPower = move.feature.getProperty("power").getAsDouble();
			//Feature updatedFeature = move.feature);
			//int featureIndex = this.features.indexOf(move.feature);
			System.out.println(move.feature);
			move.feature.removeProperty("coins");
			move.feature.removeProperty("power");
			move.feature.addStringProperty("coins",Double.toString(oldCoins-move.coinGain));
			move.feature.addStringProperty("power",Double.toString(oldPower-move.powerGain));
			System.out.println("test"+move.powerGain);
			//System.out.println(this.features.get(this.features.indexOf(this.features.get(0))));
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