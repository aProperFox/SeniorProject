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
import com.threed.jpct.Camera;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Logger;
import com.threed.jpct.SimpleVector;

@SuppressLint( "NewApi" )
public class Tutorial extends Activity {
	private AssetsPropertyReader assetsPropertyReader;
    private Context context;
    private Properties config;
    
	private GLSurfaceView mGLView;
	private BBRenderer renderer = null;
	
	private float xpos = -1;
	private float ypos = -1;
	private float firstX;
	private float firstY;
	
	private boolean isShootMode = true;
	private boolean isViewMode = false;
	private int moveProperties = 5;
	
	private long lastPressedWattson;
	
	private int width;
	private int height;
	
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
		Display display = getWindowManager().getDefaultDisplay();
		
		// Remove title bar
		this.requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		context = this;
		assetsPropertyReader = new AssetsPropertyReader();
		config = assetsPropertyReader.getProperties( "config.properties" );
         
		mGLView = new GLSurfaceView( getApplication() );
		
		// Enable Immersive mode ( hides status and nav bar )
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
	        mGLView.setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
	        this.UiChangeListener();
    	}
		
		// Use legacy code if running on older Android versions
		if ( android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2 ) {
			width = display.getWidth();
			height = display.getHeight();
		} else {
			Point size = new Point();
			display.getRealSize( size );
			width = size.x;
			height = size.y;
		}
		
		mGLView.setEGLConfigChooser( new GLSurfaceView.EGLConfigChooser() {
			
			@Override
			public EGLConfig chooseConfig( EGL10 egl, EGLDisplay display ) {
				//Ensure that we get a 16bit framebuffer. Otherwise we'll fall
				//back to PixelFlinger on some device ( read: Samsung I7500 )
				int[] attributes = new int[] {EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig( display, attributes, configs, 1, result );
				return configs[0];
			}
		} );
		
		renderer = new BBRenderer( width, height, 0 );
		mGLView.setRenderer( renderer );
		mGLView.setKeepScreenOn( true );
		setContentView( mGLView );
		
		
		icon = getResources().getDrawable( R.drawable.pause_button_pressed );
		Bitmap bb=( (BitmapDrawable ) icon ).getBitmap();

		int iconWidth = bb.getWidth();
		int iconHeight = bb.getHeight();           
		  
		float scaleWidth = ( (float ) width/8 ) / iconWidth;
		float scaleHeight = ( (float ) width/8 ) / iconHeight;


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
			if ( xpos < width/5 && xpos > 0 && ypos > 0 && ypos < width/5 ) {
				if ( lastPressedWattson < System.currentTimeMillis() - 1500 ) {
					moveProperties = renderer.iterateWattson();
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
				if ( xpos < ( 3 * width/16 ) && xpos > width/16 && ypos > ( height - ( 3 * width/16 ) ) && ypos < height - width/16 && ( moveProperties == 2 || moveProperties <= 0 ) ) {
					isViewMode = false;
					isShootMode = true;
					renderer.setFireButtonState( true );
				}
				
				else if ( xpos < width && xpos > width-( width/10 ) && ypos > 0 && ypos < width/10 && moveProperties == 0 ) {
					isViewMode = false;
					isShootMode = false;
					renderer.setPauseButtonState();
					final CharSequence[] items = {getString( R.string.c_resume ), getString( R.string.c_settings ), getString( R.string.c_exit )};

					AlertDialog.Builder builder = new AlertDialog.Builder( this );
					builder.setIcon( icon );
					builder.setTitle( getString( R.string.c_title ) );
					builder.setItems( items, new DialogInterface.OnClickListener() {
					    public void onClick( DialogInterface dialog, int item ) {
							if ( items[item]==getString( R.string.c_resume ) ) {
								renderer.setPauseButtonState();
							}
							else if ( items[item]==getString( R.string.c_settings ) ) {
								renderer.setPauseButtonState();
								/*
								Intent intent = new Intent( context, Settings.class );
								startActivity( intent );
								*/
							}
							else if ( items[item]==getString( R.string.c_exit ) ) {
								renderer.restart();
							}
					    }
					} );
					AlertDialog alert = builder.create();
					alert.show();
					
				}
				
				else {
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
				renderer.horizontalSwipe = 0;
				renderer.verticalSwipe = 0;
				isShootMode = false;
				isViewMode = true;
				renderer.setFireButtonState( false );
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
				renderer.horizontalSwipe = 0;
				renderer.verticalSwipe = 0;
				float xd = me.getX( 1 ) - firstX;
				float yd = me.getY( 1 ) - firstY;
				if ( yd < ( -height/5 ) && Math.abs( xd ) < width/6 ) {
					if ( moveProperties != -2 ) {
						renderer.loadBubble( WordObject.MASCULINE );
						moveProperties = renderer.iterateWattson();
						Log.d( "Tutorial", "Blue bubble shot, iterating Wattson" );
					}
					else
						return false;
				}
				else if ( yd > ( height/5 ) && Math.abs( xd ) < width/6 ) {
					if ( moveProperties != -1 ) {
						renderer.loadBubble( WordObject.FEMININE );
						moveProperties = renderer.iterateWattson();
						Log.d( "Tutorial", "Red bubble shot, iterating Wattson" );
					}
					else
						return false;
				}
				else {
					return true;
				}
				Camera cam = renderer.getCam();
				SimpleVector dir = Interact2D.reproject2D3DWS( cam, renderer.getFrameBuffer(), width/2, height/2 );
				dir.scalarMul( -70 );
				RigidBody body = renderer.shoot( cam.getPosition() );
				Log.d( "Tutorial", "Bubble Shot!" );
				if ( body != null ) {
					Vector3f force = new Vector3f( -dir.x*2, dir.y*2, dir.z*2 );
					body.activate( true );
					body.setLinearVelocity( force );
				}
				return true;
			
    		case MotionEvent.ACTION_MOVE:
    			if ( isViewMode ) {
    				xd = me.getX() - xpos;
    				yd = me.getY() - ypos;

    				Camera cam1 = renderer.getCam();
    				SimpleVector dir1 = Interact2D.reproject2D3DWS( cam1, renderer.getFrameBuffer(), width/2, height/2 );
    				if ( isViewMode && moveProperties == 0 ) {
    					renderer.horizontalSwipe = ( xd / -( width/5f ) );
    					renderer.verticalSwipe = ( yd / -( height/5f ) );
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
	
	/**
	 * 
	 */
	public void UiChangeListener() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener ( new View.OnSystemUiVisibilityChangeListener() {
            @TargetApi( 19 )
			@Override
            public void onSystemUiVisibilityChange( int visibility ) {
                if ( (visibility & View.SYSTEM_UI_FLAG_FULLSCREEN ) == 0 ) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
                }
            }
        } );
    }
	
}

