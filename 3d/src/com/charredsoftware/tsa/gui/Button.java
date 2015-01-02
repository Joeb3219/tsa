package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.input.Mouse;
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
	public float checkedRed = 64 / 255f, checkedGreen = 64 / 255f, checkedBlue = 64 / 255f, checkedAlpha = 255 / 255f;
	public boolean checked = false, checkable = true;
	public static final int padding = 16;
	
	/**
	 * Creates a new button
	 * @param p Position
	 * @param text Text to put in button
	 * @param red Red value
	 * @param green Green value
	 * @param blue Blue value
	 * @param alpha Alpha value
	 */
	public Button(Position p, String text){
		super(p, 200, 75, 0 / 255f, 0 / 255f, 0 / 255f, 0 / 255f);
		this.text = text;
	}
	
	/**
	 * Creates a new button
	 * @param p Position
	 * @param text Text to put in button
	 * @param width Width of the button
	 * @param height Height of the button
	 */
	public Button(Position p, String text, float width, float height) {
		super(p, width, height, 0 / 255f, 0 / 255f, 0 / 255f, 0 / 255f);
		this.text = text;
	}
	
	/**
	 * Creates a button that centers itself in the menu.
	 * @param m The menu which the button is within.
	 * @param yPosition the y-Position of the button.
	 * @param text Text to put in button
	 */
	public Button(Menu m, float yPosition, String text){
		super(new Position(-1, yPosition, -1), -1, -1, 0 / 255f, 0 / 255f, 0 / 255f, 0 / 255f);
		this.text = text;
	}
	
	/**
	 * @return Returns <tt>true</tt> if the mouse is on top of the button.
	 */
	public boolean mouseInBounds(){
		float x = pos.x;
		float y = Display.getHeight() - pos.y;
		
		float w = this.width;
		float h = this.height;
		if(width == -1 && height == -1){
			w = Main.getInstance().font.getWidth(text + ((checkable) ? ((checked) ? ": ON" : ": OFF") : ""));
			h = Main.getInstance().font.getHeight(text);
			x = (Display.getWidth() - w) / 2 - padding / 2;
		}
		
		if(Mouse.getX() >= x && Mouse.getX() <= x + w){
			if(Mouse.getY() <= y && Mouse.getY() >= y - h){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Renders the button to the screen.
	 */
	public void render(){
		String stateText = (checkable) ? ((checked) ? ": ON" : ": OFF") : "";
		float alpha = this.alpha;
		float red = this.red;
		float green = this.green;
		float blue = this.blue;
		if(checked){
			red = this.checkedRed;
			green = this.checkedGreen;
			blue = this.checkedBlue;
			alpha = this.checkedAlpha;
		}
		if(width == -1 && height == -1){
			float width = Main.getInstance().font.getWidth(text + stateText) + padding;
			float height = Main.getInstance().font.getHeight(text);
			float xPos = (Display.getWidth() - width) / 2 - padding / 2;
			glColor4f(red, green, blue, alpha);
			glBegin(GL_QUADS);
			glVertex2f(xPos, pos.y);
			glVertex2f(xPos + width, pos.y);
			glVertex2f(xPos + width, pos.y + height);
			glVertex2f(xPos, pos.y + height);
			glEnd();
			Main.getInstance().font.drawString(xPos + padding / 2, pos.y, text + stateText);
		}else{
			glColor4f(red, green, blue, alpha);
			glBegin(GL_QUADS);
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

}
