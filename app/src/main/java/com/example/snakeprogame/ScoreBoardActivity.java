package com.example.snakeprogame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

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

        btnBack.setOnClickListener(v -> finish());

        scoresRef = FirebaseDatabase.getInstance().getReference("scores");

        loadScores();
    }

    private void loadScores() {
        scoresRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<PlayerScore> scores = new ArrayList<>();

                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String email = snapshot.child("email").getValue(String.class);
                    Integer score = snapshot.child("score").getValue(Integer.class);

                    if (email != null && score != null) {
                        scores.add(new PlayerScore(email, score));
                    }
                }

                Collections.sort(scores, (a, b) -> b.score - a.score);

                if (scores.isEmpty()) {
                    tvScores.setText("No scores yet");
                    return;
                }

                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < scores.size(); i++) {
                    PlayerScore ps = scores.get(i);

                    String name = ps.email;
                    if (name.contains("@")) {
                        name = name.substring(0, name.indexOf("@"));
                    }

                    builder.append(i + 1)
                            .append(". ")
                            .append(name)
                            .append(" - ")
                            .append(ps.score)
                            .append("\n");
                }

                tvScores.setText(builder.toString());

            } else {
                Toast.makeText(this, "Failed to load scores", Toast.LENGTH_SHORT).show();
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