package com.charredsoftware.tsa.entity;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.World;

/**
 * Entity class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class Entity {
	
	public float x = 2f, y = 1f, z = 2f, mass = 0f; //Mass in kg
	public float startingX, startingY, startingZ = 0f;
	public float currentVelocity = 0f, movingSpeed = 0.7f, beginningJumpingVelocity = 0f, currentJumpingVelocity = 0f;
	public final float defaultStartJumpingVelocity = 1.4f;
	public World world;
	public boolean markedForDeletion = false;
	private Position p = new Position(0, 0, 0);

	/**
	 * Creates a new Entity
	 */
	public Entity(){
		
	}
	
	/**
	 * Creates a new entity
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 */
	public Entity(float x, float y, float z){
		if(!(this instanceof Player)) Main.getInstance().player.world.existingEntities.add(this);
		this.x = x;
		this.y = y;
		this.z = z;
		this.startingX = x;
		this.startingY = y;
		this.startingZ = z;
	}
	
	/**
	 * Generic canMove method.
	 * @param dX X-position
	 * @param dY Y-position
	 * @param dZ Z-position
	 * @return Returns <tt>true</tt> if the entity can move to the indicated position.
	 */
	public boolean canMove(float dX, float dY, float dZ){
		return true;
	}
	
	/**
	 * Generic move method.
	 * Moves entity to given position.
	 * @param dX X-position
	 * @param dY Y-position
	 * @param dZ Z-position
	 */
	public void move(float dX, float dY, float dZ){
		
	}
	
	/**
	 * Generic update method.
	 */
	public void update(){
		
	}
	
	/**
	 * Sets the entity's position.
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 */
	public void setPosition(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * @return Returns the Position.
	 */
	public Position getPosition(){
		p.x = x;
		p.y = y;
		p.z = z;
		return p;
	}
	
	/**
	 * Renders the entity.
	 * Generic render method. 
	 */
	public void render(){
		
	}

}
