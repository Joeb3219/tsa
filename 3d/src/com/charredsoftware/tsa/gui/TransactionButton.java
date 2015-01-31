package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.world.Position;

public class TransactionButton extends Widget{

	public String item, description;
	public int cost;
	public Texture icon;
	public static final int iconSize = 64;
	public boolean used = false;
	public static final int _FAR_SIDE_PADDING = 120;
	public static final int _MAX_PER_ROW = 3;
	
	public TransactionButton(float xPosition, float yPosition, String icon, String itemName, String description, int cost){
		super(new Position(xPosition, yPosition, -1), 0 / 255f, 0 / 255f, 0 / 255f, 0 / 255f);
		this.item = itemName;
		this.description = description;
		this.cost = cost;
		try{
			this.icon = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(icon));
		}catch(Exception e){new CrashReport(e);}
	}

	public void render(){
		float textHeight = Main.getInstance().font.getHeight(item);
		float individualPadding = 8f;
		float xPos = getXPos();
		
		float alpha = (used) ? 0.25f : 1f;
		
		glBegin(GL_QUADS);
		glColor4f(red, green, blue, alpha);
		glVertex2f(xPos, pos.y);
		glVertex2f(xPos + getWidth(), pos.y);
		glVertex2f(xPos + getWidth(), pos.y + getHeight());
		glVertex2f(xPos, pos.y + getHeight());
		glEnd();
		
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		
		glColor4f(1f, 1f, 1f, alpha);
		glBegin(GL_QUADS);
		glVertex2f(xPos + (getWidth() - iconSize) / 2, pos.y + textHeight + individualPadding);
		glVertex2f(xPos + (getWidth() - iconSize) / 2 + iconSize, pos.y + textHeight + individualPadding);
		glVertex2f(xPos + (getWidth() - iconSize) / 2 + iconSize, pos.y + iconSize + textHeight + individualPadding);
		glVertex2f(xPos + (getWidth() - iconSize) / 2, pos.y + iconSize + textHeight + individualPadding);
		glEnd();

		
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		
		icon.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2f(xPos + (getWidth() - iconSize) / 2, pos.y + textHeight + individualPadding);
		glTexCoord2f(1f, 0f); glVertex2f(xPos + (getWidth() - iconSize) / 2 + iconSize, pos.y + textHeight + individualPadding);
		glTexCoord2f(1f, 1f); glVertex2f(xPos + (getWidth() - iconSize) / 2 + iconSize, pos.y + iconSize + textHeight + individualPadding);
		glTexCoord2f(0f, 1f); glVertex2f(xPos + (getWidth() - iconSize) / 2, pos.y + iconSize + textHeight + individualPadding);
		glEnd();
		
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		
		Color c = new Color(1f, 1f, 1f, alpha);
		Main.getInstance().font.drawString(xPos, pos.y, item, c);
		String coinsText = (!used) ? (cost + " coins") : ("Purchased!");
		Main.getInstance().font.drawString(xPos + (getWidth() - Main.getInstance().font.getWidth(coinsText)) / 2, pos.y + iconSize + textHeight + individualPadding + individualPadding, coinsText, c);
		
	}
	
	public float getHeight(){
		return iconSize + Main.getInstance().font.getHeight(item) + Main.getInstance().font.getHeight(cost + " coins");
	}
	
	private float getPadding(){
		return Display.getWidth() / 3f / 2;
	}
	
	private float getDrawWidth(){
		return Display.getWidth() - (getPadding() * 2);
	}

	public float getXPos(){
		float drawWidth = getDrawWidth();
		float chunkSize = drawWidth / _MAX_PER_ROW;
		return chunkSize * (pos.x - 1) + getPadding() + (0.5f * getPadding());
	}
	
	public float getWidth(){
		float w = Main.getInstance().font.getWidth(item);
		return (w > iconSize + 8) ? w : iconSize + 8;
	}
	
}
