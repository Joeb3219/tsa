package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.world.Position;

public class ControlSwitcher extends Widget{

	public String controlName;
	public float padding = 16f;
	public float selectRed = 122 / 255f, selectGreen = 180 / 255f, selectBlue = 250 / 255f;
	
	public ControlSwitcher(Menu m, float yPosition, String controlName, int defaultKey){
		super(new Position(-1, yPosition, -1), 0 / 255f, 0 / 255f, 0 / 255f, 1 / 255f);
		this.controlName = controlName;
		this.value = defaultKey;
	}
	
	public void update(){
		float xPos = (Display.getWidth() - getWidth()) / 2 - padding / 2;
		float relativeX = (Mouse.getX() - xPos);
		if(relativeX > getIdentifierWidth()){
			boolean keyFound = false;
			do{
				Display.update();
				Keyboard.poll();
				if(Keyboard.next()){
					value = Keyboard.getEventKey();
					keyFound = true;
				}
			}while(!keyFound);
		}
	}
	
	public void render(){
		glDisable(GL_TEXTURE_2D);
		float width = getWidth();
		float height = getHeight();
		float xPos = (Display.getWidth() - width) / 2 - padding / 2;
		float identifierWidth = getIdentifierWidth();
		//Colour the controlName text
		glColor4f(red, green, blue, alpha);
		glBegin(GL_QUADS);
		glVertex2f(xPos, pos.y);
		glVertex2f(xPos + identifierWidth, pos.y);
		glVertex2f(xPos + identifierWidth, pos.y + height);
		glVertex2f(xPos, pos.y + height);
		glEnd();
		
		//Colour the control value
		glColor4f(selectRed, selectGreen, selectBlue, 1f);
		glBegin(GL_QUADS);
		glVertex2f(xPos + identifierWidth, pos.y);
		glVertex2f(xPos + width, pos.y);
		glVertex2f(xPos + width, pos.y + height);
		glVertex2f(xPos + identifierWidth, pos.y + height);
		glEnd();
		glEnable(GL_TEXTURE_2D);
		Main.getInstance().font.drawString(xPos, pos.y, controlName + ":");
		Main.getInstance().font.drawString(xPos + identifierWidth + padding / 2, pos.y, Keyboard.getKeyName(value));
	}
	
	public float getWidth(){
		return Main.getInstance().font.getWidth(getDisplayText()) + padding;
	}
	
	public float getIdentifierWidth(){
		return Main.getInstance().font.getWidth(controlName + ": ");
	}
	
	public float getHeight(){
		return Main.getInstance().font.getHeight(getDisplayText());
	}
	
	public String getDisplayText(){
		return controlName + ": " + Keyboard.getKeyName(value);
	}
	
}
