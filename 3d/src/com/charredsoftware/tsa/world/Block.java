package com.charredsoftware.tsa.world;

import static org.lwjgl.opengl.GL11.GL_ALPHA;
import static org.lwjgl.opengl.GL11.GL_CURRENT_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDisableClientState;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexCoordPointer;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertexPointer;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
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
	public static Block end = new Block(7, 0, "Wall", Block.loadTexture("wall.jpg"));
	public static Block chest = new Block(8, 0, "Chest", Block.loadTexture("chest.jpg"));
	public static Block charredBlock = new Block(-2, 0, "charredblock", Block.loadTexture("charredsoftware.png"));
	public static int _VERTICES = 6 * 4, _VERTEX_SIZE = 3, _TEXTURE_SIZE = 2;
	public static int vboHandler = -1, textHandler = -1;
	public static FloatBuffer vertexData, texData;
	
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
	 * Generates Block VBOs.
	 */
	public void generateRenderBuffers(){
		if(vertexData != null && texData != null) return;
		float leftBound = -0.5f;
		float rightBound = 0.5f;
		
		vertexData = BufferUtils.createFloatBuffer(_VERTICES * _VERTEX_SIZE);
		vertexData.put(new float[]{leftBound,leftBound,rightBound, leftBound,rightBound,rightBound, rightBound,rightBound,rightBound, rightBound,leftBound,rightBound,
				leftBound,leftBound,leftBound, leftBound,rightBound,leftBound, rightBound,rightBound,leftBound, rightBound,leftBound,leftBound,
				leftBound,leftBound,leftBound, leftBound,leftBound,rightBound, leftBound,rightBound,rightBound, leftBound,rightBound,leftBound,
				rightBound,leftBound,leftBound, rightBound,leftBound,rightBound, rightBound,rightBound,rightBound, rightBound,rightBound,leftBound,
				leftBound,leftBound,leftBound, rightBound,leftBound,leftBound, rightBound,leftBound,rightBound, leftBound,leftBound,rightBound,
				leftBound,rightBound,leftBound, rightBound,rightBound,leftBound, rightBound,rightBound,rightBound, leftBound,rightBound,rightBound});
		vertexData.flip();
		
		texData = BufferUtils.createFloatBuffer(_TEXTURE_SIZE * _VERTICES);
		texData.put(new float[]{0, 2/4f, 0, 1/4f, 1/4f, 1/4f, 1/4f, 2/4f,
				2/4f, 2/4f, 2/4f, 1/4f, 1/4f, 1/4f, 1/4f, 2/4f,
				2/4f, 2/4f, 3/4f, 2/4f, 3/4f, 1/4f, 2/4f, 1/4f,
				4/4f, 2/4f, 3/4f, 2/4f, 3/4f, 1/4f, 4/4f, 1/4f,
				0/4f, 3/4f, 1/4f, 3/4f, 1/4f, 2/4f, 0/4f, 2/4f,
				0/4f, 0/4f, 1/4f, 0/4f, 1/4f, 1/4f, 0/4f, 1/4f});
		texData.flip();
		
		vboHandler = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboHandler);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		textHandler = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, textHandler);
		glBufferData(GL_ARRAY_BUFFER, texData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
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
		if(vboHandler == -1 || textHandler == -1) generateRenderBuffers();
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
		glBindBuffer(GL_ARRAY_BUFFER, vboHandler);
		glVertexPointer(_VERTEX_SIZE, GL_FLOAT, 0, 0L);
			
		glBindBuffer(GL_ARRAY_BUFFER, textHandler);
		glTexCoordPointer(_TEXTURE_SIZE, GL_FLOAT, 0, 0L);
			
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		
	}
	
	/**
	 * Cleans up the block, for after drawing.
	 * @see #drawBlock(float, float, float)
	 */
	public void drawCleanup(){
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
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
		
		glDrawArrays(GL_QUADS, 0, _VERTICES);
		
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
		if(vboHandler == -1 || textHandler == -1) generateRenderBuffers();
		drawSetup();
		drawBlock(x, y, z);
		drawCleanup();
	}
	
}
