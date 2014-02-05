package com.inherentgames;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;


public class MenuScreen extends Activity {
	
	private MediaPlayer mp;
	
	SoundPool soundPool;
	HashMap<Integer, Integer> soundPoolMap;
	int soundID = 1;

	Context context;
	
	Button sound1;
	private int easterEggCount;
	private int width;
	private int height;
	private boolean canEasterEggPlay;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@Override
    protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            context = this;
            easterEggCount = 0;
            canEasterEggPlay = true;
            Display display = getWindowManager().getDefaultDisplay();
    		
    		// Use legacy code if running on older Android versions
    		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
    			width = display.getWidth();
    			height = display.getHeight();
    		} else {
    			Point size = new Point();
    			display.getSize(size);
    			width = size.x;
    			height = size.y;
    		}
    		
            mp = MediaPlayer.create(this, R.raw.time_pi_theme);
            mp.start();
            
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            soundPoolMap = new HashMap<Integer, Integer>();
            soundPoolMap.put(soundID, soundPool.load(this, R.raw.bubble_up, 1));

            
    requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.home);
            
            int buttonTextColor = Color.rgb(156, 192, 207);
            
            Typeface typeface = Typeface.createFromAsset(getAssets(), "futura-normal.ttf"); 
            // click-handler for buttons
            Button playButton = (Button) findViewById(R.id.playbutton);
            playButton.setTextColor(buttonTextColor);
            playButton.setTextSize(24);
            playButton.setText(R.string.play_button);
            playButton.setTypeface(typeface);
            
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
            settingsButton.setTextColor(buttonTextColor);
            settingsButton.setTextSize(24);
            settingsButton.setText(R.string.settings_button);
            settingsButton.setTypeface(typeface);
            
            settingsButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuScreen.this, Settings.class);
                        startActivity(i);
                    }
            });
            
            
            Button tutorialButton = (Button) findViewById(R.id.tutorialbutton);
            tutorialButton.setTextColor(buttonTextColor);
            tutorialButton.setTextSize(24);
            tutorialButton.setText(R.string.tutorial_button);
            tutorialButton.setTypeface(typeface);
            
            tutorialButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuScreen.this, Tutorial.class);
                        startActivity(i);
                    }
            });
            
            
            Button storeButton = (Button) findViewById(R.id.storebutton);
            storeButton.setTextColor(buttonTextColor);
            storeButton.setTextSize(24);
            storeButton.setText(R.string.store_button);
            storeButton.setTypeface(typeface);
            
            storeButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuScreen.this, Store.class);
                        startActivity(i);
                    }
            });
            
            
    }
	
	public boolean onTouchEvent(MotionEvent me){
		float xpos = me.getX();
		float ypos = me.getY();
		if(me.getAction() == MotionEvent.ACTION_DOWN){
			if(xpos > width*.35 && xpos < width*.53 && ypos> height*.08 && ypos < height*.3){
				easterEggCount++;
				Log.i("olsontl", "Easter egg count: " + easterEggCount);
			}
			Log.i("olsontl", "X at " + xpos/width + "%, Y at " + ypos/height + "%");
		}
		if(easterEggCount >= 3 && canEasterEggPlay == true){
			mp.stop();
			mp.release();
			mp = MediaPlayer.create(context, R.raw.fly_haircut);
            mp.start();
            canEasterEggPlay = false;
            Toast toast = Toast.makeText(context, R.string.easter_egg, 5);
            toast.show();
		}
		return true;
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