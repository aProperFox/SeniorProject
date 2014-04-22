package com.inherentgames;

import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class BB extends Application {

    protected static Context context;
    protected static int width;
    protected static int height;
	
	public static final String EXTRA_MESSAGE = "VIDEO VALUE";
	public static String ANIMATION = "DOWN";
	public static final String PREFERENCES = "BABBLE_PREF";
	public static final boolean isDevMode = false;
	public static boolean isSponsorMode = true;
	public static boolean isTimeLimitenabled;
	
	public static int buttonWidth;
	public static int buttonHeight;
	// Empty set used as default for "playedComics"
	public static Set<String> EMPTYSET;
	
    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void onCreate(){
        super.onCreate();
        
        // Store application context
        BB.context = getApplicationContext();
        
        // Create empty set
        EMPTYSET = new HashSet<String>();
        
     // Ensure not both menus can be inflated
        if ( isDevMode ) {
        	isSponsorMode = false;
        	isTimeLimitenabled = false;
        } else {
            // Turn on time limit by default
        	isTimeLimitenabled = true;
        }
        
        // Determine and store device width & height
        Display display = ((WindowManager) BB.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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
		
		
		// Set button width and height variables
		buttonWidth = (int) (BB.width / 4.9f);
		buttonHeight = (int) (BB.height / 8.7f);
		
    }
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
	public static void setImmersiveMode( final View activityView, final View decorView ) {
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			activityView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
			activityView.setOnSystemUiVisibilityChangeListener ( new View.OnSystemUiVisibilityChangeListener() {
	            public void onSystemUiVisibilityChange( int visibility ) {
	            	Log.e( "UI", "activityView Visibility changed" );
	                if ( (visibility & View.SYSTEM_UI_FLAG_FULLSCREEN ) == 0 ) {
	                    activityView.setSystemUiVisibility(
	                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                            | View.SYSTEM_UI_FLAG_FULLSCREEN
	                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
	                }
	            }
	        } );
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_FULLSCREEN
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
			decorView.setOnSystemUiVisibilityChangeListener ( new View.OnSystemUiVisibilityChangeListener() {
	            public void onSystemUiVisibilityChange( int visibility ) {
	            	Log.e( "UI", "decorView Visibility changed" );
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
}
