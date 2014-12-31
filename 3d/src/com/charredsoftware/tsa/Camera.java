package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_MATERIAL;
import static org.lwjgl.opengl.GL11.GL_CONSTANT_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHT7;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_LIGHT_MODEL_TWO_SIDE;
import static org.lwjgl.opengl.GL11.GL_LINEAR_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADRATIC_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLightModel;
import static org.lwjgl.opengl.GL11.glLightModeli;
import static org.lwjgl.opengl.GL11.glLightf;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.tsa.entity.Player;
import com.charredsoftware.tsa.world.Region;

/**
 * Camera class. Controls the Frustum.
 * Handles all of the OpenGL initialization commands.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class Camera {

	public float x = 0, y = -2, z = 0;
	public float rx = 0, ry = 0, rz = 0;
	public float fov, aspectRatio, nearClip, farClip;
	public Frustum frustum = Frustum.getInstance();
	public float yOffset = 0f;
	public FloatBuffer buffer;
	
	/**
	 * 
	 * @param fov The Field of View
	 * @param aspectRatio Aspect rtaio in terms of width:height.
	 * @param nearClip Distance to clip at, near.
	 * @param farClip Far distance to clip at.
	 */
	public Camera(float fov, float aspectRatio, float nearClip, float farClip){
		this.fov = fov;
		this.aspectRatio = aspectRatio;
		this.nearClip = nearClip;
		this.farClip = farClip;
		
		initializeProjection();
	}
	
	/**
	 * Initializes the projection matrix.
	 * Handles enabling of lighting, textures, etc.
	 */
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
		
		buffer = BufferUtils.createFloatBuffer(4);
		
		glLightModel(GL_LIGHT_MODEL_AMBIENT, (FloatBuffer) (buffer.put((new float[]{ 0f, 0f, 0f, 0f }))).flip());
		
		for(int i = GL_LIGHT0 - 1; i < GL_LIGHT7; i ++){
			glLightf(i, GL_CONSTANT_ATTENUATION, 1.25f);
			glLightf(i, GL_LINEAR_ATTENUATION, .5f);
			glLightf(i, GL_QUADRATIC_ATTENUATION, 1.0f);
			glEnable(i);
		}
		
		for(int i = GL_LIGHT0 + 1; i <= GL_LIGHT7; i ++){
			glLight(i, GL_AMBIENT, (FloatBuffer) (buffer.put((new float[]{ 255f / 255f, 36f / 255f, 0f / 255f, 1.0f }))).flip());
			glLight(i, GL_DIFFUSE, (FloatBuffer) (buffer.put((new float[]{ 255f / 255f, 36f / 255f, 0f / 255f, 1.0f }))).flip());
			glLight(i, GL_SPECULAR, (FloatBuffer) (buffer.put((new float[]{ 0.4f, 0.4f, 0.4f, 1.0f }))).flip());
			glLight(i, GL_POSITION, (FloatBuffer) (buffer.put((new float[]{ 0f, Region._HEIGHT + 16f, 0f, 1f}))).flip());
		}

		glLight(GL_LIGHT0, GL_AMBIENT, (FloatBuffer) (buffer.put((new float[]{ .4f, 0.4f, 0.4f, 1f }))).flip());
		glLight(GL_LIGHT0, GL_DIFFUSE, (FloatBuffer) (buffer.put((new float[]{ .4f, 0.4f, 0.4f, 1f }))).flip());
		glLight(GL_LIGHT0, GL_SPECULAR, (FloatBuffer) (buffer.put((new float[]{ 0.9f, 0.4f, 0.4f, 1f }))).flip());
		glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) (buffer.put((new float[]{ 0.0f, 0.0f, 0.0f, 0.0f }))).flip());
		glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 0.4f);
		glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0.15f);
		glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0.2f);
		
		glEnable(GL_COLOR_MATERIAL);
		glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_FALSE);
		
		glClearColor(0 / 255f, 0 / 255f, 0 / 255f, 1f);
		
		frustum.calculateFrustum(fov, Display.getWidth() * 1f / Display.getHeight(), nearClip, farClip);
	}
	
	/**
	 * Resets the aspect ratio and re-initializes the project.
	 * @param aspectRatio New aspect ratio in terms of width:height.
	 */
	public void resetAspectRatio(float aspectRatio){
		this.aspectRatio = aspectRatio;
		initializeProjection();
	}
	
	/**
	 * Moves the camera.
	 * @param dir Direction to move in (-1 - 1)
	 * @param speed Velocity to move at.
	 * @deprecated All movement is now in the player class. Camera followers player.
	 */
	public void move(float dir, float speed){
		this.z += speed * Math.sin(Math.toRadians(ry + 90 * dir));
		this.x += speed * Math.cos(Math.toRadians(ry + 90 * dir));
	}
	
	/**
	 * Sets the camera to be at the position specified (player's pos).
	 * Resets the frustum.
	 */
	public void useView(){
		Vector3f camera = new Vector3f(x, y, z);
		Vector3f looking = Main.getInstance().player.getLookingAt();
		Vector3f up = new Vector3f(0, y + farClip * (float) Math.cos(Math.toRadians(rx - 90)), 0);
		frustum.setCamera(camera, looking, up);
		
		glLoadIdentity();

		for(int i = GL_LIGHT0 + 1; i < GL_LIGHT7; i ++){
			glLight(i, GL_POSITION, (FloatBuffer) (buffer.put((new float[]{ 0f, Region._HEIGHT + 16f, 0f, 1f }))).flip());
		}
		
		if(Main.getInstance().player.bow.UPGRADE_LARGER_RADIUS && !Main.getInstance().player.bow.UPGRADE_LARGER_RADIUS_APPLIED){
			for(int i = GL_LIGHT0 + 1; i < GL_LIGHT7; i ++){
				glLightf(i, GL_CONSTANT_ATTENUATION, .75f / 2f);
				glLightf(i, GL_LINEAR_ATTENUATION, .25f / 2f);
				glLightf(i, GL_QUADRATIC_ATTENUATION, .5f / 2f);
			}
			Main.getInstance().player.bow.UPGRADE_LARGER_RADIUS_APPLIED = true;
		}
		
		glRotatef(ry, 1, 0, 0);
		glRotatef(rx, 0, 1, 0);
		glRotatef(rz, 0, 0, 1);
		glTranslatef(-x, -y, -z);
		glMatrixMode(GL_MODELVIEW);
	}
	
	/**
	 * Calculates the camera's position based off of the player's position.
	 * @param player The player the camera is following.
	 */
	public void calculatePosition(Player player){
		x = player.x;
		yOffset = (float) ((3.0/3.0) * ((player.y % 2 == 0) ? player.y : player.y));
		if(player.isCrouching) yOffset -= player.height / 2;
		yOffset = yOffset * -1;
		y = -yOffset + player.height / 2;
		z = player.z;
	}
	
}
