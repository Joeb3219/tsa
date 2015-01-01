package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHT1;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glIsEnabled;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.FloatBuffer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.SoundStore;

import com.charredsoftware.tsa.entity.Bow;
import com.charredsoftware.tsa.entity.Entity;
import com.charredsoftware.tsa.entity.Player;
import com.charredsoftware.tsa.gui.Dialog;
import com.charredsoftware.tsa.gui.DialogAuthor;
import com.charredsoftware.tsa.gui.DialogHUD;
import com.charredsoftware.tsa.gui.HUDTextPopups;
import com.charredsoftware.tsa.gui.MainMenu;
import com.charredsoftware.tsa.gui.Menu;
import com.charredsoftware.tsa.gui.TextPopup;
import com.charredsoftware.tsa.world.Block;
import com.charredsoftware.tsa.world.BlockInstance;
import com.charredsoftware.tsa.world.Chest;
import com.charredsoftware.tsa.world.World;

/**
 * Main class of the game. Used to instantiate, initialize the game.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class Main {

	public Font font;
	public Player player;
	/** DESIRED_TPS - {@value} The amount of ticks to occur per second. */
	public static final int DESIRED_TPS = 30;
	/** mouseMovementThreshold - {@value} The amount of movement the mouse can move without being detected by game. */
	private static final float mouseMovementThreshold = 2f;
	public Camera camera;
	int displayFPS = 0;
	public boolean menu = false;
	public GameState gameState = GameState.MENU;
	private float cooldown = 0f;
	private Menu main_menu, options_menu;
	public GameController controller;
	public HUDTextPopups HUDText = new HUDTextPopups(10, 110);
	public DialogHUD HUDDialog;
	
	private static Main _INSTANCE = new Main();
	
	/**
	 * Instantiates Main class.
	 */
	private Main(){
		controller = GameController.getInstance();
		initializeDisplay();
		camera = new Camera(65, Display.getWidth() * 1.0f / Display.getHeight(), 0.3f, 75f);
		player = new Player(new World(0), camera);
		HUDDialog = new DialogHUD();
		HUDDialog.dialogs.add(new Dialog(DialogAuthor.PERSON, "Well there, welcome! At last you have made it... You are humanity's last hope!@Dr.Sputnik has turned off the sun, and you must fix it!@Use your bow by holding right click and releasing. You can walk around with the WASD keys..."));
	}
	
	/**
	 * @return an instance of Main.
	 */
	public static Main getInstance(){
		if(_INSTANCE == null) _INSTANCE = new Main();
		return _INSTANCE;
	}
	
	
	/**
	 * Main method. Creates game instance, runs game loop.
	 * @param args Command line arguments
	 */
	public static void main(String[] args){
		Main game = getInstance();
		
		try{
			game.loop();
		}catch(Throwable t){
			new CrashReport(t);
		}
		game.cleanDisplay();
	}
	
	/**
	 * Cleans up the display (Display & AL) for shut down of game.
	 */
	void cleanDisplay(){
		Display.destroy();
		AL.destroy();
	}
	
	/**
	 * Creates the display window. 
	 * Sets title, resizable, size.
	 */
	private void initializeDisplay(){
		try{
			if(!controller.applet){ 
				Display.setDisplayMode(new DisplayMode(1200, 1200 * 9 / 16));
				Display.setResizable(true);
				Display.setTitle("CharredSoftware: " + controller.gameName + " v." + controller.version + " [Joe B, 2014]");
			}
			Display.create();
		}catch(Exception e){new CrashReport(e);}
	}
	
	/**
	 * Runs every 1/DESIRED_TPS seconds.
	 * Tick/update method.
	 */
	public void tick(){
		if(cooldown > 0) cooldown --;
		controller.timeLeft -= 1;
		
		if(gameState == GameState.MENU && !controller.showMainMenu) gameState = GameState.GAME;
		else if(gameState == GameState.MENU) main_menu.update();
		
		
		if(gameState == GameState.GAME && (!HUDDialog.hasDialogs())){
			player.update();
			for(int i = 0; i < player.world.existingEntities.size(); i ++){
				Entity e = player.world.existingEntities.get(i);
				if(e.markedForDeletion){
					player.world.existingEntities.remove(i);
					i++;
				}else player.world.existingEntities.get(i).update();
			}
			
			mouseTick();
		}
		
		camera.calculatePosition(player);
		
		if(!HUDDialog.hasDialogs()) keyboardTick();
		
		HUDDialog.update();
		HUDText.update();
		
		while(Keyboard.next()){}
		
		SoundStore.get().poll(0);
	}

	/**
	 * Checks for keyboard events during a tick.
	 */
	private void keyboardTick() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0 && gameState == GameState.GAME){
			menu = !menu;
			cooldown = 5f;
		}
		controller.keyboardTick();
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_R)) player.spawn(1f, 1f);
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_N)){
			player.world = new World();
			player.world.generate();
			player.spawn(1, 1);
		}
	}
	
	/**
	 * Checks for mouse events during a tick.
	 */
	private void mouseTick(){
		float deltaX = Mouse.getDX();
		float deltaY = Mouse.getDY();
		
		if(Math.abs(deltaX) > mouseMovementThreshold){
			if(deltaX > 0) camera.rx += 1.8f * (Math.min(deltaX, 28) / 4);
			if(deltaX < 0) camera.rx -= 1.8f * (Math.min(-deltaX, 28) / 4);
		}
		if(Math.abs(deltaY) > mouseMovementThreshold){
			if(deltaY < 0) camera.ry += 1.8f * (Math.min(-deltaY, 28) / 4);
			if(deltaY > 0) camera.ry -= 1.8f * (Math.min(deltaY, 28) / 4);
		}

		if(camera.rx < 0) camera.rx = 360 + camera.rx;
		if(camera.rx >= 360) camera.rx = 360 - camera.rx;
		if(camera.ry < -90f) camera.ry = -90f;
		if(camera.ry > 90f) camera.ry = 90f;
		
		float dWheel = Mouse.getDWheel();
		
		if(dWheel > 0){
			if(Block.blocks.indexOf(player.selectedBlock) < Block.blocks.size() - 1) player.selectedBlock = Block.blocks.get(Block.blocks.indexOf(player.selectedBlock) + 1);
			else player.selectedBlock = Block.blocks.get(1);
		}
		if(dWheel < 0){
			if(Block.blocks.indexOf(player.selectedBlock) > 1) player.selectedBlock = Block.blocks.get(Block.blocks.indexOf(player.selectedBlock) - 1);
			else player.selectedBlock = Block.blocks.get(Block.blocks.size() - 1);
		}
		
		if(gameState == GameState.GAME && Mouse.isButtonDown(0) && cooldown == 0){
			if(player.world.lookingAt.base == Block.chest) openChest(player.world.lookingAt);
			else if(controller.buildingMode) player.world.removeBlock(player.world.getBlock(player.world.lookingAt.x, player.world.lookingAt.y, player.world.lookingAt.z));
		}
		if(gameState == GameState.GAME && Mouse.isButtonDown(1) && player.world.lookingAt.base.solid && controller.buildingMode && cooldown == 0){
			BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
			player.world.addBlock(new BlockInstance(player.selectedBlock, adjacent.x, adjacent.y, adjacent.z));
		}
		if(controller.developerMode && controller.buildingMode && gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_C) && player.world.lookingAt.base.solid && cooldown == 0){
			BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
			player.world.addBlock(new Chest(adjacent.x, adjacent.y, adjacent.z, "{\"ARROWS\":\"5\",\"COINS\":\"5\"}"));
		}
		if(controller.developerMode && controller.buildingMode && gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_M) && player.world.lookingAt.base.solid && cooldown == 0){
			//TODO: Code to place mobs.
		}
		
		if(Mouse.isButtonDown(1) || Mouse.isButtonDown(0)) cooldown = 3f;
		
		//Reset mouse position
		if(!menu) {
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
			Mouse.setGrabbed(true);
		}
		else Mouse.setGrabbed(false);
	}
	
	/**
	 * Opens the chest at signified location.
	 */
	private void openChest(BlockInstance b){
		if(!(b instanceof Chest)) return; //Not a chest! Uh oh!
		Sound.OPENING_CHEST.playSfxIfNotPlaying();
		Chest c = (Chest) b;
		c.exists = false;
		player.world.removeBlock(c);
		HUDText.popups.add(new TextPopup("You found " + c.arrows + " arrows!"));
		HUDText.popups.add(new TextPopup("You found " + c.coins + " coins!"));
		player.bow.arrows += c.arrows;
		player.score += c.coins * 2;
		player.coins += c.coins;
	}
		
	/**
	 * Renders the player's flashlight.
	 */
	private void playerFlashlight(){
		glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) (camera.buffer.put((new float[]{ (float) (player.x + Math.cos(Math.toRadians(camera.rx))), player.y + ((player.isCrouching) ? player.height / 2 : player.height), (float) (player.z + Math.sin(Math.toRadians(camera.rx))), 1f }))).flip());
	}
	
	/**
	 * Renders the display.
	 * @param camera The camera in use.
	 */
	public void render(Camera camera){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		if(gameState == GameState.MENU){
			renderMenu("main");
			return;
		}
		
		glLoadIdentity();
		camera.useView();

		if(controller.lighting){
			if(!glIsEnabled(GL_LIGHTING)) glEnable(GL_LIGHTING);
			playerFlashlight();
		}else{
			if(glIsEnabled(GL_LIGHTING)) glDisable(GL_LIGHTING);
		}
		
		for(int i = player.world.existingEntities.size() - 1; i >= 0; i --){
			player.world.existingEntities.get(i).render();
		}
		player.world.render();
		controller.lightInUse = GL_LIGHT1; //Arrows use last 7 light blocks.
		
		//player.hotbar.draw();
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glDisable(GL_TEXTURE_2D);
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		if(gameState == GameState.GAME){
			glPushMatrix();
			glLineWidth(4f);
			glColor4f(.9f, .9f, .9f, 1f);
			glBegin(GL_LINE_STRIP);
			glVertex2d(Display.getWidth() / 2, Display.getHeight() / 2 + 10);
			glVertex2d(Display.getWidth() / 2, Display.getHeight() / 2 - 10);
			glEnd();
			glBegin(GL_LINE_STRIP);
			glVertex2d(Display.getWidth() / 2 + 10, Display.getHeight() / 2);
			glVertex2d(Display.getWidth() / 2 - 10, Display.getHeight() / 2);
			glEnd();
			glDisable(GL_TEXTURE_2D);
			glPopMatrix();
	
			glLoadIdentity();
			
			if(HUDDialog.hasDialogs()) HUDDialog.render();
			
			font.drawString(10, 10, "Arrows: " + player.bow.arrows + "/" + Bow.default_maxArrows);
			font.drawString(10, 30, "Health: " + player.health + "/100");
			font.drawString(10, 50, "Time Remaining: " + controller.getRemainingTimeAsString());
			font.drawString(10, 70, "Coins: " + player.coins);
			font.drawString(10, 90, "Score: " + player.score);
			
			HUDText.render();
			
			if(controller.developerMode) controller.renderDeveloperText();
			
		}
		

		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		
		if(controller.buildingMode){
			glPushMatrix();
			glEnable(GL_TEXTURE_2D);
			glViewport(10, 10, 74, 74);
			glLoadIdentity();
			glRotatef(60, 1, 0, 0);
			glRotatef(60, 0, 1, 0);
			glRotatef(60, 0, 0, 1);
			player.selectedBlock.draw(0f,0f,0f);
			glPopMatrix();
		}
		glEnable(GL_LIGHTING);
		glMatrixMode(GL_MODELVIEW);

	}
	
	/**
	 * The game loop. Handles the render/tick methods.
	 */
	private void loop(){
		java.awt.Font awtFont = new java.awt.Font("Monospaced", java.awt.Font.BOLD, 16);
		font = new TrueTypeFont(awtFont, false);
		
		player.spawn(1, 1);
		
		
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double nanoSeconds = 1000000000.0 / DESIRED_TPS;
		double delta = 0;
		int fps = 0, ticks = 0;
		
		while(!Display.isCloseRequested()){
			if(Display.wasResized()){
				glViewport(0, 0, Display.getWidth(), Display.getHeight());
				camera.resetAspectRatio(Display.getWidth() * 1.0f / Display.getHeight());
			}
			long now = System.nanoTime();
			delta+= (now - lastTime) / nanoSeconds;
			lastTime = now;
			while(delta >= 1){
				tick();
				ticks++;
				delta--;
			}
			render(camera);
			fps++;
			if((System.currentTimeMillis() - timer) > 1000){
				displayFPS = fps;
				fps = 0; //reset the FPS counter
				timer += 1000; //add one second
			}
			
			fps++;
			
			Display.update();
		}
		
		//player.world.save();
		
		Display.destroy();
		AL.destroy();
	}
	
	/**
	 * Renders the display menu
	 * @param menu Which menu to render.
	 */
	private void renderMenu(String menu){
		if(menu.equalsIgnoreCase("main")){
			if(main_menu == null) main_menu = new MainMenu();
			main_menu.render();
			return;
		}
	}
	
}
