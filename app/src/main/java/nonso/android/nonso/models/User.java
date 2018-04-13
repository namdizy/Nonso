package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {

    private String name;
    private String email;
    private String goal;
    private String userId;
    private String imageUri;
    private ArrayList<String> createdJourneys;
    private ArrayList<String> followingJourneys;
    private ArrayList<String> subscribedJourneys;
    private ArrayList<String> followingUsers;
    private ArrayList<String> followersUsers;

    public User(){

    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public ArrayList<String> getCreatedJourneys() {
        return createdJourneys;
    }

    public void setCreatedJourneys(ArrayList<String> createdJourneys) {
        this.createdJourneys = createdJourneys;
    }

    public ArrayList<String> getFollowingJourneys() {
        return followingJourneys;
    }

    public void setFollowingJourneys(ArrayList<String> followingJourneys) {
        this.followingJourneys = followingJourneys;
    }

    public ArrayList<String> getSubscribedJourneys() {
        return subscribedJourneys;
    }

    public void setSubscribedJourneys(ArrayList<String> subscribedJourneys) {
        this.subscribedJourneys = subscribedJourneys;
    }

    public ArrayList<String> getFollowingUsers() {
        return followingUsers;
    }

    public void setFollowingUsers(ArrayList<String> followingUsers) {
        this.followingUsers = followingUsers;
    }

    public ArrayList<String> getFollowersUsers() {
        return followersUsers;
    }

    public void setFollowersUsers(ArrayList<String> followersUsers) {
        this.followersUsers = followersUsers;
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
        dest.writeStringList(this.createdJourneys);
        dest.writeStringList(this.followingJourneys);
        dest.writeStringList(this.subscribedJourneys);
        dest.writeStringList(this.followingUsers);
        dest.writeStringList(this.followersUsers);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.goal = in.readString();
        this.userId = in.readString();
        this.imageUri = in.readString();
        this.createdJourneys = in.createStringArrayList();
        this.followingJourneys = in.createStringArrayList();
        this.subscribedJourneys = in.createStringArrayList();
        this.followingUsers = in.createStringArrayList();
        this.followersUsers = in.createStringArrayList();
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
