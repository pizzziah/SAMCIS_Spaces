package com.example.myapplication.userFx;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
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

        holder.venueName.setText(booking.getVenueName());
        holder.bookingDate.setText(booking.getDate());

        holder.btnCancel.setOnClickListener(v -> {
            db.collection("bookings").document(booking.getId()).delete();
            bookingList.remove(position);
            notifyDataSetChanged();
        });

        holder.btnUpdate.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), UpdateBookingActivity.class);
            intent.putExtra("bookingId", booking.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView venueName, bookingDate;
        Button btnCancel, btnUpdate;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            venueName = itemView.findViewById(R.id.venueName);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}