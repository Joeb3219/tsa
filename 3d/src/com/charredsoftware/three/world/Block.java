package com.charredsoftware.three.world;

import static org.lwjgl.opengl.GL11.*;

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
	public static Block computer = new Block("Computer", Block.loadTexture("computer.jpg"));

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
	
	public void draw(float x, float y, float z, int alpha){
		glColor4f(.1f, .1f, .1f, alpha);
		draw(x, y, z);
		glClear(GL_COLOR_BUFFER_BIT);
		glClear(GL_ALPHA);
	}
	
	public void draw(float x, float y, float z){
		if(texture == null) return;
		glPushMatrix();
		glTranslatef(x, y, z);
		texture.bind();
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
	
}
