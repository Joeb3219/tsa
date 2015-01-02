package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.world.Position;

public class Slider extends Widget{

	public String text;
	public float current, max, min;
	public float padding = 16;
	public float highlightRed = 98 / 255f, highlightGreen = 113 / 255f, highlightBlue = 227 / 255f;
	
	/**
	 * Creates a slider widget.
	 * @param m Menu to display widget on
	 * @param yPosition yPosition from top of screen
	 * @param text Text to display
	 * @param min Minimum value of the slider
	 * @param max Maximum value of the slider
	 * @param defaultValue Default value of the slider
	 */
	public Slider(Menu m, float yPosition, String text, float min, float max, float defaultValue){
		super(new Position(-1, yPosition, -1), -1, -1, 0 / 255f, 0 / 255f, 0 / 255f, 1 / 255f);
		this.text = text;
		this.max = max;
		this.min = min;
		this.current = defaultValue;
		
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
	
	private String getDisplayText(){
		return text + ": " + ((int) current) + "/" + ((int) max);
	}
	
	private float getWidth(){
		return Main.getInstance().font.getWidth(getDisplayText()) + padding;
	}
	
	private float getHeight(){
		return Main.getInstance().font.getHeight(getDisplayText());
	}
	
	public void update(){
		float xPos = (Display.getWidth() - getWidth()) / 2 - padding / 2;
		float relativeX = (Mouse.getX() - xPos);
		current = convertWidthToValue(relativeX);
	}
	
	public void render(){
		glDisable(GL_TEXTURE_2D);
		String displayText = getDisplayText();
		float width = getWidth();
		float height = getHeight();
		float xPos = (Display.getWidth() - width) / 2 - padding / 2;
		float valueWidth = convertValueToWidth(current);
		glColor4f(red, green, blue, alpha);
		glBegin(GL_QUADS);
		glVertex2f(xPos + width - valueWidth, pos.y);
		glVertex2f(xPos + width - valueWidth + width, pos.y);
		glVertex2f(xPos + width - valueWidth + width, pos.y + height);
		glVertex2f(xPos + valueWidth - valueWidth, pos.y + height);
		glEnd();
		
		glColor4f(highlightRed, highlightGreen, highlightBlue, 1f);
		glBegin(GL_QUADS);
		glVertex2f(xPos, pos.y);
		glVertex2f(xPos + valueWidth, pos.y);
		glVertex2f(xPos + valueWidth, pos.y + height);
		glVertex2f(xPos, pos.y + height);
		glEnd();
		glEnable(GL_TEXTURE_2D);
		Main.getInstance().font.drawString(xPos + padding / 2, pos.y, displayText);
	}
	
	private float convertValueToWidth(float value){
		float width = getWidth();
		return (value / (max - min)) * (width);
	}
	
	private float convertWidthToValue(float width){
		float totalWidth = getWidth();
		return (width / totalWidth) * (max - min);
	}

}
