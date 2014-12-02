package com.charredsoftware.three;

import java.io.IOException;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;

import com.charredsoftware.three.util.FileUtilities;

public class Sound {

	public static Sound HIT_GROUND = new Sound("hit_ground.wav");
	
	private String path;
	private Audio audio;
	
	public Sound(String path){
		this.path = path;
		loadSound();
	}
	
	public void playSfx(){
		audio.playAsSoundEffect(1f, 1f, false);
	}
	
	public void playSfx(float x, float y, float z){
		audio.playAsSoundEffect(1f, 1f, false, -x, -y, -z);
	}
	
	private void loadSound(){
		if(path.contains("wav"))
			try {
				audio = AudioLoader.getAudio("WAV", ClassLoader.getSystemResourceAsStream(FileUtilities.soundsPath + path));
			} catch (IOException e) {new CrashReport(e);}
	}
	
}
