package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.provider.CallLog;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.HttpsCallableResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import nonso.android.nonso.data.AuthDB;
import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.data.PostDB;
import nonso.android.nonso.data.UsersDB;
import nonso.android.nonso.models.Like;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.interfaces.UserListCallback;

public class PostViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_POST = "post";
    private static final String DATABASE_COLLECTION_JOURNEY = "journeys";
    private static final String DATABASE_COLLECTION_REPLIES = "replies";
    private static final String DATABASE_COLLECTION_LIKES = "likes";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFirestore root = FirebaseFirestore.getInstance();

    private PostDB postDB;
    private AuthDB authDB;

    private Query postListRef;
    private FirebaseQueryLiveData pListLiveData;
    private LiveData<ArrayList<Post>> postLiveData;

    private Query repliesListRef;
    private FirebaseQueryLiveData repliesLiveData;
    private LiveData<ArrayList<Post>> repliesData;

    private Query likesListRef;
    private FirebaseQueryLiveData likesLiveData;
    private LiveData<ArrayList<Like>> likesData;

    public PostViewModel(){
        postDB = new PostDB();
        authDB = new AuthDB();
    }

    public void getCurrentUser(Callback callback){
        authDB.getCurrentUser(callback);
    }

    public String getCurrentUserId(){
        return authDB.getCurrentUserId();
    }

    public void savePost(Post post, Callback callback){
        if(post.getPostId() != null){
            postDB.updatePost(post, callback);
        }else{
            postDB.savePost(post, callback);
        }
    }

    public void savePostReply(Post parent, Post current, Callback callback){
        postDB.savePostReply(parent, current, callback);
    }


    public void setPostList(String journeyId){
        postListRef = db.collection(DATABASE_COLLECTION_JOURNEY).document(journeyId).collection(DATABASE_COLLECTION_POST);
        pListLiveData = new FirebaseQueryLiveData(postListRef);
        postLiveData = Transformations.map(pListLiveData, new Deserializer());
    }

    public void setRepliesList(String postRef){
        DocumentReference ref = root.document(postRef);
        repliesListRef = ref.collection(DATABASE_COLLECTION_REPLIES);
        repliesLiveData = new FirebaseQueryLiveData(repliesListRef);
        repliesData = Transformations.map(repliesLiveData, new Deserializer());
    }

    public void setLikesList(String postRef){
        DocumentReference ref = root.document(postRef);
        likesListRef = ref.collection(DATABASE_COLLECTION_LIKES).limit(6);
        likesLiveData = new FirebaseQueryLiveData(likesListRef);
        likesData = Transformations.map(likesLiveData, new LikesDeserializer());
    }

    public LiveData<ArrayList<Post>> getPost(){
        return postLiveData;
    }
    public LiveData<ArrayList<Post>> getReplies() {return repliesData;}
    public LiveData<ArrayList<Like>> getLikesData() { return likesData; }

    public void likePost(Post post, Like like, User user, Callback callback){
        postDB.likePost(post, like, user, callback);
    }

    public void unLikePost(Post post, User user, Callback callback){
        postDB.unlikePost(post, user, callback);
    }

    public void getUsers(ArrayList<String> userIds, UserListCallback callback){
         postDB.getUsers(userIds)
                 .addOnSuccessListener(arrayList ->
                    callback.userList(arrayList))
                 .addOnFailureListener(e -> callback.result(Result.FAILED));
    }
    private class Deserializer implements Function<QuerySnapshot, ArrayList<Post>> {
        @Override
        public ArrayList<Post> apply(QuerySnapshot input) {

            ArrayList<Post> posts = new ArrayList<>();

            for (DocumentSnapshot snapshot : input) {
                posts.add(snapshot.toObject(Post.class));
            }

            return posts;
        }
    }


    private class LikesDeserializer implements Function<QuerySnapshot, ArrayList<Like>> {
        @Override
        public ArrayList<Like> apply(QuerySnapshot input) {

            ArrayList<Like> likes = new ArrayList<>();

            for (DocumentSnapshot snapshot : input) {
                likes.add(snapshot.toObject(Like.class));
            }

            return likes;
        }
    }

}
