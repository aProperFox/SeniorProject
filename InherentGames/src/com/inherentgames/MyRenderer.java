package com.inherentgames;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.util.ObjectArrayList;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

class MyRenderer implements GLSurfaceView.Renderer {
	private FrameBuffer fb = null;
	private Room world = null;
	private RGBColor back = new RGBColor(50,50,100);
	
	private float touchTurn = 0;
	private float touchTurnUp = 0;
	
	private SimpleVector V;
	
	
	private Camera cam;
	
	private int fps = 0;
	private int lightCycle = 0;
	private Light sun = null;
	Context context;
	
	private boolean isPhysics = false;
	
	private DiscreteDynamicsWorld dynamicWorld;
	private DefaultCollisionConfiguration collisionConfiguration;
	private CollisionDispatcher dispatcher;
	private Clock clock;
	
	private Object3D object;
	
	private long time = System.currentTimeMillis();
	
	private boolean isBubbleHolding = false;

	public MyRenderer(Context c) {
		context = c.getApplicationContext();
		V = new SimpleVector(0, 0, 1);
		
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
		
		
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		if (fb != null) {
			fb.dispose();
		}
		fb = new FrameBuffer(gl, w, h);

		clock = new Clock();
		
		world = new Room(0, context);
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
		float ms = clock.getTimeMicroseconds();
		clock.reset();
		dynamicWorld.stepSimulation(ms / 1000000f);
		fb.clear(back);
		
		
		world.renderScene(fb);
		world.draw(fb);
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
		RigidBody body = world.addBubble(position);
		dynamicWorld.addRigidBody(body);
		int size = dynamicWorld.getCollisionObjectArray().size();
		ObjectArrayList<CollisionObject> array = dynamicWorld.getCollisionObjectArray();
		body = (RigidBody) array.get(size-1);
		body.setGravity(new Vector3f(0,0,0));
		return body;
	}
	
	public void checkBubble(){
		for(int i = 1; i < world.getBubbleCounter()+1; i++){
			String name = "Bubble" + i;
			Object3D obj = world.getObjectByName(name);
			if(obj != null){
				RigidBody tempBody = (RigidBody)obj.getUserObject();
				//Log.i("TRANSPARENCY!!!!!!!!!!", "" + tempBody.getLinearVelocity());
				Vector3f linearVelocity = new Vector3f(0,0,0);
				linearVelocity = tempBody.getLinearVelocity(linearVelocity);
				Log.i("LINEAR VELOCITY", "" + linearVelocity);
				SimpleVector motion = new SimpleVector(linearVelocity.x,linearVelocity.y,linearVelocity.z);
				int id = obj.checkForCollision(motion, 10);
				if(id != Object3D.NO_OBJECT){
					int temp = world.getObject(id).getTransparency();
					world.getObject(id).setTransparency(temp + 1);
					world.removeObject(obj);
				}
			}
		}
	}
	
	public void cyclePhysics(){
		if(isPhysics)
			isPhysics = false;
		else
			isPhysics = true;
	}
	
	public Camera getCam(){
		return cam;
	}
	
}
