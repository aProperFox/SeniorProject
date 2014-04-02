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
	public static final int MASCULINE = 0;
	public static final int FEMININE = 1;
	
	private boolean isStatic;
	private float maxDimension;
	private int objectId = -1;
	private int article;
	private String names[] = new String[2];
	
	
	/**
	 * @param obj
	 */
	public BBWordObject( BBWordObject obj ) {
		super( obj.toObject3D() );
		isStatic = true;
		this.maxDimension = obj.getMaxDimension();
		names[BBTranslator.ENGLISH] = obj.getName( BBTranslator.ENGLISH );
		this.article = obj.getArticle();
	}
	
	/**
	 * @param obj
	 * @param rotationAxis
	 * @param name
	 * @param article
	 */
	public BBWordObject( Object3D obj, SimpleVector rotationAxis, String name, int article ) {
		super( obj );
		isStatic = true;
		names[BBTranslator.ENGLISH] = name;
		this.article = article;
		rotateBy( rotationAxis );
		setMaxDimension();
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
		/*
		PolygonManager polyMan = this.getPolygonManager();
		int polygons = polyMan.getMaxPolygonID();
		SimpleVector minVerts = new SimpleVector( 1000, 1000, 1000 );
		SimpleVector maxVerts = new SimpleVector( -1000, -1000, -1000 );
		for ( int i = 0; i < polygons; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				if ( minVerts.x > polyMan.getTransformedVertex( i, j ).x )
					minVerts.x = polyMan.getTransformedVertex( i, j ).x;
				if ( maxVerts.x < polyMan.getTransformedVertex( i, j ).x )
					maxVerts.x = polyMan.getTransformedVertex( i, j ).x;
				if ( minVerts.y > polyMan.getTransformedVertex( i, j ).y )
					minVerts.y = polyMan.getTransformedVertex( i, j ).y;
				if ( maxVerts.y < polyMan.getTransformedVertex( i, j ).y )
					maxVerts.y = polyMan.getTransformedVertex( i, j ).y;
				if ( minVerts.z > polyMan.getTransformedVertex( i, j ).z )
					minVerts.z = polyMan.getTransformedVertex( i, j ).z;
				if ( maxVerts.z < polyMan.getTransformedVertex( i, j ).z )
					maxVerts.z = polyMan.getTransformedVertex( i, j ).z;
			}
		}
		SimpleVector dimensions = new SimpleVector( maxVerts.x - minVerts.x, maxVerts.y - minVerts.y, maxVerts.z - minVerts.z );
		if ( dimensions.x > dimensions.z &&dimensions.x > dimensions.y )
			maxDimension = dimensions.x;
		else if ( dimensions.z > dimensions.x && dimensions.z > dimensions.y )
			maxDimension = dimensions.z;
		else
			maxDimension = dimensions.y;
			*/
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
	
	/**
	 * @return
	 */
	public int getArticle() {
		return article;
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
		names[BBTranslator.ENGLISH] = name;
		names[BBTranslator.SPANISH] = BBTranslator.translateToLanguage( name, BBTranslator.SPANISH );
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
