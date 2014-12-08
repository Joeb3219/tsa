package com.charredsoftware.tsa.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.tsa.Camera;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.physics.Physics;
import com.charredsoftware.tsa.world.Block;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.World;

/**
 * Player class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class Player extends Mob{

	private Camera camera; //Used to calculate motion.
	public Block selectedBlock = Block.air;
	public boolean walking = false;
	public Bow bow = new Bow();
	
	/**
	 * Creates anew Player
	 * @param world World that the player resides in
	 * @param camera Camera that will follow the player.
	 */
	public Player(World world, Camera camera){
		super();
		this.world = world;
		this.camera = camera;
		health = 100;
		movingSpeed = 0.15f;
		mass = 75f;
	}
	
	/**
	 * Updates player's movement.
	 * @see com.charredsoftware.tsa.entity.Mob#update()
	 */
	public void update(){
		if(!Main.getInstance().controller.buildingMode) bow.update();
		
		checkMovement((isCrouching) ? 4f : 1f);
		
		checkJumping();
		
		super.update();
	}

	/**
	 * Handles all jumping code.
	 */
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
				Sound.HIT_GROUND.playSfx();
				if(potentialDamage > 0) Sound.DAMAGE_GROUND.playSfx();
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

	/**
	 * Moves the player if indicated keyboard/mouse events occur.
	 * @param speedModifier SpeedModifier, such that it is slower to move and crouch.
	 */
	private void checkMovement(float speedModifier) {
		walking = false;
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			move((float) (-movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.rx + 90))), 0, (float) (-movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.rx + 90))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			move((float) (movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.rx + 90))), 0,(float) (movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.rx + 90))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			move((float) (movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.rx))), 0, (float) (movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.rx))));
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			move((float) (-movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.rx))), 0, (float) (-movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.rx))));
		}

		if(isJumping) walking = false;
		if(walking && !Sound.WALKING.audio.isPlaying()) Sound.WALKING.playSfx();
		if(!walking && Sound.WALKING.audio.isPlaying()) Sound.WALKING.audio.stop();
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) isCrouching = true;
		else isCrouching = false;
	}
	
	/**
	 * Actually moves the player if a block isn't in the way.
	 */
	public void move(float dX, float dY, float dZ){
		float fX = dX + x;
		float fY = dY + y;
		float fZ = dZ + z;

		if(!world.getBlock(new Position(fX, fY, fZ)).base.solid){
			if(isCrouching && !world.getBlock(new Position(fX, fY - 1, fZ)).base.solid) return; //Falling while crouching -> stop movement.
			if(world.getBlock(new Position(fX, fY + height / 2, fZ)).base.solid) return; //Hit yer head!
			walking = true;
			x = fX;
			z = fZ;
			y = fY;
		}
		
	}
	
	/**
	 * Spawns the player at the highest possible Y value.
	 * @param x X-position
	 * @param z Z-position
	 */
	public void spawn(float x, float z){
		setPosition(x, world.getHighestBlock(x, z).y + 2, z);
	}

	/**
	 * @return Returns a <code>Vector3f</code> that the player is looking at, at the furthest distance.
	 */
	public Vector3f getLookingAt(){
		return getLookingAt(Main.getInstance().camera.farClip);
	}
	
	/**
	 * 
	 * @param dist Distance that the player is looking at.
	 * @return Returns a <code>Vector3f</code> that the player is looking at, at dist.
	 */
	public Vector3f getLookingAt(float dist){
		double ry = Math.cos(Math.toRadians(Main.getInstance().camera.ry));
		Vector3f v = new Vector3f(x - (float) (Math.sin(Math.toRadians(360 - Main.getInstance().camera.rx)) * dist * ry), y - (float) Math.sin(Math.toRadians(Main.getInstance().camera.ry)) * dist, z - (float) (Math.cos(Math.toRadians(360 - Main.getInstance().camera.rx)) * dist * ry));
		v.translate(0, Math.max(0f, (float) (((isCrouching) ? height / 2f : height) * (Math.sin(Math.toRadians(90 - Main.getInstance().camera.ry)))) -.5f), 0);
		if(dist == Main.getInstance().camera.farClip) v.translate(.1f, 0, .1f);
		return v;
	}
	
}
