package com.smenedi.nano;

import org.json.JSONObject;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.Bind;
import butterknife.ButterKnife;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by smenedi on 9/12/15.
 */
public class Trailer {
    private static final String LOG_TAG = Trailer.class.getSimpleName();

    private static final String THUMBNAIL = "http://img.youtube.com/vi/%s/hqdefault.jpg";
    private static final String VIDEO_URL = "http://www.youtube.com/watch?v=%s";
    private static final String NAME = "name";
    private static final String ID = "source";
    private static final String SIZE = "size";
    private static final String TYPE = "type";

    //Movie fields
    private final String mName;
    private final String mSize;
    private final String mId;
    private final String mType;


    @Bind(R.id.trailer_thumbnail)
    SimpleDraweeView mThumbnail;
    @Bind(R.id.play)
    ImageView mPlay;

    Activity mParentActivity;

    public Trailer(Activity activity, JSONObject jsonObject) {
        mParentActivity = activity;
        mName = jsonObject.optString(NAME);
        mSize = jsonObject.optString(SIZE);
        mId = jsonObject.optString(ID);
        mType = jsonObject.optString(TYPE);
    }

    public String getName() {
        return mName;
    }

    public String getSize() {
        return mSize;
    }

    public String getId() {
        return mId;
    }

    public String getType() {
        return mType;
    }

    public Uri getThumbnailUrl() {
        return Uri.parse(String.format(THUMBNAIL, getId()));
    }

    public Uri getVideoUri() {
        return Uri.parse(String.format(VIDEO_URL, getId()));
    }

    public View getView(final LinearLayout parentView) {
        if(getThumbnailUrl() != null) {
            final View trailer = mParentActivity.getLayoutInflater().inflate(R.layout.item_trailer, parentView, false);
            ButterKnife.bind(this, trailer);
            Log.d(LOG_TAG, "Thumbnail:" + getThumbnailUrl().toString());
            mThumbnail.setImageURI(getThumbnailUrl());
            mPlay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent youTubeIntent = new Intent(Intent.ACTION_VIEW, getVideoUri());
                    youTubeIntent.putExtra("force_fullscreen", true);
                    if(youTubeIntent.resolveActivity(MoviesApplication.getInstance().getPackageManager()) != null) {
                        mParentActivity.startActivity(youTubeIntent);
                    } else {
                        Utility.showSnackbar(mParentActivity, mParentActivity.getString(R.string.no_youtube));
                    }
                }
            });
            return trailer;
        }
        // Return null if we have no data to display
        return null;
    }
}
