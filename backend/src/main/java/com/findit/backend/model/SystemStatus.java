package com.findit.backend.model;

/**
 * This is a POJO (Plain Old Java Object).
 * It represents the data we want to send to the mobile app.
 */
public class SystemStatus {
    private String status;
    private String version;
    private String database;

    // Constructor: Used to create the object with data
    public SystemStatus(String status, String version, String database) {
        this.status = status;
        this.version = version;
        this.database = database;
    }

    // Getters: Spring Boot uses these to "read" the values and turn them into JSON
    public String getStatus() { return status; }
    public String getVersion() { return version; }
    public String getDatabase() { return database; }

    // Setters: Used if we want to change the values later
    public void setStatus(String status) { this.status = status; }
    public void setVersion(String version) { this.version = version; }
    public void setDatabase(String database) { this.database = database; }
}
