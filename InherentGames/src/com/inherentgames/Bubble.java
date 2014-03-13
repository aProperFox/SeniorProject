package com.inherentgames;

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
	
	/**
	 * @param translation
	 * @param article
	 * @param timeInMillis
	 */
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
	
	/**
	 * @return
	 */
	public boolean isHolding(){
		return isHolding;
	}
	
	/**
	 * @param id
	 */
	public void setHeldObjectId(int id){
		heldObjectId = id;
		isHolding = true;
	}
	
	/**
	 * @return
	 */
	public int getHeldObjectId(){
		return heldObjectId;
	}
	
	/**
	 * @param index
	 */
	public void setLocalBodyIndex(int index){
		localBodyIndex = index;
	}
	
	/**
	 * @return
	 */
	public int getLocalBodyIndex(){
		return localBodyIndex;
	}
	
	/* (non-Javadoc)
	 * @see com.inherentgames.WordObject#getArticle()
	 */
	public int getArticle(){
		return article;
	}
	
	/**
	 * @return
	 */
	public int getBodyIndex(){
		return bodyIndex;
	}
	
	/**
	 * @param index
	 */
	public void setBodyIndex(int index){
		bodyIndex = index;
	}
	
	/* (non-Javadoc)
	 * @see com.inherentgames.WordObject#getObjectId()
	 */
	public int getObjectId(){
		return objectId;
	}
	
	/* (non-Javadoc)
	 * @see com.inherentgames.WordObject#setObjectId(int)
	 */
	public void setObjectId(int id){
		objectId = id;
	}
	
	/**
	 * @return
	 */
	public long getTimeCreated(){
		return timeCreated;
	}
}
