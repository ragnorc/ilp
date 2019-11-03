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
		while (i < 900) {
			MonteCarloNode leaf = selection(root);
			double simulation_result = rollout(leaf);
			backpropagation(leaf, simulation_result);
			 //System.out.println(leaf.position.longitude+" hi "+leaf.position.latitude+" res "+simulation_result+" initpow "+leaf.simulationDrone.numMoves);
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
				System.out.println("child"+child.getUCB1(300));
				if (child.getUCB1(300) > maxUCBChild.getUCB1(300)) {

					maxUCBChild = child;

					

				}
				// System.out.println("ucb"+child.getUCB1(100));
				// System.out.println("yes"+child.position.longitude);

			}
			
			node = maxUCBChild;
			System.out.println("biggest"+maxUCBChild.getUCB1(1)+maxUCBChild.direction.name());
		}

		MonteCarloNode child = node.getNextChild();
		System.out.println("childNotUCB"+child.direction.name()+" "+child.depth);


		return child == null ? node : child;

	}

	double rollout(MonteCarloNode node) throws IOException, CloneNotSupportedException {
		Position preRolloutPosition = node.simulationDrone.position;
		double preRolloutCoins = node.simulationDrone.coins;
		double preRolloutPower = node.simulationDrone.power;
		
		//Drone originalSimulationDrone = (Drone) node.simulationDrone.clone();
		
		/* New Tryyy*/
		Drone originalSimulationDrone = new StatelessDrone(this.startPosition,250, this.mapSource,5678, "simulation.");

		for (Direction dir : node.path) {
			originalSimulationDrone.move(originalSimulationDrone.getMoveInDirection(originalSimulationDrone.position, dir));
			
		}
		
		/* New Tryyy*/
		
		//System.out.println(originalSimulationDrone.position.longitude+" hi "+originalSimulationDrone.position.latitude+" mov "+originalSimulationDrone.features.size());

		while (originalSimulationDrone.power > 0 && originalSimulationDrone.numMoves < 250) {
			
			Move nextMove = originalSimulationDrone.nextMove();
			if (nextMove == null) {
				
				break;
			}
			originalSimulationDrone.move(nextMove);
			//System.out.println("sim"+node.simulationDrone.coins);
		}

		double result = originalSimulationDrone.coins;
		//node.simulationDrone.writeFlightPath();
		System.out.println(result);

		// Reset simulation drone to original values for usage by children
		//node.simulationDrone.coins = preRolloutCoins;
		//node.simulationDrone.power = preRolloutPower;
		//node.simulationDrone.position = preRolloutPosition;
		
		//node.simulationDrone = originalSimulationDrone;

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
			System.out.println("childVisits"+child.num_plays);

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