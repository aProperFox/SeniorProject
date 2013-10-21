package com.inherentgames;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;


public class Room extends World{
	private ArrayList<Object3D> walls = new ArrayList<Object3D>();
	Context context;
	public Wall wall;
	public Floor floor;
	public Floor ceiling;
	private int wallNum = 0;
	
	public Room(int roomId) {
		//Adds walls to list 'walls' based on room Id, also sets wallNum variable
		setSurfaces(roomId);
		for(int i = 0; i < walls.size(); i++)
		{
			//Adds all walls to world
			addObject(walls.get(i));
		}
		
	}
	
	public SimpleVector getLightLocation(int roomNum){
		//Get light location vector based on Room Id
		switch(roomNum){
		case 0:
			return new SimpleVector(0,-20,0);
		}
		//default light location
		return new SimpleVector(0,-50,0);
	}
	
	public void setSurfaces(int room){
		//If room id is not defined in getWallNumByRoom, returns an error
		if(getWallNumByRoom(room) == -1){
			Log.i("Room", "Invalid room number");
		}
		//texture array for walls
		int textures[] = new int[4];
		
		//get walls by room number
		switch(room){
		case 0:
			//set texture id
			textures[0] = 0;
			textures[1] = 0;
			textures[2] = 0;
			textures[3] = 0;
			//First wall
			walls.add(new Wall(new SimpleVector(0,0,75), 100, 50,textures[0]).getWall());
			//Second wall
			walls.add(new Wall(new SimpleVector(50,0,0), 150, 50,textures[0]).getWall());
			//Third wall
			walls.add(new Wall(new SimpleVector(0,0,-75), 100, 50,textures[0]).getWall());
			//Fourth wall
			walls.add(new Wall(new SimpleVector(-50,0,0), 150, 50,textures[0]).getWall());
			//Wall class and floor class to be changed to extend surface class
			floor = new Floor(new SimpleVector(100,25,150),0);
			addObject(floor.getFloor());
			ceiling = new Floor(new SimpleVector(100,-35,150),0);
			addObject(ceiling.getFloor());
			break;
		case 1:
			break;
		}
		
	}
	
	
	public int getWallNumByRoom(int room){
		switch(room){
		case 0:
			return 4;
		case 1:
			return 4;
		}
		//error
		return -1;
	}

}
