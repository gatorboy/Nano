package com.smenedi.nano;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                                .add(android.R.id.content, new SettingsFragment())
                                .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            bindPreference(findPreference(getString(R.string.pref_sort_order)));
        }

        private void bindPreference(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            final String value = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                final int index = listPreference.findIndexOfValue(value);
                if (index >= 0) {
                    preference.setSummary(listPreference.getEntries()[index]);
                }
            } else {
                preference.setSummary(value);
            }
            return true;
        }
    }


}
