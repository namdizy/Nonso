package nonso.android.nonso.viewModel;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import com.google.firebase.auth.FirebaseUser;

import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.models.Callback;
import nonso.android.nonso.models.Result;

public class AuthorizationViewModel extends ViewModel {


    private FirebaseUtils firebaseUtils = new FirebaseUtils();

    public FirebaseUser getCurrentUser(){
        return firebaseUtils.getCurrentUser();
    }


    public void signIn(String email, String password, final Callback callback){

        firebaseUtils.login(email, password, new Callback() {
            @Override
            public void result(Result result) {

            }

            @Override
            public void imageResult(Uri downloadUrl) {

            }

            @Override
            public void authorizationResult(FirebaseUser user) {
                callback.authorizationResult(user);
            }
        });

    }

    public void createUser(String email, String password, String userName, final Callback callback){
       firebaseUtils.createUser(email, password, userName, new Callback() {
           @Override
           public void result(Result result) {
               callback.result(result);
           }

           @Override
           public void imageResult(Uri downloadUrl) {

           }

           @Override
           public void authorizationResult(FirebaseUser user) {

           }
       });
    }
}


