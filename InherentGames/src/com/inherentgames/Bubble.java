package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;

public class Bubble {
	private Object3D bubble;
	
	public Bubble(){
		bubble = Primitives.getSphere(10f);
		bubble.setTransparency(5);
		bubble.enableCollisionListeners();
	}
}
