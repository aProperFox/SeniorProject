package com.inherentgames;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.inherentgames.BBRoom.Level;


public class BBGameScreen extends Activity {
    
	// Library objects
	protected GLSurfaceView glView;
	protected BBRenderer renderer;
	protected BBGame game;
	protected Dialog pauseDialog;
	protected Dialog endDialog;
	protected Dialog loseDialog;
	private Drawable icon;
	
	private Button pauseResumeButton, pauseSettingsButton, pauseExitButton, pauseResetButton;
	private Button endContinueButton, endSettingsButton, endExitButton, endResetButton;
	private View endView;
	
	// Internal parameters
	private boolean isTutorial = false;
	
	// Stops Eclipse from complaining about new API calls
	@SuppressLint( { "InlinedApi", "NewApi" } )
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		// Remove title bar
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		// Create OpenGL view
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
		SharedPreferences settings = getSharedPreferences( BB.PREFERENCES, 0 );
		int levelNum = settings.getInt( "loadLevel", 1 );
		Log.i( "GameScreen", "Current level is: " + levelNum );
        
		// So much code just to create an icon
		icon = BB.context.getResources().getDrawable( R.drawable.pause_button_pressed );
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
		
		// Define the pause menu
		LayoutInflater inflater = LayoutInflater.from(BBGameScreen.this);
        View pauseView = inflater.inflate(R.layout.pause_popup, null);
		pauseView.setMinimumWidth( 2 * BB.width / 3 );
		pauseView.setMinimumHeight( 2 * BB.height / 3 );
        
		pauseDialog = new Dialog( BBGameScreen.this, android.R.style.Theme_Translucent );
		pauseDialog.getWindow().setLayout( 2 * BB.width / 3, 2 * BB.height / 3 );
		pauseDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
		pauseDialog.getWindow().addFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND );
		pauseDialog.setContentView( pauseView );
		pauseDialog.getWindow().getAttributes().dimAmount = 0.5f;  
		
		pauseDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    game.setPauseButtonState();
                    pauseDialog.cancel();
                }
                return true;
            }
        });
		
		// Resume button
		pauseResumeButton = ( Button ) pauseDialog.findViewById( R.id.resume_button );
		pauseResumeButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		        	game.setPauseButtonState();
					pauseDialog.cancel();
		        }
		} );
		
		// Settings button
		pauseSettingsButton = ( Button ) pauseDialog.findViewById( R.id.settings_button );
		pauseSettingsButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		            Intent i = new Intent( BBGameScreen.this, BBSettings.class );
		            pauseDialog.cancel();
		            startActivity( i );
		            overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );
		        }
		} );
		
		// Reset button
		pauseResetButton = ( Button ) pauseDialog.findViewById( R.id.reset_button );
		pauseResetButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		            game.resetLevel();
		            game.setPauseButtonState();
					pauseDialog.cancel();
		        }
		} );
		
		// Exit button
		pauseExitButton = ( Button ) pauseDialog.findViewById( R.id.exit_button );
		pauseExitButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		        	pauseDialog.cancel();
		            finish();
		        }
		} );

		// Define the pause menu
        endView = inflater.inflate(R.layout.end_popup, null);
        endView.setMinimumWidth( BB.width / 2 );
        endView.setMinimumHeight( 7 * BB.height / 8 );
        
		endDialog = new Dialog( BBGameScreen.this, android.R.style.Theme_Translucent );
		endDialog.getWindow().setLayout( BB.width / 2, 7 * BB.height / 8 );
		endDialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
		endDialog.getWindow().addFlags( WindowManager.LayoutParams.FLAG_DIM_BEHIND );
		endDialog.setContentView( endView );
		endDialog.getWindow().getAttributes().dimAmount = 0.5f;  
		
		endDialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                    KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                }
                return true;
            }
        });

		// Resume button
		endContinueButton = ( Button ) endDialog.findViewById( R.id.resume_button );
		endContinueButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		        	if ( BB.context.getSharedPreferences( BB.PREFERENCES, 0).getStringSet( "playedComics", 
	                		BB.EMPTYSET).contains("comic" + ( game.level.ordinal() - 1) + "b") ) {
	                	Intent intent = new Intent( BB.context, BBGameScreen.class );
		        	    intent.setClass( BB.context, BBMapScreen.class );
		        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		        	    game.world.dispose();
		        	    endDialog.cancel();
		        	    BB.context.startActivity( intent );
		        	    game.loading = true;
	                } else {
		        		Intent intent = new Intent( BB.context, BBGameScreen.class );
		        	    intent.setClass( BB.context, BBVideoScreen.class );
		        	    intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		        	    intent.putExtra( BB.EXTRA_MESSAGE, "comic" + ( game.level.ordinal() - 1 ) + "b" );
		        	    game.world.dispose();
		        	    endDialog.cancel();
		        	    BB.context.startActivity( intent );
		        	    game.loading = true;
	                }
		        }
		} );
		
		// Settings button
		endSettingsButton = ( Button ) endDialog.findViewById( R.id.settings_button );
		endSettingsButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		            Intent i = new Intent( BBGameScreen.this, BBSettings.class );
		            endDialog.cancel();
		            startActivity( i );
		            overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );
		        }
		} );
		
		// Reset button
		endResetButton = ( Button ) endDialog.findViewById( R.id.reset_button );
		endResetButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		            game.resetLevel();
		            game.setPauseButtonState();
					endDialog.cancel();
		        }
		} );
		
		// Exit button
		endExitButton = ( Button ) endDialog.findViewById( R.id.exit_button );
		endExitButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		        	endDialog.cancel();
		        	finish();
		        }
		} );
		
		
		// Set the activity's content view to the OpenGL view
		setContentView( glView );
		
	}

	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		if ( BB.isDevMode ) {
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate( R.menu.dev_menu, menu );
		} else if ( BB.isSponsorMode ) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate( R.menu.sponsor_menu, menu );
			if ( BB.isTimeLimitenabled ) {
				menu.getItem(1).setTitle( R.string.time_limit_enabled );
			} else {
				menu.getItem(1).setTitle( R.string.time_limit_disabled );
			}
		}
	    return true;
	}
	
	// Modifying to ensure time limit option is always correct when menu selected
	@Override
	public boolean onPrepareOptionsMenu( Menu menu ) {
		if ( BB.isTimeLimitenabled ) {
			menu.getItem(1).setTitle( R.string.time_limit_enabled );
		} else {
			menu.getItem(1).setTitle( R.string.time_limit_disabled );
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
		
		// Load appropriate level (or tutorial)
		isTutorial = getIntent().getBooleanExtra( "tutorial", false ); 
        if ( isTutorial ) {
        	game.setLevel( Level.TUTORIAL );
        	game.isTutorial = true;
        } else {
        	game.setLevel( Level.values()[getSharedPreferences( BB.PREFERENCES, 0 ).getInt( "loadLevel", 1 )] );
        }
        
        // TODO: make this less horribly efficient
        // Ensure tutorial is not loaded again
        renderer = new BBRenderer();
        
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
			game.world.getObject( game._currentObjectId ).setAdditionalColor( 255, 255, 0);
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
		        	game.world.getObject( game._currentObjectId ).clearAdditionalColor();
		        	game._currentObjectId = game.objects.nextElement().getID();
		        	game.world.getObject( game._currentObjectId ).setAdditionalColor( 255, 255, 0 );
		        	Log.d( "GameScreen", "New Object is: " + game.world.getObject( game._currentObjectId ).getName() );
		        	return true;
		        case R.id.swap_time:
		        	BB.isTimeLimitenabled = !BB.isTimeLimitenabled;
		        	// Change menu item string
		        	if ( BB.isTimeLimitenabled ) {
		        		item.setTitle( R.string.time_limit_enabled );
		        		game.endTime = System.currentTimeMillis() + (game.timeLeft*1000);
		        	} else {
		        		item.setTitle( R.string.time_limit_disabled );
		        	}
		        	return true;
		        case R.id.reset:
		        	game.resetLevel();
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
		
		if ( game.loading ) {
			
		} else {
			Log.d( "BBGameScreen", "onBackPressed Called" );
			BB.ANIMATION = "DOWN";
			if ( isTutorial ) {
				finish();
			} else {
				/*
				Intent setIntent = new Intent( BBGameScreen.this, BBMapScreen.class );
				setIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
				startActivity( setIntent );
				// Dispose tutorial world, provided it has been loaded
				game.loading = true;*/
				finish();
			}
		}

	}
	
	// Used to indicate to Android system to perform an optimization
	protected boolean isFullscreenOpaque() {
		return true;
	}
	
	protected void showPauseMenu() {
		pauseDialog.show();
	}
	
	protected void showEndMenu( boolean won ) {
		if ( won ) {
			endView.setBackgroundResource( R.drawable.win_game );
		} else {
			endView.setBackgroundResource( R.drawable.lose_game );
		}
		endDialog.show();
	}


	
}