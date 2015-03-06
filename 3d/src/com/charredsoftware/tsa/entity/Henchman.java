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
 * A Henchman mob!
 * This mob is called upon by a Worker, and follows the player around.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 12th, 2014
 */

public class Henchman extends Mob{

	public static final float _FOV_TO_SHOOT = 60f, _DIST_TO_SHOOT = 7f, _CHAIN_TO_STARTING_POINT = 5f;
	private Random r = new Random();
	public Position startingPoint;
	public boolean followingPlayer = false;
	public float speed = 2f; //in m/s
	public static Model model;
	
	public Henchman(World world, Position p){
		super(world);
		if(model == null) model = Loader.load(new File(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.texturesPath + "henchman.obj"));
		startingPoint = new Position(p.x, p.y, p.z);
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
		this.startingX = x;
		this.startingY = y;
		this.startingZ = z;
		identifier = MobType.STALKER;
		height = 2f;
		shielding = 0.55f;
		killBonus = 20f;
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
			if(Math.abs(facing - (a.rX + 90)) > _FOV_TO_SHOOT)
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
				Main.getInstance().HUDText.popups.add(new TextPopup("Killed a Henchman and received " + killBonus + " points!"));
			}
			if(ticksSinceDeath > _TICKS_AFTER_DEATH_TILL_DELETION) markedForDeletion = true;
			else ticksSinceDeath ++;
			return;
		}
		followingPlayer = determineIfFollowingPlayer();
		if(followingPlayer){
			if(r.nextInt(100) <= 5 * Main.getInstance().controller.difficulty){
				Arrow a = new Arrow(this, world, new Position(x, y + 1, z), 5, facing - 270 , 0);
				a.shouldBeLit = false;
				Sound.BOW_SHOT.playSfx();
				
				a = new Arrow(this, world, new Position(x, y + 1, z), 5, facing - 270 + 5, 0);
				a.shouldBeLit = false;
				Sound.BOW_SHOT.playSfx();
				
				a = new Arrow(this, world, new Position(x, y + 1, z), 5, facing - 270 - 5, 0);
				a.shouldBeLit = false;
				Sound.BOW_SHOT.playSfx();
			}
		}else{
			boolean changedDirection = false;
			if(r.nextInt(65) == 1 || (startingPoint.calculateDistance(getPosition()) + 0.5f >= _CHAIN_TO_STARTING_POINT && r.nextInt(25) == 1)){
				changedDirection = true;
				facing += 90f;
				if(facing >= 360f) facing = 0f;
			}
			if(!changedDirection || startingPoint.calculateDistance(getPosition()) + 0.5f >= _CHAIN_TO_STARTING_POINT){
				if(r.nextInt(25) == 1){
					Position beginMovement = new Position(x, y, z);
					if(facing == 0) move(1, 0, 0);
					if(facing == 90) move(0, 0, 1);
					if(facing == 180) move(-1, 0, 0);
					if(facing == 270) move(0, 0, -1);
					if(startingPoint.calculateDistance(beginMovement) - 0.5f > _CHAIN_TO_STARTING_POINT){
						//Moved to far!
						x = beginMovement.x;
						y = beginMovement.y;
						z = beginMovement.z;
					}
				}
			}
		}
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
		if((Main.getInstance().player.getPosition().calculateDistance(getPosition()) > _DIST_TO_SHOOT)) return false;
		float angle = (float) Math.atan2(Main.getInstance().player.z - z, Main.getInstance().player.x - x);
		
		angle = (float) Math.toDegrees(angle);
		if(angle < 0) angle += 360;
		
		if(Math.abs(facing - Math.abs(angle)) <= _FOV_TO_SHOOT) return true;
		
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
