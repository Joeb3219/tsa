package com.charredsoftware.tsa.gui;

/**
 * DialogAuthor enum.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 31, 2014
 */

public enum DialogAuthor {

	PRESIDENT(0, "MR. PRESIDENT"), SPUTNIK(1, "DR. SPUTNIK"), PLAYER(2, "ME"), 
	
	;
	public String name;
	public int id;
	
	private DialogAuthor(int id, String name){
		this.name = name;
		this.id = id;
	}
	
}
