package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;

public class Wall extends Object3D{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SimpleVector[] coordinates = new SimpleVector[4];
	float scale;
	Texture texture;

	public Wall(SimpleVector[] coordinates, float[] uvs, int textureId){
		super(textureId);
		for (int i = 0;i < 4; i++){
			coordinates[i].x *= scale;
			coordinates[i].y *= scale; 
			coordinates[i].z *= scale; 
		}
		this.coordinates = coordinates;
		this.scale = scale;
		this.texture = texture;
		//uvs represent texture locations
		//uvs[0] = xMin, uvs[1] = yMin, uvs[2] = xMax, uvs[3] =  yMax
		addTriangle(coordinates[0],uvs[0],uvs[3],coordinates[1],uvs[2],uvs[3],coordinates[3],uvs[2],uvs[1],0);
		addTriangle(coordinates[0],uvs[0],uvs[3],coordinates[2],uvs[0],uvs[1],coordinates[3],uvs[2],uvs[1],0);
		
	}
	
	

}
