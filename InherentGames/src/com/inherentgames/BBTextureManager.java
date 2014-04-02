package com.inherentgames;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.Virtualizer;
import com.threed.jpct.util.BitmapHelper;


public final class BBTextureManager {

	private static BBTextureManager instance = null;
	private TextureManager tm;
	private Context context;
	private boolean spritesLoaded = false;
	
	protected BBTextureManager() {
		tm = TextureManager.getInstance();
		context = BB.getAppContext();
	}
	
	public void	addTexture( String name ) {
		tm.addTexture( name );
	}
	
	public void	addTexture( String name, Texture tex ) {
		tm.addTexture( name, tex );
	}
	
	public void	compress() {
		tm.compress();
	}
	
	public boolean containsTexture( String name ) {
		return tm.containsTexture( name );
	}
	
	public void	flush() {
		tm.flush();
	}
	
	public Texture getDummyTexture() {
		return tm.getDummyTexture();
	}
	
	public static BBTextureManager getInstance() {
		if ( instance == null ) {
			instance = new BBTextureManager();
		}
		return instance;
	}
	
	public long	getMemoryUsage() {
		return tm.getMemoryUsage();
	}
	
	public String getNameByID( int id ) {
		return tm.getNameByID( id );
	}
	
	public HashSet<String> getNames() {
		return tm.getNames();
	}
	
	public List<?>	getState() {
		return tm.getState();
	}
	
	public Texture getTexture( String name ) {
		if ( tm.containsTexture( name ) ) {
			return tm.getTexture( name );
		} else {
			//Need properties in order to determine scaled size of images
			tm.addTexture( name, new Texture( BitmapHelper.convert( context.getResources().getDrawable(
				context.getResources().getIdentifier( name.toLowerCase( Locale.US ), "drawable", context.getPackageName() ) ) ), true ) );
			return tm.getTexture( name );
		}
	}
	
	public Texture getTextureByID( int id ) {
		return tm.getTextureByID( id );
	}
	
	public int getTextureCount() {
		return tm.getTextureCount();
	}
	
	public int getTextureID( String name ) {
		return tm.getTextureID( name );
	}
	
	public Virtualizer getVirtualizer() {
		return tm.getVirtualizer();
	}
	
	public void preWarm( FrameBuffer buffer ) {
		tm.preWarm( buffer );
	}
	
	public void removeAndUnload( String name, FrameBuffer from ) {
		tm.removeAndUnload( name, from );
	}
	
	public void removeTexture( String name ) {
		tm.removeTexture( name );
	}
	
	public void replaceTexture( String name, Texture tex ) {
		tm.replaceTexture( name, tex );
	}
	
	public void setDummyTexture( Texture texture ) {
		tm.setDummyTexture( texture );
	}
	
	public void setState( List<?> dump ) {
		tm.setState( dump );
	}
	
	public void setVirtualizer( Virtualizer textureVirtualizer ) {
		tm.setVirtualizer( textureVirtualizer );
	}
	
	public void unloadTexture( FrameBuffer from, Texture texture ) {
		tm.unloadTexture( from, texture );
	}
	
	public void virtualize( Texture tex ) {
		tm.virtualize( tex );
	}
	
	// Loads all the sprite textures (AKA the textures for all the HUD elements)
	public void loadSprites() {
		Bitmap bitmap;
		if ( tm.containsTexture( "gui_font" ) ) {
		
		} else {
			if ( !spritesLoaded ) {
				try {
					
					Texture text = new Texture( context.getResources().openRawResource( R.raw.font ) );
					text.setFiltering( false );
					tm.addTexture( "gui_font", text );
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.cover1 ) ), 512, 512 );
					tm.addTexture( "loading_splash", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.bubblered ) ), 256, 256 );
					tm.addTexture( "bubbleRed", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.bubbleblue ) ), 256, 256 );
					tm.addTexture( "bubbleBlue", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.firebutton ) ), 128, 128 );
					tm.addTexture( "fireButton", new Texture( bitmap, true ) );
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.firebuttonpressed ) ), 128, 128 );
					tm.addTexture( "fireButtonPressed", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.pause_button ) ), 128, 128 );
					tm.addTexture( "pauseButton", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.pause_button_pressed ) ), 128, 128 );
					tm.addTexture( "pauseButtonPressed", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.word_bar ) ), 16, 512 );
					tm.addTexture( "FuelBar", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.time_bar ) ), 16, 512 );
					tm.addTexture( "TimeBar", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.score_bars ) ), 128, 512 );
					tm.addTexture( "ScoreBars", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.info_bar ) ), 128, 128 );
					tm.addTexture( "InfoBar", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.fuel_bar_arrow ) ), 32, 32 );
					tm.addTexture( "ScoreArrow", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.arrow_up ) ), 32, 64 );
					tm.addTexture( "ArrowUp", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.arrow_right ) ), 64, 32 );
					tm.addTexture( "ArrowRight", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.arrow_left ) ), 64, 32 );
					tm.addTexture( "ArrowLeft", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.filter ) ), 64, 64 );
					tm.addTexture( "Filter", new Texture( bitmap, true ) );
					bitmap.recycle();
					
					bitmap = BitmapHelper.rescale( BitmapHelper.convert( context.getResources().getDrawable( R.drawable.defaulttexture ) ), 256, 256 );
					tm.addTexture( "Default", new Texture( bitmap, true ) );
					bitmap.recycle();
					
				} catch( Exception e ) {
					
				}
			}
		}
	}
	
}
