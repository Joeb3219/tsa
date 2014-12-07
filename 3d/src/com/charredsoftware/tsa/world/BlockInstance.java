package com.charredsoftware.tsa.world;

/**
 * BlockInstance class. Wraps Block. Gives position, special values.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since October 8, 2014
 */

public class BlockInstance {

	public Block base;
	public float x, y, z = 0;
	public float special = -1;
	public String initJson = ""; //Used to give json data on load.
	
	/**
	 * Creates a BlockInstance
	 * @param base Block that the Instance will be representing.
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 */
	public BlockInstance(Block base, float x, float y, float z){
		this.base = base;
		Position p = new Position(x, y, z);
		p.normalizeCoords();
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}
	
	/**
	 * @return Returns the block's position.
	 */
	public Position getPosition(){
		return new Position(x, y, z);
	}
	
	/**
	 * Draws the block.
	 * @see draw()
	 * @param alpha Alpha value to draw the block with.
	 */
	public void draw(int alpha){
		base.draw(x, y, z, alpha);
	}
	
	/**
	 * Draws the block at its position.
	 * @see Block.draw(x, y, z)
	 */
	public void draw(){
		base.draw(x, y, z);
	}
	
	/**
	 * @return Returns Json data
	 */
	public String getSpecialJson(){
		return "";
	}
	
	/**
	 * @return Returns <tt>true</tt> if same base & position
	 */
	public boolean equals(Object o){
		if(!(o instanceof BlockInstance)) return false;
		BlockInstance b = ((BlockInstance) o);
		if(b.base != base) return false;
		if(b.x != x || b.y != y || b.z != z) return false;
		return true;
	}
	
}
