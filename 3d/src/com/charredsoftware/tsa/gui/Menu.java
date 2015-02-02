package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.world.Position;

/**
 * Menu class. Generic menu.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 12, 2014
 */
public class Menu {

	public float red = 0f, green = 0f, blue = 0f, alpha = 1f;
	public Position pos;
	public float width, height;
	public ArrayList<Widget> widgets = new ArrayList<Widget>();
	
	/**
	 * Creates a menu object.
	 * @param p Position of the menu
	 * @param width Width of the menu
	 * @param height Height of the menu
	 */
	public Menu(Position p, float width, float height){
		this.pos = p;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * @return Returns a Widget if the mouse is hovering over it.
	 */
	public Widget getWidgetInBounds(){
		for(Widget w : widgets){
			if(w.mouseInBounds()) return w;
		}
		return null;
	}
	
	/**
	 * Generic update method.
	 */
	public void update(){
		
	}
	
	/**
	 * Generic render method.
	 */
	public void render(){
		preRender();

		glBegin(GL_QUADS);
		glColor4f(red, green, blue, alpha);
		glVertex2f(pos.x, pos.y);
		glVertex2f(pos.x + width, pos.y);
		glVertex2f(pos.x + width, pos.y + height);
		glVertex2f(pos.x, pos.y + height);
		glEnd();
		
		for(Widget w : widgets) w.render();
		
		postRender();
	}

	/**
	 * All of the stuff that has to happen after rendering.
	 */
	protected void postRender(){
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
	/**
	 * All of the stuff that has to happen before rendering.
	 */
	protected void preRender(){
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0f, Display.getWidth(), Display.getHeight(), 0f, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		glColor4f(1f, 1f, 1f, 1f);
	}
	
}
