package module5;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import processing.core.PGraphics;

/** Implements a common marker for cities and earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Salman Hashmi
 *
 */
public abstract class CommonMarker extends SimplePointMarker {

	// Records whether this marker has been clicked (most recently)
	protected boolean clicked = false;
	
	// number of markers who either threat this marker or are threatened by it
	// These are the markers that are displayed when this marker is clicked
	protected int numberOfThreatMarkers;
	
	public CommonMarker(Location location) {
		super(location);
	}
	
	public CommonMarker(Location location, java.util.HashMap<java.lang.String,java.lang.Object> properties) {
		super(location, properties);
	}
	
	// Getter method for clicked field
	public boolean getClicked() {
		return clicked;
	}
	
	// Setter method for clicked field
	public void setClicked(boolean state) {
		clicked = state;
	}
	
	// Getter method for numberOfThreatMarkers field
	public int getThreatMarkers() {
		return numberOfThreatMarkers;
	}
	
	// Setter method for numberOfThreatMarkers field
	public void setThreatMarkers(int number) {
		numberOfThreatMarkers = number;
	}
	
	// Common piece of drawing method for markers; 
	// Note that you should implement this by making calls 
	// drawMarker and showTitle, which are abstract methods 
	// implemented in subclasses
	public void draw(PGraphics pg, float x, float y) {
		// For starter code just drawMaker(...)
		if (!hidden) {
			drawMarker(pg, x, y);
			
			if (selected) {
				showTitle(pg, x, y);  // You will implement this in the subclasses
			}
			// TEST show City Info on Click
			if (clicked) {
				showInfo(pg, x, y, numberOfThreatMarkers);
			}
		}
	}
	public abstract void drawMarker(PGraphics pg, float x, float y);
	public abstract void showTitle(PGraphics pg, float x, float y);
	public abstract void showInfo(PGraphics pg, float x, float y, int displayedMarkersCount);
}