package com.inherentgames;

import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

public class BBBubble extends BBWordObject {

	// Define valid bubble colors
	public static enum Color {
		RED  ( 226, 51, 34 ),
		BLUE ( 132, 211, 245 );
		
		public final int r, g, b;
		
		Color( int r, int g, int b ) {
			this.r = r;
			this.g = g;
			this.b = b;
		}
	};
	
	private static final long serialVersionUID = -4649047534584957279L;
	
	// Tracks whether the bubble is holding an object
	private boolean isHolding;
	// Tracks the object this bubble is holding
	private int heldObjectId;
	// Store various IDs of this object
	private int objectId= -1;
	private int bodyIndex = -1;
	private int localBodyIndex = -1;
	// Track when this bubble was created
	private long timeCreated;
	
	/**
	 * @param translation
	 * @param article
	 * @param timeInMillis
	 */
	public BBBubble( SimpleVector translation, Gender article, long timeInMillis ) {
		super( Primitives.getSphere( 5.0f ), new SimpleVector( 0, 0, 0 ), "Bubble", article );
		
		this.type = BBWordObject.Classification.BUBBLE;
		
		// Timestamp the creation of this bubble 
		timeCreated = timeInMillis;
		
		// Assign article gender
		this.article = article;
		// Initialize parameters
		isHolding = false;
		heldObjectId = -1;
		
		// Determine what color the bubble will be based on the article gender
		Color c;
		switch ( this.article ) {
			case MASCULINE:
				c = Color.BLUE;
				break;
			case FEMININE:
				c = Color.RED;
				break;
			default:
				c = Color.BLUE;
				break;
		}
		// Set material properties
		setAdditionalColor( color( c ) );
		// Make bubble translucent
		setTransparency( 8 );
		// Set lighting properties
		setSpecularLighting( Object3D.SPECULAR_ENABLED );
		// Apply the default texture
		setTexture( "Default" );
		calcTextureWrapSpherical();
		// Forces bubble to always face camera
		// Note: This is to allow for readability of the constituent object's label.
		setBillboarding( Object3D.BILLBOARDING_ENABLED );
		// Place bubble in the specified position
		translate( translation );
		// Allow bubble to move around (see BBWordObject())
		disableLazyTransformations();
		// Build bubble
		build();
		// Define collision mode
		setCollisionMode( Object3D.COLLISION_CHECK_SELF );
		setCollisionOptimization( Object3D.COLLISION_DETECTION_OPTIMIZED );
		// Assign collision listener (to be triggered when a collision is detected)
		addCollisionListener(new CollisionListener() {

			private static final long serialVersionUID = 6948038879463091212L;

			@Override
			public void collision( CollisionEvent ce ) {
				// Identify source (bubble)
				BBBubble bubble = (BBBubble) ce.getSource();
				// Identify collision targets (usually objects, sometimes other bubbles)
				Object3D[] targets = ce.getTargets();
				// Unbind the collision listener
				// Note: Since we're only using BBWordObjects as the collision objects in the 
				//       jPCT world, we don't have to worry about bubble-wall collisions
				//       accidentally making this bubble useless/not able to capture objects.
				removeCollisionListener( this );
				// Defer to the collision handler
				BBGame.getInstance().handleCollision( bubble, (BBWordObject) targets[0] );
			}

			@Override
			public boolean requiresPolygonIDs() {
				return false;
			}
			
		});
	}
	
	/**
	 * @return
	 */
	public boolean isHolding() {
		return isHolding;
	}
	
	/**
	 * @param id
	 */
	public void setHeldObjectId( int id ) {
		heldObjectId = id;
		isHolding = true;
	}
	
	/**
	 * @return
	 */
	public int getHeldObjectId() {
		return heldObjectId;
	}
	
	/**
	 * @param index
	 */
	public void setLocalBodyIndex( int index ) {
		localBodyIndex = index;
	}
	
	/**
	 * @return
	 */
	public int getLocalBodyIndex() {
		return localBodyIndex;
	}
	
	/**
	 * @return
	 */
	public int getBodyIndex() {
		return bodyIndex;
	}
	
	/**
	 * @param index
	 */
	public void setBodyIndex( int index ) {
		bodyIndex = index;
	}
	
	/* ( non-Javadoc )
	 * @see com.inherentgames.WordObject#getObjectId()
	 */
	public int getObjectId() {
		return objectId;
	}
	
	/* ( non-Javadoc )
	 * @see com.inherentgames.WordObject#setObjectId( int )
	 */
	public void setObjectId( int id ) {
		objectId = id;
	}
	
	/**
	 * @return
	 */
	public long getTimeCreated() {
		return timeCreated;
	}
	
	private RGBColor color( Color c ) {
		return new RGBColor( c.r, c.g, c.b );
	}
}
