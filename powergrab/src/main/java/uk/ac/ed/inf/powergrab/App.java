
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
import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Queue;

public class App {
	private String mapSource; 
	private Position startPosition;
	private Drone drone;
	private int seed;
	private String droneType;
	private String date;

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
		String fileNamePrefix = String.format("%s-%s.", this.droneType, this.date);

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

		Queue<Direction> nextMoves = this.drone.nextMoves();

		while (nextMoves.size() > 0 && this.drone.move(nextMoves)) {

			nextMoves = this.drone.nextMoves();

		}

		System.out.println(this.drone.coins);
		System.out.println(this.drone.power);
		this.drone.flightBWriter.close();
		this.drone.writeFlightPath();

	}

	public static void main(String[] args) throws IOException, CloneNotSupportedException {

		App game = new App(args[0], args[1], args[2], args[3], args[4], args[5], args[6]);
		game.play();

	}

}
