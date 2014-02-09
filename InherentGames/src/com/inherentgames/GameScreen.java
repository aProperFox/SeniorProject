package com.inherentgames;
import java.lang.reflect.Field;
import java.util.Properties;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.vecmath.Vector3f;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.Window;

import com.bulletphysics.dynamics.RigidBody;
import com.threed.jpct.Camera;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Logger;
import com.threed.jpct.SimpleVector;


public class GameScreen extends Activity {
	private static GameScreen master = null;
	private AssetsPropertyReader assetsPropertyReader;
    private Context context;
    private Properties config;
    
	private GLSurfaceView mGLView;
	private MyRenderer renderer = null;
	
	private float xpos = -1;
	private float ypos = -1;
	private float firstX;
	private float firstY;
	
	private boolean isShootMode = true;
	private boolean isViewMode = false;
	
	private int width;
	private int height;
	
	
	// Stops Eclipse from complaining about new API calls
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	protected void onCreate(Bundle savedInstanceState) {
		Logger.log("onCreate");
		
		if (master != null){
			copy(master);
		}
		
		super.onCreate(savedInstanceState);
		
		Display display = getWindowManager().getDefaultDisplay();
		
		// Use legacy code if running on older Android versions
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			width = display.getWidth();
			height = display.getHeight();
		} else {
			Point size = new Point();
			display.getSize(size);
			width = size.x;
			height = size.y;
		}
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		context = this;
		assetsPropertyReader = new AssetsPropertyReader(context);
		config = assetsPropertyReader.getProperties("config.properties");
         
		mGLView = new GLSurfaceView(getApplication());
		
		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			
			@Override
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				//Ensure that we get a 16bit framebuffer. Otherwise we'll fall
				//back to PixelFlinger on some device (read: Samsung I7500)
				int[] attributes = new int[] {EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});
		
		renderer = new MyRenderer(this, width, height);
		mGLView.setRenderer(renderer);
		mGLView.setKeepScreenOn(true);
		setContentView(mGLView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop(){
		super.onStop();
	}
	
	private void copy(Object src){
		try{
			Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs){
				f.setAccessible(true);
				f.set(this,  f.get(src));
			}
			
		} catch (Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public boolean onTouchEvent(MotionEvent me){
		
		switch(me.getAction() & MotionEvent.ACTION_MASK){
	    	
    		case MotionEvent.ACTION_DOWN:
				xpos = me.getX();
				ypos = me.getY();
				if(xpos < (3 * width/16) && xpos > width/16 && ypos > (height - (3 * width/16)) && ypos < height - width/16){
					isViewMode = false;
					isShootMode = true;
					renderer.setFireButtonState(true);
				}
				else if(xpos < width && xpos > width-(width/10) && ypos > 0 && ypos < width/10){
					isViewMode = false;
					isShootMode = false;
					renderer.setPauseButtonState();
				}
				else{
					isViewMode = true;
					isShootMode = false;
				}
				
				return true;
			
    		case MotionEvent.ACTION_POINTER_DOWN:
				xpos = me.getX(1);
				ypos = me.getY(1);
				firstX = xpos;
				firstY = ypos;
				return true;
			
    		case MotionEvent.ACTION_UP:
				xpos = -1;
				ypos = -1;
				renderer.setTouchTurn(0);
				renderer.setTouchTurnUp(0);
				isShootMode = false;
				isViewMode = true;
				renderer.setFireButtonState(false);
				return true;
			
    		case MotionEvent.ACTION_POINTER_UP:
				xpos = -1;
				ypos = -1;
				renderer.setTouchTurn(0);
				renderer.setTouchTurnUp(0);
				float xd = me.getX(1) - firstX;
				float yd = me.getY(1) - firstY;
				if (yd < (-height/5) && Math.abs(xd) < width/6) {
					renderer.loadBubble(WordObject.MASCULINE);
				}
				else if(yd > (height/5) && Math.abs(xd) < width/6){
					renderer.loadBubble(WordObject.FEMININE);
				}
				else{
					return true;
				}
				Camera cam = renderer.getCam();
				SimpleVector dir = Interact2D.reproject2D3DWS(cam, renderer.getFrameBuffer(), width/2, height/2);
				dir.scalarMul(-70);
				RigidBody body = renderer.shoot(cam.getPosition());
				if(body != null){
					Vector3f force = new Vector3f(-dir.x*2, dir.y*2, dir.z*2);
					body.activate(true);
					body.setLinearVelocity(force);
				}
				return true;
			
    		case MotionEvent.ACTION_MOVE:
				xd = me.getX() - xpos;
				yd = me.getY() - ypos;
				
				if(isViewMode){
					renderer.setTouchTurn(xd / -(width/10f));
					renderer.setTouchTurnUp(yd / -(height/10f));
				}
				xpos = me.getX();
				ypos = me.getY();
				return true;
		
		}
		try {
			Thread.sleep(15);
		} catch (Exception e){
			//No need
		}
		return super.onTouchEvent(me);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.lighting:
        	renderer.cycleLighting();
            return true;
        case R.id.change_level:
        	renderer.levelWin();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	protected boolean isFullscreenOpaque() {
		return true;
	}
	
	
	
	
	
}
