package com.inherentgames;

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
	protected GLSurfaceView glView;
	protected BBRenderer renderer;
	protected BBGame game;
	protected AlertDialog.Builder builder;
	
	private Drawable icon;
	
	// Stops Eclipse from complaining about new API calls
	@SuppressWarnings( "deprecation" )
	@SuppressLint( { "InlinedApi", "NewApi" } )
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		// Remove title bar
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		// Create OpenGL view (which in turn creates the renderer)
		glView = new BBGLView( this );
		// Initialize the renderer
		renderer = new BBRenderer();
		glView.setRenderer( renderer );
		
		// Initialize the game object
	    game = BBGame.getInstance();
		
		// TODO: Figure out what this actually does
		/*mGLView.setEGLConfigChooser( new GLSurfaceView.EGLConfigChooser() {
			
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
		
		// Load stored preferences
		SharedPreferences settings = getSharedPreferences( BBMenuScreen.PREFERENCES, 0 );
		int levelNum = settings.getInt( "loadLevel", 1 );
		Log.i( "GameScreen", "Current level is: " + levelNum );
        
		// Set the activity's content view to the OpenGL view
		setContentView( glView );
		
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
		
		// Define an the pause menu
		final CharSequence[] items = {getString( R.string.c_resume ), getString( R.string.c_settings ), getString( R.string.c_exit )};

		builder = new AlertDialog.Builder( this );
		builder.setIcon( icon );
		builder.setTitle( getString( R.string.c_title ) );
		builder.setItems( items, new DialogInterface.OnClickListener() {
		    public void onClick( DialogInterface dialog, int item ) {
				if ( items[item] == getString( R.string.c_resume ) ) {
					game.setPauseButtonState();
				}
				else if ( items[item] == getString( R.string.c_settings ) ) {
					game.setPauseButtonState();
					/*
					Intent intent = new Intent( context, Settings.class );
					startActivity( intent );
					*/
				}
				else if ( items[item] == getString( R.string.c_exit ) ) {
					finish();
				}
		    }
		} );
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
		glView.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		game.setLevel( Level.values()[getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).getInt( "loadLevel", 1 )] );
		glView.onResume();
		// Enable Immersive mode (hides status and nav bar)
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			BB.setImmersiveMode( glView, getWindow().getDecorView() );
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
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
	   setIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
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