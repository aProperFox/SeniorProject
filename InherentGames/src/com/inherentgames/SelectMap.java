package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SelectMap extends Activity {

	Button stage1;
	Button stage2;
	Button stage3;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		//Need tyler's revise
		//Need state of which levels has been cleared.
		SharedPreferences settings = getSharedPreferences(MenuScreen.PREFERENCES, 0);
		int levelNum = settings.getInt("nextLevel", 1);
		switch(levelNum){
		case 1:
			Log.i("SelectMap", "Level 1 map");
			setContentView(R.layout.stageone);
			stage1 = (Button) findViewById(R.id.stage_one);
			
			stage1.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SelectMap.this, VideoScreen.class);
                    i.putExtra(MenuScreen.EXTRA_MESSAGE, "comic1a");
                    startActivity(i);
                }
			});
			break;
		case 2:
			Log.i("SelectMap", "Level 2 map");
			setContentView(R.layout.stagetwo);
			stage1 = (Button) findViewById(R.id.stage_one);
			stage1.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SelectMap.this, VideoScreen.class);
                    i.putExtra(MenuScreen.EXTRA_MESSAGE, "comic1a");
                    startActivity(i);
                }
			});
			stage2 = (Button) findViewById(R.id.stage_two);
			stage2.setOnClickListener(new View.OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(SelectMap.this, VideoScreen.class);
                    i.putExtra(MenuScreen.EXTRA_MESSAGE, "comic2a");
                    startActivity(i);
                }
			});
			break;
		case 3:
			setContentView(R.layout.stagethree);
			stage1 = (Button) findViewById(R.id.stage_one);
			stage2 = (Button) findViewById(R.id.stage_two);
			stage3 = (Button) findViewById(R.id.stage_three);
			break;
		}
		
	
	}
	
	
	
	
}
