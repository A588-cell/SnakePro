package com.example.snakeprogame.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import com.example.snakeprogame.GameActivity;

public class GameView extends View {

    private final GameModule gm;
    private final Paint paint = new Paint();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final GameActivity activity;

    private float cellSize;
    private float offsetX, offsetY;
    private float touchX, touchY;
    private boolean scoreSaved = false;

    // לולאת המשחק - Tick
    private final Runnable tick = new Runnable() {
        @Override
        public void run() {
            if (gm.isGameOver() == true) {
                if (scoreSaved == false) {
                    activity.saveScore(gm.getScore());
                    scoreSaved = true;
                }
                invalidate(); // רענון אחרון להצגת מסך Game Over
                return;
            }

            gm.update();
            invalidate(); // רענון המסך
            handler.postDelayed(this, 160);
        }
    };

    public GameView(Context context) {
        super(context);
        this.activity = (GameActivity) context;
        this.gm = new GameModule();
        handler.postDelayed(tick, 160);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float cellW = (float) w / GameModule.COLS;
        float cellH = (float) h / GameModule.ROWS;
        cellSize = Math.min(cellW, cellH);

        offsetX = (w - (cellSize * GameModule.COLS)) / 2;
        offsetY = (h - (cellSize * GameModule.ROWS)) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // רקע כהה
        canvas.drawColor(Color.rgb(10, 10, 14));

        // ציור מסגרת ללוח המשחק
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(60, 60, 80));
        paint.setStrokeWidth(4);
        canvas.drawRect(
                offsetX,
                offsetY,
                offsetX + cellSize * GameModule.COLS,
                offsetY + cellSize * GameModule.ROWS,
                paint
        );

        // ציור הפרי אדום עם פינות מעוגלות
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        float fx = offsetX + gm.getFruit().getX() * cellSize;
        float fy = offsetY + gm.getFruit().getY() * cellSize;
        canvas.drawRoundRect(fx, fy, fx + cellSize, fy + cellSize, 12, 12, paint);

        // ציור הנחש בלולאת for פשוטה
        for (int i = 0; i < gm.getSnake().getBody().size(); i = i + 1) {
            SnakePart part = gm.getSnake().getBody().get(i);
            float sx = offsetX + part.getX() * cellSize;
            float sy = offsetY + part.getY() * cellSize;

            if (i == 0) {
                paint.setColor(Color.rgb(0, 255, 140)); // צבע לראש
            } else {
                paint.setColor(Color.rgb(0, 200, 110)); // צבע לגוף
            }
            canvas.drawRoundRect(sx, sy, sx + cellSize, sy + cellSize, 14, 14, paint);
        }

        // ציור הניקוד
        paint.setColor(Color.WHITE);
        paint.setTextSize(42);
        paint.setFakeBoldText(true);
        canvas.drawText("Score: " + gm.getScore(), 30, 60, paint);

        // הודעת Game Over
        if (gm.isGameOver() == true) {
            paint.setFakeBoldText(false);
            paint.setColor(Color.argb(200, 0, 0, 0));
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(70);
            paint.setFakeBoldText(true);
            canvas.drawText("GAME OVER", getWidth() / 2f - 190, getHeight() / 2f, paint);

            paint.setTextSize(40);
            paint.setFakeBoldText(false);
            canvas.drawText("Tap to restart", getWidth() / 2f - 140, getHeight() / 2f + 70, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchX = event.getX();
            touchY = event.getY();

            if (gm.isGameOver() == true) {
                gm.reset();
                scoreSaved = false;
                handler.post(tick);
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            float dx = event.getX() - touchX;
            float dy = event.getY() - touchY;

            // זיהוי החלקה על המסך
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 50) {
                    gm.setDirection(GameModule.DIR_RIGHT);
                } else if (dx < -50) {
                    gm.setDirection(GameModule.DIR_LEFT);
                }
            } else {
                if (dy > 50) {
                    gm.setDirection(GameModule.DIR_DOWN);
                } else if (dy < -50) {
                    gm.setDirection(GameModule.DIR_UP);
                }
            }
        }
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(tick);
    }
}
