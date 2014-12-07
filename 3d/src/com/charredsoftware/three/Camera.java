package com.charredsoftware.three;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.entity.Player;

public class Camera {

	public float x = 0, y = -2, z = 0;
	public float rx = 0, ry = 0, rz = 0;
	public float fov, aspectRatio, nearClip, farClip;
	public Frustum frustum = Frustum.getInstance();
	public float yOffset = 0f;
	
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
		
		glEnable(GL_LIGHTING);
		
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		
		glLightModel(GL_LIGHT_MODEL_AMBIENT, (FloatBuffer) (buffer.put((new float[]{ 0f, 0f, 0f, 0f }))).flip());
		
		glLight(GL_LIGHT0, GL_AMBIENT, (FloatBuffer) (buffer.put((new float[]{ 0f, 0f, 0f, 0f }))).flip());
		glLight(GL_LIGHT0, GL_DIFFUSE, (FloatBuffer) (buffer.put((new float[]{ 0f, 0f, 0f, 0f }))).flip());
		glLight(GL_LIGHT0, GL_SPECULAR, (FloatBuffer) (buffer.put((new float[]{ 0f, 0f, 0f, 0f }))).flip());
		glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) (buffer.put((new float[]{ 0.0f, 0.0f, 0.0f, 0.0f }))).flip());
		
		for(int i = GL_LIGHT0 - 1; i < GL_LIGHT7; i ++){
			glLightf(i, GL_CONSTANT_ATTENUATION, 1.25f);
			glLightf(i, GL_LINEAR_ATTENUATION, .5f);
			glLightf(i, GL_QUADRATIC_ATTENUATION, 1.0f);
			glEnable(i);
		}

		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 0.35f);
		glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0.1f);
		glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0.08f);
		
		glEnable(GL_COLOR_MATERIAL);
		glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_FALSE);
		
		
		glClearColor(0 / 255f, 0 / 255f, 0 / 255f, 1f);
		
		frustum.calculateFrustum(fov, Display.getWidth() * 1f / Display.getHeight(), nearClip, farClip);
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
		Vector3f camera = new Vector3f(x, y, z);
		Vector3f looking = Main.getInstance().player.getLookingAt();
		Vector3f up = new Vector3f(0, y + farClip * (float) Math.cos(Math.toRadians(rx - 90)), 0);
		frustum.setCamera(camera, looking, up);
		
		glLoadIdentity();
		glRotatef(ry, 1, 0, 0);
		glRotatef(rx, 0, 1, 0);
		glRotatef(rz, 0, 0, 1);
		glTranslatef(-x, -y, -z);
		glMatrixMode(GL_MODELVIEW);
	}
	
	public void calculatePosition(Player player){
		x = player.x;
		yOffset = (float) ((3.0/3.0) * ((player.y % 2 == 0) ? player.y : player.y));
		if(player.isCrouching) yOffset -= player.height / 2;
		yOffset = yOffset * -1;
		y = -yOffset + player.height / 2;
		z = player.z;
	}
	
}
