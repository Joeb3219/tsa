package com.charredsoftware.tsa.entity;

import java.io.IOException;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.gui.TextPopup;
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

	public Position pos2;
	public static Texture texture = null;
	private Random r = new Random();
	public static final float _FOV_TO_CALL = 30, _DISTANCE_TO_CALL = 2f;
	public boolean walkingTowardsPos2 = true;
	
	/**
	 * Creates a new Spinner Mob
	 * @param x X-Position
	 * @param y Y-Position
	 * @param z Z-Position
	 */
	public Worker(float x, float y, float z){
		super();
		identifier = MobType.WORKER;
		killBonus = 2f;
		texture = getTexture();
		this.x = x;
		this.y = y;
		this.z = z;
		this.height = 2f;
		this.shielding = 0.0f;
		this.mass = 50f;
	}
	
	public Texture getTexture(){
		if(texture == null){
			try {
				texture = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "henchman.png"));
			} catch (IOException e) {new CrashReport(e);}
		}
		return texture;
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
		if(determineIfShouldShoot()){
			facing += 2;
			if(!determineIfShouldShoot()) facing -= 4;
			if(getPosition().calculateDistance(Main.getInstance().player.getPosition()) < _DISTANCE_TO_CALL && r.nextInt(100) <= 2.5 * Main.getInstance().controller.difficulty){
				Arrow a = new Arrow(this, Main.getInstance().player.world, new Position(x, y + 1, z), 5, (float) (facing - 270), 0);
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
	
	/**
	 * @return Returns <tt>true</tt> if should shoot the bow.
	 */
	public boolean determineIfShouldShoot(){
		if(getRelativeAngle() <= _FOV_TO_CALL) return true;
		
		return false;
	}
	
}
