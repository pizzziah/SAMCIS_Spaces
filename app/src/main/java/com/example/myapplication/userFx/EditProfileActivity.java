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
    EditText emailInput, nameInput, pwdInput, confirmPwd, programInput, yearLevelInput, departmentInput;
    TextView adminApply;
    Button cancelBttn, saveBttn;
    String userCategory;
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

        // Add inputs for program, year level, and department
        programInput = findViewById(R.id.editProgram);
        yearLevelInput = findViewById(R.id.editYearLevel);
        departmentInput = findViewById(R.id.editDepartment);

        cancelBttn.setOnClickListener(v -> finish());

        saveBttn.setOnClickListener(v -> updateUserProfile());
        adminApply.setOnClickListener(v -> applyAsAdminPopup());

        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            db.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String category = documentSnapshot.contains("Category") ? documentSnapshot.getString("Category") : "N/A";

                            // Show or hide fields based on category
                            if ("Student".equalsIgnoreCase(category)) {
                                programInput.setVisibility(View.VISIBLE);
                                yearLevelInput.setVisibility(View.VISIBLE);
                                departmentInput.setVisibility(View.GONE);

                                // Pre-fill the student fields
                                programInput.setText(documentSnapshot.contains("Program") ? documentSnapshot.getString("Program") : "");
                                yearLevelInput.setText(documentSnapshot.contains("Year Level") ? documentSnapshot.getString("Year Level") : "");

                            } else if ("Faculty".equalsIgnoreCase(category)) {
                                programInput.setVisibility(View.GONE);
                                yearLevelInput.setVisibility(View.GONE);
                                departmentInput.setVisibility(View.VISIBLE);

                                // Pre-fill the faculty department field
                                departmentInput.setText(documentSnapshot.contains("Department") ? documentSnapshot.getString("Department") : "");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void updateUserProfile() {
        String email = emailInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String pwd = pwdInput.getText().toString().trim();
        String confirmPass = confirmPwd.getText().toString().trim();
        String program = programInput.getText().toString().trim();
        String yearLevel = yearLevelInput.getText().toString().trim();
        String department = departmentInput.getText().toString().trim();

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

            // Add Program, Year Level, or Department based on the category
            FirebaseUser currentUser = auth.getCurrentUser();
            String category = currentUser.getDisplayName(); // Assuming category is stored as displayName for simplicity

            if ("Student".equalsIgnoreCase(category)) {
                updates.put("Program", program);
                updates.put("Year Level", yearLevel);
            } else if ("Faculty".equalsIgnoreCase(category)) {
                updates.put("Department", department);
            }

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
