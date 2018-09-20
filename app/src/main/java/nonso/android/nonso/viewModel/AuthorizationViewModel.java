package nonso.android.nonso.viewModel;

import android.arch.lifecycle.ViewModel;
import android.net.Uri;
import com.google.firebase.auth.FirebaseUser;

import nonso.android.nonso.data.AuthDB;
import nonso.android.nonso.data.FirebaseUtils;
import nonso.android.nonso.data.UsersDB;
import nonso.android.nonso.models.interfaces.Callback;
import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;

public class AuthorizationViewModel extends ViewModel {


    private AuthDB authDB;
    private UsersDB usersDB;

    public AuthorizationViewModel(){
        authDB = new AuthDB();
        usersDB = new UsersDB();
    }

    public FirebaseUser getCurrentUser(){
        return authDB.getAuthUser();
    }


    public void signIn(String email, String password, final Callback callback){
        authDB.login(email, password, callback);
    }

    public void createUser(String email, String password, String userName, final Callback callback){
        usersDB.createUser(email, password, userName, callback);
    }
}


