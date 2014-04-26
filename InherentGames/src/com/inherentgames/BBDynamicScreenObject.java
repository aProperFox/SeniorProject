package com.inherentgames;

import android.graphics.Point;

import com.threed.jpct.RGBColor;
import com.inherentgames.BBWordObject.Gender;

public class BBDynamicScreenObject {
	// Track location
	protected Point location;
	private Point start;
	protected Point end;
	protected boolean isStatic;
	protected boolean hasFinished;
	
	// Needed for images
	protected boolean isImage;
	protected int endWidth, endHeight;
	protected int initialWidth, initialHeight;
	protected int width, height;
	private int iterations;
	
	// Needed for text
	protected RGBColor color;
	
	// Text for text objects, image name for images
	protected String data = "";
	
	// Track time of motion
	protected long initiated;
	protected long displayTime;
	
	// Constructor for text
	public BBDynamicScreenObject( int startX, int startY, int endX, int endY, String text,
			long displayTime, Gender article ) {
		start = new Point(startX, startY);
		end = new Point(endX, endY);
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
	public BBDynamicScreenObject( int startX, int startY, int endX, int endY, String imageName,
			long displayTime, int initialWidth, int initialHeight, int endWidth, int endHeight ) {
		start = new Point(startX, startY);
		end = new Point(endX, endY);
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
	
	public Point move() {
		if ( !hasFinished ) {
			if ( !isStatic ) {
				if ( !location.equals(end) ) {
					Point difference = new Point(end.x - location.x, end.y - location.y);
					int dirx, diry;
					if ( difference.x < 0 ) {
						dirx = -1;
					} else {
						dirx = 1;
					}
					if ( difference.y < 0 ) {
						diry = -1;
					} else {
						diry = 1;
					}
					if ( difference.x > difference.y ) {
						location.offset( difference.x / 30 + dirx, difference.y / 30 + diry );
					} else {
						location.offset( difference.x / 30 + dirx , difference.y / 30 + diry );
					}
				} else {
					isStatic = true;
					hasFinished = true;
				}
				if ( isImage ) {
					if ( width != endWidth ) {
						width += (int) ((((float) endWidth - (float) width) / (float) iterations) * 10f);
					}
					if ( height != endHeight ) {
						height += (int) ((((float) endHeight - (float) height) / (float) iterations) * 10f);
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
