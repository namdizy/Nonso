package nonso.android.nonso.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Step implements Parcelable {

    private String StepId;
    private String title;
    private StepType stepType;
    private Map<String, Boolean> likes;
    private Map<String, Image> images;
    private Video video;
    private String bodyText;
    private String description;
    private Boolean publish;
    private CreatedBy createdBy;
    @ServerTimestamp private Date createdAt;
    private Date updatedAt;

    public Step(){
        this.images = new HashMap<>();
        this.likes = new HashMap<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public StepType getStepType() {
        return stepType;
    }

    public void setStepType(StepType stepType) {
        this.stepType = stepType;
    }

    public Map<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(Map<String, Boolean> likes) {
        this.likes = likes;
    }

    public Map<String, Image> getImages() {
        return images;
    }

    public void setImages(Map<String, Image> images) {
        this.images = images;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public String getStepId() {
        return StepId;
    }

    public void setStepId(String stepId) {
        StepId = stepId;
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
        dest.writeString(this.StepId);
        dest.writeString(this.title);
        dest.writeInt(this.stepType == null ? -1 : this.stepType.ordinal());
        dest.writeInt(this.likes.size());
        for (Map.Entry<String, Boolean> entry : this.likes.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.images.size());
        for (Map.Entry<String, Image> entry : this.images.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeParcelable(entry.getValue(), flags);
        }
        dest.writeParcelable(this.video, flags);
        dest.writeString(this.bodyText);
        dest.writeString(this.description);
        dest.writeValue(this.publish);
        dest.writeParcelable(this.createdBy, flags);
        dest.writeLong(this.createdAt != null ? this.createdAt.getTime() : -1);
        dest.writeLong(this.updatedAt != null ? this.updatedAt.getTime() : -1);
    }

    protected Step(Parcel in) {
        this.StepId = in.readString();
        this.title = in.readString();
        int tmpStepType = in.readInt();
        this.stepType = tmpStepType == -1 ? null : StepType.values()[tmpStepType];
        int likesSize = in.readInt();
        this.likes = new HashMap<String, Boolean>(likesSize);
        for (int i = 0; i < likesSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.likes.put(key, value);
        }
        int imagesSize = in.readInt();
        this.images = new HashMap<String, Image>(imagesSize);
        for (int i = 0; i < imagesSize; i++) {
            String key = in.readString();
            Image value = in.readParcelable(Image.class.getClassLoader());
            this.images.put(key, value);
        }
        this.video = in.readParcelable(Video.class.getClassLoader());
        this.bodyText = in.readString();
        this.description = in.readString();
        this.publish = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.createdBy = in.readParcelable(CreatedBy.class.getClassLoader());
        long tmpCreatedAt = in.readLong();
        this.createdAt = tmpCreatedAt == -1 ? null : new Date(tmpCreatedAt);
        long tmpUpdatedAt = in.readLong();
        this.updatedAt = tmpUpdatedAt == -1 ? null : new Date(tmpUpdatedAt);
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel source) {
            return new Step(source);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}
