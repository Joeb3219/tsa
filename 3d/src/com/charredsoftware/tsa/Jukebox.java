package com.charredsoftware.tsa;


/**
 * Jukebox class.
 * Plays background music during the game.
 * All authors are as below specified (joeb3219) unless otherwise specified above method.
 * @author joeb3219
 * @since February 23, 2015
 */

public class Jukebox {

	public static Sound _GAME_MUSIC = new Sound("static_motion.wav");
	public static Sound _CREDITS_SONG = new Sound("carefree_credits.wav");
	public static Sound _MESMERIZE = new Sound("mesmerize.wav");
	private Sound currentlyPlaying = null;
	private static Jukebox _INSTANCE = null;
	private float position = 0;
	
	private Jukebox(){
		
	}
	
	/**
	 * @return Returns an instance of Jukebox.
	 */
	public static Jukebox getInstance(){
		if(_INSTANCE == null) _INSTANCE = new Jukebox();
		return _INSTANCE;
	}
	
	/**
	 * @return Returns the song that is playing.
	 */
	public Sound getPlaying(){
		return currentlyPlaying;
	}
	
	/**
	 * Plays a song
	 * @param sound Song to play
	 * @param loop Whether or not to loop the song.
	 */
	public void playSong(Sound sound, boolean loop){
		position = 0;
		if(currentlyPlaying != null) currentlyPlaying.audio.stop();
		currentlyPlaying = sound;
		currentlyPlaying.audio.playAsSoundEffect(1f, Main.getInstance().controller.musicVolume, loop);
	}
	
	/**
	 * Changes the volume of the jukebox per the Controller's current volume.
	 */
	public void changeVolume(){
		if(currentlyPlaying == null) return;
		if(currentlyPlaying.audio.isPlaying()){
			position = currentlyPlaying.audio.getPosition();
			currentlyPlaying.audio.stop();
			currentlyPlaying.audio.playAsSoundEffect(1f, Main.getInstance().controller.musicVolume, true);
			currentlyPlaying.audio.setPosition(position);
		}
	}
	
	/**
	 * Pauses the current song if one is playing.
	 */
	public void pause(){
		if(currentlyPlaying == null) return;
		position = currentlyPlaying.audio.getPosition();
		currentlyPlaying.audio.stop();
	}
	
	/**
	 * Resumes the current song if one was playing.
	 */
	public void resume(){
		if(currentlyPlaying == null) return;
		currentlyPlaying.audio.playAsSoundEffect(1f, Main.getInstance().controller.musicVolume, true);
		currentlyPlaying.audio.setPosition(position);
	}
	
}
