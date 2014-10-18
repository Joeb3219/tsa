package com.charredsoftware.three.world;

public class BlockInstance {

	public Block base;
	public float x, y, z = 0;
	
	public BlockInstance(Block base, float x, float y, float z){
		this.base = base;
		Position p = new Position(x, y, z);
		p.normalizeCoords();
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	public Position getPosition(){
		return new Position(x, y, z);
	}
	
	public void draw(){
		base.draw(x, y, z);
	}
	
}
