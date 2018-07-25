package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Journey implements Parcelable {

    private String journeyId;
    private String name;
    private String description;
    private String profileImage;
    private boolean permissions;
    private boolean matureContent;
    private boolean subscriptions;
    private boolean displayFollowers;
    private CreatedBy createdBy;
    private Map<String, Boolean> subscribers;
    private Map<String, Boolean> blockedList;
    private Map<String, Boolean> categories;
    private Map<String, Boolean> steps;
    @ServerTimestamp private Date createdAt;
    private Date updatedAt;


    public Journey(){
        this.subscribers = new HashMap<>();
        this.blockedList = new HashMap<>();
        this.categories = new HashMap<>();
        this.steps = new HashMap<>();
    }

    public Map<String, Boolean> getCategories() {
        return categories;
    }

    public void setCategories(Map<String, Boolean> categories) {
        this.categories = categories;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Map<String, Boolean> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Map<String, Boolean> subscribers) {
        this.subscribers = subscribers;
    }

    public Map<String, Boolean> getBlockedList() {
        return blockedList;
    }

    public void setBlockedList(Map<String, Boolean> blockedList) {
        this.blockedList = blockedList;
    }

    public boolean isDisplayFollowers() {
        return displayFollowers;
    }

    public void setDisplayFollowers(boolean displayFollowers) {
        this.displayFollowers = displayFollowers;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isPermissions() {
        return permissions;
    }

    public void setPermissions(boolean permissions) {
        this.permissions = permissions;
    }

    public boolean isSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(boolean subscriptions) {
        this.subscriptions = subscriptions;
    }

    public boolean isMatureContent() {
        return matureContent;
    }

    public void setMatureContent(boolean matureContent) {
        this.matureContent = matureContent;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public Map<String, Boolean> getSteps() {
        return steps;
    }

    public void setSteps(Map<String, Boolean> steps) {
        this.steps = steps;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.journeyId);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.profileImage);
        dest.writeByte(this.permissions ? (byte) 1 : (byte) 0);
        dest.writeByte(this.matureContent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.subscriptions ? (byte) 1 : (byte) 0);
        dest.writeByte(this.displayFollowers ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.createdBy, flags);
        dest.writeInt(this.subscribers.size());
        for (Map.Entry<String, Boolean> entry : this.subscribers.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.blockedList.size());
        for (Map.Entry<String, Boolean> entry : this.blockedList.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.categories.size());
        for (Map.Entry<String, Boolean> entry : this.categories.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.steps.size());
        for (Map.Entry<String, Boolean> entry : this.steps.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    protected Journey(Parcel in) {
        this.journeyId = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.profileImage = in.readString();
        this.permissions = in.readByte() != 0;
        this.matureContent = in.readByte() != 0;
        this.subscriptions = in.readByte() != 0;
        this.displayFollowers = in.readByte() != 0;
        this.createdBy = in.readParcelable(CreatedBy.class.getClassLoader());
        int subscribersSize = in.readInt();
        this.subscribers = new HashMap<String, Boolean>(subscribersSize);
        for (int i = 0; i < subscribersSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.subscribers.put(key, value);
        }
        int blockedListSize = in.readInt();
        this.blockedList = new HashMap<String, Boolean>(blockedListSize);
        for (int i = 0; i < blockedListSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.blockedList.put(key, value);
        }
        int categoriesSize = in.readInt();
        this.categories = new HashMap<String, Boolean>(categoriesSize);
        for (int i = 0; i < categoriesSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.categories.put(key, value);
        }
        int stepsSize = in.readInt();
        this.steps = new HashMap<String, Boolean>(stepsSize);
        for (int i = 0; i < stepsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.steps.put(key, value);
        }
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Creator<Journey> CREATOR = new Creator<Journey>() {
        @Override
        public Journey createFromParcel(Parcel source) {
            return new Journey(source);
        }

        @Override
        public Journey[] newArray(int size) {
            return new Journey[size];
        }
    };
}
