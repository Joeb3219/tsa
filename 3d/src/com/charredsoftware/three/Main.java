package com.charredsoftware.three;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glOrtho;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;

import com.charredsoftware.three.entity.Player;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
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
	
	public static void main(String[] args){
		initializeDisplay();
		camera = new Camera(70, Display.getWidth() / Display.getHeight(), 0.3f, 150f);
		player = new Player(camera);
		loop();
		cleanDisplay();
	}
	
	private static void cleanDisplay(){
		Display.destroy();
	}
	
	private static void initializeDisplay(){
		try{
			Display.setDisplayMode(new DisplayMode(1000, 1000 * 9 / 16));
			Display.setResizable(true);
			Display.setTitle("CharredSoftware: Bestest game evar");
			Display.create();
		}catch(Exception e){e.printStackTrace();}
	}
	
	public static void tick(Camera camera){
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) camera.ry += 1.8f;
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) camera.ry -= 1.8;
		if(Keyboard.isKeyDown(Keyboard.KEY_UP) && camera.rx > -90f) camera.rx -= 1.8f;
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN) && camera.rx < 90f) camera.rx += 1.8f;
		//if(camera.ry > 180) camera.ry = -180 + (camera.ry - 180);
		//if(camera.ry < -180) camera.ry = 180 - (camera.ry + 180);
		player.update();
		camera.x = player.x;
		camera.y = player.y - 4;
		if(camera.y % 2 != 0) camera.y --;
		camera.z = player.z;
	}
	
	public static void render(Camera camera){
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glLoadIdentity();
		camera.useView();

		if(player.isInWater()) glColor3f(0.4f, 0.8f, 0.8f);
		
		world.render();
		
		glMatrixMode(GL_PROJECTION);
		glPushMatrix();
		glLoadIdentity();
		glOrtho(0, 800, 600, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST); 
		glClear(GL_DEPTH_BUFFER_BIT);
		glLoadIdentity();
		
		//Display Text
				font.drawString(5, 5, "[x/y/z]: {" + player.x + "/" + player.y + "/" + player.z + "}");
				font.drawString(5, 25, "[rx/ry/rz]: {" + camera.rx + "/" + camera.ry + "/" + camera.rz + "}");
				font.drawString(5, 45, "Standing on : " + Main.world.getBlock(-player.x, -player.y - 2, -player.z).base.name);
				font.drawString(5, 65, "fps: " + displayFPS);
				font.drawString(5, 85, "Health: " + player.health);
		
		glEnable(GL_DEPTH_TEST);
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		glMatrixMode(GL_MODELVIEW);
		

	}
	
	private static void loop(){
		java.awt.Font awtFont = new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 16);
		font = new TrueTypeFont(awtFont, false);
		
		int roomSize = 32;
		world = new World(roomSize);
		
		/*for(int x = -roomSize / 2; x < roomSize / 2; x += 2){
			for(int y = -2; y < roomSize; y += 2){
				world.blocks.add(new BlockInstance(Block.bricks, x, y, roomSize / 2));
				world.blocks.add(new BlockInstance(Block.bricks, x, y, -roomSize / 2));
			}
		}
		
		for(int z = -roomSize / 2; z < roomSize / 2; z += 2){
			for(int y = -2; y < roomSize; y += 2){
				world.blocks.add(new BlockInstance(Block.bricks, roomSize / 2, y, z));
				world.blocks.add(new BlockInstance(Block.bricks, -roomSize / 2, y, z));
			}
		}
		
		for(int x = -roomSize / 2; x < roomSize / 2; x += 2){
			for(int z = -roomSize / 2; z < roomSize / 2; z += 2){
				if(!(x == 4 && z == 4)) world.blocks.add(new BlockInstance(Block.grass, x, -2.0f, z));
				world.blocks.add(new BlockInstance(Block.ceiling, x, roomSize, z));
			}
		}
		
		for(int x = 6; x < 12; x += 2){
			for(int z = 6; z < 12; z += 2){
				for(int y = -2; y < 6; y += 2){
					world.blocks.add(new BlockInstance(Block.bricks, x, y, z));
				}
			}
		}*/
		
		for(int y = -2; y <= 0; y += 2){
			for(int x = -50; x <= 50; x += 2){
				for(int z = -50; z <= 50; z += 2){
					if(y == -2) world.blocks.add(new BlockInstance(Block.grass, x, y, z));
					else{
						if(r.nextInt(2) == 1) world.blocks.add(new BlockInstance(Block.grass, x, y, z));
						else if(r.nextInt(2) == 1) world.blocks.add(new BlockInstance(Block.bricks, x, y, z));
						//else if(r.nextInt(5) == 1) world.blocks.add(new BlockInstance(Block.grass, x, y, z));
						else{} //Air
					}
				}
			}
		}
		
		
		/*world.blocks.add(new BlockInstance(Block.glass, 12, 0, -12));
		world.blocks.add(new BlockInstance(Block.glass, 12, 2, -12));
		world.blocks.add(new BlockInstance(Block.glass, 12, 4, -12));
		
		
		world.blocks.add(new BlockInstance(Block.wood, 4, 0, 6));
		world.blocks.add(new BlockInstance(Block.wood, 4, 2, 8));
		world.blocks.add(new BlockInstance(Block.wood, 4, 4, 10));
		world.blocks.add(new BlockInstance(Block.wood, 4, 0, 8));
		world.blocks.add(new BlockInstance(Block.wood, 4, 0, 10));
		world.blocks.add(new BlockInstance(Block.wood, 4, 2, 10));
		
		for(int x = -8; x <= -4; x += 2){
			for(int z = -8; z <= -4; z += 2){
				world.blocks.add(new BlockInstance(Block.bricks, x, 0, z));
			}
		}

		world.blocks.add(new BlockInstance(Block.wood, -6, 2, -6));
		
		for(int x = 4; x <= 12; x += 2){
			for(int z = -12; z <= -4; z += 2){
				world.blocks.add(new BlockInstance(Block.water, x, 0, z));
			}
		}
		
		*/
		
		world.dumpAllBlocks();
		
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
				camera.resetAspectRatio(Display.getWidth() / Display.getHeight());
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
