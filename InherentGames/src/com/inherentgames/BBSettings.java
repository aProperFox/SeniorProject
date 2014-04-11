package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class BBSettings extends Activity {
	
	
	@Override
	public void onBackPressed() {
	   Log.d( "Tutorial", "onBackPressed Called" );
	   Intent setIntent = new Intent( BBSettings.this, BBMenuScreen.class );
	   setIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP );
	   startActivity( setIntent );
	   BBMenuScreen.ANIMATION = "UP";
	}
}
