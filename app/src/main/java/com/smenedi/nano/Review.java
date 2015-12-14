package com.smenedi.nano;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by smenedi on 9/12/15.
 */
public class Review {
    private static final String LOG_TAG = Review.class.getSimpleName();

    private static final String ID = "id";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";

    //Movie fields
    private final String mId;
    private final String mAuthor;
    private final String mContent;


    @Bind(R.id.review_content)
    TextView mReview;

    Activity mParentActivity;

    public Review(Activity activity, JSONObject jsonObject) {
        mParentActivity = activity;
        mId = jsonObject.optString(ID);
        mAuthor = jsonObject.optString(AUTHOR);
        mContent = jsonObject.optString(CONTENT);
    }

    public String getContent() {
        return mContent;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getId() {
        return mId;
    }

    public View getView(final LinearLayout parentView) {
        final View review = mParentActivity.getLayoutInflater().inflate(R.layout.item_review, parentView, false);
        ButterKnife.bind(this, review);
        SpannableStringBuilder sb = new SpannableStringBuilder(getAuthor() + ": ");
        sb.setSpan(new StyleSpan(Typeface.BOLD), 0, sb.length(), SpannableStringBuilder.SPAN_INCLUSIVE_EXCLUSIVE);
        sb.append(getContent());
        mReview.setText(sb);
        return review;
    }
}
