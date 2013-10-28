package com.inherentgames;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
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
	
	private Light sun = null;
	Context context;
	
	private long time = System.currentTimeMillis();

	public MyRenderer(Context c) {
		context = c;
		V = new SimpleVector(0, 0, 1);
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		if (fb != null) {
			fb.dispose();
		}
		fb = new FrameBuffer(gl, w, h);


		world = new Room(0, context);
		world.setAmbientLight(20, 20, 20);
		
		sun = new Light(world);
		sun.setPosition(world.getLightLocation(0));
		sun.setIntensity(250, 250, 250);

		/*
		Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.ic_launcher)), 64, 64));
		TextureManager.getInstance().addTexture("texture", texture);
		*/
		world.buildAllObjects();
		cam = world.getCamera();
		cam.setPosition(new SimpleVector(0,0,0));
		cam.setOrientation(new SimpleVector(0,0,1), new SimpleVector(0,-1,0));
		MemoryHelper.compact();

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	}
	
	public void setLighting(){
		if(sun.isEnabled()){
			sun.disable();
		}
		else
			sun.enable();
	}

	public void onDrawFrame(GL10 gl) {
		
		if ( touchTurn != 0 || touchTurnUp != 0 ) {
			V.set(cam.getDirection());
			V.rotateY(touchTurn);
			if(cam.getDirection().z < 0)
				V.rotateX(-touchTurnUp);
			else
				V.rotateX(touchTurnUp);
			V.normalize(V);
			cam.lookAt(V);
			
			touchTurn = 0;
			touchTurnUp = 0;
		}
		
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
	
	public void setTouchTurn(float value){
		touchTurn = value;
	}
}
