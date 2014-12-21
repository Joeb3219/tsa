package com.charredsoftware.tsa.entity;

public enum MobType {

	GENERIC(-1), SPINNER(0),
	
	
	;
	
	public int id;
	
	private MobType(int id){
		this.id = id;
	}
	
	public static MobType fromString(String string){
		int i = Integer.parseInt(string);
		for(MobType m : values()){
			if(m.id == i) return m;
		}
		return GENERIC;
	}
	
}
