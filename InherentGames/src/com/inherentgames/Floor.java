package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class Floor{
	SimpleVector[] coordinates = new SimpleVector[4];
	String type = "";
	
	Object3D floor = new Object3D(2);
	
	public Floor(SimpleVector size, int textureId){
		if(size.y < 0){
			type = "ceiling";
		}
		if(size.y > 0){
			type = "floor";
		}
		coordinates[0] = new SimpleVector(-size.x/2,size.y,+size.z/2);
		coordinates[1] = new SimpleVector(+size.x/2,size.y,+size.z/2);
		coordinates[2] = new SimpleVector(+size.x/2,size.y,-size.z/2);
		coordinates[3] = new SimpleVector(-size.x/2,size.y,-size.z/2);
		
		if(type == "ceiling")
			floor.setAdditionalColor(100, 100, 100);
		else if(type == "floor")
			floor.setAdditionalColor(5, 5, 5);
		else
			floor.setAdditionalColor(0, 0, 0);
		floor.addTriangle(coordinates[1],1,1,coordinates[0],0,1,coordinates[2],1,0,textureId);
		floor.addTriangle(coordinates[0],0,1,coordinates[3],0,0,coordinates[2],1,0,textureId);
	}
	
	public Object3D getFloor(){
		return floor;
	}
}
