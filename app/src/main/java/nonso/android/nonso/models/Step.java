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
    private Map<String, Boolean> imageUrls;
    private String videoUrl;
    private String bodyText;
    private String description;
    private String journeyId;
    private String creatorId;
    private Boolean publish;
    private Boolean updateFlag;
    private CreatedBy createdBy;
    @ServerTimestamp private Date createdAt;
    private Date updatedAt;

    public Step(){
        this.likes = new HashMap<>();
        this.imageUrls = new HashMap<>();
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

    public Map<String, Boolean> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(Map<String, Boolean> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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

    public String getJourneyId() {
        return journeyId;
    }

    public void setJourneyId(String journeyId) {
        this.journeyId = journeyId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String userId) {
        this.creatorId = userId;
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

    public Boolean getUpdateFlag() {
        return updateFlag;
    }

    public void setUpdateFlag(Boolean updateFlag) {
        this.updateFlag = updateFlag;
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
        dest.writeInt(this.imageUrls.size());
        for (Map.Entry<String, Boolean> entry : this.imageUrls.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeString(this.videoUrl);
        dest.writeString(this.bodyText);
        dest.writeString(this.description);
        dest.writeString(this.journeyId);
        dest.writeString(this.creatorId);
        dest.writeValue(this.publish);
        dest.writeValue(this.updateFlag);
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
        int imageUrlsSize = in.readInt();
        this.imageUrls = new HashMap<String, Boolean>(imageUrlsSize);
        for (int i = 0; i < imageUrlsSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.imageUrls.put(key, value);
        }
        this.videoUrl = in.readString();
        this.bodyText = in.readString();
        this.description = in.readString();
        this.journeyId = in.readString();
        this.creatorId = in.readString();
        this.publish = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.updateFlag = (Boolean) in.readValue(Boolean.class.getClassLoader());
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
