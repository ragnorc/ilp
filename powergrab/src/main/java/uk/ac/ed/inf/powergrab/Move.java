package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;

class Move {
	
	final Direction direction;
	final Double coinGain;
	final Double powerGain;
	//final Feature feature;
	final Feature feature;
	final Double utility;
	
	Move(Direction direction, Double coinGain, Double powerGain, Double utility, Feature feature) {
		this.direction = direction;
		this.coinGain = coinGain;
		this.powerGain = powerGain;
		this.feature = feature;
		this.utility = utility;
	

		
		
	}
	
}