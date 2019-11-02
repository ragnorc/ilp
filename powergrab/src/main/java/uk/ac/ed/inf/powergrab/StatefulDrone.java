/******************************************************************************
 *  Class:   Drone 
 *  Author:  Ragnor Comerford
 *
 *
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.io.IOException;

// Lack of modifier indicates that the following class is package-private

class StatefulDrone extends Drone {

	StatefulDrone(Position startPosition, double power, String mapSource, int seed, String fileNamePrefix)
			throws IOException {
		super(startPosition, power, mapSource, seed, fileNamePrefix);
	}

	Move nextMove() throws IOException, CloneNotSupportedException {

		MonteCarloNode root = new MonteCarloNode(null, this.position, null, this.mapSource);

		int i = 0;
		while (i < 100) {
			MonteCarloNode leaf = selection(root);
			double simulation_result = rollout(leaf);
			backpropagation(leaf, simulation_result);
			 System.out.println(leaf.position.longitude+"hi"+simulation_result);
			i++;

		}
		
		System.out.println("Next move found");

		return this.getMoveInDirection(root.position, this.bestChild(root).direction);

	}

	MonteCarloNode selection(MonteCarloNode node) throws IOException, CloneNotSupportedException {

		while (node.isFullyExpanded()) {

			MonteCarloNode maxUCBChild = node.children.get(0);

			for (MonteCarloNode child : node.children) {
				// TODO: SET bias parameter
				if (child.getUCB1(100) > maxUCBChild.getUCB1(100)) {

					maxUCBChild = child;

					// System.out.println("bigger");

				}
				// System.out.println("ucb"+child.getUCB1(100));
				// System.out.println("yes"+child.position.longitude);

			}
			node = maxUCBChild;

		}

		MonteCarloNode child = node.getNextChild();

		return child == null ? node : child;

	}

	double rollout(MonteCarloNode node) throws IOException, CloneNotSupportedException {
		Position preRolloutPosition = node.simulationDrone.position;
		double preRolloutCoins = node.simulationDrone.coins;
		double preRolloutPower = node.simulationDrone.power;
		
		Drone originalSimulationDrone = (Drone) node.simulationDrone.clone();
		System.out.println("init power"+node.simulationDrone.power);

		while (node.simulationDrone.power > 0 && node.simulationDrone.numMoves < 250) {
			node.simulationDrone.move(node.simulationDrone.nextMove());
			//System.out.println("sim"+node.simulationDrone.coins);
		}

		double result = node.simulationDrone.coins;
		//node.simulationDrone.writeFlightPath();
		System.out.println(result);

		// Reset simulation drone to original values for usage by children
		//node.simulationDrone.coins = preRolloutCoins;
		//node.simulationDrone.power = preRolloutPower;
		//node.simulationDrone.position = preRolloutPosition;
		node.simulationDrone = originalSimulationDrone;

		return result;

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

}