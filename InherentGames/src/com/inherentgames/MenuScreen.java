package com.inherentgames;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class MenuScreen extends Activity {
	
	private MediaPlayer mp;
	
	SoundPool soundPool;
	HashMap<Integer, Integer> soundPoolMap;
	int soundID = 1;

	Context context;
	
	Button sound1;
	
	@Override
    
    protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            context = this;
            mp = MediaPlayer.create(this, R.raw.time_pi_theme);
            mp.start();
            
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            soundPoolMap = new HashMap<Integer, Integer>();
            soundPoolMap.put(soundID, soundPool.load(this, R.raw.bubble_up, 1));

            
    requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.home);
            
            // click-handler for buttons
            Button playButton = (Button) findViewById(R.id.playbutton);
            
            sound1 = (Button) playButton;
            playButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                    	AudioManager audioManager = (AudioManager)getSystemService(context.AUDIO_SERVICE);
                        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        int priority = 1;
                        int no_loop = 0;
                        float normal_playback_rate = 1f;
                        Intent i = new Intent(MenuScreen.this, GameScreen.class);
                        startActivity(i);
                        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                        soundPool.play(soundID, curVolume, curVolume, priority, no_loop, normal_playback_rate);
                    	mp.stop();
                    }
            });
            
            Button settingsButton = (Button) findViewById(R.id.settingsbutton);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuScreen.this, Settings.class);
                        startActivity(i);
                    }
            });
            
            
            Button tutorialButton = (Button) findViewById(R.id.tutorialbutton);
            tutorialButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuScreen.this, Tutorial.class);
                        startActivity(i);
                    }
            });
            
            
            Button storeButton = (Button) findViewById(R.id.storebutton);
            storeButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuScreen.this, Store.class);
                        startActivity(i);
                    }
            });
            
            
    }
	
	@Override
	public void onPause(){
		super.onPause();
		mp.pause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mp.start();
	}
	
	@Override
	public void onStop(){
		super.onStop();
		mp.stop();
		//mp.release();
	}
	
}
    /*
    public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.home);
            
            // click-handler for buttons
            View playButton = findViewById(R.id.playbutton);
            playButton.setOnClickListener((OnClickListener) this);
            
            View settingsButton = findViewById(R.id.settingsbutton);
            settingsButton.setOnClickListener((OnClickListener) this);
            
            View storeButton = findViewById(R.id.storebutton);
            storeButton.setOnClickListener((OnClickListener) this);
            
            View tutorialButton = findViewById(R.id.tutorialbutton);
            tutorialButton.setOnClickListener((OnClickListener) this);
            
    }
            public void onClick(View v) {
                    switch(v.getId()){
                    case R.id.playbutton:
                            Intent i = new Intent(this, MainActivity.class);
                            startActivity(i);
                            break;
                    case R.id.settingsbutton:
                            Intent j = new Intent(this, Settings.class);
                            startActivity(j);
                            break;
                    case R.id.tutorialbutton:
                            Intent k = new Intent(this, Tutorial.class);
                            startActivity(k);
                            break;
                    case R.id.storebutton:
                            Intent l = new Intent(this, Store.class);
                            startActivity(l);
                            break;
                    }                
            }
            */