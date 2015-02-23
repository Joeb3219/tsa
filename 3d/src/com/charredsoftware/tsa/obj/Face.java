package com.charredsoftware.tsa.obj;

import org.lwjgl.util.vector.Vector3f;

public class Face {

	public Vector3f vertex = new Vector3f();
	public Vector3f normals = new Vector3f();
	public Material material;
	
	public Face(Material material, Vector3f v, Vector3f n){
		this.material = material;
		this.vertex = v;
		this.normals = n;
	}
	
}
