package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
        View someView = findViewById(R.id.VideoView);
        View root = someView.getRootView();
        root.setBackgroundColor(Color.BLACK);
        		
        videoView = (VideoView)findViewById(R.id.VideoView);
        //MediaController mediaController = new MediaController(this);
        // mediaController.setAnchorView(videoView);
        //videoView.setMediaController(mediaController);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.comic1a);    
        videoView.setVideoURI(uri);
        videoView.start();  
        new Thread() {
            public void run() {
                    try{

                            sleep(123000);
                    } catch (Exception e) {

                    }
                  Intent intent = new Intent(VideoScreen.this, GameScreen.class);
                  startActivity(intent);
                  finish();
            }
    }.start();
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent me){
		if(me.getAction() == MotionEvent.ACTION_DOWN){
			videoView.stopPlayback();
			Intent intent = new Intent(VideoScreen.this, GameScreen.class);
            startActivity(intent);
            finish();
		}
			
		return true;
	}
}
