package com.charredsoftware.three;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.util.glu.GLU.gluOrtho2D;
import static org.lwjgl.util.glu.GLU.gluPerspective;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.entity.Player;

public class Camera {

	public float x = 0, y = -2, z = 0;
	public float ry = 0, rx = 0, rz = 0;
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
		glClearColor(89 / 255f, 203 / 255f, 222 / 255f, 1f);
		
		frustum.calculateFrustum(fov, Display.getWidth() * 1f / Display.getHeight(), nearClip, farClip);
	}
	
	public void resetAspectRatio(float aspectRatio){
		this.aspectRatio = aspectRatio;
		initializeProjection();
	}
	
	public void move(float dir, float speed){
		this.z += speed * Math.sin(Math.toRadians(rx + 90 * dir));
		this.x += speed * Math.cos(Math.toRadians(rx + 90 * dir));
	}
	
	public void useView(){
		Vector3f camera = new Vector3f(x, y, z);
		Vector3f looking = Main.getInstance().player.getLookingAt();
		Vector3f up = new Vector3f(0, y + farClip * (float) Math.cos(Math.toRadians(ry - 90)), 0);
		frustum.setCamera(camera, looking, up);
		
		glLoadIdentity();
		glRotatef(rx, 1, 0, 0);
		glRotatef(ry, 0, 1, 0);
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
