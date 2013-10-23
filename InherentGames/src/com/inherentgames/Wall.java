package com.inherentgames;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;

public class Wall extends Surface{
	
	
	private SimpleVector[] coordinates = new SimpleVector[4];
	private Texture textures[];
	private SimpleVector origin;
	private float width;
	private float height;
	private Object3D wall = new Object3D(2);
	
	Context context;

	public Wall(SimpleVector origin, float width, float height, String textureName){
		super(origin, width, height, 0);
		this.width = width;
		this.height = height;
		this.origin = origin;
		
		setCoordinates();
		
		wall.setAdditionalColor(100, 100, 100);
		//uvs represent texture locations
		//uvs[0] = xMin, uvs[1] = yMin, uvs[2] = xMax, uvs[3] =  yMax
		wall.addTriangle(coordinates[1],1,1,coordinates[0],0,1,coordinates[2],1,0);
		wall.addTriangle(coordinates[0],0,1,coordinates[3],0,0,coordinates[2],1,0);
	}
	
	public Object3D getWall(){
		return wall;
	}
	
	private void setCoordinates(){
		if(origin.x == 0 && origin.z > 0){
			coordinates[0] = new SimpleVector(origin.x-(width/2),origin.y-(height/2),origin.z);
			coordinates[1] = new SimpleVector(origin.x+(width/2),origin.y-(height/2),origin.z);
			coordinates[2] = new SimpleVector(origin.x+(width/2),origin.y+(height/2),origin.z);
			coordinates[3] = new SimpleVector(origin.x-(width/2),origin.y+(height/2),origin.z);
		}
		else if(origin.x > 0 && origin.z == 0){
			coordinates[0] = new SimpleVector(origin.x,origin.y-(height/2),origin.z+(width/2));
			coordinates[1] = new SimpleVector(origin.x,origin.y-(height/2),origin.z-(width/2));
			coordinates[2] = new SimpleVector(origin.x,origin.y+(height/2),origin.z-(width/2));
			coordinates[3] = new SimpleVector(origin.x,origin.y+(height/2),origin.z+(width/2));
		}
		if(origin.x == 0 && origin.z < 0){
			coordinates[0] = new SimpleVector(origin.x+(width/2),origin.y-(height/2),origin.z);
			coordinates[1] = new SimpleVector(origin.x-(width/2),origin.y-(height/2),origin.z);
			coordinates[2] = new SimpleVector(origin.x-(width/2),origin.y+(height/2),origin.z);
			coordinates[3] = new SimpleVector(origin.x+(width/2),origin.y+(height/2),origin.z);
		}
		if(origin.x < 0 && origin.z == 0){
			coordinates[0] = new SimpleVector(origin.x,origin.y-(height/2),origin.z-(width/2));
			coordinates[1] = new SimpleVector(origin.x,origin.y-(height/2),origin.z+(width/2));
			coordinates[2] = new SimpleVector(origin.x,origin.y+(height/2),origin.z+(width/2));
			coordinates[3] = new SimpleVector(origin.x,origin.y+(height/2),origin.z-(width/2));
		}
	}

}
