package nonso.android.nonso.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import nonso.android.nonso.models.interfaces.Callback;

import nonso.android.nonso.models.Result;

public class FirebaseUtils {


    private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    private static final String TAG = FirebaseUtils.class.getSimpleName();

    public FirebaseUtils(){

    }

    public void uploadImage(final Bitmap bitmap, String prepend, final Callback callback ){
        final StorageReference ref = mStorageRef.child( prepend );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = ref.putBytes(data);
        bitmap.recycle();

        uploadTask.continueWithTask(task ->{
            if (!task.isSuccessful()) {
                callback.result(Result.FAILED);
            }
            return ref.getDownloadUrl();

        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                bitmap.recycle();
                callback.imageResult(downloadUri);
                Log.d(TAG, "onSuccess: uri= "+ downloadUri.toString());
            } else {
                callback.result(Result.FAILED);
            }

        });
    }
    public void uploadVideo(final String uri, String ref, final Callback callback ){
        final StorageReference videoRef = mStorageRef.child( ref );

        Uri file = Uri.fromFile(new File(uri));

        UploadTask uploadTask = videoRef.putFile(file);

        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                callback.result(Result.FAILED);
            }
            return videoRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                callback.imageResult(downloadUri);
                Log.d(TAG, "onSuccess: uri= "+ downloadUri.toString());
            } else {
                callback.result(Result.FAILED);
            }
        });
    }



}
