package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class Surface{
	
	
	private SimpleVector[] coordinates = new SimpleVector[4];
	private SimpleVector origin;
	private float width;
	private float height;
	private Object3D surface = new Object3D(2);

	public Surface(SimpleVector origin, float width, float height, int textureId){
		
		//Texture texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.ic_launcher)), 64, 64));
		//TextureManager.getInstance().addTexture("texture", texture);
		this.width = width;
		this.height = height;
		this.origin = origin;
		
		setCoordinates();
		
		surface.setAdditionalColor(100, 100, 100);
		//uvs represent texture locations
		//uvs[0] = xMin, uvs[1] = yMin, uvs[2] = xMax, uvs[3] =  yMax
		surface.addTriangle(coordinates[1],1,1,coordinates[0],0,1,coordinates[2],1,0,textureId);
		surface.addTriangle(coordinates[0],0,1,coordinates[3],0,0,coordinates[2],1,0,textureId);
		
	}
	
	public Object3D getSurface(){
		return surface;
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
