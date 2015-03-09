package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import com.charredsoftware.tsa.Mouse;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.world.Position;

/**
 * The Widget class.
 * Base class for all other widgets
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 7, 2014
 */

public class Widget {

	public Position pos;
	public float red = 1f, green = 1f, blue = 1f, alpha = 1f;
	public int value;
	public String identifier = "null";
	public float padding = 16f;

	/**
	 * Creates a new widget
	 * @param p Position to draw the widget at
	 * @param red Red bg-colour
	 * @param green Green bg-colour
	 * @param blue Blue bg-colour
	 * @param alpha Alpha bg-colour
	 */
	public Widget(Position p, float red, float green, float blue, float alpha){
		this.pos = p;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	/**
	 * @return Returns <tt>true</tt> if the mouse is within the bounds of the widget
	 */
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
	
	/**
	 * Renders the widget. Generic render method.
	 */
	public void render(){
		glBegin(GL_QUADS);
		glColor4f(red, green, blue, alpha);
		glVertex2f(pos.x, pos.y);
		glVertex2f(pos.x + getWidth(), pos.y);
		glVertex2f(pos.x + getWidth(), pos.y + getHeight());
		glVertex2f(pos.x, pos.y + getHeight());
		glEnd();
	}
	
	/**
	 * Generic width method.
	 * @return Returns the width of the widget.
	 */
	public float getWidth(){
		return 1f;
	}
	
	/**
	 * Generic height method.
	 * @return Returns the height of the widget.
	 */
	public float getHeight(){
		return 1f;
	}
	
	/**
	 * @return Returns the x position of the widget.
	 */
	public float getXPos(){
		return (Display.getWidth() - getWidth()) / 2 - padding / 2;
	}
	
	/**
	 * @return Returns the y-position of the widget.
	 */
	public float getYPos(){
		return Display.getHeight() - pos.y;
	}
	
}
