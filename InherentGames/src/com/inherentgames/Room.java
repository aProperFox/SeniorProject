package com.inherentgames;

import java.util.List;

import android.util.Log;

import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;


public class Room extends World{
	private List<Wall> walls = null;
	
	public Room(int roomId) {
		setWalls(roomId);
	}
	
	
	public void setWalls(int room){
		int wallNum = 0;
		if((wallNum = getWallNumByRoom(room)) == -1){
			Log.i("Room", "Invalid room number");
		}
		
		SimpleVector[][] coords = new SimpleVector[wallNum][4];
		float[][] uvs = new float[wallNum][4];
		int textures[] = new int[4];
		
		//get walls by room number
		switch(room){
		case 0:
			textures[0] = 0;
		}
		
		//Add walls to the list
		for(int i = 0; i < wallNum; i++){
			walls.add(new Wall(coords[i],uvs[i],textures[i]));
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
