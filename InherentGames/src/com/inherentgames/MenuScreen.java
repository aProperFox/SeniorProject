package com.inherentgames;

import android.annotation.SuppressLint;
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
import android.util.SparseIntArray;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class MenuScreen extends Activity {
	
	private MediaPlayer mp;
	
	SoundPool soundPool;
	SparseIntArray soundPoolMap;
	int soundID = 1;

	Context context;
	
	Button sound1;
	private int easterEggCount;
	private int width;
	private int height;
	private boolean canEasterEggPlay;
	
	private int buttonTextColor;
	private Typeface typeface;
	
	public static final String EXTRA_MESSAGE = "VIDEO VALUE";
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
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
    		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.home);
            
            try {
	            mp = MediaPlayer.create(this, R.raw.time_pi_theme);
	            mp.setLooping(true);
	            mp.start();
            } catch (Exception e) {
            	Log.e("MenuScreen", "Something went wrong with the MediaPlayer.");
            }
            
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            soundPoolMap = new SparseIntArray();
            soundPoolMap.put(soundID, soundPool.load(this, R.raw.bubble_up, 1));
            
            buttonTextColor = Color.rgb(156, 192, 207);
            
            typeface = Typeface.createFromAsset(getAssets(), "futura-normal.ttf"); 
            // click-handler for buttons
            Button playButton = (Button) findViewById(R.id.playbutton);
            setButtonConfig(playButton, getString(R.string.play_button));
            
            sound1 = (Button) playButton;
            playButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                    	AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        int priority = 1;
                        int no_loop = 0;
                        float normal_playback_rate = 1f;
                        Intent i = new Intent(MenuScreen.this, VideoScreen.class);
                        i.putExtra(EXTRA_MESSAGE, "comic1a");
                        startActivity(i);
                        overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
                        soundPool.play(soundID, curVolume, curVolume, priority, no_loop, normal_playback_rate);
                        
                    	mp.stop();
                    }
            });
            
            Button settingsButton = (Button) findViewById(R.id.settingsbutton);
            setButtonConfig(settingsButton, getString(R.string.settings_button));
            
            settingsButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuScreen.this, Settings.class);
                        startActivity(i);
                    }
            });
            
            
            Button tutorialButton = (Button) findViewById(R.id.tutorialbutton);
            setButtonConfig(tutorialButton, getString(R.string.tutorial_button));
            
            tutorialButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MenuScreen.this, Tutorial.class);
                        startActivity(i);
                    }
            });
            
            
            Button storeButton = (Button) findViewById(R.id.storebutton);
            setButtonConfig(storeButton, getString(R.string.store_button));
            
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
			}
		}
		if(easterEggCount >= 3 && canEasterEggPlay == true){
			mp.stop();
			mp.release();
			mp = MediaPlayer.create(context, R.raw.fly_haircut);
            mp.start();
            canEasterEggPlay = false;
            Toast toast = Toast.makeText(context, R.string.easter_egg, Toast.LENGTH_LONG);
            toast.show();
		}
		return true;
	}
	
	@Override
	public void onPause(){
		super.onPause();
		try {
			if (mp != null && mp.isPlaying())
				mp.pause();
		} catch (IllegalStateException e) {
			Log.e("MenuScreen", "Can't pause the media player.");
		}
	}
	
	
	@SuppressLint("InlinedApi")
	@Override
	public void onResume(){
		super.onResume();
		
		// Enable Immersive mode (hides status and nav bar)
		View currentView = getWindow().getDecorView();
		if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			currentView.setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		    this.UiChangeListener();
		}
		
		try {
			if (mp == null)
				mp = MediaPlayer.create(this, R.raw.time_pi_theme);
			else if (!mp.isPlaying())
				mp.prepare();
            mp.setLooping(true);
            mp.start();
        } catch (Exception e) {
        	Log.e("MenuScreen", "Something went wrong with the MediaPlayer.");
        }
	}
	
	@Override
	public void onStop(){
		super.onStop();
		try {
			if (mp.isPlaying())
				mp.stop();
			mp.reset();
			mp.release();
			mp = null;
		} catch (IllegalStateException e) {
			Log.e("MenuScreen", "Can't stop the player.");
		}
	}
	
	private void setButtonConfig(Button button, String text){
		button.setTextColor(buttonTextColor);
        button.setTextSize(24);
        button.setText(text);
        button.setTypeface(typeface);
	}
	
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
	
}
