package com.charredsoftware.three.world;

import java.util.ArrayList;
import java.util.Random;

import com.charredsoftware.three.Main;

public class Region {

	public static final float _SIZE = 16, _HEIGHT = 128;
	public ArrayList<BlockInstance> blocks = new ArrayList<BlockInstance>();
	public float x, z;
	public float renderedBlocks = 0f; //Used to calculate # of blocks rendered per render.
	
	public Region(float x, float z){
		this.x = x;
		this.z = z;
	}

	public void generate(){
		if(x == 0 && z == 0){
			for(float x = 0 / 2; x < _SIZE - 1; x ++ ){
				for(float y = 0; y < 24; y ++){
					addBlock(new BlockInstance(Block.bricks, x, y, _SIZE - 1));
					addBlock(new BlockInstance(Block.bricks, x, y, 0));
				}
			}
			
			for(float z = 0 / 2; z < _SIZE - 1; z ++){
				for(float y = 0; y < 24; y ++){
					addBlock(new BlockInstance(Block.bricks, _SIZE - 1, y, z));
					addBlock(new BlockInstance(Block.bricks, 0, y, z));
				}
			}
			
			for(float x = 0 / 2; x < _SIZE - 1; x ++){
				for(float z = 0; z < _SIZE - 1; z ++){
					addBlock(new BlockInstance(Block.grass, x, -1.0f, z));
					if(!(x == 0 && z == 0)) addBlock(new BlockInstance(Block.ceiling, x, 24, z));
				}
			}
			
			for(float x = 6; x < 12; x ++){
				for(float z = 6; z < 12; z ++){
					for(float y = 0; y < 3; y ++){
						addBlock(new BlockInstance(Block.bricks, x, y, z));
					}
				}
			}
			
			addBlock(new BlockInstance(Block.boost, 10, 3, 10));
			
			addBlock(new BlockInstance(Block.wood, 4, 0, 6));
			addBlock(new BlockInstance(Block.wood, 4, 1, 8));
			addBlock(new BlockInstance(Block.wood, 4, 2, 10));
			addBlock(new BlockInstance(Block.wood, 4, 0, 8));
			addBlock(new BlockInstance(Block.wood, 4, 0, 10));
			addBlock(new BlockInstance(Block.wood, 4, 1, 10));
			
			addBlock(new BlockInstance(Block.computer, 8, 3, 8));
			
			return;
		
		}
		
		Random r = new Random();
		for(float y = -2f; y <= -1f; y ++){
			for(float x = 0; x < _SIZE; x ++){
				for(float z = 0; z < _SIZE; z ++){
					float fX = this.x * _SIZE + x;
					float fZ = this.z * _SIZE + z;
					if(y == -2) addBlock(new BlockInstance(Block.grass, fX, y, fZ));
					else{
						if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.grass, fX, y, fZ));
						else if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.bricks, fX, y, fZ));
						else if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.boost, fX, y, fZ));
						else if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.ceiling, fX, y, fZ));
						else if(r.nextInt(2) == 1) addBlock(new BlockInstance(Block.wood, fX, y, fZ));
						//else if(r.nextInt(5) == 1) world.addBlock(new BlockInstance(Block.grass, x, y, z));
						else{} //Air
					}
				}
			}
		}
	}
	
	public void addBlock(BlockInstance block){
		for(int i = 0; i < blocks.size() - 1; i ++){
			BlockInstance b = blocks.get(i);
			if(b.x == block.x && b.y == block.y && b.z == block.z){
				if(b.base != Block.air) return;
			}
		}
		
		blocks.add(block);
	}
	
	public void removeBlock(BlockInstance block){
		if(blocks.contains(block)) blocks.remove(block);
	}
	
	public void render(){
		renderedBlocks = 0f;
		
		for(BlockInstance b : blocks){
			if(!Main.camera.frustum.BlockInFrustum(b)) continue;
			renderedBlocks ++;
			if(b == Main.world.lookingAt) b.draw(100);
			else b.draw();
		}
	}
	
	public String toString(){
		return "[x/z]: {" + x + "/" + z + "}";
	}
	
}
