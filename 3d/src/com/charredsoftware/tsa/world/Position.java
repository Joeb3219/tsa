package com.charredsoftware.tsa.world;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector2f;
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
	 * @return Returns a String of the x/y/z values, but with all values as integers.
	 */
	public String toStringWithIntegers(){
		return "[x/y/z] {" + (int) x + "/" + (int) y + "/" + (int) z + "}";
	}
	
	/**
	 * @param s String to create position from
	 * @return Returns a position object with the coordinates in the string
	 */
	public static Position createPositionFromString(String s){
		String coords = s.split("\\{")[1].split("\\}")[0];
		return new Position(Float.parseFloat(coords.split("/")[0]),
				Float.parseFloat(coords.split("/")[1]),
				Float.parseFloat(coords.split("/")[2]));
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
	 * @return Returns <tt>true</tt> if the two position are equal.
	 */
	public boolean equals(Object o){
		if(!(o instanceof Position)) return false;
		Position p = (Position) o;
		if(p.x == x && p.y == y && p.z == z) return true;
		return false;
	}
	
	/**
	 * @return Returns a copy of the position.
	 */
	public Position clone(){
		return new Position(x, y, z);
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
	
	/**
	 * Converts the position to a <code>Vector2f</code>
	 * @return Returns a <code>Vector2f</code> with the same x/z coordinates as the Position.
	 */
	public Vector2f toVector2f(){
		return new Vector2f(x, z);
	}
	
	/**
	 * @param p Position to compare to
	 * @return Returns the distance between this position and position P.
	 */
	public float calculateDistance(Position p){
		float dX = x - p.x;
		float dY = y - p.y;
		float dZ = z - p.z;
		return (float) Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2) + Math.pow(dZ, 2));
	}
	
	public ArrayList<Position> getPositionsBetween(Position opposing){
		ArrayList<Position> positions = new ArrayList<Position>();
		float minX = Math.min(x, opposing.x);
		float maxX = Math.max(x, opposing.x);
		float minY = Math.min(y, opposing.y);
		float maxY = Math.max(y, opposing.y);
		float minZ = Math.min(z, opposing.z);
		float maxZ = Math.max(z, opposing.z);
		
		for(float xa = minX; xa < maxX; xa ++){
			for(float ya = minY; ya < maxY; ya ++){
				for(float za = minZ; za < maxZ; za ++){
					positions.add(new Position(xa, ya, za));
				}
			}
		}
		
		if(minX == maxX){
			for(float ya = minY; ya < maxY; ya ++){
				for(float za = minZ; za < maxZ; za ++){
					positions.add(new Position(minX, ya, za));
				}
			}
		}
		
		if(minZ == maxZ){
			for(float xa = minX; xa < maxX; xa ++){
				for(float ya = minY; ya < maxY; ya ++){
					positions.add(new Position(xa, ya, minZ));
				}
			}
		}
		
		if(minY == maxY){
			for(float xa = minX; xa < maxX; xa ++){
				for(float za = minZ; za < maxZ; za ++){
					positions.add(new Position(xa, minY, za));
				}
			}
		}
		
		return positions;
	}
	
}
