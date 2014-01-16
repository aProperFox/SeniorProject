package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;


public class MenuScreen extends Activity {
	
	@Override
    
    protected void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.home);
            
            // click-handler for buttons
            Button playButton = (Button) findViewById(R.id.playbutton);
            playButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                    	v.getBackground().setColorFilter(0xFF3C6F89,PorterDuff.Mode.ADD);
                        Intent i = new Intent(MenuScreen.this, GameScreen.class);
                        startActivity(i);
                    }
            });
            
            Button settingsButton = (Button) findViewById(R.id.settingsbutton);
            settingsButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                    	v.getBackground().setColorFilter(0xFF3C6F89,PorterDuff.Mode.ADD);
                        Intent i = new Intent(MenuScreen.this, Settings.class);
                        startActivity(i);
                    }
            });
            
            
            Button tutorialButton = (Button) findViewById(R.id.tutorialbutton);
            tutorialButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
        				v.getBackground().setColorFilter(0xFF3C6F89,PorterDuff.Mode.ADD);
                        Intent i = new Intent(MenuScreen.this, Tutorial.class);
                        startActivity(i);
                    }
            });
            
            
            Button storeButton = (Button) findViewById(R.id.storebutton);
            storeButton.setOnClickListener(new View.OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                    	v.getBackground().setColorFilter(0xFF3C6F89,PorterDuff.Mode.ADD);
                        Intent i = new Intent(MenuScreen.this, Store.class);
                        startActivity(i);
                    }
            });
            
            
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