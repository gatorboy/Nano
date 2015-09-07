package com.smenedi.nano;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements OnClickListener {


    //All main app launcher buttons
    private Button mSpotifyStreamer;
    private Button mScoresApp;
    private Button mLibraryApp;
    private Button mBuildItBigger;
    private Button mXyzReader;
    private Button mCapstone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSpotifyStreamer = (Button) findViewById(R.id.spotify_streamer);
        mScoresApp = (Button) findViewById(R.id.scores_app);
        mLibraryApp = (Button) findViewById(R.id.library_app);
        mBuildItBigger = (Button) findViewById(R.id.build_it_bigger);
        mXyzReader = (Button) findViewById(R.id.xyz_reader);
        mCapstone = (Button) findViewById(R.id.capstone);

        mSpotifyStreamer.setOnClickListener(this);
        mScoresApp.setOnClickListener(this);
        mLibraryApp.setOnClickListener(this);
        mBuildItBigger.setOnClickListener(this);
        mXyzReader.setOnClickListener(this);
        mCapstone.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        showToast(v.getId());
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
