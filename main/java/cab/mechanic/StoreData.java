package com.example.cab;

public class StoreData {
    private String name;
    private String phone;
    private String description;
    private String address;
    private String location;
    private String moduleName;

    // Default constructor required for calls to DataSnapshot.getValue(StoreData.class)
    public StoreData() {
    }

    public StoreData(String name, String phone, String description, String address, String location, String moduleName) {
        this.name = name;
        this.phone = phone;
        this.description = description;
        this.address = address;
        this.location= location;
        this.moduleName = moduleName;
    }

    // Getter and setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter and setter for 'phone'
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter and setter for 'description'
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Getter and setter for 'address'
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Getter and setter for 'location'
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    // Getter and setter for 'moduleName'
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
