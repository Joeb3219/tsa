package com.charredsoftware.three.entity;

import com.charredsoftware.three.world.Position;

public class Entity {

	public float x = -2f, y = -1f, z = -2f;
	public float currentVelocity = 0f, movingSpeed = 0.7f, beginningJumpingVelocity = 0f, currentJumpingVelocity = 0f;
	public final float defaultStartJumpingVelocity = 1.4f;

	public Entity(){
		
	}
	
	public Entity(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean canMove(float dX, float dY, float dZ){
		return true;
	}
	
	public void move(float dX, float dY, float dZ){
		
	}
	
	public void update(){
		
	}
	
	public void setPosition(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Position getPosition(){
		return new Position(x, y, z);
	}
	
}
