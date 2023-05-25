package com.application.homedinein;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Parcelable {
    private String Title;
    ArrayList<String> Image;
    private double Ratings;
    private int SeatingPerTable;
    private int NumberOfTables;
    private  boolean isDisabled;

    public Restaurant(String Title, ArrayList<String> Image, String overview, double Ratings, int SeatingPerTable, int NumberOfTables, boolean isDisabled) {
        this.Title = Title;
        this.Ratings = Ratings;
        this.Image = Image;
        this.SeatingPerTable = SeatingPerTable;
        this.NumberOfTables = NumberOfTables;
        this.isDisabled=isDisabled;
    }

    public ArrayList<String> getImage() {
        return Image;
    }

    public String getTitle() {
        return Title;
    }

    public String getPoster(int index) {
        return Image.get(index);
    }

    public double getRating() {
        return Ratings;
    }

    public int getSeatingPerTable() {
        return SeatingPerTable;
    }

    public int getNumberOfTables() {
        return NumberOfTables;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.Title);
        parcel.writeDouble(this.Ratings);
        parcel.writeList(this.Image);
        parcel.writeInt(this.SeatingPerTable);
        parcel.writeInt(this.NumberOfTables);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(this.isDisabled);
        }

    }

    private Restaurant(Parcel in){
        this.Title = in.readString();
        this.Ratings = in.readDouble();
        this.Image = in.readArrayList(String.class.getClassLoader());
        this.SeatingPerTable = in.readInt();
        this.NumberOfTables = in.readInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.isDisabled=in.readBoolean();
        }
    }

    public static final Parcelable.Creator<Restaurant> CREATOR = new Parcelable.Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };
}
