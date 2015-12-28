package guimodule;

import processing.core.PApplet;
import processing.core.PImage;

public class MyDisplay extends PApplet {
	PImage img;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void setup() {
		// Add setup code for PApplet
		
		size(800, 600);			// Set canvas size
		background(255, 255);	// Set canvas color
		//stroke(255, 255, 0);				// Set pen color
		
		// Load img into memory
		img = loadImage("http://www.nasa.gov/images/content/323075main_s119e009765_hires.jpg", "jpg");
		
		// Resize loaded image to full height of canvas while maintaining aspect ratio
		img.resize(0, height);	
		
		// Display image
		image(img, 0, 0);
	
	}
	
	
	public void draw() {
		
		// Calculate Sun color code
		int[] colorArray = getSunColor(second());
		
		// Set sun color
		fill(colorArray[0], colorArray[1], colorArray[2]);
	
		// Draw sun as ellipse depending on window or canvas width and height;
		// Recall: x, y, width, height
		ellipse(width/2, height/5, width/4, height/5);
	}
	
	
	public int[] getSunColor(float seconds) {
		
		int[] rgbArray = new int[3];
		
		// Scale the brightness of the yellow based on the seconds remaining till 30.
		// 0 seconds is black (meaning we've reached end of 30 seconds. 30 seconds is bright yellow
		// means 30 seconds remain and we'ev just started.
		float diffFrom30 = Math.abs(30-seconds);
		
		// ratio close to 1 (30-0sec/30) means close to 30 secs remain so bright yellow
		// ratio close to 0 (30-30sec/30) means almost no secs remain to 30sec period so black
		float ratio = diffFrom30/30;
		
		rgbArray[0] = (int)(255*ratio);
		rgbArray[1] = (int)(255*ratio);
		rgbArray[2] = 0;
		
		return rgbArray;	
	}
}
