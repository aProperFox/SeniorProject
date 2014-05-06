package com.inherentgames;

import com.threed.jpct.CollisionEvent;
import com.threed.jpct.CollisionListener;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;

/**
 * @author Tyler
 * BBBubble is a class that extends BBWordObject. Every bubble shot during gameplay is a new BBBubble, and is added to the
 * graphics engine and physics engine.
 */
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
	protected boolean isHolding;
	// Tracks the object this bubble is holding
	private int heldObjectId;
	// Store various IDs of this object
	private int objectId= -1;
	private int bodyIndex = -1;
	private int localBodyIndex = -1;
	// Track when this bubble was created
	private long timeCreated;
	
	/**
	 * The Constructor for BBBubble. Takes in a translation vector, an enum Gender, and a time created. The constructor will
	 * add the appropriate color to the bubble, depending con what the Gender given to it. Base JPCT-AE handling is done
	 * in the constructor, but physics is added during declaration.
	 * 
	 * @param translation - a JPCT-AE SimpleVector that gives it a starting point (if bubble not shot from (0,0,0) 
	 * @param article - BBWordObject enum Gender can have either a feminine or masculine state
	 * @param timeInMillis - the time of creation, in milliseconds
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
	 * Called when a bubble captures an object in the room. It will set its variable heldObjectId to
	 * the world id of the object captured, as received by the JPCT-AE function Object3D.getID() 
	 * 
	 * @param id - an int passed in of the captured object's world id
	 */
	public void setHeldObjectId( int id ) {
		heldObjectId = id;
		isHolding = true;
	}
	
	/**
	 * Called when needing the world object id of the captured object. It is used for rotating the object
	 * and updating its location relative to the bubble.
	 * 
	 * @return heldObjectId - the world object id of the held object (if any) 
	 */
	public int getHeldObjectId() {
		return heldObjectId;
	}
	
	/**
	 * Used for the 'bubbles' ArrayList in BBRoom to keep track of all bubble id's, since it is not native
	 * to JPCT-AE's 'World' class. Stored in local variable 'localBodyIndex'
	 * 
	 * @param index - the int index of this bubble in the BBRoom.bubbles ArrayList
	 */
	public void setLocalBodyIndex( int index ) {
		localBodyIndex = index;
	}
	
	/**
	 * Gets the localBodyIndex of this bubble in the BBRoom.bubbles ArrayList
	 * 
	 * @return localBodyIndex - an int representing the location of this bubble in BBRoom.bubbles
	 */
	public int getLocalBodyIndex() {
		return localBodyIndex;
	}
	
	/**
	 * Gets the index of the bubble's jBullet object RigidBody to find the object in the physics engine.
	 * 
	 * @return bodyIndex - an int that refers to the bubble's RigidBody id in the physics engine
	 */
	public int getBodyIndex() {
		return bodyIndex;
	}
	
	/**
	 * Sets the index of the bubble's jBullet object RigidBody to find the object in the physics engine. 
	 * 
	 * @param index - an int that refers to the bubble's RigidBody id in the physics engine
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
	 * Gets the time the bubble was created 
	 * 
	 * @return timeCreated - the time the bubble was created in milliseconds
	 */
	public long getTimeCreated() {
		return timeCreated;
	}
	
	/**
	 * Converts the enum BBBubble.Color to the JPCT-AE RGBColor
	 * 
	 * @param c - the BBBubble.Color 
	 * @return - a new RGBColor based on the BBBubble.Color c
	 */
	private RGBColor color( Color c ) {
		return new RGBColor( c.r, c.g, c.b );
	}
}
