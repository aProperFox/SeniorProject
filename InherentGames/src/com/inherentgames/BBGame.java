package com.inherentgames;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.vecmath.Vector3f;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import com.inherentgames.BBRoom.Level;
import com.inherentgames.BBTranslator.Language;
import com.inherentgames.BBWordObject.Gender;
import com.threed.jpct.Camera;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

// Handles game dynamics, and represents the overall state of the game
public class BBGame {
	// Singleton self reference
	private static BBGame instance = null;
	
	/* Library objects */
	
	// General
	protected Runnable loader;
	private Clock clock;
	protected Enumeration<Object3D> objects;
	private ArrayList<String> bubbleWords = new ArrayList<String>();
	private Handler handler;
	
	// OpenGL
	private BBTextureManager tm;
	protected BBRoom world;
	protected Camera cam;
	private Light sun1;
	private SimpleVector V;
	
	// Sound
	private SoundPool soundPool;
	private SparseIntArray soundPoolMap;
	
	// Physics
	protected DiscreteDynamicsWorld physicsWorld;
	private DefaultCollisionConfiguration collisionConfiguration;
	private CollisionDispatcher dispatcher;
	private Vector3f worldAabbMin;
	private Vector3f worldAabbMax;
	private AxisSweep3 overlappingPairCache;
	private SequentialImpulseConstraintSolver solver;
	
	/* Internal parameters */
	
	// Track level state
	protected static enum State { PLAYING, WON, LOST };
	protected State levelState;
	
	// Track the current room number
	protected Level level = Level.CLASSROOM;
	
	// Track whether the current level is the tutorial
	protected boolean isTutorial;
	
	// Tells whether the game is loading or running
	protected boolean loading = true;
	
	// Track loading progress
	protected int loadingProgress = 0;
	
	// Track whether the game is currently paused (in-game menu is showing)
	protected boolean isPaused = false;
	
	// Track whether the sprites/HUD elements have been loaded
	// Note: This is to avoid reloading the associated textures
	//       each time.
	private boolean spritesLoaded = false;
	
	// Track time deltas
	private long lastShot = System.currentTimeMillis();
	private long lastRotateTime = 0;
	
	// Track progress in level
	protected int captured = 0;
	
	// Track the amount of time left in the game
	protected long timeLeft;
	
	// Track time the game will end
	protected long endTime;
	
	// Track the movement of the arrow
	protected int arrowMod;
	protected int arrowDir;
	
	// Track the movement of the hand
	protected int handMod;
	protected int handDir;
	protected long handWaitEnd;
	protected boolean moveHand;
	protected int handTransparency;
	
	// Track dynamic screen objects
	BBDynamicScreenObject timeIcon;
	BBDynamicScreenObject answer;
	
	// Tracks horizontal and vertical swipe movement
	protected float horizontalSwipe = 0;
	protected float verticalSwipe = 0;
	
	// Track the states of the fire button, pause button, and bubbles
	protected String bubbleTex = "bubbleBlue";

	// Track the score
	protected int score = 0;
	
	// Track multiplier
	protected int streak;
	protected int multiplier;
	
	// Track the states and positions of fire button and pause button;
	protected BBButton fireButton;
	protected BBButton pauseButton;
	protected BBButton scoreButton;
	
	// Debugging option to track "current" object
	protected int _currentObjectId;
	
	
	// TODO: Move to BBTutorial for now, but ideally to a language file
	// Tutorial text
	protected String wattsonPhrases[][] = {
		{"Hi, Hopscotch! I'm your translator, Wattson.", "I'm here to show you how to travel through time.", "Tap me to begin!"},
		{ "Slide your finger to look around", "", ""},
		//log time, then aim at object and disable movement
		{"Nice Job! Capture the object in a bubble shot", "by your Chronopsyonic Quantum Destabilizer.", "(hold the fire button with one thumb, swipe with the other)"},
		{"Very Good!", "If the bubble color doesn't match the object", "color, you will lose precious time!"},
		//Have another blue object and shoot a red bubble at it
		{"You're on a time limit, so don't let", "the clock run out!", ""},
		{"Excellent!","Capture one of each object in a room to travel", "through time!" }
	};
	
	// Variables used to track current tutorial messages being displayed
	protected ArrayList<String> wattsonText = new ArrayList<String>();
	protected int wattsonTextIterator;
	protected boolean hasCompletedSteps = false;
	protected int wattsonPrivileges = 1;
	
	
	public BBGame() {
		// Initialize texture manager
		tm = BBTextureManager.getInstance();
		
		// Load game font
		Texture text = new Texture( BB.context.getResources().openRawResource( R.raw.font ) );
		text.setFiltering( false );
		tm.addTexture( "gui_font", text );
		
		// Load loading texture
		tm.addTexture( "loading_splash", new Texture( BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.loading_characters ) ), 512, 1024 ), true ) );
		tm.addTexture( "loading_backdrop", new Texture( BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.backdrop ) ), 1024, 1024 ), true ) );
		
		// Initialize resource loading runnable
		loader = new Runnable() {

			@Override
			public void run() {
				// Prepare tutorial (if applicable)
				prepareTutorial();
				loadingProgress = 0;
				// Load resources
				loadSprites();
				loadingProgress = 5;
				loadTextures();
				loadingProgress = 10;
				loadSounds();
				loadingProgress = 15;
				// Prepare environment
				setupScene();
				// Note: Opaque has transparency ~ 30
				loadingProgress = 100;
				// Done loading
				loading = false;
			}
			
		};
		
		

		// TODO: Find out what this is for
		handler = new Handler();
		
		// Initialize vector objects
		V = new SimpleVector( 0, 0, 1 );
		
		// Initialize the clock
		clock = new Clock();
		
	}
	
	// Allows for the singleton design pattern
	// I.e., we only want one instance of BBGame 
	//       to exist at any one time.
	public static BBGame getInstance() {
		if ( instance == null ) {
			instance = new BBGame();
		}
		return instance;
	}

	
	// Sets up the tutorial
	private void prepareTutorial() {
		// Is tutorial?
		if ( level == Level.TUTORIAL ) {
			isTutorial = true;
			wattsonPrivileges = 1;
			hasCompletedSteps = false;
			BB.isTimeLimitenabled = false;
			wattsonText.clear();
			wattsonText.add( wattsonPhrases[0][0] );
			wattsonText.add( wattsonPhrases[0][1] );
			wattsonText.add( wattsonPhrases[0][2] );
			wattsonTextIterator = 0;
			
			timeIcon = new BBDynamicScreenObject(BB.width / 2, BB.height / 2, (int) ( BB.width*0.966 ),
					( int )( BB.height*0.91 ), "TimeIcon", 3000, BB.buttonHeight / 2, BB.buttonHeight / 2,
					BB.height / 19 , BB.height / 19 );
			
		} else {
			BB.isTimeLimitenabled = ( BB.isDevMode ) ? false : true;
			isTutorial = false;
		}
	}
	
	/* Asset Loading */
	
	// TODO: All the actual texture loading needs to be offloaded to the texture manager
	// Loads all the sprite textures (AKA the textures for all the HUD elements)
	private void loadSprites() {
		// Reusable bitmap holder
		Bitmap bitmap;
		
		if ( !spritesLoaded ) {
			try {
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.bubblered ) ), 256, 256 );
				tm.addTexture( "bubbleRed", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.bubbleblue ) ), 256, 256 );
				tm.addTexture( "bubbleBlue", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.score_empty ) ), 512, 512 );
				tm.addTexture( "EmptyScore", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.firebutton ) ), 128, 128 );
				tm.addTexture( "FireButton", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.firebuttonpressed ) ), 128, 128 );
				tm.addTexture( "FireButtonPressed", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.crosshair ) ), 2048, 1024 );
				tm.addTexture( "Crosshair", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.pause_button ) ), 128, 128 );
				tm.addTexture( "PauseButton", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.pause_button_pressed ) ), 128, 128 );
				tm.addTexture( "PauseButtonPressed", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.time_bar ) ), 16, 512 );
				tm.addTexture( "TimeBar", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.time_icon ) ), 512, 512 );
				tm.addTexture( "TimeIcon", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.score_bars ) ), 128, 512 );
				tm.addTexture( "ScoreBars", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.info_bar ) ), 128, 128 );
				tm.addTexture( "InfoBar", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.fuel_bar_arrow ) ), 32, 32 );
				tm.addTexture( "ScoreArrow", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.arrow_up ) ), 128, 128 );
				tm.addTexture( "ArrowUp", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.arrow_down ) ), 128, 128 );
				tm.addTexture( "ArrowDown", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.hand ) ), 128, 128 );
				tm.addTexture( "Hand", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.filter ) ), 64, 64 );
				tm.addTexture( "Filter", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.defaulttexture ) ), 256, 256 );
				tm.addTexture( "Default", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				spritesLoaded = true;
			} catch( Exception e ) {
				e.printStackTrace();
			}
		}
	}
	
	// TODO: All the actual texture loading needs to be offloaded to the texture manager
	// Loads all the level-related textures
	private void loadTextures() {
		Bitmap bitmap;
		try {
			long startTime = System.currentTimeMillis();
			switch( level ) {
				case TUTORIAL:

					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.tutorialwall ) ), 512, 256 );
					tm.addTexture( "TutorialWall", new Texture( bitmap, true ) );
					bitmap.recycle();
					//Floor
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.tutorialfloor ) ), 256, 256 );
					tm.addTexture( "TutorialFloor", new Texture( bitmap, true ) );	
					bitmap.recycle();
					//Ceiling
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.tutorialceiling ) ), 512, 512 );
					tm.addTexture( "TutorialCeiling", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					Log.d( "BBRenderer", "Loading textures took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
					
					break;
				case CLASSROOM:
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.chalkboard ) ), 256, 256 );
					tm.addTexture( "Chalkboard", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.calendar ) ), 256, 256 );
					tm.addTexture( "Calendar", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.clock ) ), 256, 256 );
					tm.addTexture( "Clock", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.backpack ) ), 256, 256 );
					//tm.addTexture( "Backpack", objects );
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.paper ) ), 256, 128 );
					tm.addTexture( "Paper", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room0wall0 ) ), 1024, 512 );
					tm.addTexture( "Room0Wall0", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room0wall1 ) ), 1024, 512 );
					tm.addTexture( "Room0Wall1", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room0wall2 ) ), 1024, 512 );
					tm.addTexture( "Room0Wall2", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room0wall3 ) ), 1024, 512 );
					tm.addTexture( "Room0Wall3", new Texture( bitmap, true ) );
					bitmap.recycle();
					//Floor
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room0floor ) ), 1024, 1024 );
					tm.addTexture( "Room0Floor", new Texture( bitmap, true ) );	
					bitmap.recycle();
					//Ceiling
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room0ceiling ) ), 1024, 1024 );
					tm.addTexture( "Room0Ceiling", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					Log.d( "BBRenderer", "Loading textures took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
					
					break;
				case DINER:
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.money1 ) ), 512, 256 );
					tm.addTexture( "Money", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.bread ) ), 512, 512 );
					tm.addTexture( "Bread", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.bill ) ), 256, 512 );
					tm.addTexture( "Bill", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room1wall0 ) ), 1024, 512 );
					tm.addTexture( "Room1Wall0", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room1wall1 ) ), 1024, 512 );
					tm.addTexture( "Room1Wall1", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room1wall2 ) ), 1024, 512 );
					tm.addTexture( "Room1Wall2", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room1wall3 ) ), 1024, 512 );
					tm.addTexture( "Room1Wall3", new Texture( bitmap, true ) );
					bitmap.recycle();
					//Floor
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room1floor ) ), 1024, 1024 );
					tm.addTexture( "Room1Floor", new Texture( bitmap, true ) );
					bitmap.recycle();
					//Ceiling
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.room1ceiling ) ), 1024, 1024 );
					tm.addTexture( "Room1Ceiling", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					Log.d( "BBRenderer", "Loading textures took " + ( System.currentTimeMillis() - startTime ) + " milliseconds" );
					break;
					
				case STREET:
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.map ) ), 256, 512 );
					tm.addTexture( "Map", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.streetsign ) ), 256, 512 );
					tm.addTexture( "StreetSign", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.streetsign_back ) ), 256, 512 );
					tm.addTexture( "StreetSign_Back", new Texture( bitmap, true ) );
					bitmap.recycle();

					break;
				case BEACH:
					break;
			}
		
		} catch( Exception e ) {
			Log.i( "BBRenderer", "Caught exception loading textures: " + e );
		}
	}
	
	// TODO: All the actual sound loading needs to be offset to ideally a sound manager object (BBSoundManager)
	// Loads all the game sounds
	private void loadSounds() {
		soundPool = new SoundPool( 4, AudioManager.STREAM_MUSIC, 100 );
        soundPoolMap = new SparseIntArray();
        soundPoolMap.put( 1, soundPool.load( BB.context, R.raw.escritorio, 1 ) );
        soundPoolMap.put( 2, soundPool.load( BB.context, R.raw.silla, 1 ) );
        soundPoolMap.put( 3, soundPool.load( BB.context, R.raw.pizarra, 1 ) );
        soundPoolMap.put( 4, soundPool.load( BB.context, R.raw.mochila, 1 ) );
        soundPoolMap.put( 5, soundPool.load( BB.context, R.raw.calendario, 1 ) );
        soundPoolMap.put( 6, soundPool.load( BB.context, R.raw.reloj, 1 ) );
        soundPoolMap.put( 7, soundPool.load( BB.context, R.raw.puerta, 1 ) );
        soundPoolMap.put( 8, soundPool.load( BB.context, R.raw.libro, 1 ) );
        soundPoolMap.put( 9, soundPool.load( BB.context, R.raw.papel, 1 ) );
        soundPoolMap.put( 10, soundPool.load( BB.context, R.raw.ventana, 1 ) );
        soundPoolMap.put( 11, soundPool.load( BB.context, R.raw.cuenta, 1 ) );
        soundPoolMap.put( 12, soundPool.load( BB.context, R.raw.pan, 1 ) );
        soundPoolMap.put( 13, soundPool.load( BB.context, R.raw.pastel, 1 ) );
        soundPoolMap.put( 14, soundPool.load( BB.context, R.raw.taza, 1 ) );
        soundPoolMap.put( 15, soundPool.load( BB.context, R.raw.cuchillo, 1 ) );
        soundPoolMap.put( 16, soundPool.load( BB.context, R.raw.efectivo, 1 ) );
        soundPoolMap.put( 17, soundPool.load( BB.context, R.raw.plato, 1 ) );
        soundPoolMap.put( 18, soundPool.load( BB.context, R.raw.cuchara, 1 ) );
        soundPoolMap.put( 19, soundPool.load( BB.context, R.raw.mesa, 1 ) );
        soundPoolMap.put( 20, soundPool.load( BB.context, R.raw.direccion, 1 ) );
        soundPoolMap.put( 21, soundPool.load( BB.context, R.raw.bicicleta, 1 ) );
        soundPoolMap.put( 22, soundPool.load( BB.context, R.raw.autobus, 1 ) );
        soundPoolMap.put( 23, soundPool.load( BB.context, R.raw.coche, 1 ) );
        soundPoolMap.put( 24, soundPool.load( BB.context, R.raw.mapa, 1 ) );
        soundPoolMap.put( 25, soundPool.load( BB.context, R.raw.policia, 1 ) );
        soundPoolMap.put( 26, soundPool.load( BB.context, R.raw.senal, 1 ) );
        soundPoolMap.put( 27, soundPool.load( BB.context, R.raw.taxi, 1 ) );
        soundPoolMap.put( 28, soundPool.load( BB.context, R.raw.semaforo, 1 ) );
        soundPoolMap.put( 29, soundPool.load( BB.context, R.raw.basura, 1 ) );
        soundPoolMap.put( 30, soundPool.load( BB.context, R.raw.positive, 1 ) );
        soundPoolMap.put( 31, soundPool.load( BB.context, R.raw.bubble_pop, 1 ) );
        soundPoolMap.put( 32, soundPool.load( BB.context, R.raw.level_win, 1 ) );
	}
	
	// Sets up the game (OpenGL) scene
	private void setupScene() {
		// Initialize the level scene
		world = new BBRoom( level );
		world.setAmbientLight( 20, 20, 20 );
		
		// Set up lighting
		sun1 = new Light( world );
		sun1.setPosition( new SimpleVector(world.getLightLocation(level)) );
		sun1.setIntensity( 250, 250, 250 );
		
		// Set up the camera for the scene
		cam = world.getCamera();
		cam.setPosition( new SimpleVector( 0, 0, 0 ) );
		cam.lookAt( new SimpleVector( 0, 0.15, 0 ) );
		cam.setOrientation( new SimpleVector( 0, 0, 1 ), new SimpleVector( 0, -1, 0 ) );
		//cam.lookAt( new SimpleVector( 0, -0.1, 1 ) );
		
		loadingProgress = 20;
		
		// Load objects in the scene
		objects = world.getObjects();
		_currentObjectId = objects.nextElement().getID();
		
		// TODO: See if this is actually helpful, or just snake oil
		// Memory management
		MemoryHelper.compact();
		
		// Initialize the physics engine
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher( collisionConfiguration );
		worldAabbMin = new Vector3f( -1000, -1000, -1000 );
		worldAabbMax = new Vector3f( 1000, 1000, 1000 );
		overlappingPairCache = new AxisSweep3( worldAabbMin, worldAabbMax, world.getNumWordObjects() + 50 );
		solver = new SequentialImpulseConstraintSolver();
		physicsWorld = new DiscreteDynamicsWorld( dispatcher, overlappingPairCache, solver, collisionConfiguration );
		physicsWorld.setGravity( new Vector3f( 0, -9.8f, 0 ) );
		physicsWorld.getDispatchInfo().allowedCcdPenetration = 0f;
		
		// Enable physics on all objects
		for ( int i = 0; i < world.getNumBodies(); i++ ) {
			physicsWorld.addCollisionObject( world.getBody( i ) );
		}
		
		// TODO: Find out what this actually does
		// Do something
		physicsWorld.clearForces();
		for ( int i = 5; i < world.getNumBodies(); i++ ) {
			physicsWorld.addRigidBody( world.getBody( i ) );
		}
		
		loadingProgress = 25;
		
		// Set the level duration to be 100 seconds
		endTime = System.currentTimeMillis() + 100000;
		
		// Set the time left (to ensure the tutorial has time)
		timeLeft = 100;
		
		// Set the game state to "playing"
        isPaused = false;
        
        // Set the arrowMod to it's lowest (negative) point and arrow direction to move down (inc y)
        arrowMod = -(BB.height / 20);
        arrowDir = 1;
        
        // Set the handMod to it's highest (positive) point, and direction to move up (dec y)
        handMod = BB.height / 5;
        handDir = -1;
        handWaitEnd = System.currentTimeMillis() + 2000;
        moveHand = true;
        handTransparency = 50;
        
        // Track answers on screen
        answer = new BBDynamicScreenObject((BB.width / 2) - (0 * (BB.width / 15 + 4)),
        		BB.height / 10, BB.width / 2, - BB.height / 5, "", 1500, Gender.MASCULINE );
        
        // Setup pause and fire buttons
    	pauseButton = new BBButton( BB.width - BB.width / 30, BB.width / 35, BB.width / 15, BB.width / 15, 
    			"PauseButton", "PauseButtonPressed");
    	fireButton = new BBButton( BB.width / 10, BB.height - (BB.width / 10), BB.width / 6, BB.width / 6, 
    			"FireButton", "FireButtonPressed");
    	scoreButton = new BBButton( BB.height / 6, BB.height / 6, BB.height / 3, BB.height / 3, "EmptyScore", "EmptyScore");
    	scoreButton.canBePressed = false;

    	score = 0;
    	streak = 0;
    	multiplier = 1;
    	
    	levelState = State.PLAYING;
	}
	
	/**
	 * @return
	 */
	public Camera getCam() {
		return cam;
	}
	
	// Steps one frame/iteration in the game rendering
	protected void update() {
		// Get wall clock time
		long ms = clock.getTimeMicroseconds();
		clock.reset();
		
		// Adjust camera according to movement (if applicable)
		if ( horizontalSwipe != 0 || verticalSwipe != 0 ) {
			V.set( cam.getDirection() );
			//Log.d( "BBRenderer", "Cam direction: " + V );
			V.rotateY( horizontalSwipe );
			cam.lookAt( V );
			cam.rotateCameraX( -verticalSwipe/1.5f );
			horizontalSwipe = 0;
			verticalSwipe = 0;
		}
		
		// Step forward one "iteration" in the physics world
		try {
			physicsWorld.stepSimulation( ms / 1000000.0f );
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		
		// Update time bar
		if ( BB.isTimeLimitenabled ) {
			if ( !isPaused ) {
				if ( endTime - System.currentTimeMillis() > 0 ) {
					timeLeft = (endTime - System.currentTimeMillis() )/1000;
				} else {
					levelLose();
				}
			}
		}
		
		// Update answer location
		answer.move();
		
		if ( isTutorial ) {
			timeIcon.move();
		}

		
		// Update arrow movement
		if ( arrowDir > 0 ) {
			arrowMod += BB.height / 200;
		} else {
			arrowMod -= BB.height / 200;
		}
		if ( Math.abs(arrowMod) >= BB.height / 20 ) {
			arrowDir *= -1;
		}
		
		// Update hand movement
		if ( handDir > 0 && moveHand) {
			handMod += BB.height / 100;
		} else {
			if ( moveHand ) {
				handMod -= BB.height / 100;
			}
		}
		if ( Math.abs(handMod) >= BB.height / 5 ) {
			
			handMod = BB.height / 5 * handDir;
			
			// Change transparency if not moving, hold still for 3 seconds
			if ( System.currentTimeMillis() < handWaitEnd ) {
				moveHand = false;
				handTransparency = (handTransparency > 0) ? handTransparency - 1 :  0;
			} else {
				handMod *= -1;
				handWaitEnd = System.currentTimeMillis() + 2000;
				moveHand = true; 
				handTransparency = 50;
			}
		} else {
			moveHand = true;
			handTransparency = 50;
		}
		
		
		// TODO: Check the efficiency of this
		// Rotate objects floating in the bubbles
		try {
			if ( lastRotateTime < ( System.currentTimeMillis() - 15 ) ) {
				lastRotateTime = System.currentTimeMillis();
				ArrayList<BBBubble> bubbleObjects = world.getBubbleObjects();
				for ( BBBubble bubble : BBReversed.reversed( bubbleObjects ) ) {
					if ( bubble.isHolding ) {
						Object3D obj = world.getObject( bubble.getHeldObjectId() );
						obj.translate( bubble.getTranslation().calcSub( obj.getTranslation() ).calcSub( obj.getOrigin() ).calcSub( obj.getCenter() ) );
						obj.rotateY( 0.1f );
						world.getObject( bubble.getObjectId() ).rotateY( 0.1f );
						
					} else {
						/* Currently, the bubble pops but the next one shot breaks the physics engine.
						if ( System.currentTimeMillis() > bubble.getTimeCreated() + 5000 ) {
							Log.i( "BBRenderer", "I'm deleting the bubble!" );
							deleteBubble( bubble );
							continue;
						}*/
					}
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		checkCollisions();
	}
	
	/**
	 * @param position
	 * @return
	 */
	// Shoots a bubble in the aimed direction
	public void shootBubble() {
		// Make sure the game is running
		if ( !isPaused ) {
			// TODO: Adjust this delay to see what feels "natural"
			if ( System.currentTimeMillis() > lastShot + 50 ) {
				// Add the bubble to the world
				RigidBody body = world.addBubble( cam.getPosition() );
				// Activate physics on the bubble
				if ( body != null ) {
					Log.i( "BBRenderer", "Before adding bubble to physics world" );
					physicsWorld.addRigidBody( body );
					int size = physicsWorld.getCollisionObjectArray().size();
					body = (RigidBody) physicsWorld.getCollisionObjectArray().get( size - 1 );
					body.setGravity( new Vector3f( 0, 0, 0 ) );
					world.getLastBubble().setBodyIndex( size - 1 );
					lastShot = System.currentTimeMillis();
					Log.i( "BBRenderer", "After adding bubble to physics world" );
					//return body;
					
					SimpleVector dir = Interact2D.reproject2D3DWS( cam, BBRenderer.getFrameBuffer(), BB.width/2, BB.height/2 );
					dir.scalarMul( -70 );
					Vector3f force = new Vector3f( -dir.x * 2, dir.y * 2, dir.z * 2 );
					body.activate( true );
					body.setLinearVelocity( force );
				}
			}
		}
	}
	
	/**
	 * @param bubble
	 */
	// Removes bubble from the game/world
	public void deleteBubble( BBBubble bubble ) {
		physicsWorld.removeRigidBody( (RigidBody) physicsWorld.getCollisionObjectArray().get( bubble.getBodyIndex() ) );
		world.removeBubble( bubble );
	}
	
	/**
	 * @param state
	 */
	// Sets the bubble/gender state
	public void setGender( Gender state ) {
		//Put 2D bubble image on screen with 2D renderer
		world.setGender( state );
		if ( state == Gender.FEMININE )
			bubbleTex = "bubbleRed";
		else
			bubbleTex = "bubbleBlue";
	}
	
	/**
	 * @return
	 */
	public int iterateWattson() {
		wattsonText.clear();
		if ( !hasCompletedSteps ) {
			wattsonTextIterator++;
			wattsonText.add( wattsonPhrases[wattsonTextIterator][0] );
			wattsonText.add( wattsonPhrases[wattsonTextIterator][1] );
			wattsonText.add( wattsonPhrases[wattsonTextIterator][2] );
			
			if(wattsonTextIterator < 4){
				wattsonPrivileges = wattsonPrivileges << 1;
			}
			else if(wattsonTextIterator == 4){
				wattsonPrivileges = 17;
				timeIcon = new BBDynamicScreenObject(BB.width / 2, BB.height / 2, (int) ( BB.width*0.966 ),
						( int )( BB.height*0.91 ), "TimeIcon", 2000, BB.height / 2, BB.height / 2,
						BB.height / 35 , BB.height / 35 );
			}
			else{
				wattsonPrivileges = 14;
				hasCompletedSteps = true;
			}
			Log.d( "BBRenderer", "Iterating Wattson, and return value of: " + wattsonPrivileges );
		}
		return wattsonPrivileges;
	}
	
	/**
	 * @return
	 */
	// TODO: Make sure there are no gross inefficiencies here
	// Checks for bubble-object match; if there is, it shrinks the object down
	// and sets it to stay inside the bubble object
	public int handleCollision( BBBubble bubble, BBWordObject target ) {
		
		try {
			// Handle bubble-object collision
			if ( target.type == BBWordObject.Classification.WORD_OBJECT ) {
				// Only capture the object if the articles match
				if ( bubble.article == target.article ) {
					// Play positive sound
					soundPool.play(30, 3, 3, 1, 0, 1f);
					
					// Display Correct answer
					String word;
					if ( target.article == Gender.FEMININE ) {
						word = "La ";
					} else {
						word = "El ";
					}
					word += target.getName( Language.SPANISH );
					answer = new BBDynamicScreenObject((BB.width / 2) - (word.length() * (BB.width / 65)),
							BB.height / 20 + BB.height / 8, (BB.width / 2) - (word.length() * (BB.width / 65)),
							0, word, 1000, target.article );

					// Add to list of completed words
					bubbleWords.add( target.getName( BBTranslator.Language.ENGLISH ) );
					target.disableLazyTransformations();
					if ( target.getName( BBTranslator.Language.ENGLISH ) != "Plate" ) {
						target.scale( 5.0f / 1.1f );
					}
					// Capture object
					target.setStatic( false );
					// Track which object the bubble captured
					bubble.setHeldObjectId( target.getID() );
					bubble.setCollisionMode( Object3D.COLLISION_CHECK_OTHERS );
					bubble.build();
					// Play the corresponding sound of the captured object
					soundPool.play( BBTranslator.getIndexByWord( target.getName( BBTranslator.Language.SPANISH ) ) + 1, 3, 3, 1, 0, 1f );
					// Handle winning the game
					if ( hasWonLevel() ) {
						new Thread( new Runnable() {

							@Override
							public void run() {
								levelWin();
							}
							
						}).start();
					}
					return 0;
				}
				else {
					
					// Wrong color guessed :-(
					soundPool.play(31, 0.5f, 0.5f, 1, 0, 1f);
					
					// Don't display anything for Incorrect answer
					answer.location = answer.end;
					
					deleteBubble( bubble );
					endTime -= 5000;
					timeLeft -= 5;
					multiplier = 1;
					streak = 0;
					return 0;
				}
			// Handle bubble-bubble collision
			// TODO: This assumes that a pre-existing bubble contains an object, which is not necessarily true. Fix this assumption
			} else if ( target.type == BBWordObject.Classification.BUBBLE ) {
				Log.i( "BBRenderer", "Object is a bubble!" );
				BBBubble bubbleCollisionObject = (BBBubble) target;
				world.removeObject( bubbleCollisionObject.getHeldObjectId() );
				soundPool.play(31, 0.5f, 0.5f, 1, 0, 1f);
				if ( bubble.getObjectId() > bubbleCollisionObject.getObjectId() ) {
					deleteBubble( bubble );
					deleteBubble( bubbleCollisionObject );
				} else {
					deleteBubble( bubbleCollisionObject );
					deleteBubble( bubble );
				}
				return 0;
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return 0;
	}
	
	// Check for bubble collisions, and captures objects inside bubbles (if applicable)
	public int checkCollisions() {
		int i = 0, numBubbles = world.getNumBubbles();
		
		try {
			// Iterate through all bubbles to check for collisions
			// Note: Unfortunately, this is apparently the only way to check for collisions.
			for ( i = 0; i < numBubbles; i++ ) {
				BBBubble bubble = world.getBubble( i );
				// Make sure the bubble doesn't already contain an object
				if ( bubble != null && !bubble.isHolding && bubble.getBodyIndex() != -1 ) {
					RigidBody tempBody = (RigidBody) physicsWorld.getCollisionObjectArray().get( bubble.getBodyIndex() );
					
					// To get the position the bubble will be in the next "step"/tick, just use its current linear velocity
					// Note: This works because the physics world calculates position changes per "step"/tick as well, so 
					//       in the next "step"/tick, the bubble will have traveled the distance of linearVelocity.
					Vector3f linearVelocity = new Vector3f( 0, 0, 0 );
					linearVelocity = tempBody.getLinearVelocity( linearVelocity );
					
					// Physics world uses standard axes, but jPCT's Y- and Z-axes are reversed
					SimpleVector translation = new SimpleVector( linearVelocity.x, -linearVelocity.y, -linearVelocity.z );
					translation = translation.normalize();
					
					// Check to see if the bubble will collide with an object (in the next "step"/tick)
					world.getObject( bubble.getObjectId() ).checkForCollisionSpherical( translation, 5.0f );
					
				}
			}
		} catch ( IndexOutOfBoundsException e ) {
			Log.e( "BBRenderer", "Index is out of bounds for index " + i + " and array size " + world.getNumBubbles() );
		}
		return 0;
	}
	
	/**
	 * 
	 */
	// Sets the pause button state
	public void setPauseButtonState() {
		if ( pauseButton.swapState() ) {
			isPaused = true;
			if ( BB.isTimeLimitenabled )
				timeLeft = (endTime - System.currentTimeMillis())/1000;
		} else {
			if ( BB.isTimeLimitenabled )
				endTime = System.currentTimeMillis() + (timeLeft*1000);
			isPaused = false;
		}
	}
	
	/**
	 * @return
	 */
	// Checks if the player has won the level
	public boolean hasWonLevel() {
		
		// Update score
		score += 100 * multiplier;
		
		// Update multiplier
		streak++;
		multiplier = (streak > 2) ? (streak / 3 ) * 2 : 1;
		
		int total = world.roomObjectWords.size();
		int tempCaptured = 0;
		
		for(String word : world.roomObjectWords){
			if(bubbleWords.contains(word)){
				tempCaptured++;
			}
		}
		
		captured = tempCaptured;
		  		
		if(isTutorial){
			if(wattsonPrivileges == 8){
				cam.lookAt(new SimpleVector(0,0.1,1));
			}
			else if(wattsonPrivileges == 17){
				cam.lookAt(new SimpleVector(0,0,1));
			}
		}
		if ( captured != total )
			return false;
		else
			return true;
	}
	
	/**
	 * 
	 */
	// TODO: Check to make sure there are no gross inefficiencies, also handle activity changes correctly
	// Handles the player winning the level
	public void levelWin() {
		bubbleWords.clear();
		
		levelState = State.WON;
		// TODO: show game screen dialog

		SharedPreferences settings = BB.context.getSharedPreferences( BB.PREFERENCES, 0 );
		bubbleTex = "bubbleBlue";
    	if ( isTutorial ) {
    		wattsonText.clear();
    		Log.d( "BBRenderer", "Setting hasBeatenTutorial" );
    		settings.edit().putBoolean( "hasBeatenTutorial", true ).commit();
    		
            world.dispose();
    		handler.post( new Runnable() {
                public void run() {
                	Toast toast = Toast.makeText( BB.context, "Looks like you've got it!", Toast.LENGTH_LONG );
                    toast.show();
                    Intent intent = new Intent( BB.context, BBGameScreen.class );
            	    intent.setClass( BB.context, BBMenuScreen.class );
            	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            	    BB.context.startActivity( intent );
            	    loading = true;
                }
            } );
    	} else {
    		
    		soundPool.play(32, 2, 2, 1, 0, 1f);
    		
    		level = level.getNext();  
    		
    		if ( settings.getInt( "nextLevel", 0 ) < level.ordinal() ) {
    			settings.edit().putInt( "nextLevel", level.ordinal() ).commit();
    		}
    		handler.post( new Runnable() {
    			@SuppressLint("NewApi")
				public void run() {
    				BBGameScreen.endContinueButton.setText( R.string.c_resume );
    				BBGameScreen.endContinueButton.setOnClickListener( new View.OnClickListener() {
        		        
        		        @Override
        		        public void onClick( View v ) {
				        	if ( BB.context.getSharedPreferences( BB.PREFERENCES, 0).getStringSet( "playedComics", 
			                		BB.EMPTYSET).contains("comic" + ( level.ordinal() - 1) + "b") ) {
			                	Intent intent = new Intent( BB.context, BBGameScreen.class );
				        	    intent.setClass( BB.context, BBMapScreen.class );
				        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				        	    world.dispose();
				        	    BBGameScreen.endDialog.cancel();
				        	    BB.context.startActivity( intent );
				        	    loading = true;
			                } else {
				        		Intent intent = new Intent( BB.context, BBGameScreen.class );
				        	    intent.setClass( BB.context, BBVideoScreen.class );
				        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
				        	    intent.putExtra( BB.EXTRA_MESSAGE, "comic" + ( level.ordinal() - 1 ) + "b" );
				        	    world.dispose();
				        	    BBGameScreen.endDialog.cancel();
				        	    BB.context.startActivity( intent );
				        	    loading = true;
			                }
        		        }
    				} );
    				
    				BBGameScreen.endView.setBackgroundResource( R.drawable.win_game );
    				BBGameScreen.endDialog.show();
    				setPauseButtonState();
    				/*
		        	if ( BB.context.getSharedPreferences( BB.PREFERENCES, 0).getStringSet( "playedComics", 
	                		BB.EMPTYSET).contains("comic" + ( level.ordinal() - 1) + "b") ) {
	                	Intent intent = new Intent( BB.context, BBGameScreen.class );
		        	    intent.setClass( BB.context, BBMapScreen.class );
		        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		        	    BB.context.startActivity( intent );
		        	    loading = true;
	                } else {
		        		Intent intent = new Intent( BB.context, BBGameScreen.class );
		        	    intent.setClass( BB.context, BBVideoScreen.class );
		        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		        	    intent.putExtra( BB.EXTRA_MESSAGE, "comic" + ( level.ordinal() - 1 ) + "b" );
		        	    BB.context.startActivity( intent );
		        	    loading = true;
	                }
	                */
    			}
    		});
           
    	}

	}

	/**
	 * 
	 */
	// TODO: Make sure activity changes are handled correctly
	// Handles the player losing the level
	public void levelLose() {
		
		levelState = State.LOST;
		// TODO: show game screen dialog
		handler.post( new Runnable() {
            public void run() {
	        	BBGameScreen.endContinueButton.setText( R.string.c_back_to_map );
            	BBGameScreen.endContinueButton.setOnClickListener( new View.OnClickListener() {
    		        
    		        @Override
    		        public void onClick( View v ) {
	                	Intent intent = new Intent( BB.context, BBGameScreen.class );
		        	    intent.setClass( BB.context, BBMapScreen.class );
		        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		        	    world.dispose();
		        	    BBGameScreen.endDialog.cancel();
		        	    BB.context.startActivity( intent );
		        	    loading = true;
    		        	
    		        }
            	} );
            	BBGameScreen.endView.setBackgroundResource( R.drawable.lose_game );
				BBGameScreen.endDialog.show();
				setPauseButtonState();
            	/*
            	Toast toast = Toast.makeText( BB.context, R.string.lose_level_title, Toast.LENGTH_LONG );
                toast.show();
                Intent intent = new Intent( BB.context, BBGameScreen.class );
        	    intent.setClass( BB.context, BBMapScreen.class );
        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        	    BB.context.startActivity( intent );
        	    BB.ANIMATION = "DOWN";
        	    loading = true;
        	    */
            }
        } );
	}
	
	/**
	 * @param num
	 */
	public void setLevel( Level num ) {
		level = num;
	}
	
	public void resetLevel() {
		timeLeft = 100;
		endTime = System.currentTimeMillis() + 100000;
		bubbleTex = "bubbleBlue";
		bubbleWords.clear();
		score = 0;
		multiplier = 1;
		
		BBBubble bubble;
		
		for ( BBBubble object : world.getBubbleObjects()) {
			bubble = (BBBubble) world.getObject( object.getObjectId() );
			bubble.isHolding = false;
			//world.removeObject( bubble.getID() );
			//physicsWorld.removeRigidBody( (RigidBody) physicsWorld.getCollisionObjectArray().get( bubble.getBodyIndex() ) );
		}
		
		BBWordObject wordObject;
		
		for ( BBWordObject object : world.getWordObjects()) {
			wordObject = (BBWordObject) world.getObject( object.getObjectId() );
			wordObject.clearTranslation();
			wordObject.setStatic( true );
			wordObject.scaleFrom( 5.0f / 1.1f );
			wordObject.clearRotation();
		}
		
		levelState = State.PLAYING;
		
		// TODO: delete bubbles in room
		/*
		for ( BBBubble object : world.getBubbleObjects()) {
			bubble = (BBBubble) world.getObject( object.getObjectId() );
			world.removeObject( bubble.getID() );
			physicsWorld.removeRigidBody( (RigidBody) physicsWorld.getCollisionObjectArray().get( bubble.getBodyIndex() ) );
		}
		*/
	}
	
	
}
