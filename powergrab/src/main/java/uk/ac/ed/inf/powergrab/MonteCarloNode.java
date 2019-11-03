package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import com.mapbox.geojson.Feature;

class MonteCarloNode {

	int num_plays = 0;
	double total_coins = 0;
	
	MonteCarloNode parent;
	ArrayList<MonteCarloNode> children = new ArrayList<MonteCarloNode>();
	ArrayList<Direction> path = new ArrayList<Direction>();
	Stack<Direction> availableNextDirections  = new Stack<Direction>();
	int depth = 0;
	Position position;
	Direction direction;
	Drone simulationDrone;
	String mapSource;

	MonteCarloNode(MonteCarloNode parent, Position position, Direction direction, String mapSource) throws IOException, CloneNotSupportedException {
		this.position = position;
		this.parent = parent;
		this.direction = direction;
		this.mapSource = mapSource;
		this.depth = (parent == null) ? 0 : parent.depth + 1;
		
		//System.out.print("depth"+this.depth);
		if (parent == null) {
			this.simulationDrone = new StatelessDrone(position, 250, mapSource, 5678, "simulation.");
			
		}
		else {
			// Create simulation drone based on path of current leaf
		
		this.simulationDrone =  (Drone) parent.simulationDrone.clone(); //new StatelessDrone(parent.simulationDrone.position, parent.simulationDrone.power, parent.simulationDrone.mapSource,  0, "simulation.");
		this.simulationDrone.move(this.simulationDrone.getMoveInDirection(parent.position, direction));
		
		this.path = ((ArrayList<Direction>) parent.path.clone());
		this.path.add(direction);
		}
		//TODO: Check that it is in play area
		
		for (Direction potentialDirection : Direction.values()) {
			if((position.nextPosition(potentialDirection)).inPlayArea())
			{
				availableNextDirections.add(potentialDirection);
			}
			

		}

	}

	MonteCarloNode getNextChild() throws IOException, CloneNotSupportedException {

		if (this.depth < 250) {

			// TODO: Get random child?
			Direction direction = availableNextDirections.pop();
			MonteCarloNode child = new MonteCarloNode(this,this.position.nextPosition(direction), direction, this.mapSource );
			children.add(child);
			return child;
		} else {

			return null;
		}

	}

	double getUCB1(double biasParam) {
		// TODO: Check for null value of parent
		return (this.total_coins / this.num_plays)
				+ Math.sqrt(biasParam * Math.log(this.parent.num_plays) / this.num_plays);
	}

	boolean isFullyExpanded() {
		//TODO: check if this.depth check is necessary

		return this.availableNextDirections.size() == 0 || this.depth >= 249;
	}
}