package com.charredsoftware.tsa.entity;

/**
 * A Spinner mob!
 * This mob spins and shoots arrows.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 12th, 2014
 */

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex3f;

import java.io.IOException;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.gui.TextPopup;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;

public class Spinner extends Mob{

	public float facing = 0; //In degrees.
	public static Texture texture = null;
	private Random r = new Random();
	public static final float _FOV_TO_SHOOT = 60;
	
	/**
	 * Creates a new Spinner Mob
	 * @param x X-Position
	 * @param y Y-Position
	 * @param z Z-Position
	 */
	public Spinner(float x, float y, float z){
		super();
		identifier = MobType.SPINNER;
		killBonus = 5f;
		texture = getTexture();
		this.x = x;
		this.y = y;
		this.z = z;
		this.height = 2f;
		this.shielding = 0.25f;
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
		if(health <= 0){
			if(ticksSinceDeath == 0){
				Main.getInstance().player.score += killBonus;
				Main.getInstance().HUDText.popups.add(new TextPopup("Killed a Spinner and received " + killBonus + " points!"));
			}
			if(ticksSinceDeath > _TICKS_AFTER_DEATH_TILL_DELETION) markedForDeletion = true;
			else ticksSinceDeath ++;
			return;
		}
		facing -= 1;
		if(r.nextInt(100) <= 5){
			Arrow a = new Arrow(this, Main.getInstance().player.world, new Position(x, y + 1, z), 2, facing, 0);
			a.shouldBeLit = false;
			Sound.BOW_SHOT.playSfx();
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
	
	public boolean determineIfShouldShoot(){
		float angle = (float) Math.atan2(Main.getInstance().player.z - z, Main.getInstance().player.x - x);
		
		angle = (float) Math.toDegrees(angle);
		if(angle < 0) angle += 360;
		
		if(Math.abs(facing - Math.abs(angle)) <= _FOV_TO_SHOOT) return true;
		
		return false;
	}
	
	/**
	 * Renders the mob.
	 */
	public void render(){
		texture.bind();
		
		glPushMatrix();
		glTranslatef(x, y, z);
		glRotatef(facing, 0, 1, 0);
		if(ticksSinceDeath > 0){
			glRotatef(90, 1, 0, 0);
			glRotatef(facing, 0, 0, 1);
		}
		
		glBegin(GL_QUADS);
		
		float leftBound = -0.5f;
		float rightBound = 0.5f;
		float heightMultiplier = height;
		
		//Front
		glTexCoord2f(0, 2/4f); glVertex3f(leftBound,leftBound,rightBound);
		glTexCoord2f(0, 1/4f); glVertex3f(leftBound,rightBound * heightMultiplier + 0.5f,rightBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound * heightMultiplier + 0.5f,rightBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);

		//Right
		glTexCoord2f(2/4f, 2/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(2/4f, 1/4f); glVertex3f(leftBound,rightBound * heightMultiplier + 0.5f,leftBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound * heightMultiplier + 0.5f,leftBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,leftBound);

		//Left
		glTexCoord2f(2/4f, 2/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(3/4f, 2/4f); glVertex3f(leftBound,leftBound,rightBound);
		glTexCoord2f(3/4f, 1/4f); glVertex3f(leftBound,rightBound * heightMultiplier + 0.5f,rightBound);
		glTexCoord2f(2/4f, 1/4f); glVertex3f(leftBound,rightBound * heightMultiplier + 0.5f,leftBound);

		//Back
		glTexCoord2f(4/4f, 2/4f); glVertex3f(rightBound,leftBound,leftBound);
		glTexCoord2f(3/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);
		glTexCoord2f(3/4f, 1/4f); glVertex3f(rightBound,rightBound * heightMultiplier + 0.5f,rightBound);
		glTexCoord2f(4/4f, 1/4f); glVertex3f(rightBound,rightBound * heightMultiplier + 0.5f,leftBound);

		//Bottom
		glTexCoord2f(0/4f, 3/4f); glVertex3f(leftBound,leftBound,leftBound);
		glTexCoord2f(1/4f, 3/4f); glVertex3f(rightBound,leftBound,leftBound);
		glTexCoord2f(1/4f, 2/4f); glVertex3f(rightBound,leftBound,rightBound);
		glTexCoord2f(0/4f, 2/4f); glVertex3f(leftBound,leftBound,rightBound);

		//Top
		glTexCoord2f(0/4f, 0/4f); glVertex3f(leftBound,rightBound * heightMultiplier + 0.5f,leftBound);
		glTexCoord2f(1/4f, 0/4f); glVertex3f(rightBound,rightBound * heightMultiplier + 0.5f,leftBound);
		glTexCoord2f(1/4f, 1/4f); glVertex3f(rightBound,rightBound * heightMultiplier + 0.5f,rightBound);
		glTexCoord2f(0/4f, 1/4f); glVertex3f(leftBound,rightBound * heightMultiplier + 0.5f,rightBound);
	
		glEnd();

		
		glPopMatrix();
	}
	
}
