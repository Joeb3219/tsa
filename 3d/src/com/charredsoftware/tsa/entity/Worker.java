package com.charredsoftware.tsa.entity;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.io.File;
import java.util.Random;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.gui.TextPopup;
import com.charredsoftware.tsa.obj.Loader;
import com.charredsoftware.tsa.obj.Model;
import com.charredsoftware.tsa.physics.Physics;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;

/**
 * A Worker mob!
 * This mob paces back and forth.
 * If it sees the player, it will call a Henchman!
 * Shoots dem arrows.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since February 1, 2015
 */
public class Worker extends Mob{

	public Position pos2, startingPoint;
	private Random r = new Random();
	public static final float _FOV_TO_CALL = 30, _DISTANCE_TO_CALL = 3.5f;
	public int henchmenCalled = 0, ticksSinceLastCall = 0;
	public static final int _MAX_HECNHMEN_CAN_CALL = 4, _TICKS_BETWEEN_CALLS = Main.DESIRED_TPS * 2;
	public boolean walkingTowardsPos2 = true;
	public static Model model;
	
	/**
	 * Creates a new Worker Mob
	 * @param x X-Position
	 * @param y Y-Position
	 * @param z Z-Position
	 */
	public Worker(float x, float y, float z, Position pos2){
		super();
		this.pos2 = pos2;
		this.pos2.normalizeCoords();
		if(model == null) model = Loader.load(new File(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.texturesPath + "worker.obj"));
		identifier = MobType.WORKER;
		killBonus = 2f;
		startingPoint = new Position(x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
		this.startingX = x;
		this.startingY = y;
		this.startingZ = z;
		this.height = 2f;
		this.shielding = 0.0f;
		this.mass = 50f;
	}
	
	/**
	 * Updates the mob.
	 */
	public void update(){
		if(Main.getInstance().controller.buildingMode) return;
		if(facing <= 0) facing = 360;
		if(facing > 360) facing = 0;
		if(health <= 0){
			if(ticksSinceDeath == 0){
				Main.getInstance().player.mobsKilled ++;
				Main.getInstance().player.score += killBonus;
				Main.getInstance().HUDText.popups.add(new TextPopup("Killed a Spinner and received " + killBonus + " points!"));
			}
			if(ticksSinceDeath > _TICKS_AFTER_DEATH_TILL_DELETION) markedForDeletion = true;
			else ticksSinceDeath ++;
			return;
		}
		
		callHenchman();
		
		attemptMovement();
	}
	
	/**
	 * Calls a henchman if the player is within the required view.
	 */
	private void callHenchman(){
		if(ticksSinceLastCall > 0 || henchmenCalled > _MAX_HECNHMEN_CAN_CALL) return;
		if(getPosition().calculateDistance(Main.getInstance().player.getPosition()) > _DISTANCE_TO_CALL) return; //Too far away
		if(getRelativeAngle() > _FOV_TO_CALL) return; //Not within angle
		Main.getInstance().player.world.existingEntities.add(new Henchman(Main.getInstance().player.world.getNearbyEmptyBlock(getPosition(), 2)));
		ticksSinceLastCall = _TICKS_BETWEEN_CALLS;
		henchmenCalled ++;
	}
	
	/**
	 * Attempts to move the mob in some direction.
	 */
	private void attemptMovement(){
		float stepSize = 0.125f / 4;
		Position dest = (walkingTowardsPos2) ? pos2 : startingPoint;
		Position current = getPosition();
		
		Position closest = current;
		for(float x = current.x - stepSize; x <= current.x + stepSize; x += stepSize){
			for(float z = current.z - stepSize; z <= current.z + stepSize; z += stepSize){
				Position testingPos = new Position(x, y, z);
				if(testingPos.calculateDistance(dest) < closest.calculateDistance(dest)) closest = testingPos;
			}
		}

		if(closest.calculateDistance(dest) <= 0.125f){
			walkingTowardsPos2 = !walkingTowardsPos2;
			if(facing == 360) facing = 180;
			else facing = 360;
		}
		
		this.x = closest.x;
		this.z = closest.z;
	}
	
	/**
	 * Detects if an arrow has hit the mob, and damages the mob if so.
	 * @return Returns <tt>true</tt> if an arrow has hit the mob.
	 */
	public boolean arrowHit(Arrow a){
		boolean hit = super.arrowHit(a);
		if(!(a.shooter instanceof Player)) hit = false; //If hit by another mob, no damage.
		if(hit) damageMob(this.health);
		if(hit && Main.getInstance().controller.removeMobMode) Main.getInstance().player.world.removeMobFromWorld(this);
		return hit;
	}
	
	/**
	 * @return Returns the angle, relative to facing direction.
	 */
	public float getRelativeAngle(){
		float f = Math.abs(facing - Math.abs(Physics.calculate2DAngle(Main.getInstance().player.getPosition(), getPosition())));
		if(f > 180) f -= _FOV_TO_CALL / 2;
		else f += _FOV_TO_CALL / 2;
		return f;
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
