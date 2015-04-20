package com.example.kas.contacts;

public class Contact{

    private long id;
    private String name;
    private String phone;
    private boolean location;

    public Contact(int id, String name, String phone, boolean location) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.location = location;

    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

//    public void setPhone(String phone) {
//       this.phone = phone;
//   }

    public boolean isLocation() {
        return location;
    }

//    public void setLocation(boolean location) {
//        this.location = location;
//    }
} //end of class Contact
