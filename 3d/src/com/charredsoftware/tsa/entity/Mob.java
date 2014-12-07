package com.charredsoftware.tsa.entity;

import java.util.ArrayList;
	


import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.world.Block;
import com.charredsoftware.tsa.world.BlockInstance;
import com.charredsoftware.tsa.world.Position;

/**
 * Mob class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class Mob extends Entity{

	public int health = 20;
	public float height = 2f;
	public boolean isJumping, isCrouching = false;
	protected float jumpingTime = 0;
	
	/**
	 * Creates a new Mob.
	 */
	public Mob(){
		super();
	}
	
	/**
	 * Updates the mob. 
	 */
	public void update(){
		if(getBlockUnder().base == Block.boost){
			isJumping = true;
			currentJumpingVelocity = defaultStartJumpingVelocity * 1.3f;
			beginningJumpingVelocity = currentJumpingVelocity;
			jumpingTime = 0;
		}
		checkGettingHurt();
	}
	
	/**
	 * Checks to see if can jump without going through a block.
	 * @param dY Change in Y position.
	 */
	protected void checkCanJump(float dY){
		float fY = y + dY;
		ArrayList<BlockInstance> blocks = world.getBlocksInRange(x, z, y, dY);
		if(dY > 0){
			for(int i = 0; i <= blocks.size() - 1; i ++){
				if(blocks.get(i).y < fY + height && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y = blocks.get(i).y - height;
					Sound.DAMAGE_GROUND.playSfx();
					return;
				}
			}
		}else{
			for(int i = blocks.size() - 1; i >= 0; i --){
				if(blocks.get(i).y > fY && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y =  blocks.get(i).y + 1;
					return;
				}
			}
		}
		y = fY;
	}
	
	/**
	 * Check if the mob is getting damaged.
	 */
	protected void checkGettingHurt() {
		if(y < -50 && ((int) y) % 4 == 0) health --;
		if(stuckInBlock()){
			//At first, attempt to push player away.
			Position startingPosition = getPosition();
			if(canMove(0, 1, 0)) move(0, 1, 0);
			/*if(canMove(1, 0, 0)) move(1, 0, 0);
			else if(canMove(0, 0, 1)) move(0, 0, 1);
			else if(canMove(-1, 0, 0)) move(-1, 0, 0);
			else if(canMove(0, 0, -1)) move(0, 0, -1);*/
			if(startingPosition.equals(getPosition())) health -= 1f; //Didn't move -> start suffocating!
		}
	}
	
	/**
	 * @return Returns <tt>true</tt> if the mob is stuck inside of a block.
	 */
	public boolean stuckInBlock(){
		Position p = new Position(x, y, z);
		p.normalizeCoords();
		return world.getBlock(p).base.solid;
	}
	
	/**
	 * @return Returns <tt>true</tt> if the mob is in water.
	 */
	public boolean isInWater(){
		if(world.getBlock(new Position(x, y, z)).base == Block.water) return true;
		if(world.getBlock(new Position(x, y + 1, z)).base == Block.water) return true;
		return false;
	}
	
	/**
	 * @return Returns the <code>BlockInstance</code> directly under the mob.
	 */
	public BlockInstance getBlockUnder(){
		Position p = new Position(x, y - 1f, z);
		return world.getBlock(p);
	}

	/**
	 * @return Returns <tt>true</tt> if the block under the mob is solid.
	 * @see getBlockUnder()
	 */
	public boolean standingOnSolid(){
		return getBlockUnder().base.solid;
	}
	
}
