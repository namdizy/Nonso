package nonso.android.nonso.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import nonso.android.nonso.models.Image;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.utils.ImageUtils;

public class JourneysDB {

    private FirebaseFirestore db = new AuthDB().getDb();
    private final String TAG = this.getClass().getSimpleName();

    private Journey sJourney;

    private static final String DATABASE_COLLECTION_JOURNEYS = "journeys/";


    public void getJourney(String journeyId, final Callback callback){

        DocumentReference docRef = db.collection(DATABASE_COLLECTION_JOURNEYS).document(journeyId);
        docRef.get().addOnSuccessListener( documentSnapshot ->
                callback.journeyResult(documentSnapshot.toObject(Journey.class))
        );
    }

    public void saveJourney(Journey journey, final Callback callback){

        sJourney = journey;

        FirebaseUtils firebaseUtils = new FirebaseUtils();

        if(sJourney.getImage() != null){

            ImageUtils imageUtils = new ImageUtils();
            Bitmap imageBitmap = imageUtils.StringToBitMap(sJourney.getImage().getImageUrl());

            firebaseUtils.uploadImage(imageBitmap, sJourney.getImage().getImageReference(), new Callback() {
                @Override
                public void result(Result result) {}

                @Override
                public void authorizationResult(FirebaseUser user) {

                }

                @Override
                public void userResult(User user) {

                }

                @Override
                public void journeyResult(Journey journey) {

                }

                @Override
                public void stepResult(Step step) {

                }

                @Override
                public void imageResult(Uri downloadUrl) {
                    createJourney(downloadUrl, callback);
                }
            });
        }else{
            createJourney(null, callback);
        }
    }

    public void deleteJourney(Journey journey, final Callback callback){
        db.collection(DATABASE_COLLECTION_JOURNEYS).
                document(journey.getJourneyId())
                .delete()
                .addOnSuccessListener( Void ->
                        callback.result(Result.SUCCESS)
                ).addOnFailureListener(e -> {
            callback.result(Result.FAILED);
            Log.v(TAG, "Failed to delete journey");
        });
    }

    private void createJourney(Uri downloadUri, final Callback callback){

        if(downloadUri != null){
            Image image = sJourney.getImage();
            image.setImageUrl(downloadUri.toString());
            sJourney.setImage(image);
        }
        db.collection(DATABASE_COLLECTION_JOURNEYS).add(sJourney)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("journeyId", documentReference.getId());
                    callback.result(Result.SUCCESS);}
                )
                .addOnFailureListener(e -> {
                            Log.w(TAG, "Error adding uploading document", e);
                            callback.result(Result.FAILED);
                        }
                );
    }

    public void updateJourneyDescription(String journeyId, String description, final Callback callback){

        DocumentReference journeyRef = db.collection(DATABASE_COLLECTION_JOURNEYS).document(journeyId);
        journeyRef.update("description", description)
                .addOnSuccessListener(Void ->
                        callback.result(Result.SUCCESS)

                ).addOnFailureListener(e ->
                callback.result(Result.FAILED)
        );
    }
}
