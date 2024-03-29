package com.inherentgames;

import javax.vecmath.Vector3f;
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
 * An extension of BBSurface that is based around walls
 */
public class BBWall extends BBSurface {
	
	
	private SimpleVector[] coordinates = new SimpleVector[4];
	private SimpleVector origin;
	private float width;
	private float height;
	private Object3D wall = new Object3D( 2 );
	
	private RigidBody body;
	private CollisionShape groundShape;

	/**
	 * The constructor for BBWall. Creates basic plane objects and sets the texture based on given parameters
	 * 
	 * @param origin - a vector giving the location of the surface
	 * @param width - the width of the surface
	 * @param height - the height of the surface
	 * @param textureId - the texture id to be applied to the surface
	 */
	public BBWall( SimpleVector origin, float width, float height, String textureName ) {
		super( origin, width, height, 0 );
		this.width = width;
		this.height = height;
		this.origin = origin;
		
		setCoordinates();
		
		wall.setAdditionalColor( 100, 100, 100 );
		wall.setTransparencyMode( Object3D.TRANSPARENCY_MODE_ADD );
		//uvs represent texture locations
		//uvs[0] = xMin, uvs[1] = yMin, uvs[2] = xMax, uvs[3] =  yMax
		wall.addTriangle( coordinates[1], 1, 1, coordinates[0], 0, 1, coordinates[2], 1, 0 );
		wall.addTriangle( coordinates[0], 0, 1, coordinates[3], 0, 0, coordinates[2], 1, 0 );
		
		
		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		groundTransform.origin.set( new Vector3f( origin.x, origin.y, origin.z ) );
		float mass = 0f;
		Vector3f localInertia = new Vector3f( 0, 0, 0 );
		DefaultMotionState myMotionState = new DefaultMotionState( groundTransform );
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo( mass,
				myMotionState, groundShape, localInertia );
		body = new RigidBody( rbInfo );
		
	}
	
	/**
	 * @return - the Object3D associated with this class
	 */
	public Object3D getWall() {
		return wall;
	}
	
	/**
	 * Sets the coordinates of the surface, ensuring the orientation of the wall is correct and the texture is facing
	 * the correct direction.
	 */
	private void setCoordinates() {
		if ( origin.x == 0 && origin.z > 0 ) {
			coordinates[0] = new SimpleVector( origin.x-( width/2 ), origin.y-( height/2 ), origin.z );
			coordinates[1] = new SimpleVector( origin.x+( width/2 ), origin.y-( height/2 ), origin.z );
			coordinates[2] = new SimpleVector( origin.x+( width/2 ), origin.y+( height/2 ), origin.z );
			coordinates[3] = new SimpleVector( origin.x-( width/2 ), origin.y+( height/2 ), origin.z );
			groundShape =  new BoxShape( new Vector3f( width, height, 1.0f ) );
		}
		else if ( origin.x > 0 && origin.z == 0 ) {
			coordinates[0] = new SimpleVector( origin.x, origin.y-( height/2 ), origin.z+( width/2 ) );
			coordinates[1] = new SimpleVector( origin.x, origin.y-( height/2 ), origin.z-( width/2 ) );
			coordinates[2] = new SimpleVector( origin.x, origin.y+( height/2 ), origin.z-( width/2 ) );
			coordinates[3] = new SimpleVector( origin.x, origin.y+( height/2 ), origin.z+( width/2 ) );
			groundShape =  new BoxShape( new Vector3f( 1.0f, height, width ) );
		}
		if ( origin.x == 0 && origin.z < 0 ) {
			coordinates[0] = new SimpleVector( origin.x+( width/2 ), origin.y-( height/2 ), origin.z );
			coordinates[1] = new SimpleVector( origin.x-( width/2 ), origin.y-( height/2 ), origin.z );
			coordinates[2] = new SimpleVector( origin.x-( width/2 ), origin.y+( height/2 ), origin.z );
			coordinates[3] = new SimpleVector( origin.x+( width/2 ), origin.y+( height/2 ), origin.z );
			groundShape =  new BoxShape( new Vector3f( width, height, 1.0f ) );
		}
		if ( origin.x < 0 && origin.z == 0 ) {
			coordinates[0] = new SimpleVector( origin.x, origin.y-( height/2 ), origin.z-( width/2 ) );
			coordinates[1] = new SimpleVector( origin.x, origin.y-( height/2 ), origin.z+( width/2 ) );
			coordinates[2] = new SimpleVector( origin.x, origin.y+( height/2 ), origin.z+( width/2 ) );
			coordinates[3] = new SimpleVector( origin.x, origin.y+( height/2 ), origin.z-( width/2 ) );
			groundShape =  new BoxShape( new Vector3f( 1.0f, height, width ) );
		}
	}

	/**
	 * @return - the RigidBody associated with the wall
	 */
	public RigidBody getBody() {
		return body;
	}
	
}
