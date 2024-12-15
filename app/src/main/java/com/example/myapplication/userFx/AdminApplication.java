package com.example.myapplication.userFx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class AdminApplication extends AppCompatActivity {

    private EditText enterCode;
    private Button cancelBttn, saveBttn;

    private static final String VALID_CODE = "123454321";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_application);

        enterCode = findViewById(R.id.enterCode);
        cancelBttn = findViewById(R.id.cancelBttn);
        saveBttn = findViewById(R.id.saveBttn);

        cancelBttn.setOnClickListener(v -> {
            Intent intent = new Intent(AdminApplication.this, EditProfileActivity.class);
            startActivity(intent);
            finish();
        });

        saveBttn.setOnClickListener(v -> verifyCode());
    }

    private void verifyCode() {
        String codeInput = enterCode.getText().toString().trim();

        if (codeInput.isEmpty()) {
            enterCode.setError("Please enter the verification code.");
            return;
        }

        if (codeInput.equals(VALID_CODE)) {
            Toast.makeText(AdminApplication.this, "Verification successful! You are now an Admin.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(AdminApplication.this, EditProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(AdminApplication.this, "Invalid verification code. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}
