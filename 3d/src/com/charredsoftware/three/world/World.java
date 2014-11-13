package com.charredsoftware.three.world;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.Main;
import com.charredsoftware.three.computer.Peripheral;

public class World {

	public ArrayList<Region> regions = new ArrayList<Region>();
	public float renderedBlocks = 0f;
	public BlockInstance lookingAt;
	public int id;
	public File dir;
	
	public World(){
		this.id = getNextId();
		this.dir = new File("res/saves/" + id);
	}
	
	public World(int id){
		this.id = id;
		this.dir = new File("res/saves/" + id);
	}
	
	private int getNextId(){
		int highest = 0;
		
		//Should later actually get the next id.
		
		
		
		return highest;
	}
	
	public void save(){
		for(Region r : regions) r.save(dir);
	}
	
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
		
	private void generateWorldFolders(){
		dir.mkdir();
		new File(dir, "data/computers").mkdirs();
	}
	
	private void load(File dir){
		String[]entries = dir.list();
		for(String s : entries){
			if(new File(dir.getAbsolutePath(), s).isDirectory()) continue;
			String coordinates = s.split("_")[1];
			if(coordinates.split("l").length != 2) continue;
			Region r = new Region(Float.parseFloat(coordinates.split("l")[0]), Float.parseFloat(coordinates.split("l")[1].split(".csf")[0]));
			regions.add(r);
			r.generate(new File(dir.getAbsolutePath(), s));
			
		}
	}
	
	public BlockInstance getBlock(Position p){
		p.normalizeCoords();
		for(BlockInstance b : findRegion(p.x, p.z).blocks){
			if(b.x == p.x && b.y == p.y && b.z == p.z) return b;
		}
		return new BlockInstance(Block.air, p.x, p.y, p.z);
	}
	
	public BlockInstance getBlock(float x, float y, float z){
		x = (float) ((int) (x));
		y = (float) ((int) (y));
		z = (float) ((int) (z));
		
		return getBlock(new Position(x, y, z));
	}
	
	public void addBlock(BlockInstance block){
		findRegion(block.x, block.z).addBlock(block);
	}
	
	public void removeBlock(BlockInstance block){
		findRegion(block.x, block.z).removeBlock(block);
	}
	
	public Region findRegion(float x, float z){
		x /= Region._SIZE;
		z /= Region._SIZE;
		
		x = (float) (Math.floor(x));
		z = (float) (Math.floor(z));
	
		for(Region r : regions){
			if(x == r.x && z == r.z) return r;
		}
		
		Region r = new Region(x, z);
		regions.add(r);
		r.generate();
		
		return r;
	}
	
	public Region findRegion(BlockInstance b){
		return findRegion(b.x, b.z);
	}
	
	public ArrayList<BlockInstance> getBlocksInRange(float x, float z, float yCurrent, float dY){
		ArrayList<BlockInstance> inRange = getBlocksInY(x, z);
		ArrayList<BlockInstance> inRangeVertically = new ArrayList<BlockInstance>();
		for(BlockInstance b : inRange){
			if(dY > 0 && b.y < yCurrent && b.y > yCurrent - dY) inRangeVertically.add(b);
			else if(dY < 0 && b.y > yCurrent && b.y < yCurrent - dY) inRangeVertically.add(b);
		}
		return inRangeVertically;
	}
	
	public BlockInstance getClosestSolidRoofBlock(Position p){
		p.normalizeCoords();
		ArrayList<BlockInstance> inY = getBlocksInY(p.x, p.z);
		BlockInstance lowest = new BlockInstance(Block.air, 0, 1000, 0);
		for(BlockInstance b : inY){
			if(b.base.solid && b.y < lowest.y && b.y > p.y) lowest = b;
		}
		return lowest;
	}
	
	//Gets highest **solid** block that is less than current y.
		public BlockInstance getRelativeHighestSolidBlock(Position p){
			p.normalizeCoords();
			ArrayList<BlockInstance> inY = getBlocksInY(p.x, p.z);
			BlockInstance highest = new BlockInstance(Block.air, 0, -4, 0);
			for(BlockInstance b : inY){
				if(b.base.solid && b.y > highest.y && b.y < p.y) highest = b;
			}
			return highest;
		}
	
	//Gets highest block that is less than current y.
	public BlockInstance getNextHighestBlock(Position p){
		p.normalizeCoords();
		ArrayList<BlockInstance> inY = getBlocksInY(p.x, p.z);
		BlockInstance highest = new BlockInstance(Block.air, 0, 0, 0);
		for(BlockInstance b : inY){
			if(b.y > highest.y && b.y < p.y) highest = b;
		}
		return highest;
	}
	
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
	
	
	public void render(){
		renderedBlocks = 0f;
		for(Region r : regions){
			r.render();
			renderedBlocks += r.renderedBlocks;
		}
		lookingAt = getBlockLookingAt();
	}
	
	public BlockInstance getBlockLookingAt(){
		for(float i = -1; i < 6; i += 0.25f){
			Vector3f looking = Main.player.getLookingAt(i);
			looking.translate(0f, 2f, 0f);
			BlockInstance b = getBlock(new Position((float) (looking.getX()), (float) (looking.getY()), (float) (looking.getZ())));
			
			if(b.base != Block.air) return b;
		}
		
		Vector3f looking = Main.player.getLookingAt(6);
		looking.translate(0, 2f, 0);
		return new BlockInstance(Block.air, (float) (looking.getX()), (float) (looking.getY()), (float) -(looking.getZ()));
	}
	
	public ArrayList<BlockInstance> getBlocksInRegion(float x, float z){
		return findRegion(x, z).blocks;
	}
	
	public Peripheral getPeripheral(float x, float y, float z){
		for(Peripheral p : findRegion(x, z).peripherals){
			if(p.x == x && p.y == y && p.z == z) return p;
		}
		
		return null;
	}
	
}
