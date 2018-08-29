package nonso.android.nonso.models.interfaces;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Map;

import nonso.android.nonso.models.CreatedBy;
import nonso.android.nonso.models.Image;

public interface JourneyInterface {

    String getJourneyId();
    String getName();
    String getDescription();
    Image getImage();
    boolean isPermissions();
    boolean isMatureContent();
    boolean isSubscriptions();
    boolean isDisplayFollowers();
    CreatedBy getCreatedBy();
    Map<String, Boolean> getCategories();
    Date getCreatedAt();
    Date getUpdatedAt();
}
