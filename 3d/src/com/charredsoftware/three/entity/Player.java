package com.charredsoftware.three.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.Camera;
import com.charredsoftware.three.Main;
import com.charredsoftware.three.computer.Peripheral;
import com.charredsoftware.three.inventory.Hotbar;
import com.charredsoftware.three.inventory.Item;
import com.charredsoftware.three.inventory.ItemGroup;
import com.charredsoftware.three.physics.Physics;
import com.charredsoftware.three.world.Block;
import com.charredsoftware.three.world.Position;
import com.charredsoftware.three.world.World;

public class Player extends Mob{

	private Camera camera; //Used to calculate motion.
	public Hotbar hotbar;
	public Block selectedBlock;
	public Peripheral selectedPeripheral = null;
	
	public Player(World world, Camera camera){
		super();
		this.world = world;
		this.camera = camera;
		health = 100;
		movingSpeed = 0.15f;
		mass = 75f;
		this.hotbar = new Hotbar(10);
		hotbar.addItem(new ItemGroup(new Item("Test", Block.computer.texture, 32), 1));
	}
	
	public void update(){
		checkMovement((isCrouching) ? 4f : 1f);
		
		checkJumping();
		
		super.update();
	}

	private void checkJumping() {
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) && !isJumping && standingOnSolid()){
			isJumping = true;
			currentJumpingVelocity = defaultStartJumpingVelocity;
			beginningJumpingVelocity = defaultStartJumpingVelocity;
			jumpingTime = 0;
		}
		else if(isJumping){
			jumpingTime += .5f / Main.DESIRED_TPS;
			currentJumpingVelocity = Physics.calculateFinalVelocity(beginningJumpingVelocity, Physics.DOWNWARD_ACCELERATION, jumpingTime);
			float potentialDamage = Physics.calculateDamage(currentJumpingVelocity / 2);
			checkCanJump(currentJumpingVelocity / 2);
			if(standingOnSolid() && jumpingTime > .5f / Main.DESIRED_TPS){
				health -= potentialDamage;
				y = (float) ((int) y);
				isJumping = false;
			}
		}if(!isJumping && !standingOnSolid()){
			isJumping = true;
			currentJumpingVelocity = 0;
			beginningJumpingVelocity = currentJumpingVelocity;
			jumpingTime = 0;
		}
	}

	private void checkMovement(float speedModifier) {
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			move((float) (-movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.ry + 90))), 0, (float) (-movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.ry + 90))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			move((float) (movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.ry + 90))), 0,(float) (movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.ry + 90))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			move((float) (movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.ry))), 0, (float) (movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.ry))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			move((float) (-movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.ry))), 0, (float) (-movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.ry))));
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) isCrouching = true;
		else isCrouching = false;
	}
	
	public void move(float dX, float dY, float dZ){
		float fX = dX + x;
		float fY = dY + y;
		float fZ = dZ + z;

		if(!world.getBlock(new Position(fX, fY, fZ)).base.solid){
			if(isCrouching && !world.getBlock(new Position(fX, fY - 1, fZ)).base.solid) return; //Falling while crouching -> stop movement.
			if(world.getBlock(new Position(fX, fY + height / 2, fZ)).base.solid) return; //Hit yer head!
			x = fX;
			z = fZ;
			y = fY;
		}
		
	}

	public Vector3f getLookingAt(){
		return getLookingAt(Main.getInstance().camera.farClip);
	}
	
	public Vector3f getLookingAt(float dist){
		double rx = Math.cos(Math.toRadians(Main.getInstance().camera.rx));
		Vector3f v = new Vector3f(x - (float) (Math.sin(Math.toRadians(360 - Main.getInstance().camera.ry)) * dist * rx), y - (float) Math.sin(Math.toRadians(Main.getInstance().camera.rx)) * dist, z - (float) (Math.cos(Math.toRadians(360 - Main.getInstance().camera.ry)) * dist * rx));
		v.translate(0, Math.max(0f, (float) (((isCrouching) ? height / 2f : height) * (Math.sin(Math.toRadians(90 - Main.getInstance().camera.rx)))) -.5f), 0);
		if(dist == Main.getInstance().camera.farClip) v.translate(.1f, 0, .1f);
		return v;
	}
	
}
