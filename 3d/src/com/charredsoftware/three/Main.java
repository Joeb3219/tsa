package com.charredsoftware.three;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glVertex2d;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;

import com.charredsoftware.three.computer.Computer;
import com.charredsoftware.three.entity.Player;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;
import com.charredsoftware.three.world.World;

public class Main {

	private static Font font;
	public static Player player;
	public static final int TPS = 30;
	public static final float DOWNWARD_ACCELERATION = -9.8f / 4;
	public static Camera camera;
	public static World world;
	private static int displayFPS = 0;
	private static Random r = new Random();
	public static float yOffset = 0f;
	public static float lastMX = 0, lastMY = 0, movementThreshold = 2f;
	public static boolean menu = false;
	public static GameState gameState = GameState.GAME;
	
	private static boolean RANDOM_MODE, DISPLAY_INFO = true;
	
	public static void main(String[] args){
		initializeDisplay();
		camera = new Camera(70, Display.getWidth() * 1.0f / Display.getHeight(), 0.3f, 150f);
		player = new Player(camera);
		loop();
		cleanDisplay();
	}
	
	private static void cleanDisplay(){
		Display.destroy();
	}
	
	private static void initializeDisplay(){
		try{
			Display.setDisplayMode(new DisplayMode(1200, 1200 * 9 / 16));
			Display.setResizable(true);
			Display.setTitle("CharredSoftware: A Game Demo [Joe B, 2014]");
			Display.create();
		}catch(Exception e){e.printStackTrace();}
	}
	
	private static float cooldown = 0f;
	public static void tick(Camera camera){
		if(cooldown > 0) cooldown --;
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && cooldown == 0){
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

			System.out.println(deltaX + " " + deltaY);
			
			if(camera.ry < 0) camera.ry = 360 + camera.ry;
			if(camera.ry >= 360) camera.ry = 360 - camera.ry;
			if(camera.rx < -90f) camera.rx = -90f;
			if(camera.rx > 90f) camera.rx = 90f;
			
			if(!menu) Mouse.setGrabbed(true);
			else Mouse.setGrabbed(false);
		}
		
		camera.x = player.x;
		yOffset = (float) ((3.0/3.0) * ((player.y % 2 == 0) ? player.y : player.y));
		if(player.isCrouching) yOffset += 1;
		camera.y = yOffset - 2;
		camera.z = player.z;
		
		if(world.lookingAt.base == Block.computer && Mouse.isButtonDown(1)) gameState = GameState.COMPUTER;
		if(Mouse.isButtonDown(1) && gameState == GameState.GAME && world.lookingAt.base.solid) world.addBlock(new BlockInstance(Block.blocks.get(r.nextInt(Block.blocks.size())), world.lookingAt.x, world.lookingAt.y + 1, world.lookingAt.z));
		if(Mouse.isButtonDown(0) && gameState == GameState.GAME) world.blocks.remove(world.getBlock(world.lookingAt.x, world.lookingAt.y, world.lookingAt.z));
		if(gameState == GameState.COMPUTER && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) gameState = GameState.GAME;
		if(gameState == GameState.GAME && Keyboard.isKeyDown(Keyboard.KEY_R)) player.setPosition(0, -1, 0);
		
	}
	
	public static void render(Camera camera){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glLoadIdentity();
		camera.useView();

		if(player.isInWater()) glColor3f(0.4f, 0.8f, 0.8f);
		
		world.render();
		if(gameState == GameState.COMPUTER) new Computer().draw();
		
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		
		glPushMatrix();
		glColor3f(1f, 1f, 1f);
		glLineWidth(4f);
		glBegin(GL_LINE_STRIP);
		glVertex2d(Display.getWidth() / 2, Display.getHeight() / 2 + 10);
		glVertex2d(Display.getWidth() / 2, Display.getHeight() / 2 - 10);
		glEnd();
		glBegin(GL_LINE_STRIP);
		glVertex2d(Display.getWidth() / 2 + 10, Display.getHeight() / 2);
		glVertex2d(Display.getWidth() / 2 - 10, Display.getHeight() / 2);
		glColor3f(1f, 1f, 1f);
		glEnd();
		glPopMatrix();

		glLoadIdentity();
		
		//Display Text
		if(DISPLAY_INFO){
			font.drawString(5, 5, "[x/y/z]: {" + player.x + "/" + player.y + "/" + player.z + "} [currentJumpingVelocity] {" + player.currentJumpingVelocity + "}" + " isJumping: " + player.isJumping);
			font.drawString(5, 25, "[rx/ry/rz]: {" + camera.rx + "/" + camera.ry + "/" + camera.rz + "} [cx/cy/cz]" + camera.x + "/" + camera.y + "/" + camera.z + "} yOffset: " + yOffset);
			font.drawString(5, 45, "Standing on : " + Main.world.getBlock(-player.x, -player.y - 1, -player.z).base.name + " [highest rel. solid/roof]: {" + Main.world.getRelativeHighestSolidBlock(new Position(-player.x, -player.y, -player.z)).base.name + "/" + Main.world.getClosestSolidRoofBlock(new Position(-player.x, (-player.y + 2), -player.z)).base.name + "}");
			font.drawString(5, 65, "Looking at " + world.lookingAt.base.name + " [" + world.lookingAt.x + ", " + world.lookingAt.y + ", " + world.lookingAt.z + "]");
			font.drawString(5, 85, "fps: " + displayFPS + "; blocksRendered: " + world.renderedBlocks);
			font.drawString(5, 105, "Health: " + player.health);
		}
		
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);


	}
	
	private static void loop(){
		java.awt.Font awtFont = new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 20);
		font = new TrueTypeFont(awtFont, false);
		
		int roomSize = 24;
		world = new World(roomSize);
		
		
		for(int x = -roomSize / 2; x < roomSize / 2; x ++ ){
			for(int y = 0; y < roomSize; y ++){
				world.blocks.add(new BlockInstance(Block.bricks, x, y, roomSize / 2));
				world.blocks.add(new BlockInstance(Block.bricks, x, y, -roomSize / 2));
			}
		}
		
		for(int z = -roomSize / 2; z < roomSize / 2; z ++){
			for(int y = 0; y < roomSize; y ++){
				world.blocks.add(new BlockInstance(Block.bricks, roomSize / 2, y, z));
				world.blocks.add(new BlockInstance(Block.bricks, -roomSize / 2, y, z));
			}
		}
		
		for(int x = -roomSize / 2; x < roomSize / 2; x ++){
			for(int z = -roomSize / 2; z < roomSize / 2; z ++){
				world.blocks.add(new BlockInstance(Block.grass, x, -1.0f, z));
				if(!(x == 0 && z == 0)) world.blocks.add(new BlockInstance(Block.ceiling, x, roomSize, z));
			}
		}
		
		for(int x = 6; x < 12; x ++){
			for(int z = 6; z < 12; z ++){
				for(int y = 0; y < 3; y ++){
					world.blocks.add(new BlockInstance(Block.bricks, x, y, z));
				}
			}
		}
		
		world.blocks.add(new BlockInstance(Block.boost, 10, 3, 10));
		
		/*
		for(int y = -2; y <= 0; y ++){
			for(int x = -25; x <= 25; x ++){
				for(int z = -25; z <= 25; z ++){
					if(y == -2) world.blocks.add(new BlockInstance(Block.grass, x, y, z));
					else{
						if(r.nextInt(2) == 1) world.blocks.add(new BlockInstance(Block.grass, x, y, z));
						else if(r.nextInt(2) == 1) world.blocks.add(new BlockInstance(Block.bricks, x, y, z));
						else if(r.nextInt(2) == 1) world.blocks.add(new BlockInstance(Block.boost, x, y, z));
						else if(r.nextInt(2) == 1) world.blocks.add(new BlockInstance(Block.glass, x, y, z));
						else if(r.nextInt(2) == 1) world.blocks.add(new BlockInstance(Block.ceiling, x, y, z));
						else if(r.nextInt(2) == 1) world.blocks.add(new BlockInstance(Block.wood, x, y, z));
						//else if(r.nextInt(5) == 1) world.blocks.add(new BlockInstance(Block.grass, x, y, z));
						else{} //Air
					}
				}
			}
		}
		*/
		
		
		world.blocks.add(new BlockInstance(Block.glass, 12, 0, -12));
		world.blocks.add(new BlockInstance(Block.glass, 12, 2, -12));
		world.blocks.add(new BlockInstance(Block.glass, 12, 4, -12));
		
		
		world.blocks.add(new BlockInstance(Block.wood, 4, 0, 6));
		world.blocks.add(new BlockInstance(Block.wood, 4, 1, 8));
		world.blocks.add(new BlockInstance(Block.wood, 4, 2, 10));
		world.blocks.add(new BlockInstance(Block.wood, 4, 0, 8));
		world.blocks.add(new BlockInstance(Block.wood, 4, 0, 10));
		world.blocks.add(new BlockInstance(Block.wood, 4, 1, 10));
		
		world.blocks.add(new BlockInstance(Block.wall, -3, 3, 11));
		
		for(int x = -8; x <= -4; x ++){
			for(int z = -8; z <= -4; z ++){
				world.blocks.add(new BlockInstance(Block.bricks, x, 0, z));
			}
		}
		
		world.blocks.add(new BlockInstance(Block.computer, -6, 1, -6));
		//world.blocks.add(new BlockInstance(Block.computer, -5, 1, -6));
		
		for(int x = 4; x <= 12; x ++){
			for(int z = -12; z <= -4; z ++){
				world.blocks.add(new BlockInstance(Block.water, x, 0, z));
			}
		}

		
		//world.dumpAllBlocks();
		
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double nanoSeconds = 1000000000.0 / TPS;
		double delta = 0;
		int fps = 0, ticks = 0;
		/*int roomSize = 26;
		World world = new World(roomSize);
		
		for(int x = -roomSize / 2; x < roomSize / 2; x += 2){
			for(int y = -2; y < roomSize; y += 2){
				world.setBlock(new BlockInstance(test, x, y, roomSize / 2));
				world.setBlock(new BlockInstance(test, x, y, -roomSize / 2));
			}
		}*/
		
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
		
		Display.destroy();
	}
	
}
