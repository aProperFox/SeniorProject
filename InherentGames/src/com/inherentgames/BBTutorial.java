package com.inherentgames;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
	private GLSurfaceView mGLView;
	protected BBGame game;
	protected BBRenderer renderer = null;
	
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
         
		mGLView = new GLSurfaceView( getApplication() );

		// Enable Immersive mode (hides status and nav bar)
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			BB.setImmersiveMode( findViewById( Window.ID_ANDROID_CONTENT ), getWindow().getDecorView() );
		}

		//assetsPropertyReader = new BBAssetsPropertyReader();
		//config = assetsPropertyReader.getProperties( "config.properties" );

		mGLView.setEGLConfigChooser( new GLSurfaceView.EGLConfigChooser() {

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
		} );
		
        // Initialize the game object
        game = BBGame.getInstance();
        game.setLevel(Level.TUTORIAL);
        // Initialize the OpenGL renderer
		renderer = new BBRenderer();
		// Assign the OpenGL renderer to this view
		mGLView.setRenderer( renderer );
		// Prevent screen from turning off (after idling)
		mGLView.setKeepScreenOn( true );
		setContentView( mGLView );


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
		mGLView.onPause();
	}

	/* ( non-Javadoc )
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		game.setLevel( Level.TUTORIAL );
		mGLView.onResume();
		// Enable Immersive mode (hides status and nav bar)
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			BB.setImmersiveMode( mGLView, getWindow().getDecorView() );
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
		if ( me.getAction() == MotionEvent.ACTION_DOWN && (game.wattsonPrivileges & 1) != 0 ) {
			xpos = me.getX();
			ypos = me.getY();
			if ( xpos < BB.width/5 && xpos > 0 && ypos > 0 && ypos < BB.width/5 ) {

				game.iterateWattson();
				isShootMode = false;
				return true;
			}
		}
		if ((game.wattsonPrivileges & 1) == 0) {
			switch( me.getAction() & MotionEvent.ACTION_MASK ) {

    		case MotionEvent.ACTION_DOWN:
				xpos = me.getX( 0 );
				ypos = me.getY( 0 );
				//press fire button
				if ( xpos < ( 3 * BB.width/16 ) && xpos > BB.width/16 && ypos > ( BB.height - ( 3 * BB.width/16 ) ) && ypos < BB.height - BB.width/16 && (game.wattsonPrivileges & 12) != 0 ) {
					isShootMode = true;
					game.setFireButtonState( true );
				}
				//press pause button
				else if ( xpos < BB.width && xpos > BB.width-( BB.width/10 ) && ypos > 0 && ypos < BB.width/10) {
					isShootMode = false;
					game.setPauseButtonState();
					final CharSequence[] items = {getString( R.string.c_resume ), getString( R.string.c_settings ), getString( R.string.c_exit )};

					AlertDialog.Builder builder = new AlertDialog.Builder( this );
					builder.setIcon( icon );
					builder.setTitle( getString( R.string.c_title ) );
					builder.setItems( items, new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int item ) {
							if ( items[item]==getString( R.string.c_resume ) ) {
								game.setPauseButtonState();
							}
							else if ( items[item]==getString( R.string.c_settings ) ) {
								game.setPauseButtonState();
								/*
								Intent intent = new Intent( context, Settings.class );
								startActivity( intent );
								*/
							}
							else if ( items[item]==getString( R.string.c_exit ) ) {
								finish();
							}
					    }
					} );
					AlertDialog alert = builder.create();
					alert.show();

				}

				else {
					isShootMode = false;
				}

				return true;

    		case MotionEvent.ACTION_POINTER_DOWN:
				firstX = me.getX( 1 );
				firstY = me.getY( 1 );
				return true;

    		case MotionEvent.ACTION_UP:
				xpos = -1;
				ypos = -1;
				game.horizontalSwipe = 0;
				game.verticalSwipe = 0;
				isShootMode = false;
				game.setFireButtonState( false );
				return true;

    		case MotionEvent.ACTION_POINTER_UP:
    			/*
    			 * May work to get exact screen size in inches
    			 * 
    			DisplayMetrics dm = new DisplayMetrics();
    		    getWindowManager().getDefaultDisplay().getMetrics( dm );
    		    double x = Math.pow( dm.BB.widthPixels/dm.xdpi, 2 );
    		    double y = Math.pow( dm.BB.heightPixels/dm.ydpi, 2 );
    		    double screenInches = Math.sqrt( x+y );
    			*/
    			Log.d( "GameScreen", "Action Pointer Up" );
				xpos = -1;
				ypos = -1;
				game.horizontalSwipe = 0;
				game.verticalSwipe = 0;
				float xd = me.getX( 1 ) - firstX;
				float yd = me.getY( 1 ) - firstY;
				if ( yd < ( -BB.height/5 ) && Math.abs( xd ) < BB.width/6 ) {
					// If you can shoot, set the gender
					if ( (game.wattsonPrivileges & 4) != 0 ) {
						game.setGender( BBWordObject.Gender.MASCULINE );
						game.iterateWattson();

						Log.d( "Tutorial", "Blue bubble shot, iterating Wattson" );
					}
					else
						return false;
				}
				else if ( yd > ( BB.height/5 ) && Math.abs( xd ) < BB.width/6 ) {
					// If you can shoot, set the gender
					if ( (game.wattsonPrivileges & 8) != 0 ) {
						game.setGender( BBWordObject.Gender.FEMININE );
						game.iterateWattson();
						Log.d( "Tutorial", "Red bubble shot, iterating Wattson" );
					}
					else
						return false;
				}
				else {
					return true;
				}
				game.shootBubble();

				return true;

    		case MotionEvent.ACTION_MOVE:
    			if ( !isShootMode ) {
    				xd = me.getX() - xpos;
    				yd = me.getY() - ypos;
    				
    				if( (game.wattsonPrivileges & 2) != 0  && lastPressedWattson == 0){
						lastPressedWattson = System.currentTimeMillis();
					}
    				
    				if(lastPressedWattson != 0 && lastPressedWattson < System.currentTimeMillis() - 3000){
    					game.iterateWattson();
    					lastPressedWattson = 0;
    				}
    				if( (game.wattsonPrivileges & 2) != 0 ){
	    				game.horizontalSwipe = ( xd / -( BB.width/5f ) );
						game.verticalSwipe = ( yd / -( BB.height/5f ) );
    				}
					
    				xpos = me.getX();
    				ypos = me.getY();
    			}

				return true;

		}
		}
		try {
			Thread.sleep( 10 );
		} catch ( Exception e ) {
			//No need
		}
		return super.onTouchEvent( me );
	}


	/**
	 * @return
	 */
	protected boolean isFullscreenOpaque() {
		return true;
	}

}