package com.charredsoftware.tsa;


public class Mouse{

	private static boolean button_left = false, button_right = false;
	
	public static boolean isButtonDown(int button){
		if(button == 0) return button_left;
		if(button == 1) return button_right;
		return false;
	}
	
	public static void update(){
		while(org.lwjgl.input.Mouse.next()){
			if(org.lwjgl.input.Mouse.getEventButton() == 0) button_left = org.lwjgl.input.Mouse.getEventButtonState();
			if(org.lwjgl.input.Mouse.getEventButton() == 1) button_right = org.lwjgl.input.Mouse.getEventButtonState();
		}
	}
	
	public static float getX(){
		return org.lwjgl.input.Mouse.getX();
	}
	
	public static float getY(){
		return org.lwjgl.input.Mouse.getY();
	}
	
	public static void setGrabbed(boolean state){
		org.lwjgl.input.Mouse.setGrabbed(state);
	}
	
	public static float getDX(){
		return org.lwjgl.input.Mouse.getDX();
	}

	public static float getDY(){
		return org.lwjgl.input.Mouse.getDY();
	}
	
	public static float getDWheel(){
		if(org.lwjgl.input.Mouse.hasWheel()) return org.lwjgl.input.Mouse.getDWheel();
		return 0;
	}
	
	public static void setCursorPosition(int x, int y){
		org.lwjgl.input.Mouse.setCursorPosition(x, y);
	}

}
