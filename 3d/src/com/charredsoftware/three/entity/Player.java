package com.charredsoftware.three.entity;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.charredsoftware.three.Camera;
import com.charredsoftware.three.Main;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;

public class Player extends Mob{

	private Camera camera; //Used to calculate motion.
	public boolean isJumping, isCrouching = false;
	private float jumpingTime = 0;
	
	public Player(Camera camera){
		super();
		this.camera = camera;
		health = 20;
		movingSpeed = 0.35f;
	}
	
	public void update(){
		super.update();
		
		float speedModifier = (isCrouching) ? 4f : 1f;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			move((float) (movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.ry + 90))), 0,(float) (movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.ry + 90))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			move((float) (-movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.ry + 90))), 0, (float) (-movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.ry + 90))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			move((float) (-movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.ry))), 0, (float) (-movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.ry))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			move((float) (movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.ry))), 0, (float) (movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.ry))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) isCrouching = true;
		else isCrouching = false;
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !isJumping && standingOnSolid()){
			isJumping = true;
			currentJumpingVelocity = (isCrouching) ? jumpingVelocityStart / 2f : jumpingVelocityStart;
			jumpingTime = 0;
		}
		else if(isJumping){
			jumpingTime += 0.1f; //Tenth of a second.
			currentJumpingVelocity = ( (isCrouching) ? jumpingVelocityStart / 1.25f : jumpingVelocityStart) + Main.DOWNWARD_ACCELERATION * jumpingTime; //Calculate final velocity
			checkCanJump(currentJumpingVelocity);
			//if(currentJumpingVelocity <= 0 && (y - currentJumpingVelocity / 4) > Main.world.getRelativeHighestSolidBlock(new Position(-x, (-y - currentJumpingVelocity / 4), -z)).y) currentJumpingVelocity /= 8;
			//if(currentJumpingVelocity >= 0 && (y - currentJumpingVelocity / 4) > Main.world.getClosestSolidRoofBlock(new Position(-x, (-y - (currentJumpingVelocity / 4) + 1), -z)).y) currentJumpingVelocity /= 8;
			//y -= (currentJumpingVelocity);
			if(y >  0 || standingOnSolid()){
				y = (float) ((int) y);
				isJumping = false;
			}
		}if(!isJumping && !standingOnSolid()){
			y += 0.5f;
			if(y % 1 != 0) y = (float) ((int) y - 0.5);
		}if(y > 50) health --;
		
		if(getBlockUnder().base == Block.boost){
			y -= 5f;
			isJumping = true;
			currentJumpingVelocity = -30f;
			jumpingTime = 0;
		}
		
		if(stuckInBlock()) y -= 0.5f;
		
		
	}
	
	public void checkCanJump(float dY){
		float fY = y - dY;
		ArrayList<BlockInstance>  blocks = Main.world.getBlocksInRange(-x, -z, -y, -dY);
		if(dY > 0){
			for(int i = 0; i <= blocks.size() - 1; i ++){
				System.out.println("CHECKING ABOVE! " + blocks.get(i).y);
				if(blocks.get(i).y > fY && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y = -blocks.get(i).y + 2;
					System.out.println("Hit head!! on " + blocks.get(i).base.name);
					return;
				}
			}
		}else{
			for(int i = blocks.size() - 1; i >= 0; i --){
				System.out.println("CHECKING BELOW! " + blocks.get(i).y);
				if(blocks.get(i).y < fY && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y = -blocks.get(i).y - 1;
					System.out.println("Broken feet! on " + blocks.get(i).base.name);
					return;
				}
			}
		}
		y = fY;
	}
	
	public void move(float dX, float dY, float dZ){
		float fX = dX + x;
		float fY = dY + y;
		float fZ = dZ + z;

		if(!Main.world.getBlock(new Position(-fX, -fY, -fZ)).base.solid){
			x = fX;
			z = fZ;
			y = fY;
		}
		
	}

	public boolean stuckInBlock(){
		Position p = new Position(-x, -y, -z);
		p.normalizeCoords();
		return Main.world.getBlock(p).base.solid;
	}
	
	public boolean isInWater(){
		if(Main.world.getBlock(new Position(-x, -y, -z)).base == Block.water) return true;
		if(Main.world.getBlock(new Position(-x, -y + 1, -z)).base == Block.water) return true;
		return false;
	}
	
	public BlockInstance getBlockUnder(){
		Position p = new Position(-x, -y - 1, -z);
		p.normalizeCoords();
		BlockInstance b = Main.world.getBlock(p);
		
		return b;
	}

	public BlockInstance getBlockLookingAt(){
		
		
		return new BlockInstance(Block.air, -x, -2, -z);
	}
	
	public boolean standingOnSolid(){
		return getBlockUnder().base.solid;
	}
	
}
