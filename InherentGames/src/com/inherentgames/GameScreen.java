package com.inherentgames;

import java.lang.reflect.Field;
import java.util.Properties;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.vecmath.Vector3f;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

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
	private Toast load;
	
	private Drawable icon;
	
	// Stops Eclipse from complaining about new API calls
	@SuppressWarnings("deprecation")
	@SuppressLint({ "InlinedApi", "NewApi" })
	protected void onCreate(Bundle savedInstanceState) {
		Logger.log("onCreate");
		
		if (master != null){
			copy(master);
		}
		
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		context = this;
		assetsPropertyReader = new AssetsPropertyReader(context);
		config = assetsPropertyReader.getProperties("config.properties");
         
		mGLView = new GLSurfaceView(getApplication());
		
		// Enable Immersive mode (hides status and nav bar)
		if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
	        mGLView.setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	        this.UiChangeListener();
    	}
		
		// Use legacy code if running on older Android versions
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			width = display.getWidth();
			height = display.getHeight();
		} else {
			Point size = new Point();
			display.getRealSize(size);
			width = size.x;
			height = size.y;
		}
		
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
		SharedPreferences settings = getSharedPreferences(MenuScreen.PREFERENCES, 0);
		int levelNum = settings.getInt("loadLevel", 1);
		Log.i("GameScreen", "Current level is: " + levelNum);
		load = Toast.makeText(context, R.string.load_level, Toast.LENGTH_LONG);
        load.show();
		renderer = new MyRenderer(this, width, height, levelNum);
		mGLView.setRenderer(renderer);
		mGLView.setKeepScreenOn(true);
		setContentView(mGLView);
		
		
		icon = getResources().getDrawable(R.drawable.pause_button_pressed);
		Bitmap bb=((BitmapDrawable) icon).getBitmap();

		int iconWidth = bb.getWidth();
		int iconHeight = bb.getHeight();           
		  
		float scaleWidth = ((float) width/8) / iconWidth;
		float scaleHeight = ((float) width/8) / iconHeight;


		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resultBitmap = Bitmap.createBitmap(bb, 0, 0,iconWidth, iconHeight, matrix, true);
		icon = new BitmapDrawable(resultBitmap);
		
	}
	
	//Keeping this in case we find a better way to get the context menu instead of using alert Dialog
	/*
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(getString(R.string.c_title));
		menu.add(0, v.getId(), 0, getString(R.string.c_resume));
		menu.add(0, v.getId(), 0, getString(R.string.c_restart));
		menu.add(0, v.getId(), 0, getString(R.string.c_exit));	
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item){
		if(item.getTitle()==getString(R.string.c_resume)){
			renderer.setPauseButtonState();
		}
		else if(item.getTitle()==getString(R.string.c_restart)){
    	    renderer.levelLose();
		}
		else if(item.getTitle()==getString(R.string.c_exit)){
			 Intent intent = new Intent(context, MenuScreen.class);
			 startActivity(intent);
		}
		
		return true;
	}
	*/
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(MenuScreen.isDevMode){
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.menu, menu);
		}
	    return true;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}
	
	@Override
	protected void onResume(){
		renderer.setRoomNum(getSharedPreferences(MenuScreen.PREFERENCES, 0).getInt("loadLevel", 1));
		load.show();
		super.onResume();
		//renderer.setTextures();
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
				xpos = me.getX(0);
				ypos = me.getY(0);
				if(xpos < (3 * width/16) && xpos > width/16 && ypos > (height - (3 * width/16)) && ypos < height - width/16){
					isViewMode = false;
					isShootMode = true;
					renderer.setFireButtonState(true);
				}
				else if(xpos < width && xpos > width-(width/10) && ypos > 0 && ypos < width/10){
					isViewMode = false;
					isShootMode = false;
					renderer.setPauseButtonState();
					final CharSequence[] items = {getString(R.string.c_resume), getString(R.string.c_settings), getString(R.string.c_exit)};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setIcon(icon);
					builder.setTitle(getString(R.string.c_title));
					builder.setItems(items, new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int item) {
							if(items[item]==getString(R.string.c_resume)){
								renderer.setPauseButtonState();
							}
							else if(items[item]==getString(R.string.c_settings)){
								renderer.setPauseButtonState();
								/*
								Intent intent = new Intent(context, Settings.class);
								startActivity(intent);
								*/
							}
							else if(items[item]==getString(R.string.c_exit)){
								renderer.restart();
							}
					    }
					});
					AlertDialog alert = builder.create();
					alert.show();
					
				}
				
				else{
					isViewMode = true;
					isShootMode = false;
				}
				
				return true;
			
    		case MotionEvent.ACTION_POINTER_DOWN:
				firstX = me.getX(1);
				firstY = me.getY(1);
				isViewMode = false;
				return true;
			
    		case MotionEvent.ACTION_UP:
    			Log.d("GameScreen", "Action Up");
				xpos = -1;
				ypos = -1;
				renderer.setTouchTurn(0);
				renderer.setTouchTurnUp(0);
				isShootMode = false;
				isViewMode = true;
				renderer.setFireButtonState(false);
				return true;
			
    		case MotionEvent.ACTION_POINTER_UP:
    			Log.d("GameScreen", "Action Pointer Up");
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
    			if(isViewMode){
    				xd = me.getX() - xpos;
    				yd = me.getY() - ypos;

    				if(isViewMode){
    					renderer.setTouchTurn(xd / -(width/5f));
    					renderer.setTouchTurnUp(yd / -(height/5f));
    				}
    				xpos = me.getX();
    				ypos = me.getY();
    			}

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
        case R.id.delete_data:
        	getSharedPreferences(MenuScreen.PREFERENCES, 0).edit().remove("hasBeatenTutorial").commit();
        	getSharedPreferences(MenuScreen.PREFERENCES, 0).edit().remove("nextLevel").commit();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	protected boolean isFullscreenOpaque() {
		return true;
	}
	
	@SuppressLint("NewApi")
	public void UiChangeListener() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @TargetApi(19)
			@Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
    }
	
}