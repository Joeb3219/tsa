package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.world.Position;

public class Widget {

	public Position pos;
	public float red, green, blue, alpha;
	public float height, width;
	public String identifier = "null";

	public Widget(Position p, float width, float height, float red, float green, float blue, float alpha){
		this.pos = p;
		this.width = width;
		this.height = height;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public boolean mouseInBounds(){
		float x = pos.x;
		float y = pos.y;
		
		System.out.println(Mouse.getX() + ", " + Mouse.getY() + " :: " + x + ", " + y);
		
		if(Mouse.getX() >= x && Mouse.getX() <= x + width){
			if(Mouse.getY() >= y && Mouse.getY() <= y + width){
				return true;
			}
		}
		return false;
	}
	
	public void render(){
		glBegin(GL_QUADS);
		glColor4f(red, green, blue, alpha);
		glVertex2f(pos.x, pos.y);
		glVertex2f(pos.x + width, pos.y);
		glVertex2f(pos.x + width, pos.y + height);
		glVertex2f(pos.x, pos.y + height);
		glEnd();
	}
	
}
