package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;



/******************************************************************************
 *  Class:       MonteCarloDrone
 *  Author:  	 s1614102 
 *  Description: This class implements the Monte Carlo drone that is used by the
 *  			 stateful drone to run the simulations to find the best path. The Monte Carlo
 *  			 drone itself selects its moves according to a probability distribution
 *  			 in which the probability of a move is proportional to its utility. That way
 *  			 the Monte Carlo drone will occasionally perform non-greedy moves that
 *  			 might lead to more optimal path overall.
 *
 ******************************************************************************/

class MonteCarloDrone extends Drone {
	

	MonteCarloDrone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);

	}
	
	/**
	 * Method: nextMoves
	 * Description: The following method defines the move selection strategy of the simulation drone.
	 * 				It iterates through all the stations on the map that are available and adds paths to positive-utility
	 *              stations alongside their respective utilities as weights to the custom probabilistic 
	 *              data structure 'WeightedRandomCollection' which is explained in its own class definition. 
	 *              It then randomly picks a path from the random collection and returns it. If the random collection
	 *              is empty, usually when all positive stations have been visited, it picks a random move in the same
	 *              way the stateless drone would pick a random move. 
	 *
	 */


	Queue<Direction> nextMoves() {

                WeightedRandomCollection<LinkedList<Direction>> availablePaths = new WeightedRandomCollection<>(this.random);


				for (Feature feature : this.features) {
					double longitude = ((Point) feature.geometry()).coordinates().get(0);
					double latitude = ((Point) feature.geometry()).coordinates().get(1);
					Position featurePosition = new Position(latitude, longitude);
					LinkedList<Direction> path = this.getPathToPosition(this.position,featurePosition);
					if (path == null) {
						continue;
						
					}
					if (path.size() < (250 - this.numMoves) && path.size() * 1.25 <= this.power) {

						double stationCoins = feature.getProperty("coins").getAsDouble();
						double stationPower = feature.getProperty("power").getAsDouble();
						int numMovesToStation = path.size();
						availablePaths.add(this.getUtilityOfStation(stationCoins, stationPower,numMovesToStation), path);

					}

				}

				if (availablePaths.size() > 0) {
					
					// Rollout policy: Pick feature with probability proportional to its utility.
                   
					return availablePaths.next();

				}

				else {
					//No stations available anymore. Pick the best (random) move.
					LinkedList<Direction> ret  = new LinkedList<Direction>();
					ret.add(this.getBestRandomDirection());
					return ret;
                   

				}

			}

		

	

}