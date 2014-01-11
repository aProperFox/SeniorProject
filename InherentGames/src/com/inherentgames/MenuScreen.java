package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;


public class MenuScreen extends Activity implements OnClickListener {
	
	public void onResume() {
		super.onResume();
		setContentView(R.layout.main);
		// Click handler for buttons
		View startButton = findViewById(R.id.start);
		startButton.setOnClickListener(this);
		View closeButton = findViewById(R.id.exit);
		closeButton.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.start:
				final Intent i = new Intent(this, GameScreen.class);
				setContentView(R.layout.splash);

				Thread welcomeThread = new Thread() {
					@Override
					public void run() {
						try {
							super.run();
							sleep(2000);  //Delay of 10 seconds
						} catch (Exception e) {

						} finally {
							startActivity(i);
						}
					}
				};
				welcomeThread.start();
				break;
			case R.id.exit:
				finish();
				break;
			case R.id.tutorial:
				break;
			case R.id.settings:
				break;
		}
	}
}