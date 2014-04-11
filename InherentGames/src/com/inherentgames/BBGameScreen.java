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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.inherentgames.BBRoom.Level;
import com.inherentgames.BBWordObject.Gender;


public class BBGameScreen extends Activity {
    
	// Library objects
	private GLSurfaceView mGLView;
	protected BBGame game;
	protected BBRenderer renderer = null;
	
	// Internal Parameters
	private float xpos = -1;
	private float ypos = -1;
	private float firstX;
	private float firstY;
	// TODO: Need to move this to the BBGame class
	private boolean isShootMode = false;
	private Toast loadingText;
	
	private Drawable icon;
	
	// Stops Eclipse from complaining about new API calls
	@SuppressWarnings( "deprecation" )
	@SuppressLint( { "InlinedApi", "NewApi" } )
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		// Remove title bar
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		// Create OpenGL view
		mGLView = new GLSurfaceView( getApplication() );
		
		// TODO: Figure out what this actually does
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
		
		// Load stored preferences
		SharedPreferences settings = getSharedPreferences( BBMenuScreen.PREFERENCES, 0 );
		int levelNum = settings.getInt( "loadLevel", 1 );
		Log.i( "GameScreen", "Current level is: " + levelNum );
        
        // Initialize the game object
        game = BBGame.getInstance();
        // Initialize the OpenGL renderer
		renderer = new BBRenderer();
		// Assign the OpenGL renderer to this view
		mGLView.setRenderer( renderer );
		// Prevent screen from turning off (after idling)
		mGLView.setKeepScreenOn( true );
		setContentView( mGLView );
		
		// TODO: Figure out what this code actually does and why it's in this section of the class
		icon = getResources().getDrawable( R.drawable.pause_button_pressed );
		Bitmap bb = ((BitmapDrawable) icon).getBitmap();

		int iconWidth = bb.getWidth();
		int iconHeight = bb.getHeight();           
		  
		float scaleWidth = ((float) BB.width / 8) / iconWidth;
		float scaleHeight = ((float) BB.width / 8) / iconHeight;

		Matrix matrix = new Matrix();
		matrix.postScale( scaleWidth, scaleHeight );

		Bitmap resultBitmap = Bitmap.createBitmap( bb, 0, 0, iconWidth, iconHeight, matrix, true );
		// TODO: Do something about the deprecated stuff
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
			renderer.setPauseButtonState();
		}
		else if ( item.getTitle()==getString( R.string.c_restart ) ) {
    	    renderer.levelLose();
		}
		else if ( item.getTitle()==getString( R.string.c_exit ) ) {
			 Intent intent = new Intent( context, MenuScreen.class );
			 startActivity( intent );
		}
		
		return true;
	}
	*/
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		if ( BBMenuScreen.isDevMode ) {
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate( R.menu.menu, menu );
		}
	    return true;
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		game.setLevel( Level.values()[getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).getInt( "loadLevel", 1 )] );
		mGLView.onResume();
		// Enable Immersive mode (hides status and nav bar)
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			BB.setImmersiveMode( mGLView, getWindow().getDecorView() );
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	// Handle touch events during the game
	public boolean onTouchEvent( MotionEvent me ) {
		switch( me.getAction() & MotionEvent.ACTION_MASK ) {
	    	
			// First finger pressed down
    		case MotionEvent.ACTION_DOWN:
				xpos = me.getX( 0 );
				ypos = me.getY( 0 );
				// Pressed fire button
				if ( xpos < ( 3 * BB.width/16 ) && xpos > BB.width/16 && ypos > ( BB.height - ( 3 * BB.width/16 ) ) && ypos < BB.height - BB.width/16 ) {
					isShootMode = true;
					game.setFireButtonState( true );
				}
				// Pressed pause button
				else if ( xpos < BB.width && xpos > BB.width-( BB.width/10 ) && ypos > 0 && ypos < BB.width/10 ) {
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
					
				// Pressed anywhere else
				} else {
					isShootMode = false;
				}
				return true;
			// Second or later finger pressed down
			case MotionEvent.ACTION_POINTER_DOWN:
				firstX = me.getX( 1 );
				firstY = me.getY( 1 );
				isShootMode = true;
				return true;
			// First finger released
    		case MotionEvent.ACTION_UP:
    			Log.d( "GameScreen", "Action Up" );
				xpos = -1;
				ypos = -1;
				game.horizontalSwipe = 0;
				game.verticalSwipe = 0;
				isShootMode = false;
				game.setFireButtonState( false );
				return true;
			// Second or later finger released
    		case MotionEvent.ACTION_POINTER_UP:
    			Log.d( "GameScreen", "Action Pointer Up" );
				xpos = -1;
				ypos = -1;
				game.horizontalSwipe = 0;
				game.verticalSwipe = 0;
				float xd = me.getX( 1 ) - firstX;
				float yd = me.getY( 1 ) - firstY;
				// Swipe up indicates masculine
				if ( yd < ( -BB.height/7 ) && Math.abs( xd ) < BB.width/6 ) {
					game.setGender( Gender.MASCULINE );
				}
				// Swipe down indicates feminine
				else if ( yd > ( BB.height/7 ) && Math.abs( xd ) < BB.width/6 ) {
					game.setGender( Gender.FEMININE );
				}
				// Neither indicates nothing
				else {
					return true;
				}
				// Shoot bubble accordingly
				game.shootBubble();
				return true;
			// Finger moved
			// Note: Here, we're only using this event for panning around, not for swiping up/down to shoot bubbles
    		case MotionEvent.ACTION_MOVE:
    			if ( !isShootMode ) {
    				xd = me.getX() - xpos;
    				yd = me.getY() - ypos;

					game.horizontalSwipe = ( xd / -( BB.width/5f ) );
					game.verticalSwipe = ( yd / -( BB.height/5f ) );
					
    				xpos = me.getX();
    				ypos = me.getY();
    			}

				return true;
		
		}
		try {
			Thread.sleep( 10 );
		} catch ( Exception e ) {
			//No need
		}
		return super.onTouchEvent( me );
	}
	
	@Override
	// Debugging purposes
    public boolean onOptionsItemSelected( MenuItem item ) {
		try {
	        switch ( item.getItemId() ) {
		        case R.id.inc_object_x:
		        	game.world.getObject( game._currentObjectId ).translate( 1, 0, 0 );
		        	break;
		        case R.id.inc_object_z:
		        	game.world.getObject( game._currentObjectId ).translate( 0, 0, 1 );
		        	break;
		        case R.id.dec_object_x:
		        	game.world.getObject( game._currentObjectId ).translate( -1, 0, 0 );
		        	break;
		        case R.id.dec_object_z:
		        	game.world.getObject( game._currentObjectId ).translate( 0, 0, -1 );
		        	break;
		        case R.id.inc_object_y:
		        	game.world.getObject( game._currentObjectId ).translate( 0, 1, 0 );
		        	break;
		        case R.id.dec_object_y:
		        	game.world.getObject( game._currentObjectId ).translate( 0, -1, 0 );
		        	return true;
		        case R.id.inc_obj:
		        	game._currentObjectId = game.objects.nextElement().getID();
		        	Log.d( "GameScreen", "New Object is: " + game.world.getObject( game._currentObjectId ).getName() );
		        	return true;
	        }
	        Log.d( "GameScreen", "New object location: " + game.world.getObject( game._currentObjectId ).getTranslation() );
		}catch ( Exception e ) {
			e.printStackTrace();
		}
        return super.onOptionsItemSelected( item );
    }
	
	@Override
	public void onBackPressed() {
	   Log.d( "Tutorial", "onBackPressed Called" );
	   Intent setIntent = new Intent( BBGameScreen.this, BBMapScreen.class );
	   setIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP );
	   startActivity( setIntent );
	   BBMenuScreen.ANIMATION = "DOWN";
	   // Dispose tutorial world, provided it has been loaded
	   game.loading = true;
	}
	
	// Used to indicate to Android system to perform an optimization
	protected boolean isFullscreenOpaque() {
		return true;
	}
	
}