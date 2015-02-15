package com.charredsoftware.tsa.gui;

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
