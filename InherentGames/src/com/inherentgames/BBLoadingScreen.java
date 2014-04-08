package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class BBLoadingScreen extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.splash );
 
        new Handler().postDelayed( new Runnable() {
 
            @Override
            public void run() {
                    //new activity
                Intent i = new Intent( BBLoadingScreen.this, BBGameScreen.class );
                startActivity( i );
 
                // activity closed
                BBLoadingScreen.this.finish();
            }
        }, 3000 );
    }
 
}