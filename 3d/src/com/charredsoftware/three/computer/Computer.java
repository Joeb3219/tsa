package com.charredsoftware.three.computer;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;

import com.charredsoftware.three.world.Position;

public class Computer {

	public int id;
	public String mac;
	public Position pos;
	
	public Computer(){
		
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
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
}
