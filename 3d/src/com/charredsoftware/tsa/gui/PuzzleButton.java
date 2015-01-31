package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.world.Position;

/**
 * PuzzleButton Class.
 * Creates a new Puzzle Button.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since January 17, 2015
 */
public class PuzzleButton extends Widget{

	public int value;
	public float checkedRed = 64 / 255f, checkedGreen = 64 / 255f, checkedBlue = 64 / 255f, checkedAlpha = 255 / 255f;
	public boolean checked = false, checkable = true;
	public static final int padding = 16;
	public Puzzle menu;
	public boolean highlight = false;
	
	/**
	 * Creates a new Puzzle Button.
	 * @param m Menu which the button is on.
	 * @param value Value of the button (0-9)
	 */
	public PuzzleButton(Puzzle m, int value){
		super(new Position(0, 0, -1), 0 / 255f, 0 / 255f, 0 / 255f, 0 / 255f);
		this.value = value;
		float row = 0;
		float col = 0;
		if(value >= 1 && value <= 3) row = 1;
		if(value >= 4 && value <= 6) row = 2;
		if(value >= 7 && value <= 9) row = 3;
		if(value == 0) row = 4;
		if(value == 1 || value == 4 || value == 7) col = 1;
		if(value == 2 || value == 5 || value == 8 || value == 0) col = 2;
		if(value == 3 || value == 6 || value == 9) col = 3;
		
		
		pos = new Position(row, col, -1);
		this.menu = m;
	}
	
	public void update(){
		
	}
	
	/**
	 * @return Returns <tt>true</tt> if the mouse is on top of the button.
	 */
	public boolean mouseInBounds(){
		float x = getXPos();
		float y = Display.getHeight() - getYPos();
		
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
	 * @return Returns the width of a button.
	 */
	public float getWidth(){
		return (menu.getUsableWidth() - 2 * padding) / 3;
	}
	
	/**
	 * @return Returns the height of a button.
	 */
	public float getHeight(){
		return 76f;
	}
	
	/**
	 * @return Returns the x Position of the button.
	 */
	public float getXPos(){
		float startX = menu.getXStart();
		float endX = startX + menu.getUsableWidth();
		if(pos.y == 1) return startX;
		if(pos.y == 2) return startX + getWidth() + padding;
		return endX - getWidth();
	}
	
	/**
	 * @return Returns the y position of the button.
	 */
	public float getYPos(){
		float startY = menu.getYStart() + menu.getButtonsYOffset();
		if(pos.x == 1) return startY;
		if(pos.x == 2) return startY + getHeight() + padding;
		if(pos.x == 3) return startY + getHeight() + padding + getHeight() + padding;
		return startY + getHeight() + padding + getHeight() + padding + getHeight() + padding;
	}
	
	/**
	 * Renders the button to the screen.
	 */
	public void render(){
		float alpha = 1f;
		float red = 0f;
		float green = 0f;
		float blue = 0f;
		if(highlight){
			red = this.checkedRed;
			green = this.checkedGreen;
			blue = this.checkedBlue;
			alpha = this.checkedAlpha;
		}
		
		float width = getWidth();
		float height = getHeight();
		float xPos = getXPos(); 
		float yPos = getYPos();
		
		glDisable(GL_TEXTURE_2D);
		
		glColor4f(red, green, blue, alpha);
		glBegin(GL_QUADS);
		glVertex2f(xPos, yPos);
		glVertex2f(xPos + width, yPos);
		glVertex2f(xPos + width, yPos + height);
		glVertex2f(xPos, yPos + height);
		glEnd();

		glEnable(GL_TEXTURE_2D);
		
		Main.getInstance().font.drawString(xPos + getWidth() / 2, yPos + (getHeight() - Main.getInstance().font.getHeight(value + "")) / 2, value + "", Color.white);
	}
	
}
