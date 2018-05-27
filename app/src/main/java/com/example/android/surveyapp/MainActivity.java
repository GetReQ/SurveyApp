package com.example.android.surveyapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.surveyapp.utilities.NetworkUtility;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mDataSources;

    private TextView mNetworkResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataSources = findViewById(R.id.tv_data_sources);
        mNetworkResults = findViewById(R.id.tv_network_results_json);
        new SampleNetworkQueryTask().execute();
    }


    public class SampleNetworkQueryTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String networkResults = null;
            try {
                networkResults = NetworkUtility.getResponseFromHttpUrl();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return networkResults;
        }

        @Override
        protected void onPostExecute(String networkResults) {
            if (networkResults != null && !networkResults.equals("")) {
                mNetworkResults.setText(networkResults);
            }
        }
    }
}
