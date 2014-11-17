package com.charredsoftware.three.inventory;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.Display;

public class Hotbar extends Inventory{

	public int selected = 0;
	
	public Hotbar(int size) {
		super(size);
	}

	public void scroll(int dir){
		if(dir > 0){
			if(selected > 0) selected --;
			else selected = size - 1;
		}else{
			if(selected < size -1) selected ++;
			else selected = 0;
		}
	}
	
	public ItemGroup getSelectedItem(){
		return items.get(selected);
	}
	
	public void draw(){
		glLoadIdentity();
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
		
		glBegin(GL_QUADS);
		glColor3f(.4f, .4f, .4f);
		
		float width = Display.getWidth() / 2;
		
		glVertex2f(width - width / 2, 0f);
		glVertex2f(width + width / 2, 0f);
		glVertex2f(width + width / 2, 64f);
		glVertex2f(width - width / 2, 64f);
		
		glEnd();
		
		int objectSize = (int) (width * 2 / size);
		
		int z = (int) ((width - width / 2) - objectSize);
		
		for(ItemGroup i : items){
			z += objectSize;
			i.draw(0, 0);
		}
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
}
