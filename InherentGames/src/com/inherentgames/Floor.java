package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class Floor{
	SimpleVector[] coordinates = new SimpleVector[4];
	String type = "";
	
	// This is an object
	Object3D floor = new Object3D(2);
	
	public Floor(SimpleVector size, int textureId){
		
		coordinates[0] = new SimpleVector(-size.x/2,size.y,+size.z/2);
		coordinates[1] = new SimpleVector(+size.x/2,size.y,+size.z/2);
		coordinates[2] = new SimpleVector(+size.x/2,size.y,-size.z/2);
		coordinates[3] = new SimpleVector(-size.x/2,size.y,-size.z/2);
		
		//JPCT can only render triangles created counter-clockwise relative to the camera?
		if(size.y < 0){
			type = "ceiling";
			floor.setAdditionalColor(200, 200, 200);
			floor.addTriangle(coordinates[1],1,1,coordinates[2],1,0,coordinates[0],0,1,textureId);
			floor.addTriangle(coordinates[0],0,1,coordinates[2],1,0,coordinates[3],0,0,textureId);
		}
		if(size.y > 0){
			type = "floor";
			floor.setAdditionalColor(5, 5, 5);
			floor.addTriangle(coordinates[1],1,1,coordinates[0],0,1,coordinates[2],1,0,textureId);
			floor.addTriangle(coordinates[0],0,1,coordinates[3],0,0,coordinates[2],1,0,textureId);
		}
		

	}
	
	public Object3D getFloor(){
		return floor;
	}
}
