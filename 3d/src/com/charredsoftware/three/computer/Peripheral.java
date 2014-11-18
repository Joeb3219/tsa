package com.charredsoftware.three.computer;

import com.charredsoftware.three.world.Position;

public class Peripheral {

	public float x, y, z;
	public float special;
	
	public Peripheral(float x, float y, float z, float special){
		this.x = x;
		this.y = y;
		this.z = z;
		this.special = special;
		this.special = generateSpecialId();
	}
	
	public float generateSpecialId(){
		if(special != -1) return special;
		return -1;
	}
	
	public void update(){
		
	}
	
	public void draw(){
		
	}
	
	public Position getPosition(){
		return new Position(x, y, z);
	}
	
}
