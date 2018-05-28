package com.example.android.surveyapp.network;

public class Asset {
    public int Id;
    public String Code;
    public String Description;

    public Asset(int id, String code, String description) {
        Id = id;
        Code = code;
        Description = description;
    }

    @Override
    public String toString() {
        return Description.toString();
    }
}
