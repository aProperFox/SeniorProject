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
	
	TextureManager tm;
	
	private RGBColor bubbleColor;
	
	public Room(int roomId, Context context, TextureManager tm) {
		this.context = context.getApplicationContext();
		
		this.tm = tm;
		
		roomObjectWords = new ArrayList<String>();
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
			wall = new Wall(new SimpleVector(65,0,0), 150, 50,"Room0Wall1");
			walls.add(wall.getWall());
			walls.get(1).setTexture("Room0Wall1");
			bodies.add(wall.getBody());
			//Third wall
			wall = new Wall(new SimpleVector(0,0,-75), 130, 50,"Room0Wall2");
			walls.add(wall.getWall());
			walls.get(2).setTexture("Room0Wall2");
			bodies.add(wall.getBody());
			//Fourth wall
			wall = new Wall(new SimpleVector(-65,0,0), 150, 50,"Room0Wall3");
			walls.add(wall.getWall());
			walls.get(3).setTexture("Room0Wall3");
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
		roomObjectWords = new ArrayList<String>();
		switch (roomId){
		case 0:
			try {
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/desk.obj"),
						context.getResources().getAssets().open("raw/room0/desk.mtl"), 1.5f)),
						new SimpleVector((float)Math.PI,-(float)Math.PI/2,0),"Desk",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/chair.obj"),
						context.getResources().getAssets().open("raw/room0/chair.mtl"),3.0f)),
						new SimpleVector((float)Math.PI,(float)Math.PI/2,0),"Chair",WordObject.FEMININE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/chalkboard.obj"),
						context.getResources().getAssets().open("raw/room0/chalkboard.mtl"), 6.0f)),new SimpleVector(0,(float)Math.PI,(float)Math.PI),"Chalkboard",WordObject.FEMININE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/backpack.obj"),
						context.getResources().getAssets().open("raw/room0/backpack.mtl"), 2.0f)),new SimpleVector(0,0.8f*(float)Math.PI/2,(float)Math.PI),"Backpack",WordObject.FEMININE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/calendar.obj"),
						context.getResources().getAssets().open("raw/room0/calendar.mtl"), 1.0f)),new SimpleVector(0,(float)Math.PI/2.0f,(float)Math.PI),"Calendar",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/clock.obj"),
						context.getResources().getAssets().open("raw/room0/clock.mtl"), 1.0f)),new SimpleVector(0,0,(float)Math.PI),"Clock",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/door.obj"),
						context.getResources().getAssets().open("raw/room0/door.mtl"), 3.5f)),new SimpleVector(0,0,0),"Door",WordObject.FEMININE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/book.obj"),
						context.getResources().getAssets().open("raw/room0/book.mtl"), 1.0f)),new SimpleVector(0,0,0),"Book",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/paper.obj"),
						context.getResources().getAssets().open("raw/room0/paper.mtl"), 1.0f)),new SimpleVector(0,(float)Math.PI/2.0f,(float)Math.PI),"Paper",WordObject.MASCULINE));
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room0/window.obj"),
						context.getResources().getAssets().open("raw/room0/window.mtl"), 1.0f)),new SimpleVector(0,(float)Math.PI/2.0f,0),"Window",WordObject.FEMININE));
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
			addWordObject(-5,-13,-74, roomObjects.get(6), "Door");
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
			addWordObject(-64,-8,0, roomObjects.get(9), "Window");
			addWordObject(-64,-8,40, roomObjects.get(9), "Window");
			addWordObject(-64,-8,-40, roomObjects.get(9), "Window");
			
			break;
		case 1:
			try {
				
				//Bill = 0
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/bill.obj"),
						context.getResources().getAssets().open("raw/room1/bill.mtl"), 1.0f)), new SimpleVector(0,0,0),"Bill",WordObject.FEMININE));
				//Bread = 1
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/bread.obj"),
						context.getResources().getAssets().open("raw/room1/bread.mtl"), 0.75f)), new SimpleVector((float)Math.PI,0,0),"Bread",WordObject.MASCULINE));
				//Cake = 2
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/cake.obj"),
						context.getResources().getAssets().open("raw/room1/cake.mtl"), 1.0f)), new SimpleVector((float)Math.PI,0,0),"Cake",WordObject.MASCULINE));
				//Cup = 3
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/cup.obj"),
						context.getResources().getAssets().open("raw/room1/cup.mtl"), 0.5f)), new SimpleVector(0,0,0),"Cup",WordObject.MASCULINE));
				/*Fork = 4
				 * Deleted currently as file was HUGE
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/fork.obj"),
						context.getResources().getAssets().open("raw/room1/fork.mtl"), 1.0f)), new SimpleVector(0,0,0),"Table",WordObject.MASCULINE));
						*/
				//Knife = 5
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/knife.obj"),
						context.getResources().getAssets().open("raw/room1/knife.mtl"), 0.3f)), new SimpleVector((float)Math.PI,0,0),"Knife",WordObject.MASCULINE));
				//Money = 6
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/money.obj"),
						context.getResources().getAssets().open("raw/room1/money.mtl"), 1.0f)), new SimpleVector(0,0,0),"Money",WordObject.MASCULINE));
				//Plate = 7
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/plate1.obj"),
						context.getResources().getAssets().open("raw/room1/plate1.mtl"), 1.0f)), new SimpleVector(0,0,0),"Plate",WordObject.MASCULINE));
				//Spoon = 8
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/spoon.obj"),
						context.getResources().getAssets().open("raw/room1/spoon.mtl"), 0.4f)), new SimpleVector(0,0,0),"Spoon",WordObject.FEMININE));
				//Table = 9
				roomObjects.add(new WordObject(Object3D.mergeAll(Loader.loadOBJ(context.getResources().getAssets().open("raw/room1/table.obj"),
						context.getResources().getAssets().open("raw/room1/table.mtl"), 1.0f)), new SimpleVector((float)Math.PI,0,0),"Table",WordObject.FEMININE));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			//Bill 0
			addWordObject(0,-5,60, roomObjects.get(0), "Bill");
			//Bread 1
			addWordObject(-50,5,50, roomObjects.get(1), "Bread");
			//Cake 2
			addWordObject(50,5,-50,roomObjects.get(2), "Cake");
			//Cup 3
			addWordObject(50,5,50,roomObjects.get(3), "Cup");
			//Fork 4
			//addWordObject(60,-5,20,roomObjects.get(4), "Fork");
			//Knife 5
			addWordObject(-25,5,-25, roomObjects.get(4), "Knife");
			//Money 6
			addWordObject(0,5,-50, roomObjects.get(5), "Money");
			//Plate 7
			addWordObject(50,5,0, roomObjects.get(6), "Plate");
			//Spoon 8
			addWordObject(0,5,25, roomObjects.get(7), "Spoon");
			//Table 9
			addWordObject(-50,5,0, roomObjects.get(8), "Table");

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
		if(tm.containsTexture(name)){
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
		Bubble bubble = new Bubble(position, article, System.currentTimeMillis());
		bubble.setAdditionalColor(bubbleColor);
		bubble.setTexture("Default");
		bubble.calcTextureWrapSpherical();
		
		addObject(bubble);
		JPCTBulletMotionState ms = new JPCTBulletMotionState(bubble);

		//Creates a RigidBody and adds it to the DynamicWorld
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, ms, shape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setRestitution(0f);
		body.setFriction(0f);
		body.setGravity(new Vector3f(0,0,0));
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
	
	public int addObject(Bubble bubble){
		//Extra function for adding a bubble to the Room
		int objectId = super.addObject((Object3D) bubble);
		Log.i("olsontl", "Bubble id when created: " + objectId);
		bubble.setObjectId(objectId);
		return objectId;
	}
	
	public WordObject getWordObject(int id){
		for(WordObject wordObject : wordObjects){
			if(wordObject.getObjectId() == id)
				return wordObject;
		}
		return null;
	}
	
	public int getNumWordObjects(){
		return wordObjects.size();
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
	
	public ArrayList<String> getRoomObjectWords(){
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

