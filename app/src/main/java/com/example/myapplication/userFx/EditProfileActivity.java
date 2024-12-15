package com.example.myapplication.userFx;

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
import com.example.myapplication.startUp.FacultyCreateProfile;
import com.example.myapplication.startUp.SignUp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    EditText emailInput, nameInput, pwdInput, confirmPwd, departmentInput, yearLevelInput, programInput;
    TextView adminApply;
    Button cancelBttn, saveBttn;

    FirebaseAuth auth;
    FirebaseFirestore db;

    String userCategory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        emailInput = findViewById(R.id.editEmail);
        nameInput = findViewById(R.id.editName);
        pwdInput = findViewById(R.id.editPassword);
        confirmPwd = findViewById(R.id.confirmPwd);
        adminApply = findViewById(R.id.applyAsAdmin);
        cancelBttn = findViewById(R.id.cancelBttn);
        saveBttn = findViewById(R.id.saveBttn);

        // New EditTexts for additional fields
        departmentInput = findViewById(R.id.editDepartment);
        yearLevelInput = findViewById(R.id.editYearLevel);
        programInput = findViewById(R.id.editProgram);

        userCategory = getIntent().getStringExtra("UserCategory");

        updateFieldVisibility();

        cancelBttn.setOnClickListener(v -> finish());
        saveBttn.setOnClickListener(v -> updateUserProfile());

        adminApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditProfileActivity.this, AdminApplication.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updateFieldVisibility() {
        if ("Faculty".equalsIgnoreCase(userCategory)) {
            departmentInput.setVisibility(View.VISIBLE);
            yearLevelInput.setVisibility(View.GONE);
            programInput.setVisibility(View.GONE);
        } else if ("Student".equalsIgnoreCase(userCategory)) {
            yearLevelInput.setVisibility(View.VISIBLE);
            programInput.setVisibility(View.VISIBLE);
            departmentInput.setVisibility(View.GONE);
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

            if (TextUtils.isEmpty(name)) {
                nameInput.setError("Name cannot be empty");
                return;
            }

            if (!TextUtils.isEmpty(pwd) && !pwd.equals(confirmPass)) {
                confirmPwd.setError("Passwords do not match");
                return;
            }

            Map<String, Object> updates = new HashMap<>();
            updates.put("FullName", name);

            if ("Faculty".equalsIgnoreCase(userCategory)) {
                updates.put("Department", department);
            } else if ("Student".equalsIgnoreCase(userCategory)) {
                updates.put("YearLevel", yearLevel);
                updates.put("Program", program);
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
}
