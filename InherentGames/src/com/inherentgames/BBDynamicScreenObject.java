package com.inherentgames;

import javax.vecmath.Point2f;

import android.graphics.Point;

import com.inherentgames.BBWordObject.Gender;
import com.threed.jpct.RGBColor;

/**
 * @author Tyler
 * Class that displays on screen images or text, and moves them from their initial location to a given end point.
 * If the object is an image, it can shrink or enlarge the image from an initial width/height, to a new width/height.
 */
public class BBDynamicScreenObject {
	// Track location
	protected Point2f location;
	private Point2f start;
	protected Point2f end;
	protected boolean isStatic;
	protected boolean hasFinished;
	
	// Needed for images
	protected boolean isImage;
	protected float endWidth, endHeight;
	protected float initialWidth, initialHeight;
	protected float width, height;
	private float iterations;
	
	// Needed for text
	protected RGBColor color;
	
	// Text for text objects, image name for images
	protected String data = "";
	
	// Track time of motion
	protected long initiated;
	protected long displayTime;
	
	// Constructor for text
	/**
	 * Constructor for text object
	 * 
	 * @param startX - the x position of where the object should start when first displayed (in pixels)
	 * @param startY - the y position of where the object should start when first displayed (in pixels)
	 * @param endX - the x position of where the object should end after moved (in pixels)
	 * @param endY - the y position of where the object should end after moved (in pixels)
	 * @param text - the string that should be displayed
	 * @param displayTime - how long the text will be displayed until is moved to its end point
	 * @param article - the Gender of the word, determines the color of the text
	 */
	public BBDynamicScreenObject( int startX, int startY, int endX, int endY, String text,
			long displayTime, Gender article ) {
		start = new Point2f((float) startX, (float) startY);
		end = new Point2f((float) endX, (float) endY);
		location = start;
		data = text;
		this.displayTime = displayTime;
		
		isImage = false;
		isStatic = true;
		initiated = System.currentTimeMillis();
		hasFinished = false;
		
		if (article == Gender.FEMININE) {
			color = new RGBColor( 226, 51, 34 );
		} else {
			color = new RGBColor( 132, 211, 245 );
		}
	}
	
	// Constructor for image
	/**
	 * Constructor for image object
	 * 
	 * @param startX - the x position of where the object should start when first displayed (in pixels)
	 * @param startY - the y position of where the object should start when first displayed (in pixels)
	 * @param endX - the x position of where the object should end after moved (in pixels)
	 * @param endY - the y position of where the object should end after moved (in pixels)
	 * @param imageName - the string name of the image to be displayed
	 * @param displayTime - how long the text will be displayed until is moved to its end point
	 * @param initialWidth - the starting width of the image before moved (in pixels)
	 * @param initialHeight - the starting height of the image before moved (in pixels)
	 * @param endWidth - the ending width of the image before moved (in pixels)
	 * @param endHeight - the ending height of the image before moved (in pixels)
	 */
	public BBDynamicScreenObject( int startX, int startY, int endX, int endY, String imageName,
			long displayTime, int initialWidth, int initialHeight, int endWidth, int endHeight ) {
		start = new Point2f((float) startX, (float) startY);
		end = new Point2f((float) endX, (float) endY);
		location = start;
		data = imageName;
		this.displayTime = displayTime;
		this.initialWidth = initialWidth;
		this.initialHeight = initialHeight;
		this.endWidth = endWidth;
		this.endHeight = endHeight;
		width = initialWidth;
		height = initialHeight;
		
		isImage = true;
		isStatic = true;
		initiated = System.currentTimeMillis();
		iterations = (int) Math.sqrt( ((end.x - start.x) * (end.x - start.x)) + ((end.x - start.x) * (end.y - start.y)));
		hasFinished = false;
	}
	
	/**
	 * Moves the object towards its end point if displayTime has passed and it hasn't yet reached the end point
	 * 
	 * @return - the current location of the object
	 */
	public Point2f move() {
		if ( !hasFinished ) {
			if ( !isStatic ) {
				if ( !location.equals(end) ) {
					
					Point2f difference = new Point2f(end.x - location.x, end.y - location.y);
					
					if (difference.x < 5 && difference.y < 5) {
						if ( difference.x > difference.y ) {
							location.set( difference.x / Math.abs(difference.y) + location.x,
									difference.y / Math.abs(difference.y)  + location.y);
						} else {
							location.set( 3f * difference.x / Math.abs(difference.x)  + location.x,
									difference.y / Math.abs(difference.x)  + location.y);
						}
					} else {
						if ( difference.x > difference.y ) {
							location.set( 3f * difference.x / Math.abs(difference.y) + location.x,
									3f * difference.y / Math.abs(difference.y)  + location.y);
						} else {
							location.set( 3f * difference.x / Math.abs(difference.x)  + location.x,
									3f * difference.y / Math.abs(difference.x)  + location.y);
						}
					}
				} else {
					isStatic = true;
					hasFinished = true;
				}
				if ( isImage ) {
					if ( width != endWidth ) {
						width += ((endWidth - width) / iterations) * 15f;
					}
					if ( height != endHeight ) {
						height += ((endHeight - height) / iterations) * 15f;
					}
				}
			} else {
				if ( System.currentTimeMillis() > (initiated + displayTime) ) {
					isStatic = false;
				}
			}
		}
		return location;
	}
}
