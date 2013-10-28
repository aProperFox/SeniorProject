package com.inherentgames;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;


public class Room extends World{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9088044018714661773L;
	private ArrayList<Object3D> walls = new ArrayList<Object3D>();
	Context context;
	public Wall wall;
	public Floor floor;
	public Floor ceiling;
	
	public Room(int roomId, Context context) {
		this.context = context;
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
		return new SimpleVector(0,-20,0);
	}
	
	public void setSurfaces(int room){
		//If room id is not defined in getWallNumByRoom, returns an error
		if(getWallNumByRoom(room) == -1){
			Log.i("Room", "Invalid room number");
		}
		//texture array for walls
		Texture textures[]; 
		
		//get walls by room number
		switch(room){
		case 0:		
			textures = new Texture[6];
			//set textures
				//Walls
			textures[0] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.walls)), 1024, 1024));
			textures[1] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall1)), 1024, 1024));
			textures[2] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall2)), 1024, 1024));
			textures[3] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall3)), 1024, 1024));
				//Floor
			textures[4] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0floor)), 1024, 1024));
				//Ceiling
			textures[5] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0ceiling)), 1024, 1024));
			
			TextureManager.getInstance().addTexture("Room0Wall0", textures[0]);
			TextureManager.getInstance().addTexture("Room0Wall1", textures[1]);
			TextureManager.getInstance().addTexture("Room0Wall2", textures[2]);
			TextureManager.getInstance().addTexture("Room0Wall3", textures[3]);
			TextureManager.getInstance().addTexture("Room0Floor", textures[4]);
			TextureManager.getInstance().addTexture("Room0Ceiling", textures[5]);
			
			//First wall
			walls.add(new Wall(new SimpleVector(0,0,75), 100, 50,"Room0Wall0").getWall());
			walls.get(0).setTexture("Room0Wall0");
			//Second wall
			walls.add(new Wall(new SimpleVector(50,0,0), 150, 50,"Room0Wall1").getWall());
			walls.get(1).setTexture("Room0Wall1");
			//Third wall
			walls.add(new Wall(new SimpleVector(0,0,-75), 100, 50,"Room0Wall2").getWall());
			walls.get(2).setTexture("Room0Wall2");
			//Fourth wall
			walls.add(new Wall(new SimpleVector(-50,0,0), 150, 50,"Room0Wall3").getWall());
			walls.get(3).setTexture("Room0Wall3");
			
			//Wall class and floor class to be changed to extend surface class
			floor = new Floor(new SimpleVector(100,25,150),0);
			floor.floor.setTexture("Room0Floor");
			addObject(floor.getFloor());
			ceiling = new Floor(new SimpleVector(100,-25,150),0);
			ceiling.floor.setTexture("Room0Ceiling");
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
