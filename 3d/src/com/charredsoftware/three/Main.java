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

import java.util.Random;
import java.util.Timer;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;

import com.charredsoftware.three.computer.Peripheral;
import com.charredsoftware.three.entity.Player;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;
import com.charredsoftware.three.world.World;

public class Main {

	public static String gameName = "NovaScript";
	public static String version = "1.0.15";
	public static Font font;
	public static Player player;
	public static final int TPS = 30;
	public static final float DOWNWARD_ACCELERATION = -9.8f;
	public static Camera camera;
	public static World world;
	static int displayFPS = 0;
	public static float yOffset = 0f;
	public static float lastMX = 0, lastMY = 0, movementThreshold = 2f;
	public static boolean menu = false;
	public static GameState gameState = GameState.GAME;
	public static Block selectedBlock;
	public static Peripheral selectedPeripheral = null;
	public static Random r = new Random();
	private static float cooldown = 0f;
	
	private static boolean DISPLAY_INFO = true;
	
	public static void main(String[] args){
		initializeDisplay();
		camera = new Camera(65, Display.getWidth() * 1.0f / Display.getHeight(), 0.3f, 100f);
		player = new Player(camera);
		selectedBlock = Block.computer;
		try{
		loop();
		}catch(Throwable t){
			CrashReport crash = new CrashReport(t);
			String lnBreak = System.getProperty("line.separator");
			Sys.alert(gameName + " " + version + ": Crash Report", "NovaScript has crashed." + lnBreak + lnBreak + crash.synop + lnBreak + lnBreak + "Refer to " + crash.file.getAbsolutePath());
			cleanDisplay();
			
			System.exit(0); //We crashed! Cannot recover! Kill the system!
		}
		cleanDisplay();
	}
	
	private static void cleanDisplay(){
		Display.destroy();
	}
	
	private static void initializeDisplay(){
		try{
			Display.setDisplayMode(new DisplayMode(1200, 1200 * 9 / 16));
			Display.setResizable(true);
			Display.setTitle("CharredSoftware: NovaScript [Joe B, 2014]");
			Display.create();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void tick(Camera camera){
		if(cooldown > 0) cooldown --;
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0 && gameState == GameState.GAME){
			menu = !menu;
			cooldown = 5f;
		}

		if(gameState == GameState.GAME){
			if(Keyboard.isKeyDown(Keyboard.KEY_F1)) DISPLAY_INFO = !DISPLAY_INFO;
			player.update();
			
			float deltaX = Mouse.getDX();
			float deltaY = Mouse.getDY();
			
			if(Math.abs(deltaX) > movementThreshold){
				if(deltaX > 0) camera.ry += 1.8f * (Math.min(deltaX, 28) / 4);
				if(deltaX < 0) camera.ry -= 1.8f * (Math.min(-deltaX, 28) / 4);
			}
			if(Math.abs(deltaY) > movementThreshold){
				if(deltaY < 0 && camera.rx < 90f) camera.rx += 1.8f * (Math.min(-deltaY, 28) / 4);
				if(deltaY > 0 && camera.rx > -90f) camera.rx -= 1.8f * (Math.min(deltaY, 28) / 4);
			}

			if(camera.ry < 0) camera.ry = 360 + camera.ry;
			if(camera.ry >= 360) camera.ry = 360 - camera.ry;
			if(camera.rx < -90f) camera.rx = -90f;
			if(camera.rx > 90f) camera.rx = 90f;
			
			float dWheel = Mouse.getDWheel();
			
			if(dWheel > 0){
				if(Block.blocks.indexOf(selectedBlock) < Block.blocks.size() - 1) selectedBlock = Block.blocks.get(Block.blocks.indexOf(selectedBlock) + 1);
				else selectedBlock = Block.blocks.get(1);
			}
			if(dWheel < 0){
				if(Block.blocks.indexOf(selectedBlock) > 1) selectedBlock = Block.blocks.get(Block.blocks.indexOf(selectedBlock) - 1);
				else selectedBlock = Block.blocks.get(Block.blocks.size() - 1);
			}
			
			if(!menu) {
				Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
				Mouse.setGrabbed(true);
			}
			else Mouse.setGrabbed(false);
		}
		
		camera.x = player.x;
		yOffset = (float) ((3.0/3.0) * ((player.y % 2 == 0) ? player.y : player.y));
		if(player.isCrouching) yOffset += 1;
		camera.y = yOffset - 1;
		camera.z = player.z;
		
		if(world.lookingAt.base == Block.computer && Mouse.isButtonDown(1) && cooldown == 0 && !player.isCrouching){
			selectedPeripheral = world.getPeripheral(world.lookingAt.x, world.lookingAt.y, world.lookingAt.z);
			gameState = GameState.COMPUTER;
		}
		if(Mouse.isButtonDown(1) && gameState == GameState.GAME && world.lookingAt.base.solid && cooldown == 0){
			BlockInstance adjacent = world.getBlockAdjectLookingAt();
			world.addBlock(new BlockInstance(selectedBlock, adjacent.x, adjacent.y, adjacent.z));
		}
		if(Mouse.isButtonDown(0) && gameState == GameState.GAME && cooldown == 0) world.removeBlock(world.getBlock(world.lookingAt.x, world.lookingAt.y, world.lookingAt.z));
		if(gameState == GameState.COMPUTER && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) gameState = GameState.GAME;
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_R)) player.setPosition(-2f, -1f, -2f);
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_N)){
			world = new World(1);
			player.setPosition(-2f, -1f, -2f);
		}
		
		if(gameState == GameState.COMPUTER) selectedPeripheral.update();
		
		if(gameState == GameState.COMPUTER) selectedPeripheral.update();
		
		if(gameState == GameState.COMPUTER && cooldown == 0) selectedPeripheral.update();
		
		if(Mouse.isButtonDown(1) || Mouse.isButtonDown(0)) cooldown = 3f;
		
		while(Keyboard.next()){}
		
	}
	
	public static void render(Camera camera){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		glLoadIdentity();
		camera.useView();

		if(player.isInWater()) glColor3f(0.4f, 0.8f, 0.8f);
		
		world.render();
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
		
		if(gameState == GameState.COMPUTER) selectedPeripheral.draw();
		
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
				font.drawString(5, 5, "[x/y/z]: {" + player.x + "/" + player.y + "/" + player.z + "} REGION: " + world.findRegion(player.x, player.z).toString() + " [currentJumpingVelocity] {" + player.currentJumpingVelocity + "}" + " isJumping: " + player.isJumping);
				font.drawString(5, 25, "[rx/ry/rz]: {" + camera.rx + "/" + camera.ry + "/" + camera.rz + "} [cx/cy/cz]" + camera.x + "/" + camera.y + "/" + camera.z + "} yOffset: " + yOffset);
				font.drawString(5, 45, "Standing on : " + Main.world.getBlock(-player.x, -player.y - 1, -player.z).base.name + " [highest rel. solid/roof]: {" + Main.world.getRelativeHighestSolidBlock(new Position(-player.x, -player.y, -player.z)).base.name + "/" + Main.world.getClosestSolidRoofBlock(new Position(-player.x, (-player.y + 2), -player.z)).base.name + "}");
				font.drawString(5, 65, "Looking at " + world.lookingAt.base.name + " [" + world.lookingAt.x + ", " + world.lookingAt.y + ", " + world.lookingAt.z + "]");
				font.drawString(5, 85, "fps: " + displayFPS + "; blocksRendered: " + world.renderedBlocks);
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
			selectedBlock.draw(0f,0f,0f);
			glPopMatrix();
		}
		glMatrixMode(GL_MODELVIEW);

	}
	
	private static void loop(){
		java.awt.Font awtFont = new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 20);
		font = new TrueTypeFont(awtFont, false);
		
		world = new World();
		world.generate();
		
		Timer t = new Timer();
		//t.scheduleAtFixedRate(new TimerTask(){public void run(){Main.world.save();}}, 5, 5000);
		
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double nanoSeconds = 1000000000.0 / TPS;
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
				tick(camera);
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
		world.save();
		
		Display.destroy();
	}
	
}
