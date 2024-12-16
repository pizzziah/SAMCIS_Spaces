package com.example.myapplication.adminFx;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    private List<AdminBooking> bookingList;
    private Context context;
    private FirebaseFirestore db;

    public AdminBookingAdapter(List<AdminBooking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for individual booking items
        View view = LayoutInflater.from(context).inflate(R.layout.booking_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the current booking item
        AdminBooking booking = bookingList.get(position);

        // Bind the booking details to the UI
        holder.textViewBookingId.setText("Booking ID: " + booking.getUser());
        holder.textViewBookingDetails.setText("Details: " + booking.getBookingDetails());
        holder.textViewBookingDate.setText("Booking Date: "  + booking.getDate());
        holder.textViewStatus.setText("Status: "  + booking.getBookingStatus());

        // View Details Button
        holder.buttonViewDetails.setOnClickListener(v -> showBookingDetailsDialog(booking));

        // Approve Booking Button
        holder.buttonApprove.setOnClickListener(v -> approveBooking(booking.getBookingId(), position));

        // Deny Booking Button
        holder.buttonDeny.setOnClickListener(v -> denyBooking(booking.getBookingId(), position));
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    // Show detailed booking info in a dialog
    private void showBookingDetailsDialog(AdminBooking booking) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Booking Details")
                .setMessage("Booking ID: " + booking.getBookingId() + "\n" +
                        "Details: " + booking.getBookingDetails() + "\n" +
                        "Booking Date: " + booking.getDate() + "\n" +
                        "Status: " + booking.getBookingStatus())
                .setPositiveButton("OK", null)
                .show();
    }

    // Approve Booking by updating its status in Firestore
    private void approveBooking(String bookingId, int position) {
        // Reference to the Users collection
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            // Reference the bookings subcollection for each user
                            userDoc.getReference()
                                    .collection("bookings")
                                    .document(bookingId) // Use the specific booking ID you want to update
                                    .update("status", "true")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Booking approved successfully!", Toast.LENGTH_SHORT).show();
                                        Log.d("UpdateBooking", "Updated booking ID: " + bookingId + " for user: " + userDoc.getId());
                                        notifyItemChanged(position); // Refresh the item in the list
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("UpdateBooking", "Error updating booking ID: " + bookingId + " for user: " + userDoc.getId(), e);
                                        Toast.makeText(context, "Error approving booking", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching users", task.getException());
                        Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    // Deny Booking by updating its status in Firestore
    private void denyBooking(String bookingId, int position) {
        // Fetch all users from the "Users" collection
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            // Access the "bookings" subcollection for each user
                            userDoc.getReference()
                                    .collection("bookings")
                                    .document(bookingId) // Specific booking ID to deny
                                    .update("status", "false")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Booking denied successfully!", Toast.LENGTH_SHORT).show();
                                        Log.d("DenyBooking", "Booking denied: " + bookingId + " for user: " + userDoc.getId());
                                        notifyItemChanged(position); // Refresh the list item
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("DenyBooking", "Error denying booking for user: " + userDoc.getId(), e);
                                    });
                        }
                    } else {
                        Log.e("FirestoreError", "Error fetching users", task.getException());
                        Toast.makeText(context, "Error fetching users", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewBookingId, textViewBookingDetails, textViewBookingDate, textViewStatus;
        Button buttonViewDetails, buttonApprove, buttonDeny;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize UI components
            textViewBookingId = itemView.findViewById(R.id.textViewName);
            textViewBookingDetails = itemView.findViewById(R.id.textViewBookingDetails);
            textViewBookingDate = itemView.findViewById(R.id.textViewBookingDate);
            buttonViewDetails = itemView.findViewById(R.id.buttonViewDetails);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonDeny = itemView.findViewById(R.id.buttonDeny);
        }
    }
}
