/******************************************************************************
 *  Class:       Move
 *  Author:  	 s1614102 
 *  Description: The Move class describes a move in a certain direction using
 *  			 attributes such as the (potentially negative) gain in power 
 *  			 and coins, the overall utility of the move, and the station
 *  			 the drone is connecting it to on performing that move. In case no station 
 *  			 is in reach, the attribute "feature" is just null. The attributes are 
 *  			 declared as final because the compute properties of a move don't change.
 *
 ******************************************************************************/

package uk.ac.ed.inf.powergrab;

import com.mapbox.geojson.Feature;

class Move {

	final Direction direction;
	final Double coinGain;
	final Double powerGain;
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