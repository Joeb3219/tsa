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
		x = (float) ((int) (x));
		y = (float) ((int) y);
		z = (float) ((int) (z));
		
		return getBlock(new Position(x, y, z));
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
		x = (float) ((int) (x));
		z = (float) ((int) (z));
		ArrayList<BlockInstance> inY = new ArrayList<BlockInstance>();
		for(BlockInstance b : blocks){
			if(b.x == x && b.z == z) inY.add(b);
		}
		return inY;
	}
	
	
	//Implementing frustum cullung via http://www.lighthouse3d.com/tutorials/view-frustum-culling
	public void render(){
		lookingAt = getBlockLookingAt();
		//float nearH = (float) (2 * Math.tan(Main.camera.fov / 2) * Main.camera.nearClip);
		//float nearW = nearH * Main.camera.aspectRatio;
	//	float farH = (float) (2 * Math.tan(Main.camera.fov / 2) * Main.camera.farClip);
	//	float farW = farH * Main.camera.aspectRatio;
		renderedBlocks = 0f;
		
		for(BlockInstance b : blocks){
			if(!Main.camera.frustum.BlockInFrustum(b)) continue;
		//	if(Math.abs(Main.player.x - b.x) > 20) continue;
		//	if(Math.abs(b.z - Main.player.z) > 20) continue;
			renderedBlocks ++;
			b.draw();
		}
	}
	
	public void dumpAllBlocks(){
		for(BlockInstance b : blocks){
			System.out.println(b.base.name + " @ " + b.x + "/" + b.y + "/" + b.z);
		}
		
		System.out.println("====TOTAL BLOCKS: " + blocks.size() + "=====");
	}
	
	public BlockInstance getBlockLookingAt(){
		Position player = new Position(-Main.player.x, -Main.player.y + 2, -Main.player.z);
		player.normalizeCoords();
		Vector3f v = new Vector3f((float) -Math.sin(Math.toRadians(360 - Main.camera.ry)), (float) -Math.cos(Math.toRadians(Main.camera.rx - 90)), (float) -Math.cos(Math.toRadians(360 - Main.camera.ry)));
		
		for(float x = 0f; x < 6f; x ++){
			for(float z = 0f; z < 6f; z ++){
				for(float y = 0f; y < 6f; y ++){
					BlockInstance b = getBlock(-(float) (v.getX() * x * 1.0 - player.x), -(float) (1.0 * v.getY() * y - player.y), -(float) (1.0 * v.getZ() * z - player.z));
					if(b.base != Block.air) return b;
				}
			}
		}
		
		return new BlockInstance(Block.air, -(float) (v.getX() * 6.0 - player.x), -(float) (v.getY() * 6.0 - player.y), -(float) (v.getZ() * 6.0 - player.z));
	}
	
	
}
