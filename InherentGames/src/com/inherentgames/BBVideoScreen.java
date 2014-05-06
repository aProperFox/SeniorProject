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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.VideoView;

/**
 * @author Tyler
 * The activity that handles playing videos and changing activities once completed.
 */
public class BBVideoScreen extends Activity {
	VideoView videoView;
	
	private boolean shouldLoadMap = false;
	
	private Button skipButton;
	
	@SuppressLint( { "InlinedApi", "NewApi" } )
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
            		videoView.stopPlayback();
            		videoView.suspend();
                    startActivity( intent );
                    finish();
            	}
            	else {
            		Intent intent = new Intent( BBVideoScreen.this, BBGameScreen.class );
                    intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
                    intent.putExtra( "tutorial", false );
                    videoView.stopPlayback();
                    videoView.suspend();
                    startActivity( intent );
                    finish();
            	}
            	
            }
        } );
        
        // Handle skip button
        skipButton = (Button) findViewById( R.id.video_skip_button );
        skipButton.setLayoutParams(new RelativeLayout.LayoutParams( BB.width / 10, BB.width / 10 ));
        skipButton.setX( BB.height * 1.47f - BB.width / 6 );
        skipButton.setY( BB.height - BB.width / 6 );
        
        skipButton.setOnClickListener( new View.OnClickListener() {
	        
	        @Override
	        public void onClick( View v ) {
				if ( shouldLoadMap ) {
	        		Intent intent = new Intent( BBVideoScreen.this, BBMapScreen.class );
	        		intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
	        		videoView.stopPlayback();
	        		
	                startActivity( intent );
	                finish();
	        	}
	        	else {
	        		Intent intent = new Intent( BBVideoScreen.this, BBGameScreen.class );
	                intent.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP );
	                videoView.stopPlayback();
	                videoView.suspend();
	                startActivity( intent );
	                finish();
	        	}
	        }
	        
        });

    }
	
	@Override
	public void onBackPressed() {
		
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
