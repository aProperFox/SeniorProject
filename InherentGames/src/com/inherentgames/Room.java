package com.inherentgames;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import android.content.Context;
import android.util.Log;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
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
	private Object3D[] backpack =  new Object3D[4];
	private Object3D chalkboard;
	private Object3D[] book =  new Object3D[2];

	private int bubbleCounter = 0;
	
	private ArrayList<RigidBody> bodies = new ArrayList<RigidBody>();
	private ArrayList<RigidBody> bubbles = new ArrayList<RigidBody>();
	
	
	private Object3D pencil;
	private Object3D bubble;
	
	private Object3D deskObj;
	private Object3D chairObj;
	
	
	public Room(int roomId, Context context) {
		this.context = context.getApplicationContext();
		

		
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
				chairs[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chair.obj"), null, 3.0f));
				chairs[i].rotateX((float)Math.PI);
				chairs[i].rotateY((float)Math.PI/2);
			}
			
			
			chairs[0].setOrigin(new SimpleVector());
			chairs[1].setOrigin(new SimpleVector());
			chairs[2].setOrigin(new SimpleVector());
			chairs[3].setOrigin(new SimpleVector());
			chairs[4].setOrigin(new SimpleVector( ));
			chairs[5].setOrigin(new SimpleVector( ));
			
			
			
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
				
			deskObj = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/desk.obj"),null, 1.5f));
			addDesk(-35,-6,45);
			addDesk(-35,-6,10);
			addDesk(-35,-6,-25);
			addDesk(35,-6,45);
			addDesk(35,-6,10);
			addDesk(35,-6,-25);
			
			chairObj = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chair.obj"),null,3.0f));
			addChair(-35,2,18);
			addChair(-35,2,-17);
			addChair(-35,2,-52);
			addChair(35,2,18);
			addChair(35,2,-17);
			addChair(35,2,-52);
			
			bubble = Primitives.getSphere(5.0f);
			bubble.setSpecularLighting(Object3D.SPECULAR_ENABLED);
			bubble.setTransparency(1);
			
			chalkboard = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chalkboard.obj"), context.getResources().getAssets().open("raw/chalkboardTex.mtl"), 6.0f));
			chalkboard.rotateX((float)Math.PI);
			chalkboard.setOrigin(new SimpleVector(0,0,65));
			chalkboard.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			chalkboard.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
			addObject(chalkboard);
			
			
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
		return bodies.get(id);
	}
	
	public int getNumBubbles(){
		return bubbles.size();
	}
	
	public int getNumBodies(){
		return bodies.size();
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
			Wall wall = new Wall(new SimpleVector(0,0,75), 130, 50,"Room0Wall0");
			walls.add(wall.getWall());
			walls.get(0).setTexture("Room0Wall0");
			bodies.add(wall.getBody());
			//Second wall
			wall = new Wall(new SimpleVector(65,0,0), 150, 50,"Room0Wall0");
			walls.add(wall.getWall());
			walls.get(1).setTexture("Room0Wall0");
			bodies.add(wall.getBody());
			//Third wall
			wall = new Wall(new SimpleVector(0,0,-75), 130, 50,"Room0Wall0");
			walls.add(wall.getWall());
			walls.get(2).setTexture("Room0Wall0");
			bodies.add(wall.getBody());
			//Fourth wall
			wall = new Wall(new SimpleVector(-65,0,0), 150, 50,"Room0Wall0");
			walls.add(wall.getWall());
			walls.get(3).setTexture("Room0Wall0");
			bodies.add(wall.getBody());
			
			
			
			//Wall class and floor class to be changed to extend surface class
			floor = new Floor(new SimpleVector(130,25,150),0);
			floor.setTexture("Room0Floor");
			bodies.add(floor.getBody());
			addObject(floor.getFloor());
			ceiling = new Floor(new SimpleVector(130,-25,150),0);
			ceiling.setTexture("Room0Ceiling");
			bodies.add(ceiling.getBody());
			addObject(ceiling.getFloor());
			
			break;
			
		case 1:
			break;
		}
		
	}
	
	private void addDesk(float x, float y, float z){
		Object3D boxgfx = new Object3D(deskObj);
		boxgfx.setOrigin(new SimpleVector(x,y,z));
		boxgfx.rotateX((float)Math.PI);
		boxgfx.rotateY(3*(float)Math.PI/2);
		boxgfx.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		boxgfx.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
		boxgfx.build();
		addObject(boxgfx);/*
		Vector3f dimensions = getDimensions(deskObj);
		dimensions.scale(0.6f);
		BoxShape shape = new BoxShape(dimensions);
		Vector3f localInertia = new Vector3f(0, 0, 0);
		shape.calculateLocalInertia(12, localInertia);
		
		JPCTBulletMotionState ms = new JPCTBulletMotionState(boxgfx);
		
		Transform tr = new Transform();
		
		tr.setRotation(new Quat4f(0,(float)Math.PI,0,(float)Math.PI));
		RigidBodyConstructionInfo rbInfo =  new RigidBodyConstructionInfo(12, ms, shape, localInertia);
		RigidBody body =  new RigidBody(rbInfo);
		body.setRestitution(0.1f);
		body.setFriction(0.50f);
		body.setDamping(0f, 0f);
		body.setCenterOfMassTransform(tr);
		body.translate(new Vector3f(boxgfx.getOrigin().x + boxgfx.getCenter().x,-boxgfx.getOrigin().y - boxgfx.getCenter().y,-boxgfx.getOrigin().z + boxgfx.getCenter().z));
		body.setUserPointer(boxgfx);
		boxgfx.setUserObject(body);
		bodies.add(body);*/
	}
	
	private void addChair(float x, float y, float z){
		Object3D boxgfx = new Object3D(chairObj);
		boxgfx.setOrigin(new SimpleVector(x,y,z));
		boxgfx.rotateX((float)Math.PI);
		boxgfx.rotateY((float)Math.PI/2);
		boxgfx.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		boxgfx.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
		boxgfx.build();
		addObject(boxgfx);/*
		Vector3f dimensions = getDimensions(chairObj);
		dimensions.scale(0.6f);
		dimensions.x *= 0.5f;
		dimensions.z *= 0.5f;
		BoxShape shape = new BoxShape(dimensions);
		Vector3f localInertia = new Vector3f(0, 0, 0);
		shape.calculateLocalInertia(12, localInertia);
		
		JPCTBulletMotionState ms = new JPCTBulletMotionState(boxgfx);
		
		Transform tr = new Transform();
		
		tr.setRotation(new Quat4f(0,(float)Math.PI,0,-(float)Math.PI));
		RigidBodyConstructionInfo rbInfo =  new RigidBodyConstructionInfo(12, ms, shape, localInertia);
		RigidBody body =  new RigidBody(rbInfo);
		body.setRestitution(0.1f);
		body.setFriction(0.50f);
		body.setDamping(0f, 0f);
		body.setCenterOfMassTransform(tr);
		body.translate(new Vector3f(boxgfx.getOrigin().x + boxgfx.getCenter().x,-boxgfx.getOrigin().y - boxgfx.getCenter().y,-boxgfx.getOrigin().z - boxgfx.getCenter().z));
		body.setUserPointer(boxgfx);
		boxgfx.setUserObject(body);
		bodies.add(body);*/
	}
	
	public RigidBody addBubble(SimpleVector position) {
		Log.i("BUBBLE TIMEEEEEEEEEEE", "" + position.toString());
		SphereShape shape = new SphereShape(1.5f);
		float mass = 12;
		Vector3f localInertia = new Vector3f(0, 0, 0);
		shape.calculateLocalInertia(mass, localInertia);

		Object3D spheregfx = new Object3D(bubble);

		spheregfx.translate(position);
		spheregfx.build();
		spheregfx.setCollisionMode(Object3D.COLLISION_CHECK_SELF);
		spheregfx.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
		int id = addObject(spheregfx);
		Object3D tempBubble = getObject(id);
		bubbleCounter += 1;
		String name = "Bubble" + bubbleCounter;
		tempBubble.setName(name);
		JPCTBulletMotionState ms = new JPCTBulletMotionState(spheregfx);

		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, ms, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setRestitution(0.1f);
		body.setFriction(0.01f);
		body.setDamping(0f, 1.0f);
		body.setGravity(new Vector3f(0,0,0));
		body.setUserPointer(tempBubble);
		
		tempBubble.setUserObject(body);
		bubbles.add(body);

		return body;
	}
	
	public int getNumObjectsByRoomId(int room){
		int num = 0;
		switch(room){
		case 0:
			num = 19;
			break;
		}
		return num;
	}
	
	public void decrementBubbleCounter(){
		bubbleCounter -= 1;
	}
	
	public int getBubbleCounter(){
		return bubbleCounter;
	}
	
	public Vector3f getDimensions(Object3D obj){
		PolygonManager polyMan = obj.getPolygonManager();
		int polygons = polyMan.getMaxPolygonID();
		Vector3f minVerts = new Vector3f(1000,1000,1000);
		Vector3f maxVerts = new Vector3f(-1000,-1000,-1000);
		for(int i = 0; i < polygons; i++){
			for(int j = 0; j < 3; j++){
				if(minVerts.x > polyMan.getTransformedVertex(i, j).x)
					minVerts.x = polyMan.getTransformedVertex(i,j).x;
				if(maxVerts.x < polyMan.getTransformedVertex(i, j).x)
					maxVerts.x = polyMan.getTransformedVertex(i, j).x;
				if(minVerts.y > polyMan.getTransformedVertex(i, j).y)
					minVerts.y = polyMan.getTransformedVertex(i,j).y;
				if(maxVerts.y < polyMan.getTransformedVertex(i, j).y)
					maxVerts.y = polyMan.getTransformedVertex(i, j).y;
				if(minVerts.z > polyMan.getTransformedVertex(i, j).z)
					minVerts.z = polyMan.getTransformedVertex(i,j).z;
				if(maxVerts.z < polyMan.getTransformedVertex(i, j).z)
					maxVerts.z = polyMan.getTransformedVertex(i, j).z;
			}
		}
		return new Vector3f(maxVerts.x - minVerts.x, maxVerts.y - minVerts.y, maxVerts.z - minVerts.z);
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
