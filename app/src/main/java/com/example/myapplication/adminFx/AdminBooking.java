package com.example.myapplication.adminFx;

public class AdminBooking {
    private String bookingDate;
    private String bookingTime;
    private String receivedDate;
    private String BookingId;
    private String Status;
    private String BookingDetails;
    private int imageResource;
    private String BookingID;

    public AdminBooking(String bookingDate, String bookingTime, String receivedDate, int imageResource) {
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.receivedDate = receivedDate;
        this.imageResource = imageResource;

    }

    public String getBookingDate() { return bookingDate; }
    public String getBookingTime() { return bookingTime; }

    public String getReceivedDate() { return receivedDate; }
    public int getImageResource() { return imageResource; }

    public String getBookingDetails() {
        return BookingDetails;
    }

    public void setBookingId(String bookingId) {
        BookingId = bookingId;
    }

    public void setBookingDetails(String bookingDetails) {
        BookingDetails = bookingDetails;
    }

    public String getBookingId() {
        return BookingID;
    }

    public String getStatus() {
        return Status;
    }
}
