package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {

    private String userName;
    private String email;
    private String goal;
    private String userId;
    private Image image;
    private Map<String, Boolean> likedPost;
    private Map<String, Boolean> likedSteps;
    @ServerTimestamp private Date createdAt;
    private Date updatedAt;

    public User(){
        this.image = new Image();
        this.likedPost = new HashMap<>();
        this.likedSteps = new HashMap<>();
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
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

    public Map<String, Boolean> getLikedPost() {
        return likedPost;
    }

    public void setLikedPost(Map<String, Boolean> likedPost) {
        this.likedPost = likedPost;
    }

    public Map<String, Boolean> getLikedSteps() {
        return likedSteps;
    }

    public void setLikedSteps(Map<String, Boolean> likedSteps) {
        this.likedSteps = likedSteps;
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
        dest.writeString(this.userName);
        dest.writeString(this.email);
        dest.writeString(this.goal);
        dest.writeString(this.userId);
        dest.writeParcelable(this.image, flags);
        dest.writeInt(this.likedPost.size());
        for (Map.Entry<String, Boolean> entry : this.likedPost.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.likedSteps.size());
        for (Map.Entry<String, Boolean> entry : this.likedSteps.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    protected User(Parcel in) {
        this.userName = in.readString();
        this.email = in.readString();
        this.goal = in.readString();
        this.userId = in.readString();
        this.image = in.readParcelable(Image.class.getClassLoader());
        int likedPostSize = in.readInt();
        this.likedPost = new HashMap<String, Boolean>(likedPostSize);
        for (int i = 0; i < likedPostSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.likedPost.put(key, value);
        }
        int likedStepsSize = in.readInt();
        this.likedSteps = new HashMap<String, Boolean>(likedStepsSize);
        for (int i = 0; i < likedStepsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.likedSteps.put(key, value);
        }
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
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
