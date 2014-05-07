package com.inherentgames;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * @author Tyler
 * The unimplemented settings Activity for changing game settings. We are missing the required xml files for this
 */
public class BBSettings extends Activity implements OnSeekBarChangeListener{
	LinearLayout myscreen;
	SeekBar bgm, master, voice;
	int bgmV, masterV, voiceV;
	/*
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		myscreen=(LinearLayout)findViewById(R.id.mylayout);
		bgm=(SeekBar)findViewById(R.id.bgmseekBar);
		master=(SeekBar)findViewById(R.id.mvseekBar);
		voice=(SeekBar)findViewById(R.id.wordseekBar);
		bgm.setOnSeekBarChangeListener(this);
		master.setOnSeekBarChangeListener(this);
		voice.setOnSeekBarChangeListener(this);
		Button returnButton = (Button) findViewById(R.id.returnButton);
		returnButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Settings.this, MenuScreen.class);
                startActivity(i);
            }
		});
	}

	public void updatevolume(){
		bgmV = bgm.getProgress();
		masterV = master.getProgress();
		voiceV = voice.getProgress();

	}
	*/

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

}