package com.example.myapplication.userFx;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.startUp.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfileFragment extends Fragment {

    TextView userName, userCategory, idNumInfo, programInfo, yearLevelInfo, departmentInfo;
    ImageView userImage;
    Button logoutButton, applyAdminButton;
    FirebaseAuth auth;
    FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.u_fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userName = view.findViewById(R.id.userName);
        userCategory = view.findViewById(R.id.userCat);
        idNumInfo = view.findViewById(R.id.IDNumInfo);
        programInfo = view.findViewById(R.id.programInfo);
        yearLevelInfo = view.findViewById(R.id.yearLevelInfo);
        departmentInfo = view.findViewById(R.id.departmentInfo);
        userImage = view.findViewById(R.id.img);

        logoutButton = view.findViewById(R.id.logoutButton);
        applyAdminButton = view.findViewById(R.id.applyAdminButton);

        loadUserProfile();

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
                            String name = documentSnapshot.getString("FullName");
                            String category = documentSnapshot.getString("Category");

                            userName.setText(name);
                            userCategory.setText(category);
                            idNumInfo.setText(documentSnapshot.getString("idNumber"));

                            if ("Student".equalsIgnoreCase(category)) {
                                programInfo.setText(documentSnapshot.getString("Program"));
                                yearLevelInfo.setText(documentSnapshot.getString("yearLevel"));

                                programInfo.setVisibility(View.VISIBLE);
                                yearLevelInfo.setVisibility(View.VISIBLE);
                                departmentInfo.setVisibility(View.GONE);

                            } else if ("Faculty".equalsIgnoreCase(category)) {
                                departmentInfo.setText(documentSnapshot.getString("Department"));

                                programInfo.setVisibility(View.GONE);
                                yearLevelInfo.setVisibility(View.GONE);
                                departmentInfo.setVisibility(View.VISIBLE);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                    });
        }
    }
}
