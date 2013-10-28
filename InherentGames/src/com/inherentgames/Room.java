package com.inherentgames;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.threed.jpct.Loader;
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
	private Object3D[] desks = new Object3D[6];
	private Object3D[] backpack =  new Object3D[4];
	private Object3D chalkboard;
	private Object3D[] book =  new Object3D[2];
	private Object3D[] chairs =  new Object3D[6];
	
	private Object3D pencil;
	
	public Room(int roomId, Context context) {
		this.context = context;
		//Adds walls to list 'walls' based on room Id, also sets wallNum variable
		setSurfaces(roomId);
		for(int i = 0; i < walls.size(); i++)
		{
			//Adds all walls to world
			addObject(walls.get(i));
		}
		String[] that;
		try {
			that = context.getResources().getAssets().list("raw");
			for(int i = 0; i < that.length; i++){
				Log.i("!!!!!!!!!!!!!!!!!!!", that[i]);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try{
			
			for(int i = 0; i < 6; i++){
				desks[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/desk.obj"), null, 1.5f));
				desks[i].rotateX((float)Math.PI);
				desks[i].rotateY(3*(float)Math.PI/2);
				chairs[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chair.obj"), null, 3.0f));
				chairs[i].rotateX((float)Math.PI);
				chairs[i].rotateY((float)Math.PI/2);
			}
			
			for(int i = 0; i < 2; i++){
				book[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/book.obj"), null, 1.0f));
			}

			
			desks[0].setOrigin(new SimpleVector(-35,-5,45));
			desks[1].setOrigin(new SimpleVector(-35,-5,10));
			desks[2].setOrigin(new SimpleVector(-35,-5,-25));
			desks[3].setOrigin(new SimpleVector(35,-5,45));
			desks[4].setOrigin(new SimpleVector(35,-5,10));
			desks[5].setOrigin(new SimpleVector(35,-5,-25));
			
			
			chairs[0].setOrigin(new SimpleVector(-35,5,30));
			chairs[1].setOrigin(new SimpleVector(-35,5,-5));
			chairs[2].setOrigin(new SimpleVector(-35,5,-40));
			chairs[3].setOrigin(new SimpleVector( 35,5,30));
			chairs[4].setOrigin(new SimpleVector( 35,5,-5));
			chairs[5].setOrigin(new SimpleVector( 35,5,-40));
			
			book[0].setOrigin(new SimpleVector(-23,5,35));
			book[0].rotateZ((float)Math.PI/2);
			book[0].rotateY((float)Math.PI/2);
			book[1].setOrigin(new SimpleVector(19,7,6));
			book[1].rotateY(-0.9f*(float)Math.PI/2);
			
			
			for(int i = 0; i < 6; i++){
				addObject(desks[i]);
				addObject(chairs[i]);
			}
			
			for(int i = 0; i < 2; i++){
				addObject(book[i]);
			}
			
			for(int i = 0; i < 4; i++){
				backpack[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/backpack.obj"), context.getResources().getAssets().open("raw/backpackTex.mtl"), 2.0f));
			}
			backpack[0].setOrigin(new SimpleVector(-15,15,45));
			backpack[0].rotateY(1.2f*(float)Math.PI/2);
			backpack[1].setOrigin(new SimpleVector(35,5,40));
			backpack[1].rotateY((float)Math.PI);
			backpack[2].setOrigin(new SimpleVector(-30,-5,10));
			backpack[2].rotateY((float)Math.PI/2);
			backpack[2].rotateZ(-(float)Math.PI/2);
			backpack[3].setOrigin(new SimpleVector(17,15,-25));
			backpack[3].rotateY((float)Math.PI/2);
			backpack[3].rotateZ(-0.2f);
			
			for(int i = 0; i < 4; i++){
				addObject(backpack[i]);
			}
			
			chalkboard = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chalkboard.obj"), context.getResources().getAssets().open("raw/chalkboardTex.mtl"), 6.0f));
			chalkboard.rotateX((float)Math.PI);
			chalkboard.setOrigin(new SimpleVector(0,0,65));
			addObject(chalkboard);
			
			pencil = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/pencil.obj"), null, 50.0f));
			pencil.setOrigin(new SimpleVector(-19,5,6));
			addObject(pencil);
		
		
		} catch (IOException e){
			Log.i("ERROR: ", e.toString());
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
			textures[0] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall0)), 1024, 1024));
			/*textures[1] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall1)), 1024, 1024));
			textures[2] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall2)), 1024, 1024));
			textures[3] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0wall3)), 1024, 1024));*/
				//Floor
			textures[1] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0floor)), 1024, 1024));
				//Ceiling
			textures[2] = new Texture(BitmapHelper.rescale(BitmapHelper.convert(context.getResources().getDrawable(R.drawable.room0ceiling)), 1024, 1024));
			
			TextureManager.getInstance().addTexture("Room0Wall0", textures[0]);
			/*TextureManager.getInstance().addTexture("Room0Wall1", textures[1]);
			TextureManager.getInstance().addTexture("Room0Wall2", textures[2]);
			TextureManager.getInstance().addTexture("Room0Wall3", textures[3]);*/
			TextureManager.getInstance().addTexture("Room0Floor", textures[1]);
			TextureManager.getInstance().addTexture("Room0Ceiling", textures[2]);
			
			//First wall
			walls.add(new Wall(new SimpleVector(0,0,75), 130, 50,"Room0Wall0").getWall());
			walls.get(0).setTexture("Room0Wall0");
			//Second wall
			walls.add(new Wall(new SimpleVector(65,0,0), 150, 50,"Room0Wall0").getWall());
			walls.get(1).setTexture("Room0Wall0");
			//Third wall
			walls.add(new Wall(new SimpleVector(0,0,-75), 130, 50,"Room0Wall0").getWall());
			walls.get(2).setTexture("Room0Wall0");
			//Fourth wall
			walls.add(new Wall(new SimpleVector(-65,0,0), 150, 50,"Room0Wall0").getWall());
			walls.get(3).setTexture("Room0Wall0");
			
			//Wall class and floor class to be changed to extend surface class
			floor = new Floor(new SimpleVector(130,25,150),0);
			floor.floor.setTexture("Room0Floor");
			addObject(floor.getFloor());
			ceiling = new Floor(new SimpleVector(130,-25,150),0);
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
