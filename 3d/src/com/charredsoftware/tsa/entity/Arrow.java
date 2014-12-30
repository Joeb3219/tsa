package com.charredsoftware.tsa.entity;

import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_LIGHT7;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.physics.Physics;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.Region;
import com.charredsoftware.tsa.world.World;

/**
 * Arrow class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 6, 2014
 */

public class Arrow extends Entity{

	public static Texture texture = null;
	public float beginningVerticalVelocity = 0f, verticalVelocity = 0f, horizontalVelocity = 0f;
	public static final float DRAWBACK_MULTIPLIER = 2.3f, _STEPS = 8; //Number of steps to take in movement.
	public float drawBackTime, flyingTime = 0f;
	public float rY, rX; //Used to calculate launch angles.
	public boolean stuckInSolid = false;
	public boolean shouldBeLit = true;
	public Entity shooter;
	
	/**
	 * Creates a new Arrow.
	 * Calculates velocities, rotations, etc.
	 * @param w World to place the arrow in.
	 * @param p Position at which the arrow starts.
	 * @param drawBackTime How long the bow was pulled back.
	 */
	public Arrow(Entity shooter, World w, Position p, float drawBackTime, float rX, float rY){
		super(p.x, p.y, p.z);
		this.shooter = shooter;
		this.world = w;
			try {
				if(texture == null) texture = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "arrow.png"));
			} catch (IOException e) {new CrashReport(e);}
		
		this.rY = rY;
		this.rX = rX;
		float velocityMagnitude = drawBackTime * DRAWBACK_MULTIPLIER;
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
		if(stuckInSolid || markedForDeletion) return;
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
	
	/**
	 * Checks if the arrow has hit a mob.
	 */
	private void checkIfHitMob(){
		for(Entity e : world.existingEntities){
			if(!(e instanceof Mob)) continue;
			Mob m = (Mob) e;
			if(m.health <= 0) continue;
			if(m.arrowHit(this)){
				stuckInSolid = true;
				Sound.ARROW_HIT.playSfx();
				markedForDeletion = true;
				return;
			}
		}
		if(Main.getInstance().player.arrowHit(this)){
			stuckInSolid = true;
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
			stuckInSolid = true;
		}
	}
	
	/**
	 * Renders the arrow.
	 * Enables lighting if possible.
	 */
	public void render(){
	
		if(shouldBeLit){
			if(stuckInSolid && Main.getInstance().controller.lightInUse > GL_LIGHT7) markedForDeletion = true;
			
			if(!markedForDeletion){
				glLight(Main.getInstance().controller.lightInUse, GL_POSITION, (FloatBuffer) (Main.getInstance().camera.buffer.put((new float[]{ x, y, z, 1f }))).flip());
				Main.getInstance().controller.lightInUse ++;
			}
		}
		
		glEnable(GL_TEXTURE_2D);
		texture.bind();
		
		glPushMatrix();
		glTranslatef(x, y, z);
		glRotatef(-rY, 1, 0, 0);
		glRotatef(180, 1, 0, 0);
		glRotatef((rX > 180) ? rX : -rX, 0, 1, 0);
		if(rX >= 60 && rX <= 160) glRotatef(180, 0, 1, 0);
		
		glBegin(GL_QUADS);
		
		float leftBound = -0.25f;
		float rightBound = 0.25f;
		float sideDivisor = 8f;
		float endDivisor = 4f;
		
		//Front
		glTexCoord2f(0, 2/4f); glVertex3f(leftBound / sideDivisor,leftBound / sideDivisor,rightBound);
		glTexCoord2f(0, 1/4f); glVertex3f(leftBound / sideDivisor,rightBound / sideDivisor,rightBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound / sideDivisor,rightBound / sideDivisor,rightBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound / sideDivisor,leftBound / sideDivisor,rightBound);

		//Back
		glTexCoord2f(2/4f, 2/4f); glVertex3f(leftBound / endDivisor,leftBound / endDivisor,leftBound);
		glTexCoord2f(2/4f, 1/4f); glVertex3f(leftBound / endDivisor,rightBound / endDivisor,leftBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound / endDivisor,rightBound / endDivisor,leftBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound / endDivisor,leftBound / endDivisor,leftBound);

		//Left
		glTexCoord2f(2/4f, 2/4f); glVertex3f(leftBound / sideDivisor,leftBound / sideDivisor,leftBound);
		glTexCoord2f(3/4f, 2/4f); glVertex3f(leftBound / sideDivisor,leftBound / sideDivisor,rightBound);
		glTexCoord2f(3/4f, 1/4f); glVertex3f(leftBound / sideDivisor,rightBound / sideDivisor,rightBound);
		glTexCoord2f(2/4f, 1/4f); glVertex3f(leftBound / sideDivisor,rightBound / sideDivisor,leftBound);

		//Right
		glTexCoord2f(4/4f, 2/4f); glVertex3f(rightBound / sideDivisor,leftBound / sideDivisor,leftBound);
		glTexCoord2f(3/4f, 2/4f); glVertex3f(rightBound / sideDivisor,leftBound / sideDivisor,rightBound);
		glTexCoord2f(3/4f, 1/4f); glVertex3f(rightBound / sideDivisor,rightBound / sideDivisor,rightBound);
		glTexCoord2f(4/4f, 1/4f); glVertex3f(rightBound / sideDivisor,rightBound / sideDivisor,leftBound);

		//Bottom
		glTexCoord2f(0/4f, 3/4f); glVertex3f(leftBound / sideDivisor,leftBound / sideDivisor,leftBound);
		glTexCoord2f(1/4f, 3/4f); glVertex3f(rightBound / sideDivisor,leftBound / sideDivisor,leftBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound / sideDivisor,leftBound / sideDivisor,rightBound);
		glTexCoord2f(0/4f, 2/4f); glVertex3f(leftBound / sideDivisor,leftBound / sideDivisor,rightBound);

		//Top
		glTexCoord2f(0/4f, 0/4f); glVertex3f(leftBound / sideDivisor,rightBound / sideDivisor,leftBound);
		glTexCoord2f(1/4f, 0/4f); glVertex3f(rightBound / sideDivisor,rightBound / sideDivisor,leftBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound / sideDivisor,rightBound / sideDivisor,rightBound);
		glTexCoord2f(0/4f, 1/4f); glVertex3f(leftBound / sideDivisor,rightBound / sideDivisor,rightBound);
	
		glEnd();
		
		glPopMatrix();
	}
	
	/**
	 * @return Returns the amount of damage that the arrow will do.
	 */
	public int calculateDamage(){
		int maxDamage = 5;
		/*float velocity = (float) Math.sqrt(Math.pow(horizontalVelocity, 2) + Math.pow(verticalVelocity, 2));
		float maxVelocity = (float) Math.sqrt(Math.pow((float) (Math.abs(Math.cos(Math.toRadians(rY))) * DRAWBACK_MULTIPLIER * Bow.maxDrawBackTime), 2) + Math.pow((float) (Math.abs(Math.sin(Math.toRadians(rX))) * DRAWBACK_MULTIPLIER * Bow.maxDrawBackTime), 2));
		velocity = (velocity > maxVelocity) ?  maxVelocity : velocity;
		return (int) (maxDamage * (velocity / maxVelocity * 1f) );*/
		//return (int) (maxDamage * ((drawBackTime < Bow.maxDrawBackTime - 1) ? drawBackTime + 1 : drawBackTime) / Bow.maxDrawBackTime);
		return 5;
	}
	
}
