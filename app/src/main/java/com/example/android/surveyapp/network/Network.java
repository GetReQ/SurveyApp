package com.example.android.surveyapp.network;

import java.util.List;

public class Network {
    public List<Section> Sections;
    public List<Asset> Assets;

    public Network(List<Section> sections, List<Asset> assets) {
        Sections = sections;
        Assets = assets;
    }

    ///Search for a section in this network based on it's section label.
    public Section getSection(String label){
        int numOfSections = Sections.size();
        if (Sections != null && numOfSections > 0) {
            for (int i = 0; i < numOfSections - 1; i++) {
                if (Sections.get(i).Label == label) {
                    return Sections.get(i);
                }
            }
        }
        return null;
    }
}
