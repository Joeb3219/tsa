package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.world.Position;

/**
 * Menu class.
 * Used to create a menu for the Display.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 1, 2014
 */

public class Menu {

	public float red, green, blue, alpha = 0f;
	public Position pos;
	public float width, height;
	public ArrayList<Widget> widgets = new ArrayList<Widget>();
	
	/**
	 * Creates a menu.
	 * @param p Position to place the menu at.
	 * @param width Width of the menu
	 * @param height Height of the menu
	 * @param red Red value
	 * @param green Green value 
	 * @param blue Blue value
	 * @param alpha Alpha value
	 */
	public Menu(Position p, float width, float height, float red, float green, float blue, float alpha){
		this.pos = p;
		this.width = width;
		this.height = height;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	/**
	 * @return Returns a widget if the mouse is within the widget's bounds.
	 */
	public Widget getWidgetInBounds(){
		for(Widget w : widgets){
			if(w.mouseInBounds()) return w;
		}
		return null;
	}
	
	/**
	 * Renders the menu to the screen.
	 */
	public void render(){
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0f, Display.getWidth(), Display.getHeight(), 0f, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();

		glBegin(GL_QUADS);
		glColor4f(red, green, blue, alpha);
		glVertex2f(pos.x, pos.y);
		glVertex2f(pos.x + width, pos.y);
		glVertex2f(pos.x + width, pos.y + height);
		glVertex2f(pos.x, pos.y + height);
		glEnd();
		
		for(Widget w : widgets) w.render();
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		
	}
	
}
