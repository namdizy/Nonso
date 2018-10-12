package nonso.android.nonso.data;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nonso.android.nonso.models.Like;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Shard;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;

public class PostDB {

    private FirebaseFirestore db = new AuthDB().getDb();
    private final String TAG = this.getClass().getSimpleName();
    private static final int  NUMBER_OF_SHARDS = 10;

    private FirebaseFirestore root = FirebaseFirestore.getInstance();
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private static final String DATABASE_COLLECTION_POST = "post";
    private static final String DATABASE_COLLECTION_JOURNEY = "journeys";
    private static final String DATABASE_COLLECTION_LIKES_SHARD = "likes_count_shard";
    private static final String DATABASE_COLLECTION_REPLIES_SHARD = "replies_count_shard";
    private static final String DATABASE_SUB_COLLECTION_LIKES = "likes";
    private static final String DATABASE_SUB_COLLECTION_REPLIES = "replies";
    private static final String DATABASE_COLLECTION_USERS = "users";

    public void savePost(Post post, Callback callback){

        db.collection(DATABASE_COLLECTION_JOURNEY).document(post.getJourneyId())
            .collection(DATABASE_COLLECTION_POST)
            .add(post).addOnSuccessListener(documentReference -> {
                documentReference.update("postId", documentReference.getId(), "documentReference", documentReference.getPath());
                    this.createLikesCounter(documentReference)
                        .addOnSuccessListener(aVoid ->
                               this.createReplyCounter(documentReference).addOnSuccessListener(aVoid1 ->
                                    callback.result(Result.SUCCESS)
                               ).addOnFailureListener(e ->
                                       callback.result(Result.FAILED)
                               )
                        ).addOnFailureListener(e -> callback.result(Result.FAILED));
            }).addOnFailureListener(e ->
                callback.result(Result.FAILED
            ));
    }

    public void updatePost(Post post, Callback callback){
        db.collection(DATABASE_COLLECTION_JOURNEY).document(post.getJourneyId())
                .collection(DATABASE_COLLECTION_POST).document(post.getPostId())
                .update("title", post.getTitle(), "body", post.getBody())
                .addOnSuccessListener(aVoid -> callback.result(Result.SUCCESS))
                .addOnFailureListener(e -> callback.result(Result.FAILED));
    }

    public void likePost(Post post, Like like, User user, Callback callback){
        DocumentReference ref = root.document(post.getDocumentReference());
        ref.collection(DATABASE_SUB_COLLECTION_LIKES)
                .add(like)
                .addOnSuccessListener(documentReference ->
                    documentReference.update("likeId", documentReference.getId())
                        .addOnSuccessListener(aVoid -> {
                            this.incrementLikesCounter(ref)
                                    .addOnSuccessListener(aVoidInner ->
                                            this.updateUserLikes( user, callback )
                                    )
                                    .addOnFailureListener(e ->{
                                            Log.v(TAG, e.getMessage());
                                            callback.result(Result.FAILED);}
                                    );
                        })
                )
                .addOnFailureListener(e ->
                 callback.result(Result.FAILED)
                );
    }

    private void updateUserLikes(User user, Callback callback){
        Map<String, Boolean> list = user.getLikedPost();

        db.collection(DATABASE_COLLECTION_USERS).document(user.getUserId())
                .update("likedPost", list)
                .addOnSuccessListener(aVoid ->
                    callback.result(Result.SUCCESS)
                )
                .addOnFailureListener(e ->
                    callback.result(Result.FAILED)
                );

    }

    public void unlikePost(Post post, User user, Callback callback){
        DocumentReference ref = root.document(post.getDocumentReference());
        ref.collection(DATABASE_SUB_COLLECTION_LIKES).whereEqualTo("creatorId", user.getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot snapshot: docs){
                        this.deleteLike(snapshot.getId(), ref, user, callback);
                    }
                }).addOnFailureListener(e -> callback.result(Result.FAILED));

    }

    private void deleteLike(String id, DocumentReference ref,User user,  Callback callback){

        ref.collection(DATABASE_SUB_COLLECTION_LIKES).document(id)
                .delete().addOnSuccessListener(aVoid ->
                    this.reduceLikesCounter(ref, user, callback)
                )
                .addOnFailureListener(e -> callback.result(Result.FAILED));

    }
    private void reduceLikesCounter(final DocumentReference ref, User user, Callback callback){

        ref.collection(DATABASE_COLLECTION_LIKES_SHARD).whereGreaterThan("count", 0)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();

                    DocumentSnapshot snapshot = docs.get(0);
                    Shard shard = snapshot.toObject(Shard.class);

                    ref.collection(DATABASE_COLLECTION_LIKES_SHARD).document(snapshot.getId())
                            .update("count", shard.getCount()-1 )
                            .addOnSuccessListener(aVoid -> this.updateUserLikes(user, callback ))
                            .addOnFailureListener(e -> callback.result(Result.FAILED));

                }).addOnFailureListener(e->
                    callback.result(Result.FAILED)
                );
    }

    public void savePostReply(Post parent, Post child, final Callback callback){
        DocumentReference ref = root.document(parent.getDocumentReference());
        ref.collection(DATABASE_SUB_COLLECTION_REPLIES)
            .add(child)
            .addOnSuccessListener(documentReference -> {
                documentReference.update("postId", documentReference.getId(), "documentReference", documentReference.getPath())
                .addOnSuccessListener(aVoid ->
                        this.incrementRepliesCounter(ref).addOnSuccessListener(aVoid1 ->
                                this.createLikesCounter(documentReference)
                                    .addOnSuccessListener(aVoid2 ->
                                        this.createReplyCounter(documentReference).addOnSuccessListener(aVoid3 ->
                                                callback.result(Result.SUCCESS))
                                        .addOnFailureListener( e -> callback.result(Result.FAILED)))
                                    .addOnFailureListener(e -> callback.result(Result.FAILED))
                        ).addOnFailureListener(e -> callback.result(Result.FAILED))
                ).addOnFailureListener(e ->
                        callback.result(Result.FAILED)
                );
            }).addOnFailureListener(e ->
                callback.result(Result.FAILED
            ));
    }


    private Task<Void> createReplyCounter(final DocumentReference ref){
        List<Task<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_SHARDS; i++) {
            Task<Void> makeShard = ref.collection(DATABASE_COLLECTION_REPLIES_SHARD)
                    .document(String.valueOf(i))
                    .set(new Shard(0));

            tasks.add(makeShard);
        }
        return Tasks.whenAll(tasks);
    }

    private Task<Void> incrementRepliesCounter(final DocumentReference ref) {
        int shardId = (int) Math.floor(Math.random() * NUMBER_OF_SHARDS);
        final DocumentReference shardRef = ref.collection(DATABASE_COLLECTION_REPLIES_SHARD).document(String.valueOf(shardId));

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

    private Task<Void> createLikesCounter(final DocumentReference ref) {
        List<Task<Void>> tasks = new ArrayList<>();

        // Initialize each shard with count=0
        for (int i = 0; i < NUMBER_OF_SHARDS; i++) {
            Task<Void> makeShard = ref.collection(DATABASE_COLLECTION_LIKES_SHARD)
                    .document(String.valueOf(i))
                    .set(new Shard(0));

            tasks.add(makeShard);
        }

        return Tasks.whenAll(tasks);
    }

    private Task<Void> incrementLikesCounter(final DocumentReference ref) {
        int shardId = (int) Math.floor(Math.random() * NUMBER_OF_SHARDS);
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
