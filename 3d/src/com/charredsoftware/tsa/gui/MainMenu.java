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
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.GameState;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;

public class MainMenu extends Menu{
	
	public ArrayList<Button> buttons = new ArrayList<Button>();
	private Texture logo;

	public MainMenu() {
		super(new Position(0, 0, 0), Display.getWidth(), Display.getHeight());
		float textHeight = Main.getInstance().font.getHeight("sample text");
		Button b = new Button(this, 60, "Play Game", 1f, 1f, 1f, 1f);
		b.identifier = "play";
		buttons.add(b);
		b = new Button(this, 60 + textHeight + 10, "Level Editor", 1f, 1f, 1f, 1f);
		b.identifier = "level_editor";
		buttons.add(b);
		b = new Button(this, 60 + textHeight + 10 + textHeight + 10, "Settings", 1f, 1f, 1f, 1f);
		b.identifier = "settings";
		buttons.add(b);
		try {
			logo = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "charredsoftware.png"));
		} catch (IOException e) {new CrashReport(e);}
	}
	
	public void update(){
		if(Mouse.isButtonDown(0)){
			for(Button b : buttons){
				if(!b.mouseInBounds()) continue;
				if(b.identifier.equalsIgnoreCase("play")){
					Main.getInstance().gameState = GameState.GAME;
				}
				if(b.identifier.equalsIgnoreCase("level_editor")){
									
								}
				if(b.identifier.equalsIgnoreCase("settings")){
					
				}
			}
		}
	}
	
	public void render(){
		this.height = Display.getHeight();
		this.width = Display.getWidth();
		
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
		glVertex2f(0, 0);
		glVertex2f(width, 0);
		glVertex2f(width, height);
		glVertex2f(0, height);
		glEnd();
		
		for(Button b : buttons) b.render();
		
		logo.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 0f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 1f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 16);
		glTexCoord2f(0f, 1f); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 16);
		glEnd();
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
}
