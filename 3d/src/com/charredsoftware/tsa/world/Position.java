package com.charredsoftware.tsa.world;

import org.lwjgl.util.vector.Vector3f;

public class Position {

	public float x, y, z;
	
	public Position(){
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Position(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString(){
		return "[x/y/z] {" + x + "/" + y + "/" + z + "}";
	}
	
	//Converts decimal to whole number.
	public void normalizeCoords(){
		if(this.x > 0) this.x = (float) ((int) (x + 0.5));
		else this.x = (float) ((int) (x - 0.5));
		this.y = (float) ((int) y);
		if(this.z > 0) this.z = (float) ((int) (z + 0.5));
		else this.z = (float) ((int) (z - 0.5));
	}
	
	public int hashCode(){
		return (int) (x + y + z);
	}
	
	public boolean equals(Object o){
		if(!(o instanceof Position)) return false;
		Position p = (Position) o;
		if(p.x == x && p.y == y && p.z == z) return true;
		return false;
	}
	
	public void printLocation(){
		System.out.println(x + "," + y + "," +  z);
	}
	
	public Vector3f toVector3f(){
		return new Vector3f(x, y, z);
	}
	
}
