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

    // gm מכיל את הלוגיקה של המשחק (איפה הנחש, האם הוא אכל תפוח וכו')
    private GameModule gm;
    private Paint paint = new Paint();
    // handler אחראי על תזמון לולאת המשחק (הזזת הנחש כל כמה זמן)
    private Handler handler = new Handler(Looper.getMainLooper());
    private GameActivity activity;

    private float cellSize; // גודל של משבצת אחת במשחק
    private float offsetX, offsetY; // רווחים מהצדדים כדי שהמשחק יהיה במרכז
    private float touchX, touchY; // מיקום הנגיעה הראשונה במסך לזיהוי החלקה

    // לולאת המשחק - הפעולה הזו רצה שוב ושוב כל 160 מילי-שניות
    private Runnable tick = new Runnable() {
        @Override
        public void run() {
            // אם המשחק נגמר
            if (gm.isGameOver()) {
                activity.saveScore(gm.getScore()); // שמירת הניקוד ב-Firebase
                invalidate(); // ציור אחרון להצגת מסך Game Over
                return;
            }

            gm.update(); // הזזת הנחש ובדיקה אם הוא אכל או התנגש
            invalidate(); // קריאה למערכת לצייר את המסך מחדש (מפעיל את onDraw)
            handler.postDelayed(this, 160); // הרצה חוזרת של הפעולה בעוד 160 מיל'
        }
    };

    public GameView(Context context) { // מזומן כשלוחצים play
        super(context);
        this.activity = (GameActivity) context;
        this.gm = new GameModule();
        // התחלת הלולאה
        handler.postDelayed(tick, 160);
    }

    // פעולה שרצה כשהמסך משנה גודל (או נפתח לראשונה) - חישוב גודל המשבצות
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  // כל פעם שהמסך משתנה
        super.onSizeChanged(w, h, oldw, oldh);
        float cellW = (float) w / GameModule.COLS;
        float cellH = (float) h / GameModule.ROWS;
        cellSize = Math.min(cellW, cellH); // בחירת הגודל הקטן ביותר כדי שהלוח יהיה ריבועי

        // חישוב המרכז של המסך
        offsetX = (w - (cellSize * GameModule.COLS)) / 2;
        offsetY = (h - (cellSize * GameModule.ROWS)) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //  ציור רקע כהה
        canvas.drawColor(Color.rgb(10, 10, 14));

        //  ציור מסגרת ללוח המשחק
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

        //  ציור הפרי (בצבע אדום)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        float fx = offsetX + gm.getFruit().getX() * cellSize;
        float fy = offsetY + gm.getFruit().getY() * cellSize;
        canvas.drawRoundRect(fx, fy, fx + cellSize, fy + cellSize, 12, 12, paint);

        //  ציור הנחש (עוברים על כל חלקי הגוף שלו)
        for (int i = 0; i < gm.getSnake().getBody().size(); i = i + 1) {
            SnakePart part = gm.getSnake().getBody().get(i);
            float sx = offsetX + part.getX() * cellSize;
            float sy = offsetY + part.getY() * cellSize;

            if (i == 0) {
                paint.setColor(Color.rgb(0, 255, 140)); // צבע לראש הנחש
            } else {
                paint.setColor(Color.rgb(0, 200, 110)); // צבע לשאר הגוף
            }
            canvas.drawRoundRect(sx, sy, sx + cellSize, sy + cellSize, 14, 14, paint);
        }

        //  ציור הניקוד הנוכחי בראש המסך
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        paint.setFakeBoldText(true);
        canvas.drawText("Score: " + gm.getScore(), 50, 80, paint);

        // הודעת סוף משחק (Game Over או You Win)
        if (gm.isGameOver()) {
            paint.setColor(Color.argb(180, 0, 0, 0)); // רקע חצי שקוף
            canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

            paint.setColor(Color.WHITE);
            paint.setTextSize(100);
            
            // בדיקה האם הסיום הוא בגלל ניצחון או הפסד
            String message = "GAME OVER";
            if (gm.getScore() >= 5010) {
                message = "YOU WIN!";
                paint.setColor(Color.YELLOW); // צבע זהב לניצחון
            }
            
            canvas.drawText(message, getWidth() / 2f - 250, getHeight() / 2f, paint);
            
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            canvas.drawText("Tap to play again", getWidth() / 2f - 180, getHeight() / 2f + 100, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchX = event.getX();
            touchY = event.getY();

            // אם המשחק נגמר ונגענו במסך - מתחילים מחדש
            if (gm.isGameOver()) {
                gm.reset();
                // עצירת כל לולאה קודמת לפני שמתחילים חדשה כדי למנוע כפילויות וקריסות
                handler.removeCallbacks(tick);
                handler.post(tick); // מזמנת את tick מחדש5
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
