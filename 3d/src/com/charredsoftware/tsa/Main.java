package com.charredsoftware.tsa;

import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CONSTANT_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHT1;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_LINEAR_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADRATIC_ATTENUATION;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLightf;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glViewport;

import java.nio.FloatBuffer;
import java.util.Timer;

import org.lwjgl.BufferUtils;
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
import com.charredsoftware.tsa.gui.Button;
import com.charredsoftware.tsa.gui.Menu;
import com.charredsoftware.tsa.gui.Widget;
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

	public String gameName = "TSA Entry";
	public String version = "1.0.15";
	public Font font;
	public Player player;
	/** lightInUse - {@value} indicates the light variable in use (up to 8 OpenGL lights may be used). */
	public static int lightInUse = GL_LIGHT1;
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
	private boolean developer_mode = true;
	public boolean buildMode = true;
	public HUDTextPopups HUDText = new HUDTextPopups(10, 50);
	
	private static Main _INSTANCE = null;
	
	/**
	 * Instantiates Main class.
	 */
	private Main(){
		initializeDisplay();
		camera = new Camera(65, Display.getWidth() * 1.0f / Display.getHeight(), 0.3f, 75f);
		player = new Player(new World(0), camera);
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
			Display.setDisplayMode(new DisplayMode(1200, 1200 * 9 / 16));
			Display.setResizable(true);
			Display.setTitle("CharredSoftware: " + gameName + " v." + version + " [Joe B, 2014]");
			Display.create();
		}catch(Exception e){new CrashReport(e);}
	}
	
	/**
	 * Runs every 1/DESIRED_TPS seconds.
	 * Tick/update method.
	 */
	public void tick(){
		if(cooldown > 0) cooldown --;
		
		if(gameState == GameState.MENU && developer_mode) gameState = GameState.GAME;
		else if(gameState == GameState.MENU) unboundMouseTick(); 
		
		
		if(gameState == GameState.GAME){
			player.update();
			for(Entity e : player.world.entities) e.update();
			
			mouseTick();
		}
		
		camera.calculatePosition(player);
		
		keyboardTick();
		
		HUDText.update();
		
		while(Keyboard.next()){}
		
		SoundStore.get().poll(0);
	}

	/**
	 * Handles mouse clicks that are done when mouse is not bound to the screen.
	 */
	private void unboundMouseTick(){
		//TODO: Extract this logic to its own class.
		if(gameState == GameState.MENU){
			if(Mouse.isButtonDown(0)){
				Widget w = main_menu.getWidgetInBounds();
				if(w != null){
					gameState = GameState.GAME;
					if(w.identifier.equalsIgnoreCase("new_world")){
						player.world = new World();
						player.world.generate();
						player.spawn(1, 1);
					}else{
						System.out.println("Loading world " + w.identifier);
						player.world = new World(Integer.parseInt(w.identifier));
						player.world.generate();
						player.spawn(1, 1);
					}
				}
			}
		}
	}
	
	/**
	 * Checks for keyboard events during a tick.
	 */
	private void keyboardTick() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0 && gameState == GameState.GAME){
			menu = !menu;
			cooldown = 5f;
		}
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
			else player.world.removeBlock(player.world.getBlock(player.world.lookingAt.x, player.world.lookingAt.y, player.world.lookingAt.z));
		}
		if(gameState == GameState.GAME && Mouse.isButtonDown(1) && player.world.lookingAt.base.solid && cooldown == 0){
			BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
			player.world.addBlock(new BlockInstance(player.selectedBlock, adjacent.x, adjacent.y, adjacent.z));
		}
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_B) && player.world.lookingAt.base.solid && cooldown == 0){
			BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
			player.world.addBlock(new Chest(adjacent.x, adjacent.y, adjacent.z, "{\"ARROWS\":\"5\",\"COINS\":\"5\"}"));
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
		Chest c = (Chest) b;
		c.exists = false;
		player.world.removeBlock(c);
		HUDText.popups.add(new TextPopup("You found " + c.arrows + " arrows!"));
		HUDText.popups.add(new TextPopup("You found " + c.coins + " coins!"));
		player.bow.arrows += c.arrows;
	}
		
	/**
	 * Renders the player's flashlight.
	 */
	private void playerFlashlight(){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
		if(player.isInWater()){
			glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 1f);
			glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0.2f);
			glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0.16f);
		}else{
			glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION, 0.4f);
			glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0.15f);
			glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, 0.2f);		
		}
		
		glLight(GL_LIGHT0, GL_AMBIENT, (FloatBuffer) (buffer.put((new float[]{ .4f, 0.4f, 0.4f, 1f }))).flip());
		glLight(GL_LIGHT0, GL_DIFFUSE, (FloatBuffer) (buffer.put((new float[]{ .4f, 0.4f, 0.4f, 1f }))).flip());
		glLight(GL_LIGHT0, GL_SPECULAR, (FloatBuffer) (buffer.put((new float[]{ 0.9f, 0.4f, 0.4f, 1f }))).flip());
		glLight(GL_LIGHT0, GL_POSITION, (FloatBuffer) (buffer.put((new float[]{ (float) (player.x + Math.cos(Math.toRadians(camera.rx))), player.y + ((player.isCrouching) ? player.height / 2 : player.height), (float) (player.z + Math.sin(Math.toRadians(camera.rx))), 1f }))).flip());
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

		playerFlashlight();
		
		for(int i = player.world.entities.size() - 1; i >= 0; i --){
			player.world.entities.get(i).render();
		}
		player.world.render();
		lightInUse = GL_LIGHT1; //Arrows use last 7 light blocks.
		
		//player.hotbar.draw();
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
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
			
			font.drawString(10, 10, "Arrows: " + player.bow.arrows + "/" + Bow.maxArrows);
			font.drawString(10, 30, "Health: " + player.health + "/100");
			
			HUDText.render();
			
		}
		

		glEnable(GL_LIGHTING);
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);

	}
	
	/**
	 * The game loop. Handles the render/tick methods.
	 */
	private void loop(){
		java.awt.Font awtFont = new java.awt.Font("Monospaced", java.awt.Font.BOLD, 16);
		font = new TrueTypeFont(awtFont, false);
		
		player.world.generate();
		player.spawn(1, 1);
		
		Timer t = new Timer();
		//t.scheduleAtFixedRate(new TimerTask(){public void run(){Main.world.save();}}, 5, 5000);
		
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
		
		t.cancel();
		player.world.save();
		
		Display.destroy();
		AL.destroy();
	}
	
	/**
	 * Renders the display menu
	 * @param menu Which menu to render.
	 */
	private void renderMenu(String menu){
		if(menu.equalsIgnoreCase("main")){
			if(main_menu == null){
				main_menu = new Menu(new Position(0, 0, 0), Display.getWidth(), Display.getHeight(), 0f, 0f, 0f, 1f);
				float standardX = Display.getWidth() - Button.standardWidth - 10f;
				float standardY = Display.getHeight() - Button.standardHeight - 40f;
				Button button = new Button(new Position(standardX, standardY, 0), "New World", 1f, 1f, 1f, 1f);
				button.identifier = "new_world";
				main_menu.widgets.add(button);
				for(String s : FileUtilities.getChildDirectoriesAsString(FileUtilities.savesPath)){
					button = new Button(new Position(standardX, standardY - (Button.standardHeight + 10) * main_menu.widgets.size(), 0), "World " + s, 100f, 100f, 100f, 255f);
					button.identifier = s;
					main_menu.widgets.add(button);
				}
			}
			main_menu.render();
			return;
		}
	}
	
}
