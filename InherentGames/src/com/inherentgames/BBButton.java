package com.inherentgames;

import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

/**
 * @author Tyler
 * A class that keeps track of on screen 'Buttons'; some declarations are unclickable. Makes re-sizing and moving buttons 
 * on screen (such as fireButton, pauseButton, etc.) very simple. It is also being used for the score bar, which isn't
 * necessarily a button, so the naming convention is a bit skewed. Future implementation could extend Button and add an 
 * OnClickListener() so as to easily handle clicking the button rather than using the includes() function.
 */
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
	
	// May be useful for future implementation, if BBButton extends Button and has OnClickListener()
	protected boolean canBePressed = true;
	
	/**
	 * Constructor for BBButton
	 * 
	 * @param x - the x position of the center of the button in pixels
	 * @param y - the y position of the center of the button in pixels
	 * @param width - the width of the button in pixels
	 * @param height - the height of the button in pixels
	 * @param passiveImage - a string for the image displayed when the button isn't being pressed
	 * @param activeImage - a string for the image displayed when the button is being pressed
	 */
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
		Texture tempTex = TextureManager.getInstance().getTexture(activeImage);
		imageWidth = tempTex.getWidth();
		imageHeight = tempTex.getHeight();
		
	}
	
	/**
	 * Manually set the position of the button if it needs to be moved (perhaps for left handed mode?)
	 * 
	 * @param x - the x position of the center of the button in pixels
	 * @param y - the y position of the center of the button in pixels
	 */
	public void setPosition( int x, int y ) {
		
		// Set position coordinates
		posX = x;
		posY = y;
		
	}
	
	/**
	 * changes the state of the button (passive to active or vice versa) and sets the image to display
	 * accordingly.
	 * 
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
	 * Find out if the button is currently being pressed (active) or not
	 * 
	 * @return - true if button is active, false if passive
	 */
	public boolean isActive() {
		
		// Return true if activeImage is being used, otherwise false
		if ( currentImage == activeImage ) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Used to determine if a certain point on screen is within the button. Useful for determining whether
	 * the button has been clicked, but could be deprecated if the BBButton class extends Button, and an
	 * OnClickListener() is added.
	 * 
	 * @param x - the x position of the center of the button in pixels
	 * @param y - the y position of the center of the button in pixels
	 * @return - true if the point given is within the button's reaches, false if not
	 */
	public boolean includes( int x, int y ) {
		
		// Check to see if point is located within button bounds
		if ( x > (posX - width/2) && x < (posX + width/2) && y > (posY - height/2) && y < (posY + height/2) ) {
			return true;
		} else {
			return false;
		}
	}
}
