package com.charredsoftware.tsa.world;

import org.lwjgl.util.vector.Vector3f;

/**
 * Position class. Stores x, y, and z values.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class Position {

	public float x, y, z;
	
	/**
	 * Creates a new Position at 0, 0, 0.
	 */
	public Position(){
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	/**
	 * Creates a new Position.
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 */
	public Position(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return Returns a String of the x/y/z values
	 */
	public String toString(){
		return "[x/y/z] {" + x + "/" + y + "/" + z + "}";
	}
	
	/**
	 * Converts decimal to whole number.
	 */
	public void normalizeCoords(){
		if(this.x > 0) this.x = (float) ((int) (x + 0.5));
		else this.x = (float) ((int) (x - 0.5));
		this.y = (float) ((int) y);
		if(this.z > 0) this.z = (float) ((int) (z + 0.5));
		else this.z = (float) ((int) (z - 0.5));
	}
	
	/**
	 * @return Returns a hashcode of x + y + z.
	 */
	public int hashCode(){
		return (int) (x + y + z);
	}
	
	/**
	 * @returns Returns <tt>true</tt> if the two position are equal.
	 */
	public boolean equals(Object o){
		if(!(o instanceof Position)) return false;
		Position p = (Position) o;
		if(p.x == x && p.y == y && p.z == z) return true;
		return false;
	}
	
	/**
	 * Prints the location to console
	 */
	public void printLocation(){
		System.out.println(x + "," + y + "," +  z);
	}
	
	/**
	 * Converts the position to a <code>Vector3f</code>
	 * @return Returns a <code>Vector3f</code> with the same x/y/z coordinates as the Position.
	 */
	public Vector3f toVector3f(){
		return new Vector3f(x, y, z);
	}
	
}
