package com.example.snakeprogame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.snakeprogame.game.GameView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // אתחול כלי האימות של Firebase (זיהוי משתמשים)
        mAuth = FirebaseAuth.getInstance();
        
        // יצירת קישור לתיקיית "scores" במסד הנתונים של Firebase
        scoresRef = FirebaseDatabase.getInstance().getReference("scores");

        // יצירת תצוגת המשחק (הנחש והלוח) והצגתה על המסך
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    public void saveScore(int score) {
        // בדיקה האם יש משתמש שמחובר כרגע למערכת
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "עליך להתחבר כדי שיישמר עבורך ניקוד", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        String email = user.getEmail();

        // שליפת הניקוד הקיים ובדיקה מתי הפעולה מסתיימת
        scoresRef.child(uid).child("score").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                // בדיקה האם הפעולה מול Firebase הצליחה
                if (task.isSuccessful()) {
                    // קבלת צילום מצב של הנתונים שחזרו
                    DataSnapshot snapshot = task.getResult();
                    
                    // המרת הנתון  למספר שלם (Integer) ב-Java
                    Integer oldScore = snapshot.getValue(Integer.class);

                    // אם אין ניקוד קודם או שהניקוד הנוכחי גבוה יותר, נעדכן את ה-Firebase
                    if (oldScore == null || score > oldScore) {
                        scoresRef.child(uid).child("email").setValue(email);
                        scoresRef.child(uid).child("score").setValue(score);
                        Toast.makeText(GameActivity.this, "שיא חדש נשמר: " + score, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(GameActivity.this, "השיא הקודם שלך גבוה יותר", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GameActivity.this, "שגיאה בגישה לנתוני הניקוד", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
