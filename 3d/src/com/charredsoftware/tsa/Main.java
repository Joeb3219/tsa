package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.entity.Arrow;
import com.charredsoftware.tsa.entity.Entity;
import com.charredsoftware.tsa.entity.Mob;
import com.charredsoftware.tsa.entity.Player;
import com.charredsoftware.tsa.entity.Spinner;
import com.charredsoftware.tsa.entity.Stalker;
import com.charredsoftware.tsa.entity.Worker;
import com.charredsoftware.tsa.gui.Dialog;
import com.charredsoftware.tsa.gui.DialogAuthor;
import com.charredsoftware.tsa.gui.DialogHUD;
import com.charredsoftware.tsa.gui.GameOverMenu;
import com.charredsoftware.tsa.gui.HUDTextPopups;
import com.charredsoftware.tsa.gui.MainMenu;
import com.charredsoftware.tsa.gui.Menu;
import com.charredsoftware.tsa.gui.OptionsMenu;
import com.charredsoftware.tsa.gui.Puzzle;
import com.charredsoftware.tsa.gui.StoreMenu;
import com.charredsoftware.tsa.gui.TextPopup;
import com.charredsoftware.tsa.physics.Physics;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Block;
import com.charredsoftware.tsa.world.BlockInstance;
import com.charredsoftware.tsa.world.Chest;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.World;

/**
 * Main class of the game. Used to instantiate, initialize the game.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class Main {

	public static Font font, titleFont;
	public Player player;
	/** DESIRED_TPS - {@value} The amount of ticks to occur per second. */
	public static final int DESIRED_TPS = 30;
	/** mouseMovementThreshold - {@value} The amount of movement the mouse can move without being detected by game. */
	private static final float mouseMovementThreshold = 2f;
	public Camera camera;
	int displayFPS = 0;
	public GameState gameState = GameState.MENU, previousState = GameState.MENU;
	private float cooldown = 0f;
	public Menu main_menu, options_menu, transactions_menu, gameOver_menu, puzzleMenu;
	public GameController controller;
	public HUDTextPopups HUDText = new HUDTextPopups(10, 16);
	public DialogHUD HUDDialog;
	private Texture heart, arrowIdentifier, coin = null;
	private Random r = new Random();
	
	private static Main _INSTANCE = new Main();
	
	/**
	 * Instantiates Main class.
	 */
	private Main(){
		controller = GameController.getInstance();
		initializeDisplay();
		camera = new Camera(65, Display.getWidth() * 1.0f / Display.getHeight(), 14f);
		player = new Player(new World(0), camera);
		
		java.awt.Font awtFont = new java.awt.Font("Monospaced", java.awt.Font.BOLD, 16);
		font = new TrueTypeFont(awtFont, false);
		awtFont = new java.awt.Font("Monospaced", java.awt.Font.BOLD, 26);
		titleFont = new TrueTypeFont(awtFont, false);
		
		HUDDialog = new DialogHUD();
		
		controller.loadSettings();
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
		if(controller.timeLeft <= 0 || player.health <= 0){
			Mouse.setGrabbed(false);
			gameState = GameState.GAME_OVER;
		}
		if(cooldown > 0 && gameState != GameState.MENU && gameState != GameState.SETTINGS) cooldown --;
		if(!controller.buildingMode && (gameState == GameState.GAME || gameState == GameState.PUZZLE)) controller.timeLeft -= 1;
		
		if(gameState == GameState.MENU && !controller.showMainMenu) gameState = GameState.GAME;
		else if(gameState == GameState.MENU) main_menu.update();
		if(gameState == GameState.SETTINGS){
			if(options_menu == null) options_menu = new OptionsMenu();
			options_menu.update();
		}
		if(gameState == GameState.TRANSACTIONS){
			if(transactions_menu == null) transactions_menu = new StoreMenu();
			transactions_menu.update();
		}
		if(gameState == GameState.GAME_OVER){
			if(gameOver_menu == null) gameOver_menu = new GameOverMenu();
			gameOver_menu.update();
		}
		if(gameState == GameState.PUZZLE){
			Mouse.setGrabbed(false);
			if(puzzleMenu == null) puzzleMenu = new Puzzle();
			if(!HUDDialog.hasDialogs()) puzzleMenu.update();
		}
		
		controller.addDialogs();
		
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
			if(options_menu == null) options_menu = new OptionsMenu();
			((OptionsMenu)options_menu).cooldown = 10f;
			previousState = gameState;
			gameState = GameState.SETTINGS;
			Mouse.setGrabbed(false);
			cooldown = 10f;
		}
		if(Keyboard.isKeyDown(controller.control_buy) && cooldown == 0 && gameState == GameState.GAME){
			previousState = gameState;
			gameState = GameState.TRANSACTIONS;
			Mouse.setGrabbed(false);
			cooldown = 10f;
		}
		controller.keyboardTick();
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_R)){
			player.world = new World(player.world.id);
			player.spawn();
		}
		if(gameState == GameState.GAME && controller.buildingMode && controller.developerMode && cooldown == 0 && Keyboard.isKeyDown(Keyboard.KEY_I)){
			ArrayList<Position> fillPositions = player.leftWand.getPositionsBetween(player.rightWand);
			for(Position p : fillPositions){
				player.world.removeBlock(p);
				if(player.selectedBlock != Block.charredBlock) player.world.addBlock(new BlockInstance(player.selectedBlock, p.x, p.y, p.z));
			}
		}
		if(gameState == GameState.GAME && controller.buildingMode && controller.developerMode && cooldown == 0 && Keyboard.isKeyDown(Keyboard.KEY_O)){
			player.world.spawn.x = player.x;
			player.world.spawn.y = player.y;
			player.world.spawn.z = player.z;
		}
		if(controller.developerMode && controller.buildingMode && gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_C) && player.world.lookingAt.base.solid && cooldown == 0){
			BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
			float angle = 360 - camera.rx;
			if(angle < 90) angle = 0;
			else if(angle < 180) angle = 90;
			else if(angle < 270) angle = 180;
			else if(angle < 360) angle = 270;
			player.world.addBlock(new Chest(adjacent.x, adjacent.y, adjacent.z, "{\"ARROWS\":\"5\",\"COINS\":\"5\",\"FACING\":\"" + angle + "\"}"));
			cooldown += 10f;
		}
		if(controller.developerMode && controller.buildingMode && gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_M) && player.world.lookingAt.base.solid && cooldown == 0){
			Sys.alert("Placing mobs", "Press 1 to place a Spinner, 2 to place a Stalker, 3 to place a Worker, 4 to place Dr.Sputnik");
			boolean keyFound = false;
			int value = ' ';
			do{
				Display.update();
				Keyboard.poll();
				Keyboard.next();
				Display.update();
				Keyboard.poll();
				if(Keyboard.next()){
					value = Keyboard.getEventKey();
					try{
						Integer.parseInt(Keyboard.getKeyName(value));
						keyFound = true;
					}catch(Exception e){
						keyFound = false;
					}
				}
			}while(!keyFound);
			value = Integer.parseInt(Keyboard.getKeyName(value));
			if(value == 1) player.world.addMob(new Spinner(player.world, player.world.lookingAt.x, player.world.lookingAt.y + 1, player.world.lookingAt.z));
			if(value == 2) player.world.addMob(new Stalker(player.world, player.world.lookingAt.x, player.world.lookingAt.y + 1, player.world.lookingAt.z));
			if(value == 3) player.world.addMob(new Worker(player.world, player.world.lookingAt.x, player.world.lookingAt.y + 1, player.world.lookingAt.z, player.getPosition().clone()));
			if(value == 4) player.world.addMob(new Spinner(player.world, player.world.lookingAt.x, player.world.lookingAt.y + 1, player.world.lookingAt.z));
		}
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_K) && cooldown == 0){
			cooldown = 10f;
			controller.removeMobMode = !controller.removeMobMode;
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
			if(player.world.lookingAt.base == Block.chest && player.world.lookingAt instanceof Chest) openChest(player.world.lookingAt);
			else if(controller.buildingMode){
				if(player.selectedBlock == Block.charredBlock) player.leftWand = player.world.lookingAt.getPosition();
				else player.world.removeBlock(player.world.getBlock(player.world.lookingAt.x, player.world.lookingAt.y, player.world.lookingAt.z));
			}
		}
		if(gameState == GameState.GAME && Mouse.isButtonDown(1) && player.world.lookingAt.base.solid && controller.buildingMode && cooldown == 0){
			if(player.selectedBlock == Block.charredBlock) player.rightWand = player.world.lookingAt.getPosition();
			else{
				BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
				player.world.addBlock(new BlockInstance(player.selectedBlock, adjacent.x, adjacent.y, adjacent.z));
			}
		}
		
		if(Mouse.isButtonDown(1) || Mouse.isButtonDown(0)) cooldown = 3f;
		
		//Reset mouse position
		Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
		Mouse.setGrabbed(true);
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
		if(r.nextInt(2) == 0){
			HUDText.popups.add(new TextPopup("You found a heart of health!"));
			player.heal(2);
		}
		player.bow.arrows += c.arrows;
		player.score += c.coins * 2;
		player.coins += c.coins;
		player.chestsFound ++;
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
		if(gameState == GameState.SETTINGS){
			renderMenu("settings");
			return;
		}
		if(gameState == GameState.TRANSACTIONS){
			renderMenu("transactions");
			return;
		}
		if(gameState == GameState.GAME_OVER){
			renderMenu("game_over");
			return;
		}
		if(gameState == GameState.PUZZLE){
			renderMenu("puzzle");
			glLoadIdentity();
			glDisable(GL_TEXTURE_2D);
			if(HUDDialog.hasDialogs()) HUDDialog.render();
			return;
		}

		camera.shader.renderShader();
		
		glLoadIdentity();
		camera.useView();
		
		if(controller.lighting){
			if(!glIsEnabled(GL_LIGHTING)) glEnable(GL_LIGHTING);
			playerFlashlight();
		}else{
			if(glIsEnabled(GL_LIGHTING)) glDisable(GL_LIGHTING);
		}
		
		ArrayList<Arrow> arrows = new ArrayList<Arrow>();
		ArrayList<Mob> mobs = new ArrayList<Mob>();
		for(int i = player.world.existingEntities.size() - 1; i >= 0; i --){
			if(player.world.existingEntities.get(i) instanceof Arrow){
				arrows.add((Arrow) player.world.existingEntities.get(i));
				continue;
			}
			mobs.add((Mob) player.world.existingEntities.get(i));
		}
		
		if(arrows.size() > 0){
			arrows.get(0).preRender();
			for(Arrow a : arrows){
				glDisable(GL_TEXTURE_2D);
				if(Physics.getDistance(new Position(a.x, a.y, a.z), player.getPosition()) <= camera.farClip - 2) a.render();
				else a.lightArrow(false);
			}
			arrows.get(0).postRender();
		}
		
		for(Mob m : mobs){
			glEnable(GL_TEXTURE_2D);
			m.render();
		}
		
		player.world.render();
		
		controller.lightInUse = GL_LIGHT1; //Arrows use last 7 light blocks.
		
		player.renderBow();
		
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
			
			HUDText.render();
			renderHUD();
			
			if(controller.developerMode) controller.renderDeveloperText();
			else font.drawString(0, -100, "");
			glEnable(GL_TEXTURE_2D);

			if(HUDDialog.hasDialogs()) HUDDialog.render();
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
			player.selectedBlock.draw(0f, 0f, 0f, 0f);
			glPopMatrix();
		}
		
		camera.shader.closeShader();
		
		glEnable(GL_LIGHTING);
		glMatrixMode(GL_MODELVIEW);

	}
	
	/**
	 * Renders that HUD sweg.
	 */
	private void renderHUD(){
		controller.drawRemainingTime();
		if(HUDDialog.hasDialogs()) return;
		
		if(heart == null || arrowIdentifier == null || coin == null){
			try{
				heart = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "heart.png"));
				arrowIdentifier = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "arrow_identifier.png"));
				coin = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "coin.png"));
			}catch(Exception e){new CrashReport(e);}
		}
		
		float fullSize = 32;
		float fullHeight = 32;
		float xPos = 128f;
		float yPos = Display.getHeight() - 128f;
		for(int i = -1; i < player.health - 1; i ++){
			heart.bind();
			glBegin(GL_QUADS);
			glTexCoord2f((i % 2 == 0) ? 0.5f : 0f, 0); glVertex2f(xPos, yPos);
			glTexCoord2f((i % 2 == 0) ? 1f : 0.5f, 0); glVertex2f(xPos + fullSize / 2, yPos);
			glTexCoord2f((i % 2 == 0) ? 1f : 0.5f, 1); glVertex2f(xPos + fullSize / 2, yPos + fullHeight);
			glTexCoord2f((i % 2 == 0) ? 0.5f : 0f, 1); glVertex2f(xPos, yPos + fullHeight);
			glEnd();
			if(i % 2 == 0) xPos += fullSize / 2 + 4;
			else xPos += fullSize / 2;
		}
		
		xPos = 128f;
		yPos = Display.getHeight() - 92;
		for(int i = -1; i < player.bow.arrows - 1; i ++){
			arrowIdentifier.bind();
			glBegin(GL_QUADS);
			glTexCoord2f((i % 2 == 0) ? 0.5f : 0f, 0); glVertex2f(xPos, yPos);
			glTexCoord2f((i % 2 == 0) ? 1f : 0.5f, 0); glVertex2f(xPos + fullSize / 2, yPos);
			glTexCoord2f((i % 2 == 0) ? 1f : 0.5f, 1); glVertex2f(xPos + fullSize / 2, yPos + fullHeight);
			glTexCoord2f((i % 2 == 0) ? 0.5f : 0f, 1); glVertex2f(xPos, yPos + fullHeight);
			glEnd();
			if(i % 2 == 0) xPos += fullSize / 2 + 4;
			else xPos += fullSize / 2;
		}
		
		xPos = 128;
		yPos = Display.getHeight() - 56f;
		coin.bind();
		glBegin(GL_QUADS);
		glTexCoord2f(0, 0); glVertex2f(xPos, yPos);
		glTexCoord2f(1, 0); glVertex2f(xPos + fullSize, yPos);
		glTexCoord2f(1, 1); glVertex2f(xPos + fullSize, yPos + fullHeight);
		glTexCoord2f(0, 1); glVertex2f(xPos, yPos + fullHeight);
		glEnd();
		
		font.drawString(xPos + fullSize + 4, yPos + fullHeight / 2, "x" + player.coins);
		
		
		xPos = 208;
		fullSize = 192f;
		fullHeight = 32;
		
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		glColor4f(207 / 255f, 207 / 255f, 207 / 255f, 255 / 255f);
		glVertex2f(xPos, yPos);
		glVertex2f(xPos + fullSize, yPos);
		glVertex2f(xPos + fullSize, yPos + fullHeight);
		glVertex2f(xPos, yPos + fullHeight);
		glEnd();
		
		float colourWidth = (player.bow.drawBackTime / player.bow.maxDrawBackTime) * fullSize;
		glBegin(GL_QUADS);
		glColor4f(148 / 255f, 148 / 255f, 148 / 255f, 255 / 255f);
		glVertex2f(xPos, yPos);
		glVertex2f(xPos + colourWidth, yPos);
		glVertex2f(xPos + colourWidth, yPos + fullHeight);
		glVertex2f(xPos, yPos + fullHeight);
		glEnd();
		glEnable(GL_TEXTURE_2D);
		
		String text = "Bow Strength";
		font.drawString(xPos + (fullSize - font.getWidth(text)) / 2, yPos + (fullHeight - font.getHeight(text)) / 2, text, Color.black);
		
		
		
	}
	
	/**
	 * The game loop. Handles the render/tick methods.
	 */
	private void loop(){
		player.spawn();
		
		
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double nanoSeconds = 1000000000.0 / DESIRED_TPS;
		double delta = 0;
		int fps = 0, ticks = 0;
		
		while(!Display.isCloseRequested()){
			if(Display.wasResized()){
				glViewport(0, 0, Display.getWidth(), Display.getHeight());
				camera.resetAspectRatio(Display.getWidth() * 1.0f / Display.getHeight());
				HUDDialog.resetDialogLines();
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
		
		controller.saveSettings();
		
		camera.shader.cleanUp();
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
		if(menu.equalsIgnoreCase("settings")){
			if(options_menu == null) options_menu = new OptionsMenu();
			options_menu.render();
			return;
		}
		if(menu.equalsIgnoreCase("transactions")){
			if(transactions_menu == null) transactions_menu = new StoreMenu();
			transactions_menu.render();
			return;
		}
		if(menu.equalsIgnoreCase("game_over")){
			if(gameOver_menu == null) gameOver_menu = new GameOverMenu();
			gameOver_menu.render();
			return;
		}
		if(menu.equalsIgnoreCase("puzzle")){
			if(puzzleMenu == null) puzzleMenu = new Puzzle();
			puzzleMenu.render();
			return;
		}
	}
	
}
