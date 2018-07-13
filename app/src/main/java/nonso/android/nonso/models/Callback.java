package nonso.android.nonso.models;

import android.net.Uri;

public interface Callback {

    void result(Result result);
    void journey(Uri downloadUrl);

}

