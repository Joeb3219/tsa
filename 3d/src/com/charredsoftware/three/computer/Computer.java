package com.charredsoftware.three.computer;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.opengl.Display;

import com.charredsoftware.three.Main;

public class Computer extends Peripheral{

	public int id;
	public String mac;

	public Computer(float x, float y, float z){
		super(x, y, z);
	}
	
	public Computer(){
		super(0, 0, 0);
	}
	
	public void draw(){
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		glBegin(GL_QUADS);
		glColor3f(.4f, .4f, .4f);
		
		glVertex2f(10f, 10f);
		glVertex2f(Display.getWidth() - 10f, 10f);
		glVertex2f(Display.getWidth() - 10f, Display.getHeight() - 10f);
		glVertex2f(10f, Display.getHeight() - 10f);
		
		glEnd();
		
		
		Main.font.drawString(5, 10, "X:" + x);
		Main.font.drawString(5, 25, "Y:" + y);
		Main.font.drawString(5, 40, "Z:" + z);
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
}
