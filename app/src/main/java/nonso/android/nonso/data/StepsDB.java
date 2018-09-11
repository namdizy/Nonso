package nonso.android.nonso.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nonso.android.nonso.models.Image;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.Video;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.utils.ImageUtils;

public class StepsDB {


    private FirebaseFirestore db = new AuthDB().getDb();
    private final String TAG = this.getClass().getSimpleName();
    private StorageReference mStorageRef = new AuthDB().getmStorageRef();

    private static final String DATABASE_COLLECTION_STEPS = "steps/";
    private static final String DATABASE_STORAGE_VIDEO_BUCKET = "videos/";

    public void saveStep(Step step, final Callback callback){
        switch (step.getStepType()){
            case IMAGES:
                saveStepWithImages(step, callback);
                break;
            case TEXT:
                saveStepWithText(step, callback);
                break;
            case VIDEO:
                saveStepWithVideo(step, callback);
                break;
        }
    }

    private void saveStepWithImages(@NonNull Step step, Callback callback){

        if(step.getStepId() == null){
            Collection<Image> images =  step.getImages().values();
            List<Image> imageList = new ArrayList<>(images);

            List<Task<Uri>> taskArrayList= new ArrayList<>();
            for (Image i: imageList) {

                final StorageReference ref = mStorageRef.child( i.getImageReference() );
                taskArrayList.add(uploadImageTask(new ImageUtils().StringToBitMap(i.getImageUrl()), ref, new Callback() {
                    @Override
                    public void result(Result result) {

                    }

                    @Override
                    public void imageResult(Uri downloadUrl) {
                        i.setImageUrl(downloadUrl.toString());
                    }

                    @Override
                    public void authorizationResult(FirebaseUser user) {

                    }

                    @Override
                    public void journeyResult(Journey journey) {

                    }

                    @Override
                    public void stepResult(Step step) {

                    }

                    @Override
                    public void userResult(User user) {

                    }
                }));
            }

            Tasks.whenAllSuccess(taskArrayList).addOnCompleteListener(task -> {
                Map<String, Image> imageMap = new HashMap<>();
                for(Image im: imageList){
                    imageMap.put(String.valueOf(imageList.indexOf(im)), im);
                }
                step.setImages(imageMap);
                saveNewStepToFirebase(step, callback);
            });

        }else{
            updateStepInFirebase(step, callback);
        }

    }


    private Task<Uri> uploadImageTask(final Bitmap bitmap, StorageReference ref, Callback callback){


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        bitmap.recycle();

        return uploadTask.continueWithTask(task -> {
            bitmap.recycle();
            ref.getDownloadUrl().addOnCompleteListener(result->
                    callback.imageResult(result.getResult())
            );
            return ref.getDownloadUrl();
        });
    }

    private void saveStepWithVideo(@NonNull final Step step, final Callback callback){


        FirebaseUtils firebaseUtils = new FirebaseUtils();

        if(step.getStepId() == null){
            firebaseUtils.uploadVideo(step.getVideo().getVideoUrl(), DATABASE_STORAGE_VIDEO_BUCKET + step.getVideo().getVideoReference(), new Callback() {
                @Override
                public void result(Result result) {
                    callback.result(result);
                }

                @Override
                public void imageResult(Uri downloadUrl) {
                    Video v = step.getVideo();
                    v.setVideoUrl(downloadUrl.toString());
                    step.setVideo(v);
                    saveNewStepToFirebase(step, callback);
                }

                @Override
                public void authorizationResult(FirebaseUser user) {

                }

                @Override
                public void journeyResult(Journey journey) {

                }

                @Override
                public void stepResult(Step step) {

                }

                @Override
                public void userResult(User user) {

                }
            });


        }else{
            updateStepInFirebase(step, callback);
        }
    }

    private void saveStepWithText(Step step, final Callback callback){
        if(step.getStepId() == null){
            saveNewStepToFirebase(step, callback);
        }else{
            updateStepInFirebase(step, callback);
        }
    }

    private void saveNewStepToFirebase(Step step, final Callback callback){
        db.collection(DATABASE_COLLECTION_STEPS).add(step)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("stepId", documentReference.getId());
                    callback.result(Result.SUCCESS);
                })
                .addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
    }

    private void updateStepInFirebase(Step step, final Callback callback){
        db.collection(DATABASE_COLLECTION_STEPS).document(step.getStepId()).update("title", step.getTitle(),
                "bodyText", step.getBodyText(),
                "description", step.getDescription(),
                "publish", step.getPublish())
                .addOnSuccessListener(aVoid ->
                        callback.result(Result.SUCCESS)
                ).addOnFailureListener(e ->
                callback.result(Result.FAILED)
        );
    }

    public void deleteStep(Step step, final Callback callback){

        db.collection(DATABASE_COLLECTION_STEPS).document(step.getStepId()).delete()
                .addOnSuccessListener(aVoid ->
                        callback.result(Result.SUCCESS)
                ).addOnFailureListener(e ->
                callback.result(Result.FAILED)
        );
    }

}
