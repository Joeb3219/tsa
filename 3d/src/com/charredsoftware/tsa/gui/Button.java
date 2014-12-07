package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.world.Position;

/**
 * Button class.
 * Used to create a clickable button on the menu.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 1, 2014
 */

public class Button extends Widget{

	public String text;
	public static final float standardWidth = 200, standardHeight = 75;
	
	/**
	 * Creates a new button
	 * @param p Position
	 * @param text Text to put in button
	 * @param red Red value
	 * @param green Green value
	 * @param blue Blue value
	 * @param alpha Alpha value
	 */
	public Button(Position p, String text, float red, float green, float blue, float alpha){
		super(p, 200, 75, red, green, blue, alpha);
		this.text = text;
	}
	
	/**
	 * Creates a new button
	 * @param p Position
	 * @param text Text to put in button
	 * @param width Width of the button
	 * @param height Height of the button
	 * @param red Red value
	 * @param green Green value
	 * @param blue Blue value
	 * @param alpha Alpha value
	 */
	public Button(Position p, String text, float width, float height, float red, float green, float blue, float alpha) {
		super(p, width, height, red, green, blue, alpha);
		this.text = text;
	}
	
	/**
	 * Renders the button to the screen.
	 */
	public void render(){
		glBegin(GL_QUADS);
		glColor4f(red, green, blue, alpha);
		glVertex2f(pos.x, pos.y);
		glVertex2f(pos.x + width, pos.y);
		glVertex2f(pos.x + width, pos.y + height);
		glVertex2f(pos.x, pos.y + height);
		glEnd();
		
		float stringX = pos.x + (Main.getInstance().font.getWidth(text)) / 2;
		float stringY = Display.getHeight() - pos.y + (Main.getInstance().font.getHeight(text)) / 2;

		Main.getInstance().font.drawString(stringX, stringY, text);
	}

}
