package com.charredsoftware.tsa;

public enum GameState {

	LOADING, MENU, GAME,
	
	COMPUTER;
	
	public SubState subState = SubState.NORMAL;

	
	//resets gameState && subGameState -> removes excess code from caller.
	public GameState resetGameState(GameState gameState){
		gameState.subState = SubState.NORMAL;
		return gameState;
	}
	
}
