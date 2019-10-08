package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;

class Move {
	
	final Direction direction;
	final Double coinGain;
	final Double powerGain;
	//final Feature feature;
	final Feature feature;
	
	Move(Direction direction, Double coinGain, Double powerGain, Feature feature) {
		this.direction = direction;
		this.coinGain = coinGain;
		this.powerGain = powerGain;
		this.feature = feature;
	

		
		
	}
	
}