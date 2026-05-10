package com.example.snakeprogame.game;

import java.util.Random;

public class GameModule {

    public static final int COLS = 18;
    public static final int ROWS = 28;

    public static final int DIR_UP = 0;
    public static final int DIR_RIGHT = 1;
    public static final int DIR_DOWN = 2;
    public static final int DIR_LEFT = 3;

    private Snake snake;
    private Fruit fruit;
    private int direction = DIR_RIGHT;
    private int score = 0;
    private boolean gameOver = false;

    private final Random random = new Random();

    public GameModule() {
        reset();
    }

    public void reset() {
        int startX = COLS / 2;
        int startY = ROWS / 2;
        snake = new Snake(startX, startY);
        score = 0;
        gameOver = false;
        direction = DIR_RIGHT;
        spawnFruit();
    }

    public Snake getSnake() { return snake; }
    public Fruit getFruit() { return fruit; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }

    public void setDirection(int newDir) {
        // לא מאפשרים "היפוך" (כדי שלא ייכנס לעצמו מיד)
        if ((direction == DIR_UP && newDir == DIR_DOWN) ||
                (direction == DIR_DOWN && newDir == DIR_UP) ||
                (direction == DIR_LEFT && newDir == DIR_RIGHT) ||
                (direction == DIR_RIGHT && newDir == DIR_LEFT)) {
            return;
        }
        direction = newDir;
    }

    public void update() {
        if (gameOver) return;

        int headX = snake.getHead().getX();
        int headY = snake.getHead().getY();

        int newX = headX;
        int newY = headY;

        if (direction == DIR_UP) newY--;
        else if (direction == DIR_RIGHT) newX++;
        else if (direction == DIR_DOWN) newY++;
        else if (direction == DIR_LEFT) newX--;

        // קיר = Game Over
        if (newX < 0 || newX >= COLS || newY < 0 || newY >= ROWS) {
            gameOver = true;
            return;
        }

        snake.moveTo(newX, newY);

        // התנגש בעצמו
        if (snake.hitsItself()) {
            gameOver = true;
            return;
        }

        // אכל פרי
        if (newX == fruit.getX() && newY == fruit.getY()) {
            score += 10;
            snake.grow();
            spawnFruit();
        }
    }

    private void spawnFruit() {
        while (true) {
            int x = random.nextInt(COLS);
            int y = random.nextInt(ROWS);

            boolean onSnake = false;
            for (SnakePart p : snake.getBody()) {
                if (p.getX() == x && p.getY() == y) {
                    onSnake = true;
                    break;
                }
            }
            if (!onSnake) {
                fruit = new Fruit(x, y);
                return;
            }
        }
    }
}