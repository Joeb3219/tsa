package com.charredsoftware.tsa.entity;

/**
 * A Spinner mob!
 * This mob spins and shoots arrows.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 12th, 2014
 */

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
import com.charredsoftware.tsa.physics.Physics;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.World;

public class Spinner extends Mob{

	private Random r = new Random();
	public static final float _FOV_TO_SHOOT = 40, _DISTANCE_TO_SHOOT = 6.5f, _DISTANCE_TO_TRACK = 8f;
	public static Model model;
	
	/**
	 * Creates a new Spinner Mob
	 * @param x X-Position
	 * @param y Y-Position
	 * @param z Z-Position
	 */
	public Spinner(World world, float x, float y, float z){
		super(world);
		if(model == null) model = Loader.load(new File(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.texturesPath + "spinner.obj"));
		identifier = MobType.SPINNER;
		killBonus = 5f;
		this.x = x;
		this.y = y;
		this.z = z;
		this.startingX = x;
		this.startingY = y;
		this.startingZ = z;
		this.height = 2f;
		this.shielding = 0.25f;
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
		if(determineIfShouldTrack()){
			facing += 2;
			if(!determineIfShouldTrack()) facing -= 4;
			if(getPosition().calculateDistance(Main.getInstance().player.getPosition()) < _DISTANCE_TO_SHOOT && r.nextInt(100) <= 2.5 * Main.getInstance().controller.difficulty){
				Arrow a = new Arrow(this, world, new Position(x, y + 1, z), 5, (float) (facing - 270), 0);
				a.shouldBeLit = false;
				Sound.BOW_SHOT.playSfx();
			}
		}else{
			facing -= 1;
		}
		
	}
	
	/**
	 * Detects if an arrow has hit the mob, and damages the mob if so.
	 * @return Returns <tt>true</tt> if an arrow has hit the mob.
	 */
	public boolean arrowHit(Arrow a){
		boolean hit = super.arrowHit(a);
		if(!(a.shooter instanceof Player)) hit = false; //If hit by another mob, no damage.
		if(hit) damageMob(a.calculateDamage(this));
		if(hit && Main.getInstance().controller.removeMobMode) world.removeMobFromWorld(this);
		return hit;
	}
	
	/**
	 * @return Returns the angle, relative to facing direction.
	 */
	public float getRelativeAngle(){
		float f = Math.abs(facing - Math.abs(Physics.calculate2DAngle(Main.getInstance().player.getPosition(), getPosition())));
		if(f > 180) f -= _FOV_TO_SHOOT / 2;
		else f += _FOV_TO_SHOOT / 2;
		return f;
	}
	
	/**
	 * @return Returns <tt>true</tt> if should track the player.
	 */
	public boolean determineIfShouldTrack(){
		return (getPosition().calculateDistance(Main.getInstance().player.getPosition()) <= _DISTANCE_TO_TRACK && getRelativeAngle() <= _FOV_TO_SHOOT);
	}
	
	/**
	 * Renders the mob.
	 */
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
