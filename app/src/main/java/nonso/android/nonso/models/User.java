package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {

    private String name;
    private String email;
    private String goal;
    private String userId;
    private String imageUri;
    private Map<String, Boolean> createdJourneys;
    private Map<String, Boolean> followingJourneys;
    private Map<String, Boolean> subscribedJourneys;
    private Map<String, Boolean> followingUsers;
    private Map<String, Boolean> followersUsers;
    private Time createdAt;
    private Time updatedAt;

    public Time getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Time createdAt) {
        this.createdAt = createdAt;
    }

    public Time getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Time updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Map<String, Boolean> getCreatedJourneys() {
        return createdJourneys;
    }

    public void setCreatedJourneys(Map<String, Boolean> createdJourneys) {
        this.createdJourneys = createdJourneys;
    }

    public Map<String, Boolean> getFollowingJourneys() {
        return followingJourneys;
    }

    public void setFollowingJourneys(Map<String, Boolean> followingJourneys) {
        this.followingJourneys = followingJourneys;
    }

    public Map<String, Boolean> getSubscribedJourneys() {
        return subscribedJourneys;
    }

    public void setSubscribedJourneys(Map<String, Boolean> subscribedJourneys) {
        this.subscribedJourneys = subscribedJourneys;
    }

    public Map<String, Boolean> getFollowingUsers() {
        return followingUsers;
    }

    public void setFollowingUsers(Map<String, Boolean> followingUsers) {
        this.followingUsers = followingUsers;
    }

    public Map<String, Boolean> getFollowersUsers() {
        return followersUsers;
    }

    public void setFollowersUsers(Map<String, Boolean> followersUsers) {
        this.followersUsers = followersUsers;
    }

    public User(){
        this.createdJourneys = new HashMap<>();
        this.followersUsers = new HashMap<>();
        this.followingJourneys = new HashMap<>();
        this.followingUsers = new HashMap<>();
        this.subscribedJourneys = new HashMap<>();
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.goal);
        dest.writeString(this.userId);
        dest.writeString(this.imageUri);
        dest.writeInt(this.createdJourneys.size());
        for (Map.Entry<String, Boolean> entry : this.createdJourneys.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.followingJourneys.size());
        for (Map.Entry<String, Boolean> entry : this.followingJourneys.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.subscribedJourneys.size());
        for (Map.Entry<String, Boolean> entry : this.subscribedJourneys.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.followingUsers.size());
        for (Map.Entry<String, Boolean> entry : this.followingUsers.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.followersUsers.size());
        for (Map.Entry<String, Boolean> entry : this.followersUsers.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.goal = in.readString();
        this.userId = in.readString();
        this.imageUri = in.readString();
        int createdJourneysSize = in.readInt();
        this.createdJourneys = new HashMap<String, Boolean>(createdJourneysSize);
        for (int i = 0; i < createdJourneysSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.createdJourneys.put(key, value);
        }
        int followingJourneysSize = in.readInt();
        this.followingJourneys = new HashMap<String, Boolean>(followingJourneysSize);
        for (int i = 0; i < followingJourneysSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.followingJourneys.put(key, value);
        }
        int subscribedJourneysSize = in.readInt();
        this.subscribedJourneys = new HashMap<String, Boolean>(subscribedJourneysSize);
        for (int i = 0; i < subscribedJourneysSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.subscribedJourneys.put(key, value);
        }
        int followingUsersSize = in.readInt();
        this.followingUsers = new HashMap<String, Boolean>(followingUsersSize);
        for (int i = 0; i < followingUsersSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.followingUsers.put(key, value);
        }
        int followersUsersSize = in.readInt();
        this.followersUsers = new HashMap<String, Boolean>(followersUsersSize);
        for (int i = 0; i < followersUsersSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.followersUsers.put(key, value);
        }
        this.createdAt = (Time) in.readSerializable();
        this.updatedAt = (Time) in.readSerializable();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
