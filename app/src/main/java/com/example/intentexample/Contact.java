package com.example.intentexample;

public class Contact {
    private String name;
    private String phone;
    private String school;
    private String mail;

    // Constructor
    public Contact(String name, String phone, String school, String mail) {
        this.name = name;
        this.phone = phone;
        this.school = school;
        this.mail = mail;
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

    public String getMail() {
        return mail;
    }

    // Setter for school
    public void setMemo(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return name;
    }
}