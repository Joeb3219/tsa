package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.world.Position;

/**
 * Widget class.
 * Used to put a Widget onto a Menu
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 30, 2014
 */

public class Widget {

	public Position pos;
	public float red, green, blue, alpha;
	public float height, width;
	public String identifier = "null";

	/**
	 * Creates a new Widget
	 * @param p Position
	 * @param text Text to put in button
	 * @param red Red value
	 * @param green Green value
	 * @param blue Blue value
	 * @param alpha Alpha value
	 */
	public Widget(Position p, float width, float height, float red, float green, float blue, float alpha){
		this.pos = p;
		this.width = width;
		this.height = height;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	/**
	 * @return Returns <tt>true</tt> if the mouse is within the widget's bounds.
	 */
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
	
	/**
	 * Renders the widget to the menu.
	 */
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
