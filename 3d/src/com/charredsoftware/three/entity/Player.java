package com.charredsoftware.three.entity;

import org.lwjgl.input.Keyboard;

import com.charredsoftware.three.Camera;
import com.charredsoftware.three.Main;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;
import com.charredsoftware.three.world.World;

public class Player extends Mob{

	private Camera camera; //Used to calculate motion.
	private boolean isJumping = false;
	private float jumpingTime = 0;
	
	public Player(Camera camera){
		super();
		this.camera = camera;
		health = 20;
		movingSpeed = 0.4f;
	}
	
	public void update(){
		super.update();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			move((float) (movingSpeed * Math.cos(Math.toRadians(camera.ry + 90))), 0,(float) (movingSpeed * Math.sin(Math.toRadians(camera.ry + 90))));
		};
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			move((float) (-movingSpeed * Math.cos(Math.toRadians(camera.ry + 90))), 0, (float) (-movingSpeed * Math.sin(Math.toRadians(camera.ry + 90))));
		};
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			move((float) (-movingSpeed * Math.cos(Math.toRadians(camera.ry))), 0, (float) (-movingSpeed * Math.sin(Math.toRadians(camera.ry))));
		};
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			move((float) (movingSpeed * Math.cos(Math.toRadians(camera.ry))), 0, (float) (movingSpeed * Math.sin(Math.toRadians(camera.ry))));
		};
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !isJumping && standingOnSolid()){
			isJumping = true;
			currentJumpingVelocity = jumpingVelocityStart;
			jumpingTime = 0;
		}
		else if(isJumping){
			jumpingTime += 0.1f; //Tenth of a second.
			currentJumpingVelocity = jumpingVelocityStart + Main.DOWNWARD_ACCELERATION * jumpingTime; //Calculate final velocity
			if((y - currentJumpingVelocity / 2) > Main.world.getNextHighestBlock(new Position(-x, -(y - currentJumpingVelocity / 2), -z)).y) currentJumpingVelocity /= 2;
			y -= (currentJumpingVelocity);
			if(y >  0 || standingOnSolid()){
				y = (float) ((int) y);
				isJumping = false;
			}
		}if(!isJumping && !standingOnSolid()){
			y += 0.5f;
			if(standingOnSolid() && y % 2 != 0) y += 1f;
		}if(y > 50) health --;
		
		if(getBlockUnder().base == Block.boost){
			y -= 20f;
			isJumping = true;
			currentJumpingVelocity = 8f;
		}
		
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

	public boolean isInWater(){
		if(Main.world.getBlock(new Position(-x, -y, -z)).base == Block.water) return true;
		if(Main.world.getBlock(new Position(-x, -y + 2, -z)).base == Block.water) return true;
		return false;
	}
	
	public BlockInstance getBlockUnder(){
		Position p = new Position(-x, -y - 2, -z);
		p.normalizeCoords();
		BlockInstance b = Main.world.getBlock(p);
		
		return b;
	}
	
	public boolean standingOnSolid(){
		return getBlockUnder().base.solid;
	}
	
}
