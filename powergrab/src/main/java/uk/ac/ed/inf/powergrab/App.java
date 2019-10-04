
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

public class App {
	private String mapSource; // url object of map
	private Position startPosition;
	private Drone drone;

	// construct a new game with given parameters
	public App(URL mapUrl, Position startPosition, String droneType) throws IOException {
		
		this.startPosition = startPosition;
		
		this.mapSource = getMap(mapUrl);
		System.out.println(this.mapSource);

		switch (droneType) {
		case "stateless":
			drone = new StatelessDrone(this.startPosition);
			break;
		case "stateful":
			drone = new StatefulDrone(this.startPosition);
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

	public void play() {

	}

	public static void main(String[] args) throws IOException {

		// Construct string of URL to download the map from
		String mapString = String.format("http://homepages.inf.ed.ac.uk/stg/powergrab/%s/%s/%s/powergrabmap.geojson",
				args[2], args[1], args[0]);

		// Construct an URL object from mapString
		URL mapUrl = new URL(mapString);

		Position startPosition = new Position(Double.parseDouble(args[3]), Double.parseDouble(args[4]));

		App game = new App(mapUrl, startPosition, args[6]);

	}

}
