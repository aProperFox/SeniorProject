package com.inherentgames;

import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
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

    @SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void onCreate(){
        super.onCreate();
        
        // Store application context
        BB.context = getApplicationContext();
        
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
