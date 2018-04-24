package nonso.android.nonso.utils;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

public class ImagePickerTask extends AsyncTaskLoader<Uri> {

    PackageManager mPackageManager;
    Context mContext;
    FragmentManager mFragmentManager;

    public ImagePickerTask(Context context, FragmentManager frag){
        super(context);
        mContext = context;
        mPackageManager = context.getPackageManager();

    }


    @Nullable
    @Override
    public Uri loadInBackground() {
        PickImageDialog.build(new PickSetup()).show(mFragmentManager);
        return null;
    }

    @Override
    public void deliverResult(Uri uri) {
        super.deliverResult(uri);
    }

    @Override
    protected boolean onCancelLoad() {
        return super.onCancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
    }
}
