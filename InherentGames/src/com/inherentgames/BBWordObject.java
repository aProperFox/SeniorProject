package com.inherentgames;

import android.util.Log;

import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

/**
 * @author Tyler
 * An Extension of the JPCT-AE class Object3D. This handles all of the objects in a room. The
 * key addition here is that BBWordObjects have words and articles associated with them.
 */
public class BBWordObject extends Object3D {

	private static final long serialVersionUID = -4088731260124106298L;
	
	// Define valid genders and classifications
	public static enum Gender { MASCULINE, FEMININE, NONE };
	public static enum Classification { WORD_OBJECT, BUBBLE };
	
	// Tracks whether this object is moving around (i.e., inside a bubble or not)
	private boolean isStatic;
	protected float maxDimension;
	protected float startScale;
	private int objectId = -1;
	
	private SimpleVector initialRotation = new SimpleVector( 0, 0, 0 );
	// Tracks the gender of this object
	protected Gender article;
	// Stores the name of the object in all relevant languages
	private String names[] = new String[2];
	// Tracks what type of object this is
	// Note: This is only used to distinguish between a BBBubble that is a child of 
	//       this type, or a regular BBWordObject.
	protected Classification type = Classification.WORD_OBJECT;
	
	
	/**
	 * The constructor for BBWordObject. Sets the initial rotation of the object (so it can be
	 * rotate back if the game is reset), the article, names, and collisionMode.
	 * 
	 * @param obj - the BBWordObject to base this BBWordObject off of
	 */
	public BBWordObject( BBWordObject obj ) {
		super( obj.toObject3D() );
		isStatic = true;
		this.maxDimension = obj.getMaxDimension();
		names[BBTranslator.Language.ENGLISH] = obj.getName( BBTranslator.Language.ENGLISH );
		this.article = obj.article;
		initialRotation = obj.initialRotation;
		// Saves calculating transformation matrix until lazy transformations are disabled again, improving performance
		enableLazyTransformations();
		// Indicates that other objects can collide into this object
		setCollisionMode( Object3D.COLLISION_CHECK_OTHERS );
		
		// Used to rescale when objects are reset
		startScale = this.getScale();
	}
	
	/**
	 * The constructor for BBWordObject. Sets the initial rotation of the object (so it can be
	 * rotate back if the game is reset), the article, names, and collisionMode.
	 * 
	 * @param obj - the Object3D to base this BBWordObject off of
	 * @param rotationAxis - the vector to rotate the object by (in radians)
	 * @param name - the english name of the object
	 * @param article - the spanish gender of the object
	 */
	public BBWordObject( Object3D obj, SimpleVector rotationAxis, String name, Gender article ) {
		super( obj );
		isStatic = true;
		names[BBTranslator.Language.ENGLISH] = name;
		this.article = article;
		rotateBy( rotationAxis );
		setMaxDimension();
		// Saves calculating transformation matrix until lazy transformations are disabled again, improving performance
		enableLazyTransformations();
		// Indicates that other objects can collide into this object
		setCollisionMode( Object3D.COLLISION_CHECK_OTHERS );
	}
	
	/**
	 * @return - true if the object is not moving, false if it is
	 */
	public boolean getStaticState() {
		return isStatic;
	}
	
	/**
	 * Sets isStatic to the given boolean
	 * 
	 * @param state - the boolean to set isStatic to
	 */
	public void setStatic( boolean state ) {
		isStatic = state;
	}
	
	/**
	 * Sets the maxDimension variable to the max distance from the center to the mesh end. Important
	 * for scaling down to bubble size.
	 */
	public void setMaxDimension() {
		long startTime = System.currentTimeMillis();
		Mesh mesh = this.getMesh();
		maxDimension = getMax( mesh.getBoundingBox() );
		Log.d( "WordObject", "Checking dimensions took " + ( System.currentTimeMillis()-startTime ) + " milliseconds." );
	}
	
	/**
	 * @return - maxDimension of the object. Important for scaling down to bubble size
	 */
	public float getMaxDimension() {
		return maxDimension;
	}
	
	/**
	 * Lets the object be aware of its world id
	 * 
	 * @param id - the id to set the objectId to
	 */
	public void setObjectId( int id ) {
		this.objectId = id;
	}
	
	/**
	 * @return - the world id of the object
	 */
	public int getObjectId() {
		return objectId;
	}
	
	/* ( non-Javadoc )
	 * @see com.threed.jpct.Object3D#setName( java.lang.String )
	 */
	@Override
	public void setName( String name ) {
		/**
		 * TODO: setting for language
		 * replace Translator.ENGLISH and Translator.SPANISH with global language parameters
		 */
		names[BBTranslator.Language.ENGLISH] = name;
		names[BBTranslator.Language.SPANISH] = BBTranslator.translateToLanguage( name, BBTranslator.Language.SPANISH );
	}
	
	/**
	 * Gets the name of the object in the requested language
	 * 
	 * @param language - the language requested
	 * @return - the name of the object in the requested language
	 */
	public String getName( int language ) {
		return names[language];
	}
	
	/* ( non-Javadoc )
	 * @see com.threed.jpct.Object3D#scale( float )
	 */
	@Override
	public void scale( float scaleTo ) {
		super.scale( scaleTo/maxDimension );
	}
	
	/**
	 * Opposite of scale(). Scales the object back from the given size
	 * 
	 * @param scaleFrom - the float to scale the object from
	 */
	public void scaleFrom( float scaleFrom ) {
		super.scale( maxDimension/scaleFrom );
	}
	
	/**
	 * Removes an object from the room
	 * 
	 * @param room - the room (world) to remove the object from
	 */
	public void removeObject( BBRoom room ) {
		room.removeObject( objectId );
	}
	
	/**
	 * Rotates the object by the given vector (in radians)
	 * 
	 * @param axes - the vector to rotate by
	 */
	public void rotateBy( SimpleVector axes ) {
		if ( initialRotation.equals( new SimpleVector( 0, 0, 0 )))
			initialRotation = axes;
		this.rotateX( axes.x );
		this.rotateY( axes.y );
		this.rotateZ( axes.z );
	}
	
	/**
	 * Adds to the initalRotation vector (if object has been rotated beyond the base object's rotation set in BBRoom)
	 * 
	 * @param vector - the vector to add to the initalRotation vector
	 */
	public void addInitialRotation( SimpleVector vector ) {
		initialRotation.add( vector );
	}
	
	/* (non-Javadoc)
	 * @see com.threed.jpct.Object3D#clearRotation()
	 */
	@Override
	public void clearRotation() {
		super.clearRotation();
		if ( !initialRotation.equals( new SimpleVector( 0, 0, 0 )))
			rotateBy ( initialRotation );
		Log.d("BBWordObject", "Object: " + names[0] +"initialRotation = " + initialRotation);
	}
	
	/**
	 * @return - a casted Object3D of this
	 */
	public Object3D toObject3D() {
		return (Object3D) this;
	}

	/**
	 * A simple max function for getting the highest value of the given vertices
	 * 
	 * @param vertices - the vertices of the object
	 * @return - the max vertex
	 */
	public float getMax( float[] vertices ) {
		float max = 0;
		for ( float vertex: vertices ) {
			if ( Math.abs( vertex ) > max ) {
				max = Math.abs( vertex );
			}
		}
		return max*1.1f;
	}
	
}
