package com.charredsoftware.tsa.entity;

public enum MobType {

	GENERIC(-1), SPINNER(0), STALKER(1), HENCHMAN(2), WORKER(3), SPUTNIK(4),
	
	
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
