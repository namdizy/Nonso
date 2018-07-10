package nonso.android.nonso.data;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;


public class FirebaseQueryLiveData extends LiveData<DocumentSnapshot> {

    public static final String TAG = FirebaseQueryLiveData.class.getSimpleName();
    private DocumentReference documentReference;
    private final MyValueEventListener listener = new MyValueEventListener();
    private ListenerRegistration listenerRegistration;

    public FirebaseQueryLiveData(DocumentReference documentReference) {
        this.documentReference = documentReference;
    }

    @Override
    protected void onActive() {
        super.onActive();

        Log.d(TAG, "onActive");
        if (listenerRegistration == null )
            listenerRegistration = documentReference.addSnapshotListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();

        Log.d(TAG, "onInactive: ");
        if (listenerRegistration != null)
            listenerRegistration.remove();
    }

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