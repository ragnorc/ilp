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
	   double degree = 90 - direction.ordinal() * 22.5;
	   double radian = Math.toRadians(degree);
	   double newLong = this.longitude + r * Math.cos(radian);
	   double newLat = this.latitude + r * Math.sin(radian);
	   
	   return new Position(newLat, newLong);
	
}
public boolean inPlayArea() {
	
return this.latitude < 55.946233 && this.latitude > 55.942617 && this.longitude < -3.184319 && this.longitude > -3.192473;

}

}