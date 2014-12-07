package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.world.Position;

public class Menu {

	public float red, green, blue, alpha = 0f;
	public Position pos;
	public float width, height;
	public ArrayList<Widget> widgets = new ArrayList<Widget>();
	
	public Menu(Position p, float width, float height, float red, float green, float blue, float alpha){
		this.pos = p;
		this.width = width;
		this.height = height;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public Widget getWidgetInBounds(){
		for(Widget w : widgets){
			if(w.mouseInBounds()) return w;
		}
		return null;
	}
	
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
