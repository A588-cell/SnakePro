package com.example.snakeprogame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnRegister, btnLogin, btnPlay, btnInstructions, btnScoreBoard;
    
    // הצהרה על משתנה לניהול אימות המשתמשים מול Firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // אתחול האובייקט המאפשר גישה לשירותי האימות של Firebase
        mAuth = FirebaseAuth.getInstance();
        
        // התנתקות אוטומטית בכל פתיחה של האפליקציה כדי להתחיל "נקי"
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnPlay = findViewById(R.id.btnPlay);
        btnInstructions = findViewById(R.id.btnInstructions);
        btnScoreBoard = findViewById(R.id.btnScoreBoard);

        // הגדרת מאזין לכפתור הרשמה
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // הגדרת מאזין לכפתור התחברות
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // הגדרת מאזין לכפתור משחק
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        // הגדרת מאזין לכפתור הוראות
        btnInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
                startActivity(intent);
            }
        });

        // הגדרת מאזין לכפתור טבלת שיאים
        btnScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScoreBoardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        // שליפת הטקסט מהשדות והסרת רווחים מיותרים
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // בדיקה האם שדה האימייל ריק
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("נא להזין אימייל"); // הצגת הודעת שגיאה על גבי השדה
            etEmail.requestFocus(); // העברת המיקוד (סמן) לשדה
            return;
        }

        // בדיקה האם שדה הסיסמה ריק
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("נא להזין סיסמה");
            etPassword.requestFocus();
            return;
        }

        // בדיקה האם הסיסמה קצרה מדי (פחות מ-6 תווים)
        if (password.length() < 6) {
            etPassword.setError("סיסמה חייבת להיות לפחות 6 תווים");
            etPassword.requestFocus();
            return;
        }

        // שליחת בקשה ל-Firebase ליצירת משתמש חדש עם אימייל וסיסמה
        // הגדרת "מאזין" שיפעל ברגע שתתקבל תשובה מהשרת
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userEmail = "";
                            if (user != null) {
                                userEmail = user.getEmail();
                            }
                            Toast.makeText(MainActivity.this, "נרשמת בהצלחה: " + userEmail, Toast.LENGTH_SHORT).show();
                        } else {
                            // במקרה של כישלון, נציג הודעה כללית
                            Toast.makeText(MainActivity.this, "הרשמה נכשלה", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void loginUser() {
        // שליפת הטקסט מהשדות והסרת רווחים מיותרים
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // בדיקה האם שדה האימייל ריק
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("נא להזין אימייל");
            etEmail.requestFocus();
            return;
        }

        // בדיקה האם שדה הסיסמה ריק
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("נא להזין סיסמה");
            etPassword.requestFocus();
            return;
        }

        // בדיקה האם הסיסמה קצרה מדי (פחות מ-6 תווים)
        if (password.length() < 6) {
            etPassword.setError("סיסמה חייבת להיות לפחות 6 תווים");
            etPassword.requestFocus();
            return;
        }

        // שליחת בקשה ל-Firebase לכניסת משתמש קיים
        // הגדרת "מאזין" שיפעל ברגע שתתקבל תשובה מהשרת
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userEmail = "";
                            if (user != null) {
                                userEmail = user.getEmail();
                            }
                            Toast.makeText(MainActivity.this, "התחברת בהצלחה: " + userEmail, Toast.LENGTH_SHORT).show();
                        } else {
                            // במקרה של כישלון, נציג הודעה כללית
                            Toast.makeText(MainActivity.this, "התחברות נכשלה", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
