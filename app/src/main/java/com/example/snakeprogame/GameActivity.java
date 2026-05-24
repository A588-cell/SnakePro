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

        // אתחול Firebase
        mAuth = FirebaseAuth.getInstance();
        scoresRef = FirebaseDatabase.getInstance().getReference("scores");

        // יצירת תצוגת המשחק והצגתה
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    public void saveScore(int score) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "עליך להתחבר כדי לשמור ניקוד", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        String email = user.getEmail();

        // בדיקה האם הציון הנוכחי גבוה מהשיא הקיים במסד הנתונים
        scoresRef.child(uid).child("score").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snapshot = task.getResult();
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
