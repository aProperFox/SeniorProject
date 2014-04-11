package com.inherentgames;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.Window;

import com.inherentgames.BBRoom.Level;
import com.threed.jpct.Logger;

@SuppressLint( "NewApi" )
public class BBTutorial extends Activity {
    
	//private BBAssetsPropertyReader assetsPropertyReader;
	//private Properties config;

	// Library objects
	private GLSurfaceView glView;
	protected BBGame game;
	protected BBRenderer renderer;
	
	// Internal Parameters
	private float xpos = -1;
	private float ypos = -1;
	private float firstX;
	private float firstY;

	private boolean isShootMode = true;

	private long lastPressedWattson = 0;


	private Drawable icon;

	/* ( non-Javadoc )
	 * @see android.app.Activity#onCreate( android.os.Bundle )
	 */
	// Stops Eclipse from complaining about new API calls
	@SuppressWarnings( "deprecation" )
	@SuppressLint( { "InlinedApi", "NewApi" } )
	protected void onCreate( Bundle savedInstanceState ) {
		Logger.log( "onCreate" );

		super.onCreate( savedInstanceState );

		// Remove title bar
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );

		// Enable Immersive mode (hides status and nav bar)
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			BB.setImmersiveMode( findViewById( Window.ID_ANDROID_CONTENT ), getWindow().getDecorView() );
		}

		//assetsPropertyReader = new BBAssetsPropertyReader();
		//config = assetsPropertyReader.getProperties( "config.properties" );

		/*glView.setEGLConfigChooser( new GLSurfaceView.EGLConfigChooser() {

			@Override
			public EGLConfig chooseConfig( EGL10 egl, EGLDisplay display ) {
				//Ensure that we get a 16bit frame buffer. Otherwise we'll fall
				//back to PixelFlinger on some device ( read: Samsung I7500 )
				int[] attributes = new int[] {EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig( display, attributes, configs, 1, result );
				return configs[0];
			}
		} );*/
		
		// Create OpenGL view
		glView = new BBGLView( this );
		// Initialize the OpenGL renderer
		renderer = new BBRenderer();
		// Assign the OpenGL renderer to this view
		glView.setRenderer( renderer );
		
        // Initialize the game object
        game = BBGame.getInstance();
        game.setLevel( Level.TUTORIAL );
        
		setContentView( glView );


		icon = getResources().getDrawable( R.drawable.pause_button_pressed );
		Bitmap bb=( (BitmapDrawable ) icon ).getBitmap();

		int iconWidth = bb.getWidth();
		int iconHeight = bb.getHeight();           

		float scaleWidth = ( (float ) BB.width/8 ) / iconWidth;
		float scaleHeight = ( (float ) BB.width/8 ) / iconHeight;


		Matrix matrix = new Matrix();
		matrix.postScale( scaleWidth, scaleHeight );

		Bitmap resultBitmap = Bitmap.createBitmap( bb, 0, 0, iconWidth, iconHeight, matrix, true );
		icon = new BitmapDrawable( resultBitmap );

	}

	//Keeping this in case we find a better way to get the context menu instead of using alert Dialog
	/*
	@Override
	public void onCreateContextMenu( ContextMenu menu, View v, ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu( menu, v, menuInfo );
		menu.setHeaderTitle( getString( R.string.c_title ) );
		menu.add( 0, v.getId(), 0, getString( R.string.c_resume ) );
		menu.add( 0, v.getId(), 0, getString( R.string.c_restart ) );
		menu.add( 0, v.getId(), 0, getString( R.string.c_exit ) );	
	}
	
	@Override
	public boolean onContextItemSelected( MenuItem item ) {
		if ( item.getTitle()==getString( R.string.c_resume ) ) {
			game.setPauseButtonState();
		}
		else if ( item.getTitle()==getString( R.string.c_restart ) ) {
    	    game.levelLose();
		}
		else if ( item.getTitle()==getString( R.string.c_exit ) ) {
			 Intent intent = new Intent( context, MenuScreen.class );
			 startActivity( intent );
		}
		
		return true;
	}
	*/


	/* ( non-Javadoc )
	 * @see android.app.Activity#onCreateOptionsMenu( android.view.Menu )
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate( R.menu.menu, menu );
	    return true;
	}

	/* ( non-Javadoc )
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		glView.onPause();
	}

	/* ( non-Javadoc )
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		game.setLevel( Level.TUTORIAL );
		glView.onResume();
		// Enable Immersive mode (hides status and nav bar)
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			BB.setImmersiveMode( glView, getWindow().getDecorView() );
		}
	}

	/* ( non-Javadoc )
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
	}

	/* ( non-Javadoc )
	 * @see android.app.Activity#onTouchEvent( android.view.MotionEvent )
	 */
	public boolean onTouchEvent( MotionEvent me ) {
		
		
		/*try {
			Thread.sleep( 10 );
		} catch ( Exception e ) {
			//No need
		}*/
		
		return super.onTouchEvent( me );
	}


	/**
	 * @return
	 */
	protected boolean isFullscreenOpaque() {
		return true;
	}

	@Override
	public void onBackPressed() {
	   Log.d( "Tutorial", "onBackPressed Called" );
	   Intent setIntent = new Intent( BBTutorial.this, BBMenuScreen.class );
	   setIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
	   startActivity( setIntent );
	   BBMenuScreen.ANIMATION = "RIGHT";
	   // Dispose tutorial world, provided it has been loaded
	   game.loading = true;
	}
	
}