package nonso.android.nonso.viewModel;

import android.arch.lifecycle.ViewModel;

import nonso.android.nonso.data.UsersDB;
import nonso.android.nonso.models.interfaces.ElasticSearchCallback;

public class SearchViewModel extends ViewModel {

    private UsersDB usersDB;

    public SearchViewModel(){

        usersDB = new UsersDB();
    }


    public void getUsers(String query, ElasticSearchCallback callback){
        usersDB.searchForUsers(query, callback);
    }
}
