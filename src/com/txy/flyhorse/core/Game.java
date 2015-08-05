package com.txy.flyhorse.core;

import android.util.Log;

/*
 * 飞马游戏
 */
public class Game {

	public void setGameInterface(GameInterface gameInterface) {
		this.gameInterface = gameInterface;
	}

	public Chess getFlyHorseRed() {
		return FlyHorseRed;
	}

	public void setFlyHorseRed(Chess flyHorseRed) {
		FlyHorseRed = flyHorseRed;
	}

	public Chess getFlyHorseGreen() {
		return FlyHorseGreen;
	}

	public void setFlyHorseGreen(Chess flyHorseGreen) {
		FlyHorseGreen = flyHorseGreen;
	}

	public int getScoreRed() {
		return scoreRed;
	}

	public int getScoreGreen() {
		return scoreGreen;
	}

	public GameInterface gameInterface;

	private Chess[][] board; // 棋盘
	public Chess FlyHorseRed;
	public Chess FlyHorseGreen;
	private int scoreRed = 0;
	private int scoreGreen = 0;

	public boolean gameOver = false;
	public int winTeam;
	
	public int killGeneralNum = 0;  // 单次移动杀子数量
	public int killFlyNum = 0;  // 单次移动杀子数量
	
	public Game() {
		initBoard();
	}

	/*
	 * 初始化棋盘
	 */
	private void initBoard() {
		if (this.board != null) {
			return;
		}
		this.FlyHorseRed = new Chess(-1, -1, Chess.FLY_HORSE, Chess.RED_TEAM);
		this.FlyHorseGreen = new Chess(-1, -1, Chess.FLY_HORSE,
				Chess.GREEN_TEAM);
		board = new Chess[6][4];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				 if (i == 0) {
				 board[i][j] = new Chess(j, i, Chess.GENERAL_HORSE,
				 Chess.RED_TEAM);
				 }
				 if (i == 1 && (j == 0 || j == 3)) {
				 board[i][j] = new Chess(j, i, Chess.GENERAL_HORSE,
				 Chess.RED_TEAM);
				 }

				if (i == 5) {
					board[i][j] = new Chess(j, i, Chess.GENERAL_HORSE,
							Chess.GREEN_TEAM);
				}
				if (i == 4 && (j == 0 || j == 3)) {
					board[i][j] = new Chess(j, i, Chess.GENERAL_HORSE,
							Chess.GREEN_TEAM);
				}
			}
		}
	}

	public boolean moveChess(int sX, int sY, int tX, int tY) {
		if (this.board == null) {
			return false;
		}
		if (this.getChess(sX, sY) == null) {
			// 移动的源位置没有马
			return false;
		}
		if (this.getChess(tX, tY) != null) {
			// 移动的目标位置有马
			return false;
		}
		Chess sourceChess = this.getChess(sX, sY);
		if (sourceChess.getType() != Chess.FLY_HORSE) {
			// 不是飞马不能随意移动
			if ((Math.abs(tX - sX) > 1 || Math.abs(tY - sY) > 1)) {
				return false;
			}
			if (Math.abs(tX - sX) == 1 && Math.abs(tY - sY) == 1) {
				return false;
			}
		}
		this.board[tY][tX] = this.board[sY][sX];
		this.board[sY][sX] = null;
		gameInterface.moveChess();
		checkKillChess(tX, tY);
		return true;
	}

	public boolean useFlyHorse(int team, int tX, int tY) {
		Chess c;
		if (team == Chess.GREEN_TEAM) {
			c = this.FlyHorseGreen;
		} else if (team == Chess.RED_TEAM) {
			c = this.FlyHorseRed;
		} else {
			return false;
		}
		if (c == null) {
			return false;
		}

		if (c.isInBoard()) {
			return false;
		}

		if (this.getChess(tX, tY) != null) {
			// 移动的目标位置有马
			return false;
		}

		this.board[tY][tX] = c;
		c.setInBoard(true);
		gameInterface.moveChess();
		checkKillChess(tX, tY);
		return true;
	}

	public boolean backFlyHorse(int sX, int sY) {
		Chess c = this.getChess(sX, sY);
		if (c == null) {
			return false;
		}

		if (!c.isInBoard()) {
			return false;
		}

		this.board[sY][sX] = null;
		c.setInBoard(false);
		gameInterface.moveChess();
		return true;
	}

	private void checkGameOver() {
		int gn = this.getTeamChessNum(Chess.GREEN_TEAM);
		int rn = this.getTeamChessNum(Chess.RED_TEAM);
		if (gn <= 0) {
			this.gameOver = true;
			this.winTeam = Chess.RED_TEAM;
			if (this.gameInterface != null) {
				this.gameInterface.gameWin(this.winTeam);
			}
		} else if (rn <= 0) {
			this.gameOver = true;
			this.winTeam = Chess.GREEN_TEAM;
			if (this.gameInterface != null) {
				this.gameInterface.gameWin(this.winTeam);
			}
		}
	}

	private void killChess(int x, int y) {
		if (this.getChess(x, y) == null) {
			return;
		}
		Chess c = this.board[y][x];
		board[y][x] = null;
		c.setDeath(true);
		if (c.getType() == Chess.FLY_HORSE){
			killFlyNum++;
		} else {
			killGeneralNum++;
		}
		return;
	}

	/*
	 * 检查是否有杀掉对方的子
	 */
	private void checkKillChess(int x, int y) {
		// 从该子的四个方向以此判断有没有杀子
		Chess chess = this.board[y][x];
		if (chess == null) {
			return;
		}

		int team = chess.getTeam();
		int cn = this.getTeamChessNum(team);
		this.killGeneralNum = 0;
		this.killFlyNum = 0;
		Chess n3 = this.getChess(x, y - 3);
		Chess n2 = this.getChess(x, y - 2);
		Chess n1 = this.getChess(x, y - 1);
		Chess s1 = this.getChess(x, y + 1);
		Chess s2 = this.getChess(x, y + 2);
		Chess s3 = this.getChess(x, y + 3);
		Chess w1 = this.getChess(x - 1, y);
		Chess w2 = this.getChess(x - 2, y);
		Chess w3 = this.getChess(x - 3, y);
		Chess e1 = this.getChess(x + 1, y);
		Chess e2 = this.getChess(x + 2, y);
		Chess e3 = this.getChess(x + 3, y);

		Log.i("Game", Integer.toString(cn));
		if (cn == 1) {
			// 单独一子，进行挑担规则
			// 1 南北方向
			if (s1 != null && s2 == null && n1 != null && n2 == null) {
				if (chess.getTeam() != s1.getTeam()
						&& chess.getTeam() != n1.getTeam()) {
					// 杀s1,n1
					killChess(x, y + 1);
					killChess(x, y - 1);
				}
			}
			// 1 东西方向
			if (w1 != null && w2 == null && e1 != null && e2 == null) {
				if (chess.getTeam() != w1.getTeam()
						&& chess.getTeam() != e1.getTeam()) {
					// 杀w1,e1
					killChess(x - 1, y);
					killChess(x + 1, y);
				}
			}

		} else {
			// 正常杀子规则
			// 1 杀北
			// 1.1 杀n1
			if (n1 != null && n2 == null && s1 != null) {
				if (chess.getTeam() == s1.getTeam()
						&& n1.getTeam() != chess.getTeam()) {
					// 杀n1
					killChess(x, y - 1);
				}
			}
			// 1.2 杀n2
			if (n1 != null && n3 == null && n2 != null) {
				if (chess.getTeam() == n1.getTeam()
						&& n2.getTeam() != chess.getTeam()) {
					// 杀n2
					killChess(x, y - 2);
				}
			}
			// 2杀东
			// 2.1 杀e1
			if (e1 != null && e2 == null && w1 != null) {
				if (chess.getTeam() == w1.getTeam()
						&& e1.getTeam() != chess.getTeam()) {
					// 杀e1
					killChess(x + 1, y);
				}
			}
			// 2.2 杀e2
			if (e1 != null && e3 == null && e2 != null) {
				if (chess.getTeam() == e1.getTeam()
						&& e2.getTeam() != chess.getTeam()) {
					// 杀e2
					killChess(x + 2, y);
				}
			}
			// 3杀南
			// 3.1 杀s1
			if (s1 != null && s2 == null && n1 != null) {
				if (chess.getTeam() == n1.getTeam()
						&& s1.getTeam() != chess.getTeam()) {
					// 杀s1
					killChess(x, y + 1);
				}
			}
			// 3.2 杀s2
			if (s1 != null && s3 == null && s2 != null) {
				if (chess.getTeam() == s1.getTeam()
						&& s2.getTeam() != chess.getTeam()) {
					// 杀s2
					killChess(x, y + 2);
				}
			}
			// 4杀西
			// 4.1 杀w1
			if (w1 != null && w2 == null && e1 != null) {
				if (chess.getTeam() == e1.getTeam()
						&& w1.getTeam() != chess.getTeam()) {
					// 杀w1
					killChess(x - 1, y);
				}
			}
			// 4.2 杀w2
			if (w1 != null && w3 == null && w2 != null) {
				if (chess.getTeam() == w1.getTeam()
						&& w2.getTeam() != chess.getTeam()) {
					// 杀s2
					killChess(x - 2, y);
				}
			}
			checkGameOver();
		}
		if (this.gameInterface != null && killGeneralNum + killFlyNum != 0) {
			this.gameInterface.killChess(chess.getTeam(), killGeneralNum, killFlyNum);
		}
		this.killGeneralNum = 0;
		this.killFlyNum = 0;
	}

	private Chess getChess(int x, int y) {
		if (x >= 4 || x < 0 || y >= 6 || y < 0) {
			return null;
		} else {
			if (this.board == null) {
				return null;
			}
			return this.board[y][x];
		}
	}

	private int getTeamChessNum(int team) {
		int gn = 0;
		int rn = 0;
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				if (this.board[i][j] != null) {
					if (this.board[i][j].getType() != Chess.FLY_HORSE) {
						if (this.board[i][j].getTeam() == Chess.GREEN_TEAM) {
							gn++;
						} else if (this.board[i][j].getTeam() == Chess.RED_TEAM) {
							rn++;
						}
					}
				}
			}
		}
		if (team == Chess.GREEN_TEAM) {
			if (this.FlyHorseGreen != null && !this.FlyHorseGreen.isDeath()) {
				gn++;
			}
			return gn;
		}
		if (team == Chess.RED_TEAM) {
			if (this.FlyHorseRed != null && !this.FlyHorseRed.isDeath()) {
				rn++;
			}
			return rn;
		}
		return -1;
	}

	public Chess[][] getBoard() {
		return board;
	}

	public void setBoard(Chess[][] board) {
		this.board = board;
	}
}
