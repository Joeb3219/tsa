package com.charredsoftware.three.world;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.FileInputStream;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Block {

	public Texture texture;
	public boolean solid = true;
	public String name;
	
	
	public static Block air = new Block("Air", false);
	public static Block bricks = new Block("Bricks", Block.loadTexture("brick.jpg"));
	public static Block grass = new Block("Grass", Block.loadTexture("grass.jpg"));
	public static Block ceiling = new Block("Ceiling", Block.loadTexture("ceiling.jpg"));
	public static Block wood = new Block("Wood", Block.loadTexture("wood.jpg"));
	public static Block water = new Block("Water", Block.loadTexture("water.jpg"), false);
	public static Block glass = new Block("Glass", Block.loadTexture("glass.png"));
	public static Block boost = new Block("Boost Block", Block.loadTexture("boost.jpg"));

	public Block(String name, boolean solid){
		this.name = name;
		this.texture = null;
		this.solid = solid;
	}
	
	public Block(String name, Texture texture){
		this.name = name;
		this.texture = texture;
	}

	public Block(String name, Texture texture, boolean solid){
		this.name = name;
		this.texture = texture;
		this.solid = solid;
	}
	
	public static Texture loadTexture(String path){
		try{
			if(path.contains("png")) return TextureLoader.getTexture("png", new FileInputStream(new File("res/textures/" + path)));
			else return TextureLoader.getTexture("jpg", new FileInputStream(new File("res/textures/" + path)));
		}catch(Exception e){e.printStackTrace();}
			return null;
	}
	
	
	public void draw(float x, float y, float z){
		if(texture == null) return;
		int vectorLength = 2;
		glPushMatrix();
		glScalef(1f, 1.8f, 1f);
		glTranslatef(x, y, z);
		texture.bind();
		glBegin(GL_QUADS);
		
		glTexCoord2f(0, 0); glVertex3f(-1,-1,1);
		glTexCoord2f(0, 1); glVertex3f(-1,1,1);
		glTexCoord2f(1, 1); glVertex3f(1,1,1);
		glTexCoord2f(1, 0); glVertex3f(1,-1,1);

		glTexCoord2f(0, 0); glVertex3f(-1,-1,-1);
		glTexCoord2f(0, 1); glVertex3f(-1,1,-1);
		glTexCoord2f(1, 1); glVertex3f(1,1,-1);
		glTexCoord2f(1, 0); glVertex3f(1,-1,-1);

		glTexCoord2f(0, 0); glVertex3f(-1,-1,-1);
		glTexCoord2f(0, 1); glVertex3f(-1,-1,1);
		glTexCoord2f(1, 1); glVertex3f(-1,1,1);
		glTexCoord2f(1, 0); glVertex3f(-1,1,-1);

		glTexCoord2f(0, 0); glVertex3f(1,-1,-1);
		glTexCoord2f(0, 1); glVertex3f(1,-1,1);
		glTexCoord2f(1, 1); glVertex3f(1,1,1);
		glTexCoord2f(1, 0); glVertex3f(1,1,-1);

		glTexCoord2f(0, 0); glVertex3f(-1,-1,-1);
		glTexCoord2f(0, 1); glVertex3f(1,-1,-1);
		glTexCoord2f(1, 1); glVertex3f(1,-1,1);
		glTexCoord2f(1, 0); glVertex3f(-1,-1,1);

		glTexCoord2f(0, 0); glVertex3f(-1,1,-1);
		glTexCoord2f(0, 1); glVertex3f(1,1,-1);
		glTexCoord2f(1, 1); glVertex3f(1,1,1);
		glTexCoord2f(1, 0); glVertex3f(-1,1,1);
	
		glEnd();
		glPopMatrix();
	}
	
}
