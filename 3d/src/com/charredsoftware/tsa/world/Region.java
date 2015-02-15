package com.charredsoftware.tsa.world;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.entity.Entity;
import com.charredsoftware.tsa.entity.Mob;
import com.charredsoftware.tsa.entity.MobType;
import com.charredsoftware.tsa.entity.Player;
import com.charredsoftware.tsa.entity.Spinner;
import com.charredsoftware.tsa.entity.Stalker;

/**
 * Region class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since November 9, 2014
 */
public class Region {

	/** _SIZE - {@value} Size of chunk*/
	public static final float _SIZE = 16;
	/** _HEIGHT - {@value} Height of chunk*/
	public static final float _HEIGHT = 64;
	public ArrayList<BlockInstance> blocks = new ArrayList<BlockInstance>();
	public ArrayList<Entity> entitiesToLoad = new ArrayList<Entity>(); //Entities that should be loaded/saved into the world.
	public float x, z;
	public World world;
	
	/**
	 * Creates a region
	 * @param world World to create region in
	 * @param x X-position, divided by _SIZE.
	 * @param z Z-position, divided by _SIZE.
	 */
	public Region(World world, float x, float z){
		this.world = world;
		this.x = x;
		this.z = z;
	}
	
	/**
	 * Saves world.
	 * @param dir Direction to save world to, relative to /res
	 */
	public void save(File dir){
		File file = new File(dir.getAbsolutePath() + "/region_" + x + "l" + z + ".csf");
		try {
			if(!file.exists()) file.createNewFile();
			
			PrintWriter writer = new PrintWriter(file, "UTF-8");

			writer.println("<Region>");
			
			//Settings
			writer.println("<settings>");
			if((((int) world.spawn.x / 16) == x) && (((int) world.spawn.z / 16) == z)){
				writer.println("<setting>");
				writer.println("<key>spawn</key>");
				writer.println("<value>" + world.spawn.x + ":" + world.spawn.y + ":" + world.spawn.z + "</value>");
				writer.println("</setting>");
			}
			writer.println("</settings>");
			
			//Mobs
			writer.println("<mobs>");
			for(Entity e : entitiesToLoad){
				if(!(e instanceof Mob)) continue;
				Mob m = (Mob) e;
				writer.println("<mob>");
				writer.println("<id>" + m.identifier.id + "</id>");
				writer.println("<position>" + m.startingX + ":" + m.startingY + ":" + m.startingZ + "</position>");
				writer.println("<json></json>");
				writer.println("</mob>");
			}
			writer.println("</mobs>");
			
			//Blocks
			writer.println("<blocks>");
			
			for(BlockInstance b : blocks){
				writer.println("<block>");
				writer.println("<position>" + b.x + ":" + b.y + ":" + b.z + "</position>");
				writer.println("<data>" + b.base.id + ":" + b.base.meta + ":" + b.special + "</data>");
				writer.println("<json>" + b.getSpecialJson() + "</json>");
				writer.println("</block>");
			}
			
			writer.println("<block>");
			writer.println("<position>" + x + ":-1000:" + z + "</position>");
			writer.println("<data>0:0:0</data>");
			writer.println("</block>");
			
			writer.println("</blocks>");
			
			writer.println("</Region>");
			
			writer.close();
		} catch (IOException e) {new CrashReport(e);}
		
	}
	
	/**
	 * @return Returns <tt>true</tt> if player is inside of region.
	 */
	public boolean playerInRegion(){
		int px = ((int) Main.getInstance().player.x) / ((int) _SIZE);
		int pz = ((int) Main.getInstance().player.z) / ((int) _SIZE);
		if(x == -px && z == -pz) return true;
		return false;
	}
	
	/**
	 * @return Returns the layer on which the lowest block is.
	 */
	public float getLowestLayer(){
		float baseX = x * _SIZE;
		float baseZ = z * _SIZE;
		for(float i = -1; i < _HEIGHT; i ++){
			for(float x = 0; x < _SIZE; x += 2){
				for(float z = 0; z < _SIZE; z += 2){
					float fX = baseX + x;
					float fZ = baseZ + z;
					if(getBlock(new Position(fX, i, fZ)).base != Block.air) return i;
				}
			}
		}
		return -1f;
	}
	
	/**
	 * @return Returns blocks in region to test against frustum; Limits number of regions to render.
	 */
	public ArrayList<BlockInstance> getFrustumTestBlocks(){
		ArrayList<BlockInstance> testBlocks = new ArrayList<BlockInstance>();
		float baseX = x * _SIZE;
		float baseZ = z * _SIZE;
		for(float x = 0; x < _SIZE; x += 2){
			for(float z = 0; z < _SIZE; z += 2){
				float fX = baseX + x;
				float fZ = baseZ + z;
				testBlocks.add(getBlock(new Position(fX, 0, fZ)));
			}
		}
		return testBlocks;
	}

	/**
	 * @param p Position of the block.
	 * @return Returns the <code>BlockInstance</code> at the indicated position.
	 */
	public BlockInstance getBlock(Position p){
		p.normalizeCoords();
		for(BlockInstance b : blocks){
			if(b.x == p.x && b.y == p.y && b.z == p.z) return b;
		}
		return new BlockInstance(Block.air, p.x, p.y, p.z);
	}
	
	/**
	 * Loads a region from a file.
	 * @param file File to load region from.
	 */
	public void generate(File file){
		try {
			loadSettings(file);
			loadMobs(file);
			loadBlocks(file);
		} catch (Exception e) {
			new CrashReport(e);
		}
	}
	
	/**
	 * Loads any regional/world settings.
	 * @param file File to load from.
	 */
	private void loadSettings(File file) throws Exception{
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		NodeList base = d.getElementsByTagName("setting");
		for(int i = 0; i < base.getLength(); i ++){
			Node n = base.item(i);
			NodeList children = n.getChildNodes();
			String key = "";
			for(int l = 0; l < children.getLength() - 1; l ++){
				Node c = children.item(l);
				String value = c.getTextContent();
				if(value.equals("") || value.equals(" ")) continue;
				if(c.getNodeName().equals("key")){
					key = value;
				}else if(c.getNodeName().equals("value")){
					if(key.equalsIgnoreCase("spawn")){
						String[] values = value.split(":");
						world.spawn = new Position(Float.parseFloat(values[0]), Float.parseFloat(values[1]), Float.parseFloat(values[2]));
					}
				}
			}
			
		}
	}
	
	/**
	 * Loads the mobs.
	 * @param file File to load from.
	 */
	private void loadMobs(File file) throws Exception{
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		NodeList base = d.getElementsByTagName("mob");
		for(int i = 0; i < base.getLength(); i ++){
			Node n = base.item(i);
			NodeList children = n.getChildNodes();
			float bx = 0, by = 0, bz = 0;
			String json = "";
			MobType type = MobType.GENERIC;
			for(int l = 0; l < children.getLength() - 1; l ++){
				Node c = children.item(l);
				String value = c.getTextContent();
				if(value.equals("") || value.equals(" ")) continue;
				if(c.getNodeName().equals("position")){
					bx = Float.parseFloat(value.split(":")[0]);
					by = Float.parseFloat(value.split(":")[1]);
					bz = Float.parseFloat(value.split(":")[2]);
				}else if(c.getNodeName().equals("id")){
					type = MobType.fromString(value);
				}else if(c.getNodeName().equals("json")){
					json = value;
				}
			}
			if(type == MobType.SPINNER) entitiesToLoad.add(new Spinner(bx, by, bz));
			if(type == MobType.STALKER) entitiesToLoad.add(new Stalker(bx, by, bz));
			
		}
	}
	
	/**
	 * Loads the blocks
	 * @param file File to load from
	 */
	private void loadBlocks(File file) throws Exception{
		Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		NodeList base = d.getElementsByTagName("block");
		for(int i = 0; i < base.getLength() - 1; i ++){
			Node n = base.item(i);
			NodeList children = n.getChildNodes();
			float bx = 0, by = 0, bz = 0, bspecial = 0;
			String json = "";
			Block bbase = Block.air;
			for(int l = 0; l < children.getLength() - 1; l ++){
				Node c = children.item(l);
				String value = c.getTextContent();
				if(value.equals("") || value.equals(" ")) continue;
				if(c.getNodeName().equals("position")){
					bx = Float.parseFloat(value.split(":")[0]);
					by = Float.parseFloat(value.split(":")[1]);
					bz = Float.parseFloat(value.split(":")[2]);
				}else if(c.getNodeName().equals("data")){
					bspecial = Float.parseFloat(value.split(":")[2]);
					bbase = Block.getBlock(value.split(":")[0] + ":" + value.split(":")[1]);
				}else if(c.getNodeName().equals("json")){
					json = value;
				}
			}
			BlockInstance b = new BlockInstance(bbase, bx, by, bz);
			if(bbase == Block.chest) b = new Chest(bx, by, bz, json);
			b.special = bspecial;
			b.initJson = json;
			addBlock(b);
			
		}
	}
	
	/**
	 * Generates a new region.
	 */
	public void generate(){
		for(float x = 0; x < 16; x ++){
			for(float z = 0; z < 16; z ++){
				for(float y = 0; y < 3; y ++){
					addBlock(new BlockInstance(Block.grass, this.x * _SIZE + x, y, this.z * _SIZE + z));
				}
			}
		}
	}
	
	/**
	 * @param block <code>BlockInstance</code> to add the region.
	 */
	public void addBlock(BlockInstance block){
		if(block.base == Block.air) return;
		for(int i = 0; i < blocks.size() - 1; i ++){
			BlockInstance b = blocks.get(i);
			if(b.x == block.x && b.y == block.y && b.z == block.z){
				if(b.base != Block.air) return;
			}
		}
		
		blocks.add(block);
	}

	/**
	 * @param block <code>BlockInstance</code> to remove from region,
	 */
	public void removeBlock(BlockInstance block){
		if(blocks.contains(block)){
			blocks.remove(block);
		}
	}
	
	/**
	 * @param pos <code>Position</code> to empty in region,
	 */
	public void removeBlock(Position pos){
		for(int i = 0; i < blocks.size(); i ++){
			BlockInstance b = blocks.get(i);
			if(b.getPosition().equals(pos)){
				blocks.remove(b);
				i ++;
			}
		}
	}

	 /** 
	 * @return Returns an <code>ArrayList</code> of blocks that should be rendered.
	 */
	public ArrayList<BlockInstance> getRenderableBlocks(){
		ArrayList<BlockInstance> renderableBlocks = new ArrayList<BlockInstance>();
		
		if(!playerInRegion() && !Main.getInstance().camera.frustum.regionInFrustum(this)) return renderableBlocks;
		
		for(BlockInstance b : blocks){
			if(!Main.getInstance().camera.frustum.BlockInFrustum(b)) continue;
			renderableBlocks.add(b);
		}
		return renderableBlocks;
	}
	
	/**
	 * @param b <code>BlockInstance</code> to check.
	 * @return Returns <tt>true</tt> if <code>b</code> is visible (not covered).
	 */
	private boolean checkIfCovered(BlockInstance b){
		Player player = Main.getInstance().player;
		if(world.getBlockWithoutNewRegion(new Position(b.x, b.y + 1, b.z)).base == Block.air && player.y + player.height > b.y) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x, b.y - 1, b.z)).base == Block.air && player.y + player.height < b.y) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x + 1, b.y, b.z)).base == Block.air && player.x > b.x) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x - 1, b.y, b.z)).base == Block.air && player.x < b.x) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x, b.y, b.z + 1)).base == Block.air && player.z > b.z) return true;
		if(world.getBlockWithoutNewRegion(new Position(b.x, b.y, b.z - 1)).base == Block.air && player.z < b.z) return true;
		
		return false;
	}
	
	/**
	 * @return Returns the position.
	 */
	public Position getPosition(){
		return new Position(x, 0, z);
	}

	/**
	 * @return Returns a string of the region's position (x, y).
	 */
	public String toString(){
		return "[x/z]: {" + x + "/" + z + "}";
	}
	
}
