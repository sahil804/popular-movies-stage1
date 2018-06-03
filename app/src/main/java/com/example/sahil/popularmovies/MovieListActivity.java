package com.example.sahil.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.sahil.popularmovies.models.MovieItem;
import com.example.sahil.popularmovies.utilities.Constants;
import com.example.sahil.popularmovies.utilities.MovieJsonUtils;
import com.example.sahil.popularmovies.utilities.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<MovieItem>>, MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MovieListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private RecyclerView mRecyclerView;
    private MovieAdapter movieAdapter;
    private boolean mTwoPane;
    private static final int MOVIE_DATA_LOADER_ID = 0;
    public String currentSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.sahil.popularmovies.R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(com.example.sahil.popularmovies.R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        if (findViewById(com.example.sahil.popularmovies.R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        mRecyclerView = findViewById(com.example.sahil.popularmovies.R.id.movie_list);
        assert mRecyclerView != null;
        setupRecyclerView(mRecyclerView);
        currentSortOrder = PreferenceManager.getDefaultSharedPreferences(this).
                getString(getResources().getString(R.string.sortorder), getResources().getString(R.string.popular));
        if(isOnline()) getSupportLoaderManager().initLoader(MOVIE_DATA_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String persistedSortOrder = sp.getString(getResources().getString(R.string.sortorder), getResources().getString(R.string.popular));
        if(!isOnline()) {
            Toast.makeText(this,getResources().getString(R.string.no_nw_available),Toast.LENGTH_SHORT).show();
        }
        if(isOnline() && !currentSortOrder.equalsIgnoreCase(persistedSortOrder)){
            currentSortOrder = persistedSortOrder;
            getSupportLoaderManager().restartLoader(MOVIE_DATA_LOADER_ID, null, this);
        }

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        mRecyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(this, this);
        recyclerView.setAdapter(movieAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.example.sahil.popularmovies.R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == com.example.sahil.popularmovies.R.id.action_settings) {
            // start Preference Activity
            startActivity(new Intent(this, MovieListPreferenceActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<List<MovieItem>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<MovieItem>>(this) {
            List<MovieItem> mMovieItem;

            @Override
            protected void onStartLoading() {
                if (mMovieItem != null) {
                    deliverResult(mMovieItem);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<MovieItem> loadInBackground() {
                URL movieDataUrl = NetworkUtils.buildMovieDataUrl(currentSortOrder);
                try {
                    String jsonMovieData = NetworkUtils.getResponseFromHttpUrl(movieDataUrl);
                    mMovieItem = MovieJsonUtils.getMovieItemsFromJson(MovieListActivity.this, jsonMovieData);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return mMovieItem;
            }

            @Override
            public void deliverResult(@Nullable List<MovieItem> data) {
                mMovieItem = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieItem>> loader, List<MovieItem> data) {
        movieAdapter.setMovieData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieItem>> loader) {

    }

    @Override
    public void onClick(MovieItem movieItem) {
        if (mTwoPane) {
            MovieDetailFragment movieDetailFragment = MovieDetailFragment.newInstance(movieItem);
            getSupportFragmentManager().beginTransaction()
                    .add(com.example.sahil.popularmovies.R.id.movie_detail_container, movieDetailFragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(Constants.MOVIE_ITEM_KEY, movieItem);
            startActivity(intent);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
