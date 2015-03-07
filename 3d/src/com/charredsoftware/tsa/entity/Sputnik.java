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
 * Dr.Sputnik mob class.
 * The final boss! Summons other enemies to aid him
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since March 7, 2015
 */

public class Sputnik extends Mob{

	public Position pos2, startingPoint;
	private Random r = new Random();
	public static final float _DISTANCE_TO_CALL = 15f;
	public int ticksSinceLastCall = 0, totalSummons = 0;
	public static int _MAX_MOBS_PER_CALL = 3, _TICKS_BETWEEN_CALLS = Main.DESIRED_TPS;
	public boolean walkingTowardsPos2 = true;
	public static Model model;
	
	/**
	 * Creates a new Sputnik Mob!
	 * @param x X-Position
	 * @param y Y-Position
	 * @param z Z-Position
	 */
	public Sputnik(World world, float x, float y, float z){
		super(world);
		if(model == null) model = Loader.load(new File(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.texturesPath + "sputnik.obj"));
		identifier = MobType.SPUTNIK;
		killBonus = 200f;
		health = 60;
		startingPoint = new Position(x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
		this.startingX = x;
		this.startingY = y;
		this.startingZ = z;
		this.height = 2f;
		this.shielding = 0.6f;
		this.mass = 50f;
		_TICKS_BETWEEN_CALLS *= ((Main.getInstance().controller.difficulty == 1) ? 10 : ((Main.getInstance().controller.difficulty == 2) ? 8 : 6));
	}
	
	public void update(){
		if(Main.getInstance().controller.buildingMode) return;
		if(health <= 0){
			if(ticksSinceDeath == 0){
				Main.getInstance().player.mobsKilled ++;
				Main.getInstance().player.score += killBonus;
				Main.getInstance().HUDText.popups.add(new TextPopup("Killed Dr. Sputnik and received " + killBonus + " points!"));
			}
			if(ticksSinceDeath > _TICKS_AFTER_DEATH_TILL_DELETION) markedForDeletion = true;
			else ticksSinceDeath ++;
			return;
		}
		ticksSinceLastCall ++;
		
		calculateFacingDirection(Main.getInstance().player.getPosition());
		facing = correctAngle(facing);
		
		if(getPosition().calculateDistance(Main.getInstance().player.getPosition()) < _DISTANCE_TO_CALL && r.nextInt(100) <= 2.5 * Main.getInstance().controller.difficulty){
			Arrow a = new Arrow(this, world, new Position(x, y + 1, z), 5, (float) (facing - 270), 0);
			a.shouldBeLit = false;
			Sound.BOW_SHOT.playSfx();
		}
		
		if(ticksSinceLastCall >= _TICKS_BETWEEN_CALLS){
			totalSummons ++;
			summonMobs();
			ticksSinceLastCall = 0;
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
	
	private void summonMobs(){
		MobType type = null;
		if(totalSummons <= 1) type = MobType.SPINNER;
		else if(totalSummons <= 3) type = MobType.STALKER;
		else if(totalSummons <= 6) type = MobType.WORKER;
		else if(totalSummons <= 9) type = MobType.HENCHMAN;
		for(int i = 0; i <= _MAX_MOBS_PER_CALL; i ++) callMobs(type);
	}
	
	private void callMobs(MobType type){
		if(type == null) return;
		Position p = world.getNearbyEmptyBlock(getPosition(), r.nextInt(8) + 1);
		if(type == MobType.SPINNER) world.existingEntities.add(new Spinner(world, p.x, p.y, p.z));
		if(type == MobType.STALKER) world.existingEntities.add(new Stalker(world, p.x, p.y, p.z));
		if(type == MobType.WORKER) world.existingEntities.add(new Worker(world, x, y, z, p));
		if(type == MobType.HENCHMAN) world.existingEntities.add(new Henchman(world, p));
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
