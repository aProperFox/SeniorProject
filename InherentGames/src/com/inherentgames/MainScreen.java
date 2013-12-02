package com.inherentgames;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;


public class MainScreen extends Activity implements OnClickListener{
        /** called when the activity is first created */
        public void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                setContentView(R.layout.main);
        // click-handler for buttons
        View startButton = findViewById(R.id.start);
        startButton.setOnClickListener(this);
        View closeButton = findViewById(R.id.exit);
        closeButton.setOnClickListener(this);
        }
        
        public void onClick(View v) {
                switch(v.getId()){
                case R.id.start:
                        Intent i = new Intent(this, MainActivity.class);
                        startActivity(i);
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