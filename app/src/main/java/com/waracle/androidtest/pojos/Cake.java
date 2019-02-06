package com.waracle.androidtest.pojos;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

public class Cake implements Parcelable {
    private String title;
    private String description;
    private byte[] imageData;

    public Cake() {
    }

    protected Cake(Parcel in) {
        title = in.readString();
        description = in.readString();
        imageData = in.createByteArray();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByteArray(imageData);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Cake> CREATOR = new Creator<Cake>() {
        @Override
        public Cake createFromParcel(Parcel in) {
            return new Cake(in);
        }

        @Override
        public Cake[] newArray(int size) {
            return new Cake[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
