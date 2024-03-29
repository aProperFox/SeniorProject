package com.inherentgames;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import android.graphics.Bitmap;
import android.util.Log;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.inherentgames.BBWordObject.Gender;
import com.threed.jpct.GLSLShader;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.SkyBox;


/**
 * @author Tyler
 * An extension of the JPCT-AE class 'World', BBRoom handles loading objects and placing the room. It also creates
 * 4 walls and a floor and ceiling using the parameters stored per room. 
 * Since it is the room that everything is contained in, it keeps track of bubbles, wordObjects, and their RigidBodies
 */
public class BBRoom extends World {
	
	private static final long serialVersionUID = 9088044018714661773L;
	
	private BBTextureManager tm;
	protected SkyBox skybox;
	
	/**
	 * @author Tyler
	 * An enum to define which level is being played
	 */
	public static enum Level { TUTORIAL, CLASSROOM, DINER, STREET, BEACH; 
		/**
		 * @return - the next level in the list
		 */
		public Level getNext() {
	     return this.ordinal() < Level.values().length - 1
	         ? Level.values()[this.ordinal() + 1]
	         : null;
	   }	
	};
	
	protected Gender currentGender = Gender.NONE;
	
	private ArrayList<Object3D> walls = new ArrayList<Object3D>();
	protected BBWall wall;
	protected BBFloor floor;
	protected BBFloor ceiling;
	private ArrayList<RigidBody> bodies = new ArrayList<RigidBody>();
	private ArrayList<RigidBody> bubbles = new ArrayList<RigidBody>();
	private ArrayList<BBBubble> bubbleObjects;
	private ArrayList<BBWordObject> wordObjects;
	private ArrayList<BBWordObject> roomObjects;
	protected ArrayList<String> roomObjectWords;
	
	private float height, width, length;

	// TODO: implement modes for checking objects
	public static enum Mode { PEROBJECT, ALL };
	
	protected Mode gameMode;
	protected int numRequiredObjects;
	
	/*
	 * TODO: add color to WordObjects when camera is aimed at them
	private int cameraBoxId = 0;
	*/
	
	/**
	 * The Constructor for BBRoom. Sets the surfaces and objects in the room as well as instantiating
	 * variables.
	 * 
	 * @param roomId - the room to be played
	 */
	public BBRoom( Level roomId ) {
		
		tm = BBTextureManager.getInstance();
		
		skybox = null;
		roomObjectWords = new ArrayList<String>();
		wordObjects = new ArrayList<BBWordObject>();
		bubbleObjects = new ArrayList<BBBubble>();
		roomObjects = new ArrayList<BBWordObject>();
		
		// Adds walls to list 'walls' based on room ID, also sets wallNum variable
		setSurfaces( roomId );
		for ( int i = 0; i < walls.size(); i++ ) {
			//Adds all walls to world
			addObject( walls.get( i ) );
		}

		setObjects( roomId );
		
	}
	
	/**
	 * Has special light locations per room.
	 * 
	 * @param roomNum - the current level being played
	 * @return - a SimpleVector of the light position
	 */
	public SimpleVector getLightLocation( Level roomNum ) {
		//Get light location vector based on Room Id
		switch( roomNum ) {
		case TUTORIAL:
			return new SimpleVector( 0, -20, 0 );
		case STREET:
			return new SimpleVector( 0, -195, 0 );
		default:
			return new SimpleVector( 0, -20, 0 );
		}
	}
	
	/**
	 * Gets the RigidBody associated with a certain id
	 * 
	 * @param id - the id of the RigidBody to get
	 * @return - the RigidBodt associated with the given id
	 */
	public RigidBody getBody( int id ) {
		return bodies.get( id );
	}
	
	/**
	 * Gets the number of bubbles in the room
	 * 
	 * @return - the size of the bubbles ArrayList (a.k.a. the number of bubbles floating around)
	 */
	public int getNumBubbles() {
		return bubbles.size();
	}
	
	/**
	 * Gets the number of RigidBodies in the room
	 * 
	 * @return - the size of the bodies ArrayList( a.k.a. the number of bodies in the room)
	 */
	public int getNumBodies() {
		return bodies.size();
	}
	
	/**
	 * Removes the given bubble from the world.
	 * 
	 * @param bubble - the bubble to be removed
	 */
	public void removeBubble( BBBubble bubble ) {
		bubbleObjects.remove( bubble );
		bubbles.remove( bubble.getLocalBodyIndex() );
		removeObject( bubble );
	}
	
	/**
	 * Sets the walls and floor/ceiling of the room based on the level
	 * 
	 * @param room - the level to set up
	 */
	public void setSurfaces( Level room ) {
		BBWall wall;
		//set walls by room number
		switch( room ) {

		case TUTORIAL:
			
			//First wall
			wall = new BBWall( new SimpleVector( 0, 0, 75 ), 80, 50, "TutorialWall" );
			walls.add( wall.getWall() );
			walls.get( 0 ).setTexture( "TutorialWall" );
			bodies.add( wall.getBody() );
			//Second wall
			wall = new BBWall( new SimpleVector( 40, 0, 0 ), 150, 50, "TutorialWall" );
			walls.add( wall.getWall() );
			walls.get( 1 ).setTexture( "TutorialWall" );
			bodies.add( wall.getBody() );
			//Third wall
			wall = new BBWall( new SimpleVector( 0, 0, -75 ), 80, 50, "TutorialWall" );
			walls.add( wall.getWall() );
			walls.get( 2 ).setTexture( "TutorialWall" );
			bodies.add( wall.getBody() );
			//Fourth wall
			wall = new BBWall( new SimpleVector( -40, 0, 0 ), 150, 50, "TutorialWall" );
			walls.add( wall.getWall() );
			walls.get( 3 ).setTexture( "TutorialWall" );
			bodies.add( wall.getBody() );
			
			//Wall class and floor class to be changed to extend surface class
			//TODO change Floor class to allow change center
			floor = new BBFloor( new SimpleVector( 80, 25, 150 ), 0 );
			floor.setTexture( "TutorialFloor" );
			bodies.add( floor.getBody() );
			addObject( floor.getFloor() );
			ceiling = new BBFloor( new SimpleVector( 80, -25, 150 ), 0 );
			ceiling.setTexture( "TutorialCeiling" );
			bodies.add( ceiling.getBody() );
			addObject( ceiling.getFloor() );
			
			break;
			
		case CLASSROOM:		

			height = 60;
			length = 180;
			width = 156;
			
			//First wall
			wall = new BBWall( new SimpleVector( 0, 0, length/2 ), width, height, "Room0Wall0" );
			walls.add( wall.getWall() );
			walls.get( 0 ).setTexture( "Room0Wall0" );
			bodies.add( wall.getBody() );
			//Second wall
			wall = new BBWall( new SimpleVector( width/2, 0, 0 ), length, height, "Room0Wall1" );
			walls.add( wall.getWall() );
			walls.get( 1 ).setTexture( "Room0Wall1" );
			bodies.add( wall.getBody() );
			//Third wall
			wall = new BBWall( new SimpleVector( 0, 0, -length/2 ), width, height, "Room0Wall2" );
			walls.add( wall.getWall() );
			walls.get( 2 ).setTexture( "Room0Wall2" );
			bodies.add( wall.getBody() );
			//Fourth wall
			wall = new BBWall( new SimpleVector( -width/2, 0, 0 ), length, height, "Room0Wall3" );
			walls.add( wall.getWall() );
			walls.get( 3 ).setTexture( "Room0Wall3" );
			bodies.add( wall.getBody() );
			
			//Wall class and floor class to be changed to extend surface class
			floor = new BBFloor( new SimpleVector( width, height/2, length ), 0 );
			floor.setTexture( "Room0Floor" );
			bodies.add( floor.getBody() );
			addObject( floor.getFloor() );
			ceiling = new BBFloor( new SimpleVector( width, -height/2, length ), 0 );
			ceiling.setTexture( "Room0Ceiling" );
			bodies.add( ceiling.getBody() );
			addObject( ceiling.getFloor() );
			
			break;
			
		case DINER:
			
			//First wall
			wall = new BBWall( new SimpleVector( 0, 0, 75 ), 130, 50, "Room1Wall0" );
			walls.add( wall.getWall() );
			walls.get( 0 ).setTexture( "Room1Wall0" );
			bodies.add( wall.getBody() );
			//Second wall
			wall = new BBWall( new SimpleVector( 65, 0, 0 ), 150, 50, "Room1Wall1" );
			walls.add( wall.getWall() );
			walls.get( 1 ).setTexture( "Room1Wall1" );
			bodies.add( wall.getBody() );
			//Third wall
			wall = new BBWall( new SimpleVector( 0, 0, -75 ), 130, 50, "Room1Wall2" );
			walls.add( wall.getWall() );
			walls.get( 2 ).setTexture( "Room1Wall2" );
			bodies.add( wall.getBody() );
			//Fourth wall
			wall = new BBWall( new SimpleVector( -65, 0, 0 ), 150, 50, "Room1Wall3" );
			walls.add( wall.getWall() );
			walls.get( 3 ).setTexture( "Room1Wall3" );
			bodies.add( wall.getBody() );
			
			//Wall class and floor class to be changed to extend surface class
			floor = new BBFloor( new SimpleVector( 130, 25, 150 ), 0 );
			floor.setTexture( "Room1Floor" );
			bodies.add( floor.getBody() );
			addObject( floor.getFloor() );
			ceiling = new BBFloor( new SimpleVector( 130, -25, 150 ), 0 );
			ceiling.setTexture( "Room1Ceiling" );
			bodies.add( ceiling.getBody() );
			addObject( ceiling.getFloor() );
			
			break;
			
		case STREET:
			
			try {
				Bitmap bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.skybox_left ) ), 1024, 1024 );
				tm.addTexture( "SkyboxLeft", new Texture( bitmap, true ) );
				bitmap.recycle();
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.skybox_front ) ), 1024, 1024 );
				tm.addTexture( "SkyboxFront", new Texture( bitmap, true ) );
				bitmap.recycle();
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.skybox_right ) ), 1024, 1024 );
				tm.addTexture( "SkyboxRight", new Texture( bitmap, true ) );
				bitmap.recycle();
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.skybox_back ) ), 1024, 1024 );
				tm.addTexture( "SkyboxBack", new Texture( bitmap, true ) );
				bitmap.recycle();
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.skybox_top ) ), 1024, 1024 );
				tm.addTexture( "SkyboxTop", new Texture( bitmap, true ) );
				bitmap.recycle();
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.skybox_bottom ) ), 1024, 1024 );
				tm.addTexture( "SkyboxBottom", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room3wall0 ) ), 512, 512 );
				tm.addTexture( "Room3Wall0", new Texture( bitmap, true ) );
				bitmap.recycle();
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room3wall1 ) ), 512, 512 );
				tm.addTexture( "Room3Wall1", new Texture( bitmap, true ) );
				bitmap.recycle();
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room3wall2 ) ), 512, 512 );
				tm.addTexture( "Room3Wall2", new Texture( bitmap, true ) );
				bitmap.recycle();
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room3wall3 ) ), 512, 512 );
				tm.addTexture( "Room3Wall3", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room3floor ) ), 512, 512 );
				tm.addTexture( "Room3Floor", new Texture( bitmap, true ) );
				bitmap.recycle();
				
			} catch( Exception e ) {
				e.printStackTrace();
			}
			
			skybox = new SkyBox( "SkyboxLeft", "SkyboxFront", "SkyboxRight", "SkyboxBack", "SkyboxTop", "SkyboxBottom", 200f );
			//First wall
			wall = new BBWall( new SimpleVector( 0, -15, 95 ), 190, 50, "Room3Wall0" );
			walls.add( wall.getWall() );
			walls.get( 0 ).setTexture( "Room3Wall0" );
			bodies.add( wall.getBody() );
			//Second wall
			wall = new BBWall( new SimpleVector( 95, -15, 0 ), 190, 50, "Room3Wall1" );
			walls.add( wall.getWall() );
			walls.get( 1 ).setTexture( "Room3Wall1" );
			bodies.add( wall.getBody() );
			//Third wall
			wall = new BBWall( new SimpleVector( 0, -15, -95 ), 190, 50, "Room3Wall2" );
			walls.add( wall.getWall() );
			walls.get( 2 ).setTexture( "Room3Wall2" );
			bodies.add( wall.getBody() );
			//Fourth wall
			wall = new BBWall( new SimpleVector( -95, -15, 0 ), 190, 50, "Room3Wall3" );
			walls.add( wall.getWall() );
			walls.get( 3 ).setTexture( "Room3Wall3" );
			bodies.add( wall.getBody() );
			 
			floor = new BBFloor( new SimpleVector( 190, 10, 190 ), 0 );
			floor.setTexture( "Room3Floor" );
			bodies.add( floor.getBody() );
			addObject( floor.getFloor() );
			
			break;
			
		case BEACH:
			break;
		default:
			Log.e("BBRoom", "Level " + room + " does not exist!");
			break;
		}
		
	}
	
	/**
	 * Loads the .obj and .mtl files of the objects to be in the room; then scales, rotates, and places them
	 * as the should be placed in the room.
	 * 
	 * @param level - the level to be set up
	 */
	public void setObjects( Level level ) {
		/*
		 * TODO: add color to WordObjects when camera is aimed at them
		Object3D box = Primitives.getBox( 5.0f,  1.0f );
		box.setOrigin( new SimpleVector( 0, 0, 0 ) );
		cameraBoxId = addObject( box );
		*/
		roomObjectWords = new ArrayList<String>();
		switch ( level ) {
		case TUTORIAL:
			
			try {
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/desk.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/desk.mtl" ), 1.5f ) ),
						new SimpleVector( (float )Math.PI, -( float )Math.PI/2, 0 ), "Desk", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/chair.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/chair.mtl" ), 3.0f ) ),
						new SimpleVector( (float )Math.PI, -( float )Math.PI/2, 0 ), "Chair", Gender.FEMININE ) );
			} catch( Exception e ) {
				
			}
			
			addWordObject( -27, -6, 60, roomObjects.get( 0 ), "Desk" );
			addWordObject( 0, -6, 60, roomObjects.get( 0 ), "Desk" );
			addWordObject( 25, 2, 60, roomObjects.get( 1 ), "Chair" );
			
			break;
			
		case CLASSROOM:
			
			try {
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/desk.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/desk.mtl" ), 1.5f ) ),
						new SimpleVector( (float )Math.PI, -( float )Math.PI/2, 0 ), "Desk", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/chair.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/chair.mtl" ), 3.0f ) ),
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Chair", Gender.FEMININE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/chalkboard.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/chalkboard.mtl" ), 6.0f ) ), new SimpleVector( 0, ( float )Math.PI, ( float )Math.PI ), "Chalkboard", Gender.FEMININE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/backpack.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/backpack.mtl" ), 2.0f ) ), new SimpleVector( 0, 0.8f*( float )Math.PI/2, ( float )Math.PI ), "Backpack", Gender.FEMININE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/calendar.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/calendar.mtl" ), 1.0f ) ), new SimpleVector( 0, ( float )Math.PI/2.0f, ( float )Math.PI ), "Calendar", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/clock.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/clock.mtl" ), 1.0f ) ), new SimpleVector( 0, ( float )Math.PI/2, ( float )Math.PI ), "Clock", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/door.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/door.mtl" ), 4.5f ) ), new SimpleVector( 0, 0, 0 ), "Door", Gender.FEMININE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/book.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/book.mtl" ), 1.8f ) ), new SimpleVector( 0, 0, 0 ), "Book", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/paper.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/paper.mtl" ), 1.0f ) ), new SimpleVector( 0, ( float )Math.PI/2.0f, ( float )Math.PI ), "Paper", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/classroom/window.obj" ),
						BB.context.getResources().getAssets().open( "raw/classroom/window.mtl" ), 1.0f ) ), new SimpleVector( 0, ( float )Math.PI/2.0f, 0 ), "Window", Gender.FEMININE ) );
			} catch ( IOException e ) {
				e.printStackTrace();
			}
			
			//Desks 0
			addWordObject( -50, -1, 30, roomObjects.get( 0 ), "Desk" );
			addWordObject( -50, -1, -15, roomObjects.get( 0 ), "Desk" );
			addWordObject( 50, -1, 30, roomObjects.get( 0 ), "Desk" );
			addWordObject( 50, -1, -15, roomObjects.get( 0 ), "Desk" );
			//Chairs 1
			addWordObject( -50, 7, 10, roomObjects.get( 1 ), "Chair" );
			addWordObject( -50, 7, -35, roomObjects.get( 1 ), "Chair" );
			addWordObject( 50, 7, 10, roomObjects.get( 1 ), "Chair" );
			addWordObject( 50, 7, -35, roomObjects.get( 1 ), "Chair" );
			//Chalk board 2
			addWordObject( 0, -10, 78, roomObjects.get( 2 ), "Chalkboard" );
			//BackPacks 3
			addWordObject( -30, 20, 30, roomObjects.get( 3 ), "Backpack" );
			addWordObject( 50, 9, 25, roomObjects.get( 3 ), "Backpack" );
			addWordObject( 32, 20, -30, roomObjects.get( 3 ), "Backpack" );
			//Calendar 4
			addWordObject( 73, -10, 35, roomObjects.get( 4 ), "Calendar" );
			//Clock 5
			addWordObject( 74, -25, -60, roomObjects.get( 5 ), "Clock" );
			//Door 6
			addWordObject( -45, -19, -88, roomObjects.get( 6 ), "Door" );
			//Book 7
			addWordObject( 36, -3, -74, roomObjects.get( 7 ), "Book", new SimpleVector( 0, -( float )Math.PI/2, 0 ) );
			addWordObject( 19, 9, -74, roomObjects.get( 7 ), "Book", new SimpleVector( 0, ( float )Math.PI/2, 0 ) );
			addWordObject( 32, 20, -74, roomObjects.get( 7 ), "Book", new SimpleVector( 0, ( float )Math.PI/2, 0 ) );
			//Paper 8
			addWordObject( 42, 15.5f, -20, roomObjects.get( 8 ), "Paper", new SimpleVector( 0, ( float )Math.PI*0.1, 0 ) );
			addWordObject( -40, 15.25f, 25, roomObjects.get( 8 ), "Paper", new SimpleVector( 0, ( float )Math.PI*0.075, 0 ) );
			addWordObject( -40, 15.5f, 25, roomObjects.get( 8 ), "Paper", new SimpleVector( 0, ( float )Math.PI*0.2, 0 ) );
			addWordObject( 42, 15.25f, -20, roomObjects.get( 8 ), "Paper", new SimpleVector( 0, ( float )Math.PI*0.15, 0 ) );
			//Window 9
			addWordObject( -77, -13, 0, roomObjects.get( 9 ), "Window" );
			addWordObject( -77, -13, 47.5f, roomObjects.get( 9 ), "Window" );
			addWordObject( -77, -13, -47.5f, roomObjects.get( 9 ), "Window" );
			
			break;
			
		case DINER:
			
			try {
				long startTime = System.currentTimeMillis();
				
				//Bill = 0
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/bill.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/bill.mtl" ), 1.0f ) ), new SimpleVector( -( float )Math.PI/2, 0, 0 ), "Bill", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'bill' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Bread = 1
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/bread.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/bread.mtl" ), 0.75f ) ), new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Bread", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'bread' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Cake = 2
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/cake.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/cake.mtl" ), 1.0f ) ), new SimpleVector( (float )Math.PI, 0, 0 ), "Cake", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'cake' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Cup = 3
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/cup.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/cup.mtl" ), 0.5f ) ), new SimpleVector( 0, 0, 0 ), "Cup", Gender.FEMININE ) );
				/*Fork = 4
				 * Deleted currently as file was HUGE
				roomObjects.add( new WordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/fork.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/fork.mtl" ), 1.0f ) ), new SimpleVector( 0, 0, 0 ), "Table", WordObject.MASCULINE ) );
						*/
				//Knife = 5
				Log.d( "Room", "Loading object 'cup' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/knife.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/knife.mtl" ), 0.3f ) ), new SimpleVector( (float )Math.PI, 0, 0 ), "Knife", Gender.MASCULINE ) );
				//Money = 6
				Log.d( "Room", "Loading object 'knife' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/money.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/money.mtl" ), 1.0f ) ), new SimpleVector( -( float )Math.PI/2, 0, 0 ), "Money", Gender.MASCULINE ) );
				//Plate = 7
				Log.d( "Room", "Loading object 'money' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/plate.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/plate.mtl" ), 1.0f ) ), new SimpleVector( (float )Math.PI, 0, 0 ), "Plate", Gender.MASCULINE ) );
				//Spoon = 8
				Log.d( "Room", "Loading object 'plate' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/spoon.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/spoon.mtl" ), 1.0f ) ), new SimpleVector( 0, -( float )Math.PI/2, ( float )Math.PI ), "Spoon", Gender.FEMININE ) );
				//Table = 9
				Log.d( "Room", "Loading object 'spoon' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/diner/table.obj" ),
						BB.context.getResources().getAssets().open( "raw/diner/table.mtl" ), 1.5f ) ), new SimpleVector( (float )Math.PI, 0, 0 ), "Table", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'table' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
			} catch ( IOException e ) {
				e.printStackTrace();
			} 
			
			//Bill 0
			addWordObject( 30, 3.5f, 37, roomObjects.get( 0 ), "Bill" );
			addWordObject( -30, 3, 0, roomObjects.get( 0 ), "Bill" );
			addWordObject( 35, 3.5f, -37, roomObjects.get( 0 ), "Bill" );
			//Bread 1
			addWordObject( -30, 2, 40, roomObjects.get( 1 ), "Bread" );
			//Cake 2
			addWordObject( 25, 4, -40, roomObjects.get( 2 ), "Cake" );
			//Cup 3
			addWordObject( 32, 4, 40, roomObjects.get( 3 ), "Cup" );
			addWordObject( -34, 4, -37, roomObjects.get( 3 ), "Cup", new SimpleVector( 0, ( float )Math.PI/2, 0 ) );
			//Fork 4
			//addWordObject( 60, -5, 20, roomObjects.get( 4 ), "Fork" );
			//Knife 5
			addWordObject( -30, 5, 40, roomObjects.get( 4 ), "Knife" );
			addWordObject( -30, 5, 0, roomObjects.get( 4 ), "Knife" );
			addWordObject( -30, 5, -40, roomObjects.get( 4 ), "Knife" );
			addWordObject( 30, 5, 40, roomObjects.get( 4 ), "Knife" );
			addWordObject( 30, 5, 0, roomObjects.get( 4 ), "Knife" );
			addWordObject( 30, 5, -40, roomObjects.get( 4 ), "Knife" );
			//Money 6
			addWordObject( 25, 1, 0, roomObjects.get( 5 ), "Money" );
			addWordObject( 10, -10, -65, roomObjects.get( 5 ), "Money", new SimpleVector( (float )Math.PI/2, 0, -( float )Math.PI/2 ) );
			//Plate 7
			addWordObject( 26, 5, 40, roomObjects.get( 6 ), "Plate" );
			addWordObject( -26, 5, 0, roomObjects.get( 6 ), "Plate" );
			addWordObject( -26, 5, -40, roomObjects.get( 6 ), "Plate" );
			//Spoon 8
			addWordObject( -23, 5, 40, roomObjects.get( 7 ), "Spoon" );
			addWordObject( -23, 5, 0, roomObjects.get( 7 ), "Spoon" );
			addWordObject( -23, 5, -40, roomObjects.get( 7 ), "Spoon" );
			addWordObject( 23, 5, 40, roomObjects.get( 7 ), "Spoon" );
			addWordObject( 23, 5, 0, roomObjects.get( 7 ), "Spoon" );
			//Table 9
			addWordObject( -30, -4, 40, roomObjects.get( 8 ), "Table" );
			addWordObject( -30, -4, 0, roomObjects.get( 8 ), "Table" );
			addWordObject( -30, -4, -40, roomObjects.get( 8 ), "Table" );
			addWordObject( 30, -4, 40, roomObjects.get( 8 ), "Table" );
			addWordObject( 30, -4, 0, roomObjects.get( 8 ), "Table" );
			addWordObject( 30, -4, -40, roomObjects.get( 8 ), "Table" );

			break;
			
		case STREET: 
			
			try {
				long startTime = System.currentTimeMillis();
				
				// Address = 0 ( la direcci�n )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/address.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/address.mtl" ), 20.0f ) ), 
						new SimpleVector( 0, - (float) Math.PI / 2, 0 ), "Address", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'address' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				// Bicycle = 1 ( la bicicleta )
				//roomObjects.add( new BBWordObject( Object3D.mergeAll(
				//		Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/bicycle.obj" ),
				//		BB.context.getResources().getAssets().open( "raw/street/bicycle.mtl" ), 0.35f ) ), 
				//		new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Bicycle", Gender.FEMININE ) );
				// Bus = 2 ( el autob�s )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/bus.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/bus.mtl" ), 1.3f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Bus", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'bus' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				// Car = 3 ( el coche )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/car.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/car.mtl" ), 1f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Car", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'car' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				// Map = 4 ( el mapa )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/map.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/map.mtl" ), 1.0f ) ), 
						new SimpleVector( (float )Math.PI/2, 0, 0 ), "Map", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'map' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				// Police = 3 ( la polic�a )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/police.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/police.mtl" ), 3.5f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Police", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'car' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				// Sign = 6 ( la se�al )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/street_sign.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/street_sign.mtl" ), 0.5f ) ), 
						new SimpleVector( (float )Math.PI, 0, 0 ), "StreetSign", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'sign' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				// Taxi = 7 ( la taxi )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/taxi.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/taxi.mtl" ), 1f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Taxi", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'taxi' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				// Traffic light = 8 ( el sem�foro )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/traffic_light.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/traffic_light.mtl" ), 0.35f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Traffic_Light", Gender.MASCULINE ) );
				// Trash = 9 ( la basura )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/street/trashcan.obj" ),
						BB.context.getResources().getAssets().open( "raw/street/trashcan.mtl" ), 3.5f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Trash", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'car' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();

			} catch ( IOException e ) {
				e.printStackTrace();
			} 
			
			//Address 0
			addWordObject( 50, 0f, 0, roomObjects.get( 0 ), "Address" );
			//Bus 2
			addWordObject( -54, 8f, 13, roomObjects.get( 1 ), "Bus", new SimpleVector( 0, ( float )Math.PI, 0 ) );
			//Car 3
			addWordObject( -30, 8f, -23, roomObjects.get( 2 ), "Car", new SimpleVector( 0, 0, 0 ) );
			//Map 4
			addWordObject( 4, -1f, -20, roomObjects.get( 3 ), "Map" );
			//Street sign 6
			addWordObject( -25, -1f, -19, roomObjects.get( 5 ), "Sign" );
			//Taxi 7
			addWordObject( 20, 7f, 10, roomObjects.get( 6 ), "Taxi", new SimpleVector( 0, -( float ) Math.PI / 2, 0 ) );
			//Traffic Light 8
			addWordObject( -70, -3f, -20, roomObjects.get( 7 ), "Traffic_Light", new SimpleVector( 0, ( float )Math.PI, 0 ) );
			addWordObject( -23, -3f, 22, roomObjects.get( 7 ), "Traffic_Light" );
			break;
			
		case BEACH:
			break;
		default:
			Log.e("BBRoom", "Level " + level + " does not exist!");
			break;
		}
		Loader.clearCache();
		roomObjects.clear();
	}
	
	/**
	 * Calls the other addWordObject function with a null value for 'rotateBy'
	 * 
	 * @param x - the x position of the object in JPCT-AE world coordinates (right is positive)
	 * @param y - the y position of the object in JPCT-AE world coordinates (down is positive)
	 * @param z - the z position of the object in JPCT-AE world coordinates (forward is positive)
	 * @param wordObject - the BBWordObject to be added
	 * @param name - the English name of the object
	 */
	private void addWordObject( float x, float y, float z, BBWordObject wordObject, String name ) {
		addWordObject( x, y, z, wordObject, name, new SimpleVector( 0, 0, 0 ) );
	}
	
	/**
	 * Adds a BBWord Object to the room and sets its additional color.
	 * 
	 * @param x - the x position of the object in JPCT-AE world coordinates (right is positive)
	 * @param y - the y position of the object in JPCT-AE world coordinates (down is positive)
	 * @param z - the z position of the object in JPCT-AE world coordinates (forward is positive)
	 * @param wordObject - the BBWordObject to be added
	 * @param name - the English name of the object
	 * @param rotateBy - a SimpleVector that rotates the object by some amount of radians
	 */
	private void addWordObject( float x, float y, float z, BBWordObject wordObject, String name, SimpleVector rotateBy ) {
		//Creates a new WordObject from the generic roomObject
		//and adds it to the Room and wordObjects ArrayList
		Log.i( "olsontl", "Adding object " + name );
		BBWordObject object = new BBWordObject( wordObject );
		object.setCenter( SimpleVector.ORIGIN );
		object.setOrigin( new SimpleVector( x, y, z ) );
		object.setName( name );
		object.rotateBy( rotateBy );
		object.addInitialRotation( rotateBy );
		object.setTransparencyMode( Object3D.TRANSPARENCY_MODE_ADD );
		object.setCollisionMode( Object3D.COLLISION_CHECK_OTHERS );
		object.setCollisionOptimization( Object3D.COLLISION_DETECTION_OPTIMIZED );
		object.calcTangentVectors();
		object.setSpecularLighting( true );
		if ( tm.containsTexture( name ) && ( name != BBTranslator.translateToLanguage( name, BBTranslator.Language.SPANISH ) ) ) {
			object.setTexture( name );
		}
		if ( !roomObjectWords.contains( name ) ) {
			roomObjectWords.add( name );
		}
		object.build();
		if ( object.article == Gender.FEMININE )
			object.setAdditionalColor( 200, 0, 0 );
		else
			object.setAdditionalColor( 0, 0, 200 );
		//object.setRotationPivot( pivot );
		addObject( object );
		Log.i( "olsontl", "Object " + name + " added" );
	}
	
	
	/**
	 * Adds a bubble to the room. Similar to addWordObject, but bubbles are a special object type, and have
	 * physics applied to them by this function.
	 * 
	 * @param position - the starting location of the bubble
	 * @return - the physics RigidBody of the bubble
	 */
	public RigidBody addBubble( SimpleVector position ) {
		//Creates a new bubble Object and adds it to the room
		SphereShape shape = new SphereShape( 5.0f );
		float mass = 12;
		Vector3f localInertia = new Vector3f( 0, 0, 0 );
		shape.calculateLocalInertia( mass, localInertia );
		BBBubble bubble = new BBBubble( position, currentGender, System.currentTimeMillis() );
		
		addObject( bubble );
		JPCTBulletMotionState ms = new JPCTBulletMotionState( bubble );

		//Creates a RigidBody and adds it to the DynamicWorld
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo( mass, ms, shape, localInertia );
		RigidBody body = new RigidBody( rbInfo );
		body.setRestitution( 0f );
		body.setFriction( 0f );
		body.setGravity( new Vector3f( 0, 0, 0 ) );
		bubbles.add( body );
		bubble.setLocalBodyIndex( bubbles.size()-1 );
		bubbleObjects.add( bubble );

		return body;
	}
	
	/**
	 * Sets the currentGender variable to be applied to a new bubble
	 * 
	 * @param state - the Gender that should be set
	 */
	public void setGender( Gender article ) {
		currentGender = article;
	}
	
	/**
	 * Get a BBBubble associated with the given index
	 * 
	 * @param index - the location of the bubble in bubbleObjects
	 * @return - the BBBubble associated
	 */
	public BBBubble getBubble( int index ) {
		return bubbleObjects.get( index );
	}
	
	/**
	 * Returns the ArrayList of bubble objects in the room
	 * 
	 * @return - the ArrayList of bubble objects in the room
	 */
	public ArrayList<BBBubble> getBubbleObjects() {
		return bubbleObjects;
	}
	
	/**
	 * Gets a string associated with the current gender of the on screen bubble
	 * 
	 * @return - the article associated with the current gender
	 */
	public String getBubbleArticle() {
		switch ( currentGender ) {
			case MASCULINE:
				return "El";
			case FEMININE:
				return "La";
		}
		return "";
	}
	
	/**
	 * @return - the size of the bubbles ArrayList
	 */
	public int getBubbleCounter() {
		return bubbles.size();
	}
	
	/**
	 * @return - the height of the room
	 */
	public float getHeight() {
		return height;
	}
	
	/**
	 * @return - the width of the room (x direction)
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * @return - the length of the room (z direction)
	 */
	public float getLength() {
		return length;
	}
	
	/**
	 * A new function for the JPCT-AE function addObject that just takes in a BBWordObject parameter
	 * and adds it to the wordObjects ArrayList
	 * 
	 * @param wordObject - the BBWordObject to be added
	 * @return - the world id of the object added to the room
	 */
	public int addObject( BBWordObject wordObject ) {
		//Extra function for adding an object to the Room class that also adds
		//information for the WordObject in the wordObjects ArrayList
		int objectId = super.addObject( (Object3D ) wordObject );
		wordObject.setObjectId( objectId );
		wordObjects.add( wordObject );
		return objectId;
	}
	
	/**
	 * A new function for the JPCT-AE function addObject that just takes in a BBBubble parameter
	 * 
	 * @param bubble - the bubble to be added to the room
	 * @return - the world id of the bubble added to the room
	 */
	public int addObject( BBBubble bubble ) {
		//Extra function for adding a bubble to the Room
		int objectId = super.addObject( (Object3D ) bubble );
		Log.i( "olsontl", "Bubble id when created: " + objectId );
		bubble.setObjectId( objectId );
		return objectId;
	}
	
	/**
	 * Gets the BBWordObject associated with the given id
	 * 
	 * @param id - the id of the BBWordObject to return
	 * @return - the BBWordObject associated with the given id
	 */
	public BBWordObject getWordObject( int id ) {
		for ( BBWordObject wordObject : wordObjects ) {
			if ( wordObject.getObjectId() == id )
				return wordObject;
		}
		return null;
	}
	
	/**
	 * @return - the size of the wordObjects ArrayList
	 */
	public int getNumWordObjects() {
		return wordObjects.size();
	}
	
	/**
	 * @return - the wordObjects ArrayList
	 */
	public ArrayList<BBWordObject> getWordObjects() {
		return wordObjects;
	}
	
	/**
	 * Determines if the object associated with the given world id is a bubble
	 * 
	 * @param id - the world id of the requested object
	 * @return - whether the object is a BBBubble type or not
	 */
	public boolean isBubbleType( int id ) {
		for ( BBBubble bubble : bubbleObjects ) {
			if ( bubble.getObjectId() == id ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return - the last element in the bubbleObjects ArrayList
	 */
	public BBBubble getLastBubble() {
		return bubbleObjects.get( bubbleObjects.size()-1 );
	}
	
	/**
	 * Converts the JPCT-AE SimpleVector to the jBullet Vector3f as the graphics and physics engine use different
	 * vector types.
	 * 
	 * @param vector - JPCT-AE SimpleVector to be converted
	 * @return - jBullet Vector3f created by given vector
	 */
	public Vector3f toVector3f( SimpleVector vector ) {
		//Converts a SimpleVector to a Vector3f
		return new Vector3f( vector.x, vector.y, vector.z );
	}
	
	/**
	 * Converts the jBullet Vector3f to the JPCT-AE SimpleVector as the graphics and physics engine use different
	 * vector types.
	 * 
	 * @param vector - jBullet Vector3f to be converted
	 * @return - JPCT-AE SimpleVector created by given vector
	 */
	public SimpleVector toSimpleVector( Vector3f vector ) {
		//Converts a Vector3f to a SimpleVector
		return new SimpleVector( vector.x, vector.y, vector.z );
	}
	
	/**
	 * @return - the roomObjectWords ArrayList, a list of the names of objects in the room; one of each.
	 */
	public ArrayList<String> getRoomObjectWords() {
		return roomObjectWords;
	}
	
	/* ( non-Javadoc )
	 * @see com.threed.jpct.World#dispose()
	 */
	@Override
	public void dispose() {
		roomObjectWords.clear();
		wordObjects.clear();
		bubbleObjects.clear();
		roomObjects.clear();
		bubbles.clear();
		bodies.clear();
		walls.clear();
		
		super.dispose();
	}

}
