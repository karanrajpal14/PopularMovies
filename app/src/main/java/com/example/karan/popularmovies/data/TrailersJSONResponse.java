package com.example.karan.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailersJSONResponse implements Parcelable {

    public final static Parcelable.Creator<TrailersJSONResponse> CREATOR = new Creator<TrailersJSONResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public TrailersJSONResponse createFromParcel(Parcel in) {
            TrailersJSONResponse instance = new TrailersJSONResponse();
            instance.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
            in.readList(instance.results, (com.example.karan.popularmovies.data.Trailers.class.getClassLoader()));
            return instance;
        }

        public TrailersJSONResponse[] newArray(int size) {
            return (new TrailersJSONResponse[size]);
        }

    };
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("results")
    @Expose
    private List<Trailers> results = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailers> getResults() {
        return results;
    }

    public void setResults(List<Trailers> results) {
        this.results = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeList(results);
    }

    public int describeContents() {
        return 0;
    }

}