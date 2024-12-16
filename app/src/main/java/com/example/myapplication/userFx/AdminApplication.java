package com.example.myapplication.userFx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminApplication extends AppCompatActivity {

    private EditText enterCode;
    private Button cancelBttn, saveBttn;

    private static final String VALID_CODE = "123454321";

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_application);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        enterCode = findViewById(R.id.enterCode);
        cancelBttn = findViewById(R.id.cancelBttn);
        saveBttn = findViewById(R.id.saveBttn);

        cancelBttn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminApplication.this, UsersEditProfileActivity.class);
            startActivity(intent);
            finish();
        });

        userRole = getIntent().getStringExtra("UserRole");

        if (userRole == null || userRole.isEmpty()) {
            fetchUserRoleFromFirestore();
        } else {
        }

        saveBttn.setOnClickListener(v -> verifyCode());
    }

    private void fetchUserRoleFromFirestore() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userRole = documentSnapshot.getString("UserRole");
                        } else {
                            Toast.makeText(this, "User data not found!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to fetch user role!", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void verifyCode() {
        String codeInput = enterCode.getText().toString().trim();

        if (codeInput.isEmpty()) {
            enterCode.setError("Please enter the verification code.");
            return;
        }

        if (codeInput.equals(VALID_CODE)) {
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                String userId = currentUser.getUid();

                db.collection("Users").document(userId)
                        .update("UserRole", "Admin")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AdminApplication.this, "Verification successful! You are now an Admin.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(AdminApplication.this, MainActivity.class);
                            intent.putExtra("UserRole", "Admin");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(AdminApplication.this, "Failed to update role: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AdminApplication.this, "Invalid verification code. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
