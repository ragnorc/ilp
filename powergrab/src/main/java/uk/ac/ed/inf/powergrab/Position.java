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
		
		//TODO: Make sure path is in play area and avoid negative stations on the way. Handle null returns 

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
				double distance = Math.sqrt(Math.pow(potentialPosition.longitude - goalPosition.longitude, 2)
						+ Math.pow(potentialPosition.latitude - goalPosition.latitude, 2));

				if (distance <= shortestDistance) {

					shortestDistance = distance;
					shortestDirection = direction;

				}

			}
			currentPosition = currentPosition.nextPosition(shortestDirection);

			distanceToGoal = Math.sqrt(Math.pow(goalPosition.longitude - currentPosition.longitude, 2)
					+ Math.pow(goalPosition.latitude - currentPosition.latitude, 2));
			System.out.println(distanceToGoal);
			path.add(shortestDirection);

		}
		System.out.println("path" + i);

		return path;

	}

}