package com.inherentgames;

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
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.bulletphysics.dynamics.RigidBody;
import com.inherentgames.BBWordObject.Gender;
import com.threed.jpct.Camera;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Logger;
import com.threed.jpct.SimpleVector;

@SuppressLint( "NewApi" )
public class BBTutorial extends Activity {
    
	private BBAssetsPropertyReader assetsPropertyReader;
	private Properties config;
	
	private GLSurfaceView mGLView;
	private BBRenderer renderer;
	private BBGame game;
	
	private float xpos = -1;
	private float ypos = -1;
	private float firstX;
	private float firstY;
	
	private boolean isShootMode = true;
	private boolean isViewMode = false;
	private int moveProperties = 5;
	
	private long lastPressedWattson;
	
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
		
		assetsPropertyReader = new BBAssetsPropertyReader();
		config = assetsPropertyReader.getProperties( "config.properties" );
		
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
		
		renderer = new BBRenderer();
		game = BBGame.getInstance();
		mGLView.setRenderer( renderer );
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
		mGLView.onResume();
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
		if ( me.getAction() == MotionEvent.ACTION_DOWN && moveProperties > 0 ) {
			xpos = me.getX();
			ypos = me.getY();
			if ( xpos < BB.width/5 && xpos > 0 && ypos > 0 && ypos < BB.width/5 ) {
				if ( lastPressedWattson < System.currentTimeMillis() - 1500 ) {
					moveProperties = game.iterateWattson();
					lastPressedWattson = System.currentTimeMillis();
					isViewMode = false;
					isShootMode = false;
				}
				return true;
			}
		}
		if ( moveProperties <= 2 ) {
			switch( me.getAction() & MotionEvent.ACTION_MASK ) {
	    	
	    		case MotionEvent.ACTION_DOWN:
					xpos = me.getX( 0 );
					ypos = me.getY( 0 );
					if ( xpos < ( 3 * BB.width/16 ) && xpos > BB.width/16 && ypos > ( BB.height - ( 3 * BB.width/16 ) ) && ypos < BB.height - BB.width/16 && ( moveProperties == 2 || moveProperties <= 0 ) ) {
						isViewMode = false;
						isShootMode = true;
						game.setFireButtonState( true );
					} else if ( xpos < BB.width && xpos > BB.width-( BB.width/10 ) && ypos > 0 && ypos < BB.width/10 && moveProperties == 0 ) {
						isViewMode = false;
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
						
					} else {
						isViewMode = true;
						isShootMode = false;
					}
					
					return true;
				
	    		case MotionEvent.ACTION_POINTER_DOWN:
					firstX = me.getX( 1 );
					firstY = me.getY( 1 );
					isViewMode = false;
					return true;
				
	    		case MotionEvent.ACTION_UP:
					xpos = -1;
					ypos = -1;
					game.horizontalSwipe = 0;
					game.verticalSwipe = 0;
					isShootMode = false;
					isViewMode = true;
					game.setFireButtonState( false );
					return true;
				
	    		case MotionEvent.ACTION_POINTER_UP:
	    			/*
	    			 * May work to get exact screen size in inches
	    			 * 
	    			DisplayMetrics dm = new DisplayMetrics();
	    		    getWindowManager().getDefaultDisplay().getMetrics( dm );
	    		    double x = Math.pow( dm.widthPixels/dm.xdpi, 2 );
	    		    double y = Math.pow( dm.heightPixels/dm.ydpi, 2 );
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
						if ( moveProperties != -2 ) {
							game.setGender( Gender.MASCULINE );
							moveProperties = game.iterateWattson();
							Log.d( "Tutorial", "Blue bubble shot, iterating Wattson" );
						}
						else
							return false;
					}
					else if ( yd > ( BB.height/5 ) && Math.abs( xd ) < BB.width/6 ) {
						if ( moveProperties != -1 ) {
							game.setGender( Gender.FEMININE );
							moveProperties = game.iterateWattson();
							Log.d( "Tutorial", "Red bubble shot, iterating Wattson" );
						}
						else
							return false;
					}
					else {
						return true;
					}
					game.shootBubble();
					Log.d( "Tutorial", "Bubble Shot!" );
					return true;
				
	    		case MotionEvent.ACTION_MOVE:
	    			if ( isViewMode ) {
	    				xd = me.getX() - xpos;
	    				yd = me.getY() - ypos;
	
	    				Camera cam1 = game.getCam();
	    				SimpleVector dir1 = Interact2D.reproject2D3DWS( cam1, renderer.getFrameBuffer(), BB.width/2, BB.height/2 );
	    				if ( isViewMode && moveProperties == 0 ) {
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
			Thread.sleep( 15 );
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

