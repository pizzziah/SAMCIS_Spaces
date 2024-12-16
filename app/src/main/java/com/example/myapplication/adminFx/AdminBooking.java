package com.example.myapplication.adminFx;

public class AdminBooking {

    private String bookingId;
    private String bookingDetails;
    private String name;
    private String User;
    private String date;
    private boolean
            bookingStatus; // E.g., "Pending", "Approved", "Denied"

    // Default constructor required for Firebase Firestore
    public AdminBooking() {
    }

    // Constructor to initialize booking details
    public AdminBooking(String bookingId, String bookingDetails, String User, String bookingDate, boolean bookingStatus, String name) {
        this.bookingId = bookingId;
        this.bookingDetails = bookingDetails;
        this.User = User;
        this.date = date;
        this.bookingStatus = bookingStatus;
        this.name = name;

    }

    // Getter and setter methods for each field
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(String bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public String getUser() {
        return User;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setUser(String User) {
        this.User = User;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getBookingStatus() {
        return bookingStatus;
    }


    public void setBookingStatus(boolean bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    // Optional: Override toString() to easily view the object data
    @Override
    public String toString() {
        return "Booking ID: " + bookingId + ", Details: " + bookingDetails + ", User: " + User + ", Date: " + date + ", Status: " + bookingStatus;
    }
}
