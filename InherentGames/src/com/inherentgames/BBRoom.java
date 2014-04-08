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


public class BBRoom extends World {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9088044018714661773L;
	
	private BBTextureManager tm;
	protected SkyBox skybox;
	
	private ArrayList<Object3D> walls = new ArrayList<Object3D>();
	protected BBWall wall;
	protected BBFloor floor;
	protected BBFloor ceiling;
	private ArrayList<RigidBody> bodies = new ArrayList<RigidBody>();
	private ArrayList<RigidBody> bubbles = new ArrayList<RigidBody>();
	private ArrayList<BBBubble> bubbleObjects;
	private ArrayList<BBWordObject> wordObjects;
	private ArrayList<BBWordObject> roomObjects;
	private ArrayList<String> roomObjectWords;
	
	private float height, width, length;
	
	protected Gender currentGender = Gender.MASCULINE;
	
	//private GLSLShader shader;
	/*
	 * TODO: add color to WordObjects when camera is aimed at them
	private int cameraBoxId = 0;
	*/
	
	/**
	 * @param roomId
	 * @param context
	 * @param tm
	 */
	public BBRoom( int roomId ) {
		
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
		
		/*try {
			shader = new GLSLShader( Loader.loadTextFile( BB.context.getAssets().open( "toon.vs" ) ), Loader.loadTextFile( BB.context.getAssets().open( "toon.fs" ) ) );
		} catch ( IOException e1 ) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
	}
	
	/**
	 * @param roomNum
	 * @return
	 */
	public SimpleVector getLightLocation( int roomNum ) {
		//Get light location vector based on Room Id
		switch( roomNum ) {
		case 0:
			return new SimpleVector( 0, -20, 0 );
		}
		//default light location
		return new SimpleVector( 0, -20, 0 );
	}
	
	/**
	 * @param id
	 * @return
	 */
	public RigidBody getBody( int id ) {
		return bodies.get( id );
	}
	
	/**
	 * @return
	 */
	public int getNumBubbles() {
		return bubbles.size();
	}
	
	/**
	 * @return
	 */
	public int getNumBodies() {
		return bodies.size();
	}
	
	/**
	 * @param bubble
	 */
	public void removeBubble( BBBubble bubble ) {
		bubbleObjects.remove( bubble );
		bubbles.remove( bubble.getLocalBodyIndex() );
		removeObject( bubble );
	}
	
	/**
	 * @param room
	 */
	public void setSurfaces( int room ) {
		//If room id is not defined in getWallNumByRoom, returns an error
		if ( getWallNumByRoomId( room ) == -1 ) {
			Log.i( "Room", "Invalid room number" );
		}
		BBWall wall;
		//set walls by room number
		switch( room ) {

		case 0:
			
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
			
		case 1:		

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
			
		case 2:
			
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
			
		case 3:
			
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
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.street ) ), 512, 512 );
				tm.addTexture( "Street", new Texture( bitmap, true ) );
				bitmap.recycle();
				
			} catch( Exception e ) {
				//TODO: log exception
			}
			
			skybox = new SkyBox( "SkyboxLeft", "SkyboxFront", "SkyboxRight", "SkyboxBack", "SkyboxTop", "SkyboxBottom", 200f );
			floor = new BBFloor( new SimpleVector( 200, 10, 200 ), 0 );
			floor.setTexture( "Street" );
			bodies.add( floor.getBody() );
			addObject( floor.getFloor() );
			
			break;
		}
		
	}
	
	/**
	 * @param roomId
	 */
	public void setObjects( int roomId ) {
		/*
		 * TODO: add color to WordObjects when camera is aimed at them
		Object3D box = Primitives.getBox( 5.0f,  1.0f );
		box.setOrigin( new SimpleVector( 0, 0, 0 ) );
		cameraBoxId = addObject( box );
		*/
		roomObjectWords = new ArrayList<String>();
		switch ( roomId ) {
		case 0:
			
			try {
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/desk.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/desk.mtl" ), 1.5f ) ),
						new SimpleVector( (float )Math.PI, -( float )Math.PI/2, 0 ), "Desk", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/chair.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/chair.mtl" ), 3.0f ) ),
						new SimpleVector( (float )Math.PI, -( float )Math.PI/2, 0 ), "Chair", Gender.FEMININE ) );
			} catch( Exception e ) {
				
			}
			
			addWordObject( -25, -6, 60, roomObjects.get( 0 ), "Desk" );
			addWordObject( 25, 2, 60, roomObjects.get( 1 ), "Chair" );
			
			break;
			
		case 1:
			
			try {
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/desk.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/desk.mtl" ), 1.5f ) ),
						new SimpleVector( (float )Math.PI, -( float )Math.PI/2, 0 ), "Desk", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/chair.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/chair.mtl" ), 3.0f ) ),
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Chair", Gender.FEMININE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/chalkboard.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/chalkboard.mtl" ), 6.0f ) ), new SimpleVector( 0, ( float )Math.PI, ( float )Math.PI ), "Chalkboard", Gender.FEMININE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/backpack.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/backpack.mtl" ), 2.0f ) ), new SimpleVector( 0, 0.8f*( float )Math.PI/2, ( float )Math.PI ), "Backpack", Gender.FEMININE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/calendar.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/calendar.mtl" ), 1.0f ) ), new SimpleVector( 0, ( float )Math.PI/2.0f, ( float )Math.PI ), "Calendar", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/clock.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/clock.mtl" ), 1.0f ) ), new SimpleVector( 0, ( float )Math.PI/2, ( float )Math.PI ), "Clock", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/door.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/door.mtl" ), 4.5f ) ), new SimpleVector( 0, 0, 0 ), "Door", Gender.FEMININE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/book.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/book.mtl" ), 1.8f ) ), new SimpleVector( 0, 0, 0 ), "Book", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/paper.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/paper.mtl" ), 1.0f ) ), new SimpleVector( 0, ( float )Math.PI/2.0f, ( float )Math.PI ), "Paper", Gender.MASCULINE ) );
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room0/window.obj" ),
						BB.context.getResources().getAssets().open( "raw/room0/window.mtl" ), 1.0f ) ), new SimpleVector( 0, ( float )Math.PI/2.0f, 0 ), "Window", Gender.FEMININE ) );
			} catch ( IOException e ) {
				// TODO Auto-generated catch block
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
			
		case 2:
			
			try {
				long startTime = System.currentTimeMillis();
				
				//Bill = 0
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/bill.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/bill.mtl" ), 1.0f ) ), new SimpleVector( -( float )Math.PI/2, 0, 0 ), "Bill", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'bill' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Bread = 1
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/bread.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/bread.mtl" ), 0.75f ) ), new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Bread", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'bread' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Cake = 2
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/cake.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/cake.mtl" ), 1.0f ) ), new SimpleVector( (float )Math.PI, 0, 0 ), "Cake", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'cake' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Cup = 3
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/cup.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/cup.mtl" ), 0.5f ) ), new SimpleVector( 0, 0, 0 ), "Cup", Gender.FEMININE ) );
				/*Fork = 4
				 * Deleted currently as file was HUGE
				roomObjects.add( new WordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/fork.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/fork.mtl" ), 1.0f ) ), new SimpleVector( 0, 0, 0 ), "Table", WordObject.MASCULINE ) );
						*/
				//Knife = 5
				Log.d( "Room", "Loading object 'cup' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/knife.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/knife.mtl" ), 0.3f ) ), new SimpleVector( (float )Math.PI, 0, 0 ), "Knife", Gender.MASCULINE ) );
				//Money = 6
				Log.d( "Room", "Loading object 'knife' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/money.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/money.mtl" ), 1.0f ) ), new SimpleVector( -( float )Math.PI/2, 0, 0 ), "Money", Gender.MASCULINE ) );
				//Plate = 7
				Log.d( "Room", "Loading object 'money' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/plate.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/plate.mtl" ), 1.0f ) ), new SimpleVector( (float )Math.PI, 0, 0 ), "Plate", Gender.MASCULINE ) );
				//Spoon = 8
				Log.d( "Room", "Loading object 'plate' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/spoon.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/spoon.mtl" ), 1.0f ) ), new SimpleVector( 0, -( float )Math.PI/2, ( float )Math.PI ), "Spoon", Gender.FEMININE ) );
				//Table = 9
				Log.d( "Room", "Loading object 'spoon' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				roomObjects.add( new BBWordObject( Object3D.mergeAll( Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room1/table.obj" ),
						BB.context.getResources().getAssets().open( "raw/room1/table.mtl" ), 1.5f ) ), new SimpleVector( (float )Math.PI, 0, 0 ), "Table", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'table' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
			} catch ( IOException e ) {
				// TODO Auto-generated catch block
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
			
		case 3: 
			
			try {
				long startTime = System.currentTimeMillis();
				
				//Address = 0 ( la dirección )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room3/address.obj" ),
						BB.context.getResources().getAssets().open( "raw/room3/address.mtl" ), 20.0f ) ), 
						new SimpleVector( 0, 0, 0 ), "Address", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'address' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Bus = 1 ( el autobús )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room3/bus.obj" ),
						BB.context.getResources().getAssets().open( "raw/room3/bus.mtl" ), 1.3f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Bus", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'bus' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Car = 2 ( el coche )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room3/car.obj" ),
						BB.context.getResources().getAssets().open( "raw/room3/car.mtl" ), 3.5f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Car", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'car' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//Map = 3 ( el mapa )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room3/map.obj" ),
						BB.context.getResources().getAssets().open( "raw/room3/map.mtl" ), 1.0f ) ), 
						new SimpleVector( (float )Math.PI/2, 0, 0 ), "Map", Gender.MASCULINE ) );
				Log.d( "Room", "Loading object 'map' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//sign = 4 ( la señal )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room3/street_sign.obj" ),
						BB.context.getResources().getAssets().open( "raw/room3/street_sign.mtl" ), 0.5f ) ), 
						new SimpleVector( (float )Math.PI, 0, 0 ), "StreetSign", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'sign' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//taxi = 5 ( la taxi )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room3/taxi.obj" ),
						BB.context.getResources().getAssets().open( "raw/room3/taxi.mtl" ), 2.5f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Taxi", Gender.FEMININE ) );
				Log.d( "Room", "Loading object 'taxi' took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
				startTime = System.currentTimeMillis();
				//traffic light = 6 ( el semáforo )
				roomObjects.add( new BBWordObject( Object3D.mergeAll(
						Loader.loadOBJ( BB.context.getResources().getAssets().open( "raw/room3/traffic_light.obj" ),
						BB.context.getResources().getAssets().open( "raw/room3/traffic_light.mtl" ), 0.35f ) ), 
						new SimpleVector( (float )Math.PI, ( float )Math.PI/2, 0 ), "Traffic_Light", Gender.MASCULINE ) );

			} catch ( IOException e ) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			//Address 0
			addWordObject( 20, 0f, 10, roomObjects.get( 0 ), "Address" );
			//Bus 1
			addWordObject( -54, 8f, 13, roomObjects.get( 1 ), "Bus", new SimpleVector( 0, ( float )Math.PI, 0 ) );
			//Car 2
			addWordObject( -30, 8f, -23, roomObjects.get( 2 ), "Car", new SimpleVector( 0, ( float )Math.PI/2, 0 ) );
			//Map 3
			addWordObject( 4, -1f, -20, roomObjects.get( 3 ), "Map" );
			//Street sign 4
			addWordObject( -25, -1f, -19, roomObjects.get( 4 ), "Sign" );
			//Taxi 5
			addWordObject( 20, 7f, 10, roomObjects.get( 5 ), "Taxi", new SimpleVector( 0, ( float )Math.PI, 0 ) );
			//Traffic Light 6
			addWordObject( -70, -3f, -20, roomObjects.get( 6 ), "Traffic_Light", new SimpleVector( 0, ( float )Math.PI, 0 ) );
			addWordObject( -23, -3f, 22, roomObjects.get( 6 ), "Traffic_Light" );
			break;
		}
		Loader.clearCache();
		roomObjects.clear();
	}
	
	/*
	 * TODO: add color to WordObjects when camera is aimed at them
	public Object3D getCameraBox() {
		return getObject( cameraBoxId );
	}
	*/
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param wordObject
	 * @param name
	 */
	private void addWordObject( float x, float y, float z, BBWordObject wordObject, String name ) {
		addWordObject( x, y, z, wordObject, name, new SimpleVector( 0, 0, 0 ) );
	}
	
	/**
	 * @param x
	 * @param y
	 * @param z
	 * @param wordObject
	 * @param name
	 * @param rotateBy
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
		object.setCollisionMode( Object3D.COLLISION_CHECK_OTHERS );
		object.setCollisionOptimization( Object3D.COLLISION_DETECTION_OPTIMIZED );
		object.calcTangentVectors();
		//object.setShader( shader );
		object.setSpecularLighting( true );
		if ( tm.containsTexture( name ) && ( name != BBTranslator.translateToLanguage( name, BBTranslator.Language.SPANISH ) ) ) {
			object.setTexture( name );
		}
		if ( !roomObjectWords.contains( name ) ) {
			roomObjectWords.add( name );
		}
		object.build();
		if ( object.getArticle() == Gender.FEMININE )
			object.setAdditionalColor( 200, 0, 0 );
		else
			object.setAdditionalColor( 0, 0, 200 );
		//object.setRotationPivot( pivot );
		addObject( object );
		Log.i( "olsontl", "Object " + name + " added" );
	}
	
	
	/**
	 * @param position
	 * @return
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
	 * @param state
	 */
	public void setGender( Gender article ) {
		currentGender = article;
	}
	
	/**
	 * @param index
	 * @return
	 */
	public BBBubble getBubble( int index ) {
		return bubbleObjects.get( index );
	}
	
	/**
	 * @return
	 */
	public ArrayList<BBBubble> getBubbleObjects() {
		return bubbleObjects;
	}
	
	/**
	 * @return
	 */
	public String getBubbleArticle() {
		switch ( currentGender ) {
			case MASCULINE:
				return "El";
			case FEMININE:
				return "La";
		}
		return "Nada";
	}
	
	/**
	 * @return
	 */
	public int getBubbleCounter() {
		return bubbles.size();
	}
	
	/**
	 * @return
	 */
	public float getHeight() {
		return height;
	}
	
	/**
	 * @return
	 */
	public float getWidth() {
		return width;
	}
	
	/**
	 * @return
	 */
	public float getLength() {
		return length;
	}
	
	/**
	 * @param wordObject
	 * @return
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
	 * @param bubble
	 * @return
	 */
	public int addObject( BBBubble bubble ) {
		//Extra function for adding a bubble to the Room
		int objectId = super.addObject( (Object3D ) bubble );
		Log.i( "olsontl", "Bubble id when created: " + objectId );
		bubble.setObjectId( objectId );
		return objectId;
	}
	
	/**
	 * @param id
	 * @return
	 */
	public BBWordObject getWordObject( int id ) {
		for ( BBWordObject wordObject : wordObjects ) {
			if ( wordObject.getObjectId() == id )
				return wordObject;
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public int getNumWordObjects() {
		return wordObjects.size();
	}
	
	/**
	 * @return
	 */
	public ArrayList<BBWordObject> getWordObjects() {
		return wordObjects;
	}
	
	/**
	 * @param id
	 * @return
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
	 * @return
	 */
	public BBBubble getLastBubble() {
		return bubbleObjects.get( bubbleObjects.size()-1 );
	}
	
	/**
	 * @param vector
	 * @return
	 */
	public Vector3f toVector3f( SimpleVector vector ) {
		//Converts a SimpleVector to a Vector3f
		return new Vector3f( vector.x, vector.y, vector.z );
	}
	
	/**
	 * @param vector
	 * @return
	 */
	public SimpleVector toSimpleVector( Vector3f vector ) {
		//Converts a Vector3f to a SimpleVector
		return new SimpleVector( vector.x, vector.y, vector.z );
	}
	
	/**
	 * @param room
	 * @return
	 */
	public int getWallNumByRoomId( int room ) {
		//Returns the number of walls defined per room ( Currently only
		//implementable with 4 )
		int num = -1;
		switch( room ) {
		default:
			num = 4;
		}
		
		return num;
	}
	
	/**
	 * @return
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
