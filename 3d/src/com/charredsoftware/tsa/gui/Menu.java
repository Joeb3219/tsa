package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2f;

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

	public float red, green, blue, alpha = 0f;
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
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
	}
	
}
