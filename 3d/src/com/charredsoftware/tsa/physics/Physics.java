package com.charredsoftware.tsa.physics;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.world.Position;

/**
 * Static helper class for Physics calculations.
 * Most calculations (damage, force, etc.) do not account for exterior forces (gravity, friction, etc)
 * Somewhat reality-based physics, but mostly modified to make game logic work.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since November 28, 2014
 */

public class Physics {

	/** DOWNWARD_ACCELERATION - {@value} Acceleration due to gravity, */
	public static final float DOWNWARD_ACCELERATION = -9.8f;

	/**
	 * @param initialVelocity Initial velocity in m/s
	 * @param acceleration Acceleration in m/s^2
	 * @param time Time of the event.
	 * @return Returns the final velocity.
	 */
	public static float calculateFinalVelocity(float initialVelocity, float acceleration, float time){
		return initialVelocity + acceleration * time;
	}
	
	/**
	 * @param initialVelocity Initial velocity in m/s
	 * @param time Time of the event.
	 * @param acceleration Acceleration in m/s^2
	 * @return Returns the change in position.
	 */
	public static float calculateDX(float initialVelocity, float time, float acceleration){
		return (float) (initialVelocity * time + 0.5f * acceleration * Math.pow(time, 2));
	}
	
	/**
	 * @param initialVelocity Initial velocity in m/s
	 * @param acceleration Acceleration in m/s^2
	 * @param distance Change in distance.
	 * @return Returns the final velocity.
	 */
	public static float calculateFinalVelocityWDistance(float initialVelocity, float acceleration, float distance){
		return (float) Math.sqrt( (Math.pow(initialVelocity, 2) + 2 * acceleration * distance) );
	}
	
	/**
	 * @param mass Mass of the falling object.
	 * @param acceleration Acceleration of the falling object, in m/s^2
	 * @return Returns the damage that the object feels.
	 */
	public static float calculateDamage(float mass, float acceleration){
		float force = -1f * calculateForce(mass, acceleration);
		if(force <= calculateForce(mass, 9.8f) + 15) return 0; //If within 15N of gravity, no damage.
		return (float) Math.pow(force / calculateForce(mass, 9.8f), 2);
	}
	
	/**
	 * @param velocity velocity of the falling object, in m/s
	 * @return Returns the damage that the object feels.
	 */
	public static float calculateDamage(float velocity){
		float maxVelocityWithoutDamage = 0.9f;
		velocity = (float) Math.abs(velocity);
		if(velocity <= maxVelocityWithoutDamage) return 0;
		return (float) Math.pow(velocity / maxVelocityWithoutDamage, 2) * 0.6f + velocity;
	}
	
	/**
	 * @param mass Mass of the object.
	 * @param acceleration Acceleration of the object in m/s^2
	 * @return Returns the force felt by the object.
	 */
	public static float calculateForce(float mass, float acceleration){
		return mass * acceleration;
	}

	/**
	 * @param a Position a
	 * @param b Position b
	 * @return Returns the distance between a and b.
	 */
	public static float getDistance(Position a, Position b){
		float dX = b.x - a.x;
		float dY = b.y - a.y;
		float dZ = b.z - a.z;
		return (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2) + Math.pow(dZ, 2));
	}
	
	/**
	 * @param a Position a
	 * @param b Position b
	 * @return Returns the 2D angle between a and b.
	 */
	public static float calculate2DAngle(Position a, Position b){
		float angle = (float) Math.atan2(a.z - b.z, a.x - b.x);
		
		angle = (float) Math.toDegrees(angle);
		if(angle < 0) angle += 360;
		
		return angle;
	}
	
}
