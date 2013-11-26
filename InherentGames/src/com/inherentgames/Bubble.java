package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;

public class Bubble extends WordObject{
	private boolean isHolding;
	private int heldObjectId;
	
	public Bubble(){
		super(Primitives.getSphere(5.0f),new SimpleVector(0,0,0),"Bubble","");
		setTransparency(5);
		setSpecularLighting(Object3D.SPECULAR_ENABLED);
		isHolding = false;
		heldObjectId = -1;
	}
	
	public boolean isHolding(){
		return isHolding;
	}
	
	public int getHeldObjectId(){
		return heldObjectId;
	}
}
