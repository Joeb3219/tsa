package com.charredsoftware.tsa.gui;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.GameState;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;

/**
 * Main Menu class.
 * Extends Menu, loads the menu shown on game opening.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */
public class MainMenu extends Menu{
	
	public ArrayList<Button> buttons = new ArrayList<Button>();
	private Texture logo, gameLogo;

	/**
	 * Creates a MainMenu object.
	 */
	public MainMenu() {
		super(new Position(0, 0, 0), Display.getWidth(), Display.getHeight());
		float textHeight = Main.getInstance().font.getHeight("sample text");
		Button b = new Button(this, 160, "Play Game");
		b.identifier = "play";
		b.checkable = false;
		buttons.add(b);
		b = new Button(this, buttons.get(buttons.size() - 1).pos.y + textHeight +  10, "Level Editor");
		b.identifier = "level_editor";
		b.checkable = false;
		buttons.add(b);
		b = new Button(this, buttons.get(buttons.size() - 1).pos.y + textHeight +  10, "Settings");
		b.identifier = "settings";
		b.checkable = false;
		buttons.add(b);
		b = new Button(this, buttons.get(buttons.size() - 1).pos.y + textHeight +  10, "Quit");
		b.identifier = "quit";
		b.checkable = false;
		buttons.add(b);
		try {
			logo = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "charredsoftware.png"));
			gameLogo = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "enigmalogo.png"));
		} catch (IOException e) {new CrashReport(e);}
	}
	
	/**
	 * Updates the menu.
	 * Catches mouse clicks.
	 */
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
					Main.getInstance().previousState = Main.getInstance().gameState;
					Main.getInstance().gameState = GameState.SETTINGS;
				}
				if(b.identifier.equalsIgnoreCase("quit")){
					Main.getInstance().controller.saveSettings();
					AL.destroy();
					Display.destroy();
					System.exit(0);
				}
			}
		}
	}
	
	/**
	 * Renders the menu.
	 */
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
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();

		glColor4f(red, green, blue, alpha);
		glBegin(GL_QUADS);
		glVertex2f(0, 0);
		glVertex2f(width, 0);
		glVertex2f(width, height);
		glVertex2f(0, height);
		glEnd();
		
		for(Button b : buttons) b.render();
		
		glEnable(GL_LIGHTING);
		glEnable(GL_TEXTURE_2D);
		
		logo.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 0f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 512 - 16);
		glTexCoord2f(1f, 1f); glVertex2f(Display.getWidth() - 16, Display.getHeight() - 16);
		glTexCoord2f(0f, 1f); glVertex2f(Display.getWidth() - 16 - 512, Display.getHeight() - 16);
		glEnd();
		
		gameLogo.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2f(Display.getWidth() / 2- 128, 16);
		glTexCoord2f(1f, 0f); glVertex2f(Display.getWidth() / 2 + 128, 16);
		glTexCoord2f(1f, 1f); glVertex2f(Display.getWidth() / 2 + 128, 16 + 128);
		glTexCoord2f(0f, 1f); glVertex2f(Display.getWidth() / 2 - 128, 16 + 128);
		glEnd();
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
	}
	
}
