package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
 
public class SplashScreen extends Activity {

 
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView( R.layout.splash );
 
        new Handler().postDelayed( new Runnable() {
 
            @Override
            public void run() {
                    //new activity
                Intent i = new Intent( SplashScreen.this, MenuScreen.class );
                startActivity( i );
 
                // activity closed
                SplashScreen.this.finish();
            }
        }, 3000 );
    }
 
}



                /*
                setContentView( R.layout.main );
        // click-handler for buttons
        View startButton = findViewById( R.id.start );
        startButton.setOnClickListener( this );
        View closeButton = findViewById( R.id.exit );
        closeButton.setOnClickListener( this );
        
        }
        
        public void onClick( View v ) {
                switch( v.getId() ) {
                case R.id.start:
                        Intent i = new Intent( this, MainActivity.class );
                        startActivity( i );
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
*/