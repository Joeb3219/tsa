package com.charredsoftware.three;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.physics.Physics;
import com.charredsoftware.three.world.BlockInstance;
import com.charredsoftware.three.world.Position;
import com.charredsoftware.three.world.Region;

public class Frustum {

	public float nWidth, nHeight, ratio, fov, tFOV, farClip, nearClip;
	public Vector3f cameraPos, x, y, z;
	private static Frustum _INSTANCE = null;

	private Frustum(){
		
	}
	
	public static Frustum getInstance(){
		if(_INSTANCE == null) _INSTANCE = new Frustum();;
		return _INSTANCE;
	}
	
	public void calculateFrustum(float fov, float ratio, float nearClip, float farClip){
		this.nearClip = nearClip;
		this.farClip = farClip;
		this.ratio = ratio;
		this.fov = fov;
		this.tFOV = (float) Math.tan(Math.PI / 180.0 * fov * 0.5f);
		nHeight = tFOV * nearClip;
		nWidth = nHeight * ratio;
	}
	
	public void setCamera(Vector3f cameraPos, Vector3f looking, Vector3f up){
		this.cameraPos = new Vector3f(cameraPos);
		
		z = Vector3f.sub(looking, cameraPos, null);
		z.normalise();
		
		x = Vector3f.cross(z, up, null);
		x.normalise();
		
		y = new Vector3f(0, Vector3f.dot(x, z), 0);
	}
	
	public boolean regionInFrustum(Region r){
		//if(Physics.getDistance(Main.getInstance().player.getPosition(), new Position(r.x * Region._SIZE, Main.getInstance().player.y, r.z * Region._SIZE)) > Main.getInstance().camera.farClip) return false;
		for(BlockInstance b : r.getFrustumTestBlocks(Main.getInstance().player.y)){
			if(blockInFrustum(b, false)) return true;
		}
		return false;
	}
	
	public boolean blockInFrustum(BlockInstance b, boolean considerY){
		Vector3f p = b.getPosition().toVector3f();
		p = Vector3f.sub(p, cameraPos, null);
		
		float pcz = Vector3f.dot(p, z);
		if (Math.abs(pcz) > farClip || Math.abs(pcz) < nearClip) return false;

		float pcy = Vector3f.dot(p, y);
		float aux = pcz * tFOV;
	 	if (considerY && (pcy - 1 > aux || pcy + 1< -aux)) return false;

	 	float pcx = Vector3f.dot(p, x);
	 	aux *= ratio;
	 	if (pcx - 1 > aux || pcx + 1 < -aux) return false;

		return true;
	}
	
	public boolean BlockInFrustum(BlockInstance b){
		return blockInFrustum(b, true);
	}
	
}
