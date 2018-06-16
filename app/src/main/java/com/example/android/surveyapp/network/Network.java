package com.example.android.surveyapp.network;

import java.util.List;

public class Network {
    public List<Section> Sections;
    public List<Asset> Assets;

    public Network(List<Section> sections, List<Asset> assets) {
        Sections = sections;
        Assets = assets;
    }
}
