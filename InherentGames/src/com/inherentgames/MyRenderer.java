package com.inherentgames;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.app.FragmentActivity;

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
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

class MyRenderer extends FragmentActivity implements GLSurfaceView.Renderer{
	private FrameBuffer fb = null;
	private Room world = null;
	private RGBColor back = new RGBColor(50,50,100);
	
	private float touchTurn = 0;
	private float touchTurnUp = 0;
	
	private long lastRotateTime = 0;
	
	private SimpleVector V;
	
	private Camera cam;
	
	private int fps = 0;
	private int lightCycle = 0;
	private Light sun = null;
	Context context;
	
	private int roomNum = 0;
	
	private ArrayList<String> bubbleWords = new ArrayList<String>();
	
	private String fireButtonState = "fireButton";
	
	private Renderer2D renderer2D;
	
	private DiscreteDynamicsWorld dynamicWorld;
	private DefaultCollisionConfiguration collisionConfiguration;
	private CollisionDispatcher dispatcher;
	private Clock clock;

	private int width = 0;
	private int height = 0;
	
	private String bubbleTexture = "bubbleBlue";
	
	private int score = 0;
	
	private long time = System.currentTimeMillis();
	private long lastShot = System.currentTimeMillis();
	
	public MyRenderer(Context c, int w, int h) {
		context = c.getApplicationContext();
		V = new SimpleVector(0, 0, 1);
		
		width = w;
		height = h;
		try{
		Texture text = new Texture(context.getResources().openRawResource(R.raw.font));
		text.setFiltering(false);
		TextureManager.getInstance().addTexture("gui_font", text);
		Texture bubble = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.bubblered)), 512, 512));
		TextureManager.getInstance().addTexture("bubbleRed", bubble);
		bubble = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.bubbleblue)), 512, 512));
		TextureManager.getInstance().addTexture("bubbleBlue", bubble);
		Texture buttons = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.firebutton)), 256, 256));
		TextureManager.getInstance().addTexture("fireButton", buttons);
		buttons = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.firebuttonpressed)), 256, 256));
		TextureManager.getInstance().addTexture("fireButtonPressed", buttons);
		
		Texture objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.pizarra)), 256, 256));
		TextureManager.getInstance().addTexture("Pizarra", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.escritorio)), 256, 256));
		TextureManager.getInstance().addTexture("Escritorio", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.silla)), 256, 256));
		TextureManager.getInstance().addTexture("Silla", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.mochila)), 256, 256));
		TextureManager.getInstance().addTexture("Mochila", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.reloj)), 256, 256));
		TextureManager.getInstance().addTexture("Reloj", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.calendario)), 256, 256));
		TextureManager.getInstance().addTexture("Calendario", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.puerta)), 256, 256));
		TextureManager.getInstance().addTexture("Puerta", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.libro)), 256, 256));
		TextureManager.getInstance().addTexture("Libro", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.papel)), 256, 256));
		TextureManager.getInstance().addTexture("Papel", objectNames);
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.ventana)), 256, 256));
		TextureManager.getInstance().addTexture("Ventana", objectNames);
		
		objectNames = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.defaulttexture)), 256, 256));
		TextureManager.getInstance().addTexture("Default", objectNames);
		
		Texture objects = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.chalkboard)), 256, 256));
		TextureManager.getInstance().addTexture("Chalkboard", objects);
		objects = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.calendar)), 256, 256));
		TextureManager.getInstance().addTexture("Calendar", objects);
		objects = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.clock)), 256, 256));
		TextureManager.getInstance().addTexture("Clock", objects);
		objects = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.backpack)), 256, 256));
		TextureManager.getInstance().addTexture("Backpack", objects);
		
		Texture[] textures = new Texture[6];
		//set textures
		//Walls
		textures[0] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall0)), 1024, 1024));
		/*textures[1] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall1)), 1024, 1024));
		textures[2] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall2)), 1024, 1024));
		textures[3] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall3)), 1024, 1024));*/
			//Floor
		textures[1] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0floor)), 1024, 1024));
			//Ceiling
		textures[2] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0ceiling)), 1024, 1024));
		
		TextureManager.getInstance().addTexture("Room0Wall0", textures[0]);
		/*TextureManager.getInstance().addTexture("Room0Wall1", textures[1]);
		TextureManager.getInstance().addTexture("Room0Wall2", textures[2]);
		TextureManager.getInstance().addTexture("Room0Wall3", textures[3]);*/
		TextureManager.getInstance().addTexture("Room0Floor", textures[1]);
		TextureManager.getInstance().addTexture("Room0Ceiling", textures[2]);
		}catch(Exception e){
			
		}
		
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		if (fb != null) {
			fb.dispose();
		}
		fb = new FrameBuffer(gl, w, h);

		renderer2D = new Renderer2D(fb);
		clock = new Clock();
		
		world = new Room(roomNum, context);
		world.setAmbientLight(20, 20, 20);
		
		
		sun = new Light(world);
		sun.setPosition(world.getLightLocation(0));
		sun.setIntensity(250, 250, 250);
		
		
		cam = world.getCamera();
		cam.setPosition(new SimpleVector(0,0,0));
		cam.setOrientation(new SimpleVector(0,0,1), new SimpleVector(0,-1,0));
		MemoryHelper.compact();
		
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldAabbMin = new Vector3f(-10000,-10000,-10000);
		Vector3f worldAabbMax = new Vector3f(10000,10000,10000);
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		dynamicWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		dynamicWorld.setGravity(new Vector3f(0,-10,0));
		dynamicWorld.getDispatchInfo().allowedCcdPenetration = 0f;
	
		for(int i = 0; i < world.getNumBodies(); i++){
			dynamicWorld.addCollisionObject(world.getBody(i));
		}
	
		dynamicWorld.clearForces();

		for(int i = 5; i < world.getNumBodies(); i++){
			dynamicWorld.addRigidBody(world.getBody(i));
		}
	
	}

	private void changeLevel(){
		clock = new Clock();
		
		world.dispose();
		world = new Room(roomNum, context);
		world.setAmbientLight(20, 20, 20);
		
		sun = new Light(world);
		sun.setPosition(world.getLightLocation(0));
		sun.setIntensity(250, 250, 250);
		
		cam = world.getCamera();
		cam.setPosition(new SimpleVector(0,0,0));
		cam.setOrientation(new SimpleVector(0,0,1), new SimpleVector(0,-1,0));
		MemoryHelper.compact();
		
		collisionConfiguration = new DefaultCollisionConfiguration();
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		Vector3f worldAabbMin = new Vector3f(-10000,-10000,-10000);
		Vector3f worldAabbMax = new Vector3f(10000,10000,10000);
		AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
		SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
		
		dynamicWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
		dynamicWorld.setGravity(new Vector3f(0,-10,0));
		dynamicWorld.getDispatchInfo().allowedCcdPenetration = 0f;
	
		for(int i = 0; i < world.getNumBodies(); i++){
			dynamicWorld.addCollisionObject(world.getBody(i));
		}
	
		dynamicWorld.clearForces();

		for(int i = 5; i < world.getNumBodies(); i++){
			dynamicWorld.addRigidBody(world.getBody(i));
		}
	
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}
	
	public void cycleLighting(){
		lightCycle++;
		if(lightCycle > 4)
			lightCycle = 0;
		switch(lightCycle){
		case 0:
			sun.enable();
			sun.setIntensity(250,250,250);
			break;
		case 1:
			sun.setIntensity(180,180,180);
			break;
		case 2:
			sun.setIntensity(100,100,100);
			break;
		case 3:
			sun.setIntensity(50,50,50);
			break;
		case 4:
			sun.disable();
			break;
		}
	}

	public void onDrawFrame(GL10 gl) {
		
		if ( touchTurn != 0 || touchTurnUp != 0 ) {
			V.set(cam.getDirection());
			V.rotateY(touchTurn);
			cam.lookAt(V);
			cam.rotateCameraX(-touchTurnUp/1.5f);
			touchTurn = 0;
			touchTurnUp = 0;
		}
		
		checkBubble();
		for(Bubble bubble : world.getBubbleObjects()) {
			if(bubble.isHolding()){
				Object3D obj = world.getObject(bubble.getHeldObjectId());
				obj.setOrigin(bubble.getTranslation().calcSub(obj.getCenter()));
				if(lastRotateTime < (System.currentTimeMillis() - 15)){
					obj.rotateY(0.1f);
					world.getObject(bubble.getObjectId()).rotateY(0.1f);
				}
			}
		}
		if(lastRotateTime < (System.currentTimeMillis() - 15))
			lastRotateTime = System.currentTimeMillis();
		
		float ms = clock.getTimeMicroseconds();
		clock.reset();
		dynamicWorld.stepSimulation(ms / 1000000f);
		fb.clear(back);
		
		world.renderScene(fb);
		world.draw(fb);
		renderer2D.blitCrosshair(fb, width, height);
		renderer2D.blitImage(fb, bubbleTexture, width/2, height, 512, 512, width/3, width/3, 5);
		renderer2D.blitText(world.getBubbleArticle(), width/2-width/25, height-width/10, width/25, height/10,RGBColor.WHITE);
		renderer2D.blitText("Score: " + score, width-width/9, height/20, width/95, height/16, RGBColor.WHITE);
		renderer2D.blitImage(fb, fireButtonState, width/8, height-(width/8), 256, 256, width/8, width/8, 5);
		fb.display();
		
		if (System.currentTimeMillis() - time >= 1000) {
			Logger.log(fps + "fps");
			fps = 0;
			time = System.currentTimeMillis();
		}
		fps++;
	}
	
	public void setTouchTurnUp(float value){
		touchTurnUp = value;
	}
	
	public Room getWorld(){
		return world;
	}
	
	public FrameBuffer getFrameBuffer(){
		return fb;
	}
	
	public void setTouchTurn(float value){
		touchTurn = value;
	}
	
	public RigidBody shoot(SimpleVector position){
		if ((System.currentTimeMillis()-lastShot) > 1000){
			RigidBody body = world.addBubble(position);
			dynamicWorld.addRigidBody(body);
			int size = dynamicWorld.getCollisionObjectArray().size();
			body = (RigidBody) dynamicWorld.getCollisionObjectArray().get(size-1);
			body.setGravity(new Vector3f(0,0,0));
			world.getLastBubble().setBodyIndex(size-1);
			lastShot = System.currentTimeMillis();
			return body;
		}
		return null;
	}
	
	public int checkBubble(){
		//Checks bubble collision and if a collision occurs, it shrinks the object down
		//and sets it in the state to stay inside the bubble object
		for(int i = 0; i < world.getNumBubbles(); i++){
			Bubble bubble = world.getBubble(i);
			if(bubble.isHolding() == false && bubble.getBodyIndex() != -1 && bubble != null){
				RigidBody tempBody = (RigidBody) dynamicWorld.getCollisionObjectArray().get(bubble.getBodyIndex());
				Vector3f linearVelocity = new Vector3f(0,0,0);
				linearVelocity = tempBody.getLinearVelocity(linearVelocity);
				SimpleVector motion = toSimpleVector(linearVelocity);
				int id = world.getObject(bubble.getObjectId()).checkForCollision(motion, 10);
				WordObject collisionObject;
				if(id >= 0){
					if((collisionObject = world.getWordObject(id)) != null){
						if(collisionObject.getArticle() == bubble.getArticle()){
							bubbleWords.add(collisionObject.getWord());
							collisionObject.scale(5.0f);
							bubble.setHeldObjectId(id);
							//Object3D worldBubbleObject = world.getObject(bubble.getObjectId());
							bubble.setTexture(collisionObject.getName(Translator.SPANISH));
							bubble.calcTextureWrap();
							bubble.build();
							score += 100;
							hasWonGame();
							return 0;
						}
						else{
							deleteBubble(bubble);
							score -= 25;
							return 0;
						}
					}
					else if(world.isBubbleType(id)){
						score -= 10;
						Bubble bubbleCollisionObject = (Bubble) world.getObject(id);
						world.removeObject(bubbleCollisionObject.getHeldObjectId());
						deleteBubble(bubbleCollisionObject);
						deleteBubble(bubble);
						return 0;
					}
				}
			}
		}
		return 0;
	}
	
	public Camera getCam(){
		return cam;
	}
	
	public void deleteBubble(Bubble bubble){
		dynamicWorld.removeRigidBody((RigidBody)dynamicWorld.getCollisionObjectArray().get(bubble.getBodyIndex()));
		world.removeBubble(bubble);
	}
	
	public void loadBubble(int state){
		//Put 2D bubble image on screen with 2D renderer
		world.setBubbleColor(state);
		if(state == WordObject.FEMININE)
			bubbleTexture = "bubbleRed";
		else
			bubbleTexture = "bubbleBlue";
	}
	
	public void setFireButtonState(boolean isPressed){
		if(isPressed){
			fireButtonState = "fireButtonPressed";
		}
		else{
			fireButtonState = "fireButton";
		}
	}
	
	public boolean hasWonGame(){
		ArrayList<String> tempWords = world.getRoomObjectWords();
		for(int i = 0; i < tempWords.size(); i++){
			boolean doesWordExist = false;
			for(int j = 0; j < bubbleWords.size(); j++){
				if(bubbleWords.get(j) == tempWords.get(i)){
					doesWordExist = true;
					break;
				}
			}
			if(!doesWordExist){
				return false;
			}
		}
		levelWin();
		return true;
	}
	
	public Vector3f getDimensions(Object3D obj){
		PolygonManager polyMan = obj.getPolygonManager();
		int polygons = polyMan.getMaxPolygonID();
		Vector3f minVerts = new Vector3f(1000,1000,1000);
		Vector3f maxVerts = new Vector3f(-1000,-1000,-1000);
		for(int i = 0; i < polygons; i++){
			for(int j = 0; j < 3; j++){
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
	
	public void levelWin(){
		if(roomNum == 0)
			roomNum ++;
		changeLevel();
	}
	
	public Vector3f toVector3f(SimpleVector vector){
		return new Vector3f(vector.x,vector.y,vector.z);
	}
	
	public SimpleVector toSimpleVector(Vector3f vector){
		return new SimpleVector(vector.x,vector.y,vector.z);
	}
}

