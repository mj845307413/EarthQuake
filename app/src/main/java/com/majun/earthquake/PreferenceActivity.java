package com.majun.earthquake;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

public class PreferenceActivity extends Activity {

    private CheckBox autoUpdate;
    private Spinner updateFreqSpinner;
    private Spinner magnitudeSpinner;
    public static final String USER_PREFERENCE = "USER_PREFERENCE";
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
    public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";
    public static final String PREF_UPDATE_FREQ_INDEX = "PREF_UPDATE_FREQ_INDEX";
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        autoUpdate= (CheckBox) findViewById(R.id.checkbox_auto_update);
        updateFreqSpinner= (Spinner) findViewById(R.id.spinner_update_freq);
        magnitudeSpinner= (Spinner) findViewById(R.id.spinner_quake_mag);
        populationSpinners();
        sharedPreferences=getSharedPreferences("MyPreference",MODE_PRIVATE);
        UpdateUIFromPerefence();
        Button okButton= (Button) findViewById(R.id.okButton);
        Button cancelButton= (Button) findViewById(R.id.cancelButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreference();
                PreferenceActivity.this.setResult(RESULT_OK);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void savePreference() {
        int updateIndex = updateFreqSpinner.getSelectedItemPosition();
        int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
        boolean autoUpdateChecked = autoUpdate.isChecked();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_AUTO_UPDATE, autoUpdateChecked);
        editor.putInt(PREF_UPDATE_FREQ_INDEX, updateIndex);
        editor.putInt(PREF_MIN_MAG_INDEX, minMagIndex);
        editor.commit();
    }

    private void UpdateUIFromPerefence() {
        boolean autoUpChecked = sharedPreferences.getBoolean(PREF_AUTO_UPDATE, false);
        int updateFreqIndex = sharedPreferences.getInt(PREF_UPDATE_FREQ_INDEX, 2);
        int minMagIndex = sharedPreferences.getInt(PREF_MIN_MAG_INDEX, 0);

        updateFreqSpinner.setSelection(updateFreqIndex);
        magnitudeSpinner.setSelection(minMagIndex);
        autoUpdate.setChecked(autoUpChecked);
    }

    private void populationSpinners() {
        ArrayAdapter fAdapter = ArrayAdapter.createFromResource(this,R.array.update_freq_options,android.R.layout.simple_spinner_item);
        fAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateFreqSpinner.setAdapter(fAdapter);
        ArrayAdapter mAdapter=ArrayAdapter.createFromResource(this,R.array.magnitude_options,android.R.layout.simple_spinner_item);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        magnitudeSpinner.setAdapter(mAdapter);

    }
}
