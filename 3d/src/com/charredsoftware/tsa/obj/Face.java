package com.charredsoftware.tsa.obj;

import org.lwjgl.util.vector.Vector3f;

/**
 * Face class.
 * Represents an OBJ model's face.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since January 24, 2015
 */

public class Face {

	public Vector3f vertex = new Vector3f();
	public Vector3f normals = new Vector3f();
	public Material material;
	
	/**
	 * Creates a face.
	 * @param material Material to bind to the face.
	 * @param v Vector representing the vertex.
	 * @param n Vector representing the normal.
	 */
	public Face(Material material, Vector3f v, Vector3f n){
		this.material = material;
		this.vertex = v;
		this.normals = n;
	}
	
}
