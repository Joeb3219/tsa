package com.charredsoftware.three.computer;

public enum Hardware {

	//Processors
	PROCESSOR_S1("Single Core 1Ghz", HardwareType.PROCESSOR, 1, 40),
	PROCESSOR_S2("Single Core 2Ghz", HardwareType.PROCESSOR, 2, 50),
	PROCESSOR_D1("Dual Core 1.5Ghz", HardwareType.PROCESSOR, 5, 75),
	PROCESSOR_D2("Dual Core 2Ghz", HardwareType.PROCESSOR, 8, 85),
	PROCESSOR_D3("Dual Core 4Ghz", HardwareType.PROCESSOR, 16, 90),
	PROCESSOR_Q1("Quad Core 3Ghz", HardwareType.PROCESSOR, 32, 125),
	PROCESSOR_Q2("Quad Core 4Ghz", HardwareType.PROCESSOR, 40, 140),
	PROCESSOR_Q3("Quad Core 4.5Ghz", HardwareType.PROCESSOR, 46, 150),
	PROCESSOR_Q4("Quad Core 5.5Ghz", HardwareType.PROCESSOR, 60, 250),

	//Harddrives
	HARDDRIVE_1("1GB Harddrive", HardwareType.HARDDRIVE, 1, 5),
	HARDDRIVE_2("2GB Harddrive", HardwareType.HARDDRIVE, 2, 7),
	HARDDRIVE_3("10GB Harddrive", HardwareType.HARDDRIVE, 10, 10),
	HARDDRIVE_4("100GB Harddrive", HardwareType.HARDDRIVE, 100, 15),
	
	//Memory
	MEMORY_D2_1("DDR2 1GB", HardwareType.MEMORY, 1, 5),
	MEMORY_D2_2("DDR2 2GB", HardwareType.MEMORY, 2, 8),
	MEMORY_D2_3("DDR2 4GB", HardwareType.MEMORY, 4, 12),
	MEMORY_D3_1("DDR3 1GB", HardwareType.MEMORY, 6, 15),
	MEMORY_D3_2("DDR3 2GB", HardwareType.MEMORY, 8, 10),
	MEMORY_D3_3("DDR3 4GB", HardwareType.MEMORY, 16, 15),
	MEMORY_D3_4("DDR3 8GB", HardwareType.MEMORY, 32, 18),
	MEMORY_D3_5("DDR3 16GB", HardwareType.MEMORY, 64, 25),
	
	//Graphics Cards
	GRAPHICS_1("Radio 110 Graphics Card", HardwareType.GRAPHICS_CARD, 10, 40),
	GRAPHICS_2("Radio 120 Graphics Card", HardwareType.GRAPHICS_CARD, 20, 60),
	GRAPHICS_3("Radio 250 Graphics Card", HardwareType.GRAPHICS_CARD, 50, 70),
	GRAPHICS_4("Radio 2100 Graphics Card", HardwareType.GRAPHICS_CARD, 100, 90),
	GRAPHICS_5("Radio 380 Graphics Card", HardwareType.GRAPHICS_CARD, 80, 80),
	GRAPHICS_6("Radio 3250 Graphics Card", HardwareType.GRAPHICS_CARD, 250, 110),
	GRAPHICS_7("Radio 4150 Graphics Card", HardwareType.GRAPHICS_CARD, 150, 100),
	GRAPHICS_8("Radio 4400 Graphics Card", HardwareType.GRAPHICS_CARD, 400, 150),
	GRAPHICS_9("Radio 5300 Graphics Card", HardwareType.GRAPHICS_CARD, 300, 130),
	GRAPHICS_10("Radio 5750 Graphics Card", HardwareType.GRAPHICS_CARD, 750, 250),
	
	//PSU's
	PSU_1("50W Power Supply", HardwareType.POWER_SUPPLY, 50, 0),
	PSU_2("10W Power Supply", HardwareType.POWER_SUPPLY, 100, 0),
	PSU_3("200W Power Supply", HardwareType.POWER_SUPPLY, 200, 0),
	PSU_4("350W Power Supply", HardwareType.POWER_SUPPLY, 350, 0),
	PSU_5("450W Power Supply", HardwareType.POWER_SUPPLY, 450, 0),
	PSU_6("500W Power Supply", HardwareType.POWER_SUPPLY, 500, 0),
	PSU_7("750W Power Supply", HardwareType.POWER_SUPPLY, 750, 0),
	;
	
	public String name;
	public int powerDraw, level; //Level denotes multiplier for speed/ability/etc >> Higher == better
	public HardwareType type;
	
	private Hardware(String name, HardwareType type, int level, int power){
		this.name = name;
		this.type = type;
		this.level = level;
		this.powerDraw = power;
	}
	
}
