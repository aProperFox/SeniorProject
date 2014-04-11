package com.inherentgames;

import com.inherentgames.BBWordObject.Gender;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class BBGLView extends GLSurfaceView {
	
	// Library objects
	protected BBGame game;
	private BBGameScreen gameScreen;
	
	// Internal Parameters
	private float xpos = -1;
	private float ypos = -1;
	private float firstX;
	private float firstY;
	// TODO: Need to move this to the BBGame class
	private boolean isShootMode = false;
	private Toast loadingText;
	
	BBGLView( Context context ) {
		super( context );
		// Set OpenGL context to version 2
		setEGLContextClientVersion(2);
		// Prevent screen from turning off (after idling)
		setKeepScreenOn( true );
		
		// Initialize the game object
        game = BBGame.getInstance();
        
        // Initialize reference to game screen activity
        if ( context instanceof BBGameScreen ) {
        	gameScreen = (BBGameScreen) context;
        }
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
					
					AlertDialog alert = gameScreen.builder.create();
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
		
		/*try {
			Thread.sleep( 10 );
		} catch ( Exception e ) {
			//No need
		}*/
		
		return super.onTouchEvent( me );
	}

}
