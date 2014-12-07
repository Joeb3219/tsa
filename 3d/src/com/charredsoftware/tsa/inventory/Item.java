package com.charredsoftware.tsa.inventory;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import org.newdawn.slick.opengl.Texture;

public class Item {

	public static Item NULL = new Item();
	
	public Texture texture;
	public String name;
	public int maxStack = 1;
	
	public Item(){
		this.texture = null;
		this.name = "NULL ITEM";
	}
	
	public Item(String name, Texture texture, int maxStack){
		this.name = name;
		this.texture = texture;
		this.maxStack = maxStack;
	}
	
	public void draw(float x, float z){
		if(texture == null) return;
		glPushMatrix();
		glTranslatef(x, 0, z);
		texture.bind();
		glBegin(GL_QUADS);
		
		float leftBound = -0.5f;
		float rightBound = 0.5f;
		
		//Front
		glTexCoord2f(0, 2/4f); glVertex3f(leftBound,leftBound,rightBound);
		glTexCoord2f(0, 1/4f); glVertex3f(leftBound,rightBound,rightBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);

		//Right
		glTexCoord2f(2/4f, 2/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(2/4f, 1/4f); glVertex3f(leftBound,rightBound,leftBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound,leftBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,leftBound);

		//Left
		glTexCoord2f(2/4f, 2/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(3/4f, 2/4f); glVertex3f(leftBound,leftBound,rightBound);
		glTexCoord2f(3/4f, 1/4f); glVertex3f(leftBound,rightBound,rightBound);
		glTexCoord2f(2/4f, 1/4f); glVertex3f(leftBound,rightBound,leftBound);

		//Back
		glTexCoord2f(4/4f, 2/4f); glVertex3f(rightBound,leftBound,leftBound);
		glTexCoord2f(3/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);
		glTexCoord2f(3/4f, 1/4f); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(4/4f, 1/4f); glVertex3f(rightBound,rightBound,leftBound);

		//Bottom
		glTexCoord2f(0/4f, 3/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(1/4f, 3/4f); glVertex3f(rightBound,leftBound,leftBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);
		glTexCoord2f(0/4f, 2/4f); glVertex3f(leftBound,leftBound,rightBound);

		//Top
		glTexCoord2f(0/4f, 0/4f); glVertex3f(leftBound,rightBound,leftBound);
		glTexCoord2f(1/4f, 0/4f); glVertex3f(rightBound,rightBound,leftBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(0/4f, 1/4f); glVertex3f(leftBound,rightBound,rightBound);
	
		glEnd();
		glPopMatrix();
	}
	
}
