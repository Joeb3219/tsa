package com.charredsoftware.three;

import org.lwjgl.util.vector.Vector3f;

import com.charredsoftware.three.world.BlockInstance;

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
		this.tFOV = (float) Math.tan(Math.PI / 180.0 * fov);
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
	
	public boolean BlockInFrustum(BlockInstance b){
		Vector3f p = b.getPosition().toVector3f();
		Vector3f.sub(p, cameraPos, p);
		
		float pcz = Vector3f.dot(p, z);
		if (pcz > farClip || pcz < nearClip) return false;

		float pcy = Vector3f.dot(p, y);
		float aux = pcz * tFOV;
	 	if (pcy > aux || pcy < -aux) return false;

	 	float pcx = Vector3f.dot(p, x);
	 	aux = aux * ratio;
	 	if (pcx > aux || pcx < -aux) return false;

		return true;
	}
	
}
