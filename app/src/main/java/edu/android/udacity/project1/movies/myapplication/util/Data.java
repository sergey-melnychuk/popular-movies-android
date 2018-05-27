package edu.android.udacity.project1.movies.myapplication.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.android.udacity.project1.movies.myapplication.data.MoviesContract;
import edu.android.udacity.project1.movies.myapplication.model.Movie;

public class Data {

    private static final String TAG = Data.class.getName();

    public static void delete(Context context, String name) {
        File dir = context.getFilesDir();
        File file = new File(dir, name);
        file.delete();
    }

    public static void save(Context context, String name, Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(name, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            Log.d(TAG, "Saved file: " + name);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Cant find file", e);
        } catch (IOException e) {
            Log.e(TAG, "IO exception", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public static ContentValues movie2cv(Movie movie) {
        ContentValues cv = new ContentValues();
        cv.put(MoviesContract.MovieEntry.COLUMN_EXTERNAL_ID, movie.id);
        cv.put(MoviesContract.MovieEntry.COLUMN_TITLE, movie.title);
        cv.put(MoviesContract.MovieEntry.COLUMN_YEAR, movie.releaseDate.substring(0,4));
        cv.put(MoviesContract.MovieEntry.COLUMN_RATING, movie.voteAverage);
        cv.put(MoviesContract.MovieEntry.COLUMN_PLOT, movie.overview);
        return cv;
    }

    public static List<Movie> fetchMovies(Cursor cursor, Context context) {
        List<Movie> movies = new ArrayList<>();
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                int externalId = cursor.getInt(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_EXTERNAL_ID));
                String title = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_TITLE));
                String year = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_YEAR));
                double rating = cursor.getDouble(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_RATING));
                String plot = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.COLUMN_PLOT));
                String uri = "file://" + context.getFilesDir() + "/" + externalId;
                Movie movie = new Movie(externalId, title, uri, year, plot, rating);
                movies.add(movie);
            } while (cursor.moveToNext());
        }
        return movies;
    }
}
