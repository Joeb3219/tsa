package com.charredsoftware.tsa.world;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.util.FileUtilities;

/**
 * Block class. Handles texture loading, id mapping of blocks.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

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
	public static Block wall = new Block(7, 0, "Wall", Block.loadTexture("wall.jpg"));
	public static Block chest = new Block(8, 0, "Chest", Block.loadTexture("chest.jpg"));
	public static Block charredBlock = new Block(-2, 0, "charredblock", Block.loadTexture("charredsoftware.png"));
	
	/**
	 * Creates a textureless block.
	 * @param id The ID of the block
	 * @param meta The meta value of the block
	 * @param name The name of the block
	 * @param solid Whether or not block is a solid
	 */
	public Block(float id, float meta, String name, boolean solid){
		this.id = id;
		this.meta = meta;
		this.name = name;
		this.texture = null;
		this.solid = solid;
		blocks.add(this);
	}
	
	/**
	 * Creates a block
	 * @param id The ID of the block
	 * @param meta The meta value of the block
	 * @param name The name of the block
	 * @param texture The texture of the block
	 */
	public Block(float id, float meta, String name, Texture texture){
		this.id = id;
		this.meta = meta;
		this.name = name;
		this.texture = texture;
		blocks.add(this);
	}

	/**
	 * Creates a block
	 * @param id The ID of the block
	 * @param meta The meta value of the block
	 * @param name The name of the block
	 * @param texture The texture of the block
	 * @param solid Whether or not block is solid.
	 */
	public Block(float id, float meta, String name, Texture texture, boolean solid){
		this.id = id;
		this.meta = meta;
		this.name = name;
		this.texture = texture;
		this.solid = solid;
		blocks.add(this);
	}
	
	
	/**
	 * @param idString As id:meta
	 * @return Block with given id:meta values
	 */
	public static Block getBlock(String idString){
		float id = Float.parseFloat(idString.split(":")[0]);
		float meta = Float.parseFloat(idString.split(":")[1]);
		for(Block b : blocks){
			if(b.id == id && b.meta == meta) return b;
		}
		
		return air;
	}
	
	/**
	 * Loads a texture.
	 * @param path Path, relative to texturesPath (don't include texturePath)
	 * @return Loaded Texture
	 */
	public static Texture loadTexture(String path){
		try{
			if(path.contains("png")) return TextureLoader.getTexture("png", Block.class.getClassLoader().getResourceAsStream(FileUtilities.texturesPath + path));
			else return TextureLoader.getTexture("jpg", Block.class.getClassLoader().getResourceAsStream(FileUtilities.texturesPath + path));
		}catch(Exception e){new CrashReport(e);}
			return null;
	}
	
	/**
	 * Draws the block with given alpha value.
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 * @param alpha Alpha value
	 */
	public void draw(float x, float y, float z, int alpha){
		glPushAttrib(GL_CURRENT_BIT);
		glColor4f(.1f, .1f, .1f, alpha);
		draw(x, y, z);
		glClear(GL_ALPHA);
		glPopAttrib();
	}
	
	/**
	 * Sets up the drawing of block.
	 * @see #drawBlock(float, float, float)
	 */
	public void drawSetup(){
		glEnable(GL_TEXTURE_2D);
		if(this == Block.air || texture == null) return;
		texture.bind();
	}
	
	/**
	 * Cleans up the block, for after drawing.
	 * @see #drawBlock(float, float, float)
	 */
	public void drawCleanup(){
		glDisable(GL_TEXTURE_2D);
	}
	
	/**
	 * Actually draws the block.
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 * @see #drawBlock(float, float, float)
	 */
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
	
	/**
	 * Calls all of the methods required to draw a full block.
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 */
	public void draw(float x, float y, float z){
		if(texture == null) return;
		drawSetup();
		drawBlock(x, y, z);
		drawCleanup();
	}
	
}
