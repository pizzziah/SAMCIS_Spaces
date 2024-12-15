package com.example.myapplication.userFx;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileFragment extends Fragment {

    TextView userName, userCategory, idNumInfo, programInfo, yearLevelInfo, departmentInfo, editProfile, programTitle, yearLevelTitle, departmentTitle;
    ImageView userImage;
    Button logoutButton;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.u_fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editProfile = view.findViewById(R.id.editProfile);
        userName = view.findViewById(R.id.userName);
        userCategory = view.findViewById(R.id.userCat);
        idNumInfo = view.findViewById(R.id.IDNumInfo);
        programInfo = view.findViewById(R.id.programInfo);
        yearLevelInfo = view.findViewById(R.id.yearLevelInfo);
        programTitle = view.findViewById(R.id.programTitle);
        yearLevelTitle = view.findViewById(R.id.yearLevelTitle);
        departmentTitle = view.findViewById(R.id.departmentTitle);
        departmentInfo = view.findViewById(R.id.departmentInfo);
        userImage = view.findViewById(R.id.img);
        logoutButton = view.findViewById(R.id.logoutButton);

        loadUserProfile();

        // Edit profile redirect
        editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
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
                            // Get Full Name and Category
                            String name = documentSnapshot.contains("FullName") ? documentSnapshot.getString("FullName") : "N/A";
                            String category = documentSnapshot.contains("Category") ? documentSnapshot.getString("Category") : "N/A";

                            // Set the values for Name and Category
                            userName.setText(name);
                            userCategory.setText(category);

                            // ID Number
                            if (documentSnapshot.contains("ID Number")) {
                                idNumInfo.setText(documentSnapshot.getString("ID Number"));
                            } else {
                                idNumInfo.setText("N/A");
                            }

                            // Display data based on category (Student or Faculty)
                            if ("Student".equalsIgnoreCase(category)) {
                                programInfo.setText(documentSnapshot.contains("Program") ? documentSnapshot.getString("Program") : "N/A");
                                yearLevelInfo.setText(documentSnapshot.contains("Year Level") ? documentSnapshot.getString("Year Level") : "N/A");

                                // Show student-related views and hide faculty-related views
                                programTitle.setVisibility(View.VISIBLE);
                                programInfo.setVisibility(View.VISIBLE);
                                yearLevelTitle.setVisibility(View.VISIBLE);
                                yearLevelInfo.setVisibility(View.VISIBLE);

                                departmentTitle.setVisibility(View.GONE);
                                departmentInfo.setVisibility(View.GONE);

                            } else if ("Faculty".equalsIgnoreCase(category)) {
                                departmentInfo.setText(documentSnapshot.contains("Department") ? documentSnapshot.getString("Department") : "N/A");

                                // Show faculty-related views and hide student-related views
                                programTitle.setVisibility(View.GONE);
                                programInfo.setVisibility(View.GONE);
                                yearLevelTitle.setVisibility(View.GONE);
                                yearLevelInfo.setVisibility(View.GONE);

                                departmentTitle.setVisibility(View.VISIBLE);
                                departmentInfo.setVisibility(View.VISIBLE);
                            }
                        } else {
                            // Show a message if the document does not exist
                            Toast.makeText(getActivity(), "User data not found!", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(e -> {
                        // Handle failure when reading document
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } else {
            Toast.makeText(getActivity(), "User not authenticated!", Toast.LENGTH_SHORT).show();
        }
    }

}
