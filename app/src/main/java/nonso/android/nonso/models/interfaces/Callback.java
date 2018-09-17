package nonso.android.nonso.models.interfaces;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import nonso.android.nonso.models.Journey;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.Step;
import nonso.android.nonso.models.User;

public interface Callback {

    void result(Result result);
    void imageResult(Uri downloadUrl);
    void authorizationResult(FirebaseUser user);
    void journeyResult(Journey journey);
    void stepResult(Step step);
    void userResult(User user);
}
