
/******************************************************************************
 *  Compilation:  javac Student.java StdIn.java
 *  Execution:    java Student n < students.txt
 *
 *  Read in an integer command-line argument n. Then, read in a list
 *  of n student records from standard input into a Student data type.
 *  Each record consists of four fields, separated by whitespace:
 *      - first name
 *      - last name
 *      - email address
 *      - which section they're in
 *
 *  Then, print out the list of the first N students in sorted order,
 *  according to their section number.
 *
 ******************************************************************************/
package uk.ac.ed.inf.powergrab;

import java.net.URL;
import java.util.stream.Collectors;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class App {
	private String mapSource; // url object of map
	private Position startPosition;
	private Drone drone;
	private int seed;
	private String droneType;
	private String date;

	// construct a new game with given parameters
	public App(String day, String month, String year, String startLat, String startLong, String seed, String droneType)
			throws IOException {

		// Construct an URL object from mapString
		URL mapUrl = new URL(String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson",
				year, month, day));
		
		this.date = String.format("%s-%s-%s", day, month, year);

		Position startPosition = new Position(Double.parseDouble(startLat), Double.parseDouble(startLong));

		this.startPosition = startPosition;
		this.seed = Integer.parseInt(seed);
		this.mapSource = getMap(mapUrl);
		this.droneType = droneType;
		String fileNamePrefix = String.format("%s-%s.",this.droneType, this.date);

		switch (droneType) {
		case "stateless":
			drone = new StatelessDrone(this.startPosition, 250, this.mapSource, this.seed, fileNamePrefix);
			break;
		case "stateful":
			drone = new StatefulDrone(this.startPosition, 250, this.mapSource, this.seed, fileNamePrefix);
			break;
		default:
			System.out.println("Drone type does not exist.");
		}
	}

	private String getMap(URL mapUrl) throws IOException {
		InputStream is = mapUrl.openConnection().getInputStream();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			return in.lines().collect(Collectors.joining(System.lineSeparator()));

		} finally {
			is.close();
		}
	}

	public void play() throws IOException, CloneNotSupportedException {
		// TODO: Change max moves to 250 back
		int i = 1;
		while (this.drone.power > 0 && this.drone.numMoves < 10) {
			Move nextMove = this.drone.nextMove();
			if (nextMove == null) {
				System.out.println("Drone got stuck. Ending early");
				break;
			}
			this.drone.move(this.drone.nextMove());
			//System.out.println("Move"+i);
			i++;
		}
		
		System.out.println(this.drone.coins);
		System.out.println(this.drone.power);
		this.drone.writeFlightPath();
		
	}



	public static void main(String[] args) throws IOException, CloneNotSupportedException {

		App game = new App(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
		game.play();

	}

}
