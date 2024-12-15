package com.example.myapplication.startUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.main.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class StudentCreateProfile extends AppCompatActivity {

    EditText idNum, yearLevel;
    Spinner programSpinner;
    Button cancelBttn, saveBttn;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_student);

        db = FirebaseFirestore.getInstance();
        userId = getIntent().getStringExtra("USER_ID");

        idNum = findViewById(R.id.idNum);
        yearLevel = findViewById(R.id.yearLevel);
        programSpinner = findViewById(R.id.programCategory);
        cancelBttn = findViewById(R.id.cancelBttn);
        saveBttn = findViewById(R.id.saveBttn);

        String[] programs = {
                "Choose Program",
                "BS in Accountancy",
                "BS in Management Accounting",
                "BS in Business Administration major in Financial Management with specialization Business Analytics",
                "BS in Business Administration major in Marketing Management with specialization in Business Analytics",
                "BS in Entrepreneurship with specialization in Business Analytics",
                "BS in Tourism Management",
                "BS in Hospitality Management",
                "BS in Computer Science",
                "BS in Information Technology",
                "BS in Multimedia Arts"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, programs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        programSpinner.setAdapter(adapter);

        cancelBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentCreateProfile.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        saveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idNumber = idNum.getText().toString().trim();
                String yearLevelValue = yearLevel.getText().toString().trim();
                String selectedProgram = programSpinner.getSelectedItem().toString();

                // Input Validation
                if (idNumber.isEmpty() || yearLevelValue.isEmpty() || selectedProgram.equals("Choose Program")) {
                    if (idNumber.isEmpty()) idNum.setError("This field is required");
                    if (yearLevelValue.isEmpty()) yearLevel.setError("This field is required");
                    if (selectedProgram.equals("Choose Program"))
                        Toast.makeText(StudentCreateProfile.this, "Please choose a program.", Toast.LENGTH_SHORT).show();
                } else {
                    saveStudentProfile(idNumber, yearLevelValue, selectedProgram);
                }
            }
        });
    }

    private void saveStudentProfile(String idNumber, String yearLevel, String program) {
        DocumentReference userRef = db.collection("Users").document(userId);

        Map<String, Object> studentProfile = new HashMap<>();
        studentProfile.put("ID Number", idNumber);
        studentProfile.put("Year Level", yearLevel);
        studentProfile.put("Program", program);
        studentProfile.put("ProfileComplete", true);

        userRef.update(studentProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(StudentCreateProfile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StudentCreateProfile.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StudentCreateProfile.this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
