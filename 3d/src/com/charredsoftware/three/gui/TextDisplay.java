package com.charredsoftware.three.gui;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.charredsoftware.three.Main;

public class TextDisplay {

	public ArrayList<String> lines = new ArrayList<String>();
	public int xCursor, yCursor = 0;
	public float x, y, height, width;
	protected float cooldown = 0f; //Used to add some delay to events.
	
	public TextDisplay(){
	}
	
	public TextDisplay(float x, float y, float height, float width, ArrayList<String> lines){
		this.x = x;
		this.y = y;
		this.height = height;
		this.width = width;
		this.lines = lines;
	}
	
	public void scroll(int num){
		
	}
	
	public void navigate(int xNum, int yNum){
		
	}
	
	public void update(){
		
	}
	
	public void clearScreen(){
		lines = new ArrayList<String>();
	}
	
	public void draw(){
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		glLoadIdentity();
		
		int yPos = -1;
		for(String s : lines){
			yPos ++;
			int netLine = yPos - yCursor;
			if(netLine < 0) continue;
			
			for(int i = 0; i < s.length(); i ++){
				Main.font.drawString(x + 10 + (Main.font.getWidth(s.substring(0, i)) + 1), y + (netLine * 20) + 10, s.charAt(i) + "");
			}
		}

		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
	}
	
}
