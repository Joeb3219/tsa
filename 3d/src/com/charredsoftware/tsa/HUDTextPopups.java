package com.charredsoftware.tsa;

import java.util.ArrayList;

import org.newdawn.slick.Font;

/**
 * HUDTextPopups class. Displays information to the player in form of TextPopups.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 7, 2014
 */

public class HUDTextPopups {

	public ArrayList<TextPopup> popups = new ArrayList<TextPopup>();
	/** Value - {@value} Number of ticks until a popup stops displaying. */
	public static final float POPUP_LIFESPAN = Main.DESIRED_TPS * 4, POPUP_FADE_TICKS = Main.DESIRED_TPS * 1;
	public float x, y;
	
	/**
	 * Creates new HUDTextPopups object.
	 * @param x X-position to start drawing at.
	 * @param y Y-position to start drawing at.
	 */
	public HUDTextPopups(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Updates all popups.
	 */
	public void update(){
		ArrayList<TextPopup> forRemoval = new ArrayList<TextPopup>();
		for(TextPopup t : popups){
			if(t.displayTick >= POPUP_LIFESPAN + POPUP_FADE_TICKS){
				forRemoval.add(t);
				continue;
			}
			t.update();
		}
		popups.removeAll(forRemoval);
	}
	
	/**
	 * Renders all available popups.
	 */
	public void render(){
		Font font = Main.getInstance().font;
		float ySpace = font.getHeight("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		float current = 0;
		for(TextPopup t : popups){
			float alpha = 1f;
			if(t.displayTick >= POPUP_LIFESPAN){
				float difference = t.displayTick - POPUP_LIFESPAN;
				alpha = 1f - ((1f * difference) / POPUP_FADE_TICKS);
			}
			t.render(x, y + (ySpace * current), alpha);
			current ++;
		}
	}
	
}
