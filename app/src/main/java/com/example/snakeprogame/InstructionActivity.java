package com.example.snakeprogame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InstructionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        Button btnBack = findViewById(R.id.btnBack);
        
        // הגדרת מאזין
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // סגירת המסך הנוכחי וחזרה למסך הקודם
                finish();
            }
        });
    }
}
