package com.inherentgames;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class VideoScreen extends Activity{
	VideoView videoView;
	
	private boolean shouldLoadMap = false;
	
	@SuppressLint("InlinedApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoscreen);
        
        Intent intent = getIntent();
        final String message = intent.getStringExtra(MenuScreen.EXTRA_MESSAGE);
        
        videoView = (VideoView)findViewById(R.id.VideoView);
        View root = videoView.getRootView();
        
        // Enable Immersive mode (hides status and nav bar)
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
	        videoView.setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
     	    this.UiChangeListener();
        }
        
        if(message.contains("b")){
        	shouldLoadMap = true;
        }
        else
        	shouldLoadMap = false;
        
        root.setBackgroundColor(Color.BLACK);
        		
        Uri uri = Uri.parse("android.resource://com.inherentgames/raw/" + message);   

        videoView.setVideoURI(uri);
        videoView.start();  
        
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() 
        {
            @Override
            public void onCompletion(MediaPlayer mp) 
            {
            	if(shouldLoadMap){
            		Intent intent = new Intent(VideoScreen.this, SelectMap.class);
            		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
            	}
            	else{
            		Intent intent = new Intent(VideoScreen.this, GameScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
            	}
            	
            }
        });

    }
	
	@Override
	public boolean onTouchEvent(MotionEvent me){
		if(me.getAction() == MotionEvent.ACTION_DOWN){
			if(shouldLoadMap){
        		Intent intent = new Intent(VideoScreen.this, SelectMap.class);
                startActivity(intent);
                finish();
        	}
        	else{
        		Intent intent = new Intent(VideoScreen.this, GameScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
        	}
		}
			
		return true;
	}
	
	@SuppressLint("NewApi")
	public void UiChangeListener() {
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @TargetApi(19)
			@Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }
        });
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
