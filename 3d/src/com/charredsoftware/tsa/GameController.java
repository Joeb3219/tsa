package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.GL_LIGHT1;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Font;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.charredsoftware.tsa.entity.Entity;
import com.charredsoftware.tsa.entity.Mob;
import com.charredsoftware.tsa.gui.Button;
import com.charredsoftware.tsa.gui.ControlSwitcher;
import com.charredsoftware.tsa.gui.OptionsMenu;
import com.charredsoftware.tsa.gui.Slider;
import com.charredsoftware.tsa.gui.ToggleSwitcher;
import com.charredsoftware.tsa.gui.Widget;
import com.charredsoftware.tsa.util.FileUtilities;

/**
 * GameController class. Used to have a single place to store info about the current game session.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 8, 2014
 */
public class GameController {

	private static GameController _INSTANCE;
	public String gameName = "The Enigma Machine";
	public String version = "1.1.1";
	public boolean developerMode = true, buildingMode = false, lighting = true, displayDialogs = true, showMainMenu = true, removeMobMode = false, vsync = false;
	public boolean fullscreen = false;
	public boolean applet = false;
	public float soundVolume = 0.5f, musicVolume = 0.5f;
	public int control_forward = Keyboard.KEY_W, control_backward = Keyboard.KEY_S, control_strafe_left = Keyboard.KEY_A, control_strafe_right = Keyboard.KEY_D, control_jump = Keyboard.KEY_SPACE, control_crouch = Keyboard.KEY_LCONTROL, control_buy = Keyboard.KEY_Z;
	public int difficulty = 2; //1 = easy, 2 = normal, 3 = hard
	public int lightInUse = GL_LIGHT1;
	private float cooldown = 0f;
	public int timeLeft = Main.DESIRED_TPS * (60 * 15); // 15 minutes.
	public String sessionName;
	
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
		float yStart = Display.getHeight() - 216;
		font.drawString(xStart, yStart + 10, "DEVELOPER MODE");
		font.drawString(xStart, yStart + 30, Main.getInstance().player.getPosition().toStringWithIntegers());
		font.drawString(xStart, yStart + 50, "FPS: " + Main.getInstance().displayFPS);
		font.drawString(xStart, yStart + 70, "Lighting: " + lighting);
		font.drawString(xStart, yStart + 90, "Building: " + buildingMode);
		font.drawString(xStart, yStart + 110, "RemoveMobMode: " + removeMobMode);
		font.drawString(xStart, yStart + 130, "Entities: " + Main.getInstance().player.world.existingEntities.size());
		font.drawString(xStart, yStart + 150, "Rendered Blocks: " + Main.getInstance().player.world.renderedBlocks + " (" + Main.getInstance().player.world.regionsRendered + ")");
		if(removeMobMode) font.drawString(xStart, yStart + 170, "Shooting mobs removes them!");
		
	}
	
	/**
	 * Draws the remaining time to the screen
	 */
	public void drawRemainingTime(){
		String remainingTime = getRemainingTimeAsString();
		Main.getInstance().titleFont.drawString(Display.getWidth() - 160, 16, remainingTime);
		Main.getInstance().font.drawString(Display.getWidth() - 160, 12 + Main.getInstance().titleFont.getHeight(remainingTime), "Level " + (Main.getInstance().player.world.id + 1) + "/4");
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
	 * Loads the user's settings.
	 */
	public void loadSettings(){
		try{
			File dir = new File(FileUtilities.getBaseDirectory() + FileUtilities.savesPath);
			File file = new File(dir.getAbsolutePath() + "/" + FileUtilities.settingsFile);
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			NodeList base = d.getElementsByTagName("setting");
			for(int i = 0; i < base.getLength(); i ++){
				Node n = base.item(i);
				NodeList children = n.getChildNodes();
				String identifier = "";
				for(int l = 0; l < children.getLength() - 1; l ++){
					Node c = children.item(l);
					String value = c.getTextContent();
					if(value.equals("") || value.equals(" ")) continue;
					if(c.getNodeName().equals("id")){
						identifier = value;
					}else if(c.getNodeName().equals("value")){
						if(identifier.contains("_tab")) continue;
						if(identifier.contains("developer_mode")) developerMode = Boolean.parseBoolean(value);
						if(identifier.contains("difficulty")) difficulty = (int) Float.parseFloat(value) + 1;
						if(identifier.contains("fullscreen")){
							fullscreen = Boolean.parseBoolean(value);
							if(fullscreen) Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
						}
						if(identifier.contains("vsync")){
							vsync = Boolean.parseBoolean(value);
							Display.setVSyncEnabled(vsync);
						}
						if(identifier.contains("volume_slider")) soundVolume = Float.parseFloat(value); 
						if(identifier.contains("music_slider")) musicVolume = Float.parseFloat(value); 
						if(identifier.contains("fov_slider")){
						}
						if(identifier.contains("control_forward")) control_forward = Integer.parseInt(value);
						if(identifier.contains("control_backward")) control_backward = Integer.parseInt(value);
						if(identifier.contains("control_strafe_left")) control_strafe_left = Integer.parseInt(value);
						if(identifier.contains("control_strafe_right")) control_strafe_right = Integer.parseInt(value);
						if(identifier.contains("control_jump")) control_jump = Integer.parseInt(value);
						if(identifier.contains("control_crouch")) control_crouch = Integer.parseInt(value);
						if(identifier.contains("control_buy")) control_buy = Integer.parseInt(value);
					}
				}
			}
		}catch(Exception e){new CrashReport(e);}
	}
	
	/**
	 * Saves the user's settings
	 */
	public void saveSettings(){
		try{
		File dir = new File(FileUtilities.getBaseDirectory() + FileUtilities.savesPath);
		File file = new File(dir.getAbsolutePath() + "/" + FileUtilities.settingsFile);
		PrintWriter writer = new PrintWriter(file, "UTF-8");
		writer.println("<Settings>");
		
		if(Main.getInstance().options_menu == null) Main.getInstance().options_menu = new OptionsMenu();
		for(Widget w : Main.getInstance().options_menu.widgets){
			if(w.identifier.contains("_tab")) continue;
			writer.println("<setting>");
			writer.println("<id>" + w.identifier + "</id>");
			if(w instanceof Button){
				writer.println("<value>" + ((Button)w).checked + "</value>");
			}
			if(w instanceof Slider){
				writer.println("<value>" + ((Slider)w).value + "</value>");
			}
			if(w instanceof ControlSwitcher){
				writer.println("<value>" + ((ControlSwitcher)w).value + "</value>");
			}
			if(w instanceof ToggleSwitcher){
				writer.println("<value>" + ((ToggleSwitcher)w).value + "</value>");
			}
			writer.println("</setting>");
		}
		
		writer.println("</Settings>");
		
		writer.close();
		}catch(Exception e){new CrashReport(e);}
	}
	
	/**
	 * @return Returns an instance of GameController, or creates one if it doesn't yet exist.
	 */
	public static GameController getInstance(){
		if(_INSTANCE == null) _INSTANCE = new GameController();
		return _INSTANCE;
	}
	
	/**
	 * @return Returns <tt>true</tt> if the game is being run from a Jar.
	 */
	public boolean isJar(){
		return GameController.class.getResource("GameController.class").getPath().contains("jar:");
	}
	
	/**
	 * @return Returns <tt>true</tt> if the game is in demo mode.
	 */
	public boolean isInDemoMode(){
		if(isJar()) return false;
		return true;
	}

	/**
	 * @return Returns the player's final score.
	 */
	public float calculateFinalScore(){
		float score = Main.getInstance().player.score;
		if(Main.getInstance().player.health > 0) score += (((Main.DESIRED_TPS * (60 * 15)) - timeLeft) / Main.DESIRED_TPS) / (2 * 5);
		return score;
	}
	
}
