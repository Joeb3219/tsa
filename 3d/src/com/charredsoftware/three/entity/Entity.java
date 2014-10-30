package com.charredsoftware.three.entity;

public class Entity {

	public float x, y, z = 0;
	public float currentVelocity = 0f, movingSpeed = 0.7f, jumpingVelocityStart = 1.75f, currentJumpingVelocity = 0f;

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
	
}
