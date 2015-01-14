package com.charredsoftware.tsa;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.tsa.physics.Physics;
import com.charredsoftware.tsa.world.BlockInstance;
import com.charredsoftware.tsa.world.Position;
import com.charredsoftware.tsa.world.Region;

/**
 * Frustum class.
 * Used to determine which blocks are in view and which are not.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 30, 2014
 */

public class Frustum {

	public float nWidth, nHeight, ratio, fov, tFOV, farClip, nearClip;
	public Vector3f cameraPos, x, y, z;
	private static Frustum _INSTANCE = null;

	/**
	 * Creates a singleton Frustum.
	 */
	private Frustum(){
		
	}
	
	/**
	 * @return Returns an instance of Frustum.
	 */
	public static Frustum getInstance(){
		if(_INSTANCE == null) _INSTANCE = new Frustum();;
		return _INSTANCE;
	}
	
	/**
	 * Calculates the frustum's size.
	 * @param fov Field of View
	 * @param ratio Aspect Ratio, in terms of width:height.
	 * @param nearClip Near distance to clip at.
	 * @param farClip Far distance to clip at.
	 */
	public void calculateFrustum(float fov, float ratio, float nearClip, float farClip){
		this.nearClip = nearClip;
		this.farClip = farClip;
		this.ratio = ratio;
		this.fov = fov;
		this.tFOV = (float) Math.tan(Math.PI / 180f * fov * 0.5f);
		nHeight = tFOV * nearClip;
		nWidth = nHeight * ratio;
	}
	
	/**
	 * Sets the camera's position in the frustum.
	 * @param cameraPos Camera's Position as <code>Vector3f</code>
	 * @param looking Position the player is looking at, as <code>Vector3f</code>
	 * @param up Player's up Position as <code>Vector3f</code>
	 * @see com.charredsoftware.tsa.entity.Player#getLookingAt()
	 */
	public void setCamera(Vector3f cameraPos, Vector3f looking, Vector3f up){
		this.cameraPos = new Vector3f(cameraPos);
		
		z = Vector3f.sub(looking, cameraPos, null);
		z.normalise();
		
		x = Vector3f.cross(z, up, null);
		x.normalise();
		
		y = new Vector3f(0, Vector3f.dot(x, z), 0);
	}
	
	/**
	 * @param r Region to test.
	 * @return Returns <tt>true</tt> if any of the region's testable points are in the frustum.
	 */
	public boolean regionInFrustum(Region r){
		/*
		 * TODO: Let's rewrite the regionInFrustum checks!
		 * 
		 * Instead of using a considerY flag, let's actually check y levels.
		 * Current system will always render other regions if looking straight down, which makes no damn sense (doesn't look for y values).
		 * Instead, let's throw in, starting at lowest level, 8x8 blocks per layer, for every 6-8? layers in a region.
		 * If a block isn't in that spot, don't add it to the list -> only things in the list are to be added.
		 * Then iterate over them. You might end up checking more than if you only did bottom layer, but more accuracy.
		 * Possibly even less total rendered blocks. CPU performance is low, GPU is high. Nada good.
		 */
		for(BlockInstance b : r.getFrustumTestBlocks()){
			if(blockInFrustum(b, true)){
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param b Block to test
	 * @param considerY Whether or not to consider the Y-position.
	 * @return Returns <tt>true</tt> if the block is in the frustum.
	 */
	public boolean blockInFrustum(BlockInstance b, boolean considerY){
		Vector3f p = b.getPosition().toVector3f();
		p = Vector3f.sub(p, cameraPos, null);
		
		float pcz = Vector3f.dot(p, z);
		if (Math.abs(pcz) > farClip || Math.abs(pcz) < nearClip) return false;

		float pcy = Vector3f.dot(p, y);
		float aux = pcz * tFOV;
	 	if (considerY && (pcy - 1 > aux || pcy + 1 < -aux)) return false;

	 	float pcx = Vector3f.dot(p, x);
	 	aux *= ratio;
	 	if ((pcx - 1 > aux || pcx + 1 < -aux)) return false;

		return true;
	}
	
	/**
	 * @param b Block to test
	 * @return Returns <tt>true</tt> if the block is in the frustum.
	 * @see #blockInFrustum(BlockInstance)
	 */
	public boolean BlockInFrustum(BlockInstance b){
		return blockInFrustum(b, true);
	}
	
}
