package com.example.android.surveyapp.network;

public class Network {
    public Iterable<Section> Sections;
    public Iterable<Asset> Assets;

    public Network(Iterable<Section> sections, Iterable<Asset> assets) {
        Sections = sections;
        Assets = assets;
    }
}
