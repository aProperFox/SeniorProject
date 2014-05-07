package com.inherentgames;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.Virtualizer;
import com.threed.jpct.util.BitmapHelper;


/**
 * @author Tyler
 * A class used to dynamically load textures into the texture manager.
 */
public final class BBTextureManager {

	private static BBTextureManager instance = null;
	private TextureManager tm;
	
	/**
	 * Constructor for BBTextureManager, sets the local JPCT-AE TextureManager
	 */
	protected BBTextureManager() {
		tm = TextureManager.getInstance();
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param name
	 */
	public void	addTexture( String name ) {
		tm.addTexture( name );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param name
	 * @param tex
	 */
	public void	addTexture( String name, Texture tex ) {
		tm.addTexture( name, tex );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 */
	public void	compress() {
		tm.compress();
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param name
	 * @return
	 */
	public boolean containsTexture( String name ) {
		return tm.containsTexture( name );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 */
	public void	flush() {
		tm.flush();
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @return
	 */
	public Texture getDummyTexture() {
		return tm.getDummyTexture();
	}
	
	/**
	 * Gets an instance of the BBTextureManager
	 * 
	 * @return - and instance of the BBTextureManager
	 */
	public static BBTextureManager getInstance() {
		if ( instance == null ) {
			instance = new BBTextureManager();
		}
		return instance;
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @return
	 */
	public long	getMemoryUsage() {
		return tm.getMemoryUsage();
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param id
	 * @return
	 */
	public String getNameByID( int id ) {
		return tm.getNameByID( id );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @return
	 */
	public HashSet<String> getNames() {
		return tm.getNames();
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @return
	 */
	public List<?>	getState() {
		return tm.getState();
	}
	
	/**
	 * Will dynamically load textures if not defined yet, otherwise return the texture
	 * 
	 * TODO: setup a properties file with image scale width/height so the dynamic loading can work
	 * 
	 * See the associated JPCT-AE TextureManager function for details
	 * @param name
	 * @return
	 */
	public Texture getTexture( String name ) {
		if ( tm.containsTexture( name ) ) {
			return tm.getTexture( name );
		} else {
			//Need properties in order to determine scaled size of images
			tm.addTexture( name, new Texture( BitmapHelper.convert( BB.context.getResources().getDrawable(
				BB.context.getResources().getIdentifier( name.toLowerCase( Locale.US ), "drawable", BB.context.getPackageName() ) ) ), true ) );
			return tm.getTexture( name );
		}
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param id
	 * @return
	 */
	public Texture getTextureByID( int id ) {
		return tm.getTextureByID( id );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @return
	 */
	public int getTextureCount() {
		return tm.getTextureCount();
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param name
	 * @return
	 */
	public int getTextureID( String name ) {
		return tm.getTextureID( name );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @return
	 */
	public Virtualizer getVirtualizer() {
		return tm.getVirtualizer();
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param buffer
	 */
	public void preWarm( FrameBuffer buffer ) {
		tm.preWarm( buffer );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param name
	 * @param from
	 */
	public void removeAndUnload( String name, FrameBuffer from ) {
		tm.removeAndUnload( name, from );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param name
	 */
	public void removeTexture( String name ) {
		tm.removeTexture( name );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param name
	 * @param tex
	 */
	public void replaceTexture( String name, Texture tex ) {
		tm.replaceTexture( name, tex );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param texture
	 */
	public void setDummyTexture( Texture texture ) {
		tm.setDummyTexture( texture );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param dump
	 */
	public void setState( List<?> dump ) {
		tm.setState( dump );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param textureVirtualizer
	 */
	public void setVirtualizer( Virtualizer textureVirtualizer ) {
		tm.setVirtualizer( textureVirtualizer );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param from
	 * @param texture
	 */
	public void unloadTexture( FrameBuffer from, Texture texture ) {
		tm.unloadTexture( from, texture );
	}
	
	/**
	 * See the associated JPCT-AE TextureManager function for details
	 * @param tex
	 */
	public void virtualize( Texture tex ) {
		tm.virtualize( tex );
	}
	
}
