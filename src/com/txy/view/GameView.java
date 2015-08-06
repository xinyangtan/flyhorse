package com.txy.view;

import utils.Utils;
import utils.Sound;

import com.txy.flyhorse.R;
import com.txy.flyhorse.core.Chess;
import com.txy.flyhorse.core.Game;
import com.txy.flyhorse.core.GameInterface;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class GameView extends BasicView implements GameInterface {

	public boolean isFreeMove() {
		return freeMove;
	}

	public void setFreeMove(boolean freeMove) {
		this.freeMove = freeMove;
	}

	private Context context;

	private Bitmap mBackground = BitmapFactory.decodeResource(getResources(),
			R.drawable.game_backgroud2);// 背景图
	private Sound sound;
	private Paint mPaint = new Paint();
	private Point[][] boardPoints = null;
	private Point FlyHorseGPoint = null;
	private Point FlyHorseRPoint = null;

	private int chessR;
	private Game game;

	private boolean gameOver = false;
	private int winTeam;
	private Point selectChess; // 待移动的子
	private boolean moveReady; // 是否有子选中
	private Chess selectFlyHorse;
	private boolean useFlyHorse;
	private boolean backFlyHorse;

	// 得分
	private int scoreG = 0;
	private int scoreR = 0;

	private int moveTeam;
	private boolean freeMove = true;

	public GameView(Context context) {
		super(context);
		this.context = context;
		initPaint();
		initData();
		// this.setBackgroundColor(Color.BLUE);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initPaint();
		initData();
	}

	private void initData() {
		sound = new Sound(this.context);
		game = new Game();
		game.setGameInterface(this);

		if (!freeMove) {
			initMoveTeam();
		}
	}

	private void initMoveTeam() {
		int t = (int) ((Math.random() * 100) % 2);
		if (t == 0) {
			moveTeam = Chess.GREEN_TEAM;
		} else {
			moveTeam = Chess.RED_TEAM;
		}
	}

	private void initBoardPoints() {
		if (boardPoints != null) {
			return;
		}
		boardPoints = new Point[6][4];
		int w = this.getWidth();
		int h = this.getHeight();
		int x_start = 100;
		int y_start = 200;
		int dx = (int) ((w - 100 * 2) / 3);
		int dy = (int) ((h - 200 * 2) / 5);
		// 棋子半径计算
		this.chessR = Math.min(dx, dy) / 4;
		for (int i = 0; i < 6; i++) {
			int y = y_start + i * (dy);
			for (int j = 0; j < 4; j++) {
				int x = x_start + j * (dx);
				Point p = new Point();
				p.x = x;
				p.y = y;
				boardPoints[i][j] = p;
			}
		}

		this.FlyHorseRPoint = new Point(boardPoints[0][1].x + (int) (dx / 2),
				boardPoints[0][1].y + (int) (dx / 2));
		this.FlyHorseGPoint = new Point(boardPoints[4][1].x + (int) (dx / 2),
				boardPoints[4][1].y + (int) (dx / 2));

	}

	private void initPaint() {
		mPaint.setColor(0xFFFFFFFF);
		mPaint.setTextSize(18);
		mPaint.setStyle(Style.FILL);

	}

	private void playMoveSound() {
		sound.playSound(Sound.MOVE);
	}

	private void playKillSound(int n) {
		switch (n) {
		case 1:
			sound.playSound(Sound.KILL);
			break;
		case 2:
			sound.playSound(Sound.DOUBLEKILL);
			break;
		case 3:
			sound.playSound(Sound.TRIPLEKILL);
			break;
		case 4:
			sound.playSound(Sound.QUATREKILL);
			break;
		}
	}

	private void playWinSound() {
		sound.playSound(Sound.VICTORY);
	}

	private void newGame() {
		game.reStartGame();
		gameOver = false;
		this.scoreG = 0;
		this.scoreR = 0;
	}

	@Override
	public void drawOnce(Canvas canvas) {
//		super.drawOnce(canvas);
		if (canvas == null) {
			return;
		}
		Bitmap resizeBG = Utils.resizeBitmap(mBackground, this.getWidth(),
				this.getHeight());
		canvas.drawBitmap(resizeBG, 0, 0, null);// 画游戏背景

		mPaint.setColor(0xFFFFFFFF);
		mPaint.setTextSize(18);
		canvas.drawText(("screen width:" + Integer.toString(this.getWidth())),
				5, 15, mPaint);
		canvas.drawText(
				("screen height:" + Integer.toString(this.getHeight())), 5, 30,
				mPaint);

		mPaint.setColor(0x0FE0040BF);
		mPaint.setTextSize(40);
		canvas.drawText(("得分" + Integer.toString(scoreR)),
				(this.getWidth() - (40 * 2)) / 2, 200 - chessR - 40, mPaint);
		canvas.drawText(("得分" + Integer.toString(scoreG)),
				(this.getWidth() - (40 * 2)) / 2, this.getHeight()
						- (200 - chessR - 40), mPaint);
		// 画棋盘
		Paint linePaint = new Paint();
		// Shader mShader = new LinearGradient(0, 0, 100, 100,
		// new int[] { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
		// Color.LTGRAY }, null, Shader.TileMode.REPEAT); //
		// 一个材质,打造出一个线性梯度沿著一条线。
		// linePaint.setShader(mShader);
		linePaint.setTextSize(50);
		linePaint.setColor(Color.BLUE);
		linePaint.setStyle(Style.FILL);

		initBoardPoints();

		// 画出棋盘的线
		for (int i = 0; i < boardPoints[0].length; i++) {
			// 画出竖线
			Point startPoint = boardPoints[0][i];
			Point endPoint = boardPoints[boardPoints.length - 1][i];
			canvas.drawRect(startPoint.x - 2, startPoint.y, endPoint.x + 2,
					endPoint.y, linePaint);
		}
		for (int i = 0; i < boardPoints.length; i++) {
			// 画出横线
			int length = boardPoints[i].length;
			Point startPoint = boardPoints[i][0];
			Point endPoint = boardPoints[i][length - 1];
			canvas.drawRect(startPoint.x, startPoint.y - 2, endPoint.x,
					endPoint.y + 2, linePaint);
		}
		// 画出飞马位置线
		Point startPoint = boardPoints[0][1];
		Point endPoint = boardPoints[1][2];
		canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
				linePaint);
		startPoint = boardPoints[0][2];
		endPoint = boardPoints[1][1];
		canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
				linePaint);

		startPoint = boardPoints[5][1];
		endPoint = boardPoints[4][2];
		canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
				linePaint);
		startPoint = boardPoints[5][2];
		endPoint = boardPoints[4][1];
		canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y,
				linePaint);

		// 画出马
		Paint chessPaint = new Paint();

		Chess[][] chessBord = game.getBoard();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				Chess chess = chessBord[i][j];
				if (chess != null) {
					Point p = boardPoints[i][j];
					if (chess.getTeam() == Chess.RED_TEAM) {
						chessPaint.setColor(Color.WHITE);
					} else {
						chessPaint.setColor(Color.GRAY);
					}

					if (chess.isSelected()) {
						chessPaint.setColor(Color.BLUE);
					}
					if (chess.getType() == Chess.FLY_HORSE) {
						drawFlyHorse(chess, p, canvas, chessPaint);
					} else {
						canvas.drawCircle(p.x, p.y, chessR, chessPaint);
					}
				}
			}
		}
		// 画飞马
		Chess FlyHorseRed = game.getFlyHorseRed();
		Chess FlyHorseGreen = game.getFlyHorseGreen();
		if (FlyHorseRed != null && !FlyHorseRed.isDeath()
				&& !FlyHorseRed.isInBoard()) {
			drawFlyHorse(game.FlyHorseRed, FlyHorseRPoint, canvas, chessPaint);
		}
		if (FlyHorseGreen != null && !FlyHorseGreen.isDeath()
				&& !FlyHorseGreen.isInBoard()) {
			drawFlyHorse(game.FlyHorseGreen, FlyHorseGPoint, canvas, chessPaint);
		}

		if (gameOver) {
			String win;
			if (winTeam == Chess.RED_TEAM) {
				win = "白色方胜利";
			} else if (winTeam == Chess.GREEN_TEAM) {
				win = "灰色方胜利";
			} else {
				win = "和局";
			}
			mPaint.setTextSize(50);
			mPaint.setColor(Color.YELLOW);
			canvas.drawText(win, (this.getWidth() - (50 * win.length())) / 2,
					this.getHeight() / 2, mPaint);
			canvas.drawText("重新开始", (this.getWidth() - (50 * 5)) / 2,
					this.getHeight() / 2 + 70, mPaint);

		}
		// canvas.drawRect(0, 200, this.getWidth(), 210, linePaint);
		// canvas.drawCircle(cx, cy, radius, paint);
	}

	private void drawFlyHorse(Chess flyHorse, Point p, Canvas canvas,
			Paint chessPaint) {
		if (flyHorse.getTeam() == Chess.GREEN_TEAM) {
			chessPaint.setColor(Color.GRAY);
		} else {
			chessPaint.setColor(Color.WHITE);
		}
		if (flyHorse.isSelected()) {
			chessPaint.setColor(Color.BLUE);
		}
		canvas.drawCircle(p.x, p.y, chessR, chessPaint);
		mPaint.setColor(Color.GREEN);
		mPaint.setTextSize(chessR);
		canvas.drawText("帅", p.x - (int) (chessR / 2),
				p.y + (int) (chessR / 2), mPaint);
	}

	private void clearSelectStatus() {
		Chess[][] chessBord = game.getBoard();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				Chess chess = chessBord[i][j];
				if (chess != null && chess.isSelected()) {
					chess.setSelected(false);
				}
			}
		}
		game.getFlyHorseGreen().setSelected(false);
		game.getFlyHorseRed().setSelected(false);
		selectChess = null;
		this.moveReady = false;
		this.backFlyHorse = false;
		selectFlyHorse = null;
		this.useFlyHorse = false;
	}

	private Point getSelectChessPoint(int x, int y) {
		// boardPoints
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 4; j++) {
				Point cP = boardPoints[i][j];
				Rect touchR = new Rect(cP.x - chessR, cP.y - chessR, cP.x
						+ chessR, cP.y + chessR);
				if (touchR.contains(x, y)) {
					Point sp = new Point();
					sp.x = j;
					sp.y = i;
					return sp;
				}
			}
		}
		return null;
	}

	private Chess getSelectFlyHorse(int x, int y) {
		Point cP = this.FlyHorseGPoint;
		Rect touchR = new Rect(cP.x - chessR, cP.y - chessR, cP.x + chessR,
				cP.y + chessR);
		if (touchR.contains(x, y)) {
			return game.getFlyHorseGreen();
		}
		cP = this.FlyHorseRPoint;
		touchR = new Rect(cP.x - chessR, cP.y - chessR, cP.x + chessR, cP.y
				+ chessR);
		if (touchR.contains(x, y)) {
			return game.getFlyHorseRed();
		}
		return null;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final String tag = "Touch";
		String msg = "";
		int x = (int) event.getX();
		int y = (int) event.getY();
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			msg += "DOWN";
			getSelectChessPoint(x, y);

			break;
		case MotionEvent.ACTION_MOVE:
			msg += "MOVE";
			break;
		case MotionEvent.ACTION_UP:
			msg += "UP";
			if (gameOver) {
				Rect touchR = new Rect((this.getWidth() - (50 * 4)) / 2,
						this.getHeight() / 2,
						(this.getWidth() - (50 * 4)) / 2 + 50 * 4,
						this.getHeight() / 2 + 70);
				if (touchR.contains(x, y)) {
					newGame();
				}
				break;
			}
			Point sP = getSelectChessPoint(x, y);
			if (sP != null) {
				Chess sc = game.getBoard()[sP.y][sP.x];
				if (sc != null && (moveTeam == sc.getTeam() || freeMove)) {
					boolean isChessSelect = sc.isSelected();
					clearSelectStatus();
					sc.setSelected(!isChessSelect);
					if (sc.isSelected()) {
						selectChess = sP;
						if (sc.getType() == Chess.FLY_HORSE) {
							this.backFlyHorse = true;
						}
						this.moveReady = true;
						selectFlyHorse = null;
						this.useFlyHorse = false;
					} else {
						selectChess = null;
						this.moveReady = false;
						this.backFlyHorse = false;
						selectFlyHorse = null;
						this.useFlyHorse = false;
					}
				} else if (this.moveReady) {
					game.moveChess(selectChess.x, selectChess.y, sP.x, sP.y);
					clearSelectStatus();

				} else if (this.useFlyHorse) {
					game.useFlyHorse(this.selectFlyHorse.getTeam(), sP.x, sP.y);
					clearSelectStatus();
				}
			} else {
				Chess sFH = getSelectFlyHorse(x, y);
				if (sFH != null && (moveTeam == sFH.getTeam() || freeMove)) {
					if (this.backFlyHorse) {
						this.backFlyHorse = false;
						game.backFlyHorse(selectChess.x, selectChess.y);
						clearSelectStatus();
					} else {
						boolean isChessSelect = sFH.isSelected();
						clearSelectStatus();
						sFH.setSelected(!isChessSelect);
						if (sFH.isSelected()) {
							selectChess = null;
							this.moveReady = false;
							this.backFlyHorse = false;
							selectFlyHorse = sFH;
							this.useFlyHorse = true;
						} else {
							selectChess = null;
							this.moveReady = false;
							this.backFlyHorse = false;
							selectFlyHorse = null;
							this.useFlyHorse = false;
						}
					}
				} else {
					clearSelectStatus();
				}
			}
			break;
		}
		msg += "x:" + Integer.toString(x) + "  y:" + Integer.toString(y);
		Log.i(tag, msg);
		return true;
	}

	@Override
	public void gameWin(int winTeam) {
		playWinSound();
		this.winTeam = winTeam;
		// 和局判定
		if (winTeam != Chess.RED_TEAM && winTeam != Chess.GREEN_TEAM) {
			if (this.scoreG > this.scoreR) {
				this.winTeam = Chess.GREEN_TEAM;
			} else if (this.scoreG < this.scoreR) {
				this.winTeam = Chess.RED_TEAM;
			}
		}
		this.gameOver = true;
	}

	@Override
	public void moveChess() {
		playMoveSound();
		if (!freeMove) {
			// 移动方转换
			moveTeam = (moveTeam == Chess.GREEN_TEAM) ? Chess.RED_TEAM
					: Chess.GREEN_TEAM;
		}
	}

	@Override
	public void killChess(int team, int generalN, int flyN) {
		// TODO Auto-generated method stub
		int n = generalN + flyN;
		int score = generalN * 10 + flyN * 30;
		switch (n) {
		case 1:
			break;
		case 2:
			score = (int) (score * 1.5);
			break;
		case 3:
			score = (int) (score * 2);
			break;
		case 4:
			score = (int) (score * 3);
			break;
		default:
			break;
		}
		if (team == Chess.GREEN_TEAM) {
			this.scoreG += score;
		}
		if (team == Chess.RED_TEAM) {
			this.scoreR += score;
		}
		playKillSound(n);
	}
}
