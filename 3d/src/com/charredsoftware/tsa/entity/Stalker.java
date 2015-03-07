package com.charredsoftware.tsa.entity;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.io.File;
import java.util.Random;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.gui.TextPopup;
import com.charredsoftware.tsa.obj.Loader;
import com.charredsoftware.tsa.obj.Model;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.World;

/**
 * A Stalker mob!
 * This mob randomly walks around a 5(?) block radius from starting point.
 * If it sees the player, it will follow the player around.
 * Shoots dem arrows.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since January 1, 2015
 */

public class Stalker extends Mob{
	
	public static final float _DISTANCE_TO_STALK = 5f, _FOV_TO_STALK = 90f, _CHAIN_TO_STARTING_POINT = 5f, _DISTANCE_TO_SHOOT = 2.5f;
	private Random r = new Random();
	public Position startingPoint;
	public boolean followingPlayer = false;
	public float speed = 2f; //in m/s
	public static Model model;
	
	public Stalker(World world, float startingX, float startingY, float startingZ){
		super(world);
		if(model == null) model = Loader.load(new File(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.texturesPath + "stalker.obj"));
		startingPoint = new Position(startingX, startingY, startingZ);
		this.x = startingX;
		this.y = startingY;
		this.z = startingZ;
		this.startingX = x;
		this.startingY = y;
		this.startingZ = z;
		identifier = MobType.STALKER;
		height = 2f;
		shielding = 0.5f;
		killBonus = 10f;
	}
	
	/**
	 * Detects if an arrow has hit the mob, and damages the mob if so.
	 * @return Returns <tt>true</tt> if an arrow has hit the mob.
	 */
	public boolean arrowHit(Arrow a){
		boolean hit = super.arrowHit(a);
		if(!(a.shooter instanceof Player)) hit = false; //If hit by another mob, no damage.
		if(hit){
			damageMob(a.calculateDamage(this));
			if(Math.abs(facing - (a.rX + 90)) > _FOV_TO_STALK)
			facing = (a.rX + 90) - 10;
		}
		if(hit && Main.getInstance().controller.removeMobMode) world.removeMobFromWorld(this);
		return hit;
	}
	
	public void update(){
		if(Main.getInstance().controller.buildingMode) return;
		if(health <= 0){
			if(ticksSinceDeath == 0){
				Main.getInstance().player.mobsKilled ++;
				Main.getInstance().player.score += killBonus;
				Main.getInstance().HUDText.popups.add(new TextPopup("Killed a Stalker and received " + killBonus + " points!"));
			}
			if(ticksSinceDeath > _TICKS_AFTER_DEATH_TILL_DELETION) markedForDeletion = true;
			else ticksSinceDeath ++;
			return;
		}
		Position originalPosition = getPosition().clone();
		followingPlayer = determineIfFollowingPlayer();
		if(followingPlayer){
			if(getPosition().calculateDistance(Main.getInstance().player.getPosition()) <= _DISTANCE_TO_SHOOT){
				if(r.nextInt(100) <= 5 * Main.getInstance().controller.difficulty){
					Arrow a = new Arrow(this, world, new Position(x, y + 1, z), 5, facing - 270 , 0);
					a.shouldBeLit = false;
					Sound.BOW_SHOT.playSfx();
				}
			}else attemptMovement();
		}else{
			jump(false);
			boolean changedDirection = false;
			if(r.nextInt(100) == 3 || (startingPoint.calculateDistance(getPosition()) + 0.5f >= _CHAIN_TO_STARTING_POINT && r.nextInt(25) == 1)){
				changedDirection = true;
				
				if(r.nextInt(2) == 0) facing += 30;
				else facing -= 30;
				
				if(facing >= 360f) facing = 0f;
			}
			if(!changedDirection || startingPoint.calculateDistance(getPosition()) + 0.5f >= _CHAIN_TO_STARTING_POINT){
				if(r.nextInt(100) == 50){
					Position beginMovement = new Position(x, y, z);
					move((float) Math.cos(Math.toRadians(facing)), 0, (float) Math.sin(Math.toRadians(facing)));
					if(startingPoint.calculateDistance(beginMovement) - 0.5f > _CHAIN_TO_STARTING_POINT){
						//Moved to far!
						x = beginMovement.x;
						y = beginMovement.y;
						z = beginMovement.z;
					}
				}
			}
		}
		Position finalPosition = getPosition();
		if(!originalPosition.equals(finalPosition)){
			calculateFacingDirection(originalPosition);
			facing += 180;
			facing = correctAngle(facing);
		}
	}
	
	/**
	 * Attempts to move the mob in some direction.
	 */
	private void attemptMovement(){
		float stepSize = 0.1f;
		Position dest = Main.getInstance().player.getPosition();
		Position current = getPosition();
		
		Position closest = current;
		for(float x = current.x - stepSize; x <= current.x + stepSize; x += stepSize){
			for(float z = current.z - stepSize; z <= current.z + stepSize; z += stepSize){
				Position testingPos = new Position(x, y, z);
				if(testingPos.calculateDistance(dest) < closest.calculateDistance(dest)) closest = testingPos;
			}
		}

		if(dest.y > current.y) jump(true);
		else jump(false);
		
		this.x = closest.x;
		this.z = closest.z;
		
		
	}
	
	public void move(float dX, float dY, float dZ){
		float fX = dX + x;
		float fY = dY + y;
		float fZ = dZ + z;

		if(!world.getBlock(new Position(fX, fY, fZ)).base.solid){
			if(world.getBlock(new Position(fX, fY + height / 2, fZ)).base.solid) return; //Hit yer head!
			x = fX;
			z = fZ;
			y = fY;
		}
	}
	
	public boolean determineIfFollowingPlayer(){
		if((Main.getInstance().player.getPosition().calculateDistance(getPosition()) > _DISTANCE_TO_STALK)) return false;
		
		float angle = getRelativeAngle(_FOV_TO_STALK);
		angle = correctAngle(angle);

		if(angle <= _FOV_TO_STALK) return true;
		
		return false;
	}
	
	public void render(){
		glPushMatrix();

		glTranslatef(x, y, z);
		glRotatef(90 - facing, 0, 1, 0);
		glRotatef(270, 1, 0, 0);
		if(ticksSinceDeath > 0){
			glRotatef(90, 1, 0, 0);
			glRotatef(facing, 0, 0, 1);
		}
		
		model.render();
		
		glPopMatrix();
	}

}
