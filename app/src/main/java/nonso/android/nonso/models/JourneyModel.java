package nonso.android.nonso.models;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;
import java.util.Map;

@Entity
public class JourneyModel {

    @PrimaryKey(autoGenerate = false)
    private String journeyId;
    private String name;
    private String description;
    private String profileImage;
    private boolean permissions;
    private boolean matureContent;
    private boolean subscriptions;
    private boolean displayFollowers;
    private CreatedBy createdBy;
    private Map<String, Boolean> categories;
    @TypeConverters(DateConverter.class)
    private Date createdAt;
    @TypeConverters(DateConverter.class)
    private Date updatedAt;

    public JourneyModel(String journeyId, String name, String description, String profileImage,
                        boolean permissions, boolean matureContent, boolean subscriptions, boolean displayFollowers,
                        CreatedBy createdBy, Map categories, Date createdAt, Date updatedAt){

        this.journeyId = journeyId;
        this.name = name;
        this.description = description;
        this.profileImage = profileImage;
        this.permissions = permissions;
        this.matureContent = matureContent;
        this.subscriptions = subscriptions;
        this.displayFollowers = displayFollowers;
        this.createdBy = createdBy;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setPermissions(boolean permissions) {
        this.permissions = permissions;
    }

    public void setMatureContent(boolean matureContent) {
        this.matureContent = matureContent;
    }

    public void setSubscriptions(boolean subscriptions) {
        this.subscriptions = subscriptions;
    }

    public void setDisplayFollowers(boolean displayFollowers) {
        this.displayFollowers = displayFollowers;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public void setCategories(Map<String, Boolean> categories) {
        this.categories = categories;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
