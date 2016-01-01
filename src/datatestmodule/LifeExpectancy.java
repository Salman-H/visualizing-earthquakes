package datatestmodule;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.data.Feature;


import de.fhpotsdam.unfolding.data.GeoJSONReader;
import processing.core.PApplet;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 
 * @author Salman
 *
 */
public class LifeExpectancy extends PApplet {
	/**
	 * Visualizing life expectancy data on the world map
	 */
	private static final long serialVersionUID = 1L;
	
	UnfoldingMap map;
	HashMap<String, Float> lifeExpByCountryHM;
	
	/**
	 * (non-Javadoc)
	 * @see processing.core.PApplet#setup()
	 * 
	 * unfoldingmaps.org/javadoc
	 * 
	 * Class FEATURE: 
	 * A feature stores one or more locations, its type, and additional data properties
	 * 
	 * Interface MARKER: 
	 * Marker interface for all markers to be drawn on to maps. 
	 * A marker has at least one location, and properties. 
	 * A marker can be drawn, selected, and tested if hit. 
	
	 */
	List<Feature> countryFeaturesList;
	List<Marker> countryMarkersList;

	
	// Executes once
	public void setup() {
		
		size(2500, 2100, OPENGL);		// specify OpenGL renderer with size
		map = new UnfoldingMap(this, 50, 50, 2400, 2000, new Google.GoogleMapProvider());
		
		// for default interactivity
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// Create Object of type Map; method returns a HashMap Object
		lifeExpByCountryHM = loadLifeExpectancyFromCSV("LifeExpectancyWorldBankModule3.csv");
		
		// Test HashMap
		System.out.println(lifeExpByCountryHM.get("CAN"));
		
		// 1 Feature + 1 Marker per Country
		/*
		 * Class GeoJSONReader in unfolding.data extends GeoDataReader -> Reads
		 * GeoJSON files and creates features
		 * 
		 * Feature Helper Method: loadData(PApplet, String fileName) -> Parses
		 * a GeoJSON String and creates features for them -> returns <Feature>
		 */
		countryFeaturesList = GeoJSONReader.loadData(this, "countries.geo.json");
		
		/*
		 * Class MapUtils
		 * -> Provides utility and convenience methods for simplifying map usage
		 * 
		 * MapUtils Helper Method: createSimpleMarkers(<Feature>)
		 * -> Creates simple markers from features
		 * -> Returns a list of markers
		 * 
		 */
		countryMarkersList = MapUtils.createSimpleMarkers(countryFeaturesList);
		
		/*
		 * UnfoldingMap Method: addMarkers(List<Markers>)
		 * -> Adds multiple markers to the map from List<Markers>
		 * -> Returns nothing
		 */
		map.addMarkers(countryMarkersList);
		
		/*
		 * After adding Features and Markers, we need to color-code countries
		 * depending on life expectancy 
		 */
		colorCodeCountries();
	
	}
	
	
	
	// Executes often
	public void draw() {
		map.draw();
	}
	
	
	// Helper method: Takes a CSV file and returns a HashMap of key-value pairs 
	private HashMap<String, Float> loadLifeExpectancyFromCSV(String fileName) {
		
		HashMap<String, Float> lifeExpHashMap = new HashMap<String, Float>();
		
		/*
		 * 	processing.core
		 *  Reads the contents of a file and creates a String array of its individual lines. 
		 *  Note: If the name of the file is used as the parameter, the file must be loaded in the 
		 *  sketch's "data" directory/folder. 
		 */
		// Store CSV file in an Array of row Strings
		// Remember, Column 4 is Country ID and Column 5 is Average Life Expectancy (both as Strings)
		String[] rowsArray = loadStrings(fileName);
		
		
		// for each String row, 
		for (String row: rowsArray) {
			
			// split that String sentence (by "," since reading CSV) 
			// into an array of individual Strings
			String[] columns = row.split(",");
			
			// then if columns array has 5 elements, and column 5 Strings are non-empty, 
			// represented as ".." in the file
			if (columns.length == 6 && !columns[5].equalsIgnoreCase("..")) {
				
				// cast the life expectancy String into a Float
				// and Store column 4 as key and column 5 as value into the lifeExpMap HashMap
				lifeExpHashMap.put(columns[4], Float.parseFloat(columns[5]));
				
				System.out.println("columns[0] " + columns[0] + " columns[1] " + columns[1] + " " + "columns[2] " + columns[2] + "columns[3] " + columns[3]);
			}
			
		}
		
		return lifeExpHashMap;
	}
	
	/**
	 * color code countries depending on average life expectancy of that country
	 * 
	 */
	private void colorCodeCountries() {
		// Need to map average life expectancy range to RGB Red range 10 to 255
		
		// for each marker in CountriesMarkerList, get the country ID
		for(Marker marker: countryMarkersList) {
			 String countryID = marker.getId();
			 
			// if lifeExpectancyHashMap of (country ID, avg life expec) contains that country ID 
			 if (lifeExpByCountryHM.containsKey(countryID)) {
				// get the value i.e. float avg life expec of that country ID key
				float lifeExpValue = lifeExpByCountryHM.get(countryID);
				
				// and map to a corresponding value in range 10 to 255
				// then store that mapped value as int. 
				int colorCode = (int)map(lifeExpValue, 40, 90, 10, 255);
				
				// The color that is varied depending on the life exp value is 
				// actually the B value in RGB. The more the life exp, the higher the
				// color code, and the higher the Blue value below, and the lower the Red value.
				// Thus, Countries with higher life expectancies will have a higher Blue value and lower Red value
				// whereas countries with lower life expectancies with have a lower Blue value and higher REd value
				marker.setColor(color(255-colorCode, 100, colorCode));
			 }
			 else {
				 // if the country is not in our HashMap,
				 // give that country a neutral color
				 marker.setColor(color(150, 150, 150));
			 }
			 
		}
		
	}
	

}
