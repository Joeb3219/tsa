package com.charredsoftware.tsa.obj;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glNormal3f;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

public class Model {

	public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	public ArrayList<Face> faces = new ArrayList<Face>();
	
	public void render(){
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_TRIANGLES);
		for(Face f : faces){
			Vector3f n1 = normals.get((int) f.normals.y - 1);
			glNormal3f(n1.x, n1.y, n1.z);
			Vector3f v1 = vertices.get((int) f.vertex.x - 1);
			glVertex3f(v1.x, v1.y, v1.z);
			Vector3f n2 = normals.get((int) f.normals.y - 1);
			glNormal3f(n2.x, n2.y, n2.z);
			Vector3f v2 = vertices.get((int) f.vertex.y- 1);
			glVertex3f(v2.x, v2.y, v2.z);
			Vector3f n3 = normals.get((int) f.normals.z - 1);
			glNormal3f(n3.x, n3.y, n3.z);
			Vector3f v3 = vertices.get((int) f.vertex.z - 1);
			glVertex3f(v3.x, v3.y, v3.z);
		}
		glEnd();
	}
	
}
