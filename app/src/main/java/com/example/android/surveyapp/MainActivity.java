package com.example.android.surveyapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.surveyapp.network.Network;
import com.example.android.surveyapp.network.Section;
import com.example.android.surveyapp.network.Asset;
import com.example.android.surveyapp.utilities.NetworkUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int NUM_SECTION_ITEMS = 100;

    private Network mNetwork;

    private SectionAdapter mSectionAdapter;

    private RecyclerView mSectionList;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionList = (RecyclerView) findViewById(R.id.rv_network);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mSectionList.setLayoutManager(layoutManager);

        mSectionList.setHasFixedSize(true);

        mSectionAdapter = new SectionAdapter();

        mSectionList.setAdapter(mSectionAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        getNetworkData();
    }

    private void getNetworkData(){
        showSectionDataView();

        new SampleNetworkQueryTask().execute();
    }

    private void showSectionDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mSectionList.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mSectionList.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class SampleNetworkQueryTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
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
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (networkResults != null && !networkResults.equals("")) {
                mNetwork = DeserialiseNetwork(networkResults);
                showSectionDataView();
                mSectionAdapter.setSectionData(mNetwork.Sections);
            } else {
                showErrorMessage();
            }
        }

        private Network DeserialiseNetwork(String networkJson) {
            Network network = null;
            try {
                JSONObject JsonNetwork = new JSONObject(networkJson);
                JSONArray sections = JsonNetwork.getJSONArray("Sections");
                Iterable<Section> networkSections = getSectionsFromJsonArray(sections);
                JSONArray assets = JsonNetwork.getJSONArray("Assets");
                Iterable<Asset> networkAssets = getAssetsFromJsonArray(assets);
                network = new Network(networkSections, networkAssets);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return network;
        }

        private Iterable<Section> getSectionsFromJsonArray(JSONArray sections) {
            List<Section> networkSections = new ArrayList<>();
            for (int i=0; i<sections.length(); i++) {
                try {
                    JSONObject sectionRow = sections.getJSONObject(i);
                    int section_id = sectionRow.getInt("Id");
                    String section_label = sectionRow.getString("Label");
                    int section_length = sectionRow.getInt("Length");
                    networkSections.add(
                            new Section(section_id, section_label, section_length)
                    );
                 } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return networkSections;
        }

        private Iterable<Asset> getAssetsFromJsonArray(JSONArray assets) {
            List<Asset> networkAssets = new ArrayList<>();
            for (int i=0; i<assets.length(); i++) {
                try {
                    JSONObject assetRow = assets.getJSONObject(i);
                    int asset_id = assetRow.getInt("Id");
                    String asset_code = assetRow.getString("Code");
                    String asset_name = assetRow.getString("Name");
                    networkAssets.add(
                            new Asset(asset_id, asset_code, asset_name)
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return networkAssets;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh_network) {
            mSectionAdapter.setSectionData(null);
            getNetworkData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
