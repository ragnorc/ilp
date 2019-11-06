package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;

public class Position {
	public double latitude;
	public double longitude;

	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;

	}

	public Position nextPosition(Direction direction) {
		double r = 0.0003;
		double degree = 90 - direction.ordinal() * 22.5;
		double radian = Math.toRadians(degree);
		double newLong = this.longitude + r * Math.cos(radian);
		double newLat = this.latitude + r * Math.sin(radian);

		return new Position(newLat, newLong);

	}

	public boolean inPlayArea() {

		return this.latitude < 55.946233 && this.latitude > 55.942617 && this.longitude < -3.184319
				&& this.longitude > -3.192473;

	}

	public ArrayList<Direction> getPathToPosition(Position goalPosition) {

		// TODO: Computer direction using angle; Make sure path is in play area and
		// avoid negative stations on the way. Handle null returns

		Position currentPosition = this;
		ArrayList<Direction> path = new ArrayList<Direction>();

		double distanceToGoal = Double.POSITIVE_INFINITY;
		int i = 0;
		while (distanceToGoal > 0.00025) {
			i++;

			Direction shortestDirection = null;
			double shortestDistance = distanceToGoal;
			for (Direction direction : Direction.values()) {
				Position potentialPosition = currentPosition.nextPosition(direction);
				double distance = potentialPosition.getDistanceToPosition(goalPosition);

				if (distance <= shortestDistance) {

					shortestDistance = distance;
					shortestDirection = direction;

				}

			}
			currentPosition = currentPosition.nextPosition(shortestDirection);

			distanceToGoal = currentPosition.getDistanceToPosition(goalPosition);

			path.add(shortestDirection);

		}

		return path;

	}

	double getDistanceToPosition(Position position) {

		return Math.sqrt(
				Math.pow(this.longitude - position.longitude, 2) + Math.pow(this.latitude - position.latitude, 2));

	}

}