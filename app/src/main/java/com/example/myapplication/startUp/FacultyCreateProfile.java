package com.example.myapplication.startUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.userFx.UserHomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FacultyCreateProfile extends AppCompatActivity {

    EditText idNum;
    Spinner deptCategory;
    Button cancelBtn, saveBtn;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_faculty);

        db = FirebaseFirestore.getInstance();

        idNum = findViewById(R.id.idNum);
        deptCategory = findViewById(R.id.deptCategory);
        cancelBtn = findViewById(R.id.cancelBttn);
        saveBtn = findViewById(R.id.saveBttn);

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
        deptCategory.setAdapter(adapter);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyCreateProfile.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idNumber = idNum.getText().toString().trim();
                String selectedDept = deptCategory.getSelectedItem().toString();

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
        });
    }

    private void saveProfileToFirestore(String idNumber, String departments) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("ID Number", idNumber);
        userProfile.put("Department", departments);

        db.collection("Users")
                .add(userProfile)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(FacultyCreateProfile.this, "Profile Saved Successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(FacultyCreateProfile.this, UserHomeFragment.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FacultyCreateProfile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
