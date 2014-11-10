package com.charredsoftware.three.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.Main;

public class World {

	public ArrayList<Region> regions = new ArrayList<Region>();
	public int size = 0;
	public float renderedBlocks = 0f;
	public BlockInstance lookingAt;
	
	public World(int size){
		this.size = size;
	}
	
	public void generate(){
		for(int x = 0; x < size; x ++){
			for(int z = 0; z < size; z ++){
				findRegion(x * Region._SIZE, z * Region._SIZE);//.generate();
			}
		}
		
		System.out.println(regions.size() + " regions generated:");
		for(Region r : regions){
			System.out.println("Region @: " + r.toString());
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
		
		x = (float) ((int) x);
		z = (float) ((int) z);
	
		for(Region r : regions){
			if(x == r.x && z == r.z) return r;
		}
		
		Region r = new Region(x, z);
		regions.add(r);
		r.generate();
		
		System.out.println("Total regions: " + regions.size());
		
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
			looking.translate(0, 2f, 0);
			BlockInstance b = getBlock(new Position((float) (looking.getX() - (Main.player.x)), (float) (looking.getY() - Main.player.y), (float) (looking.getZ() - (Main.player.z))));
			
			if(b.base != Block.air) return b;
		}
		
		Vector3f looking = Main.player.getLookingAt(6);
		return new BlockInstance(Block.air, (float) (looking.getX() - (Main.player.x)), (float) (looking.getY() - Main.player.y), (float) -(looking.getZ() - (Main.player.z)));
	}
	
	public ArrayList<BlockInstance> getBlocksInRegion(float x, float z){
		return findRegion(x, z).blocks;
	}
	
	
}
