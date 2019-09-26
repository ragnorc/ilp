package uk.ac.ed.inf.powergrab;
public class Position {
public double latitude;
public double longitude;

public Position(double latitude, double longitude) {
	this.latitude = latitude;
	this.longitude = longitude;
	
}
public Position nextPosition(Direction direction) {
	   double r = 0.0003;
	   double degree = direction.ordinal() * 22.5;
	   double newLong = this.longitude + r * Math.cos(90 - degree);
	   double newLat = this.latitude + r * Math.sin(90 - degree);
	   
	   return new Position(newLat, newLong);
	
}
//public boolean inPlayArea() {  }

}