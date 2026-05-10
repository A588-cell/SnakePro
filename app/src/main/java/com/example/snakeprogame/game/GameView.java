package com.example.snakeprogame.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {

    public interface GameOverListener {
        void onGameOver(int score);
    }

    private final BoardGame boardGame;
    private final Paint paint = new Paint();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final int TICK_MS = 160;
    private boolean scoreSaved = false;

    private float cellSize;
    private float offsetX;
    private float offsetY;
    private float touchStartX, touchStartY;

    private GameOverListener listener;

    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            GameModule gm = boardGame.getModule();

            gm.update();

            if (gm.isGameOver() && !scoreSaved) {
                scoreSaved = true;
                if (listener != null) {
                    listener.onGameOver(gm.getScore());
                }
            }

            invalidate();
            handler.postDelayed(this, TICK_MS);
        }
    };

    public GameView(Context context, GameOverListener listener) {
        super(context);
        this.listener = listener;
        boardGame = new BoardGame();
        handler.postDelayed(tick, TICK_MS);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float cellW = (float) w / GameModule.COLS;
        float cellH = (float) h / GameModule.ROWS;
        cellSize = Math.min(cellW, cellH);

        float boardW = cellSize * GameModule.COLS;
        float boardH = cellSize * GameModule.ROWS;

        offsetX = (w - boardW) / 2f;
        offsetY = (h - boardH) / 2f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        GameModule gm = boardGame.getModule();

        canvas.drawColor(Color.rgb(10, 10, 14));

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(60, 60, 80));
        paint.setStrokeWidth(4);
        canvas.drawRect(offsetX, offsetY,
                offsetX + cellSize * GameModule.COLS,
                offsetY + cellSize * GameModule.ROWS, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        float fx = offsetX + gm.getFruit().getX() * cellSize;
        float fy = offsetY + gm.getFruit().getY() * cellSize;
        canvas.drawRoundRect(fx, fy, fx + cellSize, fy + cellSize, 12, 12, paint);

        for (int i = 0; i < gm.getSnake().getBody().size(); i++) {
            SnakePart part = gm.getSnake().getBody().get(i);

            float x = offsetX + part.getX() * cellSize;
            float y = offsetY + part.getY() * cellSize;

            if (i == 0) paint.setColor(Color.rgb(0, 255, 140));
            else paint.setColor(Color.rgb(0, 200, 110));

            canvas.drawRoundRect(x, y, x + cellSize, y + cellSize, 14, 14, paint);
        }

        paint.setColor(Color.WHITE);
        paint.setTextSize(42);
        paint.setFakeBoldText(true);
        canvas.drawText("Score: " + gm.getScore(), 30, 60, paint);

        if (gm.isGameOver()) {
            paint.setColor(Color.argb(200, 0, 0, 0));
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(70);
            canvas.drawText("GAME OVER", getWidth() / 2f - 190, getHeight() / 2f, paint);

            paint.setTextSize(40);
            canvas.drawText("Tap to restart", getWidth() / 2f - 140, getHeight() / 2f + 70, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        GameModule gm = boardGame.getModule();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchStartX = event.getX();
            touchStartY = event.getY();

            if (gm.isGameOver()) {
                gm.reset();
                scoreSaved = false;
                return true;
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            float dx = event.getX() - touchStartX;
            float dy = event.getY() - touchStartY;

            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 50) gm.setDirection(GameModule.DIR_RIGHT);
                else if (dx < -50) gm.setDirection(GameModule.DIR_LEFT);
            } else {
                if (dy > 50) gm.setDirection(GameModule.DIR_DOWN);
                else if (dy < -50) gm.setDirection(GameModule.DIR_UP);
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(tick);
    }
}