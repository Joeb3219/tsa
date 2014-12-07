package com.charredsoftware.three.entity;

import org.lwjgl.input.Mouse;

import com.charredsoftware.three.Main;
import com.charredsoftware.three.Sound;
import com.charredsoftware.three.world.Position;

public class Bow {

	public float drawBackTime = 0f;
	public static final float maxDrawBackTime = 3f;
	public static final int maxArrows = 25;
	public int arrows = 25;
	
	public Bow(){
		
	}
	
	public void update(){
		if(arrows > maxArrows) arrows = maxArrows;
		if(Mouse.isButtonDown(1)) pullingBack();
		else if(drawBackTime > 0) release();
	}
	
	private void pullingBack(){
		if(arrows <= 0) return;
		drawBackTime += (1f / Main.DESIRED_TPS);
		if(drawBackTime > maxDrawBackTime) drawBackTime = maxDrawBackTime;
	}
	
	private void release(){
		arrows --;
		Position player = Main.getInstance().player.getPosition();
		Arrow a = new Arrow(Main.getInstance().player.world, new Position(player.x, player.y + 1, player.z), drawBackTime);
		drawBackTime = 0f;
		Sound.BOW_SHOT.playSfx();
	}
	
}
