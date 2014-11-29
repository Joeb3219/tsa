package com.charredsoftware.three.computer;

import java.util.ArrayList;

public class HardwareSet {

	public Hardware processor, memory, harddrive, powerSupply, graphicsCard;
	
	public HardwareSet(){
		processor = Hardware.PROCESSOR_D1;
		memory = Hardware.MEMORY_D2_1;
		harddrive = Hardware.HARDDRIVE_1;
		powerSupply = Hardware.PSU_1;
		graphicsCard = Hardware.GRAPHICS_1;
	}
	
	public HardwareSet(Hardware processor, Hardware memory, Hardware harddrive, Hardware powerSupply, Hardware graphics){
		this.processor = processor;
		this.memory = memory;
		this.harddrive = harddrive;
		this.powerSupply = powerSupply;
		this.graphicsCard = graphics;
	}
	
	public int getSystemScore(){
		return processor.level + memory.level + harddrive.level + powerSupply.level + graphicsCard.level;
	}
	
	public int getNeededPower(){
		return processor.powerDraw + memory.powerDraw + harddrive.powerDraw + graphicsCard.powerDraw;
	}
	
	public boolean hasEnoughWattage(){
		return powerSupply.level > getNeededPower();
	}
	
	public ArrayList<String> getInfo(){
		ArrayList<String> info = new ArrayList<String>();
		info.add("CPU: " + processor.name + " (draws " + processor.powerDraw + "W)");
		info.add("RAM: " + memory.name + " (draws " + memory.powerDraw + "W)");
		info.add("Harddrive: " + harddrive.name + " (draws " + harddrive.powerDraw + "W)");
		info.add("Graphics Card: " + graphicsCard.name + " (draws " + graphicsCard.powerDraw + "W)");
		info.add("Power Supply Unit: " + powerSupply.name);
		return info;
	}
	
	public void loadFromJson(String json){
		if(!json.contains("{")) return;
		while(json.charAt(0) == '{') json = json.substring(1, json.length() - 1);
		String[] sections = json.split(",");
		if(sections.length != 5) return;
		
		processor = Hardware.getById(Float.parseFloat(sections[0].split(":")[1].replace("\"", "")));
		memory = Hardware.getById(Float.parseFloat(sections[1].split(":")[1].replace("\"", "")));
		harddrive = Hardware.getById(Float.parseFloat(sections[2].split(":")[1].replace("\"", "")));
		powerSupply = Hardware.getById(Float.parseFloat(sections[3].split(":")[1].replace("\"", "")));
		graphicsCard = Hardware.getById(Float.parseFloat(sections[4].split(":")[1].replace("\"", "")));
	}
	
	public String getJson(){
		String response = "{";
		response += "\"PROCESSOR\":\"" + processor.id + "\",";
		response += "\"MEMORY\":\"" + memory.id + "\",";
		response += "\"HARDDRIVE\":\"" + harddrive.id + "\",";
		response += "\"POWERSUPPLY\":\"" + powerSupply.id + "\",";
		response += "\"GRAPHICS\":\"" + graphicsCard.id + "\",";
		response += "}";
		return response;
	}
	
}
