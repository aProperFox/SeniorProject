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
	
	private Object3D object;
	
	private RigidBody body;
	//private BvhTriangleMeshShape shape;
	
	public WordObject(Object3D obj, SimpleVector toCenter, SimpleVector rotationAxis){
		super(obj);
		isStatic = true;
		centerTranslate = toCenter;
		
		object =  new Object3D(obj);/*
		object.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		object.setCollisionOptimization(Object3D.COLLISION_DETECTION_OPTIMIZED);
		object.build();
		dimensions = getDimensions();
		rotateBy(rotationAxis);
		BoxShape shape = new BoxShape(new Vector3f(0.2f,1.3f,0.5f));
		Vector3f localInertia = new Vector3f(0, 0, 0);
		shape.calculateLocalInertia(12, localInertia);
		
		ms = new JPCTBulletMotionState(object);
		
		rbInfo =  new RigidBodyConstructionInfo(12, ms, shape, localInertia);
		body =  new RigidBody(rbInfo);
		body.setRestitution(0.1f);
		body.setFriction(0.50f);
		body.setDamping(0f, 0f);
		body.setUserPointer(object);
		//object.setUserObject(body);*/
		
		//object.setOrigin(new SimpleVector(0,-5,20));
		//object.setName("book");
		
	}
	
	public boolean getStaticState(){
		return isStatic;
	}
	
	public void setStatic(boolean state){
		isStatic = state;
	}
	/*
	private void setShape(){
		PolygonManager polyMan = object.getPolygonManager();
		int max = polyMan.getMaxPolygonID();
		ByteBufferVertexData vertexData = new ByteBufferVertexData();
		int j = 0;
		for(int i = 0; i < max; i++){
			vertexData.setVertex(j, polyMan.getTransformedVertex(i, 0).x, polyMan.getTransformedVertex(i, 0).y,
					polyMan.getTransformedVertex(i, 0).z);
			j++;
			vertexData.setVertex(j, polyMan.getTransformedVertex(i, 1).x, polyMan.getTransformedVertex(i, 1).y,
					polyMan.getTransformedVertex(i, 1).z);
			j++;
			vertexData.setVertex(j, polyMan.getTransformedVertex(i, 2).x, polyMan.getTransformedVertex(i, 2).y,
					polyMan.getTransformedVertex(i, 2).z);
			j++;
		}
		TriangleIndexVertexArray tri = new TriangleIndexVertexArray(max,vertexData.indexData, 1, j,vertexData.vertexData, 1);
		StridingMeshInterface iFace = tri;
		shape = new BvhTriangleMeshShape(iFace, false);
		
	}*/
	
	public SimpleVector getDimensions(){
		PolygonManager polyMan = object.getPolygonManager();
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
		return new SimpleVector(maxVerts.x - minVerts.x, maxVerts.y - minVerts.y, maxVerts.z - minVerts.z);
	}
	
	public Object3D getObject3D(){
		return object;
	}
	
	public void rotateBy(SimpleVector axes){
		object.rotateX(axes.x);
		object.rotateY(axes.y);
		object.rotateZ(axes.z);
	}
	
	public RigidBody getBody(){
		return body;
	}
	
}
