package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.GL_LIGHT1;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Font;

import com.charredsoftware.tsa.entity.Entity;
import com.charredsoftware.tsa.entity.Mob;

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
	public boolean applet = false;
	public float soundVolume = 0.5f;
	public int lightInUse = GL_LIGHT1;
	private float cooldown = 0f;
	public int timeLeft = Main.DESIRED_TPS * (60 * 15); // 15 minutes.
	
	/**
	 * Things to check on keyboard updates.
	 */
	public void keyboardTick(){
		if(cooldown > 0) cooldown --;
		if(developerMode){
			if(Keyboard.isKeyDown(Keyboard.KEY_L) && cooldown == 0){
				cooldown = 4f;
				lighting = !lighting;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_B) && cooldown == 0){
				cooldown = 4f;
				buildingMode = !buildingMode;
				if(!buildingMode) Main.getInstance().player.world.save();
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_UP) && cooldown == 0){
				cooldown = 4f;
				Main.getInstance().player.bow.arrows ++;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) && cooldown == 0){
				cooldown = 4f;
				Main.getInstance().player.bow.arrows --;
			}
			if(Keyboard.isKeyDown(Keyboard.KEY_P) && cooldown == 0){
				cooldown = 4f;
				ArrayList<Entity> entities = Main.getInstance().player.world.existingEntities;
				for(int i = 0; i < entities.size() - 1; i ++){
					if(!(entities.get(i) instanceof Mob)){
						entities.remove(i);
					}
				}
			}
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
		font.drawString(xStart, 30, Main.getInstance().player.getPosition().toStringWithIntegers());
		font.drawString(xStart, 50, "FPS: " + Main.getInstance().displayFPS);
		font.drawString(xStart, 70, "Lighting: " + lighting);
		font.drawString(xStart, 90, "Building: " + buildingMode);
		font.drawString(xStart, 110, "Entities: " + Main.getInstance().player.world.existingEntities.size());
		
	}
	
	/**
	 * @return Returns the amount of time left in the game as a string
	 */
	public String getRemainingTimeAsString(){
		String result = "";
		int seconds = timeLeft / Main.DESIRED_TPS;
		int minutes = seconds / 60;
		int actualSeconds = seconds - (minutes * 60);
		int microSeconds = ( timeLeft - (minutes * 60 * Main.DESIRED_TPS) - (actualSeconds * Main.DESIRED_TPS) ) * 2;
		
		if(minutes < 10) result += "0";
		result += minutes + ":";
		if(actualSeconds < 10) result += "0";
		result += actualSeconds + ":";
		if(microSeconds < 10) result += "0";
		result += microSeconds;
		
		return result;
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
