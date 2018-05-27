package edu.android.udacity.project1.movies.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

// REVIEW: Consider using Parcelable instead of Serializable
// http://www.developerphil.com/parcelable-vs-serializable/

public class Movie implements Parcelable {
    public final int id;
    public final String title;
    public final String posterImageUri;
    public final String releaseDate;
    public final String overview;
    public final double voteAverage;

    public Movie(int id, String title, String posterImageUri, String releaseDate, String overview, double voteAverage) {
        this.id = id;
        this.title = title;
        this.posterImageUri = posterImageUri;
        this.releaseDate = releaseDate;
        this.overview = overview;
        this.voteAverage = voteAverage;
    }

    public Movie(Parcel in) {
        this.id = in.readInt();
        this.title = in.readString();
        this.posterImageUri = in.readString();
        this.releaseDate = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(posterImageUri);
        dest.writeString(releaseDate);
        dest.writeString(overview);
        dest.writeDouble(voteAverage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }
        @Override
        public Movie[] newArray(int size) {
            return new Movie[0];
        }
    };
}
