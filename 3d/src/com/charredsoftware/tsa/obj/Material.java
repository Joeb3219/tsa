package com.charredsoftware.tsa.obj;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

/**
 * Material class!
 * Represents an OBJ model's material.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since January 24, 2015
 */

public class Material {
	
	public Texture texture;
	public String name;
	public FloatBuffer ambient, specular;
	public Vector3f diffuse;
	public float shininess, illum, transparency;
	public static FloatBuffer buffer;
	
	/**
	 * Creates a new material.
	 * @param name Name of the material
	 */
	public Material(String name){
		this.name = name;
	}
	
	/**
	 * Converts coordinates into a buffer.
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 * @return Returns a buffer from coordinates.
	 */
	public static FloatBuffer coordsToBuffer(float x, float y, float z){
		buffer = BufferUtils.createFloatBuffer(4);
		buffer.put(x).put(y).put(z).put(1f).flip();
		return buffer;
	}
	
}