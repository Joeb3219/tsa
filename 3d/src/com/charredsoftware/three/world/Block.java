package com.charredsoftware.three.world;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_CURRENT_BIT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.three.CrashReport;
import com.charredsoftware.three.util.FileUtilities;

public class Block {

	public float id, meta;
	public Texture texture;
	public boolean solid = true;
	public String name;
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	
	
	public static Block air = new Block(0, 0, "Air", false);
	public static Block bricks = new Block(1, 0, "Bricks", Block.loadTexture("brick.jpg"));
	public static Block grass = new Block(2, 0, "Grass", Block.loadTexture("grass.jpg"));
	public static Block ceiling = new Block(3, 0, "Ceiling", Block.loadTexture("ceiling.jpg"));
	public static Block wood = new Block(4, 0, "Wood", Block.loadTexture("wood.jpg"));
	public static Block water = new Block(5, 0, "Water", Block.loadTexture("water.jpg"), false);
	public static Block boost = new Block(6, 0, "Boost Block", Block.loadTexture("boost.jpg"));
	public static Block wall = new Block(7, 0,"Wall", Block.loadTexture("wall.jpg"));
	public static Block computer = new Block(8, 0, "Computer", Block.loadTexture("computer.jpg"));

	public Block(float id, float meta, String name, boolean solid){
		this.id = id;
		this.meta = meta;
		this.name = name;
		this.texture = null;
		this.solid = solid;
		blocks.add(this);
	}
	
	public Block(float id, float meta, String name, Texture texture){
		this.id = id;
		this.meta = meta;
		this.name = name;
		this.texture = texture;
		blocks.add(this);
	}

	public Block(float id, float meta, String name, Texture texture, boolean solid){
		this.id = id;
		this.meta = meta;
		this.name = name;
		this.texture = texture;
		this.solid = solid;
		blocks.add(this);
	}
	
	
	//Expects id:meta
	public static Block getBlock(String idString){
		float id = Float.parseFloat(idString.split(":")[0]);
		float meta = Float.parseFloat(idString.split(":")[1]);
		for(Block b : blocks){
			if(b.id == id && b.meta == meta) return b;
		}
		
		return air;
	}
	
	public static Texture loadTexture(String path){
		try{
			if(path.contains("png")) return TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + path));
			else return TextureLoader.getTexture("jpg", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + path));
		}catch(Exception e){new CrashReport(e);}
			return null;
	}
	
	public void draw(float x, float y, float z, int alpha){
		glPushAttrib(GL_CURRENT_BIT);
		glColor4f(.1f, .1f, .1f, alpha);
		draw(x, y, z);
		glClear(GL_ALPHA);
		glPopAttrib();
	}
	
	public void drawSetup(){
		if(this == Block.air) return;
		texture.bind();
	}
	
	public void drawCleanup(){
	}
	
	public void drawBlock(float x, float y, float z){
		if(this == Block.air) return;

		glPushMatrix();
		glTranslatef(x, y, z);
		glBegin(GL_QUADS);
		
		float leftBound = -0.5f;
		float rightBound = 0.5f;
		
		//Front
		glTexCoord2f(0, 2/4f); glVertex3f(leftBound,leftBound,rightBound);
		glTexCoord2f(0, 1/4f); glVertex3f(leftBound,rightBound,rightBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);

		//Right
		glTexCoord2f(2/4f, 2/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(2/4f, 1/4f); glVertex3f(leftBound,rightBound,leftBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound,leftBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,leftBound);

		//Left
		glTexCoord2f(2/4f, 2/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(3/4f, 2/4f); glVertex3f(leftBound,leftBound,rightBound);
		glTexCoord2f(3/4f, 1/4f); glVertex3f(leftBound,rightBound,rightBound);
		glTexCoord2f(2/4f, 1/4f); glVertex3f(leftBound,rightBound,leftBound);

		//Back
		glTexCoord2f(4/4f, 2/4f); glVertex3f(rightBound,leftBound,leftBound);
		glTexCoord2f(3/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);
		glTexCoord2f(3/4f, 1/4f); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(4/4f, 1/4f); glVertex3f(rightBound,rightBound,leftBound);

		//Bottom
		glTexCoord2f(0/4f, 3/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(1/4f, 3/4f); glVertex3f(rightBound,leftBound,leftBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);
		glTexCoord2f(0/4f, 2/4f); glVertex3f(leftBound,leftBound,rightBound);

		//Top
		glTexCoord2f(0/4f, 0/4f); glVertex3f(leftBound,rightBound,leftBound);
		glTexCoord2f(1/4f, 0/4f); glVertex3f(rightBound,rightBound,leftBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(0/4f, 1/4f); glVertex3f(leftBound,rightBound,rightBound);
	
		glEnd();
		glPopMatrix();
	}
	
	public void draw(float x, float y, float z){
		if(texture == null) return;
		drawSetup();
		drawBlock(x, y, z);
		drawCleanup();
	}
	
}
