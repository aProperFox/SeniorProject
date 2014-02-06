package com.inherentgames;

import java.io.IOException;
import java.util.ArrayList;

import javax.vecmath.Vector3f;

import android.content.Context;
import android.util.Log;

import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Clock;
import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;


public class Room extends World {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9088044018714661773L;
	public static RGBColor bubbleRed = new RGBColor(226,51,34);
	public static RGBColor bubbleBlue = new RGBColor(132,211,245);
	
	private ArrayList<Object3D> walls = new ArrayList<Object3D>();
	Context context;
	public Wall wall;
	public Floor floor;
	public Floor ceiling;
	private ArrayList<RigidBody> bodies = new ArrayList<RigidBody>();
	private ArrayList<RigidBody> bubbles = new ArrayList<RigidBody>();
	private ArrayList<Bubble> bubbleObjects;
	private ArrayList<WordObject> wordObjects;
	private ArrayList<WordObject> roomObjects;
	private ArrayList<String> roomObjectWords;
	
	Clock clock;
	
	private RGBColor bubbleColor;
	
	public Room(int roomId, Context context) {
		this.context = context.getApplicationContext();
		
		roomObjectWords = new ArrayList<String>();
		clock = new Clock();
		wordObjects = new ArrayList<WordObject>();
		bubbleObjects = new ArrayList<Bubble>();
		roomObjects = new ArrayList<WordObject>();
		
		bubbleColor = bubbleBlue;
		
		//Adds walls to list 'walls' based on room Id, also sets wallNum variable
		setSurfaces(roomId);
		for(int i = 0; i < walls.size(); i++)
		{
			//Adds all walls to world
			addObject(walls.get(i));
		}

		setObjects(roomId);
		
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
	
	public void removeBubble(Bubble bubble){
		bubbleObjects.remove(bubble);
		bubbles.remove(bubble.getLocalBodyIndex());
		removeObject(bubble);
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
			//First wall
			wall = new Wall(new SimpleVector(0,0,75), 130, 50,"Room0Wall0");
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
		}
		
	}
	
	public void setObjects(int roomId){
		switch (roomId){
		case 0:
			try {
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/desk.obj"),
						context.getResources().getAssets().open("raw/desk.mtl"), 1.5f)),
						new SimpleVector((float)Math.PI,-(float)Math.PI/2,0),"Desk",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chair.obj"),
						context.getResources().getAssets().open("raw/chair.mtl"),3.0f)),
						new SimpleVector((float)Math.PI,(float)Math.PI/2,0),"Chair",WordObject.FEMININE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/chalkboard.obj"),
						context.getResources().getAssets().open("raw/chalkboard.mtl"), 6.0f)),new SimpleVector(0,(float)Math.PI,(float)Math.PI),"Chalkboard",WordObject.FEMININE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/backpack.obj"),
						context.getResources().getAssets().open("raw/backpack.mtl"), 2.0f)),new SimpleVector(0,0.8f*(float)Math.PI/2,(float)Math.PI),"Backpack",WordObject.FEMININE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/calendar.obj"),
						context.getResources().getAssets().open("raw/calendar.mtl"), 1.0f)),new SimpleVector(0,(float)Math.PI/2.0f,(float)Math.PI),"Calendar",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/clock.obj"),
						context.getResources().getAssets().open("raw/clock.mtl"), 1.0f)),new SimpleVector(0,0,(float)Math.PI),"Clock",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/door.obj"),
						context.getResources().getAssets().open("raw/door.mtl"), 4.0f)),new SimpleVector(0,0,0),"Door",WordObject.FEMININE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/book.obj"),
						context.getResources().getAssets().open("raw/book.mtl"), 1.0f)),new SimpleVector(0,0,0),"Book",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/paper.obj"),
						context.getResources().getAssets().open("raw/paper.mtl"), 1.0f)),new SimpleVector(0,(float)Math.PI/2.0f,(float)Math.PI),"Paper",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/window.obj"),
						context.getResources().getAssets().open("raw/window.mtl"), 1.0f)),new SimpleVector(0,(float)Math.PI/2.0f,0),"Window",WordObject.FEMININE));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Desks 0
			addWordObject(-35,-6,25, roomObjects.get(0), "Desk");
			addWordObject(-35,-6,-10, roomObjects.get(0), "Desk");
			addWordObject(35,-6,25, roomObjects.get(0), "Desk");
			addWordObject(35,-6,-10, roomObjects.get(0), "Desk");
			//Chairs 1
			addWordObject(-35,2,5, roomObjects.get(1), "Chair");
			addWordObject(-35,2,-30, roomObjects.get(1), "Chair");
			addWordObject(35,2,5, roomObjects.get(1), "Chair");
			addWordObject(35,2,-30, roomObjects.get(1), "Chair");
			//Chalk board 2
			addWordObject(0,-13,65,roomObjects.get(2), "Chalkboard");
			//BackPacks 3
			addWordObject(-15,15,25,roomObjects.get(3), "Backpack");
			addWordObject(35,4,20,roomObjects.get(3), "Backpack");
			addWordObject(17,15,-25,roomObjects.get(3), "Backpack");
			//Calendar 4
			addWordObject(60,-5,20,roomObjects.get(4), "Calendar");
			//Clock 5
			addWordObject(-30,-20,-71, roomObjects.get(5), "Clock");
			//Door 6
			addWordObject(0,-19,-74, roomObjects.get(6), "Door");
			//Book 7
			addWordObject(10,15,20, roomObjects.get(7), "Book");
			addWordObject(-10,15,30, roomObjects.get(7), "Book");
			addWordObject(-20,15,-10, roomObjects.get(7), "Book");
			addWordObject(30,15,-50, roomObjects.get(7), "Book");
			//Paper 8
			addWordObject(0,15,10, roomObjects.get(8), "Paper");
			addWordObject(0,14.25f,10, roomObjects.get(8), "Paper");
			addWordObject(0,14.5f,10, roomObjects.get(8), "Paper");
			addWordObject(0,14.75f,10, roomObjects.get(8), "Paper");
			//Window 9
			addWordObject(-64,-7,0, roomObjects.get(9), "Window");
			
			break;
		case 1:
			try {
				
				//Table = 0
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/restaurant-table.obj"),null, 1.5f)),
						new SimpleVector(0,0,0),"Table",WordObject.FEMININE));
				//Chair1 = 1
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/restaurant-chair.obj"),null,3.0f)),
						new SimpleVector((float)Math.PI,(float)Math.PI/2,0),"Chair",WordObject.FEMININE));
				//Chair2 = 2
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/restaurant-chair2.obj"),
						context.getResources().getAssets().open("raw/chalkboardTex.mtl"), 6.0f)),new SimpleVector(0,0,0),"Chair",WordObject.FEMININE));
				//Fork = 3
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/restaurant-fork.obj"),
						null, 2.0f)),new SimpleVector(0,0,0),"Fork",WordObject.MASCULINE));
				//Knife = 4
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/restaurant-knife.obj"),
						null, 2.0f)),new SimpleVector(0,0,0),"Knife",WordObject.MASCULINE));
				//Spoon = 5
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/restaurant-spoon.obj"),
						null, 2.0f)),new SimpleVector(0,0,0),"Spoon",WordObject.FEMININE));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			addWordObject(0,0,45, roomObjects.get(0), "Table");
			break;
		}
	}
	
	private void addWordObject(float x, float y, float z, WordObject wordObject, String name){
		//Creates a new WordObject from the generic roomObject
		//and adds it to the Room and wordObjects ArrayList
		Log.i("olsontl", "Adding object " + name);
		WordObject object = new WordObject(wordObject);
		object.setCenter(SimpleVector.ORIGIN);
		object.setOrigin(new SimpleVector(x,y,z));
		object.setName(name);
		object.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		object.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
		if(TextureManager.getInstance().containsTexture(name)){
			object.setTexture(name);
		}
		if(!roomObjectWords.contains(name)){
			roomObjectWords.add(name);
		}
		object.build();
		//object.setRotationPivot(pivot);
		addObject(object);
		Log.i("olsontl", "Object " + name + " added");
	}
	
	
	public RigidBody addBubble(SimpleVector position) {
		//Creates a new bubble Object and adds it to the room
		SphereShape shape = new SphereShape(5.0f);
		float mass = 12;
		Vector3f localInertia = new Vector3f(0, 0, 0);
		shape.calculateLocalInertia(mass, localInertia);
		int article = 0;
		if(bubbleColor == bubbleRed){
			article = WordObject.FEMININE;
		}
		else if(bubbleColor == bubbleBlue){
			article = WordObject.MASCULINE;
		}
		Bubble bubble = new Bubble(position, article, clock.getTimeMilliseconds());
		bubble.setAdditionalColor(bubbleColor);
		bubble.setTexture("Default");
		bubble.calcTextureWrapSpherical();
		
		int objectId = addObject(bubble);
		bubble.setObjectId(objectId);
		JPCTBulletMotionState ms = new JPCTBulletMotionState(bubble);

		//Creates a RigidBody and adds it to the DynamicWorld
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, ms, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setRestitution(0.01f);
		body.setFriction(0.01f);
		body.setDamping(0f, 0f);
		body.setGravity(new Vector3f(0,0,0));
		body.setUserPointer(getObject(bubble.getObjectId()));
		getObject(bubble.getObjectId()).setUserObject(body);
		bubbles.add(body);
		bubble.setLocalBodyIndex(bubbles.size()-1);
		bubbleObjects.add(bubble);

		return body;
	}
	
	public void setBubbleColor(int state){
		if(state == Bubble.MASCULINE){
			bubbleColor = bubbleBlue;
		}
		else if(state == Bubble.FEMININE){
			bubbleColor = bubbleRed;
		}
	}
	
	public Bubble getBubble(int index){
		return bubbleObjects.get(index);
	}
	
	public ArrayList<Bubble> getBubbleObjects(){
		return bubbleObjects;
	}
	
	public String getBubbleArticle(){
		if(bubbleColor == bubbleBlue)
			return "El";
		else if(bubbleColor == bubbleRed)
			return "La";
		return "Nada";
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
	
	public RGBColor getBubbleColor(){
		return bubbleColor;
	}
	
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
	
	public boolean isBubbleType(int id){
		for(Bubble bubble : bubbleObjects){
			if(bubble.getObjectId() == id){
				return true;
			}
		}
		return false;
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
	
	public ArrayList getRoomObjectWords(){
		return roomObjectWords;
	}
	
	@Override
	public void dispose(){
		roomObjectWords.clear();
		wordObjects.clear();
		bubbleObjects.clear();
		roomObjects.clear();
		bubbles.clear();
		bodies.clear();
		walls.clear();
		
		super.dispose();
	}
}

