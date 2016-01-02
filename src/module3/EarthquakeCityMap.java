package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Salman Hashmi
 * Date: January 1, 2016
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_LIGHT = 4;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(2450, 1400, P2D);
		
		this.background(0, 51, 50);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 2400, 1600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 250, 50, 2400, 1300, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    //map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    // These print statements show you (1) all of the relevant properties 
	    // in the features, and (2) how to get one property and use it
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(0);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	float mag = Float.parseFloat(magObj.toString());
	    	// PointFeatures also have a getLocation method
	    }
	    
	    // Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  
	    // int yellow = color(255, 255, 0);
	    
	    //TODO: Add code here as appropriate
	    // for each PointFeature in List earthquakes,    
	    for (PointFeature pointFeature: earthquakes) {
	    	// create a SimplePointMarker for that pointFeature
	    	
	    	SimplePointMarker currentMarker = createMarker(pointFeature);
	    	
	    	// get magnitude of PointFeature and parse to Float from String
			float magnitude = Float.parseFloat(pointFeature.getProperty("magnitude").toString());
			
			// activate the marker depending on earthquake magnitude
			activateMarkers(currentMarker, magnitude);
			
			// add current SimplePointMarker to ArrayList markers
			markers.add(currentMarker);
	    }
	    // display all markers on the map
	    map.addMarkers(markers);
	}
		
	// A suggested helper method that takes in an earthquake feature and 
	// returns a SimplePointMarker for that earthquake
	// TODO: Implement this method and call it from setUp, if it helps
	private SimplePointMarker createMarker(PointFeature feature)
	{
		// get Location of that PointFeature, and create a new SimplePointMarker with that Location
		// Note: Location represents a geo location defined by latitude and longitude
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation()); 
		
		return marker;
	}
	
	/**
	 *  Change color and size of marker depending on earthquake magnitude scale classification
	 *  
	 *  CLASS		MAGNITUDE	Marker Color and Radius
	 *  Great 		8 or more	Red			60		
	 *  Major 		7 - 7.9		Red			50
	 *  Strong 		6 - 6.9		Orange		40
	 *  Moderate 	5 - 5.9		Yellow		30
	 *  Light 		4 - 4.9		Blue		20
	 *  Minor 		3 -3.9		Green		10
	 *  
	 */
	private void activateMarkers(SimplePointMarker m, float mag) {
		
		if (mag >= 7.0) {
			// set size of marker
			m.setRadius(50);
			// set color
			m.setColor(color(255, 0, 0));
		}
		
		else if (mag >= 6.0 && mag <= 6.9) {
			m.setRadius(40);
			m.setColor(color(255, 128, 0));
		}
		
		else if (mag >= 5.0 && mag <= 5.9) {
			m.setRadius(30);
			m.setColor(color(255, 255, 0));
		}
		
		else if (mag >= 4.0 && mag <= 4.9) {
			m.setRadius(20);
			m.setColor(color(0, 128, 255));
		}
		
		else {
			// less than 4.0
			m.setRadius(10);
			m.setColor(color(0, 255, 0));
		}
	}
	
	public void draw() {
	    background(10);
	    map.draw();
	    addKey();
	}


	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey() 
	{	
		// Remember you can use Processing's graphics methods here
		
		// Rectangle Key
		fill(220, 220, 220);
		rect(50, 50, 500, 1300);
		
		// Key
		fill(0);
		text("KEY", 200, 150);
		text("Class: Magnitude", 200, 200);
		
		// Major, Red
		stroke(255, 0, 0);
		fill(255, 0, 0);
		ellipse(200, 300, 60, 60);
		fill(0);
		text("Major: 7+", 240, 310);
		textSize(25);
		
		// Strong, Orange
		stroke(255, 128, 0);
		fill(255, 128, 0);
		ellipse(200, 400, 50, 50);
		fill(0);
		text("Strong: 6.0-6.9", 240, 410);
		textSize(25);
		
		// Moderate, Yellow
		stroke(255, 255, 0);
		fill(255, 255, 0);
		ellipse(200, 500, 40, 40);
		fill(0);
		text("Moderate: 5.0-5.9", 240, 510);
		textSize(25);
		
		// Light, Blue
		stroke(0, 128, 255);
		fill(0, 128, 255);
		ellipse(200, 600, 30, 30);
		fill(0);
		text("Light: 4.0-4.9", 240, 610);
		textSize(25);
		
		// Minor, Green
		stroke(0, 255, 0);
		fill(0, 255, 0);
		ellipse(200, 700, 20, 20);
		fill(0);
		text("Minor: Below 4.0", 240, 710);
		textSize(25);
	
	}
}
