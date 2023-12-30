package com.example.intentexample;

public class Contact {
    private String name;
    private String phone;
    private String school;
    private String memo;

    // Constructor
    public Contact(String name, String phone, String school, String memo) {
        this.name = name;
        this.phone = phone;
        this.school = school;
        this.memo = memo;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for phone
    public String getPhone() {
        return phone;
    }

    // Setter for phone
    public void setPhone(String phone) {
        this.phone = phone;
    }

    // Getter for school
    public String getSchool() {
        return school;
    }

    // Setter for school
    public void setSchool(String school) {
        this.school = school;
    }

    public String getMemo() {
        return memo;
    }

    // Setter for school
    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return name;
    }
}