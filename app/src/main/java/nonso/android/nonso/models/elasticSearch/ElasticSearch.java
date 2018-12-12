package nonso.android.nonso.models.elasticSearch;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

public class ElasticSearch implements Parcelable {

    private String password;
    private String authorization;

    @Nullable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public ElasticSearch() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.password);
        dest.writeString(this.authorization);
    }

    protected ElasticSearch(Parcel in) {
        this.password = in.readString();
        this.authorization = in.readString();
    }

    public static final Creator<ElasticSearch> CREATOR = new Creator<ElasticSearch>() {
        @Override
        public ElasticSearch createFromParcel(Parcel source) {
            return new ElasticSearch(source);
        }

        @Override
        public ElasticSearch[] newArray(int size) {
            return new ElasticSearch[size];
        }
    };
}


