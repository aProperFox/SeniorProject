package com.inherentgames;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MapScreen extends Activity {

	Button stage1;
	ImageButton stage2;
	Button stage3;
	
	int width, height;
	int levelNum;
	
	@SuppressWarnings( "deprecation" )
	@SuppressLint( "NewApi" )
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		
		Display display = getWindowManager().getDefaultDisplay();
		// Use legacy code if running on older Android versions
		if ( android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2 ) {
			width = display.getWidth();
			height = display.getHeight();
		} else {
			Point size = new Point();
			display.getSize( size );
			width = size.x;
			height = size.y;
		}
		

		SharedPreferences settings = getSharedPreferences( MenuScreen.PREFERENCES, 0 );
		levelNum = settings.getInt( "nextLevel", 1 );
		switch( levelNum ) {
			case 1:
				Log.i( "MapScreen", "loading Level 1 map" );
				setContentView( R.layout.stageone );
				break;
			case 2:
				Log.i( "MapScreen", "loading Level 2 map" );
				setContentView( R.layout.stagetwo );
				break;
			case 3:
				Log.i( "MapScreen", "Loading Level 3 map" );
				setContentView( R.layout.stagethree );
				break;
		}
	}
	
	@SuppressLint("InlinedApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onResume() {
		super.onResume();
		
		// Enable Immersive mode ( hides status and nav bar )
		View currentView = getWindow().getDecorView();
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			currentView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
		    this.UiChangeListener();
		}
	}
	
	public boolean onTouchEvent( MotionEvent me ) {
		float xpos = me.getX();
		float ypos = me.getY();
		if ( me.getAction() == MotionEvent.ACTION_DOWN ) {
			if ( xpos > width*.13 && xpos < width*.33 && ypos > height*0.162 && ypos < height*0.4 ) {
				getSharedPreferences( MenuScreen.PREFERENCES, 0 ).edit().putInt( "loadLevel", 1 ).commit();
                Intent i = new Intent( MapScreen.this, VideoScreen.class );
                i.putExtra( MenuScreen.EXTRA_MESSAGE, "comic1a" );
                startActivity( i );
                finish();
			}
			
			else if ( xpos > width*.4 && xpos < width*.6 && ypos > height*0.618 && ypos < height*0.88 ) {
				if ( levelNum > 1 ) {
					getSharedPreferences( MenuScreen.PREFERENCES, 0 ).edit().putInt( "loadLevel", 2 ).commit();
                    Intent i = new Intent( MapScreen.this, VideoScreen.class );
                    i.putExtra( MenuScreen.EXTRA_MESSAGE, "comic2a" );
                    startActivity( i );
                    finish();
				}
			}
			
			else if ( xpos > width*.675 && xpos < width*.88 && ypos > height*0.176 && ypos < height*0.44 ) {
				if ( levelNum > 2 ) {
					getSharedPreferences( MenuScreen.PREFERENCES, 0 ).edit().putInt( "loadLevel", 3 ).commit();
                    Intent i = new Intent( MapScreen.this, GameScreen.class );
                    startActivity( i );
                    finish();
				}
			}
			
			
		}
		return true;
	}
	
	
	@Override
	public void onBackPressed() {
	   Log.d( "MapScreen", "onBackPressed Called" );
	   Intent setIntent = new Intent( this, MenuScreen.class );
	   setIntent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
	   startActivity( setIntent );
	}
	
	@SuppressLint("InlinedApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
