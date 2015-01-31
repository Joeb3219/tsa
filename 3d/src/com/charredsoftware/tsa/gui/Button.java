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
	public float checkedRed = 64 / 255f, checkedGreen = 64 / 255f, checkedBlue = 64 / 255f, checkedAlpha = 255 / 255f;
	public boolean checked = false, checkable = true;
	public static final int padding = 16;
	
	/**
	 * Creates a button that centers itself in the menu.
	 * @param m The menu which the button is within.
	 * @param yPosition the y-Position of the button.
	 * @param text Text to put in button
	 */
	public Button(Menu m, float yPosition, String text){
		super(new Position(-1, yPosition, -1), 0 / 255f, 0 / 255f, 0 / 255f, 0 / 255f);
		this.text = text;
	}
	
	/**
	 * Renders the button to the screen.
	 */
	public void render(){
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
		
		float width = getWidth();
		float height = getHeight();
		float xPos = (Display.getWidth() - width) / 2 - padding / 2;
		glColor4f(red, green, blue, alpha);
		glBegin(GL_QUADS);
		glVertex2f(xPos, pos.y);
		glVertex2f(xPos + width, pos.y);
		glVertex2f(xPos + width, pos.y + height);
		glVertex2f(xPos, pos.y + height);
		glEnd();
		Main.getInstance().font.drawString(xPos + padding / 2, pos.y, getText());
	}

	public float getHeight(){
		return Main.getInstance().font.getHeight(getText());
	}
	
	public float getWidth(){
		return Main.getInstance().font.getWidth(getText()) + padding;
	}
	
	public String getText(){
		return text + ((checkable) ? ((checked) ? ": ON" : ": OFF") : "");
	}
	
}
