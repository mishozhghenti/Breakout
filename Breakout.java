/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH = (WIDTH - (NBRICKS_PER_ROW - 1)
			* BRICK_SEP)
			/ NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

	/** Number of turns */
	private static final int NTURNS = 3;

	/* Method: run() */
	/** Runs the Breakout program. */
	public void run() {

		getReadyForPlay();
		gameProcess();

	}

	/* sets the window */
	private void setWindow() {
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		setSize(2 * APPLICATION_WIDTH - getWidth(), 2 * APPLICATION_HEIGHT
				- getHeight());
	}

	/* preparing for game */
	private void getReadyForPlay() {
		setWindow();
		drawBricks();
		drawPaddle();
		drawBall();
		addCitiation();
		showScore();
	}

	/* game process */
	private void gameProcess() {
		waitForBegin();
		vx = rgen.nextDouble(1.0, 3.0);
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		while (checkBottomTouch == true) {
			checkBottomTouch();
			moveBall();
			checkTouch();
			getCollidingObject();
			showScore();
			checkPaddleTouch();
			checkWin();
		}
	}

	/* it appears when we start the game */
	private void waitForBegin() {
		GLabel glabel = new GLabel("Please Click To Play");
		glabel.setLocation((WIDTH - glabel.getWidth()) / 2,
				(HEIGHT - glabel.getAscent()) / 2 + 4 * BALL_RADIUS);
		add(glabel);
		waitForClick();
		remove(glabel);
	}

	/* draws bricks */
	private void drawBricks() {
		int countLine = 0;
		Color color = Color.RED;
		for (int i = 0; i < NBRICK_ROWS; i++) {
			countLine++;
			for (int j = 0; j < NBRICKS_PER_ROW; j++) {
				drawOneBrick(i, j, color); /*
											 * draws two bricks, on the top side
											 * it is different brick
											 */
				if (rgen.nextBoolean(0.3) == true) {
					drawOneBrick(i, j, Color.GRAY);
					differentBrick++;
				}
			}
			if (countLine % 2 == 0) {
				color = getColor(color);
			}
		}
	}

	/* it draws one brick */
	private void drawOneBrick(int i, int j, Color color) {
		GRect brick = new GRect(
				j
						* (BRICK_WIDTH + BRICK_SEP)
						+ (WIDTH - BRICK_WIDTH * NBRICKS_PER_ROW - (NBRICKS_PER_ROW - 1)
								* BRICK_SEP) / 2, BRICK_Y_OFFSET + i
						* (BRICK_HEIGHT + BRICK_SEP), BRICK_WIDTH, BRICK_HEIGHT);
		brick.setFilled(true);
		brick.setColor(color);
		brick.setFillColor(color);
		add(brick);
	}

	/* generates the color of the next line */
	private Color getColor(Color color) {
		Color rez = null;
		Color[] col = new Color[5];
		col[0] = Color.RED;
		col[1] = Color.ORANGE;
		col[2] = Color.YELLOW;
		col[3] = Color.GREEN;
		col[4] = Color.CYAN;
		for (int i = 0; i < col.length; i++) {
			if (color == col[i]) {
				if (i == 4) {
					rez = col[0];
				} else {
					rez = col[i + 1];
				}
			}
		}
		return rez;
	}

	/* draws the paddle */
	private void drawPaddle() {
		paddle = new GRect((WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_Y_OFFSET
				- PADDLE_HEIGHT, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Color.BLACK);
		paddle.setFillColor(Color.BLACK);
		add(paddle);
		addMouseListeners();
	}

	/* paddle moves as the mouse */
	public void mouseMoved(MouseEvent e) {
		paddle.setLocation(e.getX(), HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		if ((e.getX() + PADDLE_WIDTH) > WIDTH) {
			paddle.setLocation((WIDTH - PADDLE_WIDTH), HEIGHT - PADDLE_Y_OFFSET
					- PADDLE_HEIGHT);
		}
	}

	/* draws the ball */
	private void drawBall() {
		ball = new GOval(WIDTH / 2 - BALL_RADIUS, HEIGHT / 2 - BALL_RADIUS,
				2 * BALL_RADIUS, 2 * BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Color.BLACK);
		ball.setFillColor(Color.BLACK);
		add(ball);
	}

	/* moves the ball */
	private void moveBall() {
		ball.move(vx, vy);
		pause(pause);
	}

	/* it checks if ball touches paddle */
	private void checkPaddleTouch() {
		if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) == paddle) {
			vy = -Math.abs(vy);
			playSound();
		}
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
				* BALL_RADIUS) == paddle) {
			vy = -Math.abs(vy);
			playSound();
		}
	}

	/* checks wall touches */
	private void checkTouch() {
		/* checks right wall */
		if (ball.getX() + 2 * BALL_RADIUS >= WIDTH) {
			playSound();
			vx = -vx;
		}
		/* checks left wall */
		if (ball.getX() <= 0) {
			playSound();
			vx = -vx;
		}
		/* checks top wall */
		if (ball.getY() <= 0) {
			playSound();
			vy = -vy;
		}
	}

	/*
	 * checks if ball touch the bottom side, it means that we have to try again
	 */
	private void checkBottomTouch() {
		if (ball.getY() + 2 * BALL_RADIUS >= getHeight()) {
			turn--;
			nextTry();
			checkBottomTouch = false;
		} else {
			checkBottomTouch = true;
		}
	}

	/* it removes the touched brick, and control if there are different bricks */
	private void getCollidingObject() {
		if (getElementAt(ball.getX(), ball.getY()) != null
				&& getElementAt(ball.getX(), ball.getY()) != paddle
				&& getElementAt(ball.getX(), ball.getY()) != scoreBar
				&& getElementAt(ball.getX(), ball.getY()) != labelScore
				&& getElementAt(ball.getX(), ball.getY()) == getElementAt(
						ball.getX() + 2 * BALL_RADIUS, ball.getY())) {
			remove(getElementAt(ball.getX(), ball.getY()));
			brickCount++;
			generatePsulScore();
			totalScore += plusScore;
			playSound();
			vy = -vy;
		} else {
			removeBricks();
		}
	}

	/* it removes the touched bricks */
	private void removeBricks() {
		if (getElementAt(ball.getX(), ball.getY()) != null
				&& getElementAt(ball.getX(), ball.getY()) != paddle
				&& getElementAt(ball.getX(), ball.getY()) != scoreBar
				&& getElementAt(ball.getX(), ball.getY()) != labelScore) {
			remove(getElementAt(ball.getX(), ball.getY()));
			brickCount++;
			generatePsulScore();
			totalScore += plusScore;
			playSound();
			vy = -vy;
		}
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != null
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != paddle
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != scoreBar
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()) != labelScore) {
			remove(getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY()));
			brickCount++;
			generatePsulScore();
			totalScore += plusScore;
			playSound();
			if (vy < 0) {
				vy = -vy;
			}
		}
		if (getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != null
				&& getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != paddle
				&& getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != scoreBar
				&& getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS) != labelScore) {
			remove(getElementAt(ball.getX(), ball.getY() + 2 * BALL_RADIUS));
			brickCount++;
			generatePsulScore();
			totalScore += plusScore;
			playSound();
			vy = -vy;
		}
		if (getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
				* BALL_RADIUS) != null
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != paddle
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != scoreBar
				&& getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
						* BALL_RADIUS) != labelScore) {
			remove(getElementAt(ball.getX() + 2 * BALL_RADIUS, ball.getY() + 2
					* BALL_RADIUS));
			brickCount++;
			generatePsulScore();
			totalScore += plusScore;
			playSound();
			if (vy > 0) {
				vy = -vy;
			}
		}
	}

	/* it checks if we win (when all bricks are removed) */
	private void checkWin() {
		if (brickCount == NBRICK_ROWS * NBRICKS_PER_ROW + differentBrick) {
			checkBottomTouch = false;
			youWin();
		}
	}

	/* it is used when we win */
	private boolean youWin() {
		if (brickCount == NBRICK_ROWS * NBRICKS_PER_ROW + differentBrick) {
			win.play();
			GLabel glabel = new GLabel("You Win");
			glabel.setLocation((WIDTH - glabel.getWidth()) / 2,
					(HEIGHT - glabel.getAscent()) / 2);
			add(glabel);
			remove(ball);
			return false;
		} else {
			return true;
		}
	}

	/* it is used when we lose try, but we havn't lost NTurn times */
	private void nextTry() {
		if (turn > 0) {
			tryAgain.play();
			GLabel glabel = new GLabel("Try Again, Left " + turn + " Try");
			glabel.setLocation((WIDTH - glabel.getWidth()) / 2,
					(HEIGHT - glabel.getAscent()) / 2);
			add(glabel);
			waitForClick();
			remove(glabel);
			remove(ball);
			drawBall();
			gameProcess();
		} else {
			youLose();
		}
	}

	/* when we finally lose, we have already lost NTurn times */
	private void youLose() {
		lose.play();
		GLabel glabel = new GLabel("You Lose");
		glabel.setLocation((WIDTH - glabel.getWidth()) / 2,
				(HEIGHT - glabel.getAscent()) / 2);
		add(glabel);
	}

	/* play the touch sound */
	private void playSound() {
		bounceClip.play();
	}

	/* it shows the score bar(text only : score) */
	private void addCitiation() {
		scoreBar = new GLabel("Score : ");
		scoreBar.setColor(Color.RED);
		add(scoreBar, getWidth() - 3 * scoreBar.getWidth(), getHeight()
				- scoreBar.getAscent());
	}

	/* it shows the current score */
	private void showScore() {
		if (totalScore != 0) {
			remove(labelScore);
			labelScore = new GLabel(" " + totalScore);
			labelScore.setColor(Color.RED);
			add(labelScore, getWidth() - labelScore.getWidth(), getHeight()
					- labelScore.getAscent());
		} else {
			labelScore = new GLabel(" " + totalScore);
			labelScore.setColor(Color.RED);
			add(labelScore, getWidth() - labelScore.getWidth(), getHeight()
					- labelScore.getAscent());
		}
	}

	/*
	 * after every the seventh brick the score gets bigger(+20) and the velocity
	 * of the ball gets bigger(+0.5)
	 */
	private void generatePsulScore() {
		if (brickCount % 7 == 0) {
			plusScore += 20;
			if (vy > 0) {
				vy += 0.3;
			} else {
				vy -= 0.3;
			}
		}
	}

	private int totalScore = 0;
	private int plusScore = 50;
	private int pause = 10;
	private int turn = NTURNS;
	private int brickCount = 0;
	private int differentBrick = 0;
	private GRect paddle;
	private GOval ball;
	private GLabel scoreBar;
	private GLabel labelScore;
	private boolean checkBottomTouch = true;
	private double vx;
	private double vy = 3.0;
	private RandomGenerator rgen = new RandomGenerator();
	private AudioClip bounceClip = MediaTools.loadAudioClip("bounce.au");
	private AudioClip lose = MediaTools.loadAudioClip("fail.au");
	private AudioClip tryAgain = MediaTools.loadAudioClip("tryAgain.au");
	private AudioClip win = MediaTools.loadAudioClip("win.au");
}