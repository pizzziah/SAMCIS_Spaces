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

public class FacultyCreateProfile extends AppCompatActivity {

    EditText idNum;
    Spinner deptCat;
    Button cancelBttn, saveBttn;
    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_faculty);

        db = FirebaseFirestore.getInstance();

        userId = getIntent().getStringExtra("USER_ID");

        idNum = findViewById(R.id.idNum);
        deptCat = findViewById(R.id.deptCategory);
        cancelBttn = findViewById(R.id.cancelBttn);
        saveBttn = findViewById(R.id.saveBttn);

        String[] departments = {
                "Choose Department",
                "CIS",
                "Math",
                "Accountancy",
                "HTM",
                "Gen. Ed",
                "Non-Teaching"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, departments);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        deptCat.setAdapter(adapter);

        cancelBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyCreateProfile.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        saveBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idNumber = idNum.getText().toString().trim();
                String selectedDept = deptCat.getSelectedItem().toString();

                if (idNumber.isEmpty() || selectedDept.equals("Choose Department")) {
                    if (idNumber.isEmpty()) {
                        idNum.setError("This field is required");
                    }
                    if (selectedDept.equals("Choose Department")) {
                        Toast.makeText(FacultyCreateProfile.this, "Please select a valid department.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    saveProfileToFirestore(idNumber, selectedDept);
                }
            }
        });    }

    private void saveProfileToFirestore(String idNumber, String departments) {
        DocumentReference userRef = db.collection("Users").document(userId);

        Map<String, Object> facultyProfile = new HashMap<>();
        facultyProfile.put("ID Number", idNumber);
        facultyProfile.put("Department", departments);
        facultyProfile.put("ProfileComplete", true);

        userRef.update(facultyProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(FacultyCreateProfile.this, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FacultyCreateProfile.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FacultyCreateProfile.this, "Error saving profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
