package com.example.myapplication.startUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.main.MainActivity;
import com.example.myapplication.userFx.UserHomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private EditText email, password;
    private Button loginButton;
    private TextView signUp;
    private boolean isPasswordVisible = false;

    private FirebaseAuth fAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signUp = findViewById(R.id.signUp);

        // Set listeners
        password.setOnClickListener(v -> togglePasswordVisibility());

        loginButton.setOnClickListener(v -> handleLogin());

        signUp.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUp.class));
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_slash, 0);
        } else {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_fill, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        password.setSelection(password.getText().length());
    }

    private void handleLogin() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (!validateInputFields(userEmail, userPassword)) {
            return;
        }

        fAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = fAuth.getCurrentUser().getUid();
                        fetchUserDetails(userId);
                    } else {
                        Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateInputFields(String emailInput, String passwordInput) {
        if (emailInput.isEmpty()) {
            email.setError("Email is required.");
            email.requestFocus();
            return false;
        }
        if (passwordInput.isEmpty()) {
            password.setError("Password is required.");
            password.requestFocus();
            return false;
        }
        return true;
    }

    private void fetchUserDetails(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Retrieve user data from Firestore
                        String userRole = task.getResult().getString("UserRole");

                        if (userRole != null) {
                            userRole = userRole.trim();
                            android.util.Log.d("USER_ROLE", "Fetched role: " + userRole);

                            if ("User".equalsIgnoreCase(userRole)) {
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                intent.putExtra("fragment", UserHomeFragment.class.getName());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Login.this, "Invalid user role. Role: " + userRole, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(Login.this, "User data not found in Firestore.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Login.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
