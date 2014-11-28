package com.charredsoftware.three.entity;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.Camera;
import com.charredsoftware.three.Main;
import com.charredsoftware.three.inventory.Hotbar;
import com.charredsoftware.three.inventory.Item;
import com.charredsoftware.three.inventory.ItemGroup;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;

public class Player extends Mob{

	private Camera camera; //Used to calculate motion.
	public boolean isJumping, isCrouching = false;
	private float jumpingTime = 0;
	public Hotbar hotbar;
	
	public Player(Camera camera){
		super();
		this.camera = camera;
		health = 20;
		movingSpeed = 0.15f;
		this.hotbar = new Hotbar(10);
		hotbar.addItem(new ItemGroup(new Item("Test", Block.computer.texture, 32), 1));
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
			currentJumpingVelocity = defaultStartJumpingVelocity;
			beginningJumpingVelocity = defaultStartJumpingVelocity;
			jumpingTime = 0;
		}
		else if(isJumping){
			jumpingTime += .5f / Main.TPS;
			currentJumpingVelocity = beginningJumpingVelocity + (Main.DOWNWARD_ACCELERATION) * jumpingTime; //Calculate final velocity
			checkCanJump(currentJumpingVelocity / 2);
			if(y >  0 || standingOnSolid() && jumpingTime > .5f / Main.TPS){
				y = (float) ((int) y);
				isJumping = false;
			}
		}if(!isJumping && !standingOnSolid()){
			isJumping = true;
			currentJumpingVelocity = 0f;
			beginningJumpingVelocity = currentJumpingVelocity;
			jumpingTime = 0;
		}if(y > 50) health --;
		
		if(getBlockUnder().base == Block.boost){
			isJumping = true;
			currentJumpingVelocity = defaultStartJumpingVelocity * 1.3f;
			beginningJumpingVelocity = currentJumpingVelocity;
			jumpingTime = 0;
		}
		
		if(stuckInBlock()) y -= 1f;
		
		
	}
	
	public void checkCanJump(float dY){
		float fY = y - dY;
		ArrayList<BlockInstance>  blocks = Main.world.getBlocksInRange(-x, -z, -y, -dY);
		if(dY > 0){
			for(int i = 0; i <= blocks.size() - 1; i ++){
				if(blocks.get(i).y > fY && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y = -blocks.get(i).y + 2;
					return;
				}
			}
		}else{
			for(int i = blocks.size() - 1; i >= 0; i --){
				if(blocks.get(i).y < fY && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y = -blocks.get(i).y - 1;
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
			if(isCrouching && !Main.world.getBlock(new Position(-fX, -fY - 1, -fZ)).base.solid) return;
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
		Position p = new Position(-x, -y - 1f, -z);
		return Main.world.getBlock(p);
	}

	public boolean standingOnSolid(){
		return getBlockUnder().base.solid;
	}
	
	public Vector3f getLookingAt(){
		return getLookingAt(Main.camera.farClip);
	}
	
	public Vector3f getLookingAt(float dist){
		double rx = Math.cos(Math.toRadians(Main.camera.rx));
		Vector3f v = new Vector3f((float) -(Math.sin(Math.toRadians(360 - Main.camera.ry)) * dist * rx) - x, (float) -Math.sin(Math.toRadians(Main.camera.rx)) * dist - y, (float) -(Math.cos(Math.toRadians(360 - Main.camera.ry)) * dist * rx) - z);
		v.translate(0, Math.max(0f, (float) (((isCrouching) ? 1f : 2f) * (Math.sin(Math.toRadians(90 - Main.camera.rx)))) -.5f), 0);
		//if(dist == Main.camera.farClip) v.translate(.1f, 0, .1f);
		return v;
	}
	
}
