package nonso.android.nonso.data;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;


public class FirebaseDocumentLiveData extends LiveData<DocumentSnapshot> {

    public static final String TAG = FirebaseDocumentLiveData.class.getSimpleName();
    private DocumentReference documentReference;
    private final MyValueEventListener listener = new MyValueEventListener();
    private ListenerRegistration listenerRegistration;

    private final Handler handler = new Handler();
    private boolean listenerRemovePending = false;

    public FirebaseDocumentLiveData(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    @Override
    protected void onActive() {
        super.onActive();

        Log.d(TAG, "onActive");

        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        }
        else {
            listenerRegistration = documentReference.addSnapshotListener(listener);
        }

    }

    @Override
    protected void onInactive() {
        super.onInactive();

        Log.d(TAG, "onInactive: ");
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }

    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            listenerRegistration.remove();
            listenerRemovePending = false;
        }
    };
    private class MyValueEventListener implements EventListener<DocumentSnapshot> {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
            if (e != null){
                Log.e(TAG, "Can't listen to doc snapshots: " + documentSnapshot + ":::" + e.getMessage());
                return;
            }
            setValue(documentSnapshot);
        }
    }
}