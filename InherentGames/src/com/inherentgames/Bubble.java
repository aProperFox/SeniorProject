package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class Bubble extends WordObject{
	public static int MASCULINE = 0;
	public static int FEMININE = 1;
	
	private boolean isHolding;
	private int heldObjectId;
	private int objectId= -1;
	private int bodyIndex = -1;
	
	public Bubble(SimpleVector translation){
		super(Primitives.getSphere(5.0f),new SimpleVector(0,0,0),"Bubble","");
		setTransparency(5);
		setSpecularLighting(Object3D.SPECULAR_ENABLED);
		translate(translation);
		build();
		setCollisionMode(Object3D.COLLISION_CHECK_SELF);
		setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
		
		isHolding = false;
		heldObjectId = -1;
	}
	
	public boolean isHolding(){
		return isHolding;
	}
	
	public void setHeldObjectId(int id){
		heldObjectId = id;
		isHolding = true;
	}
	
	public int getHeldObjectId(){
		return heldObjectId;
	}
	
	public int getBodyIndex(){
		return bodyIndex;
	}
	
	public void setBodyIndex(int index){
		bodyIndex = index;
	}
	
	public int getObjectId(){
		return objectId;
	}
	
	public void setObjectId(int id){
		objectId = id;
	}
}
