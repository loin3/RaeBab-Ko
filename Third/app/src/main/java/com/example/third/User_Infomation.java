package com.example.third;

import android.net.Uri;

public class User_Infomation {
    public String token;
    public String outfit;

    public User_Infomation() {
        // Default Constructor
    }

    public User_Infomation(String token, String outfit){
        this.token = token;
        this.outfit = outfit;
    }
}
