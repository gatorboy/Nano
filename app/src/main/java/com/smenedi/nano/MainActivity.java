package com.smenedi.nano;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final int UNKNOWN = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Performs click action on the button clicked
     *
     * @param v
     *         View object of the button clicked
     */
    @SuppressWarnings("unused") // This is actually used via button click
    public void appOpen(View v) {
        final int stringId;
        switch (v.getId()) {
        case R.id.spotify_streamer:
            stringId = R.string.spotify_streamer;
            break;
        case R.id.scores_app:
            stringId = R.string.scores_app;
            break;
        case R.id.library_app:
            stringId = R.string.library_app;
            break;
        case R.id.build_it_bigger:
            stringId = R.string.build_it_bigger;
            break;
        case R.id.xyz_reader:
            stringId = R.string.xyz_reader;
            break;
        case R.id.capstone:
            stringId = R.string.capstone;
            break;
        default:
            stringId = UNKNOWN;
            break;
        }
        showToast(stringId);
    }

    /**
     * Shows a Toast message with the name of the app
     *
     * @param appId
     *         String id of the app name
     */
    private void showToast(int appId) {
        final String appName = getString(appId);
        Toast.makeText(this, "This button will launch " + appName, Toast.LENGTH_SHORT).show();
    }
}
