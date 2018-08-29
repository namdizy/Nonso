package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

import nonso.android.nonso.utils.StringGenerator;

public class Image implements Parcelable {

    private String imageUrl;
    private String imageReference;


    public void Image(){

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageReference() {
        return imageReference;
    }

    public void setImageReference(String imageReference) {
        this.imageReference = imageReference;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.imageUrl);
        dest.writeString(this.imageReference);
    }

    public Image() {
    }

    protected Image(Parcel in) {
        this.imageUrl = in.readString();
        this.imageReference = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
