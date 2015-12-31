package datatestmodule;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
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
	
	
	// Executes once
	public void setup() {
		
		size(800, 600, OPENGL);		// specify OpenGL renderer with size
		map = new UnfoldingMap(this, 50, 50, 700, 500, new Google.GoogleMapProvider());
		
		// for default interactivity
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// Create Object of type Map; method returns a HashMap Object
		lifeExpByCountryHM = loadLifeExpectancyFromCSV("LifeExpectancyWorldBankModule3.csv");
		
		// Test HashMap
		System.out.println(lifeExpByCountryHM.get("CAN"));
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

}
