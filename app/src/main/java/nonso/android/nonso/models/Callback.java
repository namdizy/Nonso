package nonso.android.nonso.models;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public interface Callback {

    void result(Result result);
    void imageResult(Uri downloadUrl);
    void authorizationResult(FirebaseUser user);
    void journeyResult(Journey journey);
    void stepResult(Step step);
    void userResult(User user);
}



