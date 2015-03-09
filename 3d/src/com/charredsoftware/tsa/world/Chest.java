package com.charredsoftware.tsa.world;

/**
 * Chest class. Holds items that the player can find.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 7, 2014
 */

public class Chest extends BlockInstance{

	public int arrows, coins;
	public boolean exists = true;
	
	/**
	 * Creates a new Chest
	 * @param x X-position
	 * @param y Y-position
	 * @param z Z-position
	 * @param json Json data, containing arrows and coin count.
	 */
	public Chest(float x, float y, float z, String json) {
		super(Block.chest, x, y, z);
		this.initJson = json;
		generateChestContents();
	}
	
	/**
	 * @return Returns a string containing the chest's special JSON.
	 */
	public String getSpecialJson(){
		return generateChestJson();
	}
	
	/**
	 * @return Returns a string consisting of ARROW and COIN in json.
	 */
	public String generateChestJson(){
		String json = "{";
		json += "\"arrows\":\"" + arrows + "\",";
		json += "\"coins\":\"" + coins + "\"" + "\",";
		json += "\"facing\":\"" + facing + "\"";
		return json + "}";
	}

	/**
	 * Sets arrow and coin count to data in json.
	 */
	private void generateChestContents(){
		if(!initJson.contains("{")) return;
		while(initJson.charAt(0) == '{') initJson = initJson.substring(1, initJson.length() - 1);
		String[] sections = initJson.split(",");
		if(sections.length != 3) return;
		
		arrows = Integer.parseInt(sections[0].split(":")[1].replace("\"", ""));
		coins = Integer.parseInt(sections[1].split(":")[1].replace("\"", ""));
		facing = Float.parseFloat(sections[2].split(":")[1].replace("\"", ""));
	}
	
}
