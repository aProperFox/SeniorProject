package com.inherentgames;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;


public class Room extends World{
	private List<Object3D> walls = null;
	Context context;
	public Object3D wall;
	
	private int wallNum = 0;
	
	public Room(int roomId) {
		/*setWalls(roomId);
		for(int i = 0; i < walls.size(); i++)
		{
			addObject(walls.get(i));
		}*/
		wall = Primitives.getPlane(1, 150);
		wall.setOrigin(new SimpleVector(0,0,75));
		wall.strip();
		wall.build();
		wall.setAdditionalColor(200,0,0);
		addObject(wall);
		
		wall = Primitives.getPlane(1,150);
		wall.setOrigin(new SimpleVector(75,0,0));
		wall.rotateY((float)Math.PI/2.0f);
		wall.strip();
		wall.build();
		
		wall.setAdditionalColor(0,0,200);
		addObject(wall);
		
		wall = Primitives.getPlane(1,150);
		wall.strip();
		wall.build();
		wall.setOrigin(new SimpleVector(0,0,-75));
		wall.rotateY((float)Math.PI);
		wall.setAdditionalColor(0,200,0);
		addObject(wall);
		
		wall = Primitives.getPlane(1,150);
		wall.strip();
		wall.build();
		wall.setOrigin(new SimpleVector(-75,0,0));
		wall.rotateY(3.0f*(float)Math.PI/2.0f);
		wall.setAdditionalColor(100,250,250);
		addObject(wall);
		
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
			textures[1] = 0;
			textures[2] = 0;
			textures[3] = 0;
			//First wall
			coords[0][0] = new SimpleVector(-50,-50, 50); 
			coords[0][1] = new SimpleVector( 50,-50, 50); 
			coords[0][2] = new SimpleVector( 50, 50, 50); 
			coords[0][3] = new SimpleVector(-50, 50, 50);
			uvs[0][0] = 0;
			uvs[0][1] = 0;
			uvs[0][2] = 1;
			uvs[0][3] = 1;
			//Second wall
			coords[1][0] = new SimpleVector( 50,-50, 50);
			coords[1][1] = new SimpleVector( 50,-50,-50);
			coords[1][2] = new SimpleVector( 50, 50,-50); 
			coords[1][3] = new SimpleVector( 50, 50, 50);
			uvs[1][0] = 0;
			uvs[1][1] = 0;
			uvs[1][2] = 1;
			uvs[1][3] = 1;
			//Third wall
			coords[2][0] = new SimpleVector( 50,-50,-50); 
			coords[2][1] = new SimpleVector(-50,-50,-50); 
			coords[2][2] = new SimpleVector(-50, 50,-50); 
			coords[2][3] = new SimpleVector( 50, 50,-50);
			uvs[2][0] = 0;
			uvs[2][1] = 0;
			uvs[2][2] = 1;
			uvs[2][3] = 1;
			//Fourth wall
			coords[3][0] = new SimpleVector(-50,-50,-50); 
			coords[3][1] = new SimpleVector(-50,-50, 50); 
			coords[3][2] = new SimpleVector(-50, 50, 50); 
			coords[3][3] = new SimpleVector(-50, 50,-50);
			uvs[3][0] = 0;
			uvs[3][1] = 0;
			uvs[3][2] = 1;
			uvs[3][3] = 1;
			break;
		case 1:
			textures[0] = 0;
			textures[1] = 0;
			textures[2] = 0;
			textures[3] = 0;
			//First wall
			coords[0][0] = new SimpleVector(-50,-50, 50); 
			coords[0][1] = new SimpleVector( 50,-50, 50); 
			coords[0][2] = new SimpleVector( 50, 50, 50); 
			coords[0][3] = new SimpleVector(-50, 50, 50);
			uvs[0][0] = 0;
			uvs[0][1] = 0;
			uvs[0][2] = 1;
			uvs[0][3] = 1;
			//Second wall
			coords[1][0] = new SimpleVector( 50,-50, 50);
			coords[1][1] = new SimpleVector( 50,-50,-50);
			coords[1][2] = new SimpleVector( 50, 50,-50); 
			coords[1][3] = new SimpleVector( 50, 50, 50);
			uvs[1][0] = 0;
			uvs[1][1] = 0;
			uvs[1][2] = 1;
			uvs[1][3] = 1;
			//Third wall
			coords[2][0] = new SimpleVector( 50,-50,-50); 
			coords[2][1] = new SimpleVector(-50,-50,-50); 
			coords[2][2] = new SimpleVector(-50, 50,-50); 
			coords[2][3] = new SimpleVector( 50, 50,-50);
			uvs[2][0] = 0;
			uvs[2][1] = 0;
			uvs[2][2] = 1;
			uvs[2][3] = 1;
			//Fourth wall
			coords[3][0] = new SimpleVector(-50,-50,-50); 
			coords[3][1] = new SimpleVector(-50,-50, 50); 
			coords[3][2] = new SimpleVector(-50, 50, 50); 
			coords[3][3] = new SimpleVector(-50, 50,-50);
			uvs[3][0] = 0;
			uvs[3][1] = 0;
			uvs[3][2] = 1;
			uvs[3][3] = 1;
			break;
		}
		
		//Add walls to the list
		for(int i = 0; i < wallNum; i++){
			walls.add(new Wall(coords[i],uvs[i], 0.1f,textures[i]));
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
