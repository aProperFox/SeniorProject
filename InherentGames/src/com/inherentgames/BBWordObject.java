package com.inherentgames;

import android.util.Log;

import com.threed.jpct.Mesh;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class BBWordObject extends Object3D {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4088731260124106298L;
	
	// Define valid genders and classifications
	public static enum Gender { MASCULINE, FEMININE };
	public static enum Classification { WORD_OBJECT, BUBBLE };
	
	// Tracks whether this object is moving around (i.e., inside a bubble or not)
	private boolean isStatic;
	private float maxDimension;
	private int objectId = -1;
	// Tracks the gender of this object
	protected Gender article;
	// Stores the name of the object in all relevant languages
	private String names[] = new String[2];
	// Tracks what type of object this is
	// Note: This is only used to distinguish between a BBBubble that is a child of 
	//       this type, or a regular BBWordObject.
	protected Classification type = Classification.WORD_OBJECT;
	
	
	/**
	 * @param obj
	 */
	public BBWordObject( BBWordObject obj ) {
		super( obj.toObject3D() );
		isStatic = true;
		this.maxDimension = obj.getMaxDimension();
		names[BBTranslator.Language.ENGLISH] = obj.getName( BBTranslator.Language.ENGLISH );
		this.article = obj.article;
		// Saves calculating transformation matrix until lazy transformations are disabled again, improving performance
		enableLazyTransformations();
		// Indicates that other objects can collide into this object
		setCollisionMode( Object3D.COLLISION_CHECK_OTHERS );
	}
	
	/**
	 * @param obj
	 * @param rotationAxis
	 * @param name
	 * @param article
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
	 * @return
	 */
	public boolean getStaticState() {
		return isStatic;
	}
	
	/**
	 * @param state
	 */
	public void setStatic( boolean state ) {
		isStatic = state;
	}
	
	/**
	 * 
	 */
	public void setMaxDimension() {
		long startTime = System.currentTimeMillis();
		Mesh mesh = this.getMesh();
		maxDimension = getMax( mesh.getBoundingBox() );
		Log.d( "WordObject", "Checking dimensions took " + ( System.currentTimeMillis()-startTime ) + " milliseconds." );
	}
	
	/**
	 * @return
	 */
	public float getMaxDimension() {
		return maxDimension;
	}
	
	/**
	 * @param id
	 */
	public void setObjectId( int id ) {
		this.objectId = id;
	}
	
	/**
	 * @return
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
	 * @param language
	 * @return
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
	 * @param room
	 */
	public void removeObject( BBRoom room ) {
		room.removeObject( objectId );
	}
	
	/**
	 * @param axes
	 */
	public void rotateBy( SimpleVector axes ) {
		this.rotateX( axes.x );
		this.rotateY( axes.y );
		this.rotateZ( axes.z );
	}
	
	/**
	 * @return
	 */
	public Object3D toObject3D() {
		return ( Object3D )this;
	}

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
