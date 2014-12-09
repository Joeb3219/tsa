package com.charredsoftware.tsa.entity;

import static org.lwjgl.opengl.GL11.*;

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
	public static final float DRAWBACK_MULTIPLIER = 4f, _STEPS = 8; //Number of steps to take in movement.
	public float flyingTime = 0f;
	public float rY, rX; //Used to calculate launch angles.
	public boolean stuckInSolid = false;
	
	/**
	 * Creates a new Arrow.
	 * Calculates velocities, rotations, etc.
	 * @param w World to place the arrow in.
	 * @param p Position at which the arrow starts.
	 * @param drawBackTime How long the bow was pulled back.
	 */
	public Arrow(World w, Position p, float drawBackTime){
		super(p.x, p.y, p.z);
		this.world = w;
			try {
				if(texture == null) texture = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "arrow.png"));
			} catch (IOException e) {new CrashReport(e);}
		
		rY =  Main.getInstance().camera.ry;
		rX = Main.getInstance().camera.rx;
		float velocityMagnitude = drawBackTime * DRAWBACK_MULTIPLIER;
		horizontalVelocity = (float) (Math.abs(Math.cos(Math.toRadians(rY))) * velocityMagnitude);
		verticalVelocity = (float) (Math.abs(Math.sin(Math.toRadians(rY))) * velocityMagnitude) * ((rY < 0) ? 1 : -1);
		beginningVerticalVelocity = verticalVelocity;
	}
	
	/**
	 * Updates the arrow. Moves it along its path.
	 * Uses game's physics engine.
	 */
	public void update(){
		if(stuckInSolid || y < 0) return;
		flyingTime += 0.5f / Main.DESIRED_TPS;
		double ryCos = Math.cos(Math.toRadians(rY));
		float hVelocity = horizontalVelocity / _STEPS;
		Vector3f v = new Vector3f((float) (Math.sin(Math.toRadians(rX)) * hVelocity * ryCos), verticalVelocity, (float) -(Math.cos(Math.toRadians(rX)) * hVelocity * ryCos));
		v.setY(v.getY() /_STEPS);
		
		for(float step = 0; step < _STEPS; step ++){
			Position last = getPosition();
			move(v.getX(), v.getY(), v.getZ());
			if(last.equals(getPosition())) break; //Nothing has changed -> no movement -> get out of here!
		}
		
		verticalVelocity = Physics.calculateFinalVelocity(beginningVerticalVelocity, Physics.DOWNWARD_ACCELERATION, flyingTime);
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
		FloatBuffer buffer = BufferUtils.createFloatBuffer(4);
	
		glLight(Main.getInstance().controller.lightInUse, GL_AMBIENT, (FloatBuffer) (buffer.put((new float[]{ 255f / 255f, 36f / 255f, 0f / 255f, 1.0f }))).flip());
		glLight(Main.getInstance().controller.lightInUse, GL_DIFFUSE, (FloatBuffer) (buffer.put((new float[]{ 255f / 255f, 36f / 255f, 0f / 255f, 1.0f }))).flip());
		glLight(Main.getInstance().controller.lightInUse, GL_SPECULAR, (FloatBuffer) (buffer.put((new float[]{ 0.4f, 0.4f, 0.4f, 1.0f }))).flip());
		glLight(Main.getInstance().controller.lightInUse, GL_POSITION, (FloatBuffer) (buffer.put((new float[]{ x, y, z, 1f }))).flip());
		
		Main.getInstance().controller.lightInUse ++;
		
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
	
}
