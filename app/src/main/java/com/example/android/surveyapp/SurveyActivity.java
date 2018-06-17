package com.example.android.surveyapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.android.surveyapp.network.Section;

public class SurveyActivity extends AppCompatActivity {

    private TextView mSectionLabel;
    private Section mSelectedSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        mSectionLabel = (TextView) findViewById(R.id.tv_survey_section_label);

        Intent startedSurveyIntent = getIntent();
        if (startedSurveyIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String sectionLabel = startedSurveyIntent.getStringExtra(Intent.EXTRA_TEXT);
            mSectionLabel.setText(sectionLabel);
        }
    }
}
