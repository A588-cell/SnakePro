package com.example.snakeprogame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.snakeprogame.game.GameView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference scoresRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        scoresRef = FirebaseDatabase.getInstance().getReference("scores");

        setContentView(new GameView(this));
    }

    public void saveScore(int score) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Login first to save score", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        String email = user.getEmail();

        scoresRef.child(uid).child("score").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Integer oldScore = task.getResult().getValue(Integer.class);

                if (oldScore == null || score > oldScore) {
                    scoresRef.child(uid).child("email").setValue(email);
                    scoresRef.child(uid).child("score").setValue(score);

                    Toast.makeText(this, "New high score saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Score saved only if it is a new record", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to check score", Toast.LENGTH_SHORT).show();
            }
        });
    }
}