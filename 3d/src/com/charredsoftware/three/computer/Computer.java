package com.charredsoftware.three.computer;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;

import org.lwjgl.opengl.Display;

import com.charredsoftware.three.Main;

public class Computer extends Peripheral{

	public int id;
	public String mac;
	public File dir;

	public Computer(float x, float y, float z, float special){
		super(x, y, z, special);
	}
	
	public Computer(){
		super(0, 0, 0, -1);
	}
	
	public float generateSpecialId(){
		if(special != -1){
			dir = new File(Main.world.dir.getAbsolutePath(), "data/computers/" + special);
			dir.mkdirs();
			return special; //One is already assigned, everything is good.
		}
		dir = new File(Main.world.dir.getAbsolutePath(), "data/computers");
		String[]entries = dir.list();
		float highest = -1f;
		for(String s : entries){
			//Look for highest directory -> next id will be the highest + 1.
			if(!new File(dir, s).isDirectory()) continue;
			float current = Float.parseFloat(s);
			highest = Math.max(current, highest);
		}
		highest ++;
		dir = new File(dir, highest + "");
		dir.mkdirs();
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
		
		Main.font.drawString(15, 15, getPosition().toString());
		Main.font.drawString(15, 30, "Computer ID: " + special);
		Main.font.drawString(15, 45, "Programs: (" + dir.list().length + ")");
		float yStringPos = 45;
		for(String s : dir.list()){
			yStringPos += 15;
			Main.font.drawString(20, yStringPos, s);
		}
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
}
