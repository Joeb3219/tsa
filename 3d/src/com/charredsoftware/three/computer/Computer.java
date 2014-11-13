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

import java.io.File;

import org.lwjgl.opengl.Display;

import com.charredsoftware.three.Main;

public class Computer extends Peripheral{

	public int id;
	public String mac;

	public Computer(float x, float y, float z, float special){
		super(x, y, z, special);
	}
	
	public Computer(){
		super(0, 0, 0, -1);
	}
	
	public float generateSpecialId(){
		if(special != -1) return special; //One is already assigned, everything is good.
		File cDir = new File(Main.world.dir.getAbsolutePath(), "data/computers");
		String[]entries = cDir.list();
		float highest = -1f;
		for(String s : entries){
			//Look for highest directory -> next id will be the highest + 1.
			if(!new File(cDir, s).isDirectory()) continue;
			float current = Float.parseFloat(s);
			highest = Math.max(current, highest);
		}
		highest ++;
		new File(cDir, highest + "").mkdir();
		return highest;
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
