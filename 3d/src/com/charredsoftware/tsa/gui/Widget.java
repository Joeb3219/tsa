package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import com.charredsoftware.tsa.Mouse;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.world.Position;

public class Widget {

	public Position pos;
	public float red = 1f, green = 1f, blue = 1f, alpha = 1f;
	public int value;
	public String identifier = "null";
	public float padding = 16f;

	public Widget(Position p, float red, float green, float blue, float alpha){
		this.pos = p;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public boolean mouseInBounds(){
		float x = getXPos();
		float y = getYPos();
		
		float w = getWidth();
		float h = getHeight();
		
		if(Mouse.getX() >= x && Mouse.getX() <= x + w){
			if(Mouse.getY() <= y && Mouse.getY() >= y - h){
				return true;
			}
		}
		return false;
	}
	
	public void render(){
		glBegin(GL_QUADS);
		glColor4f(red, green, blue, alpha);
		glVertex2f(pos.x, pos.y);
		glVertex2f(pos.x + getWidth(), pos.y);
		glVertex2f(pos.x + getWidth(), pos.y + getHeight());
		glVertex2f(pos.x, pos.y + getHeight());
		glEnd();
	}
	
	public float getWidth(){
		return 1f;
	}
	
	public float getHeight(){
		return 1f;
	}
	
	public float getXPos(){
		return (Display.getWidth() - getWidth()) / 2 - padding / 2;
	}
	
	public float getYPos(){
		return Display.getHeight() - pos.y;
	}
	
}
