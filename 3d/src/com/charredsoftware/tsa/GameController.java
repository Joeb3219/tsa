package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.GL_LIGHT1;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glDisable;

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
import com.charredsoftware.tsa.entity.Sputnik;
import com.charredsoftware.tsa.gui.Button;
import com.charredsoftware.tsa.gui.ControlSwitcher;
import com.charredsoftware.tsa.gui.Dialog;
import com.charredsoftware.tsa.gui.DialogAuthor;
import com.charredsoftware.tsa.gui.DialogHUD;
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
	public String version = "2.0.0 (Beta bug testing)";
	public boolean developerMode = true, buildingMode = false, lighting = true, displayDialogs = true, showMainMenu = true, removeMobMode = false, vsync = false;
	public boolean fullscreen = false;
	public boolean applet = false;
	public float soundVolume = 0.5f, musicVolume = 0.35f;
	public int control_forward = Keyboard.KEY_W, control_backward = Keyboard.KEY_S, control_strafe_left = Keyboard.KEY_A, control_strafe_right = Keyboard.KEY_D, control_jump = Keyboard.KEY_SPACE, control_crouch = Keyboard.KEY_LCONTROL, control_buy = Keyboard.KEY_Z;
	public float gamma = 1f, brightness = 0f, contrast = 1f;
	public int difficulty = 2; //1 = easy, 2 = normal, 3 = hard
	public int lightInUse = GL_LIGHT1;
	private float cooldown = 0f;
	public int timeLeft = Main.DESIRED_TPS * (60 * 15); // 15 minutes.
	public int dialogToAdd = 0;
	public int totalChests = 89;
	public int totalMobs = 76;
	public int ticks_since_damage = 30;
	
	
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
	 * Adds a new dialog to the hud if the time is appropriate (when events happen).
	 */
	public void addDialogs(){
		DialogHUD HUDDialog = Main.getInstance().HUDDialog;
		if(dialogToAdd == 0){
			HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "You! Yes, you! You've stumbled into the Enigma Machine!"));
			HUDDialog.dialogs.add(new Dialog(DialogAuthor.PLAYER, "The Enigma what?"));
			HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "The Enigma Machine! It's an evil machine that Dr. Sputnik made! It turned off the SUN! Listen, kid, you must go through the factory and turn off the machine. Our Intel says that Sputnik is on the 4th floor."));
			HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "The factory is full of his minions, and we have just 15 minutes until the Earth freezes. Listen, kid, hold the right mouse button to use your flame bow to neutralize his minions."));
			dialogToAdd ++;
		}else if(dialogToAdd == 1 && Main.getInstance().player.world.id == 0 && Main.getInstance().gameState == GameState.PUZZLE){
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "It appears that each room has a passcode puzzle lock! Our top hackers have broken into the system, and we have the code."));
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PLAYER, "What is it?"));
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "It wouldn't be a challenge if I just told you! Instead, I'll flash the code one digit at a time, and you must key it in afterwards. If you input it incorrectly, I'll flash the code again. Act quickly, the time is ticking!"));
			dialogToAdd ++;
		}else if(dialogToAdd == 2 && Main.getInstance().player.world.id == 1){
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "Woah, you've made it to the first machine room! You can finally see the inner-workings of the Enigma Machine."));
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "Be careful of his workers - if they spot you, they'll call their henchmen to attack you!"));
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "By the way, did you know that we can send you upgrades for you bow if you have enough coin? Just press " + Keyboard.getKeyName(control_buy) + "!"));
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PLAYER, "You're charging me when I'm saving the world?"));
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "Listen, kid, capitalism won't take a hit just because of bad circumstance. I won't bug you until you reach the fourth floor."));
			dialogToAdd ++;
		}else if(dialogToAdd == 3 && Main.getInstance().player.world.id == 3){
			Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PRESIDENT, "Listen, kid, you're in the very bottom of the Enigma Machine. All you need to do now is destroy Dr. Sputnik!"));
			Main.getInstance().player.heal(100);
			dialogToAdd ++; 
		}else if(dialogToAdd == 4 && Main.getInstance().player.world.id == 3){
			for(Entity m : Main.getInstance().player.world.existingEntities){
				if(m instanceof Sputnik && (((Sputnik) m).getPosition().calculateDistance(Main.getInstance().player.getPosition()) <= Sputnik._DISTANCE_TO_CALL)){
					System.out.println((((Sputnik) m).getPosition().calculateDistance(Main.getInstance().player.getPosition())));
					Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.SPUTNIK, "Who... Whaa... Ho... WHO ARE YOU?!"));
					Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PLAYER, "I'm here to foil your evil plans! Direct order of the President!"));
					Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.SPUTNIK, "Haha! YOU? Stop me? That's laughable!"));
					dialogToAdd ++; 
				}
			}
		}
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
		font.drawString(xStart, yStart + 90, "Building: " + buildingMode + ((buildingMode) ? " (" + Main.getInstance().player.selectedBlock.toString() + ")" : ""));
		font.drawString(xStart, yStart + 110, "RemoveMobMode: " + removeMobMode);
		font.drawString(xStart, yStart + 130, "Entities: " + Main.getInstance().player.world.existingEntities.size());
		font.drawString(xStart, yStart + 150, "Rendered Blocks: " + Main.getInstance().player.world.renderedBlocks + " (" + Main.getInstance().player.world.regionsRendered + ")");
		if(removeMobMode) font.drawString(xStart, yStart + 170, "Shooting mobs removes them!");
		glDisable(GL_TEXTURE_2D);
		
	}
	
	/**
	 * Draws the remaining time to the screen
	 */
	public void drawRemainingTime(){
		String remainingTime = getRemainingTimeAsString();
		Main.getInstance().titleFont.drawString(Display.getWidth() - 160, 16, remainingTime);
		Main.getInstance().font.drawString(Display.getWidth() - 160, 12 + Main.getInstance().titleFont.getHeight(remainingTime), "Room " + (Main.getInstance().player.world.id + 1) + "/4");
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
						if(identifier.contains("volume_slider")) soundVolume = Float.parseFloat(value) / 100f; 
						if(identifier.contains("music_slider")) musicVolume = Float.parseFloat(value) / 100f; 
						if(identifier.contains("fov_slider")){
						}
						if(identifier.contains("gamma_slider")) gamma = Float.parseFloat(value) / 100f;
						if(identifier.contains("brightness_slider")) brightness = (Float.parseFloat(value) - 50)/ 100f;
						if(identifier.contains("contrast_slider")) contrast = Float.parseFloat(value) / 100f;
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
		try{
			Display.setDisplayConfiguration(gamma, brightness, contrast);
		}catch(Exception e){}
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
		float mobBonus = 300f * (Main.getInstance().player.mobsKilled / totalMobs);
		float chestBonus = 250f * (Main.getInstance().player.chestsFound / totalChests);
		if(Main.getInstance().player.bow.UPGRADE_FURTHER_SHOTS) score += 25f;
		if(Main.getInstance().player.bow.UPGRADE_MORE_DAMAGE) score += 35f;
		if(Main.getInstance().player.bow.UPGRADE_LARGER_RADIUS) score += 15f;
		if(Main.getInstance().player.health > 0){
			float totalTime = (60 * 15);
			float remainingTime = timeLeft / Main.DESIRED_TPS;
			score += 500 * (remainingTime / totalTime);
		}
		return score + mobBonus + chestBonus;
	}
	
}
