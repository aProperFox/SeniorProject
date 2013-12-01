package com.inherentgames;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import android.content.Context;
import android.util.Log;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
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
	private Object3D[] book =  new Object3D[2];

	private ArrayList<RigidBody> bodies = new ArrayList<RigidBody>();
	private ArrayList<RigidBody> bubbles = new ArrayList<RigidBody>();
	private ArrayList<Bubble> bubbleObjects;
	private ArrayList<WordObject> wordObjects;
	
	private Object3D pencil;
	private Object3D bubble;
	
	private WordObject deskObj;
	private WordObject chairObj;
	private WordObject chalkboardObj;

	private RGBColor bubbleColor = RGBColor.BLUE;
	
	public Room(int roomId, Context context) {
		this.context = context.getApplicationContext();
		
		wordObjects = new ArrayList<WordObject>();
		bubbleObjects = new ArrayList<Bubble>();
		//Adds walls to list 'walls' based on room Id, also sets wallNum variable
		setSurfaces(roomId);
		for(int i = 0; i < walls.size(); i++)
		{
			//Adds all walls to world
			addObject(walls.get(i));
		}

		
		try{
			/*

			backpack[i] = Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/backpack.obj"), context.getResources().getAssets().open("raw/backpackTex.mtl"), 2.0f));
			
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
			
			
			*/
				
			deskObj = new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/desk.obj"),null, 1.5f)),new SimpleVector((float)Math.PI,-(float)Math.PI/2,0),"Desk","La");
			addWordObject(-35,-6,45, deskObj);
			addWordObject(-35,-6,10, deskObj);
			addWordObject(-35,-6,-25, deskObj);
			addWordObject(35,-6,45, deskObj);
			addWordObject(35,-6,10, deskObj);
			addWordObject(35,-6,-25, deskObj);
			
			chairObj = new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chair.obj"),null,3.0f)),new SimpleVector((float)Math.PI,(float)Math.PI/2,0),"Chair","La");
			addWordObject(-35,2,25, chairObj);
			addWordObject(-35,2,-10, chairObj);
			addWordObject(-35,2,-45, chairObj);
			addWordObject(35,2,25, chairObj);
			addWordObject(35,2,-10, chairObj);
			addWordObject(35,2,-45, chairObj);
			
			chalkboardObj = new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chalkboard.obj"),
					context.getResources().getAssets().open("raw/chalkboardTex.mtl"), 6.0f)),new SimpleVector(0,(float)Math.PI,(float)Math.PI),"Chalkboard","El");
			addWordObject(0,0,65,chalkboardObj);
			
			
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
	
	private void addWordObject(float x, float y, float z, WordObject wordObject){
		//Creates a new desk WordObject from the generic Object3D 'deskObj'
		//and adds it to the Room and WwordObjects ArrayList
		WordObject object = new WordObject(wordObject);
		object.setOrigin(new SimpleVector(x,y,z));
		object.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		object.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
		object.build();
		addObject(object);
	}
	
	
	public RigidBody addBubble(SimpleVector position) {
		//Creates a new bubble Object and adds it to the room
		SphereShape shape = new SphereShape(5.0f);
		float mass = 12;
		Vector3f localInertia = new Vector3f(0, 0, 0);
		shape.calculateLocalInertia(mass, localInertia);

		Bubble bubble = new Bubble(position);
		bubble.setAdditionalColor(bubbleColor);
		int objectId = addObject(bubble);
		bubble.setObjectId(objectId);
		bubbleObjects.add(bubble);
		JPCTBulletMotionState ms = new JPCTBulletMotionState(bubble);

		//Creates a RigidBody and adds it to the DynamicWorld
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, ms, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setRestitution(0.1f);
		body.setFriction(0.01f);
		body.setDamping(0f, 1.0f);
		body.setGravity(new Vector3f(0,0,0));
		body.setUserPointer(getObject(bubble.getObjectId()));
		getObject(bubble.getObjectId()).setUserObject(body);
		bubbles.add(body);


		return body;
	}
	
	public void setBubbleColor(int state){
		if(state == Bubble.MASCULINE){
			bubbleColor = RGBColor.BLUE;
		}
		else if(state == Bubble.FEMININE){
			bubbleColor = RGBColor.RED;
		}
	}
	
	public Bubble getBubble(int index){
		return bubbleObjects.get(index);
	}
	
	public ArrayList<Bubble> getBubbleObjects(){
		return bubbleObjects;
	}
	
	public int getNumObjectsByRoomId(int room){
		//Returns the number of WordObjects per room
		int num = 0;
		switch(room){
		case 0:
			num = 19;
			break;
		}
		return num;
	}
	
	public int getBubbleCounter(){
		return bubbles.size();
	}
	
	/*@Override
	public WordObject getObject(int id){
		//Overrides the World function 'getObject' by returning a WordObject
		//rather than an Object3D
		Object3D object = super.getObject(id);
		return wordObjects.get(id);
	}*/
	
	public int addObject(WordObject wordObject){
		//Extra function for adding an object to the Room class that also adds
		//information for the WordObject in the wordObjects ArrayList
		int objectId = super.addObject((Object3D) wordObject);
		wordObject.setObjectId(objectId);
		wordObjects.add(wordObject);
		return objectId;
	}
	
	public WordObject getWordObject(int id){
		for(WordObject wordObject : wordObjects){
			if(wordObject.getObjectId() == id)
				return wordObject;
		}
		return null;
	}
	
	public Bubble getLastBubble(){
		return bubbleObjects.get(bubbleObjects.size()-1);
	}
	
	public Vector3f toVector3f(SimpleVector vector){
		//Converts a SimpleVector to a Vector3f
		return new Vector3f(vector.x,vector.y,vector.z);
	}
	
	public SimpleVector toSimpleVector(Vector3f vector){
		//Converts a Vector3f to a SimpleVector
		return new SimpleVector(vector.x,vector.y,vector.z);
	}
	
	public int getWallNumByRoomId(int room){
		//Returns the number of walls defined per room (Currently only
		//implementable with 4)
		int num = -1;
		switch(room){
		default:
			num = 4;
		}
		
		return num;
	}
	
	public int getObjectsSize(){
		int num = 0;
		for(int i = 0; i < 50; i++){
			if(this.getObject(i) != null)
				num = i;
		}
		return num;
	}
}

