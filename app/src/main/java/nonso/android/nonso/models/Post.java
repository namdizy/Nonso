package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post implements Parcelable {

    private String postId;
    private String title;
    private String body;
    private Map<String, Boolean> comments;
    private String journeyId;
    private CreatedBy createdBy;
    private String parentId;
    @ServerTimestamp
    private Date createdAt;
    private Date updatedAt;

    public Post(){
        this.comments = new HashMap<>();
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public Map<String, Boolean> getComments() {
        return comments;
    }

    public void setComments(Map<String, Boolean> comments) {
        this.comments = comments;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
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
        dest.writeString(this.postId);
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeInt(this.comments.size());
        for (Map.Entry<String, Boolean> entry : this.comments.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeString(this.journeyId);
        dest.writeParcelable(this.createdBy, flags);
        dest.writeString(this.parentId);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    protected Post(Parcel in) {
        this.postId = in.readString();
        this.title = in.readString();
        this.body = in.readString();
        int commentsSize = in.readInt();
        this.comments = new HashMap<String, Boolean>(commentsSize);
        for (int i = 0; i < commentsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.comments.put(key, value);
        }
        this.journeyId = in.readString();
        this.createdBy = in.readParcelable(CreatedBy.class.getClassLoader());
        this.parentId = in.readString();
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
