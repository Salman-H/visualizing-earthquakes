package module4;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Salman Hashmi
 * Date: January 9, 2016
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(2450, 1400, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 2400, 1300, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		// earthquakesURL = "test2.atom";
		
		// WHEN TAKING THIS QUIZ: Uncomment the next line
		earthquakesURL = "quiz1.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    // Note: if the applet is launched with a large earthquake file/feed 
	    // (e.g. 1.0+ Past week or 30 days), printQuakes may take a long time to run
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
	
	}
	
	// helper method to draw key in GUI
	// TODO: Update this method as appropriate
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		rect(65, 50, 500, 1300);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(28);
		
		text("Earthquake Key", 150, 105);
		
		// City Marker Triangle
		fill(color(0, 255, 0));
		triangle(150, 145, 135, 175, 165, 175);	// center: 150, 150, TRI_SIZE: 15
		
		// Land Quake Circle
		fill(color(255, 255, 255));
		stroke(color(0, 0, 0));
		ellipse(150, 200, 30, 30);
		
		// Ocean Quake Square
		fill(color(255, 255, 255));
		stroke(color(0, 0, 0));
		strokeWeight(3);
		rect(135, 230, 30, 30);
		
		// Shallow 
		fill(color(255, 255, 0));
		stroke(color(0, 0, 0));
		strokeWeight(3);
		ellipse(150, 362, 30, 30);
		
		// Intermediate
		fill(color(0, 0, 255));
		stroke(color(0, 0, 0));
		strokeWeight(3);
		ellipse(150, 407, 30, 30);
		
		// Deep
		fill(color(255, 0, 0));
		stroke(color(0, 0, 0));
		strokeWeight(3);
		ellipse(150, 452, 30, 30);
		
		// Past Day
		fill(color(255, 255, 255));
		stroke(color(0, 0, 0));
		strokeWeight(3);
		ellipse(150, 497, 30, 30);
		line(135, 482, 165, 512);
		line(135, 512, 165, 482);
		// fill(0);
		// textSize(32);
		// text("X", 150, 497);
		

		fill(0, 0, 0);
		text("City Marker", 195, 160);
		text("Land Quake", 195, 200);
		text("Ocean Quake", 195, 240);
		
		textAlign(LEFT, CENTER);
		text("Size ~ Magnitude", 140, 299);
		text("Shallow (0-70km)", 195, 360);
		text("Intermediate (70-300km)", 195, 405);
		text("Deep (300-700km)", 195, 450);
		text("Past Day", 195, 495);
		// text("5.0+ Magnitude", 75, 125);
		// text("4.0+ Magnitude", 75, 175);
		// text("Below 4.0", 75, 225);
		
		
		
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		
		// TODO: Implement this method using the helper method isInCountry
		
		// We need to check if the input PointFeature earthquake location is 
		// in *some* country to establish that it is actually on land
		
		// for each countryMarker in List countryMarkers,
		for (Marker countryMarker: countryMarkers) {
			
			// check if the input PointFeature earthquake is in that countryMarker
			if (isInCountry(earthquake, countryMarker)) {
				// if given earthquake PointFeature is in *some* country, it means it's on land, so
				return true;
				// Note that isInCountry() method also sets the "country" property on the 
				// countryMarker to the country where it finds the input earthquake to have occurred
				// This essentially updates the "country" property of all countryMarkers in List 
				// countryMarkers for the input earthquake location
			}
		}
		// We have looped through all countryMarkers and didn't find the 
		// input PointFeature earthquake location in any one of them, this means that
		// this earthquake location is not on land and, therefore, must be in the ocean
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() 
	{
		// TODO: Implement this method
		int oceanQuakeCount = 0;
		
		for (Marker country: countryMarkers) {
			
			int countryQuakeCount = 0;
			String countryName = country.getStringProperty("name");
			
			for(Marker quake: quakeMarkers) {
				// check all quakeMarkers against the current countryMarker to see 
				// which (if any) occurs in that country
				
				if (countryName.equals(quake.getStringProperty("country"))) {
					countryQuakeCount++;
				}
			}
			// print result for current country if it had any earthquakes
			if (countryQuakeCount > 0) {
				System.out.println(countryName + ": " + countryQuakeCount);
			}						
		}
		// print earthquakes occurring in the ocean	
		for (Marker quake: quakeMarkers) {
			// check for ocean quakes
			// if the country property of the current earthquake is not set,
			// that implies that this quake does not occur on land i.e. occurs in ocean
			if (quake.getProperty("country") == null) {
				oceanQuakeCount++;
			}
		}
		System.out.println("OCEAN QUAKES: " + oceanQuakeCount);
	}
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake 
	// feature if it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
