package com.example.android.surveyapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.surveyapp.adapters.SectionAdapter;
import com.example.android.surveyapp.network.GeoPoint;
import com.example.android.surveyapp.network.Geometry;
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

public class MainActivity extends AppCompatActivity
    implements SectionAdapter.ListItemClickListener {

    private static final int NUM_SECTION_ITEMS = 100;

    private Network mNetwork;

    private SectionAdapter mSectionAdapter;
    private RecyclerView mSectionList;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSectionList = (RecyclerView) findViewById(R.id.rv_network);

        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mSectionList.setLayoutManager(layoutManager);

        mSectionList.setHasFixedSize(true);

        mSectionAdapter = new SectionAdapter(this);

        mSectionList.setAdapter(mSectionAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        //enable or disable map item depending on if a section is selected
        //MenuItem item = menu.findItem(R.id.action_load_map);
        /*if (mSelectedSection != null) {
            item.setEnabled(true);
            item.getIcon().setAlpha(255);
        } else {
            item.setEnabled(false);
            item.getIcon().setAlpha(130);
        }*/
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.network, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_refresh_network:
                getNetworkData();
                return true;
            case R.id.action_load_map:
                loadMap(mSelectedSection);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    ///Get the section selected based on the index clicked on the list
    private Section getSectionSelected(int clickedItemIndex) {
        if (mNetwork == null || mNetwork.Sections.size() < clickedItemIndex) return null;
        return mNetwork.Sections.get(clickedItemIndex);
    }

    private Section mSelectedSection;   //The current section selected by the user
    private int mPreviousItemClicked = -1;
    @Override
    public void onListItemClick(int clickedItemIndex) {
        mSelectedSection = getSectionSelected(clickedItemIndex);

        if (mToast != null) { //Cancel the previous toast
            mToast.cancel();
        }

        if (mPreviousItemClicked == clickedItemIndex){
            //double click - start survey
            NavigateToStartSurvey(mSelectedSection);
        } else {
            //display toast of selection
            String toastMessage = "Section: " + mSelectedSection.Label;
            mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
            mToast.show();
        }

        mPreviousItemClicked = clickedItemIndex;
    }

    private void NavigateToStartSurvey(Section selectedSection)
    {
        Context context = MainActivity.this;
        Class destinationActivity = SurveyActivity.class;
        Intent startSurveyIntent = new Intent(context, destinationActivity);
        startSurveyIntent.putExtra(Intent.EXTRA_TEXT, selectedSection.Label);
        startActivity(startSurveyIntent);
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

    private void loadMap(Section section) {
        //Check section has geometry and at least one geometry point
        if (section.Geometry != null && section.Geometry.Coordinates.size() > 0)
        {
            //Create intent for loading geopoint in map
            Uri locationPointUri = generateGeopointUri(section);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(locationPointUri);
            //Load map if intent can be resolved
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                //display toast of selection
                String toastMessage = "No map app found.";
                mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);
                mToast.show();
            }
        }
    }

    ///Create a URI for navigating to a geopoint
    private Uri generateGeopointUri(Section section) {
        if (section.Geometry.Coordinates.get(0) == null) return null;
        GeoPoint point = section.Geometry.Coordinates.get(0);
        String label = "Start: " + section.Label;
        String latitude = String.valueOf(point.Latitude);
        String longitude = String.valueOf(point.Longitude);
        String location = latitude + "," + longitude;
        String uriBegin = "geo:" + location;
        String query = location + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=19";
        Uri locationUri = Uri.parse(uriString);
        return locationUri;
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
                List<Section> networkSections = getSectionsFromJsonArray(sections);
                JSONArray assets = JsonNetwork.getJSONArray("Assets");
                List<Asset> networkAssets = getAssetsFromJsonArray(assets);
                network = new Network(networkSections, networkAssets);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return network;
        }

        private List<Section> getSectionsFromJsonArray(JSONArray sections) {
            List<Section> networkSections = new ArrayList<>();
            for (int i=0; i<sections.length(); i++) {
                try {
                    //Get section attributes
                    JSONObject sectionRow = sections.getJSONObject(i);
                    int section_id = sectionRow.getInt("Id");
                    String section_label = sectionRow.getString("Label");
                    int section_length = sectionRow.getInt("Length");
                    //Get geometry
                    JSONObject geometryRow = sectionRow.getJSONObject("Geometry");
                    Geometry geometry = getGeometryFromJson(geometryRow);
                    networkSections.add(
                            new Section(section_id, section_label, section_length, geometry)
                    );
                 } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return networkSections;
        }

        private Geometry getGeometryFromJson(JSONObject geometryRow) {
            Geometry geometry = null;
            try{
                String geometry_type = geometryRow.getString("Type");
                geometry = new Geometry(geometry_type);
                //Get coordinates
                JSONArray coordinates = geometryRow.getJSONArray("Coordinates");
                geometry.Coordinates = getGeometryPointsFromJsonArray(coordinates);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return geometry;
        }

        private List<GeoPoint> getGeometryPointsFromJsonArray(JSONArray coordinates) {
            List<GeoPoint> geoPoints = new ArrayList<>();
            for (int i=0; i<coordinates.length(); i++) {
                try {
                    JSONObject coordinatesRow = coordinates.getJSONObject(i);
                    double latitude = coordinatesRow.getDouble("Latitude");
                    double longitude = coordinatesRow.getDouble("Longitude");
                    GeoPoint point = new GeoPoint(latitude, longitude);
                    geoPoints.add(point);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return geoPoints;
        }

        private List<Asset> getAssetsFromJsonArray(JSONArray assets) {
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
}
