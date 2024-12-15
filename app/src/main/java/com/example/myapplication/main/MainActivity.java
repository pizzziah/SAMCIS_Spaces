package com.example.myapplication.main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.adminFx.AdminBookingFragment;
import com.example.myapplication.adminFx.AdminHomeFragment;
import com.example.myapplication.adminFx.AdminProfileFragment;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.userFx.UserBookingFragment;
import com.example.myapplication.userFx.UserHomeFragment;
import com.example.myapplication.userFx.UserProfileFragment;
import com.example.myapplication.R;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRole = getIntent().getStringExtra("UserRole");

        if ("Admin".equals(userRole)) {
            replaceFragment(new AdminHomeFragment());
        }

        if ("User".equals(userRole)) {
            replaceFragment(new UserHomeFragment());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int menuItemId = item.getItemId();

                if ("Admin".equals(userRole)) {
                    if (menuItemId == R.id.home) {
                        replaceFragment(new AdminHomeFragment());
                    } else if (menuItemId == R.id.booking) {
                        replaceFragment(new AdminBookingFragment());
                    } else if (menuItemId == R.id.profile) {
                        replaceFragment(new AdminProfileFragment());
                    }
                    return true;
                }

                if ("Admin".equals(userRole)) {
                    if (menuItemId == R.id.home) {
                        replaceFragment(new UserHomeFragment());
                    } else if (menuItemId == R.id.booking) {
                        replaceFragment(new UserBookingFragment());
                    } else if (menuItemId == R.id.profile) {
                        replaceFragment(new UserProfileFragment());
                    }
                    return true;
                }
                return false;
            }
        });
    }

//        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                int menuItemId = item.getItemId();
//
//                if (menuItemId == R.id.home) {
//                    if ("Admin".equals(userRole)) {
//                        replaceFragment(new AdminHomeFragment());
//                    } else {
//                        replaceFragment(new UserHomeFragment());
//                    }
//                } else if (menuItemId == R.id.booking) {
//                    replaceFragment(new UserBookingFragment());
//                } else if (menuItemId == R.id.profile) {
//                    replaceFragment(new UserProfileFragment());
//                }
//                return true;
//            }
//        });
//    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}

