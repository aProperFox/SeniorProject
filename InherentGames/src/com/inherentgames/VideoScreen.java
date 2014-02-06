package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.VideoView;

public class VideoScreen extends Activity{
	VideoView videoView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoscreen);

        videoView = (VideoView)findViewById(R.id.VideoView);
        //MediaController mediaController = new MediaController(this);
        // mediaController.setAnchorView(videoView);
        //videoView.setMediaController(mediaController);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.comic1a);    
        videoView.setVideoURI(uri);
        Log.i("olsontl", "WE'RE IN THE VIDEOSCREEN");
        final int vtime = videoView.getDuration();
        videoView.start();  
        Log.i("olsontl", "video length is: " + vtime);
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
