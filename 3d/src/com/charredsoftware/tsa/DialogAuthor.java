package com.charredsoftware.tsa;

public enum DialogAuthor {

	PERSON("Bob"),
	
	;
	public String name;
	
	private DialogAuthor(String name){
		this.name = name;
	}
	
}
