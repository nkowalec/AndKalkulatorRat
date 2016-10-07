package com.nicotec.kr.kalkulatorrat;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Nikodem on 07.10.2016.
 */

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
