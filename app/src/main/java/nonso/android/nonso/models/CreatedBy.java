
package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

public class CreatedBy implements Parcelable {
    private String id;
    private String name;
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
    }

    public CreatedBy() {
    }

    protected CreatedBy(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imageUrl = in.readString();
    }

    public static final Parcelable.Creator<CreatedBy> CREATOR = new Parcelable.Creator<CreatedBy>() {
        @Override
        public CreatedBy createFromParcel(Parcel source) {
            return new CreatedBy(source);
        }

        @Override
        public CreatedBy[] newArray(int size) {
            return new CreatedBy[size];
        }
    };
}