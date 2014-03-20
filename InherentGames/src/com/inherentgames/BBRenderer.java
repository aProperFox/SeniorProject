package com.inherentgames;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Enumeration;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
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
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

class BBRenderer implements GLSurfaceView.Renderer{
	private TextureManager tm = TextureManager.getInstance();
	public static final int SHORT_TOAST = 5;
	private FrameBuffer fb = null;
	private Room world = null;
	private RGBColor back = new RGBColor(50,50,100);
	
	public float horizontalSwipe = 0;
	public float verticalSwipe = 0;
	
	private long lastRotateTime = 0;
	
	private SimpleVector V;
	
	public Camera cam;
	
	private int lightCycle = 0;
	private Light sun1 = null, sun2 = null;
	
	Context context;
	
	private int roomNum = 1;
	
	private ArrayList<String> bubbleWords = new ArrayList<String>();
	
	private String fireButtonState = "fireButton";
	
	private Renderer2D renderer2D;
	
	private DiscreteDynamicsWorld dynamicWorld;
	private DefaultCollisionConfiguration collisionConfiguration;
	private CollisionDispatcher dispatcher;
	
	public int currentObjectId;
	public Enumeration<Object3D> objects;
	
	private Clock clock;

	private int width = 0;
	private int height = 0;
	
	private int fuelHeight = 0;
	private int timeHeight;
	private int arrowHeight;
	private boolean isArrowAscending;
	
	private int letterWidth;
	
	private String bubbleTexture = "bubbleBlue";
	private String pauseButtonState = "pauseButton";
	
	private String wattsonPhrases[][] = {
			{"Hi, Hopscotch! I'm your translator, Wattson.", "I'm here to show you how to travel through time.", "Click me to begin!"},
			{"This is your Chronopsyonic Quantum Destabilizer.", "It shoots bubbles that capture objects in a quantum field", "to propel you back in time."},
			{"While holding down the fire button, you enable", "the destabilizer's charge sequence, allowing it to fire.", "Try holding the button!"},
			{"It's an incredible piece of technology!", "But it's uh... not exactly umm...",  ""},
			{".....legal.","",""},
			{"But not to worry! I'm sure you'll be fine", "so long as the cops don't see you use it.", ""},
			{"Holding the fire button and swiping up shoots", "a blue bubble. Try it now!", ""},
			{"Good show! Now try firing a red bubble by", "holding the fire button and swiping down", ""},
			{"Excellent! Now shoot a bubble at the desk to capture it", "but be careful! If you shoot the wrong bubble you risk", "getting teleported back to the future at 88 mph!"},
			{"You got it! Just two more pieces of advice", "till you're off to save the world!", ""},
			{"This is your fuel gauge. It fills up with every", "object you capture. Once it's full, you can time travel!", ""},
			{"The Chronospyonic Quantum Destabilizer can only", "keep you in quantum stasis for a limited time so be quick!", ""},
			{"The further you travel back in time,the harder", "it will be for the CQD to hold you in stasis.", "In the present there is no time limit."},
			{"You're the only one who can retrieve all the universal", "translators that are being sent to the past.", ""},
			{"Help us Hopscotch Krono, you're language's", "only hope.", ""}
	};
	
	private ArrayList<String> wattsonText = new ArrayList<String>();
	private int wattsonTextIterator;
	
	
	private long lastShot = System.currentTimeMillis();
	private long endTime;
	private long timeLeft;
	
	
	private Handler handler = new Handler();
	
	private SoundPool soundPool;
	SparseIntArray soundPoolMap;
	int soundID = 1;
	
	private boolean isPaused;
	private boolean isTutorial;
	private boolean hasCompletedSteps = false;
	private int[] screenItems = {0,5,4,4,3,5,-3,-2,-1,5,5,5,2,2,5};
	
	private int arrowX, arrowY, arrowImageWidth, arrowImageHeight, arrowScreenWidth, arrowScreenHeight;
	private String arrowState = "ArrowUp";
	
	/**
	 * @param c
	 * @param w
	 * @param h
	 * @param roomNum
	 */
	public BBRenderer(Context c, int w, int h, int roomNum) {
		context = c.getApplicationContext();
		V = new SimpleVector(0, 0, 1);
		this.roomNum = roomNum;
		
		width = w;
		height = h;
		
		//is tutorial?
		if(roomNum == 0) {
			isTutorial = true;
			wattsonText.add(wattsonPhrases[0][0]);
			wattsonText.add(wattsonPhrases[0][1]);
			wattsonText.add(wattsonPhrases[0][2]);
			wattsonTextIterator = 0;
			
			arrowX = width/12;
			arrowY = height/10 + width/6;
			arrowImageWidth = 32;
			arrowImageHeight = 64;
			arrowScreenWidth = width/12;
			arrowScreenHeight = width/6;
			
		}
		else{
			isTutorial = false;
		}
		
		letterWidth = width/96;

		
		Bitmap bitmap;
	      if(tm.containsTexture("gui_font")) {
	    	  
	      }
	      else{
			try{
	  	      
			Texture text = new Texture(context.getResources().openRawResource(R.raw.font));
			text.setFiltering(false);
			tm.addTexture("gui_font", text);
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.bubblered)), 256, 256);
			tm.addTexture("bubbleRed", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.bubbleblue)), 256, 256);
			tm.addTexture("bubbleBlue", new Texture(bitmap,true));
			bitmap.recycle();
			
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.firebutton)), 128, 128);
			tm.addTexture("fireButton", new Texture(bitmap,true));
			
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.firebuttonpressed)), 128, 128);
			tm.addTexture("fireButtonPressed", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.pause_button)), 128, 128);
			tm.addTexture("pauseButton", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.pause_button_pressed)), 128, 128);
			tm.addTexture("pauseButtonPressed", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.word_bar)), 16, 512);
			tm.addTexture("FuelBar", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.time_bar)), 16, 512);
			tm.addTexture("TimeBar", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.score_bars)), 128, 512);
			tm.addTexture("ScoreBars", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.info_bar)), 128, 128);
			tm.addTexture("InfoBar", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.fuel_bar_arrow)), 32, 32);
			tm.addTexture("ScoreArrow", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.arrow_up)), 32, 64);
			tm.addTexture("ArrowUp", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.arrow_right)), 64, 32);
			tm.addTexture("ArrowRight", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.arrow_left)), 64, 32);
			tm.addTexture("ArrowLeft", new Texture(bitmap,true));
			bitmap.recycle();
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.filter)), 64, 64);
			tm.addTexture("Filter", new Texture(bitmap,true));
			bitmap.recycle();
			
			bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.defaulttexture)), 256, 256);
			tm.addTexture("Default", new Texture(bitmap,true));
			bitmap.recycle();
			}catch(Exception e) {
				
			}
	      }
			
		setTextures();
		
	}

	/**
	 * 
	 */
	public void setTextures() {
		Bitmap bitmap;
		try{
			long startTime = System.currentTimeMillis();
		switch(roomNum) {
			case 0:
				
				//Can probably delete if, since tutorial now mandatory
				if(!tm.containsTexture("Escritorio")) {
					bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.escritorio)), 256, 256);
					tm.addTexture("Escritorio", new Texture(bitmap,true));
					bitmap.recycle();
					bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.silla)), 256, 256);
					tm.addTexture("Silla", new Texture(bitmap,true));
					bitmap.recycle();
				}
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.tutorialwall)), 512, 256);
				tm.addTexture("TutorialWall", new Texture(bitmap,true));
				bitmap.recycle();
				//Floor
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.tutorialfloor)), 256, 256);
				tm.addTexture("TutorialFloor", new Texture(bitmap,true));	
				bitmap.recycle();
				//Ceiling
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.tutorialceiling)), 512, 512);
				tm.addTexture("TutorialCeiling", new Texture(bitmap,true));
				bitmap.recycle();
				
				Log.d("MyRenderer", "Loading textures took " + (System.currentTimeMillis() - startTime) + " milliseconds");
				
				break;
			case 1:
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.chalkboard)), 256, 256);
				tm.addTexture("Chalkboard", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.calendar)), 256, 256);
				tm.addTexture("Calendar", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.clock)), 256, 256);
				tm.addTexture("Clock", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.backpack)), 256, 256);
				//tm.addTexture("Backpack", objects);
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.paper)), 256, 128);
				tm.addTexture("Paper", new Texture(bitmap,true));
				bitmap.recycle();
				
				//Can probably delete if, since tutorial now mandatory
				if(!tm.containsTexture("Escritorio")) {
					bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.escritorio)), 128, 128);
					tm.addTexture("Escritorio", new Texture(bitmap,true));
					bitmap.recycle();
					bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.silla)), 128, 128);
					tm.addTexture("Silla", new Texture(bitmap,true));
					bitmap.recycle();
				}
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.pizarra)), 128, 128);
				tm.addTexture("Pizarra", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.mochila)), 128, 128);
				tm.addTexture("Mochila", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.reloj)), 128, 128);
				tm.addTexture("Reloj", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.calendario)), 128, 128);
				tm.addTexture("Calendario", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.puerta)), 128, 128);
				tm.addTexture("Puerta", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.libro)), 128, 128);
				tm.addTexture("Libro", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.papel)), 128, 128);
				tm.addTexture("Papel", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.ventana)), 128, 128);
				tm.addTexture("Ventana", new Texture(bitmap,true));
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall0)), 1024, 512);
				tm.addTexture("Room0Wall0", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall1)), 1024, 512);
				tm.addTexture("Room0Wall1", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall2)), 1024, 512);
				tm.addTexture("Room0Wall2", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall3)), 1024, 512);
				tm.addTexture("Room0Wall3", new Texture(bitmap,true));
				bitmap.recycle();
				//Floor
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0floor)), 1024, 1024);
				tm.addTexture("Room0Floor", new Texture(bitmap,true));	
				bitmap.recycle();
				//Ceiling
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0ceiling)), 1024, 1024);
				tm.addTexture("Room0Ceiling", new Texture(bitmap,true));
				bitmap.recycle();
				
				Log.d("MyRenderer", "Loading textures took " + (System.currentTimeMillis() - startTime) + " milliseconds");
				
				break;
			case 2:
				
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.money1)), 512, 256);
				tm.addTexture("Money", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.bread)), 512, 512);
				tm.addTexture("Bread", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.bill)), 256, 512);
				tm.addTexture("Bill", new Texture(bitmap,true));
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room1wall0)), 1024, 512);
				tm.addTexture("Room1Wall0", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room1wall1)), 1024, 512);
				tm.addTexture("Room1Wall1", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room1wall2)), 1024, 512);
				tm.addTexture("Room1Wall2", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room1wall3)), 1024, 512);
				tm.addTexture("Room1Wall3", new Texture(bitmap,true));
				bitmap.recycle();
				//Floor
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room1floor)), 1024, 1024);
				tm.addTexture("Room1Floor", new Texture(bitmap,true));
				bitmap.recycle();
				//Ceiling
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room1ceiling)), 1024, 1024);
				tm.addTexture("Room1Ceiling", new Texture(bitmap,true));
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.cuenta)), 128, 128);
				tm.addTexture("Cuenta", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.pan)), 128, 128);
				tm.addTexture("Pan", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.pastel)), 128, 128);
				tm.addTexture("Pastel", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.taza)), 128, 128);
				tm.addTexture("Taza", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.cuchillo)), 128, 128);
				tm.addTexture("Cuchillo", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.efectivo)), 128, 128);
				tm.addTexture("Efectivo", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.plato)), 128, 128);
				tm.addTexture("Plato", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.cuchara)), 128, 128);
				tm.addTexture("Cuchara", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.mesa)), 128, 128);
				tm.addTexture("Mesa", new Texture(bitmap,true));
				bitmap.recycle();
				
				Log.d("MyRenderer", "Loading textures took " + (System.currentTimeMillis() - startTime) + " milliseconds");
				break;
				
			case 3:
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.map)), 256, 512);
				tm.addTexture("Map", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.streetsign)), 256, 512);
				tm.addTexture("StreetSign", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.streetsign_back)), 256, 512);
				tm.addTexture("StreetSign_Back", new Texture(bitmap,true));
				bitmap.recycle();
				
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.direccion)), 256, 256);
				tm.addTexture("Direccion", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.autobus)), 256, 256);
				tm.addTexture("Autobus", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.coche)), 256, 256);
				tm.addTexture("Coche", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.mapa)), 256, 256);
				tm.addTexture("Mapa", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.senal)), 256, 256);
				tm.addTexture("Senal", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.taxi)), 256, 256);
				tm.addTexture("Taxi", new Texture(bitmap,true));
				bitmap.recycle();
				bitmap = BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.semaforo)), 256, 256);
				tm.addTexture("Semaforo", new Texture(bitmap,true));
				bitmap.recycle();
			}
		
		} catch(Exception e) {
			Log.i("MyRenderer", "Caught exception loading textures: " + e);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		if (fb != null) {
			fb.dispose();
		}
		fb = new FrameBuffer(gl, w, h);

		isArrowAscending = true;
		arrowHeight = (int)(((float)height)/3.5f);
		
		renderer2D = new Renderer2D(fb);
		clock = new Clock();
		setTextures();
		world = new Room(roomNum, context, tm);
		world.setAmbientLight(20, 20, 20);
		
		sun1 = new Light(world);
		sun1.setPosition(new SimpleVector(0,-20, world.getLength()/2));
		sun1.setIntensity(250, 250, 250);
		
		sun2 = new Light(world);
		sun2.setPosition(new SimpleVector(0,-20, -world.getLength()/2));
		sun2.setIntensity(250, 250, 250);
		
		cam = world.getCamera();
		cam.setPosition(new SimpleVector(0,0,0));
		cam.lookAt(new SimpleVector(0,0.1,0));
		cam.setOrientation(new SimpleVector(0,0,1), new SimpleVector(0,-1,0));
		//cam.lookAt(new SimpleVector(0,-0.1,1));
		
		objects = world.getObjects();
		currentObjectId = objects.nextElement().getID();
		
		MemoryHelper.compact();
		
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldAabbMin = new Vector3f(-1000,-1000,-1000);
		Vector3f worldAabbMax = new Vector3f(1000,1000,1000);
		
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, world.getNumWordObjects() + 50);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		dynamicWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		dynamicWorld.setGravity(new Vector3f(0,-10,0));
		dynamicWorld.getDispatchInfo().allowedCcdPenetration = 0f;
	
		for(int i = 0; i < world.getNumBodies(); i++) {
			dynamicWorld.addCollisionObject(world.getBody(i));
		}
	
		dynamicWorld.clearForces();

		for(int i = 5; i < world.getNumBodies(); i++) {
			dynamicWorld.addRigidBody(world.getBody(i));
		}
	
		timeHeight = (int)(height*0.76);
		endTime = System.currentTimeMillis() + 100000;
		fuelHeight = 0;
		
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        //soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap = new SparseIntArray();
        soundPoolMap.put(1, soundPool.load(context, R.raw.escritorio, 1));
        soundPoolMap.put(2, soundPool.load(context, R.raw.silla, 1));
        soundPoolMap.put(3, soundPool.load(context, R.raw.pizarra, 1));
        soundPoolMap.put(4, soundPool.load(context, R.raw.mochila, 1));
        soundPoolMap.put(5, soundPool.load(context, R.raw.calendario, 1));
        soundPoolMap.put(6, soundPool.load(context, R.raw.reloj, 1));
        soundPoolMap.put(7, soundPool.load(context, R.raw.puerta, 1));
        soundPoolMap.put(8, soundPool.load(context, R.raw.libro, 1));
        soundPoolMap.put(9, soundPool.load(context, R.raw.papel, 1));
        soundPoolMap.put(10, soundPool.load(context, R.raw.ventana, 1));
        soundPoolMap.put(11, soundPool.load(context, R.raw.cuenta, 1));
        soundPoolMap.put(12, soundPool.load(context, R.raw.pan, 1));
        soundPoolMap.put(13, soundPool.load(context, R.raw.pastel, 1));
        soundPoolMap.put(14, soundPool.load(context, R.raw.taza, 1));
        soundPoolMap.put(15, soundPool.load(context, R.raw.cuchillo, 1));
        soundPoolMap.put(16, soundPool.load(context, R.raw.efectivo, 1));
        soundPoolMap.put(17, soundPool.load(context, R.raw.plato, 1));
        soundPoolMap.put(18, soundPool.load(context, R.raw.cuchara, 1));
        soundPoolMap.put(19, soundPool.load(context, R.raw.mesa, 1));
        isPaused = false;
		
		
	}

	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	public void onDrawFrame(GL10 gl) {
		
		if ( horizontalSwipe != 0 || verticalSwipe != 0 ) {
			V.set(cam.getDirection());
			Log.d("MyRenderer", "Cam direction: " + V);
			V.rotateY(horizontalSwipe);
			cam.lookAt(V);
			cam.rotateCameraX(-verticalSwipe/1.5f);
			horizontalSwipe = 0;
			verticalSwipe = 0;
			
		}
		
		float ms = clock.getTimeMicroseconds();
		clock.reset();
		try {
			dynamicWorld.stepSimulation(ms / 1000000f);
		} catch (NullPointerException e) {
			Log.e("MyRenderer", "jBulletPhysics threw a NullPointerException.");
		}
		fb.clear(back);
		
		if(world.skybox != null)
			world.skybox.render(world, fb);
	    
	    
		world.renderScene(fb);
		world.draw(fb);

		//Only called for
		if(isTutorial == false && MenuScreen.isDevMode == false) {
			if(!isPaused) {
				if(endTime - System.currentTimeMillis() > 0) {
					timeHeight = (int)((float)(endTime - System.currentTimeMillis())/100000f*(height*0.76));
				}
				else {
					levelLose();
				}
			}
		}
		
		display2DGameInfo(fb);
		
		try{
		
		if(lastRotateTime < (System.currentTimeMillis() - 15)) {
			lastRotateTime = System.currentTimeMillis();
			ArrayList<Bubble> bubbleObjects = world.getBubbleObjects();
			for(Bubble bubble : Reversed.reversed(bubbleObjects)) {
				if(bubble.isHolding()) {
					Object3D obj = world.getObject(bubble.getHeldObjectId());
					obj.setOrigin(bubble.getTranslation().calcSub(obj.getCenter()));
					obj.rotateY(0.1f);
					world.getObject(bubble.getObjectId()).rotateY(0.1f);
					
				}
				else{
					/* Currently, the bubble pops but the next one shot breaks the physics engine.
					if(System.currentTimeMillis() > bubble.getTimeCreated() + 5000) {
						Log.i("MyRenderer", "I'm deleting the bubble!");
						deleteBubble(bubble);
						continue;
					}*/
				}
			}
		}
		}catch(ConcurrentModificationException e) {
			Log.e("MyRenderer", "Concurrent Modification error occured");
		}
		
		checkBubble();
		/*
		 * TODO: add color to WordObjects when camera is aimed at them
		int id = world.getCameraBox().checkForCollision(cam.getDirection(), 80);
		if(id != -100) {
			WordObject wordObject = (WordObject)world.getObject(id);
			if(wordObject.getStaticState()) {
			Log.i("MyRenderer", "Viewed object collision!");
				//wordObject.setAdditionalColor(255,255,0);
			}
		}
		*/
			
	}
	
	/**
	 * @return
	 */
	public Room getWorld() {
		return world;
	}
	
	/**
	 * @return
	 */
	public FrameBuffer getFrameBuffer() {
		return fb;
	}
	
	/**
	 * @param position
	 * @return
	 */
	public RigidBody shoot(SimpleVector position) {
		if(!isPaused) {
			if(System.currentTimeMillis() > lastShot + 500) {
				RigidBody body = world.addBubble(position);
				if(body != null) {
					Log.i("MyRenderer", "Before adding bubble to physics world");
					dynamicWorld.addRigidBody(body);
					int size = dynamicWorld.getCollisionObjectArray().size();
					body = (RigidBody) dynamicWorld.getCollisionObjectArray().get(size-1);
					body.setGravity(new Vector3f(0,0,0));
					world.getLastBubble().setBodyIndex(size-1);
					lastShot = System.currentTimeMillis();
					Log.i("MyRenderer", "After adding bubble to physics world: " );
					return body;
				}
			}
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public int checkBubble() {
		int i = 0;
		//Checks bubble collision and if a collision occurs, it shrinks the object down
		//and sets it in the state to stay inside the bubble object
		try {
			for(i = 0; i < world.getNumBubbles(); i++) {
				Bubble bubble = world.getBubble(i);
				if(bubble.isHolding() == false && bubble.getBodyIndex() != -1 && bubble != null) {
					RigidBody tempBody = (RigidBody) dynamicWorld.getCollisionObjectArray().get(bubble.getBodyIndex());
					Vector3f linearVelocity = new Vector3f(0,0,0);
					linearVelocity = tempBody.getLinearVelocity(linearVelocity);
					SimpleVector motion = new SimpleVector(linearVelocity.x,-linearVelocity.y,-linearVelocity.z);
					int id = world.getObject(bubble.getObjectId()).checkForCollision(motion, 5);
					WordObject collisionObject;
					if(id != -100) Log.i("MyRenderer", "Checking object with id: " + id);
					if(id >= 0) {
						Log.d("MyRenderer", "That doesn't make sense... id != 0, but not bubble nor wordObject");
						if((collisionObject = world.getWordObject(id)) != null) {
							if(world.isBubbleType(id)) {
								Log.d("MyRenderer", "That doesn't make sense... Collision object is a bubble.");
							}
							else{
								Log.i("MyRenderer", "Object is a WordObject!");
							}
							if(collisionObject.getArticle() == bubble.getArticle()) {
								bubbleWords.add(collisionObject.getName(Translator.ENGLISH));
								if(collisionObject.getName(Translator.ENGLISH) != "Plate") {
									collisionObject.scale(5.0f);
								}
								collisionObject.setStatic(false);
								bubble.setHeldObjectId(id);
								//Object3D worldBubbleObject = world.getObject(bubble.getObjectId());
								bubble.setTexture(collisionObject.getName(Translator.SPANISH));
								bubble.calcTextureWrap();
								bubble.build();
								soundPool.play(Translator.getIndexByWord(collisionObject.getName(Translator.SPANISH)) + 1, 3, 3, 1, 0, 1f);
								hasWonGame();
								return 0;
							}
							else{
								deleteBubble(bubble);
								return 0;
							}
						}
						else if(world.isBubbleType(id)) {
							Log.i("MyRenderer", "Object is a bubble!");
							Bubble bubbleCollisionObject = (Bubble) world.getObject(id);
							world.removeObject(bubbleCollisionObject.getHeldObjectId());
							deleteBubble(bubbleCollisionObject);
							deleteBubble(bubble);
							return 0;
						}
					}
				}
			}
		} catch (IndexOutOfBoundsException e) {
			Log.e("MyRenderer", "Index is out of bounds for index " + i + " and array size " + world.getNumBubbles());
		}
		return 0;
	}
	
	/**
	 * @return
	 */
	public Camera getCam() {
		return cam;
	}
	
	/**
	 * @param bubble
	 */
	public void deleteBubble(Bubble bubble) {
		dynamicWorld.removeRigidBody((RigidBody)dynamicWorld.getCollisionObjectArray().get(bubble.getBodyIndex()));
		world.removeBubble(bubble);
	}
	
	/**
	 * @param state
	 */
	public void loadBubble(int state) {
		//Put 2D bubble image on screen with 2D renderer
		world.setBubbleColor(state);
		if(state == WordObject.FEMININE)
			bubbleTexture = "bubbleRed";
		else
			bubbleTexture = "bubbleBlue";
	}
	
	/**
	 * @param isPressed
	 */
	public void setFireButtonState(boolean isPressed) {
		if(isPressed) {
			fireButtonState = "fireButtonPressed";
		}
		else{
			fireButtonState = "fireButton";
		}
	}
	
	/**
	 * @return
	 */
	public int iterateWattson() {
		wattsonText.clear();
		if(!hasCompletedSteps) {
			wattsonTextIterator ++;
			if(wattsonTextIterator > screenItems.length -1) {
				wattsonTextIterator = 0;
			}
			wattsonText.add(wattsonPhrases[wattsonTextIterator][0]);
			wattsonText.add(wattsonPhrases[wattsonTextIterator][1]);
			wattsonText.add(wattsonPhrases[wattsonTextIterator][2]);
			
			int lastItem = screenItems[screenItems.length - 1];
			for(int i = screenItems.length - 1; i > 0; i--) {
				screenItems[i] = screenItems[i-1];
			}
			screenItems[0] = lastItem;
			lastItem = screenItems[screenItems.length-1];
			if(lastItem == 0)
				hasCompletedSteps = true;
			if(lastItem == -3)
				lastItem = -1;
			Log.d("MyRenderer", "Iterating Wattson, and return value of: " + lastItem);
			return lastItem;
		}
		return 0;
	}
	
	/**
	 * @param fb
	 */
	public void display2DGameInfo(FrameBuffer fb) {
		
		if(isTutorial) {
			//CrossHair
			renderer2D.blitCrosshair(fb, width, height);
			
			if(hasCompletedSteps) {
				//Bubble image
				renderer2D.blitImage(fb, bubbleTexture, width/2, height, 256, 256, width/3, width/3, 5);
				//Bubble text
				renderer2D.blitText(world.getBubbleArticle(), width/2-width/25, height-width/10, width/25, height/10,RGBColor.WHITE);
				//Fire Button
				renderer2D.blitImage(fb, fireButtonState, width/8, height-(width/8), 128, 128, width/8, width/8, 10);
				//Pause Button
				renderer2D.blitImage(fb, pauseButtonState, width-width/30, width/35, 128, 128, width/15, width/15, 100);
				//Dynamic fuel/time bars
				renderer2D.blitImageBottomUp(fb, "FuelBar", (int)(width*0.909), height/2, 16, 512, width/38, fuelHeight, (int)(height*0.76), 100);
				renderer2D.blitImageBottomUp(fb, "TimeBar", (int)(width*0.966), height/2, 16, 512, width/38, (int)(height*0.76), (int)(height*0.76), 100);
				//Score bars 
				renderer2D.blitImage(fb, "ScoreBars", width-(width/16), height/2, 128, 512, width/8, (int)(height*0.9), 100);
				renderer2D.blitImage(fb, "ScoreArrow", (int)(width*0.9), (int)(height*0.881)- fuelHeight, 32, 32, width/38, width/38, 100);
			}
			
			else{
				//Pause Button
				renderer2D.blitImage(fb, pauseButtonState, width-width/30, width/35, 128, 128, width/15, width/15, 100);
				
				int iterator = 0, size = screenItems.length - 1;
				for(int i : screenItems) {
					if(iterator < size)
						displayScreenItem(fb, i);
					iterator++;
				}
				if(screenItems[screenItems.length - 1] <= 0) {
				}
				else{
					renderer2D.blitImage(fb, "Filter", width/2, height/2, 64, 64, width, height, 10);
				}
				displayScreenItem(fb, screenItems[size]);
			}
			if(screenItems[screenItems.length-1] > 0)
				renderer2D.blitImage(fb, arrowState, arrowX, arrowY, arrowImageWidth, arrowImageHeight, arrowScreenWidth, arrowScreenHeight, 100);
			//Info Bar
			//Has extra 1 px hang if using real size? Decremented to 127x127
			renderer2D.blitImage(fb, "InfoBar", width/10, width/10, 127, 127, width/5, width/5, 100);

			try{
				//Wattson help text
				int iteration = 0;
				for(String string : wattsonText) {
					renderer2D.blitText(string, width/6, height/30 + (letterWidth*2*iteration), letterWidth, letterWidth*2,RGBColor.WHITE);
					iteration++;
				}
			}catch (ConcurrentModificationException e) {
				Log.e("Myrenderer", "display2DGameInfo got ConcurrentModificationError: " + e);
			}
			
		}
		
		//Not tutorial displays
		else{
			
			renderer2D.blitCrosshair(fb, width, height);
			//Bubble image
			renderer2D.blitImage(fb, bubbleTexture, width/2, height, 256, 256, width/3, width/3, 5);
			//Bubble text
			renderer2D.blitText(world.getBubbleArticle(), width/2-width/25, height-width/10, width/25, height/10,RGBColor.WHITE);
			//Fire Button
			renderer2D.blitImage(fb, fireButtonState, width/8, height-(width/8), 128, 128, width/8, width/8, 10);
			//Pause Button
			renderer2D.blitImage(fb, pauseButtonState, width-width/30, width/35, 128, 128, width/15, width/15, 100);
			//Info Bar
			//Has extra 1 px hang if using real size? Decremented to 255x255
			renderer2D.blitImage(fb, "InfoBar", width/10, width/10, 127, 127, width/5, width/5, 100);
			
			//Dynamic fuel/time bars
			renderer2D.blitImageBottomUp(fb, "FuelBar", (int)(width*0.909), height/2, 16, 512, width/38, fuelHeight, (int)(height*0.76), 100);
			renderer2D.blitImageBottomUp(fb, "TimeBar", (int)(width*0.966), height/2, 16, 512, width/38, timeHeight, (int)(height*0.76), 100);
			//Score bars
			renderer2D.blitImage(fb, "ScoreBars", width-(width/16), height/2, 128, 512, width/8, (int)(height*0.9), 100);
			renderer2D.blitImage(fb, "ScoreArrow", (int)(width*0.9), (int)(height*0.881)- fuelHeight, 32, 32, width/38, width/38, 100);
		
		}
		
		fb.display();
	}
	
	/**
	 * @param fb
	 * @param itemNum
	 */
	private void displayScreenItem(FrameBuffer fb, int itemNum) {
		boolean isLastItem;
		if(screenItems[screenItems.length-1] == itemNum)
			isLastItem = true;
		else
			isLastItem = false;
		switch(itemNum) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			//Fire Button
			renderer2D.blitImage(fb, fireButtonState, width/8, height-(width/8), 128, 128, width/8, width/8, 10);
			//Bubble image
			renderer2D.blitImage(fb, bubbleTexture, width/2, height, 256, 256, width/3, width/3, 5);
			//Bubble text
			renderer2D.blitText(world.getBubbleArticle(), width/2-width/25, height-width/10, width/25, height/10,RGBColor.WHITE);
			
			if(isLastItem) {
				arrowX = (int)(width/3.5f);
				arrowY = height - (width/8);
				arrowImageWidth = 64;
				arrowImageHeight = 32;
				arrowScreenWidth = width/6;
				arrowScreenHeight = width/12;
				arrowState = "ArrowLeft";
			}
			break;
		case 3:
			//Dynamic fuel bar
			renderer2D.blitImageBottomUp(fb, "FuelBar", (int)(width*0.909), height/2, 16, 512, width/38, fuelHeight, (int)(height*0.76), 100);
			renderer2D.blitImage(fb, "ScoreBars", width-(width/16), height/2, 128, 512, width/8, (int)(height*0.9), 100);
			renderer2D.blitImage(fb, "ScoreArrow", (int)(width*0.9), (int)(height*0.881)- fuelHeight, 32, 32, width/38, width/38, 100);
			
			if(isLastItem) {
				arrowX = 4*width/5;
				arrowY = height/2;
				arrowImageWidth = 64;
				arrowImageHeight = 32;
				arrowScreenWidth = width/6;
				arrowScreenHeight = width/12;
				arrowState = "ArrowRight";
			}
			break;
		case 4:
			//Dynamic time bar
			
			renderer2D.blitImageBottomUp(fb, "TimeBar", (int)(width*0.966), height/2, 16, 512, width/38, (int)(height*0.76), (int)(height*0.76), 100);
			//Score bars 
			renderer2D.blitImage(fb, "ScoreBars", width-(width/16), height/2, 128, 512, width/8, (int)(height*0.9), 100);
			renderer2D.blitImage(fb, "ScoreArrow", (int)(width*0.9), (int)(height*0.881)- fuelHeight, 32, 32, width/38, width/38, 100);
			
			if(isLastItem) {
				arrowX = 7*width/8;
				arrowY = height/2;
				arrowImageWidth = 64;
				arrowImageHeight = 32;
				arrowScreenWidth = width/6;
				arrowScreenHeight = width/12;
				arrowState = "ArrowRight";
			}
			
			break;
		case 5:
			arrowX = width/12;
			arrowY = height/10 + width/6;
			arrowImageWidth = 32;
			arrowImageHeight = 64;
			arrowScreenWidth = width/12;
			arrowScreenHeight = width/6;
			arrowState = "ArrowUp";
			break;
		case -3:
			if(screenItems[0] == itemNum)
				cam.lookAt(new SimpleVector(0,0,1));
			if(isLastItem)
				cam.lookAt(new SimpleVector(-.358, 0.174, 0.917));
			break;
		default:
			break;
		}
		
	}
	
	
	/**
	 * 
	 */
	public void setPauseButtonState() {
		if(pauseButtonState == "pauseButton") {
			isPaused = true;
			timeLeft = endTime - System.currentTimeMillis();
			pauseButtonState = "pauseButtonPressed";
		}
		else{
			endTime = System.currentTimeMillis() + timeLeft;
			isPaused = false;
			pauseButtonState = "pauseButton";
		}
	}
	
	/**
	 * @return
	 */
	public boolean hasWonGame() {
		ArrayList<String> tempWords = world.getRoomObjectWords();
		int listLength = tempWords.size();
		float numWordsCaptured = 0;
		for(int i = 0; i < tempWords.size(); i++) {
			for(int j = 0; j < bubbleWords.size(); j++) {
				if(bubbleWords.get(j) == tempWords.get(i)) {
					numWordsCaptured++;
					break;
				}
			}
		}
		fuelHeight = (int)((float)(numWordsCaptured/listLength)*(height*0.75));
		if(numWordsCaptured != listLength) {
			return false;
		}
		levelWin();
		System.gc();
		return true;
	}
	
	
	/**
	 * @param obj
	 * @return
	 */
	public Vector3f getDimensions(Object3D obj) {
		PolygonManager polyMan = obj.getPolygonManager();
		int polygons = polyMan.getMaxPolygonID();
		Vector3f minVerts = new Vector3f(1000,1000,1000);
		Vector3f maxVerts = new Vector3f(-1000,-1000,-1000);
		for(int i = 0; i < polygons; i++) {
			for(int j = 0; j < 3; j++) {
				if(minVerts.x > polyMan.getTransformedVertex(i, j).x)
					minVerts.x = polyMan.getTransformedVertex(i,j).x;
				if(maxVerts.x < polyMan.getTransformedVertex(i, j).x)
					maxVerts.x = polyMan.getTransformedVertex(i, j).x;
				if(minVerts.y > polyMan.getTransformedVertex(i, j).y)
					minVerts.y = polyMan.getTransformedVertex(i,j).y;
				if(maxVerts.y < polyMan.getTransformedVertex(i, j).y)
					maxVerts.y = polyMan.getTransformedVertex(i, j).y;
				if(minVerts.z > polyMan.getTransformedVertex(i, j).z)
					minVerts.z = polyMan.getTransformedVertex(i,j).z;
				if(maxVerts.z < polyMan.getTransformedVertex(i, j).z)
					maxVerts.z = polyMan.getTransformedVertex(i, j).z;
			}
		}
		return new Vector3f(maxVerts.x - minVerts.x, maxVerts.y - minVerts.y, maxVerts.z - minVerts.z);
	}
	
	/**
	 * 
	 */
	public void levelWin() {
		SharedPreferences settings = context.getSharedPreferences(MenuScreen.PREFERENCES, 0);
		bubbleTexture = "bubbleBlue";
    	if(isTutorial) {
    		Log.d("MyRenderer", "Setting hasBeatenTutorial");
    		settings.edit().putBoolean("hasBeatenTutorial", true).commit();
    		handler.post(new Runnable() {
                public void run() {
                	Toast toast = Toast.makeText(context, "Looks like you've got it!", Toast.LENGTH_LONG);
                    toast.show();
                    Intent intent = new Intent(context, GameScreen.class);
            	    intent.setClass(context, MenuScreen.class);
            	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	    context.startActivity(intent);
            	    world.dispose();
                }
            });
    	}
    	else{
    		  roomNum++;
    		  
    		  if(settings.getInt("nextLevel", 0) < roomNum)
    			  settings.edit().putInt("nextLevel", roomNum).commit();
    	      
	        handler.post(new Runnable() {
	            public void run() {
	            	Toast toast = Toast.makeText(context, R.string.win_level_title, Toast.LENGTH_LONG);
	                toast.show();
	        		Intent intent = new Intent(context, GameScreen.class);
	        	    intent.setClass(context, VideoScreen.class);
	        	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	    intent.putExtra(MenuScreen.EXTRA_MESSAGE, "comic" + (roomNum-1) + "b");
	        	    context.startActivity(intent);
	        		world.dispose();
	            }
	        });
    	}

	}

	/**
	 * 
	 */
	public void levelLose() {
		handler.post(new Runnable() {
            public void run() {
            	Toast toast = Toast.makeText(context, R.string.lose_level_title, Toast.LENGTH_LONG);
                toast.show();
                Intent intent = new Intent(context, GameScreen.class);
        	    intent.setClass(context, MenuScreen.class);
        	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	    context.startActivity(intent);
        	    world.dispose();
            }
        });
	}
	
	/**
	 * 
	 */
	public void restart() {
		handler.post(new Runnable() {
            public void run() {
                Intent intent = new Intent(context, GameScreen.class);
        	    intent.setClass(context, MenuScreen.class);
        	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	    context.startActivity(intent);
        	    world.dispose();
            }
        });
	}
	
	
	/**
	 * @param vector
	 * @return
	 */
	public Vector3f toVector3f(SimpleVector vector) {
		return new Vector3f(vector.x,vector.y,vector.z);
	}
	
	/**
	 * @param vector
	 * @return
	 */
	public SimpleVector toSimpleVector(Vector3f vector) {
		return new SimpleVector(vector.x,vector.y,vector.z);
	}
	
	/**
	 * @param num
	 */
	public void setRoomNum(int num) {
		roomNum = num;
	}
}

