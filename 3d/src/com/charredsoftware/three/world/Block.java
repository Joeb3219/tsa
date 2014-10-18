package com.charredsoftware.three.world;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Block {

	public Texture texture;
	public boolean solid = true;
	public String name;
	public static ArrayList<Block> blocks = new ArrayList<Block>();
	
	
	public static Block air = new Block("Air", false);
	public static Block bricks = new Block("Bricks", Block.loadTexture("brick.jpg"));
	public static Block grass = new Block("Grass", Block.loadTexture("grass.jpg"));
	public static Block ceiling = new Block("Ceiling", Block.loadTexture("ceiling.jpg"));
	public static Block wood = new Block("Wood", Block.loadTexture("wood.jpg"));
	public static Block water = new Block("Water", Block.loadTexture("water.jpg"), false);
	public static Block glass = new Block("Glass", Block.loadTexture("glass.png"));
	public static Block boost = new Block("Boost Block", Block.loadTexture("boost.jpg"));
	public static Block wall = new Block("Wall", Block.loadTexture("wall.jpg"));

	public Block(String name, boolean solid){
		this.name = name;
		this.texture = null;
		this.solid = solid;
		blocks.add(this);
	}
	
	public Block(String name, Texture texture){
		this.name = name;
		this.texture = texture;
		blocks.add(this);
	}

	public Block(String name, Texture texture, boolean solid){
		this.name = name;
		this.texture = texture;
		this.solid = solid;
		blocks.add(this);
	}
	
	public static Texture loadTexture(String path){
		try{
			if(path.contains("png")) return TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream("textures/" + path));
			else return TextureLoader.getTexture("jpg", ClassLoader.getSystemResourceAsStream("textures/" + path));
		}catch(Exception e){e.printStackTrace();}
			return null;
	}
	
	
	public void draw(float x, float y, float z){
		if(texture == null) return;
		glPushMatrix();
		glTranslatef(x, y, z);
		texture.bind();
		glBegin(GL_QUADS);
		
		float leftBound = -0.5f;
		float rightBound = 0.5f;
		
		glTexCoord2f(0, 0); glVertex3f(leftBound,leftBound,rightBound);
		glTexCoord2f(0, 1); glVertex3f(leftBound,rightBound,rightBound);
		glTexCoord2f(1, 1); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(1, 0); glVertex3f(rightBound,leftBound,rightBound);

		glTexCoord2f(0, 0); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(0, 1); glVertex3f(leftBound,rightBound,leftBound);
		glTexCoord2f(1, 1); glVertex3f(rightBound,rightBound,leftBound);
		glTexCoord2f(1, 0); glVertex3f(rightBound,leftBound,leftBound);

		glTexCoord2f(0, 0); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(0, 1); glVertex3f(leftBound,leftBound,rightBound);
		glTexCoord2f(1, 1); glVertex3f(leftBound,rightBound,rightBound);
		glTexCoord2f(1, 0); glVertex3f(leftBound,rightBound,leftBound);

		glTexCoord2f(0, 0); glVertex3f(rightBound,leftBound,leftBound);
		glTexCoord2f(0, 1); glVertex3f(rightBound,leftBound,rightBound);
		glTexCoord2f(1, 1); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(1, 0); glVertex3f(rightBound,rightBound,leftBound);

		glTexCoord2f(0, 0); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(0, 1); glVertex3f(rightBound,leftBound,leftBound);
		glTexCoord2f(1, 1); glVertex3f(rightBound,leftBound,rightBound);
		glTexCoord2f(1, 0); glVertex3f(leftBound,leftBound,rightBound);

		glTexCoord2f(0, 0); glVertex3f(leftBound,rightBound,leftBound);
		glTexCoord2f(0, 1); glVertex3f(rightBound,rightBound,leftBound);
		glTexCoord2f(1, 1); glVertex3f(rightBound,rightBound,rightBound);
		glTexCoord2f(1, 0); glVertex3f(leftBound,rightBound,rightBound);
	
		glEnd();
		glPopMatrix();
	}
	
}
