package module5;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Salman Hashmi
 *
 */
// TODO: Change SimplePointMarker to CommonMarker as the very first thing you do 
// in module 5 (i.e. CityMarker extends CommonMarker).  It will cause an error.
// That's what's expected.
public class CityMarker extends CommonMarker {
	
	public static int TRI_SIZE = 10;  // The size of the triangle marker
	
	public float averageQuakeMagnitude;	/***************************************************************/
	
	
	public CityMarker(Location location) {
		super(location);
	}
	
	
	public CityMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}
	
	// getter for averageQuakeMagnitude field
	public float getAverageQuakeMag() {
		return averageQuakeMagnitude;
	}

	// setter for averageQuakeMagnitude field
	public void setAverageQuakeMag(float mag) {
		averageQuakeMagnitude = mag;
	}
	
	/**
	 * Implementation of method to draw marker on the map.
	 */
	public void drawMarker(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();
		
		// IMPLEMENT: drawing triangle for each city
		pg.fill(150, 30, 30);
		pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);
		
		// Restore previous drawing style
		pg.popStyle();
	}
	
	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{
		
		// TODO: Implement this method
		String cityProperties = getCity() + ", " + getCountry() + "\n" + "Population: " + getPopulation() + " million";
		
		pg.pushStyle();
		
		pg.fill(255, 255, 255);
		pg.rect(x, y-TRI_SIZE-60, pg.textWidth(cityProperties)+ 20, 60);
		
		pg.fill(0);
		pg.textSize(18);
		pg.text(cityProperties, x+5, y-TRI_SIZE-35);
		
		pg.popStyle();
	}
	
	/**
	 * Show quake-related information for a city if that city marker is clicked
	 */
	public void showInfo(PGraphics pg, float x, float y, int displayedMarkersCount) {
		
		String cityProperties = getCity() + ", " + getCountry() + 
				"\n" + "Population : " + getPopulation() + " million" +
				"\n" + "Earthquakes Affecting this city : " + displayedMarkersCount +
				"\n" + "Average Magnitude of Affecting Earthquakes : " + averageQuakeMagnitude;
		
		pg.pushStyle();
		
		pg.fill(255, 255, 255);
		pg.rect(x, y-TRI_SIZE-120, pg.textWidth(cityProperties)+ 20, 120);
		
		pg.fill(0);
		pg.textSize(18);
		pg.text(cityProperties, x+5, y-TRI_SIZE-95);
		
		pg.popStyle();
	
	}
	
	
	
	/* Local getters for some city properties.  
	 */
	public String getCity()
	{
		return getStringProperty("name");
	}
	
	public String getCountry()
	{
		return getStringProperty("country");
	}
	
	public float getPopulation()
	{
		return Float.parseFloat(getStringProperty("population"));
	}
}
