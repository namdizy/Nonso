package nonso.android.nonso.viewModel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

import nonso.android.nonso.data.FirebaseQueryLiveData;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.models.Post;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.interfaces.Callback;

public class PostViewModel extends ViewModel {

    private static final String DATABASE_COLLECTION_POST = "post";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseUtils firebaseUtils;

    private Query postListRef;
    private FirebaseQueryLiveData pListLiveData;
    private LiveData<ArrayList<Post>> postLiveData;

    private Query repliesListRef;
    private FirebaseQueryLiveData repliesLiveData;
    private LiveData<ArrayList<Post>> repliesData;

    public PostViewModel(){
        firebaseUtils = new FirebaseUtils();
    }

    public void getCurrentUser(Callback callback){
        firebaseUtils.getCurrentUser(callback);
    }

    public void savePost(Post post, Callback callback){
        firebaseUtils.savePost(post, callback);
    }

    public void savePostReply(Post parent, Post current, Callback callback){
        firebaseUtils.savePostReply(parent, current, callback);
    }

    public void setPostList(String journeyId){
        postListRef = db.collection(DATABASE_COLLECTION_POST).whereEqualTo("journeyId", journeyId )
                .whereEqualTo("parentId", null);
        pListLiveData = new FirebaseQueryLiveData(postListRef);
        postLiveData = Transformations.map(pListLiveData, new Deserializer());
    }

    public void setRepliesList(String parentId){
        repliesListRef = db.collection(DATABASE_COLLECTION_POST).whereEqualTo("parentId", parentId);
        repliesLiveData = new FirebaseQueryLiveData(repliesListRef);
        repliesData = Transformations.map(repliesLiveData, new Deserializer());
    }



    public LiveData<ArrayList<Post>> getPost(){
        return postLiveData;
    }
    public LiveData<ArrayList<Post>> getReplies() {return repliesData;}

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

}
