package com.inherentgames;

import android.app.Activity;
import android.os.Bundle;

public class BBPopup extends Activity {
        
        @Override
        public void onCreate( Bundle savedInstanceState ) {
                
                super.onCreate( savedInstanceState );
                setContentView( R.layout.pause_popup );
        }
        /*
        public void onPopupBtClick( View view ) {
                PopupMenu menu = new PopupMenu( this, view );
                menu.getMenuInflater().inflate( R.menu.popupmenu, menu.getMenu() );
                menu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                        
                        public boolean onMenuItemClick( MenuItem item ) {
                                return true;
                                
                        }
                } );
                menu.show();
        }
        */
        
}