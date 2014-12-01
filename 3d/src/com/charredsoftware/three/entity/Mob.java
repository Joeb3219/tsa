package com.charredsoftware.three.entity;

import java.util.ArrayList;

import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;

public class Mob extends Entity{

	public int health = 20;
	public float height = 2f;
	public boolean isJumping, isCrouching = false;
	protected float jumpingTime = 0;
	
	public Mob(){
		super();
	}
	
	public void update(){
		if(getBlockUnder().base == Block.boost){
			isJumping = true;
			currentJumpingVelocity = defaultStartJumpingVelocity * 1.3f;
			beginningJumpingVelocity = currentJumpingVelocity;
			jumpingTime = 0;
		}
		checkGettingHurt();
	}
	
	protected void checkCanJump(float dY){
		float fY = y + dY;
		ArrayList<BlockInstance>  blocks = world.getBlocksInRange(x, z, y, dY);
		if(dY > 0){
			for(int i = 0; i <= blocks.size() - 1; i ++){
				if(blocks.get(i).y < fY + height && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y = blocks.get(i).y - height;
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
	
	protected void checkGettingHurt() {
		if(y > 50 && ((int) y) % 4 == 0) health --;
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
	
	public boolean stuckInBlock(){
		Position p = new Position(x, y, z);
		p.normalizeCoords();
		return world.getBlock(p).base.solid;
	}
	
	public boolean isInWater(){
		if(world.getBlock(new Position(x, y, z)).base == Block.water) return true;
		if(world.getBlock(new Position(x, y + 1, z)).base == Block.water) return true;
		return false;
	}
	
	public BlockInstance getBlockUnder(){
		Position p = new Position(x, y - 1f, z);
		return world.getBlock(p);
	}

	public boolean standingOnSolid(){
		return getBlockUnder().base.solid;
	}
	
}
