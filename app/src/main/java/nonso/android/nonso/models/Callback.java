package nonso.android.nonso.models;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public interface Callback {

    void result(Result result);
    void journey(Uri downloadUrl);
    void authorization(FirebaseUser user);

}

