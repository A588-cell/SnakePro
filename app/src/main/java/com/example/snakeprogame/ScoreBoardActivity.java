package com.example.snakeprogame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreBoardActivity extends AppCompatActivity {

    private TextView tvScores;
    private Button btnBack;
    private DatabaseReference scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        tvScores = findViewById(R.id.tvScores);
        btnBack = findViewById(R.id.btnBack);

        // הגדרת כפתור חזרה למסך הראשי
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // סגירת המסך הנוכחי
            }
        });

        // אתחול הקישור לתיקיית "scores" ב-Firebase
        scoresRef = FirebaseDatabase.getInstance().getReference("scores");

        // טעינת הנתונים מהענן
        loadScores();
    }


    private void loadScores() {
        // שליחת בקשה לקבלת הנתונים מהענן
        scoresRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    // יצירת רשימה זמנית לאחסון התוצאות שנחלץ
                    ArrayList<PlayerScore> scores = new ArrayList<PlayerScore>();

                    // מעבר בלולאה על כל רשומה בתוך התוצאה שחזרה
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        // חילוץ האימייל והניקוד מתוך הרשומה
                        String email = snapshot.child("email").getValue(String.class);
                        Integer scoreValue = snapshot.child("score").getValue(Integer.class);

                        // אם הנתונים קיימים, נוסיף אותם לרשימה שלנו
                        if (email != null && scoreValue != null) {
                            scores.add(new PlayerScore(email, scoreValue));
                        }
                    }

                    // מיון הרשימה מהניקוד הגבוה לנמוך
                    Collections.sort(scores, new Comparator<PlayerScore>() {
                        @Override
                        public int compare(PlayerScore a, PlayerScore b) {
                            // מחזיר את ההפרש בין ב' ל-א' כדי לקבל סדר יורד
                            return b.score - a.score;
                        }
                    });

                    // בדיקה אם הרשימה ריקה
                    if (scores.isEmpty()) {
                        tvScores.setText("אין תוצאות להצגה");
                        return;
                    }

                    // בניית הטקסט להצגה סופית על המסך
                    StringBuilder builder = new StringBuilder();
                    
                    // קביעת כמות השורות שנציג (המקסימום הוא 10)
                    int count = Math.min(scores.size(), 10);

                    for (int i = 0; i < count; i++) {
                        PlayerScore ps = scores.get(i);

                        // חילוץ השם מתוך האימייל (כל מה שלפני ה-@)
                        String name = ps.email;
                        if (name != null && name.contains("@")) {
                            name = name.substring(0, name.indexOf("@"));
                        }

                        // הוספת שורה לטקסט: מקום, שם וניקוד
                        builder.append(i + 1)
                                .append(". ")
                                .append(name)
                                .append(" - ")
                                .append(ps.score)
                                .append("\n");
                    }

                    // הצגת כל הטקסט שבנינו בתוך ה-TextView
                    tvScores.setText(builder.toString());

                } else {
                    Toast.makeText(ScoreBoardActivity.this, "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static class PlayerScore {
        String email;
        int score;

        PlayerScore(String email, int score) {
            this.email = email;
            this.score = score;
        }
    }
}
