package com.charredsoftware.three.world;

import java.util.ArrayList;

public class World {

	public ArrayList<BlockInstance> blocks = new ArrayList<BlockInstance>();
	public int size = 0;
	
	public World(int size){
		this.size = size;
	}
	
	public BlockInstance getBlock(Position p){
		p.normalizeCoords();
		for(BlockInstance b : blocks){
			if(b.x == p.x && b.y == p.y && b.z == p.z) return b;
		}
		return new BlockInstance(Block.air, p.x, p.y, p.z);
	}
	
	public BlockInstance getBlock(float x, float y, float z){
		x = (float) ((int) (x));
		y = (float) ((int) y);
		z = (float) ((int) (z));
		
		if(x % 2 != 0) x --;
		if(y % 2 != 0) y --;
		if(z % 2 != 0) z --;
		
		return getBlock(new Position(x, y, z));
	}
	
	//Gets highest **solid** block that is less than current y.
		public BlockInstance getRelativeHighestSolidBlock(Position p){
			p.normalizeCoords();
			ArrayList<BlockInstance> inY = getBlocksInY(p.x, p.z);
			BlockInstance highest = new BlockInstance(Block.air, 0, 0, 0);
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
		if(x % 2 != 0) x --;
		if(z % 2 != 0) z --;
		
		ArrayList<BlockInstance> inY = getBlocksInY(x, z);
		if(inY.size() == 0) return new BlockInstance(Block.air, 0, 0, 0);
		BlockInstance highest = inY.get(0);
		for(BlockInstance b : inY){
			if(b.y > highest.y) highest = b;
		}
		return highest;
	}
	
	public ArrayList<BlockInstance> getBlocksInY(float x, float z){
		x = (float) ((int) (x));
		z = (float) ((int) (z));
		if(x % 2 != 0) x --;
		if(z % 2 != 0) z --;
		ArrayList<BlockInstance> inY = new ArrayList<BlockInstance>();
		for(BlockInstance b : blocks){
			if(b.x == x && b.z == z) inY.add(b);
		}
		return inY;
	}
	
	public void render(){
		for(BlockInstance b : blocks) b.draw();
	}
	
	public void dumpAllBlocks(){
		for(BlockInstance b : blocks){
			System.out.println(b.base.name + " @ " + b.x + "/" + b.y + "/" + b.z);
		}
		
		System.out.println("====TOTAL BLOCKS: " + blocks.size() + "=====");
	}
	
	
}
