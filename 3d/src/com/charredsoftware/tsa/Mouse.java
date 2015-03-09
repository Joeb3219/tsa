package com.charredsoftware.tsa;

/**
 * Mouse class.
 * A helper class that overrides the LWJGL Mouse class. Used to ensure that mouse clicks are registered on the laptops without physical buttons.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since March 2, 2015
 */

public class Mouse{

	private static boolean button_left = false, button_right = false;
	
	/**
	 * @param button Which button to consider
	 * @return Returns <tt>true</tt> if the identified button is being held down.
	 */
	public static boolean isButtonDown(int button){
		if(button == 0) return button_left;
		if(button == 1) return button_right;
		return false;
	}
	
	/**
	 * Polls the Mouse event log.
	 */
	public static void update(){
		while(org.lwjgl.input.Mouse.next()){
			if(org.lwjgl.input.Mouse.getEventButton() == 0) button_left = org.lwjgl.input.Mouse.getEventButtonState();
			if(org.lwjgl.input.Mouse.getEventButton() == 1) button_right = org.lwjgl.input.Mouse.getEventButtonState();
		}
	}
	
	/**
	 * @return Returns the x position of the mouse.
	 */
	public static float getX(){
		return org.lwjgl.input.Mouse.getX();
	}
	
	/**
	 * @return Returns the y-position of the mouse.
	 */
	public static float getY(){
		return org.lwjgl.input.Mouse.getY();
	}
	
	/**
	 * Sets the mouse to grab the screen or not.
	 * @param state Whether or not to grab.
	 */
	public static void setGrabbed(boolean state){
		org.lwjgl.input.Mouse.setGrabbed(state);
	}
	
	/**
	 * @return Returns the change in mouse x-position since the last check.
	 */
	public static float getDX(){
		return org.lwjgl.input.Mouse.getDX();
	}

	/**
	 * @return Returns the change in mouse y-position since the last check.
	 */
	public static float getDY(){
		return org.lwjgl.input.Mouse.getDY();
	}
	
	/**
	 * @return Returns the change in d-wheel position.
	 */
	public static float getDWheel(){
		if(org.lwjgl.input.Mouse.hasWheel()) return org.lwjgl.input.Mouse.getDWheel();
		return 0;
	}
	
	/**
	 * Sets the cursor's position.
	 * @param x X-position
	 * @param y Y-position
	 */
	public static void setCursorPosition(int x, int y){
		org.lwjgl.input.Mouse.setCursorPosition(x, y);
	}

}
