package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

public class VideoScreen extends Activity{
	VideoView videoView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoscreen);
        
        Intent intent = getIntent();
        final String message = intent.getStringExtra(MenuScreen.EXTRA_MESSAGE);
        
        videoView = (VideoView)findViewById(R.id.VideoView);
        View root = videoView.getRootView();
        root.setBackgroundColor(Color.BLACK);
        		
        Uri uri = Uri.parse("android.resource://com.inherentgames/raw/" + message);   

        videoView.setVideoURI(uri);
        videoView.start();  
        
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() 
        {
            @Override
            public void onCompletion(MediaPlayer mp) 
            {
            	Intent intent = new Intent(VideoScreen.this, GameScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                finish();
            }
        });

    }
	
	
	
	@Override
	public boolean onTouchEvent(MotionEvent me){
		if(me.getAction() == MotionEvent.ACTION_DOWN){
			videoView.stopPlayback();
			Intent intent = new Intent(VideoScreen.this, GameScreen.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
		}
			
		return true;
	}
}
