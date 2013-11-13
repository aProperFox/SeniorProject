package com.inherentgames;

import javax.vecmath.Vector3f;

import android.util.Log;

import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.Transform;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.SimpleVector;

public class WordObject extends Object3D{
	
	private boolean isStatic;
	private SimpleVector centerTranslate;
	private SimpleVector dimensions;
	
	private Transform startTransform;
	private JPCTBulletMotionState ms;
	private RigidBodyConstructionInfo rbInfo;
	
	
	private RigidBody body;
	//private BvhTriangleMeshShape shape;
	
	public WordObject(Object3D obj, SimpleVector toCenter, SimpleVector rotationAxis){
		super(obj);
		isStatic = true;
		centerTranslate = toCenter;
		setDimensions();
		rotateBy(rotationAxis);
	}
	
	public boolean getStaticState(){
		return isStatic;
	}
	
	public void setStatic(boolean state){
		isStatic = state;
	}
	
	public void setDimensions(){
		PolygonManager polyMan = this.getPolygonManager();
		int polygons = polyMan.getMaxPolygonID();
		SimpleVector minVerts = new SimpleVector(1000,1000,1000);
		SimpleVector maxVerts = new SimpleVector(-1000,-1000,-1000);
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
		dimensions = new SimpleVector(maxVerts.x - minVerts.x, maxVerts.y - minVerts.y, maxVerts.z - minVerts.z);
	}
	
	public SimpleVector getDimensions(){
		return dimensions;
	}
	
	
	public void rotateBy(SimpleVector axes){
		this.rotateX(axes.x);
		this.rotateY(axes.y);
		this.rotateZ(axes.z);
	}
	
	public RigidBody getBody(){
		return body;
	}
	
}
