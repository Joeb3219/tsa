package com.charredsoftware.tsa.entity;

import org.lwjgl.input.Mouse;

import com.charredsoftware.tsa.Main;
import com.charredsoftware.tsa.Sound;
import com.charredsoftware.tsa.world.Position;

/**
 * Bow class.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 6, 2014
 */

public class Bow {

	public float drawBackTime = 0f;
	public static final float maxDrawBackTime = 3f;
	public static final int default_maxArrows = 25;
	public int maxArrows = 0;
	public int arrows = 25;
	public boolean UPGRADE_LARGER_RADIUS = false, UPGRADE_MORE_DAMAGE = false, UPGRADE_CONTINUOUS_DAMAGE, UPGRADE_FURTHER_SHOTS = false, UPGRADE_INCREASED_ACCURACY = false;
	public boolean UPGRADE_LARGER_RADIUS_APPLIED = false; //A flag used in the camera class. Will be set to true if gl calls for glLight have been updated for radius.
	
	/**
	 * Creates a new Bow.
	 */
	public Bow(){
		maxArrows = default_maxArrows;
	}
	
	/**
	 * Creates a new Bow.
	 * @param arrows Max number of arrows the bow holds.
	 */
	public Bow(int arrows){
		maxArrows = arrows;
	}
	
	/**
	 * Update method. Calls pullingBack() and release()
	 * @see #pullingBack()
	 * @see #release()
	 */
	public void update(){
		if(arrows > maxArrows) arrows = maxArrows;
		if(Mouse.isButtonDown(1)) pullingBack();
		else if(drawBackTime > 0) release();
	}
	
	/**
	 * Increases drawBackTime so long as pulling back.
	 */
	private void pullingBack(){
		if(arrows <= 0){
			Sound.OUT_OF_ARROWS.playSfxIfNotPlaying();
			return;
		}
		if(drawBackTime == 0) Sound.BOW_PULLBACK.playSfxIfNotPlaying();
		drawBackTime += (1f / Main.DESIRED_TPS);
		if(drawBackTime > maxDrawBackTime) drawBackTime = maxDrawBackTime;
	}
	
	/**
	 * Creates a new arrow with time drawBackTime
	 */
	private void release(){
		Sound.BOW_PULLBACK.audio.stop();
		arrows --;
		Position player = Main.getInstance().player.getPosition();
		new Arrow(Main.getInstance().player, Main.getInstance().player.world, new Position(player.x, player.y + 1, player.z), drawBackTime, Main.getInstance().camera.rx, Main.getInstance().camera.ry);
		drawBackTime = 0f;
		Sound.BOW_SHOT.playSfxIfNotPlaying();
	}
	
}
