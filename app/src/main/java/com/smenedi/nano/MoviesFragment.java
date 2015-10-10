package com.smenedi.nano;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    private static final String SORT_ORDER_FORMAT = "%s.desc";
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private List<Movie> mMovieList;

    public MoviesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviesfragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity(), mMovieList, mRecyclerView);
        fetchMoviesTask.execute(String.format(SORT_ORDER_FORMAT, PreferenceManager.getDefaultSharedPreferences(getContext())
                                                                                  .getString(getString(R.string.key_pref_sort_order), getString(R.string.value_pref_sort_order_default))));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this, layout);

        setUpRecylerView();

        return layout;
    }

    private void setUpRecylerView() {
        mMovieList = new ArrayList<>();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerView.setAdapter(new MoviesAdapter(getContext(), mMovieList));
    }

/*
    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                URL url = ApiRequests.getMoviesUrl(params[0]);
                Log.d(LOG_TAG, "Built URI: " + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("FetchMoviesTask", "Error " + e.getMessage());
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("FetchMoviesTask", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Movie> results) {
            mMovieList.clear();
            if (results != null && results.size() != 0) {
                mMovieList.addAll(results);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }

        private List<Movie> getMovieDataFromJson(String movieJsonStr) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String RESULTS = "results";
            JSONObject moviesJSON = new JSONObject(movieJsonStr);
            JSONArray moviesArray = moviesJSON.getJSONArray(RESULTS);
            final int len = moviesArray.length();
            final List<Movie> movies = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                movies.add(new Movie(moviesArray.getJSONObject(i)));
            }
            return movies;
        }
    }
*/
}
