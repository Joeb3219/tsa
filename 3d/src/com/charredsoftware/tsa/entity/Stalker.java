package com.charredsoftware.tsa.entity;

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
import com.charredsoftware.tsa.physics.Physics;
import com.charredsoftware.tsa.util.FileUtilities;
import com.charredsoftware.tsa.world.Position;

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
	
	public static Texture texture = null;
	public static final float _DISTANCE_TO_STALK = 5f, _FOV_TO_STALK = 60f, _CHAIN_TO_STARTING_POINT = 5f;
	private Random r = new Random();
	public Position startingPoint;
	public boolean followingPlayer = false;
	public float speed = 2f; //in m/s
	
	public Stalker(float startingX, float startingY, float startingZ){
		startingPoint = new Position(startingX, startingY, startingZ);
		this.x = startingX;
		this.y = startingY;
		this.z = startingZ;
		identifier = MobType.STALKER;
		texture = getTexture();
		height = 2f;
		shielding = 0.5f;
		killBonus = 10f;
	}
	
	public Texture getTexture(){
		if(texture == null){
			try {
				texture = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "henchman.png"));
			} catch (IOException e) {new CrashReport(e);}
		}
		return texture;
	}
	
	public boolean arrowHit(Arrow a){
		boolean hit = super.arrowHit(a);
		if(!(a.shooter instanceof Player)) hit = false; //If hit by another mob, no damage.
		if(hit) damageMob(a.calculateDamage(this));
		if(hit && Main.getInstance().controller.removeMobMode) Main.getInstance().player.world.removeMobFromWorld(this);
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
		followingPlayer = determineIfFollowingPlayer();
		if(followingPlayer){
			if(r.nextInt(100) <= 10){
				Arrow a = new Arrow(this, Main.getInstance().player.world, new Position(x, y + 1, z), 5, facing - 270 , 0);
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

		if(!Main.getInstance().player.world.getBlock(new Position(fX, fY, fZ)).base.solid){
			if(Main.getInstance().player.world.getBlock(new Position(fX, fY + height / 2, fZ)).base.solid) return; //Hit yer head!
			x = fX;
			z = fZ;
			y = fY;
		}
	}
	
	public boolean determineIfFollowingPlayer(){
		if((Main.getInstance().player.getPosition().calculateDistance(getPosition()) > _DISTANCE_TO_STALK)) return false;
		float angle = (float) Math.atan2(Main.getInstance().player.z - z, Main.getInstance().player.x - x);
		
		angle = (float) Math.toDegrees(angle);
		if(angle < 0) angle += 360;
		
		if(Math.abs(facing - Math.abs(angle)) <= _FOV_TO_STALK) return true;
		
		return false;
	}
	
	public void render(){
		texture.bind();
		
		glPushMatrix();
		glTranslatef(x, y, z);
		glRotatef(90 - facing, 0, 1, 0);
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
		
		renderBow();
		
		glPopMatrix();
	}

}
