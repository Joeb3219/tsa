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
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;

import com.charredsoftware.three.entity.Player;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;
import com.charredsoftware.three.world.World;

public class Main {

	public String gameName = "NovaScript";
	public String version = "1.0.15";
	public Font font;
	public Player player;
	public static final int DESIRED_TPS = 30;
	private static final float mouseMovementThreshold = 2f;
	public Camera camera;
	int displayFPS = 0;
	public boolean menu = false, DISPLAY_INFO = true;
	public GameState gameState = GameState.GAME;
	private float cooldown = 0f;
	
	private static Main _INSTANCE = null;
	
	
	private Main(){
		initializeDisplay();
		camera = new Camera(65, Display.getWidth() * 1.0f / Display.getHeight(), 0.3f, 75f);
		player = new Player(new World(0), camera);
		player.selectedBlock = Block.computer;
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
			CrashReport crash = new CrashReport(t);
			 //We crashed! Cannot recover! Kill the system!
		}
		game.cleanDisplay();
	}
	
	void cleanDisplay(){
		Display.destroy();
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

		if(gameState == GameState.GAME){
			if(Keyboard.isKeyDown(Keyboard.KEY_F1)) DISPLAY_INFO = !DISPLAY_INFO;
			player.update();
			
			mouseTick();
		}
		
		camera.calculatePosition(player);
		
		keyboardTick();
		
		if(gameState == GameState.COMPUTER && cooldown == 0) player.selectedPeripheral.update();
		
		while(Keyboard.next()){}
		
	}

	private void keyboardTick() {
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0 && gameState == GameState.GAME){
			menu = !menu;
			cooldown = 5f;
		}
		if(gameState == GameState.COMPUTER && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) gameState = GameState.GAME;
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_R)) player.setPosition(2f, 1f, 2f);
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_N)){
			player.world = new World();
			player.world.generate();
			player.setPosition(2f, 1f, 2f);
		}
	}
	
	private void mouseTick(){
		float deltaX = Mouse.getDX();
		float deltaY = Mouse.getDY();
		
		if(Math.abs(deltaX) > mouseMovementThreshold){
			if(deltaX > 0) camera.ry += 1.8f * (Math.min(deltaX, 28) / 4);
			if(deltaX < 0) camera.ry -= 1.8f * (Math.min(-deltaX, 28) / 4);
		}
		if(Math.abs(deltaY) > mouseMovementThreshold){
			if(deltaY < 0) camera.rx += 1.8f * (Math.min(-deltaY, 28) / 4);
			if(deltaY > 0) camera.rx -= 1.8f * (Math.min(deltaY, 28) / 4);
		}

		if(camera.ry < 0) camera.ry = 360 + camera.ry;
		if(camera.ry >= 360) camera.ry = 360 - camera.ry;
		if(camera.rx < -90f) camera.rx = -90f;
		if(camera.rx > 90f) camera.rx = 90f;
		
		float dWheel = Mouse.getDWheel();
		
		if(dWheel > 0){
			if(Block.blocks.indexOf(player.selectedBlock) < Block.blocks.size() - 1) player.selectedBlock = Block.blocks.get(Block.blocks.indexOf(player.selectedBlock) + 1);
			else player.selectedBlock = Block.blocks.get(1);
		}
		if(dWheel < 0){
			if(Block.blocks.indexOf(player.selectedBlock) > 1) player.selectedBlock = Block.blocks.get(Block.blocks.indexOf(player.selectedBlock) - 1);
			else player.selectedBlock = Block.blocks.get(Block.blocks.size() - 1);
		}
		
		//Check if attempting to break/place/right-click
		if(player.world.lookingAt.base == Block.computer && Mouse.isButtonDown(1) && cooldown == 0 && !player.isCrouching){
			player.selectedPeripheral = player.world.getPeripheral(player.world.lookingAt.x, player.world.lookingAt.y, player.world.lookingAt.z);
			gameState = GameState.COMPUTER;
		}
		if(gameState == GameState.GAME && Mouse.isButtonDown(0) && cooldown == 0) player.world.removeBlock(player.world.getBlock(player.world.lookingAt.x, player.world.lookingAt.y, player.world.lookingAt.z));
		if(gameState == GameState.GAME && Mouse.isButtonDown(1) && player.world.lookingAt.base.solid && cooldown == 0){
			BlockInstance adjacent = player.world.getBlockAdjectLookingAt();
			player.world.addBlock(new BlockInstance(player.selectedBlock, adjacent.x, adjacent.y, adjacent.z));
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
		
		glLoadIdentity();
		camera.useView();

		if(player.isInWater()) glColor3f(0.4f, 0.8f, 0.8f);
		
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
		
		if(gameState == GameState.COMPUTER) player.selectedPeripheral.draw();
		
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
			
			//Display Text
			if(DISPLAY_INFO){
				font.drawString(5, 5, "[x/y/z]: {" + player.x + "/" + player.y + "/" + player.z + "} REGION: " + player.world.findRegion(player.x, player.z).toString() + " [currentJumpingVelocity] {" + player.currentJumpingVelocity + "}" + " isJumping: " + player.isJumping);
				font.drawString(5, 25, "[rx/ry/rz]: {" + camera.rx + "/" + camera.ry + "/" + camera.rz + "} [cx/cy/cz]" + camera.x + "/" + camera.y + "/" + camera.z + "} yOffset: " + camera.yOffset);
				font.drawString(5, 45, "Standing on : " + getInstance().player.world.getBlock(player.x, player.y - 1, player.z).base.name + " [highest rel. solid/roof]: {" + getInstance().player.world.getRelativeHighestSolidBlock(new Position(player.x, player.y, player.z)).base.name + "/" + getInstance().player.world.getClosestSolidRoofBlock(new Position(player.x, (player.y + player.height), player.z)).base.name + "}");
				font.drawString(5, 65, "Looking at " + player.world.lookingAt.base.name + " [" + player.world.lookingAt.x + ", " + player.world.lookingAt.y + ", " + player.world.lookingAt.z + "]");
				font.drawString(5, 85, "fps: " + displayFPS + "; blocksRendered: " + player.world.renderedBlocks + " {checked: " + player.world.blocksChecked + "}");
				font.drawString(5, 105, "Health: " + player.health);
			}
		}
		

		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		
		if(gameState == GameState.GAME){
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
		glMatrixMode(GL_MODELVIEW);

	}
	
	private void loop(){
		java.awt.Font awtFont = new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 20);
		font = new TrueTypeFont(awtFont, false);
		
		player.world.generate();
		
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
	}
	
}
