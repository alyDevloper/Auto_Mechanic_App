package com.example.cab;

public class Mechanic {
    private String mechanicId;
    private String email;
    private String username;
    private String password;
    private String phone;
    private String CNIC;
    private String address;

    public Mechanic() {
        // Default constructor required for Firebase
    }

    public Mechanic(String mechanicId, String email, String username, String password, String phone, String CNIC, String address) {
        this.mechanicId = mechanicId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.CNIC = CNIC;
        this.address = address;
    }

    public String getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(String mechanicId) {
        this.mechanicId = mechanicId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCNIC() {
        return CNIC;
    }

    public void setCNIC(String CNIC) {
        this.CNIC = CNIC;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
