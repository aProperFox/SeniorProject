package com.inherentgames;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;

import javax.vecmath.Vector3f;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.widget.Toast;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import com.inherentgames.BBRoom.Level;
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
	
	// Track the current room number
	protected Level level = Level.CLASSROOM;
	
	// Track whether the current level is the tutorial
	protected boolean isTutorial;
	
	// Tells whether the game is loading or running
	protected boolean loading = true;
	
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
	
	// Tracks horizontal and vertical swipe movement
	protected float horizontalSwipe = 0;
	protected float verticalSwipe = 0;
	
	// Track the states of the fire button, pause button, and bubbles
	protected String fireButtonState = "fireButton";
	protected String bubbleTex = "bubbleBlue";
	protected String pauseButtonState = "pauseButton";
	
	// Debugging option to track "current" object
	protected int _currentObjectId;
	
	// TODO: Move to BBTutorial for now, but ideally to a language file
	// Tutorial text
	protected String wattsonPhrases[][] = {
		{"Hi, Hopscotch! I'm your translator, Wattson.", "I'm here to show you how to travel through time.", "Tap me to begin!"},
		{ "Slide your finger to look around", "", ""},
		//log time, then aim at object and disable movement
		{"Nice Job!", "Capture the object in a bubble shot by your", "Chronopsyonic Quantum Destabilizer."},
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
		tm.addTexture( "loading_splash", new Texture( BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.loading ) ), 1024, 1024 ), true ) );
		
		// Initialize resource loading runnable
		loader = new Runnable() {

			@Override
			public void run() {
				// Prepare tutorial (if applicable)
				prepareTutorial();
				// Load resources
				loadSprites();
				loadTextures();
				loadSounds();
				// Prepare environment
				setupScene();
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
			wattsonText.add( wattsonPhrases[0][0] );
			wattsonText.add( wattsonPhrases[0][1] );
			wattsonText.add( wattsonPhrases[0][2] );
			wattsonTextIterator = 0;
		} else {
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
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.firebutton ) ), 128, 128 );
				tm.addTexture( "fireButton", new Texture( bitmap, true ) );
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.firebuttonpressed ) ), 128, 128 );
				tm.addTexture( "fireButtonPressed", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.pause_button ) ), 128, 128 );
				tm.addTexture( "pauseButton", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.pause_button_pressed ) ), 128, 128 );
				tm.addTexture( "pauseButtonPressed", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.word_bar ) ), 16, 512 );
				tm.addTexture( "FuelBar", new Texture( bitmap, true ) );
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.time_bar ) ), 16, 512 );
				tm.addTexture( "TimeBar", new Texture( bitmap, true ) );
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
				
				bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.arrow_right ) ), 128, 128 );
				tm.addTexture( "ArrowRight", new Texture( bitmap, true ) );
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
					
					// Can probably delete if, since tutorial now mandatory
					if ( !tm.containsTexture( "Escritorio" ) ) {
						bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.escritorio ) ), 256, 256 );
						tm.addTexture( "Escritorio", new Texture( bitmap, true ) );
						bitmap.recycle();
						bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.silla ) ), 256, 256 );
						tm.addTexture( "Silla", new Texture( bitmap, true ) );
						bitmap.recycle();
					}
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
					
					//Can probably delete if, since tutorial now mandatory
					if ( !tm.containsTexture( "Escritorio" ) ) {
						bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.escritorio ) ), 128, 128 );
						tm.addTexture( "Escritorio", new Texture( bitmap, true ) );
						bitmap.recycle();
						bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.silla ) ), 128, 128 );
						tm.addTexture( "Silla", new Texture( bitmap, true ) );
						bitmap.recycle();
					}
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.pizarra ) ), 128, 128 );
					tm.addTexture( "Pizarra", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.mochila ) ), 128, 128 );
					tm.addTexture( "Mochila", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.reloj ) ), 128, 128 );
					tm.addTexture( "Reloj", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.calendario ) ), 128, 128 );
					tm.addTexture( "Calendario", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.puerta ) ), 128, 128 );
					tm.addTexture( "Puerta", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.libro ) ), 128, 128 );
					tm.addTexture( "Libro", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.papel ) ), 128, 128 );
					tm.addTexture( "Papel", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.ventana ) ), 128, 128 );
					tm.addTexture( "Ventana", new Texture( bitmap, true ) );
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
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.cuenta ) ), 128, 128 );
					tm.addTexture( "Cuenta", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.pan ) ), 128, 128 );
					tm.addTexture( "Pan", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.pastel ) ), 128, 128 );
					tm.addTexture( "Pastel", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.taza ) ), 128, 128 );
					tm.addTexture( "Taza", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.cuchillo ) ), 128, 128 );
					tm.addTexture( "Cuchillo", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.efectivo ) ), 128, 128 );
					tm.addTexture( "Efectivo", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.plato ) ), 128, 128 );
					tm.addTexture( "Plato", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.cuchara ) ), 128, 128 );
					tm.addTexture( "Cuchara", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.mesa ) ), 128, 128 );
					tm.addTexture( "Mesa", new Texture( bitmap, true ) );
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
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.direccion ) ), 256, 256 );
					tm.addTexture( "Direccion", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.bicicleta ) ), 256, 256 );
					tm.addTexture( "Bicicleta", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.autobus ) ), 256, 256 );
					tm.addTexture( "Autobus", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.coche ) ), 256, 256 );
					tm.addTexture( "Coche", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.mapa ) ), 256, 256 );
					tm.addTexture( "Mapa", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.policia ) ), 256, 256 );
					tm.addTexture( "Policia", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.senal ) ), 256, 256 );
					tm.addTexture( "Senal", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.taxi ) ), 256, 256 );
					tm.addTexture( "Taxi", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.semaforo ) ), 256, 256 );
					tm.addTexture( "Semaforo", new Texture( bitmap, true ) );
					bitmap.recycle();
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( BB.context.getResources().getDrawable( R.drawable.basura ) ), 256, 256 );
					tm.addTexture( "Basura", new Texture( bitmap, true ) );
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
		cam.lookAt( new SimpleVector( 0, 0.1, 0 ) );
		cam.setOrientation( new SimpleVector( 0, 0, 1 ), new SimpleVector( 0, -1, 0 ) );
		//cam.lookAt( new SimpleVector( 0, -0.1, 1 ) );
		
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
        handWaitEnd = System.currentTimeMillis() + 3000;
        moveHand = true;
        handTransparency = 50;

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
		} catch ( NullPointerException e ) {
			Log.e( "BBRenderer", "jBulletPhysics threw a NullPointerException." );
		}
		
		// Update time bar
		if ( !isTutorial && !BBMenuScreen.isDevMode) {
			if ( !isPaused ) {
				if ( endTime - System.currentTimeMillis() > 0 ) {
					timeLeft = (endTime - System.currentTimeMillis() )/1000;
					Log.d("BBGame", "timeLeft = " + timeLeft);
				} else {
					levelLose();
				}
			}
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
			handMod += BB.height / 150;
		} else {
			if ( moveHand ) {
				handMod -= BB.height / 150;
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
				handWaitEnd = System.currentTimeMillis() + 3000;
				moveHand = true; 
				handTransparency = 50;
			}
		} else {
			moveHand = true;
			handTransparency = 50;
		}
		Log.d("BBGame", "handTransparency: " + handTransparency);
		
		
		// TODO: Check the efficiency of this
		// Rotate objects floating in the bubbles
		try {
			if ( lastRotateTime < ( System.currentTimeMillis() - 15 ) ) {
				lastRotateTime = System.currentTimeMillis();
				ArrayList<BBBubble> bubbleObjects = world.getBubbleObjects();
				for ( BBBubble bubble : BBReversed.reversed( bubbleObjects ) ) {
					if ( bubble.isHolding() ) {
						Object3D obj = world.getObject( bubble.getHeldObjectId() );
						obj.setOrigin( bubble.getTranslation().calcSub( obj.getCenter() ) );
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
		} catch( ConcurrentModificationException e ) {
			Log.e( "BBRenderer", "Concurrent Modification error occured" );
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
	 * @param isPressed
	 */
	// Sets the fire button state
	public void setFireButtonState( boolean isPressed ) {
		if ( isPressed ) {
			fireButtonState = "fireButtonPressed";
		}
		else {
			fireButtonState = "fireButton";
		}
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
				if ( bubble.getArticle() == target.getArticle() ) {
					// Add to list of completed words
					bubbleWords.add( target.getName( BBTranslator.Language.ENGLISH ) );
					target.disableLazyTransformations();
					if ( target.getName( BBTranslator.Language.ENGLISH ) != "Plate" ) {
						target.scale( 5.0f );
					}
					// Capture object
					target.setStatic( false );
					// Track which object the bubble captured
					bubble.setHeldObjectId( target.getID() );
					//Object3D worldBubbleObject = world.getObject( bubble.getObjectId() );
					// Apply the appropriate text label on the bubble
					bubble.setTexture( target.getName( BBTranslator.Language.SPANISH ) );
					bubble.calcTextureWrap();
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
					deleteBubble( bubble );
					return 0;
				}
			// Handle bubble-bubble collision
			// TODO: This assumes that a pre-existing bubble contains an object, which is not necessarily true. Fix this assumption
			} else if ( target.type == BBWordObject.Classification.BUBBLE ) {
				Log.i( "BBRenderer", "Object is a bubble!" );
				BBBubble bubbleCollisionObject = (BBBubble) target;
				world.removeObject( bubbleCollisionObject.getHeldObjectId() );
				deleteBubble( bubbleCollisionObject );
				deleteBubble( bubble );
				return 0;
			}
		} catch ( IndexOutOfBoundsException e ) {
			Log.e( "BBRenderer", e.getMessage() );
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
				if ( bubble != null && !bubble.isHolding() && bubble.getBodyIndex() != -1 ) {
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
		if ( pauseButtonState == "pauseButton" ) {
			isPaused = true;
			timeLeft = (endTime - System.currentTimeMillis())/1000;
			pauseButtonState = "pauseButtonPressed";
		} else {
			endTime = System.currentTimeMillis() + (timeLeft*1000);
			isPaused = false;
			pauseButtonState = "pauseButton";
		}
	}
	
	/**
	 * @return
	 */
	// Checks if the player has won the level
	public boolean hasWonLevel() {
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
			else if(wattsonPrivileges == 14){
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
		SharedPreferences settings = BB.context.getSharedPreferences( BBMenuScreen.PREFERENCES, 0 );
		bubbleTex = "bubbleBlue";
    	if ( isTutorial ) {
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
    		level = level.getNext();
    		  
    		if ( settings.getInt( "nextLevel", 0 ) < level.ordinal() )
    			settings.edit().putInt( "nextLevel", level.ordinal() ).commit();
    		
            world.dispose();
    		handler.post( new Runnable() {
	            public void run() {
	            	Toast toast = Toast.makeText( BB.context, R.string.win_level_title, Toast.LENGTH_LONG );
	                toast.show();
	        		Intent intent = new Intent( BB.context, BBGameScreen.class );
	        	    intent.setClass( BB.context, BBVideoScreen.class );
	        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
	        	    intent.putExtra( BBMenuScreen.EXTRA_MESSAGE, "comic" + ( level.ordinal() - 1 ) + "b" );
	        	    BB.context.startActivity( intent );
	        	    loading = true;
	            }
	        } );
    	}

	}

	/**
	 * 
	 */
	// TODO: Make sure activity changes are handled correctly
	// Handles the player losing the level
	public void levelLose() {
        world.dispose();
		handler.post( new Runnable() {
            public void run() {
            	Toast toast = Toast.makeText( BB.context, R.string.lose_level_title, Toast.LENGTH_LONG );
                toast.show();
                Intent intent = new Intent( BB.context, BBGameScreen.class );
        	    intent.setClass( BB.context, BBMapScreen.class );
        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        	    BB.context.startActivity( intent );
        	    BBMenuScreen.ANIMATION = "DOWN";
        	    loading = true;
            }
        } );
	}
	
	/**
	 * @param num
	 */
	public void setLevel( Level num ) {
		level = num;
	}
	
	
}
