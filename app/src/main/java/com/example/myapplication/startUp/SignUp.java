package com.example.myapplication.startUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText email, name, password, confirmPassword;
    Spinner chooseCat;
    CheckBox termsCheckBox;
    boolean isPasswordVisible = false;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String selectedCategory = "";
    Button saveBttn, cancelBttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.schoolEmail);
        name = findViewById(R.id.name);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPwd);
        chooseCat = findViewById(R.id.chooseCategory);
        termsCheckBox = findViewById(R.id.tNC);
        saveBttn = findViewById(R.id.saveBttn);
        cancelBttn = findViewById(R.id.cancelBttn);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseCat.setAdapter(adapter);

        chooseCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = "";
            }
        });

        saveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        cancelBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        termsCheckBox.setOnClickListener(v -> showTermsAndConditions());

        saveBttn.setOnClickListener(v -> handleSignUp());

        password.setOnTouchListener((v, event) -> {
            togglePasswordVisibility();
            return true;
        });
    }

    private void handleSignUp() {
        String userEmail = email.getText().toString().trim();
        String userName = name.getText().toString().trim();
        String userPassword = password.getText().toString();
        String confirmPwd = confirmPassword.getText().toString();

        if (!isValidEmail(userEmail)) {
            email.setError("Invalid email format.");
            return;
        }

        if (userPassword.length() < 8) {
            password.setError("Password must be at least 8 characters long");
            return;
        }

        if (!termsCheckBox.isChecked()) {
            Toast.makeText(SignUp.this, "Please accept Terms and Conditions", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userEmail.isEmpty() || userName.isEmpty() || userPassword.isEmpty() || confirmPwd.isEmpty()) {
            Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(confirmPwd)) {
            confirmPassword.setError("Passwords do not match");
            return;
        }

        if (selectedCategory.equals("Choose Category") || selectedCategory.isEmpty()) {
            Toast.makeText(SignUp.this, "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        fAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = fAuth.getCurrentUser();

                        DocumentReference df = fStore.collection("Users").document(user.getUid());
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("UserEmail", userEmail);
                        userInfo.put("FullName", userName);
                        userInfo.put("Category", selectedCategory);
                        userInfo.put("userRole", "user");

                        df.set(userInfo)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(SignUp.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                    if (selectedCategory.equalsIgnoreCase("Student")) {
                                        startActivity(new Intent(SignUp.this, StudentCreateProfile.class));
                                    } else {
                                        startActivity(new Intent(SignUp.this, FacultyCreateProfile.class));
                                    }
                                    finish();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(SignUp.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUp.this, "Failed to create account: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
        return email.matches(emailPattern);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_fill, 0);

        } else {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_fill, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        password.setSelection(password.getText().length());
    }

    private void showTermsAndConditions() {
        String terms = "Terms and Conditions\n\n" +
                "1. INTRODUCTION\n" +
                "   Welcome to SAMCIS Spaces. By using our application, you agree to comply with these terms.\n\n" +
                "2. USER ACCOUNTS\n" +
                "   - You must provide accurate information during sign-up.\n" +
                "   - You are responsible for maintaining the confidentiality of your login credentials.\n\n" +
                "3. USER CONDUCT\n" +
                "   - No harmful, abusive, or illegal activity is allowed.\n" +
                "   - Respect the privacy and data of other users.\n\n" +
                "4. DATA PRIVACY\n" +
                "   Your personal information will be handled according to our Privacy Policy.\n\n" +
                "5. LIMITATION OF LIABILITY\n" +
                "   We are not liable for any damages caused by misuse of the app.\n\n" +
                "6. TERMINATION\n" +
                "   We may suspend or terminate your access for violating these terms.\n\n" +
                "7. CONTACT US\n" +
                "   For questions or support, contact: support@samcisspaces.com";

        new AlertDialog.Builder(this)
                .setTitle("Terms and Conditions")
                .setMessage(terms)
                .setPositiveButton("OK", null)
                .show();
    }
}
