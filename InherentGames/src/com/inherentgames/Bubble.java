package com.inherentgames;

import com.bulletphysics.linearmath.Clock;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class Bubble extends WordObject{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4649047534584957279L;
	private boolean isHolding;
	private int heldObjectId;
	private int objectId= -1;
	private int bodyIndex = -1;
	private int localBodyIndex = -1;
	private int article;
	private long timeCreated;
	
	public Bubble(SimpleVector translation, int article, long timeInMillis){
		super(Primitives.getSphere(5.0f),new SimpleVector(0,0,0),"Bubble", article);
		
		timeCreated = timeInMillis;
		
		setTransparency(8);
		setSpecularLighting(Object3D.SPECULAR_ENABLED);
		translate(translation);
		build();
		setCollisionMode(Object3D.COLLISION_CHECK_SELF);
		setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
		this.article = article;
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
	
	public void setLocalBodyIndex(int index){
		localBodyIndex = index;
	}
	
	public int getLocalBodyIndex(){
		return localBodyIndex;
	}
	
	public int getArticle(){
		return article;
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
	
	public float getTimeCreated(){
		return timeCreated;
	}
}
