/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.mapbox.geojson.Feature;

// Lack of modifier indicates that the following class is package-private

class StatefulDrone extends Drone {

	StatefulDrone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);
	}

	Move nextMove() throws IOException, CloneNotSupportedException {
		
		
		if (this.numMoves < 250 && this.power > 0) {
			
			if (this.pathToFollow.size() > 0) {
				//System.out.println("Perform path sequesssnce to node.");
                 // Perform moves to reach position of current node
				// TODO: check time complexity of removing

				return this.getMoveInDirection(this.position, this.pathToFollow.remove(0));

			}
			
			else {

		MonteCarloNode root = new MonteCarloNode(null, this.position, this.mapSource,
				this.features,this.path);

		int i = 0;
	/*TODO: Change back to 200*/	while (i < 5000) {
			MonteCarloNode leaf = selection(root);
			double simulation_result = rollout(leaf);
			backpropagation(leaf, simulation_result);
			//System.out.println(leaf.goalPosition.longitude+" hi "+leaf.goalPosition.latitude+"res "+simulation_result+" initpow ");
			i++;

		}
	
	if(root.children.size() == 0) {
		
		return null; //TODO: Return random move
	}

		//System.out.println("Next move found");
		MonteCarloNode best = this.bestChild(root);
		// System.out.println("biggest"+best.num_plays+ " "+best.coins);
		this.pathToFollow.addAll(this.position.getPathToPosition(best.goalPosition));
		return this.getMoveInDirection(this.position, this.pathToFollow.remove(0));
			}

		//return this.getMoveInDirection(this.position, this.bestChild(root).direction);
		}
		
		else {
			return null;
		}
		
		

	}

	MonteCarloNode selection(MonteCarloNode node) throws IOException, CloneNotSupportedException {

		while (node.isFullyExpanded()) {

			MonteCarloNode maxUCBChild = node.children.get(0);

			for (MonteCarloNode child : node.children) {
				// TODO: SET bias parameter
				//System.out.println("child" + child.originalFeatures.size()+"hi"+child.depth);
				if (child.getUCB1(300) > maxUCBChild.getUCB1(300)) {

					maxUCBChild = child;

				}
				  //System.out.println("ucb"+child.getUCB1(100));
				// System.out.println("yes"+child.position.longitude);
				//System.out.println("Child :"+child.num_plays + " "+child.coins);


			}

			node = maxUCBChild;
			// System.out.println("biggest"+maxUCBChild.num_plays+ " "+maxUCBChild.coins);
		}

		MonteCarloNode child = node.getNextChild();
		// System.out.println("childNotUCB"+child.originalFeatures.size()+" "+child.depth);

		return child == null ? node : child;

	}

	double rollout(MonteCarloNode node) throws IOException, CloneNotSupportedException {
		// TODO: Generate seed randomly
		RolloutDrone rolloutDrone = new RolloutDrone(this.startPosition, 250, this.mapSource, 1000 + this.random.nextInt(9000), node.pathFromRoot, "sim"+node.goalPosition.latitude+".");
		//System.out.println("SimulationStart");
		Move nextMove = rolloutDrone.nextMove();
		int i = 1;
		while (nextMove != null) {
			rolloutDrone.move(nextMove);
			nextMove = rolloutDrone.nextMove();
			i++;
			//System.out.println("Simulation");
			
		}
		
			rolloutDrone.writeFlightPath();
		
		
		rolloutDrone.flightBWriter.close();
		//System.out.println(rolloutDrone.coins);
		//System.out.println(rolloutDrone.power);
		return rolloutDrone.coins;

	}

	void backpropagation(MonteCarloNode node, double result) {
		if (node != null) // TODO: Check if aprent.node check makes more sense
		{
			node.num_plays = node.num_plays + 1;
			node.total_coins = node.total_coins + result;
			backpropagation(node.parent, result);
			// System.out.println(node.num_plays);

		}

	}

	MonteCarloNode bestChild(MonteCarloNode node) {

		MonteCarloNode maxChild = node.children.get(0);

		for (MonteCarloNode child : node.children) {
			//System.out.println("childVisits" + child.num_plays);

			// TODO: SET bias parameter
			if (child.num_plays > maxChild.num_plays) {

				maxChild = child;

				// System.out.println("bigger");

			}
			// System.out.println("ucb"+child.getUCB1(100));
			// System.out.println("yes"+child.position.longitude);

		}
		return maxChild;

	}
	
	/*
	void move(Move move) throws IOException {
		System.out.println(move.direction.name()+" MoveDrone ");
		super.move(move);
		
	
	}
	*/

}