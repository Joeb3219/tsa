package com.charredsoftware.tsa.obj;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.tsa.Main;

public class Model {

	public ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
	public ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
	public ArrayList<Face> faces = new ArrayList<Face>();
	public ArrayList<Material> materials = new ArrayList<Material>();
	
	public void render(){
		glPushAttrib(GL_COLOR_BUFFER_BIT);
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_COLOR_MATERIAL);
		glBegin(GL_TRIANGLES);
		for(Face f : faces){
			glColor3f(f.material.diffuse.x, f.material.diffuse.y, f.material.diffuse.z);
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
		glDisable(GL_COLOR_MATERIAL);
		glPopAttrib();
	}
	
}
