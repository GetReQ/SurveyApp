package com.example.android.surveyapp.utilities;


import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtility {

    final static String SAMPLE_NETWORK_URL =
            "https://raw.githubusercontent.com/GetReQ/NetworkJsonGenerator/master/output-examples/network-pretty.json";

    public static String getResponseFromHttpUrl() throws IOException {
        //Create URL object from sample network url
        Uri builtUrl = Uri.parse(SAMPLE_NETWORK_URL).buildUpon().build();
        URL url = null;
        try {
            url = new URL(builtUrl.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) return null;
        //Establish connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }

    }

}
