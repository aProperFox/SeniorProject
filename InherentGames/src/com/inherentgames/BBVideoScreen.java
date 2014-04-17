package com.inherentgames;

import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.VideoView;

public class BBVideoScreen extends Activity {
	VideoView videoView;
	
	private boolean shouldLoadMap = false;
	
	@SuppressLint( "InlinedApi" )
	@Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.video_screen );
        
        Intent intent = getIntent();
        final String message = intent.getStringExtra( BB.EXTRA_MESSAGE );
        
        videoView = ( VideoView )findViewById( R.id.VideoView );
        View root = videoView.getRootView();
        
        // Enable Immersive mode (hides status and nav bar)
    	if ( android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ) {
    		BB.setImmersiveMode( findViewById( Window.ID_ANDROID_CONTENT ).getRootView(), getWindow().getDecorView() );
    	}
        
        if ( message.contains( "b" ) ) {
        	shouldLoadMap = true;
        }
        else
        	shouldLoadMap = false;

        
        root.setBackgroundColor( Color.BLACK );
        		
        Uri uri = Uri.parse( "android.resource://com.inherentgames/raw/" + message );   

        videoView.setVideoURI( uri );
        videoView.start();  
        
        videoView.setOnCompletionListener( new MediaPlayer.OnCompletionListener() 
        {
            @Override
            public void onCompletion( MediaPlayer mp ) 
            {
            	// Update comics played
            	SharedPreferences settings = getSharedPreferences( BB.PREFERENCES , 0);
            	Set<String> set = settings.getStringSet( "playedComics", BB.EMPTYSET);
            	set.add(message);
            	Log.d("BBVideoScreen", "Played comics: " + set);
            	settings.edit().putStringSet( "playedComics", set).commit();
            	
            	if ( shouldLoadMap ) {
            		Intent intent = new Intent( BBVideoScreen.this, BBMapScreen.class );
            		intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
                    startActivity( intent );
                    finish();
            	}
            	else {
            		Intent intent = new Intent( BBVideoScreen.this, BBGameScreen.class );
                    intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    intent.putExtra( "tutorial", false );
                    startActivity( intent );
                    finish();
            	}
            	
            }
        } );

    }
	
	@Override
	public boolean onTouchEvent( MotionEvent me ) {
		
		if ( me.getAction() == MotionEvent.ACTION_DOWN ) {
			if ( shouldLoadMap ) {
        		Intent intent = new Intent( BBVideoScreen.this, BBMapScreen.class );
        		intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
                startActivity( intent );
                finish();
        	}
        	else {
        		Intent intent = new Intent( BBVideoScreen.this, BBGameScreen.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                startActivity( intent );
                finish();
        	}
        	
		}
		
		
		return true;
	}
	
	@Override
	protected void onResume() {
	    videoView.resume();
	    super.onResume();
	}

	@Override
	protected void onPause() {
	    videoView.suspend();
	    super.onPause();
	}

	@Override
	protected void onDestroy() {
	    videoView.stopPlayback();
	    super.onDestroy();
	}
	
}
