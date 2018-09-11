package nonso.android.nonso.data;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.interfaces.Callback;

public class PostDB {

    private FirebaseFirestore db = new AuthDB().getDb();
    private final String TAG = this.getClass().getSimpleName();

    private static final String DATABASE_COLLECTION_POST = "post/";

    public void savePost(Post post, Callback callback){
        db.collection(DATABASE_COLLECTION_POST).add(post).addOnSuccessListener(documentReference -> {
            documentReference.update("postId", documentReference.getId());
            callback.result(Result.SUCCESS);
        }).addOnFailureListener(e ->
                callback.result(Result.FAILED
                ));
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

}
