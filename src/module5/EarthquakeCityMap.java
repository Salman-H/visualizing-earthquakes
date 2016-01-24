package module5;

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
 * Date: January 15, 2016
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setup and draw methods will need to access (as well as other methods)
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
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
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
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
	}
	
	// If there is a marker under the cursor, and lastSelected is null 
	// set the lastSelected to be the first marker found under the cursor
	// Make sure you do not select two markers.
	// 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// TODO: Implement this method
		
		// If a marker is already selected, return
		if (lastSelected != null) {
			return;
		}
		
		// Use isInside() method of AbstractMarker Class to check if the
		// mouse cursor is inside the current marker being iterated over
		for (Marker marker: markers) {
			
			// Marker's method, isInside(map, checkX, checkY), checks whether given mouse position 
			// is inside this marker. mouseX and mouseY are PApplet instance variables
			// storing the values of the current mouse position x and y
			
			if (marker.isInside(map, mouseX, mouseY)) {
				
				// Set the lastSelected marker to be the first marker found under the cursor
				CommonMarker commonMarker = (CommonMarker)marker;
				lastSelected = commonMarker;	// lastSelected.setLocation(marker.getLocation());
				lastSelected.setSelected(true);	// commonMarker.setSelected(true);
				// ** TEST showTitle()
				//showTitle(PGraphics pg, mouseX, mouseY);
				// No other markers should then be selected
				return;	// break
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		// TODO: Implement this method
		// Hint: You probably want a helper method or two to keep this code
		// from getting too long/disorganized
		
		// if mouse is clicked with cursor outside any markers while a current marker(lastClicked)
		// is already selected, i.e. lastClicked still active, then display(unhide) ALL markers
		// and also de-select the lastClicked marker
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		// else if mouse is clicked while no markers are active, then it has to be 
		// determined whether the cursor was inside any markers during the click
		else if (lastClicked == null) {
			// specifically, we have to check, one-by-one, whether the cursor was inside
			// a city marker or a quake marker and then respond accordingly 
			checkIfClickedOnQuakeMarker();
			// while checking for the earthquake clicks, a marker could have been clicked
			// so make sure no markers are active before proceeding to check for city clicks
			if (lastClicked == null) {
				checkIfClickedOnCityMarker();
			}
		}
	}
	
	/**
	 * Is called when mouse is clicked while all markers are unhidden i.e.
	 * when there is no single selected marker on display. In that case, this
	 * method determines whether the mouse was clicked inside of an earthquake marker 
	 * or not. If so, only that earthquake marker is displayed along with any cities 
	 * within the threat circle of that earthquake, and all other city and quake markers
	 * are hidden. Otherwise, if the mouse was not clicked inside any quake markers, then
	 * nothing happens.
	 */
	private void checkIfClickedOnQuakeMarker() {
		// no need to check for quake clicks if another marker was lastClicked and active
		if (lastClicked != null) return;
		
		// otherwise proceed with finding out which, if any, quake marker is clicked
		
		// loop over all quake markers to see if cursor is inside any one of them
		for (Marker eq: quakeMarkers) {
			EarthquakeMarker quake = (EarthquakeMarker)eq;
			
			// if cursor is inside a displayed quake marker,
			if (!quake.isHidden() && quake.isInside(map, mouseX, mouseY)) {
				// Set the lastClicked marker to be this current marker
				lastClicked = quake;
				
				// then loop over all city markers and..
				for (Marker city: cityMarkers) {
					// for each city marker,
					// if quake-city distance > quake-threat distance
					if (quake.getDistanceTo(city.getLocation()) > quake.threatCircle()) {
						// hide that city
						city.setHidden(true);
					}
				}
				// finally, after hiding all quake markers that weren't clicked,..
				for (Marker otherMarker: quakeMarkers) {
					if (otherMarker != lastClicked) {
						otherMarker.setHidden(true);
					}
				}
				//..end method execution
				return;
			}	
		}
	}
	
	/**
	 * 
	 */
	private void checkIfClickedOnCityMarker() {
		// no need to check for city clicks if another marker was lastClicked and active
		if (lastClicked != null) return;
		
		for (Marker c: cityMarkers) {
			CommonMarker city = (CommonMarker)c;
			
			if (!city.isHidden() && city.isInside(map, mouseX, mouseY)) {
				lastClicked = city;
				
				for (Marker eq: quakeMarkers) {
					EarthquakeMarker quake = (EarthquakeMarker)eq;
					if (quake.threatCircle() < quake.getDistanceTo(city.getLocation())) {
						// quake not affecting city
						quake.setHidden(true);
					}
				}
				// hide all other cities and..
				for (Marker otherCity: cityMarkers) {
					if (otherCity != lastClicked) {
						otherCity.setHidden(true);
					}
				}
				//..end execution
				return;
			}
		}
	}
	
	
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 500, 1300);
		
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(27);
		text("Earthquake Key", xbase+125, ybase+25);
		
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 104;
		int tri_ybase = ybase + 80;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("City Marker", tri_xbase+60, tri_ybase);
		
		fill(0, 0, 0);
		text("Land Quake", xbase+160, ybase+120);
		fill(255, 255, 255);
		ellipse(xbase+105, ybase+120, 20, 20);
		
		fill(0, 0, 0);
		text("Ocean Quake", xbase+160, ybase+160);
		fill(255, 255, 255);
		rect(xbase+105-10, ybase+160-10, 20, 20);
		
		
		fill(0, 0, 0);
		text("Size ~ Magnitude", xbase+100, ybase+220);
		
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+150, ybase+280);
		fill(color(255, 255, 0));
		ellipse(xbase+105, ybase+280, 22, 22);
		
		fill(0, 0, 0);
		text("Intermediate", xbase+150, ybase+320);
		fill(color(0, 0, 255));
		ellipse(xbase+105, ybase+320, 22, 22);
		
		fill(0, 0, 0);
		text("Deep", xbase+150, ybase+360);
		fill(color(255, 0, 0));
		ellipse(xbase+105, ybase+360, 22, 22);

		fill(0, 0, 0);
		text("Past hour", xbase+150, ybase+400);
		fill(255, 255, 255);
		int centerx = xbase+105;
		int centery = ybase+400;
		ellipse(centerx, centery, 22, 22);

		strokeWeight(2);
		line(centerx-12, centery-12, centerx+12, centery+12);
		line(centerx-12, centery+12, centerx+12, centery-12);
			
	}

	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.	
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
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
