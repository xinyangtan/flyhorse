package com.txy.flyhorse.core;

public interface GameInterface {
	public void moveChess();
	public void killChess(int team, int generalN, int flyN);
	public void gameWin(int winTeam);
}
