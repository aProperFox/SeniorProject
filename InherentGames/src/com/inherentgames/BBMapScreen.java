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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

@SuppressLint("NewApi")
public class BBMapScreen extends Activity {

	Button stage1;
	ImageButton stage2;
	Button stage3;
	
	int levelNum;
	
	@SuppressWarnings( "deprecation" )
	@SuppressLint( "NewApi" )
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

        BBGame game = BBGame.getInstance();
        game.loading = true;
		
		SharedPreferences settings = getSharedPreferences( BBMenuScreen.PREFERENCES, 0 );
		levelNum = settings.getInt( "nextLevel", 1 );
		switch( levelNum ) {
			case 1:
				Log.i( "MapScreen", "loading Level 1 map" );
				setContentView( R.layout.stage1 );
				break;
			case 2:
				Log.i( "MapScreen", "loading Level 2 map" );
				setContentView( R.layout.stage2 );
				break;
			case 3:
				Log.i( "MapScreen", "Loading Level 3 map" );
				setContentView( R.layout.stage3 );
				break;
		}
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
	}
	
	@SuppressLint("InlinedApi")
	public void onResume() {
		super.onResume();
		
		// Check for animation message
		if ( BBMenuScreen.ANIMATION == "LEFT" ){
			overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
		} else if ( BBMenuScreen.ANIMATION == "RIGHT" ) {
			overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
		} else if ( BBMenuScreen.ANIMATION == "DOWN" ) {
			overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );
		} else {
			overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
		}
		
		// Enable Immersive mode (hides status and nav bar)
		if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
			BB.setImmersiveMode( findViewById( Window.ID_ANDROID_CONTENT ), getWindow().getDecorView() );
		}
	}
	
	public boolean onTouchEvent( MotionEvent me ) {
		float xpos = me.getX();
		float ypos = me.getY();
		if ( me.getAction() == MotionEvent.ACTION_DOWN ) {
			if ( xpos > BB.width*.13 && xpos < BB.width*.33 && ypos > BB.height*0.162 && ypos < BB.height*0.4 ) {
				getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).edit().putInt( "loadLevel", 1 ).commit();
                Intent i = new Intent( BBMapScreen.this, BBVideoScreen.class );
                i.putExtra( BBMenuScreen.EXTRA_MESSAGE, "comic1a" );
                startActivity( i );
                finish();
			}
			
			else if ( xpos > BB.width*.4 && xpos < BB.width*.6 && ypos > BB.height*0.618 && ypos < BB.height*0.88 ) {
				if ( levelNum > 1 ) {
					getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).edit().putInt( "loadLevel", 2 ).commit();
                    Intent i = new Intent( BBMapScreen.this, BBVideoScreen.class );
                    i.putExtra( BBMenuScreen.EXTRA_MESSAGE, "comic2a" );
                    startActivity( i );
                    finish();
				}
			}
			
			else if ( xpos > BB.width*.675 && xpos < BB.width*.88 && ypos > BB.height*0.176 && ypos < BB.height*0.44 ) {
				if ( levelNum > 2 ) {
					getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).edit().putInt( "loadLevel", 3 ).commit();
                    Intent i = new Intent( BBMapScreen.this, BBVideoScreen.class );
                    i.putExtra( BBMenuScreen.EXTRA_MESSAGE, "comic3a" );
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
	   Intent setIntent = new Intent( BBMapScreen.this, BBMenuScreen.class );
	   setIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP );
	   BBMenuScreen.ANIMATION = "DOWN";
	   startActivity( setIntent );
	}
	
}
