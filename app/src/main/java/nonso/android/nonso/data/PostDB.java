package nonso.android.nonso.data;

import android.telecom.Call;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import nonso.android.nonso.models.Like;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Shard;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.interfaces.UserListCallback;

public class PostDB {

    private FirebaseFirestore db = new AuthDB().getDb();
    private final String TAG = this.getClass().getSimpleName();
    private static final int  NUMBER_OF_SHARDS = 10;
    private AuthDB authDb;

    private FirebaseFirestore root = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private static final String DATABASE_COLLECTION_POST = "post";
    private static final String DATABASE_COLLECTION_JOURNEY = "journeys";
    private static final String DATABASE_COLLECTION_LIKES_SHARD = "likes_count_shard";
    private static final String DATABASE_SUB_COLLECTION_LIKES = "likes";
    private static final String DATABASE_COLLECTION_REPLIES = "replies";
    private static final String DATABASE_COLLECTION_USERS = "users";

    public void savePost(Post post, Callback callback){

        db.collection(DATABASE_COLLECTION_JOURNEY).document(post.getJourneyId())
            .collection(DATABASE_COLLECTION_POST)
            .add(post).addOnSuccessListener(documentReference -> {
                documentReference.update("postId", documentReference.getId(), "documentReference", documentReference.getPath());
                this.createLikesCounter(documentReference,NUMBER_OF_SHARDS)
                        .addOnSuccessListener(aVoid -> callback.result(Result.SUCCESS))
                        .addOnFailureListener(e -> callback.result(Result.FAILED));
            }).addOnFailureListener(e ->
                callback.result(Result.FAILED
            ));
    }

    public void likePost(Post post, Like like, User user, Callback callback){
        DocumentReference ref = root.document(post.getDocumentReference());


        ref.collection(DATABASE_SUB_COLLECTION_LIKES)
                .add(like)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("likeId", documentReference.getId())
                        .addOnSuccessListener(aVoid -> {
                            this.incrementLikesCounter(ref, NUMBER_OF_SHARDS)
                                    .addOnSuccessListener(aVoidInner ->
                                            this.updateUserLikes(documentReference.getId(), user, callback )
                                    )
                                    .addOnFailureListener(e ->{
                                            Log.v(TAG, e.getMessage());
                                            callback.result(Result.FAILED);}
                                    );
                        });
                })
                .addOnFailureListener(e ->
                 callback.result(Result.FAILED)
                );
    }

    private void updateUserLikes(String likeId, User user, Callback callback){
        Map<String, Boolean> list = user.getLikedPost();
        list.put(likeId, true);

        db.collection(DATABASE_COLLECTION_USERS).document(user.getUserId())
                .update("likedPost", list)
                .addOnSuccessListener(aVoid -> {
                    callback.result(Result.SUCCESS);
                })
                .addOnFailureListener(e -> {
                    callback.result(Result.FAILED);
                });

    }

    public void unlikePost(Post post, String userId, Callback callback){
//        DocumentReference ref = root.document(post.getDocumentReference());
//        ref.collection(DATABASE_SUB_COLLECTION_LIKES).whereEqualTo("creatorId", userId)
//                .add

    }

    public void savePostReply(Post parent, Post child, final Callback callback){
        DocumentReference ref = root.document(parent.getDocumentReference());
        ref.collection(DATABASE_COLLECTION_REPLIES)
            .add(child)
            .addOnSuccessListener(documentReference -> {
                documentReference.update("postId", documentReference.getId(), "documentReference", documentReference.getPath())
                .addOnSuccessListener(aVoid ->
                        callback.result(Result.SUCCESS)
                ).addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
            }).addOnFailureListener(e ->
                callback.result(Result.FAILED
            ));
    }

    private Task<Void> createLikesCounter(final DocumentReference ref, final int numShards) {
        List<Task<Void>> tasks = new ArrayList<>();

        // Initialize each shard with count=0
        for (int i = 0; i < numShards; i++) {
            Task<Void> makeShard = ref.collection(DATABASE_COLLECTION_LIKES_SHARD)
                    .document(String.valueOf(i))
                    .set(new Shard(0));

            tasks.add(makeShard);
        }

        return Tasks.whenAll(tasks);
    }

    private Task<Void> incrementLikesCounter(final DocumentReference ref, final int numShards) {
        int shardId = (int) Math.floor(Math.random() * numShards);
        final DocumentReference shardRef = ref.collection(DATABASE_COLLECTION_LIKES_SHARD).document(String.valueOf(shardId));

        return db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Shard shard = transaction.get(shardRef).toObject(Shard.class);
                shard.setCount(shard.getCount() +1);
                transaction.set(shardRef, shard);
                return null;
            }
        });
    }

    private Task<Void> reduceLikesCounter(final DocumentReference ref, final int numShards){
        return null;
    }


    public Task<ArrayList> getUsers(ArrayList<String> userIds){
        Map<String, ArrayList> data = new HashMap<>();
        data.put("ids", userIds);

        return mFunctions
                .getHttpsCallable("getUsers")
                .call(data)
                .continueWith(task -> {
                    if(!task.isSuccessful()){
                        Exception e = task.getException();

                        Log.w(TAG, e.getMessage());
                        return null;
                    }else{
                        Log.d(TAG, "getUsers: " +task.getResult().getData());

                        Object fromdb = task.getResult().getData();
                        ArrayList<User> users = new ArrayList<>();
                        Gson gson = new Gson();

                        for(Object d: (ArrayList)fromdb){
                            String jsonStr = gson.toJson(d);
                            JSONObject jsonObject = new JSONObject(jsonStr);

                            String createdAt = jsonObject.get("createdAt").toString();
                            try{
                                DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy hh:mm:ss z");
                                Date result = df.parse(createdAt);
                                jsonObject.put("createdAt", null);

                                jsonStr = jsonObject.toString();

                                User user = gson.fromJson(jsonStr, User.class);
                                user.setCreatedAt(result);
                                users.add(user);
                            }catch (Exception e){
                                Log.w(TAG, "Error serializing Journey Object: ", e.getCause());
                                return null;
                            }
                        }
                        return users;
                    }
                });
    }

}
