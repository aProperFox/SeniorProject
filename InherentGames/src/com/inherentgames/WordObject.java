package com.inherentgames;

import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.SimpleVector;

public class WordObject extends Object3D {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4088731260124106298L;
	public static final int MASCULINE = 0;
	public static final int FEMININE = 1;
	
	private boolean isStatic;
	private float maxDimension;
	private int objectId = -1;
	private int article;
	private String names[] = new String[2];
	
	
	public WordObject(WordObject obj){
		super(obj.toObject3D());
		isStatic = true;
		this.maxDimension = obj.getMaxDimension();
		names[Translator.ENGLISH] = obj.getName(Translator.ENGLISH);
		this.article = obj.getArticle();
	}
	
	public WordObject(Object3D obj, SimpleVector rotationAxis, String name, int article){
		super(obj);
		isStatic = true;
		names[Translator.ENGLISH] = name;
		this.article = article;
		rotateBy(rotationAxis);
		setMaxDimension();
	}
	
	public boolean getStaticState(){
		return isStatic;
	}
	
	public void setStatic(boolean state){
		isStatic = state;
	}
	
	public void setMaxDimension(){
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
		SimpleVector dimensions = new SimpleVector(maxVerts.x - minVerts.x, maxVerts.y - minVerts.y, maxVerts.z - minVerts.z);
		if(dimensions.x > dimensions.z &&dimensions.x > dimensions.y)
			maxDimension = dimensions.x;
		else if(dimensions.z > dimensions.x && dimensions.z > dimensions.y)
			maxDimension = dimensions.z;
		else
			maxDimension = dimensions.y;
	}
	
	public float getMaxDimension(){
		return maxDimension;
	}
	
	public void setObjectId(int id){
		this.objectId = id;
	}
	
	public int getObjectId(){
		return objectId;
	}
	
	public int getArticle(){
		return article;
	}
	
	@Override
	public void setName(String name){
		/**
		 * TODO: setting for language
		 * replace Translator.ENGLISH and Translator.SPANISH with global language parameters
		 */
		names[Translator.ENGLISH] = name;
		names[Translator.SPANISH] = Translator.translateToLanguage(name,Translator.SPANISH);
	}
	
	public String getName(int language){
		return names[language];
	}
	
	@Override
	public void scale(float scaleTo){
		super.scale(scaleTo/maxDimension);
	}
	
	public void removeObject(Room room){
		room.removeObject(objectId);
	}
	
	public void rotateBy(SimpleVector axes){
		this.rotateX(axes.x);
		this.rotateY(axes.y);
		this.rotateZ(axes.z);
	}
	
	public Object3D toObject3D(){
		return (Object3D)this;
	}

}
