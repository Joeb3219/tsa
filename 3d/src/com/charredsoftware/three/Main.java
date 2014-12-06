package com.charredsoftware.three;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.Timer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.openal.SoundStore;

import com.charredsoftware.three.entity.Entity;
import com.charredsoftware.three.entity.Player;
import com.charredsoftware.three.gui.Button;
import com.charredsoftware.three.gui.Menu;
import com.charredsoftware.three.gui.Widget;
import com.charredsoftware.three.util.FileUtilities;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;
import com.charredsoftware.three.world.World;

public class Main {

	public String gameName = "TSA Entry";
	public String version = "1.0.15";
	public Font font;
	public Player player;
	public static final int DESIRED_TPS = 30;
	private static final float mouseMovementThreshold = 2f;
	public Camera camera;
	int displayFPS = 0;
	public boolean menu = false;
	public GameState gameState = GameState.MENU;
	private float cooldown = 0f;
	private Menu main_menu, options_menu;
	private boolean developer_mode = true;
	
	private static Main _INSTANCE = null;
	
	
	private Main(){
		initializeDisplay();
		camera = new Camera(65, Display.getWidth() * 1.0f / Display.getHeight(), 0.3f, 75f);
		player = new Player(new World(0), camera);
	}
	
	public static Main getInstance(){
		if(_INSTANCE == null) _INSTANCE = new Main();
		return _INSTANCE;
	}
	
	public static void main(String[] args){
		Main game = getInstance();
		
		try{
			game.loop();
		}catch(Throwable t){
			new CrashReport(t);
		}
		game.cleanDisplay();
	}
	
	void cleanDisplay(){
		Display.destroy();
		AL.destroy();
	}
	
	private void initializeDisplay(){
		try{
			Display.setDisplayMode(new DisplayMode(1200, 1200 * 9 / 16));
			Display.setResizable(true);
			Display.setTitle("CharredSoftware: " + gameName + " v." + version + " [Joe B, 2014]");
			Display.create();
		}catch(Exception e){new CrashReport(e);}
	}
	
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
		
		while(Keyboard.next()){}
		
		SoundStore.get().poll(0);
	}

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
	
	private void keyboardTick() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0 && gameState == GameState.GAME){
			menu = !menu;
			cooldown = 5f;
		}
		if(gameState == GameState.COMPUTER && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) gameState = GameState.GAME;
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_R)) player.spawn(1f, 1f);
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_N)){
			player.world = new World();
			player.world.generate();
			player.spawn(1, 1);
		}
	}
	
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
		
		if(gameState == GameState.GAME && Mouse.isButtonDown(0) && cooldown == 0) player.world.removeBlock(player.world.getBlock(player.world.lookingAt.x, player.world.lookingAt.y, player.world.lookingAt.z));
		if(gameState == GameState.GAME && Mouse.isButtonDown(1) && player.world.lookingAt.base.solid && cooldown == 0){
			BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
			player.world.addBlock(new BlockInstance(player.selectedBlock, adjacent.x, adjacent.y, adjacent.z));
		}
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_B) && player.world.lookingAt.base.solid && cooldown == 0){
			BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
			player.world.addBlock(new BlockInstance(Block.torch, adjacent.x, adjacent.y, adjacent.z));
		}
		
		if(Mouse.isButtonDown(1) || Mouse.isButtonDown(0)) cooldown = 3f;
		
		//Reset mouse position
		if(!menu) {
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
			Mouse.setGrabbed(true);
		}
		else Mouse.setGrabbed(false);
	}
	
	public void render(Camera camera){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		if(gameState == GameState.MENU){
			renderMenu("main");
			return;
		}
		
		glLoadIdentity();
		camera.useView();

		if(player.isInWater()) glColor3f(0.4f, 0.8f, 0.8f);
		
		for(Entity e : player.world.entities) e.render();
		player.world.render();
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
			
		}
		

		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);

	}
	
	private void loop(){
		java.awt.Font awtFont = new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 20);
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
