package com.charredsoftware.tsa.entity;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.charredsoftware.tsa.CrashReport;
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

	/*
	 * TODO: FIGURE OUT WHY ARROWS SHOOT SO SLOW NOW!!!
	 */

	public static Texture texture = null;
	public float beginningVerticalVelocity = 0f, verticalVelocity = 0f, horizontalVelocity = 0f;
	public static final float DRAWBACK_MULTIPLIER = 2.3f, _STEPS = 8; //Number of steps to take in movement.
	public float drawBackTime, flyingTime = 0f;
	public float rY, rX; //Used to calculate launch angles.
	public BlockInstance blockStuckIn = null;
	public boolean shouldBeLit = true;
	public Entity shooter;
	public static int _VERTICES = 6 * 4, _VERTEX_SIZE = 3, _TEXTURE_SIZE = 2;
	public static int vboHandler = -1, textHandler = -1;
	public static FloatBuffer vertexData, texData;
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
			try {
				if(texture == null) texture = TextureLoader.getTexture("png", ClassLoader.getSystemResourceAsStream(FileUtilities.texturesPath + "arrow.png"));
			} catch (IOException e) {new CrashReport(e);}
		
		this.rY = rY;
		this.rX = rX;
		float velocityMagnitude = drawBackTime * DRAWBACK_MULTIPLIER;
		if((shooter instanceof Player) && ((Player)shooter).bow.UPGRADE_FURTHER_SHOTS) velocityMagnitude /= 2;
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
				Sound.ARROW_HIT.playSfx();
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
	 * Creates the VBOs for the Arrows.
	 */
	private void createVBOs(){
		if(vertexData != null && texData != null) return;

		float leftBound = -0.25f;
		float rightBound = 0.25f;
		float sideDivisor = 8f;
		float endDivisor = 4f;

		vertexData = BufferUtils.createFloatBuffer(_VERTICES * _VERTEX_SIZE);
		vertexData.put(new float[]{leftBound / sideDivisor,leftBound / sideDivisor,rightBound, leftBound / sideDivisor,rightBound / sideDivisor,rightBound, rightBound / sideDivisor,rightBound / sideDivisor,rightBound, rightBound / sideDivisor,leftBound / sideDivisor,rightBound,
				leftBound / endDivisor,leftBound / endDivisor,leftBound, leftBound / endDivisor,rightBound / endDivisor,leftBound, rightBound / endDivisor,rightBound / endDivisor,leftBound, rightBound / endDivisor,leftBound / endDivisor,leftBound,
				leftBound / sideDivisor,leftBound / sideDivisor,leftBound, leftBound / sideDivisor,leftBound / sideDivisor,rightBound, leftBound / sideDivisor,rightBound / sideDivisor,rightBound, leftBound / sideDivisor,rightBound / sideDivisor,leftBound,
				rightBound / sideDivisor,leftBound / sideDivisor,leftBound, rightBound / sideDivisor,leftBound / sideDivisor,rightBound, rightBound / sideDivisor,rightBound / sideDivisor,rightBound, rightBound / sideDivisor,rightBound / sideDivisor,leftBound,
				leftBound / sideDivisor,leftBound / sideDivisor,leftBound, rightBound / sideDivisor,leftBound / sideDivisor,leftBound, rightBound / sideDivisor,leftBound / sideDivisor,rightBound, leftBound / sideDivisor,leftBound / sideDivisor,rightBound,
				leftBound / sideDivisor,rightBound / sideDivisor,leftBound, rightBound / sideDivisor,rightBound / sideDivisor,leftBound, rightBound / sideDivisor,rightBound / sideDivisor,rightBound, leftBound / sideDivisor,rightBound / sideDivisor,rightBound});
		vertexData.flip();
		
		texData = BufferUtils.createFloatBuffer(_TEXTURE_SIZE * _VERTICES);
		texData.put(new float[]{0, 2/4f, 0, 1/4f, 1/4f, 1/4f, 1/4f, 2/4f,
				2/4f, 2/4f, 2/4f, 1/4f, 1/4f, 1/4f, 1/4f, 2/4f,
				2/4f, 2/4f, 3/4f, 2/4f, 3/4f, 1/4f, 2/4f, 1/4f,
				4/4f, 2/4f, 3/4f, 2/4f, 3/4f, 1/4f, 4/4f, 1/4f,
				0/4f, 3/4f, 1/4f, 3/4f, 1/4f, 2/4f, 0/4f, 2/4f,
				0/4f, 0/4f, 1/4f, 0/4f, 1/4f, 1/4f, 0/4f, 1/4f});
		texData.flip();
		
		vboHandler = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboHandler);
		glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		textHandler = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, textHandler);
		glBufferData(GL_ARRAY_BUFFER, texData, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	/**
	 * Sets up the VBOs for the rendering process.
	 */
	public void preRender(){
		if(1 == 1) return;
		if(vboHandler == -1 || textHandler == -1) createVBOs();
		glEnable(GL_TEXTURE_2D);
		texture.bind();
		glBindBuffer(GL_ARRAY_BUFFER, vboHandler);
		glVertexPointer(_VERTEX_SIZE, GL_FLOAT, 0, 0L);
			
		glBindBuffer(GL_ARRAY_BUFFER, textHandler);
		glTexCoordPointer(_TEXTURE_SIZE, GL_FLOAT, 0, 0L);
			
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_TEXTURE_COORD_ARRAY);
	}
	
	/**
	 * Cleans up the rendering process
	 */
	public void postRender(){
		if(1 == 1) return;
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDisable(GL_TEXTURE_2D);
	}
	
	/**
	 * Renders the arrow.
	 * Enables lighting if possible.
	 */
	public void render(){
		if(shouldBeLit){
			if(stuck() && Main.getInstance().controller.lightInUse > GL_LIGHT7) markedForDeletion = true;
			
			if(!markedForDeletion){
				glLight(Main.getInstance().controller.lightInUse, GL_POSITION, (FloatBuffer) (Main.getInstance().camera.buffer.put((new float[]{ x, y, z, 1f }))).flip());
				Main.getInstance().controller.lightInUse ++;
			}
		}
		
		glPushMatrix();

		glTranslatef(x, y, z);
		if(false && stuck()){
			if(blockStuckIn.x > x) glRotatef(-90, 1, 0, 0);
			if(blockStuckIn.x < x) glRotatef(90, 1, 0, 0);
			if(blockStuckIn.y < y) glRotatef(-90, 0, 1, 0);
			if(blockStuckIn.y > y) glRotatef(90, 0, 1, 0);
			if(blockStuckIn.z > z) glRotatef(-90, 0, 0, 1);
			if(blockStuckIn.z < z) glRotatef(90, 0, 0, 1);
		}else{
			glRotatef(-rY, 1, 0, 0);
			glRotatef(180, 1, 0, 0);
			glRotatef((rX > 180) ? rX : -rX, 0, 1, 0);
			if(rX >= 60 && rX <= 160) glRotatef(180, 0, 1, 0);
		}
		
		glScalef(1f, 1/4f, 1f);
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
	
}
