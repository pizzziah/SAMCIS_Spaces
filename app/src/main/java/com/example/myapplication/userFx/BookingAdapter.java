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

        // Ensure booking is not null
        if (booking == null) return;

        // Set basic booking details
        holder.venueName.setText(booking.getVenueName() != null ? booking.getVenueName() : "Unknown Venue");
        holder.bookingDate.setText(booking.getDate() != null ? "Date: " + booking.getDate() : "No Date");

        // Set booking status
        Boolean status = booking.getStatus(); // Assuming you added `status` to Booking
        if (status == null) {
            holder.bookingStatus.setText("Pending..");
            holder.btnUpdate.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else if (status) {
            holder.bookingStatus.setText("Booking accepted!");
            holder.btnUpdate.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        } else {
            holder.bookingStatus.setText("Booking declined");
            holder.btnUpdate.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }

        // Cancel button logic
        holder.btnCancel.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String bookingId = booking.getId();

            db.collection("Users")
                    .document(userId)
                    .collection("bookings")
                    .document(bookingId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Remove the item from the list and notify the adapter
                        bookingList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(v.getContext(), "Booking canceled successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Failed to cancel booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Update button logic
        holder.btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UpdateBookingActivity.class);
            intent.putExtra("bookingId", booking.getId());
            intent.putExtra("venueName", booking.getVenueName());
            intent.putExtra("date", booking.getDate());
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
