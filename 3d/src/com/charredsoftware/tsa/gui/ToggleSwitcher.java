package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.world.Position;

/**
 * ToggleSwitcher class.
 * Used to create a button that can be clicked to change its state.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since January 27, 2014
 */
public class ToggleSwitcher extends Widget{

	public String text;
	public ArrayList<String> values = new ArrayList<String>();
	public float padding = 16f;
	public float selectRed = 122 / 255f, selectGreen = 180 / 255f, selectBlue = 250 / 255f;
	
	/**
	 * Creates a toggle switcher that centers itself in the middle of the menu.
	 * @param m The menu which the button is within.
	 * @param yPosition the y-Position of the button.
	 * @param text Text to put in button
	 * @param options An ArrayList of options (String)
	 * @param current Integer representing the index of the current value.
	 */
	public ToggleSwitcher(Menu m, float yPosition, String text, ArrayList<String> options, int current){
		super(new Position(-1, yPosition, -1), -1, -1, 0 / 255f, 0 / 255f, 0 / 255f, 0 / 255f);
		this.text = text;
		this.values = options;
		this.value = current;
	}
	
	public void update(){
		float xPos = (Display.getWidth() - getWidth()) / 2 - padding / 2;
		float relativeX = (Mouse.getX() - xPos);
		if(relativeX > getIdentifierWidth()){
			if(value + 1 >= values.size()) value = 0;
			else value ++;
		}
	}
	
	public boolean mouseInBounds(){
		float x = pos.x;
		float y = Display.getHeight() - pos.y;
		
		float w = this.width;
		float h = this.height;
		if(width == -1 && height == -1){
			w = getWidth();
			h = getHeight();
			x = (Display.getWidth() - w) / 2 - padding / 2;
		}
		
		if(Mouse.getX() >= x && Mouse.getX() <= x + w){
			if(Mouse.getY() <= y && Mouse.getY() >= y - h){
				return true;
			}
		}
		return false;
	}
	
	public void render(){
		glDisable(GL_TEXTURE_2D);
		float width = getWidth();
		float height = getHeight();
		float xPos = (Display.getWidth() - width) / 2 - padding / 2;
		float identifierWidth = getIdentifierWidth();
		//Colour the identifier text
		glColor4f(red, green, blue, alpha);
		glBegin(GL_QUADS);
		glVertex2f(xPos, pos.y);
		glVertex2f(xPos + identifierWidth, pos.y);
		glVertex2f(xPos + identifierWidth, pos.y + height);
		glVertex2f(xPos, pos.y + height);
		glEnd();
		
		//Colour the value value
		glColor4f(selectRed, selectGreen, selectBlue, 1f);
		glBegin(GL_QUADS);
		glVertex2f(xPos + identifierWidth, pos.y);
		glVertex2f(xPos + width, pos.y);
		glVertex2f(xPos + width, pos.y + height);
		glVertex2f(xPos + identifierWidth, pos.y + height);
		glEnd();
		glEnable(GL_TEXTURE_2D);
		Main.getInstance().font.drawString(xPos, pos.y, text + ":");
		Main.getInstance().font.drawString(xPos + identifierWidth + padding / 2, pos.y, values.get(value));
	}
	
	private float getWidth(){
		return Main.getInstance().font.getWidth(getDisplayText()) + padding;
	}
	
	private float getIdentifierWidth(){
		return Main.getInstance().font.getWidth(text + ": ");
	}
	
	private float getHeight(){
		return Main.getInstance().font.getHeight(getDisplayText());
	}
	
	private String getDisplayText(){
		return text + ": " + values.get(value);
	}
	
}
