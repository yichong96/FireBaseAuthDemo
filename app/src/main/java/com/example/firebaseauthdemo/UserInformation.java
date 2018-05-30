package com.example.firebaseauthdemo;

public class UserInformation {

    public String name;
    public String address;
    public String displayName;
    public String uri;

    public UserInformation() {
    }


    public UserInformation(String name, String address,String displayName,String uri) {
        this.name = name;
        this.address = address;
        this.displayName = displayName;
        this.uri = uri;
    }
}
