package com.charredsoftware.tsa.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.tsa.Camera;
import com.charredsoftware.tsa.GameState;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.gui.Dialog;
import com.charredsoftware.tsa.gui.DialogAuthor;
import com.charredsoftware.tsa.gui.Puzzle;
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
	public Block selectedBlock = Block.bricks;
	public Position leftWand = new Position(0, 0, 0), rightWand = new Position(0, 0, 0);
	public boolean walking = false;
	public float score = 0f;
	public int mobsKilled = 0, chestsFound = 0;
	public int coins = 0;
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
		health = 20;
		movingSpeed = 0.15f;
		mass = 75f;
	}
	
	/**
	 * Updates player's movement.
	 * @see com.charredsoftware.tsa.entity.Mob#update()
	 */
	public void update(){
		bow.update();
		
		checkMovement((isCrouching || bow.drawBackTime > 0) ? 4f : 1f);
		
		checkJumping();
		
		if(getBlockUnder().base == Block.end){
			Main.getInstance().puzzleMenu = new Puzzle();
			Main.getInstance().gameState = GameState.PUZZLE_INTRO;
			if(world.id == 0) Main.getInstance().HUDDialog.dialogs.add(new Dialog(DialogAuthor.PERSON, "You've made it to the first keypad!@A sequence of digits will flash. When it stops, you must enter that combination!@If you enter the wrong code, it will flash again.@Don't waste time!"));
		}
		
		super.update();
	}

	/**
	 * Heals the player.
	 * @param amt Amount to heal by
	 */
	public void heal(int amt){
		health += amt;
		if(health > 20) health = 20;
	}
	
	/**
	 * Handles all jumping code.
	 */
	private void checkJumping() {
		if(Keyboard.isKeyDown(Main.getInstance().controller.control_jump) && !isJumping && standingOnSolid()){
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
				if(!Main.getInstance().controller.buildingMode) health -= potentialDamage;
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
		if(Keyboard.isKeyDown(Main.getInstance().controller.control_forward)){
			move((float) (-movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.rx + 90))), 0, (float) (-movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.rx + 90))));
		}
		if(Keyboard.isKeyDown(Main.getInstance().controller.control_backward)){
			move((float) (movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.rx + 90))), 0,(float) (movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.rx + 90))));
		}
		if(Keyboard.isKeyDown(Main.getInstance().controller.control_strafe_right)){
			move((float) (movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.rx))), 0, (float) (movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.rx))));
		}
		if(Keyboard.isKeyDown(Main.getInstance().controller.control_strafe_left)){
			move((float) (-movingSpeed / speedModifier * Math.cos(Math.toRadians(camera.rx))), 0, (float) (-movingSpeed / speedModifier * Math.sin(Math.toRadians(camera.rx))));
		}

		if(isJumping) walking = false;
		if(walking && !Sound.WALKING.audio.isPlaying()) Sound.WALKING.playSfx((isCrouching || bow.drawBackTime > 0) ? 0.75f : 1f);
		if(!walking && Sound.WALKING.audio.isPlaying()) Sound.WALKING.audio.stop();
		
		if(Keyboard.isKeyDown(Main.getInstance().controller.control_crouch)) isCrouching = true;
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
	 */
	public void spawn(){
		setPosition(world.spawn.x, world.spawn.y, world.spawn.z);
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
	
	/**
	 * @param damage Amount of damage to deal to player.
	 */
	public void damageMob(int damage){
		super.damageMob(damage);
	}
	
	/**
	 * @return Returns <tt>true</tt> if the arrow hit the player.
	 */
	public boolean arrowHit(Arrow a){
		boolean hit = super.arrowHit(a);
		if(hit) damageMob(1);
		return hit;
	}
	
	/**
	 * Resets the player
	 */
	public void reset(){
		this.health = 20;
		this.bow.arrows = this.bow.maxArrows;
		this.coins = 0;
		this.score = 0;
		Main.getInstance().controller.timeLeft = Main.DESIRED_TPS * (60 * 15);
		spawn();
	}
	
}
