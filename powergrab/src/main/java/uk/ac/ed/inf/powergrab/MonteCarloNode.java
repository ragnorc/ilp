package uk.ac.ed.inf.powergrab;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Point;

class MonteCarloNode {

	int num_plays = 0;
	double total_coins = 0;

	MonteCarloNode parent;
	ArrayList<MonteCarloNode> children = new ArrayList<MonteCarloNode>();
	ArrayList<Direction> previous_path = new ArrayList<Direction>();
	ArrayList<Direction> future_path = new ArrayList<Direction>();
	// Stack<Feature> availableNextFeatures = new Stack<Feature>();
	ArrayList<Feature> features;
	int depth = 0;
	Position goalPosition;
	Direction direction;
	Drone simulationDrone;
	String mapSource;

	MonteCarloNode(MonteCarloNode parent, Position goalPosition, String mapSource, ArrayList<Feature> features)
			throws IOException, CloneNotSupportedException {
		this.goalPosition = goalPosition;
		this.parent = parent;
		// this.direction = direction;
		this.mapSource = mapSource;
		this.depth = (parent == null) ? 0 : parent.depth + 1;
		this.features = features;

		// System.out.print("depth"+this.depth);
		if (parent != null) {
			// Create simulation drone based on path of current leaf
			/*
			 * this.simulationDrone = (Drone) parent.simulationDrone.clone(); // new //
			 * StatelessDrone(parent.simulationDrone.position, //
			 * parent.simulationDrone.power, // parent.simulationDrone.mapSource, 0, //
			 * "simulation.");
			 * this.simulationDrone.move(this.simulationDrone.getMoveInDirection(parent.
			 * position, direction));
			 */

			// Compute previous path
			this.previous_path = ((ArrayList<Direction>) parent.previous_path.clone());

			// this.previous_path.add(direction);
		}
		// TODO: Check that it is in play area

		/*
		 * for (Feature feature : features) { // Add deep clone of Feature
		 * availableNextFeatures.add(Feature.fromJson(feature.toJson()));
		 * 
		 * 
		 * 
		 * }
		 */

		// TODO: Compute future path from parent goal position to this.goalposition
		if (parent != null) {

			this.previous_path.addAll(parent.goalPosition.getPathToPosition(goalPosition));

		}

	}

	MonteCarloNode getNextChild() throws IOException, CloneNotSupportedException {

		// TODO: Get random child? Time complexity for removing first element
		if (this.features.size() == 0) {
			return null;
		}

		Feature childFeature = this.features.remove(0);
		Position goalPosition = new Position(((Point) childFeature.geometry()).latitude(),
				((Point) childFeature.geometry()).longitude());
		// TODO: Make a deep clone?
		ArrayList<Feature> childFeatures = ((ArrayList<Feature>) this.features.clone());

		MonteCarloNode child = new MonteCarloNode(this, goalPosition, this.mapSource, childFeatures);
		children.add(child);
		return (child.previous_path.size() <= 250) ? child : null;

	}

	double getUCB1(double biasParam) {
		// TODO: Check for null value of parent
		return (this.total_coins / this.num_plays)
				+ Math.sqrt(biasParam * Math.log(this.parent.num_plays) / this.num_plays);
	}

	boolean isFullyExpanded() {
		// TODO: check if this.depth check is necessary

		return this.features.size() == 0 && this.children.size() > 0;
	}
}