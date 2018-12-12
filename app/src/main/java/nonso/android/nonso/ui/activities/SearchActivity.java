package nonso.android.nonso.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import nonso.android.nonso.R;
import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.User;
import nonso.android.nonso.models.elasticSearch.UserHitsPOJO;
import nonso.android.nonso.models.elasticSearch.UserSource;
import nonso.android.nonso.models.interfaces.ElasticSearchCallback;
import nonso.android.nonso.viewModel.SearchViewModel;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.search_back_btn) ImageButton mBackBtn;

    private SearchViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
    }

    @OnClick(R.id.search_back_btn)
    public void backBtnClicked(View view){

        finish();
    }


    @OnTextChanged(R.id.search_query)
    public void searchQueryChange(Editable editable){
        String query = editable.toString();


        viewModel.getUsers(query, new ElasticSearchCallback() {
            @Override
            public void result(Result result) {

            }

            @Override
            public void users(UserHitsPOJO userHits) {
                UserHitsPOJO userHitsPOJO = userHits;

                List<UserSource> userSource = userHitsPOJO.getHitsList().getUserIndex();
                List<User> userList = new ArrayList<>();
                for(UserSource source: userSource){
                    userList.add(source.getUser());
                }
            }

            @Override
            public void authorization(String authorization) {

            }

            @Override
            public void password(String password) {

            }
        });

    }



}
