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
import com.example.myapplication.userFx.EditProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileFragment extends Fragment {

    TextView userName, userCategory, idNumInfo, programInfo, yearLevelInfo, departmentInfo, editProfile;
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
                            String name = documentSnapshot.contains("FullName") ? documentSnapshot.getString("FullName") : "N/A";
                            String category = documentSnapshot.contains("Category") ? documentSnapshot.getString("Category") : "N/A";

                            userName.setText(name);
                            userCategory.setText(category);

                            if (documentSnapshot.contains("idNumber")) {
                                idNumInfo.setText(documentSnapshot.getString("idNumber"));
                            }

                            if ("Student".equalsIgnoreCase(category)) {
                                programInfo.setText(documentSnapshot.contains("Program") ? documentSnapshot.getString("Program") : "N/A");
                                yearLevelInfo.setText(documentSnapshot.contains("yearLevel") ? documentSnapshot.getString("yearLevel") : "N/A");

                                programInfo.setVisibility(View.VISIBLE);
                                yearLevelInfo.setVisibility(View.VISIBLE);
                                departmentInfo.setVisibility(View.GONE);

                            } else if ("Faculty".equalsIgnoreCase(category)) {
                                departmentInfo.setText(documentSnapshot.contains("Department") ? documentSnapshot.getString("Department") : "N/A");

                                programInfo.setVisibility(View.GONE);
                                yearLevelInfo.setVisibility(View.GONE);
                                departmentInfo.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(getActivity(), "User data not found!", Toast.LENGTH_SHORT).show();
                        }

                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Failed to load profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        }
    }
}
