package com.example.myapplication.userFx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    EditText emailInput, nameInput, pwdInput, confirmPwd;
    TextView adminApply;
    Button cancelBttn, saveBttn;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailInput = findViewById(R.id.editEmail);
        nameInput = findViewById(R.id.editName);
        pwdInput = findViewById(R.id.editPassword);
        confirmPwd = findViewById(R.id.confirmPwd);
        adminApply = findViewById(R.id.applyAsAdmin);
        cancelBttn = findViewById(R.id.cancelBttn);
        saveBttn = findViewById(R.id.saveBttn);

        cancelBttn.setOnClickListener(v -> {
            finish();
        });

        saveBttn.setOnClickListener(v -> updateUserProfile());
        adminApply.setOnClickListener(v -> applyAsAdminPopup());
    }

    private void updateUserProfile() {
        String email = emailInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String pwd = pwdInput.getText().toString().trim();
        String confirmPass = confirmPwd.getText().toString().trim();

        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            // Validate Input
            if (TextUtils.isEmpty(name)) {
                nameInput.setError("Name cannot be empty");
                return;
            }

            if (!TextUtils.isEmpty(pwd) && !pwd.equals(confirmPass)) {
                confirmPwd.setError("Passwords do not match");
                return;
            }

            // Update Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("FullName", name);

            db.collection("Users").document(userId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        if (!TextUtils.isEmpty(email)) {
                            user.updateEmail(email)
                                    .addOnSuccessListener(a -> Toast.makeText(this, "Email updated", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(this, "Email update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }

                        if (!TextUtils.isEmpty(pwd)) {
                            user.updatePassword(pwd)
                                    .addOnSuccessListener(a -> Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(this, "Password update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }

                        Intent intent = new Intent(EditProfileActivity.this, UserProfileFragment.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void applyAsAdminPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Apply as Admin");
        builder.setMessage("Are you sure you want to apply as an admin? This will send a request for approval.");

        builder.setPositiveButton("Apply", (dialog, which) -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();

                Map<String, Object> adminRequest = new HashMap<>();
                adminRequest.put("adminRequest", true);

                db.collection("Users").document(userId)
                        .update(adminRequest)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(EditProfileActivity.this, "Admin request sent successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(EditProfileActivity.this, "Failed to send admin request: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
