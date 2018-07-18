package nonso.android.nonso.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nonso.android.nonso.R;

public class ImageViewActivity extends AppCompatActivity {

    @BindView(R.id.image_View_close) ImageButton mCloseBtn;
    @BindView(R.id.image_view_image) ImageView mImageView;

    private static final String PROFILE_IMAGE_EXTRA = "profile_image_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        String imageURL = intent.getStringExtra(PROFILE_IMAGE_EXTRA);

        if(imageURL != null && !imageURL.isEmpty()){
            Picasso.with(this).load(imageURL).placeholder(R.drawable.profile_image_placeholder)
                    .error(R.drawable.profile_image_placeholder).into(mImageView);
        }

    }

    @OnClick(R.id.image_View_close)
    public void onCloseClick(View view){
        finish();
    }
}
