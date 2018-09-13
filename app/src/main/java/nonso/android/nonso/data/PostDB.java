package nonso.android.nonso.data;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nonso.android.nonso.models.Like;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Shard;
import nonso.android.nonso.models.interfaces.Callback;

public class PostDB {

    private FirebaseFirestore db = new AuthDB().getDb();
    private final String TAG = this.getClass().getSimpleName();
    private static final int  NUMBER_OF_SHARDS = 10;

    private static final String DATABASE_COLLECTION_POST = "post";
    private static final String DATABASE_COLLECTION_LIKES_SHARD = "likesShard";
    private static final String DATABASE_SUB_COLLECTION_LIKES = "likes";

    public void savePost(Post post, Callback callback){
        db.collection(DATABASE_COLLECTION_POST).add(post).addOnSuccessListener(documentReference -> {
            documentReference.update("postId", documentReference.getId());
            this.createLikesCounter(documentReference,NUMBER_OF_SHARDS ).addOnSuccessListener(aVoid -> callback.result(Result.SUCCESS));
        }).addOnFailureListener(e ->
                callback.result(Result.FAILED
                ));
    }

    public void likePost(String postId, Like like, Callback callback){
        db.collection(DATABASE_COLLECTION_POST).document(postId).collection(DATABASE_SUB_COLLECTION_LIKES)
                .add(like)
                .addOnSuccessListener(documentReference -> {
                    documentReference.update("likeId", documentReference.getId());
                    this.incrementLikesCounter(documentReference, NUMBER_OF_SHARDS).addOnSuccessListener(aVoid -> callback.result(Result.SUCCESS))
                            .addOnFailureListener(e -> callback.result(Result.FAILED));
                })
                .addOnFailureListener(e ->
                 callback.result(Result.FAILED)
                );
    }


    public Task<Integer> getLikes(String postId){
        DocumentReference documentReference = db.collection(DATABASE_COLLECTION_POST).document(postId);
        return this.getLikesCount(documentReference);
    }
    public void savePostReply(Post parent, Post child, final Callback callback){

        Map<String, Boolean> map = parent.getComments();

        db.collection(DATABASE_COLLECTION_POST).add(child).addOnSuccessListener(documentReference -> {
            documentReference.update("postId", documentReference.getId());

            map.put(documentReference.getId(), true);

            db.collection(DATABASE_COLLECTION_POST).document(parent.getPostId())
                    .update("comments", map).addOnSuccessListener(aVoid -> {
                Log.v(TAG, "Success: Updated user goal");
                callback.result(Result.SUCCESS);
            }).addOnFailureListener(e -> {
                Log.w(TAG, "Failed: Update user goal failed", e);
                callback.result(Result.FAILED);
            });
        }).addOnFailureListener(e ->
                callback.result(Result.FAILED
                ));
    }


    public void updatePostLikes(Map<String, Boolean> likes, String postId, Callback callback){
        db.collection(DATABASE_COLLECTION_POST).document(postId).update("likes", likes)
                .addOnSuccessListener(aVoid ->
                        callback.result(Result.SUCCESS)
                ).addOnFailureListener(e ->
                callback.result(Result.FAILED)
        );
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


    private Task<Integer> getLikesCount(final DocumentReference ref) {
        // Sum the count of each shard in the subcollection
        return ref.collection(DATABASE_COLLECTION_LIKES_SHARD).get()
                .continueWith(new Continuation<QuerySnapshot, Integer>() {
                    @Override
                    public Integer then(@NonNull Task<QuerySnapshot> task) throws Exception {
                        int count = 0;
                        for (DocumentSnapshot snap : task.getResult()) {
                            Shard shard = snap.toObject(Shard.class);
                            count += shard.getCount();
                        }
                        return count;
                    }
                });
    }



}
