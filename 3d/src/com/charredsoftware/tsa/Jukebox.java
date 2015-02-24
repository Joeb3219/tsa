package com.charredsoftware.tsa;


public class Jukebox {

	public static Sound _GAME_MUSIC = new Sound("static_motion.wav");
	public static Sound _CREDITS_SONG = new Sound("carefree_credits.wav");
	public static Sound _MESMERIZE = new Sound("mesmerize.wav");
	private Sound currentlyPlaying = null;
	private static Jukebox _INSTANCE = null;
	private float position = 0;
	
	private Jukebox(){
		
	}
	
	public static Jukebox getInstance(){
		if(_INSTANCE == null) _INSTANCE = new Jukebox();
		return _INSTANCE;
	}
	
	public Sound getPlaying(){
		return currentlyPlaying;
	}
	
	public void playSong(Sound sound, boolean loop){
		position = 0;
		if(currentlyPlaying != null) currentlyPlaying.audio.stop();
		currentlyPlaying = sound;
		currentlyPlaying.audio.playAsSoundEffect(1f, Main.getInstance().controller.musicVolume, loop);
	}
	
	public void changeVolume(){
		if(currentlyPlaying == null) return;
		if(currentlyPlaying.audio.isPlaying()){
			position = currentlyPlaying.audio.getPosition();
			currentlyPlaying.audio.stop();
			currentlyPlaying.audio.playAsSoundEffect(1f, Main.getInstance().controller.musicVolume, true);
			currentlyPlaying.audio.setPosition(position);
		}
	}
	
	public void pause(){
		if(currentlyPlaying == null) return;
		position = currentlyPlaying.audio.getPosition();
		currentlyPlaying.audio.stop();
	}
	
	public void resume(){
		if(currentlyPlaying == null) return;
		currentlyPlaying.audio.playAsSoundEffect(1f, Main.getInstance().controller.musicVolume, true);
		currentlyPlaying.audio.setPosition(position);
	}
	
}
