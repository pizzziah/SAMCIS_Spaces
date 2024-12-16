package com.example.myapplication.adminFx;

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

public class AdminEditProfileActivity extends AppCompatActivity {
    EditText emailInput, nameInput, pwdInput, confirmPwd, departmentInput, yearLevelInput, programInput;
    Button cancelBttn, saveBttn;

    FirebaseAuth auth;
    FirebaseFirestore db;

    String userCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_admin);

        userCategory = getIntent().getStringExtra("UserCategory");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailInput = findViewById(R.id.editEmail);
        nameInput = findViewById(R.id.editName);
        pwdInput = findViewById(R.id.editPassword);
        confirmPwd = findViewById(R.id.confirmPwd);
        cancelBttn = findViewById(R.id.cancelBttn);
        saveBttn = findViewById(R.id.saveBttn);

        departmentInput = findViewById(R.id.editDepartment);
        yearLevelInput = findViewById(R.id.editYearLevel);
        programInput = findViewById(R.id.editProgram);

        userCategory = getIntent().getStringExtra("UserCategory");

        updateFieldVisibility();

        cancelBttn.setOnClickListener(v -> {
            startActivity(new Intent(AdminEditProfileActivity.this, com.example.myapplication.adminFx.AdminEditProfileActivity.class));
        });


        saveBttn.setOnClickListener(v -> updateUserProfile());


    }

    private void updateFieldVisibility() {
        departmentInput.setVisibility(View.GONE);
        yearLevelInput.setVisibility(View.GONE);
        programInput.setVisibility(View.GONE);

        if ("Faculty".equalsIgnoreCase(userCategory)) {
            departmentInput.setVisibility(View.VISIBLE);
        } else if ("Student".equalsIgnoreCase(userCategory)) {
            yearLevelInput.setVisibility(View.VISIBLE);
            programInput.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "User category is undefined!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserProfile() {
        String email = emailInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();
        String pwd = pwdInput.getText().toString().trim();
        String confirmPass = confirmPwd.getText().toString().trim();
        String department = departmentInput.getText().toString().trim();
        String yearLevel = yearLevelInput.getText().toString().trim();
        String program = programInput.getText().toString().trim();

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            Map<String, Object> updates = new HashMap<>();

            if (!TextUtils.isEmpty(name)) {
                updates.put("FullName", name);
            }
            if ("Faculty".equalsIgnoreCase(userCategory) && !TextUtils.isEmpty(department)) {
                updates.put("Department", department);
            }
            if ("Student".equalsIgnoreCase(userCategory)) {
                if (!TextUtils.isEmpty(yearLevel)) {
                    updates.put("YearLevel", yearLevel);
                }
                if (!TextUtils.isEmpty(program)) {
                    updates.put("Program", program);
                }
            }

            db.collection("Users").document(userId)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(AdminEditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();

                        if (!TextUtils.isEmpty(email)) {
                            user.updateEmail(email)
                                    .addOnSuccessListener(a -> Toast.makeText(this, "Email updated", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(this, "Email update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                        }

                        if (!TextUtils.isEmpty(pwd)) {
                            if (pwd.equals(confirmPass)) {
                                user.updatePassword(pwd)
                                        .addOnSuccessListener(a -> Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(this, "Password update failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                            } else {
                                confirmPwd.setError("Passwords do not match");
                                return;
                            }
                        }

                        Intent intent = new Intent(AdminEditProfileActivity.this, com.example.myapplication.adminFx.AdminEditProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AdminEditProfileActivity.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}