package com.example.myapplication.adminFx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.startUp.Login;
import com.example.myapplication.userFx.UsersEditProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminProfileFragment extends Fragment {

    TextView userName, userCategory, idNumTitle, idNumInfo, programTitle, programInfo,
            yearLevelTitle, yearLevelInfo, departmentTitle, departmentInfo, editProfile;
    ImageView userImage;
    Button logoutButton;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.a_fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editProfile = view.findViewById(R.id.editProfile);
        userName = view.findViewById(R.id.userName);
        userCategory = view.findViewById(R.id.userCat);
        idNumTitle = view.findViewById(R.id.IDNumTitle);
        idNumInfo = view.findViewById(R.id.IDNumInfo);
        programTitle = view.findViewById(R.id.programTitle);
        programInfo = view.findViewById(R.id.programInfo);
        yearLevelTitle = view.findViewById(R.id.yearLevelTitle);
        yearLevelInfo = view.findViewById(R.id.yearLevelInfo);
        departmentTitle = view.findViewById(R.id.departmentTitle);
        departmentInfo = view.findViewById(R.id.departmentInfo);
        userImage = view.findViewById(R.id.img);
        logoutButton = view.findViewById(R.id.logoutButton);

        loadUserProfile();

        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UsersEditProfileActivity.class);
            intent.putExtra("UserCategory", userCategory.getText().toString());
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("Users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.contains("FullName") ? documentSnapshot.getString("FullName") : "N/A";
                            String category = documentSnapshot.contains("Category") ? documentSnapshot.getString("Category") : "N/A";
                            String idNumber = documentSnapshot.contains("ID Number") ? documentSnapshot.getString("ID Number") : "N/A";

                            userName.setText(name);
                            userCategory.setText(category);
                            idNumInfo.setText(idNumber);

                            if ("Student".equalsIgnoreCase(category)) {
                                String program = documentSnapshot.contains("Program") ? documentSnapshot.getString("Program") : "N/A";
                                String yearLevel = documentSnapshot.contains("Year Level") ? documentSnapshot.getString("Year Level") : "N/A";

                                programInfo.setText(program);
                                yearLevelInfo.setText(yearLevel);

                                programTitle.setVisibility(View.VISIBLE);
                                programInfo.setVisibility(View.VISIBLE);
                                yearLevelTitle.setVisibility(View.VISIBLE);
                                yearLevelInfo.setVisibility(View.VISIBLE);
                                idNumTitle.setVisibility(View.VISIBLE);
                                idNumInfo.setVisibility(View.VISIBLE);

                                departmentTitle.setVisibility(View.GONE);
                                departmentInfo.setVisibility(View.GONE);

                            } else if ("Faculty".equalsIgnoreCase(category)) {
                                String department = documentSnapshot.contains("Department") ? documentSnapshot.getString("Department") : "N/A";

                                departmentInfo.setText(department);

                                departmentTitle.setVisibility(View.VISIBLE);
                                departmentInfo.setVisibility(View.VISIBLE);
                                idNumTitle.setVisibility(View.VISIBLE);
                                idNumInfo.setVisibility(View.VISIBLE);

                                programTitle.setVisibility(View.GONE);
                                programInfo.setVisibility(View.GONE);
                                yearLevelTitle.setVisibility(View.GONE);
                                yearLevelInfo.setVisibility(View.GONE);

                            } else {
                                Toast.makeText(getActivity(), "Invalid category!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "User data not found!", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(getActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
        }
    }
}
