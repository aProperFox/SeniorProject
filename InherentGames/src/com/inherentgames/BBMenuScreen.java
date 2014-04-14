package com.inherentgames;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;


@SuppressLint( "NewApi" )
public class BBMenuScreen extends Activity {
	
	private MediaPlayer mp;
	
	private SoundPool soundPool;
	private SparseIntArray soundPoolMap;
	private int soundID = 1;
	
	private int buttonTextColor;
	private Typeface typeface;
	
	public static final String EXTRA_MESSAGE = "VIDEO VALUE";
	public static String ANIMATION = "DOWN";
	public static final String PREFERENCES = "BABBLE_PREF";
	public static final boolean isDevMode = true;
	private Button playButton;
	
	
	@SuppressLint( "NewApi" )
	@Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
   
        Log.d( "MenuScreen", "onCreate called" );
		
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		setContentView( R.layout.menu_screen );
		
		try {
		    mp = MediaPlayer.create( this, R.raw.time_pi_theme );
		    mp.setLooping( true );
		    mp.start();
		} catch ( Exception e ) {
			Log.e( "MenuScreen", "Something went wrong with the MediaPlayer." );
		}
		
		soundPool = new SoundPool( 4, AudioManager.STREAM_MUSIC, 100 );
		soundPoolMap = new SparseIntArray();
		soundPoolMap.put( soundID, soundPool.load( this, R.raw.bubble_up, 1 ) );
		
		buttonTextColor = Color.rgb( 156, 192, 207 );
		
		typeface = Typeface.createFromAsset( getAssets(), "futura-normal.ttf" );
		
		// Set button width and height variables
		int buttonWidth = (int) (BB.width / 4.9f);
		int buttonHeight = (int) (BB.height / 8.7f);
		float buttonX = (BB.width / 1.2f) - buttonWidth / 2;
		
		// Click handler for buttons
		playButton = ( Button ) findViewById( R.id.playbutton );
		setButtonConfig( playButton, getString( R.string.play_button ).toUpperCase(Locale.US) );
		
		// Set location based on screen dimensions
		playButton.setWidth( buttonWidth );
		playButton.setHeight( buttonHeight );
		playButton.setX( buttonX );
		Log.d("BBMenuScreen", "button x is: " + playButton.getX());
		playButton.setY( (BB.height / 2.204f) - buttonHeight / 2 );
		Log.d("BBMenuScreen", "button y is: " + playButton.getY());
		
		SharedPreferences settings = getSharedPreferences( PREFERENCES, 0 );
		
		//Audio settings for bubble sounds
    	AudioManager audioManager = ( AudioManager )getSystemService( Context.AUDIO_SERVICE );
        final float curVolume = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        final int priority = 1;
        final int no_loop = 0;
        final float normal_playback_rate = 1f;
		
		if ( settings.getBoolean( "hasBeatenTutorial", false ) ) {     
		playButton.setEnabled( false );
		Log.d( "MenuScreen", "Disabling button" );
		}
		else {
			playButton.setEnabled( true );
			Log.d( "MenuScreen", "Enabling button" );
		}
		
		playButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {

		            Intent i = new Intent( BBMenuScreen.this, BBMapScreen.class );
		            startActivity( i );
		            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
		            soundPool.play( soundID, curVolume, curVolume, priority, no_loop, normal_playback_rate );
		            
		        	mp.stop();
		        }
		} );
		
		Button settingsButton = ( Button ) findViewById( R.id.settingsbutton );
		setButtonConfig( settingsButton, getString( R.string.settings_button ).toUpperCase(Locale.US) );
		
		// Set location based on screen dimensions
		settingsButton.setX( buttonX );
		settingsButton.setY( (BB.height / 1.349f) - buttonHeight / 2 );
		settingsButton.setWidth( buttonWidth );
		settingsButton.setHeight( buttonHeight );
		
		settingsButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		            Intent i = new Intent( BBMenuScreen.this, BBSettings.class );
		            startActivity( i );
		            overridePendingTransition( R.anim.slide_in_down, R.anim.slide_out_down );
		        }
		} );
		
		
		Button tutorialButton = ( Button ) findViewById( R.id.tutorialbutton );
		setButtonConfig( tutorialButton, getString( R.string.tutorial_button ).toUpperCase(Locale.US) );
		
		// Set location based on screen dimensions
		tutorialButton.setX( buttonX );
		tutorialButton.setY( (BB.height / 1.674f) - buttonHeight / 2 );
		tutorialButton.setWidth( buttonWidth );
		tutorialButton.setHeight( buttonHeight );
		
		tutorialButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		            Intent i = new Intent( BBMenuScreen.this, BBGameScreen.class );
		            i.putExtra( "tutorial", true );
		            startActivity( i );
		            overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
		            soundPool.play( soundID, curVolume, curVolume, priority, no_loop, normal_playback_rate );
		        }
		} );
		
		
		Button storeButton = ( Button ) findViewById( R.id.storebutton );
		setButtonConfig( storeButton, getString( R.string.store_button ).toUpperCase(Locale.US) );
		
		// Set location based on screen dimensions
		storeButton.setX( buttonX );
		storeButton.setY( (BB.height / 1.13f) - buttonHeight / 2 );
		Log.d("BBMenuScreen", "button y is: " + storeButton.getY());
		storeButton.setWidth( buttonWidth );
		storeButton.setHeight( buttonHeight );
		
		storeButton.setOnClickListener( new View.OnClickListener() {
		        
		        @Override
		        public void onClick( View v ) {
		            Intent i = new Intent( BBMenuScreen.this, BBStore.class );
		            startActivity( i );
		            overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
		        }
		} );
            
            
    }
	
	@Override
	public void onPause() {
		super.onPause();
		try {
			if ( mp != null && mp.isPlaying() )
				mp.pause();
		} catch ( IllegalStateException e ) {
			Log.e( "MenuScreen", "Can't pause the media player." );
		}
	}
	
	
	@SuppressLint( "InlinedApi" )
	@Override
	public void onResume() {
		
		Log.d( "MenuScreen", "Resuming Menu screen" );
		
		if ( getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).getBoolean( "hasBeatenTutorial", false ) ) {
			playButton.setEnabled( true );
			Log.d( "MenuScreen", "Enabling button" );
		}
		else {
			playButton.setEnabled( false );
			Log.d( "MenuScreen", "Disabling button" );
		}
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
		
		try {
			if ( mp == null )
				mp = MediaPlayer.create( this, R.raw.time_pi_theme );
			else if ( !mp.isPlaying() )
				mp.prepare();
            mp.setLooping( true );
            mp.start();
        } catch ( Exception e ) {
        	Log.e( "MenuScreen", "Something went wrong with the MediaPlayer." );
        }
	}
	
	@Override
	public void onStart() {
		Log.d( "MenuScreen", "onStart" );
		
		if ( getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).getBoolean( "hasBeatenTutorial", false ) ) {
			playButton.setEnabled( true );
			Log.d( "MenuScreen", "Enabling button" );

		}
		else {
			playButton.setEnabled( false );
			Log.d( "MenuScreen", "Disabling button" );
		}
		super.onStart();
	
	}
	
	@Override
	public void onStop() {
		super.onStop();
		try {
			if ( mp.isPlaying() )
				mp.stop();
			mp.reset();
			mp.release();
			mp = null;
		} catch ( IllegalStateException e ) {
			Log.e( "MenuScreen", "Can't stop the player." );
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		if ( BBMenuScreen.isDevMode ) {
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate( R.menu.menu, menu );
		}
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
        case R.id.delete_data:
        	getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).edit().remove( "hasBeatenTutorial" ).commit();
        	getSharedPreferences( BBMenuScreen.PREFERENCES, 0 ).edit().remove( "nextLevel" ).commit();
        	playButton.setEnabled( false );
        	return true;
        }
        return super.onOptionsItemSelected( item );
    }
	
	
	private void setButtonConfig( Button button, String text ) {
		button.setTextColor( buttonTextColor );
        button.setTextSize( 24 );
        button.setText( text );
        button.setTypeface( typeface );
	}
	
}
