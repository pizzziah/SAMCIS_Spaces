package com.example.myapplication.userFx;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private FirebaseFirestore db;

    public BookingAdapter(List<Booking> bookingList, FirebaseFirestore db) {
        this.bookingList = bookingList;
        this.db = db;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Set basic booking details
        holder.venueName.setText(booking.getVenueName() != null ? booking.getVenueName() : "Unknown Venue");
        holder.bookingDate.setText(booking.getDate() != null ? "Date: " + booking.getDate() : "No Date");

        // Fetch status field from Firestore
        db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid()) // Access user's document
                .collection("bookings")
                .document(booking.getId()) // Access specific booking document
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean status = documentSnapshot.getBoolean("status");
                        if (status == null) {
                            holder.bookingStatus.setText("Pending..");
                            // Keep buttons visible if status is null
                            holder.btnUpdate.setVisibility(View.VISIBLE);
                            holder.btnCancel.setVisibility(View.VISIBLE);
                        } else if (status) {
                            holder.bookingStatus.setText("Booking accepted!");
                            // Hide buttons if status is true
                            holder.btnUpdate.setVisibility(View.GONE);
                            holder.btnCancel.setVisibility(View.GONE);
                        } else {
                            holder.bookingStatus.setText("Booking declined");
                            // Hide buttons if status is false
                            holder.btnUpdate.setVisibility(View.GONE);
                            holder.btnCancel.setVisibility(View.GONE);
                        }
                    } else {
                        holder.bookingStatus.setText("Pending..");
                        // Keep buttons visible if document doesn't exist
                        holder.btnUpdate.setVisibility(View.VISIBLE);
                        holder.btnCancel.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    holder.bookingStatus.setText("Pending..");
                    // Keep buttons visible if there's an error
                    holder.btnUpdate.setVisibility(View.VISIBLE);
                    holder.btnCancel.setVisibility(View.VISIBLE);
                });

        holder.btnCancel.setOnClickListener(v -> {
            db.collection("Users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .collection("bookings")
                    .document(booking.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Remove the item from the list
                        bookingList.remove(position);
                        // Notify that the item has been removed
                        notifyItemRemoved(position);
                        // Notify that the range has been removed, if necessary
                        notifyItemRangeChanged(position, bookingList.size());  // This ensures the RecyclerView is updated
                        Toast.makeText(v.getContext(), "Booking canceled successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(v.getContext(), "Failed to cancel booking", Toast.LENGTH_SHORT).show();
                    });
        });



        // Update button logic
        holder.btnUpdate.setOnClickListener(v -> {
            // Pass all necessary data to the UpdateBookingActivity
            Intent intent = new Intent(v.getContext(), UpdateBookingActivity.class);
            intent.putExtra("bookingId", booking.getId());
            intent.putExtra("venueName", booking.getVenueName());  // Pass venueName
            intent.putExtra("date", booking.getDate());            // Pass date
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView venueName, bookingDate, bookingStatus;
        ImageView venueImage;
        Button btnCancel, btnUpdate;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            venueName = itemView.findViewById(R.id.venueName);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);
            venueImage = itemView.findViewById(R.id.venueImage);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}
