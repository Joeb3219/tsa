package com.charredsoftware.tsa.world;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.entity.Entity;
import com.charredsoftware.tsa.entity.Mob;
import com.charredsoftware.tsa.util.FileUtilities;

/**
 * World class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */
public class World {

	public ArrayList<Region> regions = new ArrayList<Region>();
	public float renderedBlocks = 0f;
	private Random r = new Random();
	public BlockInstance lookingAt = new BlockInstance(Block.air, 0, -10000, 0);
	public Position lookingAtExtended = new Position(0, -10000, 0), behindEyes = new Position(0, -10000, 0);
	public int id;
	public File dir;
	public ArrayList<Entity> existingEntities = new ArrayList<Entity>();
	public Position spawn = new Position(0, 0, 0);
	public boolean worldUpdated = true;
	public ArrayList<Integer> bufferIds = new ArrayList<Integer>();
	public ArrayList<Integer> textureIds = new ArrayList<Integer>();
	
	/**
	 * Generates a new world with the next highest ID.
	 */
	public World(){
		this.id = getNextId();
		this.dir = new File(FileUtilities.getBaseDirectory() + FileUtilities.savesPath + id);
		generate();
	}
	
	/**
	 * Generates a new world.
	 * @param id ID to generate world with.
	 */
	public World(int id){
		this.id = id;
		this.dir = new File(FileUtilities.getBaseDirectory() + FileUtilities.savesPath + id);
		generate();
	}
	
	/**
	 * @return Returns the next highest ID.
	 */
	private int getNextId(){
		int highest = -1;
		File directory = new File(FileUtilities.savesPath);
		for(String s : directory.list()){
			if(new File(directory, s).isDirectory()){
				highest = Math.max(Integer.parseInt(s), highest);
			}
		}
		highest ++;
		return highest;
	}
	
	/**
	 * Saves the world.
	 */
	public void save(){
		for(Region r : regions) r.save(dir);
	}
	
	/**
	 * Generates a world.
	 */
	public void generate(){
		if(!dir.exists()) generateWorldFolders();
		else if(dir.list().length > 0){
			load(dir);
			return;
		}

		for(int x = -2; x < 2; x ++){
			for(int z = -2; z < 2; z ++){
				findRegion(x, z).save(dir);
			}
		}
	}

	/**
	 * Makes all of the directories needed to make a new world.
	 */
	private void generateWorldFolders(){
		dir.mkdir();
	}
	
	/**
	 * Loads a new world.
	 * @param dir Directory containing the world.
	 */
	private void load(File dir){
		String[]entries = dir.list();
		for(String s : entries){
			if(new File(dir.getAbsolutePath(), s).isDirectory()) continue;
			String coordinates = s.split("_")[1];
			if(coordinates.split("l").length != 2) continue;
			Region r = new Region(this, Float.parseFloat(coordinates.split("l")[0]), Float.parseFloat(coordinates.split("l")[1].split(".csf")[0]));
			regions.add(r);
			r.generate(new File(dir.getAbsolutePath(), s));
		}
		
		for(Region r : regions) existingEntities.addAll(r.entitiesToLoad);
	}
	
	/**
	 * Does exactly what getBlock does, but won't create a new region if one doesn't exist.
	 * Useful for when doing occulsion culling.
	 * @param p Position to check.
	 * @return Returns <code>BlockInstance</code> at given position, or air if no region exists.
	 */
	public BlockInstance getBlockWithoutNewRegion(Position p){
		p.normalizeCoords();
		for(Region r : regions){
			if(p.x == r.x && p.z == r.z){
				return r.getBlock(p);
			}
		}
		return new BlockInstance(Block.air, p.x, p.y, p.z);
	}
	
	/**
	 * @param p Position to check
	 * @return Returns <code>BlockInstance</code> at given position.
	 */
	public BlockInstance getBlock(Position p){
		p.normalizeCoords();
		return findRegion(p.x, p.z).getBlock(p);
	}
	
	/**
	 * @param fx X-position.
	 * @param fy Y-position.
	 * @param fz Z-position.
	 * @return Returns an <code>ArrayList</code> of <code>BlockInstance</code> that surround the indication position.
	 */
	public ArrayList<BlockInstance> getSurroundingBlocks(float fx, float fy, float fz){
		ArrayList<BlockInstance> surrounding = new ArrayList<BlockInstance>();
		for(int x = -1; x < 1; x ++){
			for(int z = -1; z < 1; z ++){
				for(int y = -1; y < 1; y ++){
					BlockInstance block = getBlock(fx + x, fy + y, fz + z);
					if(block.base != Block.air) surrounding.add(block);
				}
			}
		}
		return surrounding;
	}
	
	/**
	 * @param b BlockInstance to check/
	 * @return Returns an <code>ArrayList</code> of <code>BlockInstance</code> that surround the indication block.
	 * @see #getSurroundingBlocks(float, float, float)
	 */
	public ArrayList<BlockInstance> getSurroundingBlocks(BlockInstance b){
		return getSurroundingBlocks(b.x, b.y, b.z);
	}
	
	/**
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 * @return Returns the block at the indicated position.
	 */
	public BlockInstance getBlock(float x, float y, float z){
		x = (float) ((int) (x));
		y = (float) ((int) (y));
		z = (float) ((int) (z));
		
		return getBlock(new Position(x, y, z));
	}
	
	/**
	 * @param block BlockInstance to add to the world.
	 */
	public void addBlock(BlockInstance block){
		findRegion(block.x, block.z).addBlock(block);
		worldUpdated = true;
	}
	
	/**
	 * @param block BlockInstance to remove from the world.
	 */
	public void removeBlock(BlockInstance block){
		findRegion(block.x, block.z).removeBlock(block);
		worldUpdated = true;
	}
	
	/**
	 * @param pos Position to empty.
	 */
	public void removeBlock(Position pos){
		findRegion(pos.x, pos.z).removeBlock(pos);
		worldUpdated = true;
	}
	
	/**
	 * @param x X-position, not divided by Region._SIZE.
	 * @param z Z-position, not divided by Region._SIZE.
	 * @return Returns the region, or creates a new one, at indicated position.
	 */
	public Region findRegion(float x, float z){
		x /= Region._SIZE;
		z /= Region._SIZE;
		
		x = (float) (Math.floor(x));
		z = (float) (Math.floor(z));
	
		for(Region r : regions){
			if(x == r.x && z == r.z) return r;
		}
		
		if(regions.size() >= 4){
			if(!Main.getInstance().controller.buildingMode) return regions.get(0);
		}
		
		worldUpdated = true;
		Region r = new Region(this, x, z);
		regions.add(r);
		r.generate();
		
		return r;
	}
	
	/**
	 * @param b Block to find the region of.
	 * @return Returns the region at the block exists in.
	 */
	public Region findRegion(BlockInstance b){
		return findRegion(b.x, b.z);
	}
	
	/**
	 * @param x X-position
	 * @param z Z-position
	 * @param yCurrent Y-position
	 * @param dY Change in Y position to look up/down to.
	 * @return Returns an <code>ArrayList</code> of <code>BlockInstance</code> that are within indicated bounds.
	 */
	public ArrayList<BlockInstance> getBlocksInRange(float x, float z, float yCurrent, float dY){
		ArrayList<BlockInstance> inRange = getBlocksInY(x, z);
		ArrayList<BlockInstance> inRangeVertically = new ArrayList<BlockInstance>();
		for(BlockInstance b : inRange){
			if(dY > 0 && b.y > yCurrent && b.y < yCurrent + dY) inRangeVertically.add(b);
			else if(dY < 0 && b.y < yCurrent && b.y > yCurrent + dY) inRangeVertically.add(b);
		}
		return inRangeVertically;
	}
	
	/**
	 * @param p Position
	 * @return Returns the closest solid block above the indicated position.
	 */
	public BlockInstance getClosestSolidRoofBlock(Position p){
		p.normalizeCoords();
		ArrayList<BlockInstance> inY = getBlocksInY(p.x, p.z);
		BlockInstance lowest = new BlockInstance(Block.air, 0, 1000, 0);
		for(BlockInstance b : inY){
			if(b.base.solid && b.y < lowest.y && b.y > p.y) lowest = b;
		}
		return lowest;
	}
	
	/**
	 * Gets highest **solid** block that is less than current y.
	 * @param p Position
	 * @return Returns the highest **solid** block that is less than current y.
	 */
		public BlockInstance getRelativeHighestSolidBlock(Position p){
			p.normalizeCoords();
			ArrayList<BlockInstance> inY = getBlocksInY(p.x, p.z);
			BlockInstance highest = new BlockInstance(Block.air, 0, -4, 0);
			for(BlockInstance b : inY){
				if(b.base.solid && b.y > highest.y && b.y < p.y) highest = b;
			}
			return highest;
		}
	
	/**
	 * Gets highest block that is less than current y.
	 * @param p Position
	 * @return Returns the highest block that is less than current y.
	 */
	public BlockInstance getNextHighestBlock(Position p){
		p.normalizeCoords();
		ArrayList<BlockInstance> inY = getBlocksInY(p.x, p.z);
		BlockInstance highest = new BlockInstance(Block.air, 0, 0, 0);
		for(BlockInstance b : inY){
			if(b.y > highest.y && b.y < p.y) highest = b;
		}
		return highest;
	}
	
	/**
	 * @param x X-position
	 * @param z Z-position
	 * @return Returns the highest block.
	 */
	public BlockInstance getHighestBlock(float x, float z){
		x = (float) ((int) (x));
		z = (float) ((int) (z));
		
		ArrayList<BlockInstance> inY = getBlocksInY(x, z);
		if(inY.size() == 0) return new BlockInstance(Block.air, 0, 0, 0);
		BlockInstance highest = inY.get(0);
		for(BlockInstance b : inY){
			if(b.y > highest.y) highest = b;
		}
		return highest;
	}
	
	/**
	 * @param x X-position
	 * @param z Z-position
	 * @return Returns all blocks in given X, Z column.
	 */
	public ArrayList<BlockInstance> getBlocksInY(float x, float z){
		Position p = new Position(x, 0, z);
		p.normalizeCoords();
		x = p.x;
		z = p.z;
		ArrayList<BlockInstance> inY = new ArrayList<BlockInstance>();
		for(BlockInstance b : findRegion(p.x, p.z).blocks){
			if(b.x == x && b.z == z) inY.add(b);
		}
		return inY;
	}
	
	public float regionsRendered = 0f;
	/**
	 * Renders the world.
	 * Wraps, calls renderMap(blockList).
	 * @see #renderMap(Map)
	 */
	public void render(){
		renderedBlocks = 0f;
		regionsRendered = 0f;
		//Creates map of textures & blocks that have those textures -> renders all similar textures at once.
		Map<Texture, ArrayList<BlockInstance>> blockList = new HashMap<Texture, ArrayList<BlockInstance>>();
		
		for(Region r : regions){
			ArrayList<BlockInstance> renderable = r.getRenderableBlocks();
			renderedBlocks += renderable.size();
			if(renderable.size() != 0) regionsRendered ++;
			//Add each renderable block to the map
			for(BlockInstance b : renderable){
				if(blockList.containsKey(b.base.texture)) blockList.get(b.base.texture).add(b);
				else blockList.put(b.base.texture, new ArrayList<BlockInstance>(Arrays.asList(b)));
			}
		}
		
		renderMap(blockList);
		
		lookingAt = getBlockLookingAt();
	}
	
	/**
	 * Renders all blocks w/ similar textures at the same time.
	 * Reduces OpenGL calls.
	 * @param blockList <code>Map<Texture, ArrayList<BlockInstance>></code> to render.
	 */
	private void renderMap(Map<Texture, ArrayList<BlockInstance>> blockList){
		//TODO: Make glass, water, etc. rendered last -> see through
		//TODO: Implement VBOs for rendering (instead of glVertex calls (immediate mode))
		
		
		for(Entry<Texture, ArrayList<BlockInstance>> e : blockList.entrySet()){
			if(e.getKey() == Block.glass.texture || e.getKey() == Block.water.texture) continue;
			renderBlocks(e.getValue());
		}
		
		if(blockList.containsKey(Block.glass.texture)) renderBlocks(blockList.get(Block.glass.texture));
		if(blockList.containsKey(Block.water.texture)) renderBlocks(blockList.get(Block.water.texture));
		
		worldUpdated = false;
	}

	/**
	 * Renders a list of blocks
	 * @param list ArrayList of BlockInstance
	 */
	private void renderBlocks(ArrayList<BlockInstance> list){
		list.get(0).base.drawSetup();
		
		for(BlockInstance b : list){
			if(Main.getInstance().controller.buildingMode && b == lookingAt) b.draw(100);
			else b.draw();
		}
		
		list.get(0).base.drawCleanup();
	}
	
	/**
	 * Generates VBOs for the world.
	 */
	private void generateVBOs(Map<Texture, ArrayList<BlockInstance>> blockList){
		for(Integer i : bufferIds) glDeleteBuffers(i);
		for(Integer i : textureIds) glDeleteBuffers(i);
		bufferIds = new ArrayList<Integer>();
		textureIds = new ArrayList<Integer>();
		for(Entry<Texture, ArrayList<BlockInstance>> e : blockList.entrySet()){
			ArrayList<BlockInstance> list = e.getValue();
			list.get(0).base.generateRenderBuffers();
			if(list.get(0).base == Block.air || list.get(0).base.texture == null) continue;
			
			int currentBuffer = glGenBuffers();
			bufferIds.add(currentBuffer);
			int currentTextureBuffer = glGenBuffers();
			textureIds.add(currentTextureBuffer);
			FloatBuffer masterBuffer = BufferUtils.createFloatBuffer(list.get(0).base.vertexData.limit() * list.size());
			FloatBuffer masterTextureBuffer = BufferUtils.createFloatBuffer(list.get(0).base.texData.limit() * list.size());
			
			for(BlockInstance b : list){
				int term = 0;
				for(int index = 0; index < b.base.vertexData.limit(); index ++){
					masterBuffer.put(b.base.vertexData.get(index) + ((term == 0) ? b.x : (term == 1) ? b.y : b.z));
					term ++;
					if(term > 2) term = 0;
				}
				for(int index = 0; index < b.base.texData.limit(); index ++){
					masterTextureBuffer.put(b.base.texData.get(index) + ((term == 0) ? b.x : (term == 1) ? b.y : b.z));
					term ++;
					if(term > 2) term = 0;
				}
			}
			
			glBindBuffer(GL_ARRAY_BUFFER, currentBuffer);
			glBufferData(GL_ARRAY_BUFFER, masterBuffer, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
			glBindBuffer(GL_ARRAY_BUFFER, currentTextureBuffer);
			glBufferData(GL_ARRAY_BUFFER, masterTextureBuffer, GL_STATIC_DRAW);
			glBindBuffer(GL_ARRAY_BUFFER, 0);
		}
	}
	
	/**
	 * @return Returns the block that the player is looking at.
	 */
	public BlockInstance getBlockLookingAt(){
		float stepSize = 0.2f;
		Vector3f behindEyesVector = Main.getInstance().player.getLookingAt((float) (-stepSize * Math.cos(Math.toRadians(Main.getInstance().camera.rx))));
		if(getBlock(new Position(behindEyesVector.x, behindEyesVector.y, behindEyesVector.z)).base.solid){
			behindEyesVector = Main.getInstance().player.getLookingAt((float) (stepSize * Math.cos(Math.toRadians(Main.getInstance().camera.rx))));
			if(getBlock(new Position(behindEyesVector.x, behindEyesVector.y, behindEyesVector.z)).base.solid){
				behindEyesVector = Main.getInstance().player.getLookingAt(0f);
				if(getBlock(new Position(behindEyesVector.x, behindEyesVector.y, behindEyesVector.z)).base.solid) behindEyesVector = Main.getInstance().player.getLookingAt(1f);
			}
		}
		for(float i = 0; i < 6; i += stepSize){
			Vector3f looking = Main.getInstance().player.getLookingAt(i);
			BlockInstance b = getBlock(new Position((float) (looking.getX()), (float) (looking.getY()), (float) (looking.getZ())));
			
			if(b.base != Block.air){
				if(b.base == Block.water && !Main.getInstance().controller.buildingMode) continue;
				Vector3f nextStep = Main.getInstance().player.getLookingAt(i + stepSize);
				this.lookingAtExtended = new Position((float) (nextStep.getX()), (float) (nextStep.getY()), (float) (nextStep.getZ()));
				this.behindEyes = new Position(behindEyesVector.getX(), behindEyesVector.getY(), behindEyesVector.getZ());
				return b;
			}
		}
		
		Vector3f looking = Main.getInstance().player.getLookingAt(6);
		Vector3f lookingExtended = Main.getInstance().player.getLookingAt(6 + stepSize);
		this.lookingAtExtended = new Position(lookingExtended.getX(), lookingExtended.getY(), lookingExtended.getZ());
		this.behindEyes = new Position(behindEyesVector.getX(), behindEyesVector.getY(), behindEyesVector.getZ());
		return new BlockInstance(Block.air, (float) (looking.getX()), (float) (looking.getY()), (float) (looking.getZ()));
	}
	
	/**
	 * @return Returns the block directly in front of the block the player is looking at.
	 */
	public BlockInstance getBlockAdjectLookingAt(){
		for(float i = 0; i < 6; i += 0.2f){
			Vector3f looking = Main.getInstance().player.getLookingAt(i);
			BlockInstance b = getBlock(new Position((float) (looking.getX()), (float) (looking.getY()), (float) (looking.getZ())));
			
			if(b == lookingAt){
				looking = Main.getInstance().player.getLookingAt(i - 0.25f);
				return getBlock(new Position((float) (looking.getX()), (float) (looking.getY()), (float) (looking.getZ())));
			}
		}
		
		Vector3f looking = Main.getInstance().player.getLookingAt(6);
		return new BlockInstance(Block.air, (float) (looking.getX()), (float) (looking.getY()), (float) (looking.getZ()));
	}
	
	/**
	 * @param x X-position
	 * @param z Z-position
	 * @return Returns the blocks in the given region.
	 */
	public ArrayList<BlockInstance> getBlocksInRegion(float x, float z){
		return findRegion(x, z).blocks;
	}
	
	/**
	 * Adds a mob to the world.
	 * Used to ensure that mobs get added to level, and can be killed later without affecting world loading/saving.
	 * @param m The mob to add.
	 */
	public void addMob(Mob m){
		findRegion(m.x, m.z).entitiesToLoad.add(m);
		existingEntities.add(m);
	}
	
	/**
	 * @param p Position at which to look
	 * @return Returns the <code>Mob</code> at the specified location, or null if there isn't one.
	 */
	public Mob getMob(Position p){
		for(Entity m : existingEntities){
			if(!(m instanceof Mob)) continue;
			if(m.getPosition().equals(p)) return (Mob) m;
		}
		return null;
	}
	
	/**
	 * Removes a mob completely from world... won't show up next time.
	 * @param m Mob to remove
	 */
	public void removeMobFromWorld(Mob m){
		findRegion(m.startingX, m.startingZ).entitiesToLoad.remove(m);
		existingEntities.remove(m);
	}
	
	/**
	 * @return Returns a nearby position on the same y layer that has no other blocks in it, excluding base position. Returns base if no empty spaces.
	 * @param base Position to consider relative to. Will not return this position.
	 * @param searchRange maximum range to search from the base.
	 */
	public Position getNearbyEmptyBlock(Position base, int searchRange){
		int x = (r.nextInt(searchRange * 2 + 1)) - (searchRange + 1);
		int z = (r.nextInt(searchRange * 2 + 1)) - (searchRange + 1);
		
		if(getBlock(base.x + x, base.y, base.z + z).base != Block.air) return getNearbyEmptyBlock(base, searchRange);
		return new Position(base.x + x, base.y, base.z + z);
	}
}
