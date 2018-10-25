package nonso.android.nonso.models.elasticSearch;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import nonso.android.nonso.models.User;


@IgnoreExtraProperties
public class UserSource {

    @SerializedName("_source")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
