package com.inherentgames;

import javax.vecmath.Vector3f;

import android.util.Log;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

/**
 * @author Tyler
 * A class that keeps track of Floor and Ceiling objects, setting their texture, size, location, and physics.
 */
public class BBFloor {
	SimpleVector[] coordinates = new SimpleVector[4];
	String type = "";
	
	Object3D floor = new Object3D( 2 );
	
	private RigidBody body;
	
	/**
	 * The Constructor for BBFloor. Determines whether the object is a floor or ceiling by looking at its y-value,
	 * then creates two triangles and puts physics on them.
	 * 
	 * @param size - a JPCT-AE SimpleVector where x and z are the width and length, and y is the height
	 * @param textureId - a texture id to set the texture of the triangles when created (may not be needed as setTexture is called later.)
	 */
	public BBFloor( SimpleVector size, int textureId ) {
		coordinates[0] = new SimpleVector( -size.x/2, size.y, +size.z/2 );
		coordinates[1] = new SimpleVector( +size.x/2, size.y, +size.z/2 );
		coordinates[2] = new SimpleVector( +size.x/2, size.y, -size.z/2 );
		coordinates[3] = new SimpleVector( -size.x/2, size.y, -size.z/2 );
		
		//JPCT can only render triangles created counter-clockwise relative to the camera?
		if ( size.y < 0 ) {
			type = "ceiling";
			//floor.setAdditionalColor( 200, 200, 200 );
			floor.addTriangle( coordinates[1], 1, 1, coordinates[2], 1, 0, coordinates[0], 0, 1, textureId );
			floor.addTriangle( coordinates[0], 0, 1, coordinates[2], 1, 0, coordinates[3], 0, 0, textureId );
		}
		if ( size.y > 0 ) {
			type = "floor";
			//floor.setAdditionalColor( 5, 5, 5 );
			floor.addTriangle( coordinates[1], 1, 1, coordinates[0], 0, 1, coordinates[2], 1, 0, textureId );
			floor.addTriangle( coordinates[0], 0, 1, coordinates[3], 0, 0, coordinates[2], 1, 0, textureId );
		}
		
		CollisionShape groundShape =  new BoxShape( new Vector3f( size.x, 1.0f, size.z ) );
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set( new Vector3f( 0.0f, -size.y, 0.0f ) );
		float mass = 0f;
		Vector3f localInertia = new Vector3f( 0, 0, 0 );
		DefaultMotionState myMotionState = new DefaultMotionState( groundTransform );
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo( mass,
				myMotionState, groundShape, localInertia );
		body = new RigidBody( rbInfo );

	}
	
	/**
	 * Function that calls the setTexture function on the JPCT-AE Object3D 'floor' contained by this class
	 * 
	 * @param tex - texture to be set, which has been added to the JPCT-AE TextureManager
	 */
	public void setTexture( String tex ) {
		floor.setTexture( tex );
	}
	
	/**
	 * Gets the JPCT-AE Object3D 'floor' contained by this class
	 * 
	 * @return - the JPCT-AE Object3D 'floor' contained by this class
	 */
	public Object3D getFloor() {
		return floor;
	}
	
	/**
	 * Gets the jBullet RigidBody (physics body) of the floor/ceiling
	 * 
	 * @return
	 */
	public RigidBody getBody() {
		return body;
	}
}
