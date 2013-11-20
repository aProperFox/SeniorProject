package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;

public class Bubble extends Object3D{
	private Object3D object;
	private boolean isHolding = false;
	private int heldObjectId = -1;
	
	public Bubble(Object3D bubble){
		super(bubble);
		object = new Object3D(bubble);
		object = Primitives.getSphere(5.0f);
		object.setTransparency(5);
		object.setSpecularLighting(Object3D.SPECULAR_ENABLED);
	}
}
