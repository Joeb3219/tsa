package com.charredsoftware.tsa.obj;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

public class Material {
	
	public Texture texture;
	public String name;
	public FloatBuffer ambient, specular;
	public Vector3f diffuse;
	public float shininess, illum, transparency;
	public static FloatBuffer buffer;
	
	public Material(String name){
		this.name = name;
	}
	
	public static FloatBuffer coordsToBuffer(float x, float y, float z){
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(x).put(y).put(z).put(1f).flip();
		return buffer;
	}
	
}