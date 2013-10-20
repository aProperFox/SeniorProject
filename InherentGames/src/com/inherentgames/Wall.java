package com.inherentgames;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;

public class Wall extends Object3D{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Context context;
	
	SimpleVector[] coordinates = new SimpleVector[4];
	float scale;
	Texture textures[];

	public Wall(SimpleVector[] coordinates, float[] uvs, float scale, int textureId){
		super(textureId);
		
		//Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.ic_launcher)), 64, 64));
		//TextureManager.getInstance().addTexture("texture", texture);
		
		for (int i = 0;i < 4; i++){
			coordinates[i].scalarMul(scale);
		}
		this.coordinates = coordinates;
		this.scale = scale;
		
		Log.i("uvs value: ", Float.toString(coordinates[3].x) + Float.toString(coordinates[3].y) + Float.toString(coordinates[3].z));
		
		//uvs represent texture locations
		//uvs[0] = xMin, uvs[1] = yMin, uvs[2] = xMax, uvs[3] =  yMax
		addTriangle(coordinates[0],uvs[0],uvs[3],coordinates[1],uvs[2],uvs[3],coordinates[2],uvs[2],uvs[1],textureId);
		addTriangle(coordinates[0],uvs[0],uvs[3],coordinates[3],uvs[0],uvs[1],coordinates[3],uvs[2],uvs[1],textureId);
		
	}
	



}
