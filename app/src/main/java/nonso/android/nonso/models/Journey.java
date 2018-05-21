package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Journey implements Parcelable {

    private String journeyId;
    private String name;
    private String description;
    private String userId;
    private String profileImage;
    private boolean permissions;
    private boolean subscriptions;
    private boolean tier1;
    private boolean tier2;
    private boolean tier3;
    private boolean displayFollowers;
    private Map<String, Boolean> subscribers;
    private Map<String, Boolean> blockedList;
    private Map<String, Boolean> categories;
    private Map<String, Boolean> steps;
    private Date createdAt;
    @ServerTimestamp private Date updatedAt;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isDisplayFollowers() {
        return displayFollowers;
    }

    public void setDisplayFollowers(boolean displayFollowers) {
        this.displayFollowers = displayFollowers;
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

    public boolean isTier1() {
        return tier1;
    }

    public void setTier1(boolean tier1) {
        this.tier1 = tier1;
    }

    public boolean isTier2() {
        return tier2;
    }

    public void setTier2(boolean tier2) {
        this.tier2 = tier2;
    }

    public boolean isTier3() {
        return tier3;
    }

    public void setTier3(boolean tier3) {
        this.tier3 = tier3;
    }

    public Journey(){
        this.subscribers = new HashMap<>();
        this.blockedList = new HashMap<>();
        this.categories = new HashMap<>();
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
        dest.writeString(this.userId);
        dest.writeString(this.profileImage);
        dest.writeByte(this.permissions ? (byte) 1 : (byte) 0);
        dest.writeByte(this.subscriptions ? (byte) 1 : (byte) 0);
        dest.writeByte(this.tier1 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.tier2 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.tier3 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.displayFollowers ? (byte) 1 : (byte) 0);
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
        this.userId = in.readString();
        this.profileImage = in.readString();
        this.permissions = in.readByte() != 0;
        this.subscriptions = in.readByte() != 0;
        this.tier1 = in.readByte() != 0;
        this.tier2 = in.readByte() != 0;
        this.tier3 = in.readByte() != 0;
        this.displayFollowers = in.readByte() != 0;
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
