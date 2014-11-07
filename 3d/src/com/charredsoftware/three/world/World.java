package com.charredsoftware.three.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.Main;

public class World {

	public ArrayList<BlockInstance> blocks = new ArrayList<BlockInstance>();
	public int size = 0;
	public float renderedBlocks = 0f;
	public BlockInstance lookingAt;
	
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
		x = (float) (Math.floor(x));
		y = (float) (Math.floor(y));
		z = (float) (Math.floor(z));
		
		return getBlock(new Position(x, y, z));
	}
	
	public void addBlock(BlockInstance block){
		for(int i = 0; i < blocks.size() - 1; i ++){
			BlockInstance b = blocks.get(i);
			if(b.x == block.x && b.y == block.y && b.z == block.z){
				if(b.base != Block.air) return;
			}
		}

			System.out.println(block.base.name + " added @ " + block.x + ", " + block.y + ", " + block.z);
			blocks.add(block);
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
		for(BlockInstance b : blocks){
			if(b.x == x && b.z == z) inY.add(b);
		}
		return inY;
	}
	
	
	public void render(){
		renderedBlocks = 0f;
		
		for(BlockInstance b : blocks){
			if(!Main.camera.frustum.BlockInFrustum(b)) continue;
			renderedBlocks ++;
			if(b == lookingAt) b.draw(100);
			else b.draw();
		}
		lookingAt = getBlockLookingAt();
	}
	
	public void dumpAllBlocks(){
		for(BlockInstance b : blocks){
			System.out.println(b.base.name + " @ " + b.x + "/" + b.y + "/" + b.z);
		}
		
		System.out.println("====TOTAL BLOCKS: " + blocks.size() + "=====");
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
	
	public ArrayList<BlockInstance> getBlocksInChunk(float x, float z){
		return blocks;
	}
	
	
}
