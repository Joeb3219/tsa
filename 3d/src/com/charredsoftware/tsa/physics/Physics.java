package com.charredsoftware.tsa.physics;

import com.charredsoftware.tsa.world.Position;

public class Physics {

	/*
	 * Static helper class for Physics calculations.
	 * Most calculations (damage, force, etc.) do not account for exterior forces (gravity, friction, etc)
	 * Somewhat reality-based physics, but mostly modified to make game logic work.
	 */
	
	public static final float DOWNWARD_ACCELERATION = -9.8f;
	
	public static float calculateFinalVelocity(float initialVelocity, float acceleration, float time){
		return initialVelocity + acceleration * time;
	}
	
	public static float calculateDX(float initialVelocity, float time, float acceleration){
		return (float) (initialVelocity * time + 0.5f * acceleration * Math.pow(time, 2));
	}
	
	public static float calculateFinalVelocityWDistance(float initialVelocity, float acceleration, float distance){
		return (float) Math.sqrt( (Math.pow(initialVelocity, 2) + 2 * acceleration * distance) );
	}
	
	public static float calculateDamage(float mass, float acceleration){
		float force = -1f * calculateForce(mass, acceleration);
		if(force <= calculateForce(mass, 9.8f) + 15) return 0; //If within 15N of gravity, no damage.
		return (float) Math.pow(force / calculateForce(mass, 9.8f), 2);
	}
	
	public static float calculateDamage(float velocity){
		float maxVelocityWithoutDamage = 0.9f;
		velocity = (float) Math.abs(velocity);
		if(velocity <= maxVelocityWithoutDamage) return 0;
		return (float) Math.pow(velocity / maxVelocityWithoutDamage, 2) * 0.6f + velocity;
	}
	
	public static float calculateForce(float mass, float acceleration){
		return mass * acceleration;
	}

	public static float getDistance(Position a, Position b){
		float dX = b.x - a.x;
		float dY = b.y - a.y;
		float dZ = b.z - a.z;
		return (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2) + Math.pow(dZ, 2));
	}
	
}
