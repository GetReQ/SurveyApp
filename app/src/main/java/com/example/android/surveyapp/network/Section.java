package com.example.android.surveyapp.network;

public class Section {
    public int Id;
    public String Label;
    public int Length;

    public Section(int id, String label, int length) {
        Id = id;
        Label = label;
        Length = length;
    }

    @Override
    public String toString() {
        return Label.toString();
    }
}
