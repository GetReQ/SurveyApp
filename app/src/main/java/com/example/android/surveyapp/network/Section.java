package com.example.android.surveyapp.network;

public class Section {
    public int Id;
    public String Label;
    public int Length;
    public Geometry Geometry;

    public Section(int id, String label, int length, Geometry geometry) {
        Id = id;
        Label = label;
        Length = length;
        Geometry = geometry;
    }

    @Override
    public String toString() {
        return Label.toString();
    }
}
