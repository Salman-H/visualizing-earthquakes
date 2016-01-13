package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/** Implements a visual marker for earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Salman Hashmi
 *
 */
public abstract class EarthquakeMarker extends SimplePointMarker
{
	
	// Did the earthquake occur on land?  This will be set by the subclasses.
	protected boolean isOnLand;

	// The radius of the Earthquake marker
	// You will want to set this in the constructor, either
	// using the thresholds below, or a continuous function
	// based on magnitude. 
	protected float radius;
	
	
	/** Greater than or equal to this threshold is a moderate earthquake */
	public static final float THRESHOLD_MODERATE = 5;
	/** Greater than or equal to this threshold is a light earthquake */
	public static final float THRESHOLD_LIGHT = 4;

	/** Greater than or equal to this threshold is an intermediate depth */
	public static final float THRESHOLD_INTERMEDIATE = 70;
	/** Greater than or equal to this threshold is a deep depth */
	public static final float THRESHOLD_DEEP = 300;

	// ADD constants for colors

	
	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
		
	
	// constructor
	public EarthquakeMarker (PointFeature feature) 
	{
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2*magnitude );
		setProperties(properties);
		this.radius = 1.75f*getMagnitude();
	}
	

	// calls abstract method drawEarthquake and then checks age and draws X if needed
	public void draw(PGraphics pg, float x, float y) {
		// save previous styling
		pg.pushStyle();
			
		// determine color of marker from depth
		colorDetermine(pg);
		
		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);
		
		// OPTIONAL TODO: draw X over marker if within past day		
		crossMarker(pg, x, y);
		
		
		// reset to previous styling
		pg.popStyle();
		
	}
	
	// determine color of marker from depth
	// We suggest: Deep = red, intermediate = blue, shallow = yellow
	// But this is up to you, of course.
	// You might find the getters below helpful.
	private void colorDetermine(PGraphics pg) {
		//TODO: Implement this method
		float depth = getDepth();
		
		// Shallow earthquakes are between 0 and 70 km deep;
		if (depth >= 0.0 && depth <= THRESHOLD_INTERMEDIATE) {
			// indicate with yellow
			pg.fill(255, 255, 0);
		}
		// Intermediate earthquakes, 70 - 300 km deep;
		else if (depth > THRESHOLD_INTERMEDIATE && depth <= THRESHOLD_DEEP) {
			// indicate with blue
			pg.fill(0, 0, 255);
		}
		// Deep earthquakes, 300 - 700 km deep
		else if (depth > THRESHOLD_DEEP) {
			// indicate with red
			pg.fill(255, 0, 0);
		}
		
	}
	
	// Cross a Marker if it occurs in the Past Hour or in the Past Day
	private void crossMarker(PGraphics pg, float x, float y) {
		String age = getAge();
		
		// "Past Hour" and "Past Day" are possible values of the "age" property
		if ("Past Hour".equals(age) || "Past Day".equals(age)) {
			// set stroke
			pg.stroke(0, 0, 0);
			pg.strokeWeight(3);
			// cross marker such that 
			float crossSize = radius + 2;
			pg.line(x-crossSize, y-crossSize, x+crossSize, y+crossSize);
			pg.line(x-crossSize, y+crossSize, x+crossSize, y-crossSize);
		}
	}
	
	
	/*
	 * getters for earthquake properties
	 */
	
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}
	
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}
	
	public String getAge() {
		return (String) getProperty("age");
	}
	
	public String getTitle() {
		return (String) getProperty("title");	
		
	}
	
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}
	
	public boolean isOnLand()
	{
		return isOnLand;
	}
	
	
}
