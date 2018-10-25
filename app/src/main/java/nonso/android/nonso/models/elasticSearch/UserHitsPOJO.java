package nonso.android.nonso.models.elasticSearch;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


@IgnoreExtraProperties
public class UserHitsPOJO {

    @SerializedName("hits")
    @Expose
    private UserHitsList hitsList;

    public UserHitsList getHitsList() {
        return hitsList;
    }

    public void setHitsList(UserHitsList hitsList) {
        this.hitsList = hitsList;
    }
}
