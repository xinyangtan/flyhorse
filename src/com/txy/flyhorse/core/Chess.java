package com.txy.flyhorse.core;

public class Chess {

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public static final int FLY_HORSE = 1;
	public static final int GENERAL_HORSE = 2;
	public static final int RED_TEAM = 1;
	public static final int GREEN_TEAM = 2;

	private int x = 0; // 在棋盘中x的位置
	private int y = 0; // 在棋盘中y的位置
	private int type = Chess.GENERAL_HORSE; // 是否是飞马
	private int team; // 所属于队伍
	private boolean selected = false;  // 是否被选中
	private boolean death = false;
	private boolean inBoard = false; // 飞马特有属性，表示飞马是否上场
	
	public Chess(int x, int y, int type, int team) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.team = team;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public boolean isDeath() {
		return death;
	}

	public void setDeath(boolean death) {
		this.death = death;
	}

	public boolean isInBoard() {
		return inBoard;
	}

	public void setInBoard(boolean inBoard) {
		this.inBoard = inBoard;
	}

}
