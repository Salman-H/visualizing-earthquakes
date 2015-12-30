package datatestmodule;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;


public class LifeExpectancy extends PApplet {
	/**
	 * Visualizing life expectancy data on the world map
	 */
	private static final long serialVersionUID = 1L;
	UnfoldingMap map;
	
	// Executes once
	public void setup() {
		
		size(800, 600, OPENGL);		// specify OpenGL renderer with size
		map = new UnfoldingMap(this, 50, 50, 700, 500, new Google.GoogleMapProvider());
		
		// for default interactivity
		MapUtils.createDefaultEventDispatcher(this, map);
		
	}
	
	// Executes often
	public void draw() {
		map.draw();
	}

}
