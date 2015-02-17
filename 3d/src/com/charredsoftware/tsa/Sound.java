package com.charredsoftware.tsa;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;

import com.charredsoftware.tsa.util.FileUtilities;

/**
 * Sound class.
 * Houses all of the Sounds used in the game.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since December 1, 2014
 */

public class Sound {

	public static Sound HIT_GROUND = new Sound("hit_ground.wav");
	public static Sound DAMAGE_GROUND = new Sound("hurt_fall_damage.wav");
	public static Sound WALKING = new Sound("walking.wav");
	public static Sound BOW_SHOT = new Sound("bow_shot.wav");
	public static Sound ARROW_HIT = new Sound("arrow_hit.wav");
	public static Sound ARROW_HIT_MOB = new Sound("arrow_hit_mob.wav");
	public static Sound BUTTON_CLICKED = new Sound("arrow_hit.wav");
	public static Sound BOW_PULLBACK = new Sound("bow_pullback.wav");
	public static Sound OUT_OF_ARROWS = new Sound("out_of_arrows.wav");
	public static Sound OPENING_CHEST = new Sound("chest_open.wav");
	public static Sound PUZZLE_SOLVED = new Sound("chest_open.wav");
	
	private String path;
	public Audio audio;
	
	public Sound(String path){
		this.path = path;
		loadSound();
	}
	
	public void playSfxIfNotPlaying(){
		if(audio.isPlaying()) return;
		playSfx();
	}
	
	public void playSfx(){
		audio.playAsSoundEffect(1f, Main.getInstance().controller.soundVolume, false);
	}
	
	public void playSfx(float pitch){
		audio.playAsSoundEffect(pitch, Main.getInstance().controller.soundVolume, false);
	}
	
	
	public void playSfx(float x, float y, float z){
		audio.playAsSoundEffect(1f, 1f, false, -x, -y, -z);
	}
	
	private void loadSound(){
		if(path.contains("wav"))
			try {
				audio = AudioLoader.getAudio("WAV", new BufferedInputStream(new FileInputStream(FileUtilities.getBaseDirectory() + FileUtilities.soundsPath + path)));
			} catch (Exception e) {new CrashReport(e);}
	}
	
}
