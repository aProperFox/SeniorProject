package com.inherentgames;

import com.inherentgames.BBRoom.Level;
import com.inherentgames.BBWordObject.Gender;

import android.app.AlertDialog;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

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
	private long lastPressedWattson = 0;
	
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
		
		if ( !game.loading ) {
			// Handle tutorial separately
			if ( game.level == Level.TUTORIAL ) {
				if ( me.getAction() == MotionEvent.ACTION_DOWN && (game.wattsonPrivileges & 1) != 0 ) {
					xpos = me.getX();
					ypos = me.getY();
					if ( xpos < BB.width/5 && xpos > 0 && ypos > 0 && ypos < BB.width/5 ) {
						if ( game.wattsonPrivileges == 17 ) {
							if ( game.timeIcon.hasFinished ) {
								game.iterateWattson();
							}
						} else {
							game.iterateWattson();
						}

						isShootMode = false;
						return true;
					}
				}
				if ((game.wattsonPrivileges & 1) == 0) {
					switch( me.getAction() & MotionEvent.ACTION_MASK ) {
		
			    		case MotionEvent.ACTION_DOWN:
							xpos = me.getX( 0 );
							ypos = me.getY( 0 );
							// Press fire button
							if ( game.fireButton.includes((int) xpos, (int) ypos) ) {
								if ( game.wattsonPrivileges > 2 ) {
									isShootMode = true;
									game.fireButton.swapState();
								}
							}
							// Press pause button
							else if ( game.pauseButton.includes((int) xpos, (int) ypos)) {
								if ( game.wattsonPrivileges == 14 ) {
									isShootMode = false;
									game.setPauseButtonState();
									gameScreen.showPauseMenu();
								}
			
							}
			
							else {
								isShootMode = false;
							}
			
							return true;
			
			    		case MotionEvent.ACTION_POINTER_DOWN:
			    			if ( isShootMode ) {
								firstX = me.getX( 1 );
								firstY = me.getY( 1 );
			    			}
							return true;
			
			    		case MotionEvent.ACTION_UP:
							xpos = -1;
							ypos = -1;
							game.horizontalSwipe = 0;
							game.verticalSwipe = 0;
							isShootMode = false;
							if ( game.fireButton.isActive() )
								game.fireButton.swapState();
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
			    			Log.d( "BBGLView", "Action Pointer Up" );
			    			
							xpos = -1;
							ypos = -1;
							game.horizontalSwipe = 0;
							game.verticalSwipe = 0;
							float xd = me.getX( 1 ) - firstX;
							float yd = me.getY( 1 ) - firstY;
							if ( isShootMode ){
								if ( yd < ( -BB.height/5 ) && Math.abs( xd ) < BB.width/4 ) {
									// If you can shoot, set the gender
									if ( (game.wattsonPrivileges & 4) != 0 ) {
										game.setGender( BBWordObject.Gender.MASCULINE );
										game.iterateWattson();
				
										Log.d( "Tutorial", "Blue bubble shot, iterating Wattson" );
									}
									else
										return false;
								}
								else if ( yd > ( BB.height/5 ) && Math.abs( xd ) < BB.width/4 ) {
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
							}
							
			
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
				
			// Handle all other levels
			} else {
			
				switch( me.getAction() & MotionEvent.ACTION_MASK ) {
			    	
					// First finger pressed down
		    		case MotionEvent.ACTION_DOWN:
						xpos = me.getX( 0 );
						ypos = me.getY( 0 );
						// Pressed fire button
						if ( game.fireButton.includes((int) xpos, (int) ypos) ) {
							isShootMode = true;
							game.fireButton.swapState();
						}
						// Pressed pause button
						else if ( game.pauseButton.includes((int) xpos, (int) ypos) ) {
							isShootMode = false;
							game.setPauseButtonState();
							gameScreen.showPauseMenu();
							
						// Pressed anywhere else
						} else {
							isShootMode = false;
						}
						return true;
					// Second or later finger pressed down
					case MotionEvent.ACTION_POINTER_DOWN:
						if ( isShootMode ) {
							firstX = me.getX( 1 );
							firstY = me.getY( 1 );
						}
						return true;
					// First finger released
		    		case MotionEvent.ACTION_UP:
		    			Log.d( "GameScreen", "Action Up" );
		    			
						xpos = -1;
						ypos = -1;
						game.horizontalSwipe = 0;
						game.verticalSwipe = 0;
						isShootMode = false;
						if ( game.fireButton.isActive() )
							game.fireButton.swapState();
						return true;
					// Second or later finger released
		    		case MotionEvent.ACTION_POINTER_UP:
		    			Log.d( "GameScreen", "Action Pointer Up" );
		    			Log.d( "BBGLView", "isShootMode: " + isShootMode );
						xpos = -1;
						ypos = -1;
						game.horizontalSwipe = 0;
						game.verticalSwipe = 0;
						float xd = me.getX( 1 ) - firstX;
						float yd = me.getY( 1 ) - firstY;
						if ( isShootMode ) {
							Log.d("BBGLView", "x difference is: " + xd);
							Log.d("BBGLView", "y difference is: " + yd);

							// Swipe up indicates masculine
							if ( yd < ( -BB.height/7 ) && Math.abs( xd ) < BB.width/4 ) {
								game.setGender( Gender.MASCULINE );
							}
							// Swipe down indicates feminine
							else if ( yd > ( BB.height/7 ) && Math.abs( xd ) < BB.width/4 ) {
								game.setGender( Gender.FEMININE );
							}
							// Neither indicates nothing
							else {
								return true;
							}
							// Shoot bubble accordingly
							game.shootBubble();
							
						}
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
			}
		}
		
		return super.onTouchEvent( me );
	}

}
