package nonso.android.nonso.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nonso.android.nonso.models.Image;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.elasticSearch.UserHitsPOJO;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.interfaces.ElasticSearchApi;
import nonso.android.nonso.models.interfaces.ElasticSearchCallback;
import nonso.android.nonso.utils.ImageUtils;
import nonso.android.nonso.utils.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Response;

public class UsersDB {

    private FirebaseAuth mAuth = new AuthDB().getmAuth();
    private FirebaseFirestore db = new AuthDB().getDb();
    private final String TAG = this.getClass().getSimpleName();


    private static final String DATABASE_COLLECTION_USERS = "users/";

    public void createUser(String email, String password, final String username, final Callback callback){

        final User userPojo = new User();
        userPojo.setUserName(username);
        userPojo.setEmail(email);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d(TAG, "createUserWithEmail:success");

                        final FirebaseUser authUser = mAuth.getCurrentUser();

                        userPojo.setUserId(authUser.getUid());
                        authUser.sendEmailVerification();
                        db.collection(DATABASE_COLLECTION_USERS).document(authUser.getUid())
                                .set(userPojo)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "Created new user");
                                    callback.result(Result.SUCCESS);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "Creating new user false");
                                    callback.result(Result.FAILED);

                                });

                    }else {
                        callback.result(Result.FAILED);
                    }
                });
    }

    public void updateUserGoals(String userId, String goals, final Callback callback){

        DocumentReference mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        mUserRef.update("goal", goals)
                .addOnSuccessListener(aVoid -> {
                    Log.v(TAG, "Success: Updated user goal");
                    callback.result(Result.SUCCESS);
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Failed: Update user goal failed", e);
                    callback.result(Result.FAILED);
                });
    }

    private void updateUserImage(String userId, Image image, final Callback callback){

        DocumentReference mUserRef = db.collection(DATABASE_COLLECTION_USERS).document(userId);
        mUserRef.update("image.imageReference", image.getImageReference(), "image.imageUrl", image.getImageUrl() )
                .addOnSuccessListener(Void ->
                        callback.result(Result.SUCCESS)
                )
                .addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
    }

    public void saveUserImage(final String userId, Image image, final Callback callback){

        Bitmap bitmap = new ImageUtils().StringToBitMap(image.getImageUrl());


        FirebaseUtils firebaseUtils = new FirebaseUtils();

        firebaseUtils.uploadImage(bitmap, image.getImageReference(), new Callback() {
            @Override
            public void result(Result result) {

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

            @Override
            public void imageResult(Uri downloadUrl) {
                image.setImageUrl(downloadUrl.toString());
                updateUserImage(userId, image, callback);
            }

            @Override
            public void authorizationResult(FirebaseUser user) {

            }
        });
    }


   public void searchForUsers(String query, ElasticSearchCallback callback){

       ElasticSearchApi elasticSearch = RetrofitClientInstance.getRetrofitInstance().create(ElasticSearchApi.class);
       Map<String, String> headers = new HashMap<>();
       headers.put("Authorization", "Basic dXNlcjpkVXNEelh5WTFSNVY=");
       headers.put("Content-Type", "application/json");

       Call<UserHitsPOJO> call = elasticSearch.search(headers, "AND", query);

       call.enqueue(new retrofit2.Callback<UserHitsPOJO>() {
           @Override
           public void onResponse(Call<UserHitsPOJO> call, Response<UserHitsPOJO> response) {
               callback.users(response.body());
           }

           @Override
           public void onFailure(Call<UserHitsPOJO> call, Throwable t) {
                callback.result(Result.FAILED);
           }
       });
   }

}
