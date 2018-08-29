package nonso.android.nonso.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import nonso.android.nonso.R;

public class ImageSlideAdapter extends PagerAdapter {



    private ArrayList<String> mImages;
    private LayoutInflater mInflater;
    private Context mContext;

    public ImageSlideAdapter(Context context, ArrayList images){

        mImages = images;
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View myImageLayout = mInflater.inflate(R.layout.image_slide, container, false);

        ImageView mImage = myImageLayout.findViewById(R.id.image_slide);

        Picasso.with(mContext).load(mImages.get(position)).placeholder(R.drawable.profile_image_placeholder)
                .error(R.drawable.profile_image_placeholder).into(mImage);

        container.addView(myImageLayout, 0);
        return myImageLayout;
    }
}
