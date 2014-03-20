package com.inherentgames;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.content.Context;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.Virtualizer;
import com.threed.jpct.util.BitmapHelper;


public final class BBTextureManager {

	private static BBTextureManager instance = null;
	private TextureManager tm;
	private Context context;
	
	protected BBTextureManager(Context c) {
		tm = TextureManager.getInstance();
		context = c;
	}
	
	public void	addTexture(String name) {
		tm.addTexture(name);
	}
	
	public void	addTexture(String name, Texture tex) {
		tm.addTexture(name, tex);
	}
	
	public void	compress() {
		tm.compress();
	}
	
	public boolean containsTexture(String name) {
		return tm.containsTexture(name);
	}
	
	public void	flush() {
		tm.flush();
	}
	
	public Texture getDummyTexture() {
		return tm.getDummyTexture();
	}
	
	public static BBTextureManager getInstance(Context c) {
		if (instance == null) {
			instance = new BBTextureManager(c);
		}
		return instance;
	}
	
	public long	getMemoryUsage() {
		return tm.getMemoryUsage();
	}
	
	public String getNameByID(int id) {
		return tm.getNameByID(id);
	}
	
	public HashSet<String> getNames() {
		return tm.getNames();
	}
	
	public List<?>	getState() {
		return tm.getState();
	}
	
	public Texture getTexture(String name) {
		if(tm.containsTexture(name)) {
			return tm.getTexture(name);
		} else {
			//Need properties in order to determine scaled size of images
			tm.addTexture(name, new Texture(BitmapHelper.convert(context.getResources().getDrawable(
				context.getResources().getIdentifier(name.toLowerCase(Locale.US), "drawable", context.getPackageName()))), true));
			return tm.getTexture(name);
		}
	}
	
	public Texture getTextureByID(int id) {
		return tm.getTextureByID(id);
	}
	
	public int getTextureCount() {
		return tm.getTextureCount();
	}
	
	public int getTextureID(String name) {
		return tm.getTextureID(name);
	}
	
	public Virtualizer getVirtualizer() {
		return tm.getVirtualizer();
	}
	
	public void preWarm(FrameBuffer buffer) {
		tm.preWarm(buffer);
	}
	
	public void removeAndUnload(String name, FrameBuffer from) {
		tm.removeAndUnload(name, from);
	}
	
	public void removeTexture(String name) {
		tm.removeTexture(name);
	}
	
	public void replaceTexture(String name, Texture tex) {
		tm.replaceTexture(name, tex);
	}
	
	public void setDummyTexture(Texture texture) {
		tm.setDummyTexture(texture);
	}
	
	public void setState(List<?> dump) {
		tm.setState(dump);
	}
	
	public void setVirtualizer(Virtualizer textureVirtualizer) {
		tm.setVirtualizer(textureVirtualizer);
	}
	
	public void unloadTexture(FrameBuffer from, Texture texture) {
		tm.unloadTexture(from, texture);
	}
	
	public void virtualize(Texture tex) {
		tm.virtualize(tex);
	}
	
}
