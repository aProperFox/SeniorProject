package com.inherentgames;

import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

public class BBButton {
	
	// Position on screen (in pixels)
	protected int posX, posY;
	
	// Displayed width and height (in pixels)
	protected int width, height;
	
	// Image strings for image states
	private String passiveImage, activeImage;
	
	// Image width and height (in pixels)
	protected int imageWidth, imageHeight;
	
	// Current image string
	protected String currentImage;
	
	public BBButton( int x, int y, int width, int height, String passiveImage, String activeImage ){
		
		// Set location
		posX = x;
		posY = y;
		
		// Set dimensions based on screen dimensions
		this.width = width;
		this.height = height;
		
		// Set image strings
		this.passiveImage = passiveImage;
		this.activeImage = activeImage;
		currentImage = this.passiveImage;
		
		// Set loaded image width and height (they must have the same dimensions)
		Texture tempTex = TextureManager.getInstance().getTexture(passiveImage);
		imageWidth = tempTex.getWidth();
		imageHeight = tempTex.getHeight();
		
	}
	
	public void setPosition( int x, int y ) {
		
		// Set position coordinates
		posX = x;
		posY = y;
		
	}
	
	/**
	 * @return boolean equivalent to isActive
	 */
	public boolean swapState() {
		
		// Swap the current image from passive to active or vice versa
		if ( currentImage == passiveImage ) {
			currentImage = activeImage;
			return true;
		} else {
			currentImage = passiveImage;
			return false;
		}
		
	}
	
	/**
	 * @return
	 */
	public boolean isActive() {
		
		// Return true if activeImage is being used, otherwise false
		if ( currentImage == activeImage ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	public boolean includes( int x, int y ) {
		
		// Check to see if point is located within button bounds
		if ( x > (posX - width) && x < (posX + width) && y > (posY - height) && y < (posY + height) ) {
			return true;
		} else {
			return false;
		}
	}
}
