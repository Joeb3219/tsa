package com.charredsoftware.three;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

	public float x = 0, y = -2, z = 0;
	public float rx = 0, ry = 0, rz = 0;
	public float fov, aspectRatio, nearClip, farClip;
	public Frustum frustum = Frustum.getInstance();
	
	public Camera(float fov, float aspectRatio, float nearClip, float farClip){
		this.fov = fov;
		this.aspectRatio = aspectRatio;
		this.nearClip = nearClip;
		this.farClip = farClip;
		
		initializeProjection();
	}
	
	private void initializeProjection(){
		glMatrixMode(GL_PROJECTION);
		gluOrtho2D(0, Display.getWidth(), 0, Display.getHeight());
		glLoadIdentity();
		gluPerspective(fov, aspectRatio, nearClip, farClip);
		glMatrixMode(GL_MODELVIEW);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		frustum.calculateFrustum(fov, Display.getWidth() / Display.getHeight(), nearClip, farClip);
	}
	
	public void resetAspectRatio(float aspectRatio){
		this.aspectRatio = aspectRatio;
		initializeProjection();
	}
	
	public void move(float dir, float speed){
		this.z += speed * Math.sin(Math.toRadians(ry + 90 * dir));
		this.x += speed * Math.cos(Math.toRadians(ry + 90 * dir));
	}
	
	public void useView(){
		Vector3f camera = new Vector3f(-x, -y, -z);
		Vector3f looking = new Vector3f((float) -Math.sin(Math.toRadians(360 - ry)) * farClip,  farClip * (float) -Math.cos(Math.toRadians(rx - 90)), (float) -Math.cos(Math.toRadians(360 - ry)) * farClip);
		Vector3f up = new Vector3f(0, y + farClip * (float) -Math.cos(Math.toRadians(rx - 90)), 0);
		frustum.setCamera(camera, looking, up);
		
		glLoadIdentity();
		glRotatef(rx, 1, 0, 0);
		glRotatef(ry, 0, 1, 0);
		glRotatef(rz, 0, 0, 1);
		glTranslatef(x, y, z);
		glMatrixMode(GL_MODELVIEW);
	}
	
}
