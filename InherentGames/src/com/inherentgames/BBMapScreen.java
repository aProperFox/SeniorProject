package com.inherentgames;

import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

@SuppressLint("NewApi")
public class BBMapScreen extends Activity {

	Button stage1;
	ImageButton stage2;
	Button stage3;
	
	private SharedPreferences settings;

	
	int levelNum;
	
	@SuppressLint( "NewApi" )
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		BBGame.getInstance().loading = true;
		
		settings = getSharedPreferences( BB.PREFERENCES, 0 );
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
			default:
				Log.i( "MapScreen", "Loading Level 3 map" );
				setContentView( R.layout.stage3 );
				break;
		}
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
	}
	
	@SuppressLint("InlinedApi")
	public void onResume() {
		super.onResume();
		BBGame.getInstance().loading = true;
		// Check for animation message
		if ( BB.ANIMATION == "LEFT" ){
			overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
		} else if ( BB.ANIMATION == "RIGHT" ) {
			overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
		} else if ( BB.ANIMATION == "DOWN" ) {
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
				settings.edit().putInt( "loadLevel", 1 ).commit();
				if ( settings.getStringSet( "playedComics", BB.EMPTYSET).contains("comic1a")) {
	                Intent i = new Intent( BBMapScreen.this, BBGameScreen.class );
	                startActivity( i );
	                finish();
				} else {
	                Intent i = new Intent( BBMapScreen.this, BBVideoScreen.class );
	                i.putExtra( BB.EXTRA_MESSAGE, "comic1a" );
	                startActivity( i );
	                finish();
				}
			}
			
			else if ( xpos > BB.width*.4 && xpos < BB.width*.6 && ypos > BB.height*0.618 && ypos < BB.height*0.88 ) {
				if ( levelNum > 1 ) {
					settings.edit().putInt( "loadLevel", 2 ).commit();
					if ( settings.getStringSet( "playedComics", BB.EMPTYSET).contains("comic2a")) {
		                Intent i = new Intent( BBMapScreen.this, BBGameScreen.class );
		                startActivity( i );
		                finish();
					} else {
		                Intent i = new Intent( BBMapScreen.this, BBVideoScreen.class );
		                i.putExtra( BB.EXTRA_MESSAGE, "comic2a" );
		                startActivity( i );
		                finish();
					}
				}
			}
			
			else if ( xpos > BB.width*.675 && xpos < BB.width*.88 && ypos > BB.height*0.176 && ypos < BB.height*0.44 ) {
				if ( levelNum > 2 ) {
					settings.edit().putInt( "loadLevel", 3 ).commit();
					if ( settings.getStringSet( "playedComics", BB.EMPTYSET).contains("comic3a")) {
		                Intent i = new Intent( BBMapScreen.this, BBGameScreen.class );
		                startActivity( i );
		                finish();
					} else {
		                Intent i = new Intent( BBMapScreen.this, BBVideoScreen.class );
		                i.putExtra( BB.EXTRA_MESSAGE, "comic3a" );
		                startActivity( i );
		                finish();
					}
				}
			}
			
			
		}
		return true;
	}
	
	
	@Override
	public void onBackPressed() {
	   Log.d( "MapScreen", "onBackPressed Called" );
	   Intent setIntent = new Intent( BBMapScreen.this, BBMenuScreen.class );
	   setIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
	   BB.ANIMATION = "DOWN";
	   startActivity( setIntent );
	}
	
}
