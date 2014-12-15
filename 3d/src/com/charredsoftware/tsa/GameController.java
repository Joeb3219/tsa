package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.GL_LIGHT1;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Font;

/**
 * GameController class. Used to have a single place to store info about the current game session.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 8, 2014
 */
public class GameController {

	private static GameController _INSTANCE;
	public String gameName = "The Enigma Machine";
	public String version = "1.0.2";
	public boolean developerMode = true, buildingMode = false, lighting = true, displayDialogs = false;
	public int lightInUse = GL_LIGHT1;
	
	/**
	 * Things to check on keyboard updates.
	 */
	public void keyboardTick(){
		if(developerMode){
			if(Keyboard.isKeyDown(Keyboard.KEY_L)) lighting = !lighting;
			if(Keyboard.isKeyDown(Keyboard.KEY_B)) buildingMode = !buildingMode;
			if(Keyboard.isKeyDown(Keyboard.KEY_UP)) Main.getInstance().player.bow.arrows ++;
			if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) Main.getInstance().player.bow.arrows --;
		}
		
		while(Keyboard.next()){}
	}
	
	/**
	 * Adds text to the screen for developers mode
	 */
	public void renderDeveloperText(){
		Font font = Main.getInstance().font;
		float xStart = Display.getWidth() - font.getWidth("DEVELOPER MODE: DEVELOPER MODE: ");
		font.drawString(xStart, 10, "DEVELOPER MODE");
		font.drawString(xStart, 30, "FPS: " + Main.getInstance().displayFPS);
		font.drawString(xStart, 50, "Lighting: " + lighting);
		font.drawString(xStart, 70, "Building: " + buildingMode);
		font.drawString(xStart, 90, "Entities: " + Main.getInstance().player.world.entities.size());
		
	}
	
	private GameController(){
		
	}
	
	/**
	 * @return Returns an instance of GameController, or creates one if it doesn't yet exist.
	 */
	public static GameController getInstance(){
		if(_INSTANCE == null) _INSTANCE = new GameController();
		return _INSTANCE;
	}
	
}
