package nonso.android.nonso.models.interfaces;


import java.util.ArrayList;

import nonso.android.nonso.models.Result;
import nonso.android.nonso.models.User;

public interface UserListCallback{

    void result (Result result);
    void userList(ArrayList<User> users);
}


