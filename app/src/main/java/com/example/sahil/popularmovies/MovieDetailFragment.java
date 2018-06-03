package com.example.sahil.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sahil.popularmovies.models.MovieItem;
import com.example.sahil.popularmovies.utilities.Constants;
import com.squareup.picasso.Picasso;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String MOVIE_ITEM_ID = "movie_item_id";

    public static final String TAG = MovieDetailFragment.class.getSimpleName();

    /**
     * The dummy content this fragment is presenting.
     */
    private MovieItem movieItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    public static MovieDetailFragment newInstance(MovieItem movieItem) {

        Bundle args = new Bundle();
        args.putParcelable(MOVIE_ITEM_ID, movieItem);

        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(MOVIE_ITEM_ID)) {
            movieItem = getArguments().getParcelable(MOVIE_ITEM_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.example.sahil.popularmovies.R.layout.movie_detail, container, false);
        if (movieItem != null) {
            ((TextView) rootView.findViewById(com.example.sahil.popularmovies.R.id.tv_title)).setText(movieItem.getMovieTitle());
            ((TextView) rootView.findViewById(com.example.sahil.popularmovies.R.id.tv_image_title)).setText(movieItem.getMovieTitle());
            ((TextView) rootView.findViewById(com.example.sahil.popularmovies.R.id.tv_release_date)).setText(movieItem.getReleaseDate());
            ((TextView) rootView.findViewById(com.example.sahil.popularmovies.R.id.tv_overview)).setText(movieItem.getPlotSynopsis());
            ((TextView) rootView.findViewById(com.example.sahil.popularmovies.R.id.tv_rating)).setText(String.valueOf(movieItem.getUserRating())
                    + "/" + Constants.DEFAULT_RATING);
            String url = Constants.BASE_IMAGE_URL + movieItem.getPosterPath();
            Picasso.with(getActivity()).load(url).placeholder(com.example.sahil.popularmovies.R.drawable.ic_launcher_foreground)
                    .into((ImageView) rootView.findViewById(com.example.sahil.popularmovies.R.id.iv_poster));
        }

        return rootView;
    }
}
