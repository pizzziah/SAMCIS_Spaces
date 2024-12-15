package com.example.myapplication.adminFx;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;
    private Context context;
    private FirebaseFirestore db;

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        // Set user details on the UI
        holder.textViewName.setText(user.getFullName());
        holder.textViewEmail.setText(user.getUserEmail());

        // View Details Button
        holder.buttonViewDetails.setOnClickListener(v -> showUserDetailsDialog(user));

        // Delete User Button
        holder.buttonDelete.setOnClickListener(v -> deleteUser(user.getUserId(), position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    // Show User Details in Dialog
    private void showUserDetailsDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("User Details")
                .setMessage("Category: " + user.getCategory() + "\n" +
                        "FullName: " + user.getFullName() + "\n" +
                        "ID Number: " + user.getIdNumber() + "\n" +
                        "Program: " + user.getProgram() + "\n" +
                        "Year Level: " + user.getYearLevel() + "\n" +
                        "Profile Complete: " + user.isProfileComplete() + "\n" +
                        "UserEmail: " + user.getUserEmail() + "\n" +
                        "UserRole: " + user.getUserRole())
                .setPositiveButton("OK", null)
                .show();
    }

    // Delete User from Firestore and update RecyclerView
    private void deleteUser(String userId, int position) {
        db.collection("Users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "User deleted successfully!", Toast.LENGTH_SHORT).show();
                    userList.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error deleting user", Toast.LENGTH_SHORT).show();
                });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewEmail;
        Button buttonViewDetails, buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
