package com.charredsoftware.tsa.entity;

import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.nio.FloatBuffer;

import org.lwjgl.util.vector.Vector3f;

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
 * Arrow class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 6, 2014
 */

public class Arrow extends Entity{

	public float beginningVerticalVelocity = 0f, verticalVelocity = 0f, horizontalVelocity = 0f;
	public static final float DRAWBACK_MULTIPLIER = 2.3f, _STEPS = 10; //Number of steps to take in movement.
	public float drawBackTime, flyingTime = 0f;
	public float rY, rX; //Used to calculate launch angles.
	public BlockInstance blockStuckIn = null;
	public boolean shouldBeLit = true;
	public Entity shooter;
	public static int _LIFESPAN_AFTER_STUCK = Main.DESIRED_TPS * 2;
	public int ticksSinceStuck = 0;
	public Model model;
	
	/**
	 * Creates a new Arrow.
	 * Calculates velocities, rotations, etc.
	 * @param w World to place the arrow in.
	 * @param p Position at which the arrow starts.
	 * @param drawBackTime How long the bow was pulled back.
	 */
	public Arrow(Entity shooter, World w, Position p, float drawBackTime, float rX, float rY){
		super(p.x, p.y, p.z);
		if(model == null) model = Loader.load(new File(FileUtilities.getBaseDirectory() + "res/" + FileUtilities.texturesPath + "arrow.obj"));
		this.shooter = shooter;
		this.world = w;
		this.rY = rY;
		this.rX = rX;
		float velocityMagnitude = drawBackTime * DRAWBACK_MULTIPLIER;
		if((shooter instanceof Player) && ((Player)shooter).bow.UPGRADE_FURTHER_SHOTS) velocityMagnitude *= 1.25f;
		this.drawBackTime = drawBackTime;
		horizontalVelocity = (float) (Math.abs(Math.cos(Math.toRadians(rY))) * velocityMagnitude);
		verticalVelocity = (float) (Math.abs(Math.sin(Math.toRadians(rY))) * velocityMagnitude) * ((rY < 0) ? 1 : -1);
		beginningVerticalVelocity = verticalVelocity;
	}
	
	/**
	 * Updates the arrow. Moves it along its path.
	 * Uses game's physics engine.
	 */
	public void update(){
		if(y <= 0) markedForDeletion = true;
		if(stuck() || markedForDeletion){
			if(!(shooter instanceof Player) && stuck()) ticksSinceStuck ++;
			if(ticksSinceStuck >= _LIFESPAN_AFTER_STUCK) markedForDeletion = true;
			return;
		}
		flyingTime += 0.5f / Main.DESIRED_TPS;
		double ryCos = Math.cos(Math.toRadians(rY));
		float hVelocity = horizontalVelocity / _STEPS;
		Vector3f v = new Vector3f((float) (Math.sin(Math.toRadians(rX)) * hVelocity * ryCos), verticalVelocity, (float) -(Math.cos(Math.toRadians(rX)) * hVelocity * ryCos));
		v.setY(v.getY() /_STEPS);
		
		for(float step = 0; step < _STEPS; step ++){
			Position last = getPosition();
			move(v.getX(), v.getY(), v.getZ());
			checkIfHitMob();
			if(last.equals(getPosition())) break; //Nothing has changed -> no movement -> get out of here!
		}
		
		verticalVelocity = Physics.calculateFinalVelocity(beginningVerticalVelocity, Physics.DOWNWARD_ACCELERATION, flyingTime);
		
	}
	
	public boolean stuck(){
		if(blockStuckIn == null) return false;
		return true;
	}
	
	/**
	 * Checks if the arrow has hit a mob.
	 */
	private void checkIfHitMob(){
		for(Entity e : world.existingEntities){
			if(!(e instanceof Mob)) continue;
			Mob m = (Mob) e;
			if(m.health <= 0) continue;
			if(m.arrowHit(this)){
				blockStuckIn = new BlockInstance(Block.air, 0, -100, 0);
				Sound.ARROW_HIT_MOB.playSfx();
				markedForDeletion = true;
				return;
			}
		}
		if(Main.getInstance().player.arrowHit(this)){
			blockStuckIn = new BlockInstance(Block.air, 0, -100, 0);
			Sound.DAMAGE_GROUND.playSfx();
			markedForDeletion = true;
		}
	}
	
	/**
	 * Moves the arrow if possible, else stops it and plays sound effect.
	 */
	public void move(float dx, float dy, float dz){
		if(!world.getBlock(new Position(dx + x, dy + y, dz + z)).base.solid){
			x += dx;
			z += dz;
			y += dy;
		}else{
			Sound.ARROW_HIT.playSfx();
			blockStuckIn = world.getBlock(new Position(dx + x, dy + y, dz + z));
		}
	}
	
	/**
	 * Sets up the VBOs for the rendering process.
	 */
	public void preRender(){
	}
	
	/**
	 * Cleans up the rendering process
	 */
	public void postRender(){
	}
	
	float facing = 0;
	/**
	 * Renders the arrow.
	 * Enables lighting if possible.
	 */
	public void render(){
		lightArrow(shouldBeLit);
		
		glPushMatrix();
		glTranslatef(x, y, z);
		
		glRotatef(-rY, 1, 0, 0);
		glRotatef(180, 1, 0, 0);
		glRotatef((rX > 180) ? rX : -rX, 0, 1f, 0);
		if(rX >= 60 && rX <= 160) glRotatef(180, 0, 1, 0);

		glScalef(1f, 1f, 1f);
		
		model.render();
		
		glPopMatrix();
	}
	
	/**
	 * @return Returns the amount of damage that the arrow will do.
	 */
	public int calculateDamage(Mob m){
		int maxDamage = 5;
		if(!(shooter instanceof Player)) return maxDamage;
		int damage = 0;
		Bow bow = Main.getInstance().player.bow;
		
		damage = (int) (maxDamage * 2 * (1f - m.shielding) * ((bow.UPGRADE_MORE_DAMAGE) ? 2 : 1));
		
		return damage;
	}
	
	/**
	 * Lights an arrow. If visible, sets location to real location. If not, sets it to under world.
	 * @param isVisible Whether or not the arrow is visible.
	 */
	public void lightArrow(boolean isVisible){
		if(stuck() && Main.getInstance().controller.lightInUse > GL_LIGHT7) markedForDeletion = true;
		
		if(!markedForDeletion && shooter instanceof Player){
			if(isVisible){
				glEnable(Main.getInstance().controller.lightInUse);
				glLight(Main.getInstance().controller.lightInUse, GL_POSITION, (FloatBuffer) (Main.getInstance().camera.buffer.put((new float[]{ x, y, z, 1f }))).flip());
			}
			Main.getInstance().controller.lightInUse ++;
		}
	}
	
}
