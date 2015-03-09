package com.charredsoftware.tsa.entity;

import static org.lwjgl.opengl.GL11.GL_CURRENT_BIT;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopAttrib;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushAttrib;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.io.File;
import java.util.ArrayList;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.obj.Loader;
import com.charredsoftware.tsa.obj.Model;
import com.charredsoftware.tsa.physics.Physics;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Block;
import com.charredsoftware.tsa.world.BlockInstance;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.World;

/**
 * Mob class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class Mob extends Entity{

	public MobType identifier = MobType.GENERIC;
	public int health = 20;
	public float height = 2f;
	public boolean isJumping, isCrouching = false;
	protected float jumpingTime = 0;
	public float killBonus = 0f;
	public float shielding = 0f; //1f is total shielding.
	public float ticksSinceDeath = 0f;
	public static final float _TICKS_AFTER_DEATH_TILL_DELETION = Main.DESIRED_TPS * 3;
	public static Model bowModel;
	public float facing = 0f;
	
	/**
	 * Creates a new Mob.
	 * @param world World which world the mob will live in.
	 */
	public Mob(World world){
		super();
		this.world = world;
		if(bowModel == null) bowModel = Loader.load(new File(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.texturesPath + "bow.obj"));
	}
	
	/**
	 * Updates the mob. 
	 */
	public void update(){
		if(getBlockUnder().base == Block.boost){
			isJumping = true;
			currentJumpingVelocity = defaultStartJumpingVelocity * 1.3f;
			beginningJumpingVelocity = currentJumpingVelocity;
			jumpingTime = 0;
		}
		checkGettingHurt();
	}
	
	/**
	 * Checks to see if can jump without going through a block.
	 * @param dY Change in Y position.
	 */
	protected void checkCanJump(float dY){
		float fY = y + dY;
		ArrayList<BlockInstance> blocks = world.getBlocksInRange(x, z, y, dY);
		if(dY > 0){
			for(int i = 0; i <= blocks.size() - 1; i ++){
				if(blocks.get(i).y < fY + height && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y = blocks.get(i).y - height;
					Sound.DAMAGE_GROUND.playSfx();
					return;
				}
			}
		}else{
			for(int i = blocks.size() - 1; i >= 0; i --){
				if(blocks.get(i).y > fY && blocks.get(i).base.solid){
					currentJumpingVelocity = 0f;
					y =  blocks.get(i).y + 1;
					return;
				}
			}
		}
		y = fY;
	}
	
	/**
	 * Makes the mob jump
	 * @param startJump whether or not is starting a jump.
	 */
	public void jump(boolean startJump){
		if(startJump && !isJumping && standingOnSolid()){
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
				if(this instanceof Player) {
					Sound.HIT_GROUND.playSfx();
					if(potentialDamage > 0) Sound.DAMAGE_GROUND.playSfx();
					if(!Main.getInstance().controller.buildingMode) health -= potentialDamage;
				}
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
	 * Check if the mob is getting damaged.
	 */
	protected void checkGettingHurt() {
		if(y < -50 && ((int) y) % 4 == 0) health --;
		if(stuckInBlock()){
			Position startingPosition = getPosition();
			if(canMove(0, 1, 0)) move(0, 1, 0);
		}
	}
	
	/**
	 * @return Returns <tt>true</tt> if the mob is stuck inside of a block.
	 */
	public boolean stuckInBlock(){
		Position p = new Position(x, y, z);
		p.normalizeCoords();
		return world.getBlock(p).base.solid;
	}
	
	/**
	 * @return Returns <tt>true</tt> if the mob is in water.
	 */
	public boolean isInWater(){
		if(world.getBlock(new Position(x, y, z)).base == Block.water) return true;
		if(world.getBlock(new Position(x, y + 1, z)).base == Block.water) return true;
		return false;
	}
	
	/**
	 * @return Returns the <code>BlockInstance</code> directly under the mob.
	 */
	public BlockInstance getBlockUnder(){
		Position p = new Position(x, y - 1f, z);
		return world.getBlock(p);
	}

	/**
	 * @return Returns <tt>true</tt> if the block under the mob is solid.
	 * @see #getBlockUnder()
	 */
	public boolean standingOnSolid(){
		return getBlockUnder().base.solid;
	}
	
	/**
	 * @param a Arrow
	 * @return Returns <tt>true</tt> if an arrow has hit the mob.
	 */
	public boolean arrowHit(Arrow a){
		if(a.markedForDeletion || a.stuck()) return false;
		if(a.shooter == this) return false;
		if(Math.abs(a.x - x) > 1) return false;
		if(Math.abs(a.z - z) > 1) return false;
		if(a.y < y || a.y > y + height) return false;
		return true;
	}
	
	/**
	 * Damages the mob.
	 * @param damage Amount of damage
	 */
	public void damageMob(int damage){
		health -= damage;
	}
	
	/**
	 * Renders a bow on the mob
	 */
	public void renderBow(){
		float facing = this.facing + 90f;
 		glPushMatrix();
 		glPushAttrib(GL_CURRENT_BIT);
 		glColor3f(150 / 255f, 113 / 255f, 27 / 255f);
 		glTranslatef(.75f - (0.05f * (float) (Math.sin(Math.toRadians(facing)))), height / 6, 0.75f - (0.05f * (float) (Math.cos(Math.toRadians(facing)))));
		glPushMatrix();
		glRotatef(90f, 1f, 0f, 0f);
		glRotatef(180, 1f, 0f, 0f);
		bowModel.render();
		glPopMatrix();
		glPopAttrib();
		glPopMatrix();
	}
	
	/**
	 * Calculates the direction the Worker should face.
	 */
	protected void calculateFacingDirection(Position destination){
		facing = Physics.calculate2DAngle(getPosition(), destination) + 180;
	}
	
	/**
	 * @return Returns the angle, relative to facing direction.
	 * @param angle Angle at which shooting will occur.
	 */
	protected float getRelativeAngle(float angle){
		float f = Math.abs(facing - Math.abs(Physics.calculate2DAngle(Main.getInstance().player.getPosition(), getPosition())));
		if(f > 180) f -= angle / 2;
		else f += angle / 2;
		return f;
	}
	
	protected float correctAngle(float facing){
		while(facing < 0) facing = 360 + facing;
		while(facing >= 360) facing -= 360;
		return facing;
	}
	
}
