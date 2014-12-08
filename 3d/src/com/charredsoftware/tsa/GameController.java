package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.GL_LIGHT1;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Font;

public class GameController {

	private static GameController _INSTANCE;
	public String gameName = "TSA Entry";
	public String version = "1.0.2";
	public boolean developerMode = true;
	public boolean buildingMode = false;
	public boolean lighting = true;
	public int lightInUse = GL_LIGHT1;
	
	public void keyboardTick(){
		if(developerMode){
			if(Keyboard.isKeyDown(Keyboard.KEY_L)) lighting = !lighting;
			if(Keyboard.isKeyDown(Keyboard.KEY_B)) buildingMode = !buildingMode;
			if(Keyboard.isKeyDown(Keyboard.KEY_UP)) Main.getInstance().player.bow.arrows ++;
			if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)) Main.getInstance().player.bow.arrows --;
		}
		
		while(Keyboard.next()){}
	}
	
	public void renderDeveloperText(){
		Font font = Main.getInstance().font;
		float xStart = Display.getWidth() - font.getWidth("DEVELOPER MODE: DEVELOPER MODE: ");
		font.drawString(xStart, 10, "DEVELOPER MODE");
		font.drawString(xStart, 30, "FPS: " + Main.getInstance().displayFPS);
		font.drawString(xStart, 50, "Lighting: " + lighting);
		font.drawString(xStart, 70, "Building: " + buildingMode);
		
	}
	
	private GameController(){
		
	}
	
	public static GameController getInstance(){
		if(_INSTANCE == null) _INSTANCE = new GameController();
		return _INSTANCE;
	}
	
}
