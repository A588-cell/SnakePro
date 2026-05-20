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
        // מניעת פנייה של 180 מעלות (הפוך מהכיוון הנוכחי)
        if (direction == DIR_UP && newDir == DIR_DOWN) {
            return;
        }
        if (direction == DIR_DOWN && newDir == DIR_UP) {
            return;
        }
        if (direction == DIR_LEFT && newDir == DIR_RIGHT) {
            return;
        }
        if (direction == DIR_RIGHT && newDir == DIR_LEFT) {
            return;
        }
        direction = newDir;
    }

    public void update() {
        if (gameOver == true) {
            return;
        }

        int headX = snake.getHead().getX();
        int headY = snake.getHead().getY();

        int newX = headX;
        int newY = headY;

        if (direction == DIR_UP) {
            newY = newY - 1;
        } else if (direction == DIR_RIGHT) {
            newX = newX + 1;
        } else if (direction == DIR_DOWN) {
            newY = newY + 1;
        } else if (direction == DIR_LEFT) {
            newX = newX - 1;
        }

        // בדיקה אם הנחש יצא מגבולות הלוח (קיר)
        if (newX < 0 || newX >= COLS || newY < 0 || newY >= ROWS) {
            gameOver = true;
            return;
        }

        snake.moveTo(newX, newY);

        // בדיקה אם הנחש התנגש בעצמו
        if (snake.hitsItself() == true) {
            gameOver = true;
            return;
        }

        // בדיקה אם הנחש אכל את הפרי
        if (newX == fruit.getX() && newY == fruit.getY()) {
            score = score + 10;
            snake.grow();
            spawnFruit();
        }
    }

    private void spawnFruit() {
        while (true) {
            int x = random.nextInt(COLS);
            int y = random.nextInt(ROWS);

            boolean onSnake = false;
            // לולאה פשוטה לבדיקה אם המיקום החדש תפוס על ידי הנחש
            for (int i = 0; i < snake.getBody().size(); i++) {
                SnakePart p = snake.getBody().get(i);
                if (p.getX() == x && p.getY() == y) {
                    onSnake = true;
                    break;
                }
            }

            if (onSnake == false) {
                fruit = new Fruit(x, y);
                return;
            }
        }
    }
}
