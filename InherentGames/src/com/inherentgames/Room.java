package com.inherentgames;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import android.content.Context;
import android.util.Log;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;


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
	
	private RigidBody[] bodies = new RigidBody[3];
	
	private int numObjects;
	
	private Object3D pencil;
	private Object3D bubble;
	
	private WordObject[] wordObjects;
	
	public Room(int roomId, Context context) {
		this.context = context.getApplicationContext();
		numObjects = getNumObjectsByRoomId(roomId);
		wordObjects = new WordObject[numObjects];
		

		
		//Adds walls to list 'walls' based on room Id, also sets wallNum variable
		setSurfaces(roomId);
		for(int i = 0; i < walls.size(); i++)
		{
			//Adds all walls to world
			addObject(walls.get(i));
		}

		
		try{
			/*
			for(int i = 0; i < 6; i++){
				desks[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/desk.obj"), null, 1.5f));
				desks[i].rotateX((float)Math.PI);
				desks[i].rotateY(3*(float)Math.PI/2);
				chairs[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chair.obj"), null, 3.0f));
				chairs[i].rotateX((float)Math.PI);
				chairs[i].rotateY((float)Math.PI/2);
			}
			
			/////~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

			/////~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

			
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
			
			
			
			for(int i = 0; i < 6; i++){
				addObject(desks[i]);
				addObject(chairs[i]);
			}
			
			for(int i = 0; i < 4; i++){
				backpack[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/backpack.obj"), context.getResources().getAssets().open("raw/backpackTex.mtl"), 2.0f));
			}
			
			
			backpack[0].setOrigin(new SimpleVector(-15,15,45));
			backpack[0].rotateY(1.2f*(float)Math.PI/2);
			backpack[0].setTransparency(5);
			backpack[1].setOrigin(new SimpleVector(35,5,40));
			backpack[1].rotateY((float)Math.PI);
			backpack[2].setOrigin(new SimpleVector(-30,-3,1));
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
			
			
			bubble = Primitives.getSphere(10f);
			bubble.setOrigin(new SimpleVector(-30,0,0));
			bubble.setAdditionalColor(255,255,255);
			bubble.setTransparency(5);
			bubble.enableCollisionListeners();
			
			addObject(bubble);*/
				
			wordObjects[0] = new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/book.obj"),
						null, 0.3f)),new SimpleVector(0,0,0), new SimpleVector(0,(float)Math.PI,(float)Math.PI/2));
			
			Object3D boxgfx = new Object3D(wordObjects[0].getObject3D());
			boxgfx.translate(1,-5,25);
			boxgfx.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			boxgfx.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
			boxgfx.build();
			addObject(boxgfx);
			
			BoxShape shape = new BoxShape(new Vector3f(0.2f,1.3f,0.5f));
			Vector3f localInertia = new Vector3f(0, 0, 0);
			shape.calculateLocalInertia(12, localInertia);
			
			JPCTBulletMotionState ms = new JPCTBulletMotionState(boxgfx);
			
			RigidBodyConstructionInfo rbInfo =  new RigidBodyConstructionInfo(12, ms, shape, localInertia);
			RigidBody body =  new RigidBody(rbInfo);
			body.setRestitution(0.1f);
			body.setFriction(0.50f);
			body.setDamping(0f, 0f);
			
			body.setUserPointer(boxgfx);
			boxgfx.setUserObject(body);
			bodies[2] = body;
		
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
	
	public RigidBody getBody(int id){
		return bodies[id];
	}
	
	public void setSurfaces(int room){
		//If room id is not defined in getWallNumByRoom, returns an error
		if(getWallNumByRoomId(room) == -1){
			Log.i("Room", "Invalid room number");
		}
		
		//set walls by room number
		switch(room){
		case 0:		

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
			floor.setTexture("Room0Floor");
			bodies[0] = floor.getBody();
			addObject(floor.getFloor());
			ceiling = new Floor(new SimpleVector(130,-25,150),0);
			ceiling.setTexture("Room0Ceiling");
			bodies[1] = ceiling.getBody();
			addObject(ceiling.getFloor());
			
			break;
			
		case 1:
			break;
		}
		
	}
	
	/*public DiscreteDynamicsWorld getDynamicWorld(){
		return dynamicWorld;
	}*/
	
	public int getNumObjectsByRoomId(int room){
		int num = 0;
		switch(room){
		case 0:
			num = 19;
			break;
		}
		return num;
	}
	
	
	public int getWallNumByRoomId(int room){
		int num = -1;
		switch(room){
		case 0:
			num = 4;
		}
		
		return num;
	}

}
