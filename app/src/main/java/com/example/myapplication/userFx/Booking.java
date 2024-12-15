package com.example.myapplication.userFx;

public class Booking {
    private String id;
    private String venueName;
    private String date;
    private Boolean status;

    // Constructor with status
    public Booking(String id, String venueName, String date, Boolean status) {
        this.id = id;
        this.venueName = venueName;
        this.date = date;
        this.status = status;
    }

    // Constructor without status
    public Booking(String id, String venueName, String date) {
        this.id = id;
        this.venueName = venueName;
        this.date = date;
        this.status = null; // Default value for status
    }

    // Getters and setters (unchanged)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
