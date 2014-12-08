package com.charredsoftware.tsa;

/**
 * TextPopup class. Holds data to be displayed on the HUDTextPopups
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 7, 2014
 */

public class TextPopup {

	public float displayTick = 0;
	public String text;
	
	/**
	 * Creates a new TextPopup
	 * @param text String to display
	 */
	public TextPopup(String text){
		this.text = text;
	}
	
	/**
	 * Update the text popup.
	 */
	public void update(){
		displayTick ++;
	}
	
	/**
	 * Render the text.
	 * @param x X-position
	 * @param y Y-position
	 */
	public void render(float x, float y){
		Main.getInstance().font.drawString(x, y, text);
	}
	
}
