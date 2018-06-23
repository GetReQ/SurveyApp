package com.example.android.surveyapp.network;

import java.util.ArrayList;
import java.util.List;

public class Geometry {
    public String type;
    public List<GeoPoint> Coordinates;

    public Geometry(String newType) {
        type = newType;
        Coordinates = new ArrayList<>();
    }

    public void AddGeometry(GeoPoint newPoint) {
        Coordinates.add(newPoint);
    }

    public GeoPoint Start() {
        if (Coordinates.size() > 0) {
            return Coordinates.get(0);
        }
        return null;
    }

    public GeoPoint End() {
        int size = Coordinates.size();
        if (size > 0) {
            return Coordinates.get(size - 1);
        }
        return null;
    }
}




